package io.jonuuh.camerautil;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;

public class Freelooker
{
    public final int initialPovSetting;
    public final float initialThirdPersonDist;

    public float rotationYaw;
    public float prevRotationYaw;

    public float rotationPitch;
    public float prevRotationPitch;

    public Freelooker(float rotationYaw, float prevRotationYaw, float rotationPitch, float prevRotationPitch, int initialPovSetting, float initialThirdPersonDist)
    {
        this.rotationYaw = rotationYaw;
        this.prevRotationYaw = prevRotationYaw;

        this.rotationPitch = rotationPitch;
        this.prevRotationPitch = prevRotationPitch;

        this.initialPovSetting = initialPovSetting;
        this.initialThirdPersonDist = initialThirdPersonDist;

//        System.out.println("constructed freelooker: "
//                + rotationYaw + " " + rotationPitch + " / "
//                + prevRotationYaw + " " + prevRotationPitch);
    }

    /**
     * Adds 15% to the entity's yaw and subtracts 15% from the pitch. Clamps pitch from -90 to 90. Both arguments in
     * degrees.
     *
     * @see net.minecraft.entity.Entity#setAngles(float, float)
     */
    public void setAngles(float yaw, float pitch)
    {
        float lastPitch = rotationPitch;
        float lastYaw = rotationYaw;

        rotationYaw = rotationYaw + yaw * 0.15F;
        rotationPitch = rotationPitch - pitch * 0.15F;

        rotationPitch = MathHelper.clamp_float(rotationPitch, -90.0F, 90.0F);

        prevRotationPitch += rotationPitch - lastPitch;
        prevRotationYaw += rotationYaw - lastYaw;
    }
}
