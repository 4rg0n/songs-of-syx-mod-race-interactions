package com.github.argon.sos.interactions.ai;

import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.HumanoidUtil;
import init.race.Race;
import lombok.RequiredArgsConstructor;
import settlement.entity.humanoid.Humanoid;
import settlement.entity.humanoid.ai.main.AIManager;
import settlement.entity.humanoid.ai.main.AIPLAN;
import settlement.entity.humanoid.ai.main.AISUB.AISubActivation;
import snake2d.util.sprite.text.Str;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PlanRaceInteract {

	private final static Logger log = Loggers.getLogger(PlanRaceInteract.class);

	private final RaceInteractions raceInteractions;

//
//	static {
//		D.ts(PlanInterract.class);
//	}
	
	final AIPLAN lookForRaces = new AIPLAN.PLANRES() {
		
		@Override
		protected AISubActivation init(Humanoid a, AIManager d) {
			return first.set(a, d);
		}
		
		private final Resumer first = new Resumer("") {
			
			@Override
			protected AISubActivation setAction(Humanoid humanoid, AIManager d) {
				List<Humanoid> nearbyHumanoids = HumanoidUtil.getNearbyHumanoids(humanoid, 20);
				double avgRaceLikings = HumanoidUtil.avgRaceLikings(humanoid, nearbyHumanoids);
				int friendsCount = HumanoidUtil.countFriends(humanoid, nearbyHumanoids);

				Map<RaceStandingCategory, Double> standingsWeightMap = new HashMap<>();
				standingsWeightMap.put(RaceStandingCategory.EXPECTATION, avgRaceLikings);
				standingsWeightMap.put(RaceStandingCategory.FULFILLMENT, avgRaceLikings);
				standingsWeightMap.put(RaceStandingCategory.LOYALTY, avgRaceLikings);

				// friend nearby?
				if (friendsCount > 0) {
					standingsWeightMap.put(RaceStandingCategory.HAPPINESS, avgRaceLikings);
				}

				RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.getCurrent()
						.orElse(RaceInteractionsConfig.Default.getConfig());
				Race race = humanoid.race();
				raceInteractions.manipulateRaceStandings(raceInteractionsConfig.getRaceStandingWeightMap(), race, standingsWeightMap);

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
