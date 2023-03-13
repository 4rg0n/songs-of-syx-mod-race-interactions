package com.github.argon.sos.interactions.game.json.mapper.legacy;

import com.github.argon.sos.interactions.game.json.mapper.*;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LegacyMappers {
    private final List<LegacyMapper<?>> mappers;

    public static LegacyMappers getDefault() {
        return new LegacyMappers(Arrays.asList(
            new LegacyBooleanMapper(),
            new LegacyDoubleMapper(),
            new LegacyLongMapper(),
            new LegacyStringMapper(),
            new LegacyArrayMapper(),
            new LegacyObjectMapper(),
            new LegacyNullMapper()
        ));
    }

    public List<LegacyMapper<?>> find(Class<?> clazz) {
        return mappers.stream()
            .filter(mapper -> mapper.supports(clazz))
            .collect(Collectors.toList());
    }

    public Optional<LegacyMapper<?>> findOne(Class<?> clazz) {
        List<LegacyMapper<?>> foundMappers = find(clazz);

        if (foundMappers.isEmpty()) {
            return Optional.empty();
        }

        if (foundMappers.size() > 1) {
            throw new JsonMapperException("Found " + foundMappers.size() + " for mapping " + clazz.getTypeName() + ". Required one.");
        }

        return Optional.of(foundMappers.get(0));
    }
}
