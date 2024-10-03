package lol.hub.lazykeys;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {
    public Main(IEventBus modEventBus, ModContainer modContainer) {

        var mc = Minecraft.getInstance();

        var toggleKeys = List.of(
                Key.of(mc.options.keyUse,    "key.lazykeys.use",    GLFW.GLFW_KEY_KP_2),
                Key.of(mc.options.keyShift,  "key.lazykeys.sneak",  GLFW.GLFW_KEY_KP_3),
                Key.of(mc.options.keyAttack, "key.lazykeys.attack", GLFW.GLFW_KEY_UNKNOWN),
                Key.of(mc.options.keySprint, "key.lazykeys.sprint", GLFW.GLFW_KEY_UNKNOWN),
                Key.of(mc.options.keyJump,   "key.lazykeys.jump",   GLFW.GLFW_KEY_UNKNOWN)
        );

        var twerkKey = new KeyMapping("key.lazykeys.twerk", GLFW.GLFW_KEY_KP_4, "category.lazykeys");
        var twerkState = new AtomicBoolean();
        AtomicReference<Instant> twerkLastMove = new AtomicReference<>(Instant.now().minusSeconds(1));

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Key key : toggleKeys) {
                event.register(key.toggleKey);
            }
            event.register(twerkKey);
        });

        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Post event) -> {
            if (mc.level == null || mc.player == null) return;

            // simulated presses
            for (Key key : toggleKeys) {
                if (key.toggleKey.consumeClick()) {
                    key.toggle();
                    var message = Component.literal("Lazy")
                            .append(" ")
                            .append(key.actionKey.getName()
                                    .replaceFirst("key\\.", ""))
                            .append(" (")
                            .append(key.actionKey.getKey().getName()
                                    .replaceFirst("key\\.", "")
                                    .replaceFirst("keyboard\\.", "")
                                    .replaceAll("\\.", " "))
                            .append(") ")
                            .append(key.active() ?
                                    Component.literal("enabled").withColor(0x00AA00) :
                                    Component.literal("disabled").withColor(0xAA0000));
                    mc.player.sendSystemMessage(message);
                    if (!key.active()) {
                        key.actionKey.setDown(false);
                    }
                }
                if (key.active()) {
                    key.actionKey.setDown(true);
                }
            }

            // twerking
            if (twerkKey.consumeClick()) {
                twerkState.set(!twerkState.get());
                var message = Component.literal("Twerking ").append(twerkState.get() ?
                        Component.literal("enabled" ).withStyle(ChatFormatting.GREEN) :
                        Component.literal("disabled").withStyle(ChatFormatting.RED));
                mc.player.sendSystemMessage(message);
                if (!twerkState.get()) {
                    mc.options.keyShift.setDown(false);
                }
            }
            if (twerkState.get()) {
                if (twerkLastMove.get().plusMillis(500).isBefore(Instant.now())) {
                    mc.options.keyShift.setDown(!mc.options.keyShift.isDown());
                    twerkLastMove.set(Instant.now());
                }
            }
        });

    }
}
