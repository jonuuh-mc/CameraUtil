package io.jonuuh.camerautil;

import net.minecraft.client.Minecraft;

public class EaseAnimation
{
    public final Minecraft mc;
    public final EaseType easeType;
    public final long startTime;
    public final int animationTimeMs;
    public final double min;
    public final double max;
    public final boolean reverse;

    private boolean isFinished;
    private double lastStep;

    public EaseAnimation(Minecraft mc, EaseType easeType, int animationTimeMs, double min, double max, boolean reverse)
    {
        this.mc = mc;
        this.easeType = easeType;
        this.startTime = Minecraft.getSystemTime();
        this.animationTimeMs = animationTimeMs;
        this.min = min;
        this.max = max;
        this.reverse = reverse;
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public double getLastStep()
    {
        return lastStep;
    }

    public void setLastStep(double lastStep)
    {
        this.lastStep = lastStep;
    }

    public double getCurrentStep()
    {
        return isFinished ? lastStep : step();
    }

    // https://www.desmos.com/calculator/jjtwicypl2
    private double step()
    {
        long elapsedMs = Minecraft.getSystemTime() - startTime;
        double normElapsed = AnimationUtils.normalize(0, animationTimeMs, elapsedMs);

        if (normElapsed >= 1.0)
        {
            isFinished = true;
            lastStep = reverse ? min : max; // TODO ?
            return lastStep;
        }

        double normEased = getEasedElapsedTime(normElapsed);

        double easedValue = AnimationUtils.denormalize(min, max, normEased);

//        System.out.println(elapsed + " " + normElapsed + " " + eased + " " + outInFov + " " + maxFov);
        double step = (reverse ? (-easedValue + min + max) : easedValue);
        lastStep = step;

        return step;
    }

    private double getEasedElapsedTime(double normTime)
    {
        switch (easeType)
        {
            case QUAD_IN:
            case CUBIC_IN:
            case QUART_IN:
            case QUINT_IN:
                return AnimationUtils.easeIn(easeType.scale, normTime);
            case QUAD_OUT:
            case CUBIC_OUT:
            case QUART_OUT:
            case QUINT_OUT:
                return AnimationUtils.easeOut(easeType.scale, normTime);
            case QUAD_IN_OUT:
            case CUBIC_IN_OUT:
            case QUART_IN_OUT:
            case QUINT_IN_OUT:
                return AnimationUtils.easeInOut(easeType.scale, normTime);
            default:
                return normTime;
        }
    }
}
