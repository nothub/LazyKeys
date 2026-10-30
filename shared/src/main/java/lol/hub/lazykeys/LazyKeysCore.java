package lol.hub.lazykeys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class LazyKeysCore {

    public final KeyMapping toggleUse;
    public final KeyMapping toggleSneak;
    public final KeyMapping toggleAttack;
    public final KeyMapping toggleSprint;
    public final KeyMapping toggleJump;
    public final KeyMapping toggleTwerk;

    private final List<Key> toggleKeys = new ArrayList<>();
    private final IntervalGate twerkGate = new IntervalGate(Duration.ofMillis(125));
    private Key twerk;

    public LazyKeysCore(KeyMapping.Category category) {
        toggleUse = new KeyMapping("key.lazykeys.use", InputConstants.KEY_NUMPAD2, category);
        toggleSneak = new KeyMapping("key.lazykeys.sneak", InputConstants.KEY_NUMPAD3, category);
        toggleAttack = new KeyMapping("key.lazykeys.attack", InputConstants.UNKNOWN.getValue(), category);
        toggleSprint = new KeyMapping("key.lazykeys.sprint", InputConstants.UNKNOWN.getValue(), category);
        toggleJump = new KeyMapping("key.lazykeys.jump", InputConstants.UNKNOWN.getValue(), category);
        toggleTwerk = new KeyMapping("key.lazykeys.twerk", InputConstants.KEY_NUMPAD4, category);
    }

    public List<KeyMapping> keyMappings() {
        return List.of(toggleUse, toggleSneak, toggleAttack, toggleSprint, toggleJump, toggleTwerk);
    }

    public void onTick(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;

        if (toggleKeys.isEmpty()) {
            toggleKeys.add(new Key(mc.options.keyUse, toggleUse));
            toggleKeys.add(new Key(mc.options.keyShift, toggleSneak));
            toggleKeys.add(new Key(mc.options.keyAttack, toggleAttack));
            toggleKeys.add(new Key(mc.options.keySprint, toggleSprint));
            toggleKeys.add(new Key(mc.options.keyJump, toggleJump));
            twerk = new Key(mc.options.keyShift, toggleTwerk);
        }

        for (Key key : toggleKeys) {
            handleToggleConsume(key, mc, keyMessageSuffix(key));
            if (key.active()) key.actionKey.setDown(true);
        }

        handleToggleConsume(twerk, mc, Component.literal("twerking "));
        if (twerk.active() && twerkGate.tryFire()) {
            twerk.actionKey.setDown(!twerk.actionKey.isDown());
        }
    }

    private static void handleToggleConsume(Key key, Minecraft mc, Component label) {
        if (!key.toggleKey.consumeClick()) return;
        key.toggle();
        var stateText = key.active() ? Component.literal("enabled")
                .withStyle(ChatFormatting.GREEN) : Component.literal("disabled")
                .withStyle(ChatFormatting.RED);
        mc.player.sendSystemMessage(Component.literal("Lazy ")
                .append(label)
                .append(stateText));
        if (!key.active()) key.actionKey.setDown(false);
    }

    private static Component keyMessageSuffix(Key key) {
        var action = key.actionKey.getName().replaceFirst("key\\.", "");
        return Component.literal(action + " (")
                .append(key.actionKey.getTranslatedKeyMessage())
                .append(") ");
    }

}
