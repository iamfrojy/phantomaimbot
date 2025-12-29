package com.phantomaimbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PhantomAimbot implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("PhantomAimbot loaded.");
        Aimbot.registerKey();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Aimbot.clientTick();
        });
    }
}
