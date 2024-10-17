package io.jonuuh.camerautil;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

public class FovAnimator
{
    public static FovAnimator INSTANCE;
    private final Minecraft mc;
    private final int animationTimeMs;
    private final double minFov;

    private EaseAnimation fovEaseINAnimation;
    private EaseAnimation fovEaseOUTAnimation;
    private boolean modifiedSmoothCamera;

    private int dWheel;

    public static void createInstance(Minecraft mc, int animationTimeMs, double minFov)
    {
        if (INSTANCE != null)
        {
            throw new IllegalStateException("FovAnimator instance has already been created");
        }
        INSTANCE = new FovAnimator(mc, animationTimeMs, minFov);
    }

    private FovAnimator(Minecraft mc, int animationTimeMs, double minFov)
    {
        this.mc = mc;
        this.animationTimeMs = animationTimeMs;
        this.minFov = minFov;
    }

    public void setDWheel(int dWheel)
    {
        this.dWheel = dWheel;
    }

    // TODO: move key logic and etc into mixin class?
    // zI = zoom in, zO = zoom out
    public float animateFov(float startFov)
    {
        if (CameraUtil.keyZoom.isKeyDown())
        {
            // if zoom key was pressed again while zO was still in progress, finish it here
            // don't null it here when finished to avoid buffering zoom inputs
            if (fovEaseOUTAnimation != null)
            {
                return (float) fovEaseOUTAnimation.getCurrentStep();
            }

            // init zI if not created yet for this key press
            if (fovEaseINAnimation == null)
            {
                fovEaseINAnimation = new EaseAnimation(mc, EaseType.QUAD_IN_OUT, animationTimeMs,
                        minFov, startFov, true); // TODO: 250ms

//                mc.gameSettings.mouseSensitivity -= 0.15F;
                if (!mc.gameSettings.smoothCamera)
                {
                    mc.gameSettings.smoothCamera = true;
                    modifiedSmoothCamera = true;
                }
            }

            // handle extra zoom in/out with scroll wheel
            if (fovEaseINAnimation.isFinished() && dWheel != 0)
            {
                fovEaseINAnimation.setLastStep(fovEaseINAnimation.getLastStep() + (dWheel > 0 ? 5 : -5));
                fovEaseINAnimation.setLastStep(AnimationUtils.clamp(1F, startFov, fovEaseINAnimation.getLastStep()));
//                System.out.println(fovEaseINAnimation.getLastStep());
                dWheel = 0;
            }

            // while key is still down, step through zI animation.
            // getNext() returns the normal animation steps if in progress,
            // or min/max if the animation has finished, depending on `reverse`
            return (float) fovEaseINAnimation.getCurrentStep();
        }
        else
        {
            // zI was finished or in progress, init new zO and then null zI
            if (fovEaseINAnimation != null)
            {
//                mc.gameSettings.mouseSensitivity += 0.15F;
                if (modifiedSmoothCamera)
                {
                    mc.gameSettings.smoothCamera = false;
                    modifiedSmoothCamera = false;
                }

                // use zI last step as zO min to avoid stutter if zI was still in progress
                fovEaseOUTAnimation = new EaseAnimation(mc, EaseType.QUAD_IN_OUT, animationTimeMs,
                        fovEaseINAnimation.getLastStep(), startFov, false);

                fovEaseINAnimation = null;
            }

            // if zO is in progress (!=null), step through it and return normal animation steps until it finishes naturally,
            // either right here or back up in "if zoom key pressed". after being finished it will be nulled here.
            // (can only be nulled and thus let a new animation cycle start if zoom key is not pressed)
            if (fovEaseOUTAnimation != null)
            {
                if (fovEaseOUTAnimation.isFinished())
                {
                    fovEaseOUTAnimation = null;
                    return startFov;
                }
                return (float) fovEaseOUTAnimation.getCurrentStep();
            }

            return startFov;
        }
    }
}
