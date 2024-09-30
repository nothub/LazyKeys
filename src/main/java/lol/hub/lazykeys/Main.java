package lol.hub.lazykeys;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod(value = "lazykeys")
public class Main {
    public Main() {

        var mc = Minecraft.getInstance();
        var keys = List.of(
                Key.of(mc.options.keyUse,    I18n.get("key.lazykeys.use"),    GLFW.GLFW_KEY_KP_2),
                Key.of(mc.options.keyShift,  I18n.get("key.lazykeys.sneak"),  GLFW.GLFW_KEY_KP_3),
                Key.of(mc.options.keyAttack, I18n.get("key.lazykeys.attack"), GLFW.GLFW_KEY_UNKNOWN),
                Key.of(mc.options.keySprint, I18n.get("key.lazykeys.sprint"), GLFW.GLFW_KEY_UNKNOWN),
                Key.of(mc.options.keyJump,   I18n.get("key.lazykeys.jump"),   GLFW.GLFW_KEY_UNKNOWN)
        );

        //noinspection removal
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((RegisterKeyMappingsEvent event) -> {
            for (Key key : keys) {
                event.register(key.toggleKey);
            }
        });

        MinecraftForge.EVENT_BUS.addListener((TickEvent.PlayerTickEvent event) -> {
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
        });

    }
}
