package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.ai.AIModule_Race;
import com.github.argon.sos.interactions.ai.PlanRaceInteract;
import com.github.argon.sos.interactions.config.ConfigJsonService;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.*;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.ui.race.section.preference.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.preference.PrefConfigSection;
import com.github.argon.sos.interactions.ui.race.section.preference.RaceTableSection;
import com.github.argon.sos.interactions.ui.race.section.standing.StandConfigSection;
import com.github.argon.sos.interactions.util.HumanoidUtil;
import com.github.argon.sos.interactions.util.RaceUtil;
import init.race.Race;
import lombok.RequiredArgsConstructor;
import settlement.entity.humanoid.Humanoid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides an API to manipulate race likings via a {@link RaceInteractionsConfig}
 */
@RequiredArgsConstructor
public class RaceInteractions {
    private final static Logger log = Loggers.getLogger(RaceInteractions.class);
    private final RaceComparator raceComparator;
    private final RacePreferenceSimilarityCalculator racePrefCalculator;
    private final RaceService raceService;
    private final RaceStandingsService raceStandingsService;

    public void manipulateRaceLikings(RaceInteractionsConfig config) {
        boolean customOnly = config.isCustomOnly();
        boolean honorCustom = config.isHonorCustom();
        List<Race> races = RaceUtil.getAll();
        List<RaceComparator.Result> compareResults = raceComparator.compare(races);
        RaceLikingsCalculator raceLikingsCalculator = new RaceLikingsCalculator(config.getRacePreferenceWeightMap());

        log.debug("Manipulating race likings for %s races", races.size());
        for (RaceComparator.Result compareResult : compareResults) {
            String race = compareResult.getRace();
            String otherRace = compareResult.getOtherRace();
            RacePreferenceSimilarityCalculator.SimilarityResult calcResult = racePrefCalculator.calculate(compareResult);
            double liking = raceLikingsCalculator.calculate(calcResult);

            if (customOnly && honorCustom) {
                if (!raceService.isCustom(race) && raceService.isCustom(otherRace)) {
                    raceService.setLiking(race, otherRace, liking);
                }
            } else if (customOnly) {
                if ((raceService.isCustom(race) && raceService.isCustom(otherRace))
                || (raceService.isCustom(race) && !raceService.isCustom(otherRace))
                || (!raceService.isCustom(race) && raceService.isCustom(otherRace))
                ) {
                    raceService.setLiking(race, otherRace, liking);
                }
            } else {
                raceService.setLiking(race, otherRace, liking);
            }
        }
    }

    public void manipulateRaceStandings(Humanoid humanoid, RaceInteractionsConfig config) {
        int raceLookRange = config.getRaceLookRange();
        boolean raceBoostSelf = config.isRaceBoostSelf();

        List<Humanoid> nearbyHumanoids = HumanoidUtil.getNearbyHumanoids(humanoid, raceLookRange);
        double avgRaceLikings = HumanoidUtil.avgRaceLikings(humanoid, nearbyHumanoids, raceBoostSelf);
        int friendsCount = HumanoidUtil.countFriends(humanoid, nearbyHumanoids);

        Map<RaceStandingCategory, Double> standingsWeightMap = new HashMap<>();
        standingsWeightMap.put(RaceStandingCategory.EXPECTATION, avgRaceLikings);
        standingsWeightMap.put(RaceStandingCategory.FULFILLMENT, avgRaceLikings);
        standingsWeightMap.put(RaceStandingCategory.LOYALTY, avgRaceLikings);

        // friend nearby?
        if (friendsCount > 0) {
            standingsWeightMap.put(RaceStandingCategory.HAPPINESS, avgRaceLikings);
        }

        log.trace("%s looked for other races %s tiles nearby. " +
                        "Found %s other citizens and %s friends with a average liking of %s",
                humanoid.title(), raceLookRange, nearbyHumanoids.size(), friendsCount, avgRaceLikings);

        Race race = humanoid.race();
        manipulateRaceStandings(config.getRaceStandingWeightMap(), race, standingsWeightMap);
    }

    public void manipulateRaceStandings(Map<RaceStandingCategory, Double> standingWeightsMap, Race race, Map<RaceStandingCategory , Double> incMap) {
        standingWeightsMap.forEach((category, weight) -> {
            if ((weight == null || weight == 0d)) {
                // 0 = means disabled
                return;
            }

            Double inc = incMap.get(category);
            // no increment for this category?
            if (inc == null || inc == 0d) {
                return;
            }

            raceStandingsService.boostStanding(category, race, inc, weight);
        });
    }

    /**
     * For restoring or directly setting likings between two races in bulk
     */
    public void applyRaceLikings(List<RaceService.RaceLiking> likings) {
        for (RaceService.RaceLiking liking : likings) {
            raceService.setLiking(liking.getRace(), liking.getOtherRace(), liking.getLiking());
        }
    }

    public static class Builder {
        public static RaceInteractions build(RaceService raceService) {
            log.debug("Setting up service elements");
            return  new RaceInteractions(
                    RaceComparator.getInstance(),
                    RacePreferenceSimilarityCalculator.getInstance(),
                    raceService,
                    RaceStandingsService.getInstance()
            );
        }

        public static RaceInteractionsConfigPanel buildConfigUI(
            RaceInteractionsConfig config,
            RaceInteractions raceInteractions,
            List<RaceInfo> allRaceInfo
        ) {
            log.debug("Setting up ui elements");
            int width = allRaceInfo.size() * 110;

            PrefConfigSection prefConfigSection = new PrefConfigSection(config);
            RaceTableSection overviewSection = new RaceTableSection(allRaceInfo, width);
            ButtonSection buttonSection = new ButtonSection();
            StandConfigSection standConfigSection = new StandConfigSection(config, width);

            return new RaceInteractionsConfigPanel(
                raceInteractions,
                prefConfigSection,
                overviewSection,
                buttonSection,
                standConfigSection,
                ConfigJsonService.getInstance(),
                width
            );
        }

        public static AIModule_Race buildAI(RaceInteractions raceInteractions) {
            PlanRaceInteract planRaceInteract = new PlanRaceInteract(raceInteractions);
            return new AIModule_Race(planRaceInteract);
        }
    }
}
