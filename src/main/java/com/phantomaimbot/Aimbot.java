package com.phantomaimbot;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Aimbot {

    public static KeyBinding AIMBOT_KEY;
    public static boolean enabled = false;

    public static void registerKey() {
        AIMBOT_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.phantomaimbot.toggle",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));
    }

    public static void clientTick() {
        if (AIMBOT_KEY.wasPressed()) {
            enabled = !enabled;
            System.out.println("Aimbot: " + (enabled ? "ON" : "OFF"));
        }

        if (enabled) doAimbot();
    }

    private static void doAimbot() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        double range = 5.0;
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;

        List<Entity> entities = client.world.getEntitiesByClass(Entity.class,
                client.player.getBoundingBox().expand(range),
                e -> e != client.player);

        for (Entity e : entities) {
            double dist = client.player.distanceTo(e);
            if (dist < closestDist) {
                closest = e;
                closestDist = dist;
            }
        }

        if (closest == null) return;

        double dx = closest.getX() - client.player.getX();
        double dy = (closest.getY() + closest.getHeight() / 2) -
                (client.player.getY() + client.player.getEyeHeight(client.player.getPose()));
        double dz = closest.getZ() - client.player.getZ();

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, distXZ));

        float smooth = 0.08f;

        float yawDiff = wrapAngle(targetYaw - client.player.getYaw());
        float pitchDiff = wrapAngle(targetPitch - client.player.getPitch());

        client.player.setYaw(client.player.getYaw() + yawDiff * smooth);
        client.player.setPitch(client.player.getPitch() + pitchDiff * smooth);
    }

    private static float wrapAngle(float angle) {
        angle %= 360F;
        if (angle >= 180F) angle -= 360F;
        if (angle < -180F) angle += 360F;
        return angle;
    }
}

