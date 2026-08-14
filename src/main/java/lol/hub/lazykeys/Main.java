package lol.hub.lazykeys;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {

    private static final List<Key> TOGGLE_KEYS = new ArrayList<>();
    private static final KeyMapping TOGGLE_USE = new KeyMapping("key.lazykeys.use", GLFW.GLFW_KEY_KP_2, KeyMapping.Category.MISC);
    private static final KeyMapping TOGGLE_SNEAK = new KeyMapping("key.lazykeys.sneak", GLFW.GLFW_KEY_KP_3, KeyMapping.Category.MISC);
    private static final KeyMapping TOGGLE_ATTACK = new KeyMapping("key.lazykeys.attack", GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.MISC);
    private static final KeyMapping TOGGLE_SPRINT = new KeyMapping("key.lazykeys.sprint", GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.MISC);
    private static final KeyMapping TOGGLE_JUMP = new KeyMapping("key.lazykeys.jump", GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.MISC);
    private static final KeyMapping TOGGLE_TWERK = new KeyMapping("key.lazykeys.twerk", GLFW.GLFW_KEY_KP_4, KeyMapping.Category.MISC);
    private static Key TWERK;
    private static final AtomicReference<Instant> TWERK_LAST_MOVE = new AtomicReference<>(Instant.now().minusSeconds(1));

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(Main::onRegisterKeyMappings);
        modEventBus.addListener(Main::onClientSetup);
        NeoForge.EVENT_BUS.addListener(Main::onPlayerTick);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_USE);
        event.register(TOGGLE_SNEAK);
        event.register(TOGGLE_ATTACK);
        event.register(TOGGLE_SPRINT);
        event.register(TOGGLE_JUMP);
        event.register(TOGGLE_TWERK);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            TOGGLE_KEYS.clear();
            TOGGLE_KEYS.add(new Key(mc.options.keyUse, TOGGLE_USE));
            TOGGLE_KEYS.add(new Key(mc.options.keyShift, TOGGLE_SNEAK));
            TOGGLE_KEYS.add(new Key(mc.options.keyAttack, TOGGLE_ATTACK));
            TOGGLE_KEYS.add(new Key(mc.options.keySprint, TOGGLE_SPRINT));
            TOGGLE_KEYS.add(new Key(mc.options.keyJump, TOGGLE_JUMP));
            TWERK = new Key(mc.options.keyShift, TOGGLE_TWERK);
        });
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (TWERK == null) return;

        for (Key key : TOGGLE_KEYS) {
            handleToggleConsume(key, mc, keyMessageSuffix(key));
            if (key.active()) key.actionKey.setDown(true);
        }

        handleToggleConsume(TWERK, mc, Component.literal("twerking "));
        if (TWERK.active() && TWERK_LAST_MOVE.get().plusMillis(125).isBefore(Instant.now())) {
            TWERK.actionKey.setDown(!TWERK.actionKey.isDown());
            TWERK_LAST_MOVE.set(Instant.now());
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
        var phys = key.actionKey.getKey()
                .getName()
                .replaceFirst("key\\.", "")
                .replaceFirst("keyboard\\.", "")
                .replaceAll("\\.", " ");
        return Component.literal(action + " (" + phys + ") ");
    }
}
