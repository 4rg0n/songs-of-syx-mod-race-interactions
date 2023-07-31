package com.github.argon.sos.interactions.game.config;


import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

@Disabled
class ConfigFileManagerTest {

    @Test
    void load_withConfigPropertiesAnnotation() {
        ConfigManager configManager = ConfigManager.getInstance();
        ConfigPropertiesPojo loadedConfig = configManager.load(Paths.get("/"), ConfigPropertiesPojo.class)
            .orElseThrow(AssertionError::new);

        ConfigPropertiesPojo expectedConfig = new ConfigPropertiesPojo();
        expectedConfig.setTestLong(1L);
        expectedConfig.setTestDouble(1.1D);
        expectedConfig.setTestString("test");
        expectedConfig.setTestBoolean(false);
        expectedConfig.setTestNull(null);

        Assertions.assertThat(loadedConfig).isEqualTo(expectedConfig);
    }

    @Data
    @ConfigProperties(path = "/assets/init/config/ConfigProperties.txt", isResource = true)
    public static class ConfigPropertiesPojo {
        private Long testLong;
        private Double testDouble;
        private String testString;
        private Boolean testBoolean;
        private Void testNull;
    }
}