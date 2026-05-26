package io.jonuuh.camerautil;

import net.minecraft.util.MathHelper;

public class Freelooker
{
    public static Freelooker INSTANCE;
    private int initThirdPersonView;
//    private float initialThirdPersonDist;

    private float rotationYaw;
    private float prevRotationYaw;

    private float rotationPitch;
    private float prevRotationPitch;

    private boolean isActive;

    private boolean canTranslate;

    public static void createInstance()
    {
        if (INSTANCE != null)
        {
            throw new IllegalStateException("Freelooker instance has already been created");
        }
        INSTANCE = new Freelooker();
    }

    private Freelooker()
    {
    }

    public void begin(float rotationYaw, float prevRotationYaw, float rotationPitch, float prevRotationPitch, int initThirdPersonView)
    {
        this.rotationYaw = rotationYaw;
        this.prevRotationYaw = prevRotationYaw;

        this.rotationPitch = rotationPitch;
        this.prevRotationPitch = prevRotationPitch;

        this.initThirdPersonView = initThirdPersonView;
        this.isActive = true;
    }

    public void end()
    {
        begin(0, 0, 0, 0, 0); // TODO: not necessary at all really
        this.isActive = false;
    }

    public int getInitThirdPersonView()
    {
        return initThirdPersonView;
    }

    public float getRotationYaw()
    {
        return rotationYaw;
    }

    public float getPrevRotationYaw()
    {
        return prevRotationYaw;
    }

    public float getRotationPitch()
    {
        return rotationPitch;
    }

    public float getPrevRotationPitch()
    {
        return prevRotationPitch;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public boolean canTranslate()
    {
        return canTranslate;
    }

    public void setCanTranslate(boolean canTranslate)
    {
        this.canTranslate = canTranslate;
    }

    /**
     * Adds 15% to the entity's yaw and subtracts 15% from the pitch. Clamps pitch from -90 to 90. Both arguments in
     * degrees.
     *
     * @see net.minecraft.entity.Entity#setAngles(float, float)
     */
    public void setAngles(float yaw, float pitch)
    {
        if (!isActive)
        {
            return;
        }

        float lastPitch = rotationPitch;
        float lastYaw = rotationYaw;

        rotationYaw = rotationYaw + yaw * 0.15F;
        rotationPitch = rotationPitch - pitch * 0.15F;

        rotationPitch = MathHelper.clamp_float(rotationPitch, -90.0F, 90.0F);

        prevRotationPitch += rotationPitch - lastPitch;
        prevRotationYaw += rotationYaw - lastYaw;
    }
}
