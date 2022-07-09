package com.elikill58.negativity.universal.monitor.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.IntStream;

public class MethodMeasurement implements Comparable<MethodMeasurement> {

    private final String id;
    private final String className;
    private final String method;

    private final Map<String, MethodMeasurement> childInvokes = new HashMap<>();
    private long totalTime;

    public MethodMeasurement(String id, String className, String method) {
        this.id = id;

        this.className = className;
        this.method = method;
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

    public Map<String, MethodMeasurement> getChildInvokes() {
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
        MethodMeasurement child = childInvokes
                .computeIfAbsent(idName, (key) -> new MethodMeasurement(key, nextClass, nextMethod));
        child.onMeasurement(stackTrace, skipElements + 1, time);
    }

    public void writeCleanedString(List<String> result, int indent) {
        StringBuilder b = new StringBuilder();
        IntStream.range(0, indent).forEach(i -> b.append(' '));

        String padding = b.toString();

        for (MethodMeasurement child : getChildInvokes().values()) {
        	if(!isConcerned(child))
        		continue;
        	result.add(padding + child.id + "() " + child.totalTime + "ms");
            child.writeCleanedString(result, indent + 1);
        }
    }
    
    public void writeRawString(List<String> result, int indent) {
        StringBuilder b = new StringBuilder();
        IntStream.range(0, indent).forEach(i -> b.append(' '));

        String padding = b.toString();

        for (MethodMeasurement child : getChildInvokes().values()) {
        	result.add(padding + child.id + "() " + child.totalTime + "ms");
            child.writeRawString(result, indent + 1);
        }
    }
    
    public boolean isConcerned(MethodMeasurement mm) {
    	for(Entry<String, MethodMeasurement> entry : new HashMap<>(mm.getChildInvokes()).entrySet()) {
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
        MethodMeasurement that = (MethodMeasurement) o;

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
    public int compareTo(MethodMeasurement other) {
        return Long.compare(this.totalTime, other.totalTime);
    }
    
    @Override
    public String toString() {
    	return "MethodMeasurement{id=" + id + ",className=" + className + ",method=" + method + ",totalTime=" + totalTime + "}";
    }
    
    public List<String> getCleanedString() {
    	List<String> result = new ArrayList<>();
        for (Map.Entry<String, MethodMeasurement> entry : getChildInvokes().entrySet()) {
        	result.add(entry.getKey() + "() " + entry.getValue().totalTime + "ms");
            entry.getValue().writeCleanedString(result, 1);
        }

        return result;
    }
    
    public List<String> getRawString() {
    	List<String> result = new ArrayList<>();
        for (Map.Entry<String, MethodMeasurement> entry : getChildInvokes().entrySet()) {
        	result.add(entry.getKey() + "() " + entry.getValue().totalTime + "ms");
            entry.getValue().writeRawString(result, 1);
        }

        return result;
    }
}