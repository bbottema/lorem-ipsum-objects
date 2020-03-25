package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomLocalDateFactoryTest {

    @Test
    public void testCreateLoremIpsumObject() {
        final RandomLocalDateFactory factory = new RandomLocalDateFactory();

        final LoremIpsumGenerator mock = mock(LoremIpsumGenerator.class);
        LoremIpsumGenerator.setInstance(mock);
        LocalDate localDate = LocalDate.now();
        when(mock.getRandomLocalDate()).thenReturn(localDate);

        assertThat(factory.createLoremIpsumObject(null, null, LoremIpsumConfig.builder().build(), null)).isEqualTo(localDate);

        LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
    }
}