package nl.theepicblock.serversidetetris;

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class ParticleUtil {

    /**
     * Sends a particle relative to the center of the screen
     */
    public static void sendRelative(Vec3f colour, float size, double x, double y, double z, float xO, float yO, float zO, float s, int c, ServerPlayerEntity playerEntity) {
        float pitch = playerEntity.getPitch();
        float yaw = playerEntity.getYaw();

        float f = MathHelper.cos((yaw + 90.0F) * 0.017453292F);
        float g = MathHelper.sin((yaw + 90.0F) * 0.017453292F);
        float h = MathHelper.cos(-pitch * 0.017453292F);
        float i = MathHelper.sin(-pitch * 0.017453292F);
        float j = MathHelper.cos((-pitch + 90.0F) * 0.017453292F);
        float k = MathHelper.sin((-pitch + 90.0F) * 0.017453292F);
        Vec3d vec3dA = new Vec3d(f * h, i, g * h);
        Vec3d vec3dB = new Vec3d(f * j, k, g * j);
        Vec3d result = vec3dA.crossProduct(vec3dB).multiply(-1.0D);
        double calculatedXStuff = vec3dA.x * z + vec3dB.x * y + result.x * x;
        double calculatedYStuff = vec3dA.y * z + vec3dB.y * y + result.y * x;
        double calculatedZStuff = vec3dA.z * z + vec3dB.z * y + result.z * x;

        sendParticle(colour, size, playerEntity.getX() + calculatedXStuff, playerEntity.getEyeY() + calculatedYStuff, playerEntity.getZ() + calculatedZStuff, xO, yO, zO, s, c, playerEntity);
    }

    public static void sendParticle(Vec3f colour, float size, double x, double y, double z, float xO, float yO, float zO, float s, int c, ServerPlayerEntity playerEntity) {
        var dust = new DustParticleEffect(colour, size);
        var packet = new ParticleS2CPacket(dust, true, x, y, z, xO, yO, zO, s, c);

        playerEntity.networkHandler.sendPacket(packet);
    }
}
