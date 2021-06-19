package com.elikill58.negativity.common.protocols;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Cheat;

public class CheckManager implements Listeners {

	private List<CheckMethod> allChecks = new ArrayList<>();
	
	public CheckManager() {
		// don't need reload option because it's only static informations
		Cheat.CHEATS.forEach((cheat) -> {
			for(Method possibleMethod : cheat.getClass().getMethods()) {
				if(possibleMethod.isAnnotationPresent(Check.class) && possibleMethod.getParameterCount() == 1) {
					Check check = possibleMethod.getAnnotation(Check.class);
					
					allChecks.add(new CheckMethod(cheat, check, possibleMethod));
				}
			}
		});
	}
	
	@EventListener
	public void onPlayerEvent(PlayerEvent e) {
		allChecks.forEach((check) -> {
			Player p = e.getPlayer();
			if(check.getCheat().isActive() && check.getMethod().getParameterTypes()[0].equals(e.getClass())) {
				// now checking all conditions
				if(!NegativityPlayer.getNegativityPlayer(p).hasDetectionActive(check.getCheat()))
					return;
				for(CheckConditions condition : check.getCheck().conditions()) {
					if(condition.check(p)) {
						return;
					}
				}
				
				check.invoke(e);
			}
		});
	}
	
	public static class CheckMethod {
		
		private final Cheat cheat;
		private final Check check;
		private final Method method;
		
		public CheckMethod(Cheat cheat, Check check, Method method) {
			this.cheat = cheat;
			this.check = check;
			this.method = method;
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
		
		public void invoke(PlayerEvent event) {
			try {
				method.invoke(cheat, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
