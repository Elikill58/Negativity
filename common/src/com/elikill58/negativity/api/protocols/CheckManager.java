package com.elikill58.negativity.api.protocols;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.monitor.MonitorType;
import com.elikill58.negativity.universal.monitor.cpu.CpuMonitor;

public class CheckManager implements Listeners {

	private final List<CheckMethod> allChecks = new ArrayList<>();
	
	public CheckManager(List<Cheat> cheats) {
		for (Cheat cheat : cheats) {
			for (Method possibleMethod : cheat.getClass().getMethods()) {
				Check check = possibleMethod.getAnnotation(Check.class);
				if (check == null) {
					continue;
				}
				
				List<MethodArgument> arguments = new ArrayList<>();
				Class<?>[] parameterTypes = possibleMethod.getParameterTypes();
				for(int i = 0; i < parameterTypes.length; i++) {
					if (PlayerEvent.class.isAssignableFrom(parameterTypes[i])) {
						arguments.add(MethodArgument.EVENT);
					} else  if (NegativityPlayer.class.isAssignableFrom(parameterTypes[i])) {
						arguments.add(MethodArgument.NEGATIVITY_PLAYER);
					} else  if (CheckData.class.isAssignableFrom(parameterTypes[i])) {
						arguments.add(MethodArgument.CHECK_DATA);
					} else {
						Adapter.getAdapter().getLogger().warn("Can't find valid assignable argument for " + check.name() + " and class " + parameterTypes[i].getSimpleName());
					}
				}
				
				if (parameterTypes.length != arguments.size()) {
					Adapter.getAdapter().getLogger().warn("Method for check " + check.name() + " doesn't have known parameters.");
					continue;
				}
				cheat.getChecks().add(check);
				allChecks.add(new CheckMethod(cheat, check, possibleMethod, arguments.toArray(new MethodArgument[] {})));
			}
		}
	}
	
	public List<CheckMethod> getCheckMethodForCheat(Cheat c){
		return allChecks.stream().filter((ch) -> ch.getCheat().equals(c)).collect(Collectors.toList());
	}
	
	@EventListener
	public void onPlayerEvent(PlayerEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		HashMap<CheckConditions, Boolean> conditionResult = new HashMap<>();
		CpuMonitor cpu = MonitorType.CPU.getMonitor();
		allChecks.forEach((check) -> {
			if(check.getCheat().isActive() && check.getFirstParam().equals(e.getClass())) {
				// now checking all conditions
				if(!np.hasDetectionActive(check.getCheat()) || !check.getCheat().checkActive(check.getCheck().name()))
					return;
				long beginTime = System.nanoTime();
				if(!check.getCheck().ignoreCancel()) {
					for(CheckConditions condition : check.getCheck().conditions()) {
						if(condition.shouldBeCached()) { // should be cached, cache it and check it
							if(conditionResult.computeIfAbsent(condition, (c) -> !condition.check(p)))
								return;
						} else if(!condition.check(p))// no cache, always check it
							return;
					}
				}
				check.invoke(e, np);
				if(cpu.isEnabled())
					cpu.getMeasureForDetection(check.getCheat().getKey()).add(check.getCheck().name(), (System.nanoTime() - beginTime) / 1000);
			}
		});
	}
	
	public static class CheckMethod {
		
		private final Cheat cheat;
		private final Check check;
		private final Method method;
		private final MethodArgument[] arguments;
		private final Class<?> firstParam;
		
		public CheckMethod(Cheat cheat, Check check, Method method, MethodArgument... arguments) {
			this.cheat = cheat;
			this.check = check;
			this.method = method;
			this.arguments = arguments;
			this.firstParam = method.getParameterTypes()[0];
		}
		
		public Cheat getCheat() {
			return cheat;
		}
		
		public Check getCheck() {
			return check;
		}
		
		public Method getMethod() {
			return method;
		}
		
		public Class<?> getFirstParam() {
			return firstParam;
		}
		
		public void invoke(PlayerEvent event, NegativityPlayer np) {
			try {
				Object[] args = new Object[arguments.length];
				for(int i = 0; i < arguments.length; i++) {
					switch (arguments[i]) {
					case EVENT:
						args[i] = event;
						break;
					case CHECK_DATA:
						args[i] = np.getCheckData(cheat);
						break;
					case NEGATIVITY_PLAYER:
						args[i] = np;
						break;
					}
				}
				method.invoke(cheat, args);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().printError("Error while trying to invoke check method for event " + event.getClass().getSimpleName() + " and cheat " + cheat.getKey().getKey(), e);
				e.printStackTrace();
			}
		}
	}
	
	public static enum MethodArgument {
		EVENT, NEGATIVITY_PLAYER, CHECK_DATA;
	}
}
