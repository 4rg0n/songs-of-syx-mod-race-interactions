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
//		D.ts(AIModule_Muh.class);
//	}
	
	private final PlanRaceInteract raceInteract;
	
	@Override
	public AiPlanActivation getPlan(Humanoid a, AIManager d) {
		return raceInteract.lookForRaces.activate(a, d);
	}

	@Override
	protected void update(Humanoid a, AIManager d, boolean newDay, int byteDelta, int upI) {
		
	}
	
	@Override
	public int getPriority(Humanoid humanoid, AIManager d) {
		return 8;
	}
}
