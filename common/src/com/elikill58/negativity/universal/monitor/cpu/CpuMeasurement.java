package com.elikill58.negativity.universal.monitor.cpu;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.IntStream;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;

public class CpuMeasurement implements Comparable<CpuMeasurement> {

    private final String id;
    private final String className;
    private final String method;

    private final Map<String, CpuMeasurement> childInvokes = new HashMap<>();
    private long totalTime;
    private IDetectionKey<?> cheatKey = null;
    private CpuMonitorTask task;

    public CpuMeasurement(String id, String className, String method, CpuMonitorTask task) {
        this.id = id;

        this.className = className;
        this.method = method;
        
        this.task = task;

        if(className.startsWith("com.elikill58.negativity.common.protocols.")) {
        	cheatKey = CheatKeys.fromLowerKey(className.split("\\.")[5]);
        } else if(className.startsWith("com.elikill58.negativity.common.special.")) {
        	cheatKey = SpecialKeys.fromLowerKey(className.split("\\.")[5]);
        }
    }
    
    public @Nullable IDetectionKey<?> getCheatKey() {
		return cheatKey;
	}
    
    public String getCheatResult() {
    	try {
    		Class<?> clazz = Class.forName(className);
    		for(Method m : clazz.getDeclaredMethods()) {
    			if(m.getName().equals(method)) { // found method
    				Check check = m.getAnnotation(Check.class);
    				if (check != null) // method with check
    					return check.name() + " " + totalTime + "ms " + getPorcent();
    				if(method.startsWith("lambda")) // sub task on given cheat
    					return method.substring(7) + ChatColor.GRAY + " (*multiple check) " + ChatColor.YELLOW + totalTime + "ms " + getPorcent(); // can't find check
					return method + ChatColor.GRAY + " (*multiple check) " + ChatColor.YELLOW + totalTime + "ms " + getPorcent(); // can't find check
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    	return cheatKey == null ? null : method + " " + totalTime + "ms " + getPorcent();
    }
    
    private String getPorcent() {
    	double l = ((double) totalTime / task.getRootNode().getTotalTime()) * 100;
    	return l <= 0.01 ? ">0%" : String.format("%.2f", l) + "%";
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getMethod() {
        return method;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public Map<String, CpuMeasurement> getChildInvokes() {
        return new HashMap<>(childInvokes);
    }

    public float getTimePercent(long parentTime) {
        //one float conversion triggers the complete calculation to be decimal
        return ((float) totalTime / parentTime) * 100;
    }

    public void onMeasurement(StackTraceElement[] stackTrace, int skipElements, long time) {
        totalTime += time;

        if (skipElements >= stackTrace.length) {
            //we reached the end
            return;
        }

        StackTraceElement nextChildElement = stackTrace[stackTrace.length - skipElements - 1];
        String nextClass = nextChildElement.getClassName();
        String nextMethod = nextChildElement.getMethodName();

        String idName = nextChildElement.getClassName() + '.' + nextChildElement.getMethodName();
        CpuMeasurement child = childInvokes
                .computeIfAbsent(idName, (key) -> new CpuMeasurement(key, nextClass, nextMethod, task));
        child.onMeasurement(stackTrace, skipElements + 1, time);
    }
    
    public void writeCleanedString(List<String> result, int indent) {
        StringBuilder b = new StringBuilder();
        IntStream.range(0, indent).forEach(i -> b.append(' '));

        String padding = b.toString();

        for (CpuMeasurement child : getChildInvokes().values()) {
        	if(!isConcerned(child))
        		continue;
        	result.add(padding + child.id + "() " + child.totalTime + "ms " + getPorcent());
            child.writeCleanedString(result, indent + 1);
        }
    }
    
    public void writeRawString(List<String> result, int indent) {
        StringBuilder b = new StringBuilder();
        IntStream.range(0, indent).forEach(i -> b.append(' '));

        String padding = b.toString();

        for (CpuMeasurement child : getChildInvokes().values()) {
        	result.add(padding + child.id + "() " + child.totalTime + "ms " + getPorcent());
            child.writeRawString(result, indent + 1);
        }
    }
    
    public void writeResultPerCheat(HashMap<IDetectionKey<?>, List<String>> map) {
        for (CpuMeasurement child : getChildInvokes().values()) {
        	if(child.getCheatKey() == null)
        		child.writeResultPerCheat(map);
        	else {
        		List<String> list = map.computeIfAbsent(child.getCheatKey(), a -> new ArrayList<>());
        		String cheatResult = child.getCheatResult();
        		if(!list.contains(cheatResult))
        			list.add(cheatResult);
        	}
        }
    }
    
    public boolean isConcerned(CpuMeasurement mm) {
    	for(Entry<String, CpuMeasurement> entry : mm.getChildInvokes().entrySet()) {
    		String id = entry.getValue().getId().toLowerCase();
    		if(id.contains("com.elikill58") || id.contains("negativity"))
    			return true;
    		if(isConcerned(entry.getValue()))
    			return true;
    	}
    	return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CpuMeasurement that = (CpuMeasurement) o;

        return totalTime == that.totalTime &&
                Objects.equals(id, that.id) &&
                Objects.equals(className, that.className) &&
                Objects.equals(method, that.method) &&
                Objects.equals(childInvokes, that.childInvokes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, className, method, childInvokes, totalTime);
    }

    @Override
    public int compareTo(CpuMeasurement other) {
        return Long.compare(this.totalTime, other.totalTime);
    }
    
    @Override
    public String toString() {
    	return "MethodMeasurement{id=" + id + ",className=" + className + ",method=" + method + ",totalTime=" + totalTime + "}";
    }
    
    public List<String> getCleanedString() {
    	List<String> result = new ArrayList<>();
        for (Map.Entry<String, CpuMeasurement> entry : getChildInvokes().entrySet()) {
        	result.add(entry.getKey() + "() " + entry.getValue().totalTime + "ms");
            entry.getValue().writeCleanedString(result, 1);
        }

        return result;
    }
    
    public List<String> getRawString() {
    	List<String> result = new ArrayList<>();
        for (Map.Entry<String, CpuMeasurement> entry : getChildInvokes().entrySet()) {
        	result.add(entry.getKey() + "() " + entry.getValue().totalTime + "ms");
            entry.getValue().writeRawString(result, 1);
        }

        return result;
    }
}