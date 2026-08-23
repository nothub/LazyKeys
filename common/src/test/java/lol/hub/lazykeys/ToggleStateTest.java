package lol.hub.lazykeys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToggleStateTest {

    @Test
    void startsInactive() {
        assertFalse(new ToggleState().active());
    }

    @Test
    void toggleFlipsState() {
        var state = new ToggleState();

        state.toggle();
        assertTrue(state.active());

        state.toggle();
        assertFalse(state.active());
    }

}
