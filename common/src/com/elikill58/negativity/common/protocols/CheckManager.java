package com.elikill58.negativity.common.protocols;

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
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.detections.Cheat;

public class CheckManager implements Listeners {

	private final List<CheckMethod> allChecks = new ArrayList<>();
	
	public CheckManager(List<Cheat> cheats) {
		for (Cheat cheat : cheats) {
			for (Method possibleMethod : cheat.getClass().getMethods()) {
				Check check = possibleMethod.getAnnotation(Check.class);
				if (check == null) {
					continue;
				}
				
				Class<?>[] parameterTypes = possibleMethod.getParameterTypes();
				if (parameterTypes.length == 0 || parameterTypes.length > 2) {
					Adapter.getAdapter().getLogger().warn("Method for check " + check.name() + " must have a PlayerEvent as first parameter and can have the NegativityPlayer as second parameter.");
					continue;
				}
				boolean hasNegativityPlayer = parameterTypes.length > 1;
				if (!PlayerEvent.class.isAssignableFrom(parameterTypes[0])) {
					Adapter.getAdapter().getLogger().warn("Parameter of check method " + possibleMethod.getName() + " must be a subclass of PlayerEvent.");
					continue;
				}
				
				if (hasNegativityPlayer && !NegativityPlayer.class.isAssignableFrom(parameterTypes[1])) {
					Adapter.getAdapter().getLogger().warn("Second parameter of check method " + possibleMethod.getName() + " must be NegativityPlayer.");
					continue;
				}
				cheat.getChecks().add(check);
				allChecks.add(new CheckMethod(cheat, check, possibleMethod, hasNegativityPlayer));
			}
		}
	}
	
	public List<CheckMethod> getCheckMethodForCheat(Cheat c){
		return allChecks.stream().filter((ch) -> ch.getCheat().equals(c)).collect(Collectors.toList());
	}
	
	@EventListener
	public void onPlayerEvent(PlayerEvent e) {
		if(e.getPlayer() == null)
			return;
		HashMap<CheckConditions, Boolean> conditionResult = new HashMap<>();
		new ArrayList<>(allChecks).forEach((check) -> {
			Player p = e.getPlayer();
			if(check.getCheat().isActive() && check.getMethod().getParameterTypes()[0].equals(e.getClass())) {
				// now checking all conditions
				NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
				if(!np.hasDetectionActive(check.getCheat()) || !check.getCheat().checkActive(check.getCheck().name()))
					return;
				if(!check.getCheck().ignoreCancel()) {
					for(CheckConditions condition : check.getCheck().conditions()) {
						if(condition.shouldBeCached()) { // should be cached, cache it and check it
							conditionResult.computeIfAbsent(condition, (c) -> condition.check(p));
							if(!conditionResult.get(condition))
								return;
						} else if(!condition.check(p)) // no cache, always check it
							return;
					}
				}
				check.invoke(e, np);
			}
		});
	}
	
	public static class CheckMethod {
		
		private final Cheat cheat;
		private final Check check;
		private final Method method;
		private final boolean askNegativityPlayer;
		
		public CheckMethod(Cheat cheat, Check check, Method method, boolean askNegativityPlayer) {
			this.cheat = cheat;
			this.check = check;
			this.method = method;
			this.askNegativityPlayer = askNegativityPlayer;
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
		
		public boolean isAskNegativityPlayer() {
			return askNegativityPlayer;
		}
		
		public void invoke(PlayerEvent event, NegativityPlayer np) {
			try {
				if(askNegativityPlayer)
					method.invoke(cheat, event, np);
				else
					method.invoke(cheat, event);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().printError("Error while trying to invoke check method for event " + event.getClass().getSimpleName() + " and cheat " + cheat.getKey().getKey(), e);
				e.printStackTrace();
			}
		}
	}
}
