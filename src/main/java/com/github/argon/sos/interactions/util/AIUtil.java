package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import settlement.entity.humanoid.HTYPE;
import settlement.entity.humanoid.ai.main.AI;
import settlement.entity.humanoid.ai.main.AIModule;

import java.util.Arrays;

public class AIUtil {
    private final static Logger log = Loggers.getLogger(AIUtil.class);

    public static void injectAIModule(HTYPE type, AIModule module) {
        ReflectionUtil.getField("modules", AI.modules())
            .map(o -> (AIModule[][]) o)
            .ifPresent(aiModules -> {
                AIModule[] typeAiModules = aiModules[type.index()];
                AIModule[] newAiModules = Arrays.copyOf(typeAiModules, typeAiModules.length + 1);
                newAiModules[typeAiModules.length] = module;

                log.debug("Injecting ai module %s into %s", module.getClass().getSimpleName(), type.name);
                aiModules[type.index()] = newAiModules;
            });
    }
}
