package lol.hub.lazykeys;

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

import java.util.List;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {
    public Main(IEventBus modEventBus, ModContainer modContainer) {

        var mc = Minecraft.getInstance();
        var keys = List.of(
                Key.of(mc.options.keyUse,    "key.lazykeys.use",    GLFW.GLFW_KEY_KP_2),
                Key.of(mc.options.keyShift,  "key.lazykeys.sneak",  GLFW.GLFW_KEY_KP_3),
                Key.of(mc.options.keyAttack, "key.lazykeys.attack", GLFW.GLFW_KEY_UNKNOWN),
                Key.of(mc.options.keySprint, "key.lazykeys.sprint", GLFW.GLFW_KEY_UNKNOWN),
                Key.of(mc.options.keyJump,   "key.lazykeys.jump",   GLFW.GLFW_KEY_UNKNOWN)
        );

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Key key : keys) {
                event.register(key.toggleKey);
            }
        });

        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Pre event) -> {
            if (mc.player == null) return;
            for (Key key : keys) {
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
        });

    }
}
