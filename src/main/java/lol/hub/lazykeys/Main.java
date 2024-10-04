package lol.hub.lazykeys;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mod(value = "lazykeys")
public class Main {
    public Main() {

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
        var twerkLastMove = new AtomicReference<>(Instant.now().minusSeconds(1));

        //noinspection removal
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Key key : toggleKeys) {
                event.register(key.toggleKey);
            }
            event.register(twerkKey);
        });

        MinecraftForge.EVENT_BUS.addListener((TickEvent.PlayerTickEvent event) -> {
            if (mc.level == null || mc.player == null) return;
            if (event.phase != TickEvent.Phase.END) return;

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
                                    Component.literal("enabled").withStyle(ChatFormatting.GREEN) :
                                    Component.literal("disabled").withStyle(ChatFormatting.RED));
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
                var message = Component.literal("Lazy twerking ").append(twerkState.get() ?
                        Component.literal("enabled" ).withStyle(ChatFormatting.GREEN) :
                        Component.literal("disabled").withStyle(ChatFormatting.RED));
                mc.player.sendSystemMessage(message);
                if (!twerkState.get()) {
                    mc.options.keyShift.setDown(false);
                }
            }
            if (twerkState.get()) {
                if (twerkLastMove.get().plusMillis(125).isBefore(Instant.now())) {
                    mc.options.keyShift.setDown(!mc.options.keyShift.isDown());
                    twerkLastMove.set(Instant.now());
                }
            }
        });

    }
}
