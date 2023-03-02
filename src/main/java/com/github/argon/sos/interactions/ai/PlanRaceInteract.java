package com.github.argon.sos.interactions.ai;

import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.entity.humanoid.Humanoid;
import settlement.entity.humanoid.ai.main.AI;
import settlement.entity.humanoid.ai.main.AIData;
import settlement.entity.humanoid.ai.main.AIManager;
import settlement.entity.humanoid.ai.main.AIPLAN;
import settlement.entity.humanoid.ai.main.AISUB.AISubActivation;
import snake2d.util.sprite.text.Str;

@RequiredArgsConstructor
public class PlanRaceInteract {

	private final static Logger log = Loggers.getLogger(PlanRaceInteract.class);

	private final RaceInteractions raceInteractions;

	@Getter
	private final AIData.AIDataBit cooldown = AI.bit();


//	static {
//		D.ts(PlanRaceInteract.class);
//	}

	/**
	 * Look for other races in a {@link RaceInteractionsConfig#getRaceLookRange()} tile radius.
	 */
	final AIPLAN lookForRaces = new AIPLAN.PLANRES() {
		
		@Override
		protected AISubActivation init(Humanoid a, AIManager d) {
			return first.set(a, d);
		}
		
		private final Resumer first = new Resumer("") {

			@Override
			protected AISubActivation setAction(Humanoid humanoid, AIManager ai) {
				RaceInteractionsConfig config = RaceInteractionsConfig.getCurrent()
						.orElse(RaceInteractionsConfig.Default.getConfig());
				raceInteractions.manipulateRaceStandingsByNearbyRaces(humanoid, config);

				cooldown.set(ai, true);
				return null;
			}
			
			@Override
			protected AISubActivation res(Humanoid humanoid, AIManager d) {
				return null;
			}
			
			@Override
			public boolean con(Humanoid a, AIManager d) {
				return true;
			}
			
			@Override
			public void can(Humanoid a, AIManager d) {
				
			}
			
			@Override
			protected void name(Humanoid a, AIManager d, Str string) {
				string.add(AIModule_Race.name);
			}
		};
	};
}
