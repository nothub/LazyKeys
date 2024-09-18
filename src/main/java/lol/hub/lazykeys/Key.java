package lol.hub.lazykeys;

import net.minecraft.client.KeyMapping;

public final class Key {

    // Target key to be pressed automatically.
    public final KeyMapping actionKey;

    // Key for the player to toggle active state.
    public final KeyMapping toggleKey;

    private boolean state;

    public Key(KeyMapping actionKey, KeyMapping toggleKey) {
        this.actionKey = actionKey;
        this.toggleKey = toggleKey;
        this.state = false;
    }

    public static Key of(KeyMapping actionKey, int toggleKey) {
        var label = actionKey.getName().replaceFirst("key\\.", "key." + Main.MODID + ".");
        return new Key(actionKey, new KeyMapping(label, toggleKey, "category." + Main.MODID));
    }

    public boolean active() {
        return state;
    }

    public void toggle() {
        state = !state;
    }

}
