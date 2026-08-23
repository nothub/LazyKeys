package lol.hub.lazykeys.fabric;

import lol.hub.lazykeys.LazyKeysCore;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class Main implements ClientModInitializer {

    private static final LazyKeysCore CORE = new LazyKeysCore();

    @Override
    public void onInitializeClient() {
        for (KeyMapping mapping : CORE.keyMappings()) {
            KeyMappingHelper.registerKeyMapping(mapping);
        }

        ClientTickEvents.END_CLIENT_TICK.register(CORE::onTick);
    }

}
