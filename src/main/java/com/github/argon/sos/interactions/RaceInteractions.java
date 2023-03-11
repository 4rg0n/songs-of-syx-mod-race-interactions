package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.ai.AIModule_Race;
import com.github.argon.sos.interactions.ai.PlanRaceInteract;
import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.game.api.GameHumanoidApi;
import com.github.argon.sos.interactions.game.api.GameRaceApi;
import com.github.argon.sos.interactions.game.api.GameUiApi;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.*;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.ui.race.section.preference.ButtonMenuSection;
import com.github.argon.sos.interactions.ui.race.section.preference.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.preference.PrefConfigSection;
import com.github.argon.sos.interactions.ui.race.section.preference.RaceTableSection;
import com.github.argon.sos.interactions.ui.race.section.standing.StandConfigSection;
import init.race.Race;
import lombok.RequiredArgsConstructor;
import settlement.entity.humanoid.Humanoid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides an API to manipulate race likings and standings via a {@link RaceInteractionsConfig}
 */
@RequiredArgsConstructor
public class RaceInteractions {
    private final static Logger log = Loggers.getLogger(RaceInteractions.class);
    private final RaceComparator raceComparator;
    private final RacePreferenceSimilarityCalculator racePrefCalculator;
    private final RaceService raceService;
    private final RaceStandingsService raceStandingsService;

    private final GameRaceApi gameRaceApi = GameRaceApi.getInstance();
    private final GameHumanoidApi gameHumanoidApi = GameHumanoidApi.getInstance();

    /**
     * Manipulates likings between races according to given {@link RaceInteractionsConfig}
     */
    public void manipulateRaceLikings(RaceInteractionsConfig config) {
        boolean customOnly = config.isCustomOnly();
        boolean honorCustom = config.isHonorCustom();
        List<Race> races = gameRaceApi.getAll();
        List<RaceComparator.Result> compareResults = raceComparator.compare(races);
        RaceLikingsCalculator raceLikingsCalculator = new RaceLikingsCalculator(config.getRacePreferenceWeights());

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

    /**
     * Decreases or increases standings {@link RaceStandingCategory} for a certain race.
     * Based of nearby races around given {@link Humanoid}.
     *
     * @param humanoid looking for other races nearby
     */
    public void manipulateRaceStandingsByNearbyRaces(Humanoid humanoid, RaceInteractionsConfig config) {
        int raceLookRange = config.getRaceLookRange();

        List<Humanoid> nearbyHumanoids = gameHumanoidApi.getNearbyHumanoids(humanoid, raceLookRange);
        double avgRaceLikings = gameHumanoidApi.avgRaceLikings(humanoid, nearbyHumanoids, config.getRaceBoostingToggles());
        int friendsCount = gameHumanoidApi.countFriends(humanoid, nearbyHumanoids);

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
        manipulateRaceStandings(config.getRaceStandingWeights(), race, standingsWeightMap);
    }

    /**
     * Decreases or increases standings {@link RaceStandingCategory} for a certain race.
     *
     * @param standingWeightsMap map with weights for each {@link RaceStandingCategory}
     * @param race to manipulate
     * @param incMap increase values for each {@link RaceStandingCategory}
     */
    public void manipulateRaceStandings(
        Map<RaceStandingCategory, Double> standingWeightsMap,
        Race race,
        Map<RaceStandingCategory, Double> incMap
    ) {
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

    /**
     * Builds the main components of the mod
     */
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
            ButtonMenuSection buttonMenuSection = new ButtonMenuSection();

            return new RaceInteractionsConfigPanel(
                raceInteractions,
                prefConfigSection,
                overviewSection,
                buttonSection,
                buttonMenuSection,
                standConfigSection,
                GameRaceApi.getInstance(),
                GameUiApi.getInstance(),
                ConfigStore.getInstance(),
                width
            );
        }

        public static AIModule_Race buildAI(RaceInteractions raceInteractions) {
            log.debug("Setting up ai modules");
            PlanRaceInteract planRaceInteract = new PlanRaceInteract(raceInteractions, ConfigStore.getInstance());
            return new AIModule_Race(planRaceInteract);
        }
    }
}
