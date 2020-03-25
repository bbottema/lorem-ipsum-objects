package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomLocalDateTimeFactoryTest {

    @Test
    public void testCreateLoremIpsumObject() {
        final RandomLocalDateTimeFactory factory = new RandomLocalDateTimeFactory();

        final LoremIpsumGenerator mock = mock(LoremIpsumGenerator.class);
        LoremIpsumGenerator.setInstance(mock);
        LocalDateTime localDateTime = LocalDateTime.now();
        when(mock.getRandomLocalDateTime()).thenReturn(localDateTime);

        assertThat(factory.createLoremIpsumObject(null, null, LoremIpsumConfig.builder().build(), null)).isEqualTo(localDateTime);

        LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
    }
}