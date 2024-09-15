package lol.hub.lazykeys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {
    private static final KeyMapping KEYBIND_TOGGLE_USE = new KeyMapping(
            "key.lazykeys.use",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_2,
            "category.lazykeys"
    );
    private static final KeyMapping KEYBIND_TOGGLE_SNEAK = new KeyMapping(
            "key.lazykeys.sneak",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_3,
            "category.lazykeys"
    );

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        Minecraft mc = Minecraft.getInstance();

        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.register(KEYBIND_TOGGLE_USE);
            event.register(KEYBIND_TOGGLE_SNEAK);
        });

        AtomicBoolean activeUse = new AtomicBoolean(false);
        AtomicBoolean activeSneak = new AtomicBoolean(false);
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Pre event) -> {
            if (mc.player == null) return;
            if (KEYBIND_TOGGLE_USE.consumeClick()) {
                activeUse.set(!activeUse.get());
                mc.player.sendSystemMessage(Component.literal("use active: " + activeUse.get()));
                if (!activeUse.get()) {
                    mc.options.keyUse.setDown(false);
                }
            }
            if (KEYBIND_TOGGLE_SNEAK.consumeClick()) {
                activeSneak.set(!activeSneak.get());
                mc.player.sendSystemMessage(Component.literal("sneak active: " + activeSneak.get()));
                if (!activeSneak.get()) {
                    mc.options.keyShift.setDown(false);
                }
            }
            if (activeUse.get()) {
                mc.options.keyUse.setDown(true);
            }
            if (activeSneak.get()) {
                mc.options.keyShift.setDown(true);
            }
        });

        modContainer.registerConfig(ModConfig.Type.COMMON, new ModConfigSpec.Builder().build());
    }
}
