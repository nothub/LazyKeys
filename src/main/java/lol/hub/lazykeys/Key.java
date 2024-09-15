package lol.hub.lazykeys;

import net.minecraft.client.KeyMapping;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Key {

    // Target key to be pressed automatically.
    private final KeyMapping gameKey;

    // Key for the player to toggle active state.
    private final KeyMapping stateKey;


    private final AtomicBoolean state;

    public Key(KeyMapping gameKey, KeyMapping stateKey) {
        this.gameKey = gameKey;
        this.stateKey = stateKey;
        this.state = new AtomicBoolean(false);
    }

    public KeyMapping gameKey() {
        return gameKey;
    }

    public KeyMapping stateKey() {
        return stateKey;
    }

    public boolean state() {
        return state.get();
    }

    public void toggle() {
        state.set(!state.get());
    }

}
