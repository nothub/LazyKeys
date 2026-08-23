package lol.hub.lazykeys;

import net.minecraft.client.KeyMapping;

public final class Key {

    // Target key to be pressed automatically.
    public final KeyMapping actionKey;

    // Key for the player to toggle active state.
    public final KeyMapping toggleKey;

    private final ToggleState state = new ToggleState();

    public Key(KeyMapping actionKey, KeyMapping toggleKey) {
        this.actionKey = actionKey;
        this.toggleKey = toggleKey;
    }

    public boolean active() {
        return state.active();
    }

    public void toggle() {
        state.toggle();
    }

}
