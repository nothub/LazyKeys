package lol.hub.lazykeys;

import com.mojang.blaze3d.platform.InputConstants;
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

import java.util.List;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {
    public Main(IEventBus modEventBus, ModContainer modContainer) {
        var mc = Minecraft.getInstance();
        var keys = List.of(
                new Key(mc.options.keyUse,
                        new KeyMapping(
                                "key.lazykeys.use",
                                InputConstants.Type.KEYSYM,
                                GLFW.GLFW_KEY_KP_2,
                                "category.lazykeys")),
                new Key(mc.options.keyShift,
                        new KeyMapping(
                                "key.lazykeys.sneak",
                                InputConstants.Type.KEYSYM,
                                GLFW.GLFW_KEY_KP_3,
                                "category.lazykeys"))
        );

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Key key : keys) {
                event.register(key.stateKey());
            }
        });

        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Pre event) -> {
            if (mc.player == null) return;
            for (Key key : keys) {
                if (key.stateKey().consumeClick()) {
                    key.toggle();
                    var message = Component.literal("Lazy")
                            .append(" ")
                            .append(key.gameKey().getKey().getDisplayName().getString().toLowerCase())
                            .append(" ")
                            .append(key.state() ?
                                    Component.literal("enabled").withColor(0x00AA00) :
                                    Component.literal("disabled").withColor(0xAA0000))
                            .append(".");
                    mc.player.sendSystemMessage(message);
                    if (!key.state()) {
                        key.gameKey().setDown(false);
                    }
                }
                if (key.state()) {
                    key.gameKey().setDown(true);
                }
            }
        });
    }
}
