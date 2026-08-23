package lol.hub.lazykeys.neoforge;

import lol.hub.lazykeys.LazyKeysCore;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = "lazykeys", dist = {Dist.CLIENT})
public class Main {

    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath("lazykeys", "keys"));
    private static final LazyKeysCore CORE = new LazyKeysCore(CATEGORY);

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(Main::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(Main::onClientTick);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        for (KeyMapping mapping : CORE.keyMappings()) {
            event.register(mapping);
        }
    }

    // PlayerTickEvent.Post fires once per Player entity tick, which on an
    // integrated server means once on the client/render thread (the local
    // player) AND once on the separate server thread (the server-side
    // player) -- the latter crashes anything here that touches rendering
    // (e.g. sendSystemMessage baking a not-yet-cached font glyph). Use the
    // client-only tick event instead: fires exactly once per client tick,
    // same as Fabric's ClientTickEvents.END_CLIENT_TICK already does.
    private static void onClientTick(ClientTickEvent.Post event) {
        CORE.onTick(Minecraft.getInstance());
    }

}
