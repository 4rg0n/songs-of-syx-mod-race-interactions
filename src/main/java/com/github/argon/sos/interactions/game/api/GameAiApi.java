package com.github.argon.sos.interactions.game.api;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.ReflectionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import settlement.entity.humanoid.HTYPE;
import settlement.entity.humanoid.ai.main.AI;
import settlement.entity.humanoid.ai.main.AIModule;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameAiApi {
    private final static Logger log = Loggers.getLogger(GameAiApi.class);

    @Getter(lazy = true)
    private final static GameAiApi instance = new GameAiApi();

    /**
     * Will inject an AI module into the game. For adding additional behavior.
     * Each module has to be assigned to a type of humanoid.
     * See {@link HTYPE} for the different types.
     *
     * @param module to inject
     * @param humanoidTypes to assign the ai module to
     */
    public static void injectAIModule(AIModule module, HTYPE... humanoidTypes) {
        ReflectionUtil.getDeclaredField("modules", AI.modules())
            .map(o -> (AIModule[][]) o)
            .ifPresent(aiModules -> {
                for (HTYPE humanoidType : humanoidTypes) {
                    AIModule[] typeAiModules = aiModules[humanoidType.index()];
                    AIModule[] newAiModules = Arrays.copyOf(typeAiModules, typeAiModules.length + 1);
                    newAiModules[typeAiModules.length] = module;

                    log.debug("Injecting ai module %s into %s", module.getClass().getSimpleName(), humanoidType.name);
                    aiModules[humanoidType.index()] = newAiModules;
                }
            });
    }
}
