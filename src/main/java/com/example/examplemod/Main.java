package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {
    private static final Logger log = LogUtils.getLogger();

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        Minecraft mc = Minecraft.getInstance();
        long windowId = mc.getWindow().getWindow();
        Set<Integer> pressed = ConcurrentHashMap.newKeySet();

        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post event) -> {
            pressed.clear();
            IntStream.range(GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LAST + 1)
                    .filter(key -> GLFW.glfwGetKey(windowId, key) == GLFW.GLFW_PRESS)
                    .boxed()
                    .forEach(pressed::add);
        });

        modContainer.registerConfig(ModConfig.Type.COMMON, new ModConfigSpec.Builder().build());
    }
}
