package gui.controller;

import main.AlgorithmParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IndexExpanderTest {
    private static IndexExpander expander;

    @BeforeAll
    public static void initialize() {
        expander = new IndexExpander(AlgorithmParameters.instance.boardDimension);
    }

    @Test
    public void testExpandingTo32() {
        Assertions.assertEquals(32, expander.expand(2, 3));
    }

    @Test
    public void testExpandingTo87() {
        Assertions.assertEquals(87, expander.expand(7, 8));
    }

    @Test
    public void testExpandingTo7() {
        Assertions.assertEquals(7, expander.expand(7, 0));
    }

    @Test
    public void testExpandingTo64() {
        Assertions.assertEquals(64, expander.expand(4, 6));
    }

    @Test
    public void testExpandingTo0() {
        Assertions.assertEquals(0, expander.expand(0, 0));
    }

    @Test
    public void testExpandingTo99() {
        Assertions.assertEquals(99, expander.expand(9, 9));
    }
}
