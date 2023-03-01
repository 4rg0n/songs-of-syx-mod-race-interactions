package com.github.argon.sos.interactions.ai;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.RequiredArgsConstructor;
import settlement.entity.humanoid.Humanoid;
import settlement.entity.humanoid.ai.main.AIManager;
import settlement.entity.humanoid.ai.main.AIModule;
import settlement.entity.humanoid.ai.main.AIPLAN.AiPlanActivation;

@RequiredArgsConstructor
public final class AIModule_Race extends AIModule{

	private final static Logger log = Loggers.getLogger(AIModule_Race.class);

	static CharSequence name = "Looking for other races";

//	static {
//		D.ts(AIModule_Race.class);
//	}
	
	private final PlanRaceInteract raceInteract;
	
	@Override
	public AiPlanActivation getPlan(Humanoid a, AIManager d) {
		return raceInteract.lookForRaces.activate(a, d);
	}

	@Override
	protected void update(Humanoid a, AIManager ai, boolean newDay, int byteDelta, int updateOfDay) {
		if ((updateOfDay & 0b011) == 0) {
			raceInteract.getCooldown().set(ai, false);
		}
	}
	
	@Override
	public int getPriority(Humanoid humanoid, AIManager ai) {
		if (ai.plan() == raceInteract.lookForRaces) {
			return 1;
		}

		if (raceInteract.getCooldown().is(ai)) {
			return 0;
		}

		return 1;
	}
}
