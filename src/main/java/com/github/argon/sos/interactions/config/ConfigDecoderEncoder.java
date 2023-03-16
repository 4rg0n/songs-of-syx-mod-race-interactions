package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.config.mapper.ConfigMappers;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.CompressionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.Optional;

/**
 * Used for importing anf exporting {@link RaceInteractionsConfig} as sharable string
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigDecoderEncoder {
    private final static Logger log = Loggers.getLogger(ConfigDecoderEncoder.class);

    @Getter(lazy = true)
    private final static ConfigDecoderEncoder instance = new ConfigDecoderEncoder(ConfigMappers.getInstance());

    private final ConfigMappers configMappers;

    public Optional<String> encode(RaceInteractionsConfig config) {
        log.debug("Encoding config");
        log.trace("CONFIG: %s", config);
        JsonE json = configMappers.toJson(config);
        String jsonString = json.toString();

        log.trace("JSON: %s", jsonString);
        return CompressionUtil.base64Encode(jsonString);
    }

    public Optional<RaceInteractionsConfig> decode(String decodedConfigString) {
        log.debug("Decoding config");
        log.trace("STRING: %s", decodedConfigString);
        try {
            return CompressionUtil.base64Decode(decodedConfigString).map(configString -> {
                    Json json = new Json(configString, ConfigDecoderEncoder.class.getCanonicalName());
                    return configMappers.fromJson(json);
                 });
        } catch (Exception e) {
            log.info("Could not decode config: %s", e.getMessage());
            return Optional.empty();
        }
    }
}
