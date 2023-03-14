package com.github.argon.sos.interactions.config.mapper.version;

import com.github.argon.sos.interactions.config.*;
import com.github.argon.sos.interactions.config.mapper.ConfigJsonMapper;
import com.github.argon.sos.interactions.config.mapper.ConfigSaveMapper;
import com.github.argon.sos.interactions.config.mapper.VersionMapper;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.*;
import static com.github.argon.sos.interactions.config.mapper.ConfigJsonMapper.*;

/**
 * For mapping {@link RaceInteractionsConfig} in and from json as also from and into the save file.
 * Takes care of the different storage formats for the config.
 *
 * Used for {@link this#VERSION} configs.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class V1ConfigMapper implements ConfigMapper {
    private final static Logger log = Loggers.getLogger(V1ConfigMapper.class);

    private final ConfigSaveMapper configSaveMapper;
    private final ConfigJsonMapper configJsonMapper;

    private final VersionMapper versionMapper;

    public final static int VERSION = 1;

    @Getter(lazy = true)
    private final static V1ConfigMapper instance = new V1ConfigMapper(
        ConfigSaveMapper.getInstance(),
        ConfigJsonMapper.getInstance(),
        VersionMapper.getInstance()
    );

    public FilePutter toSaveGame(FilePutter file, RaceInteractionsConfig config) {
        versionMapper.injectVersion(file, VERSION);
        file.bool(config.isCustomRaceOnly());
        file.bool(config.isHonorCustomRaceLikings());
        file.i(config.getRaceLookRange());

        double[] preferenceWeightsIndexed = configSaveMapper.toDoubleIndexed(config.getPreferenceWeights());
        file.ds(preferenceWeightsIndexed);

        double[] standingWeightsIndexed = configSaveMapper.toDoubleIndexed(config.getStandingWeights());
        file.ds(standingWeightsIndexed);

        String raceBoostToggles = configSaveMapper.fromRaceBoostToggles(config.getRaceBoostToggles());
        file.chars(raceBoostToggles);

        return file;
    }

    public RaceInteractionsConfig fromSaveGame(FileGetter file) throws IOException {
        boolean customOnly = file.bool();
        boolean honorCustom = file.bool();
        int raceLookRange = file.i();

        double[] preferenceWeightsIndexed = new double[RacePrefCategory.values().length];
        double[] standingWeightsIndexed = new double[RaceStandingCategory.values().length];

        file.ds(preferenceWeightsIndexed);
        file.ds(standingWeightsIndexed);
        String raceBoostTogglesString = file.chars();

        Map<RacePrefCategory, Double> preferenceWeightsMap = configSaveMapper.toPreferenceWeights(preferenceWeightsIndexed);
        Map<RaceStandingCategory, Double> standingWeightsMap = configSaveMapper.toStandingWeights(standingWeightsIndexed);
        Map<String, List<String>> raceBoostToggles = configSaveMapper.toRaceBoostToggles(raceBoostTogglesString);

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
            .version(VERSION)
            .customRaceOnly(customOnly)
            .honorCustomRaceLikings(honorCustom)
            .raceLookRange(raceLookRange)
            .vanillaRaces(ConfigStore.getInstance().getCurrentConfig()
                .orElse(RaceInteractionsConfig.Default.getConfig())
                .getVanillaRaces())
            .preferenceWeights(preferenceWeightsMap)
            .standingWeights(standingWeightsMap)
            .raceBoostToggles(raceBoostToggles)
            .build();

        log.trace("From save file %s", raceInteractionsConfig.toString());
        return raceInteractionsConfig;
    }

    public RaceInteractionsConfig fromJson(Json configJson) {
        boolean customOnly = configJson.bool(KEY_CUSTOM_RACE_ONLY, true);
        boolean honorCustom = configJson.bool(KEY_HONOR_CUSTOM_RACE_LIKINGS, true);
        int raceLookRange = configJson.i(KEY_RACE_LOOK_RANGE, 0, 100, DEFAULT_RACE_LOOK_RANGE);

        // PREFERENCE_WEIGHTS
        Map<RacePrefCategory, Double> preferencesWeightMap = configJsonMapper.toPreferenceWeights(
            configJson, RaceInteractionsConfig.Default.getPreferencesWeights());

        // STANDING_WEIGHTS
        Map<RaceStandingCategory, Double> standingsWeightMap = configJsonMapper.toStandingsWeights(
            configJson, RaceInteractionsConfig.Default.getStandingsWeights());

        // VANILLA_RACES
        List<String> gameRaces = configJsonMapper.toGameRaces(
            configJson, RaceInteractionsConfig.Default.getVanillaRaces());

        // RACE_BOOST_TOGGLES
        Map<String, List<String>> raceBoostToggles = configJsonMapper.toRaceBoostToggles(
            configJson, RaceInteractionsConfig.Default.getRaceBoostToggles());

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
            .version(VERSION)
            .preferenceWeights(preferencesWeightMap)
            .standingWeights(standingsWeightMap)
            .raceBoostToggles(raceBoostToggles)
            .raceLookRange(raceLookRange)
            .vanillaRaces(gameRaces)
            .honorCustomRaceLikings(honorCustom)
            .customRaceOnly(customOnly)
            .build();

        log.trace("From json %s", raceInteractionsConfig.toString());
        return raceInteractionsConfig;
    }

    public JsonE toJson(RaceInteractionsConfig config) {
        JsonE json = new JsonE();
        log.debug("Mapping config to json");
        log.trace("Config: %s", config.toString());

        // PREFERENCE_WEIGHTS
        JsonE preferenceWeights = configJsonMapper.toPreferenceWeights(config.getPreferenceWeights());

        // STANDING_WEIGHTS
        JsonE standingWeights = configJsonMapper.toStandingWeights(config.getStandingWeights());

        // RACE_BOOST_TOGGLES
        JsonE raceBoostToggles = configJsonMapper.toRaceBoostToggles(config.getRaceBoostToggles());

        versionMapper.injectVersion(json, VERSION);
        json.add(KEY_PREFERENCE_WEIGHTS, preferenceWeights);
        json.add(KEY_STANDING_WEIGHTS, standingWeights);
        json.add(KEY_RACE_BOOST_TOGGLES, raceBoostToggles);

        json.add(KEY_CUSTOM_RACE_ONLY, config.isCustomRaceOnly());
        json.add(KEY_HONOR_CUSTOM_RACE_LIKINGS, config.isHonorCustomRaceLikings());
        json.add(KEY_RACE_LOOK_RANGE, config.getRaceLookRange());
        json.addStrings(KEY_VANILLA_RACES, config.getVanillaRaces().toArray(new String[0]));

        return json;
    }
}
