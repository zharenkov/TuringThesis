package util;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;


public class ReversePhraseBuilderTest {
    private ReversePhraseBuilder reversePhraseBuilder;

    @Before
    public void init() {
        reversePhraseBuilder = new ReversePhraseBuilder();
    }

    @Test
    public void testToStringEmpty() throws Exception {
        assertThat(reversePhraseBuilder.toString()).isEmpty();
    }

    @Test
    public void testAddString() throws Exception {
        reversePhraseBuilder.addString("king");
        reversePhraseBuilder.addString("become");
        reversePhraseBuilder.addString("will");
        reversePhraseBuilder.addString("he");
        assertEquals("he will become king", reversePhraseBuilder.toString());
    }
}