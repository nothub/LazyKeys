package lol.hub.lazykeys;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {
    private static final Logger log = LogUtils.getLogger();

    private static final KeyMapping KEYBIND_TOGGLE_RCLICK = new KeyMapping(
            "key.lazykeys.rclick",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F12,
            "category.lazykeys"
    );

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        Minecraft mc = Minecraft.getInstance();

        modEventBus.addListener((RegisterKeyMappingsEvent event ) -> {
            event.register(KEYBIND_TOGGLE_RCLICK);
        });

        AtomicBoolean activeRClick = new AtomicBoolean(false);
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Pre event) -> {
            if (mc.player == null) return;
            if (KEYBIND_TOGGLE_RCLICK.consumeClick()) {
                var newState = !activeRClick.get();
                log.info("right click state: {}", newState);
                activeRClick.set(newState);
            }
            if (activeRClick.get()) {
                mc.options.keyUse.setDown(true);
            }
        });

        Set<Integer> pressed = ConcurrentHashMap.newKeySet();
        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post event) -> {
            long windowId = mc.getWindow().getWindow();
            pressed.clear();
            IntStream.range(GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LAST + 1)
                    .filter(key -> GLFW.glfwGetKey(windowId, key) == GLFW.GLFW_PRESS)
                    .boxed()
                    .forEach(pressed::add);
        });

        modContainer.registerConfig(ModConfig.Type.COMMON, new ModConfigSpec.Builder().build());
    }
}
