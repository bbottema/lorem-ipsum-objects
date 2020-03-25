package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RandomLocalDateTimeFactory extends LoremIpsumObjectFactory<LocalDateTime> {

    /**
     * @param knownInstances   Not used.
     * @param loremIpsumConfig Not used.
     * @param exceptions       Not used.
     * @return The result of {@link LoremIpsumGenerator#getRandomUuid()}.
     */
    @Override
    public LocalDateTime _createLoremIpsumObject(
            @Nullable final Type[] genericMetaData,
            @Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
            LoremIpsumConfig loremIpsumConfig,
            @Nullable final List<Exception> exceptions) {
        return LoremIpsumGenerator.getInstance().getRandomLocalDateTime();
    }
}