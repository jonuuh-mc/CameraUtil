package io.jonuuh.camerautil.mixins;

import io.jonuuh.camerautil.CameraUtil;
import io.jonuuh.camerautil.EaseAnimation;
import io.jonuuh.camerautil.EaseType;
import io.jonuuh.camerautil.FovAnimator;
import io.jonuuh.camerautil.Freelooker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer
{
    @Shadow
    private Minecraft mc;

    @Shadow
    private float thirdPersonDistance;

    @Shadow
    private float thirdPersonDistanceTemp; // TODO: this can be done better probably

    @Unique
    private Freelooker cameraUtil$freelooker;

    @Unique
    private EaseAnimation cameraUtil$easeAnimation;

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
    public float cameraUtil$redirectFieldReadYaw(Entity entity)
    {
        return cameraUtil$freelooker != null ? cameraUtil$freelooker.rotationYaw : entity.rotationYaw;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F"))
    public float cameraUtil$redirectFieldReadPrevYaw(Entity entity)
    {
        return cameraUtil$freelooker != null ? cameraUtil$freelooker.prevRotationYaw : entity.prevRotationYaw;
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F"))
    public float cameraUtil$redirectFieldReadPitch(Entity entity)
    {
        return cameraUtil$freelooker != null ? cameraUtil$freelooker.rotationPitch : entity.rotationPitch;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F"))
    public float cameraUtil$redirectFieldReadPrevPitch(Entity entity)
    {
        return cameraUtil$freelooker != null ? cameraUtil$freelooker.rotationPitch : entity.prevRotationPitch;
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    public MovingObjectPosition cameraUtil$redirectCameraClippingRayTrace(WorldClient instance, Vec3 vec1, Vec3 vec2)
    {
        // redirecting to null is fine because rayTraceBlocks returns null if no hit anyway
        return CameraUtil.keyNoclip.isKeyDown() ? null : instance.rayTraceBlocks(vec1, vec2);
    }

//    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
//    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(F)V"))
//    private void disableRenderHand(ItemRenderer instance, float partialTicks)
//    {
//        if (cameraUtil$freelooker == null)
//        {
//            instance.renderItemInFirstPerson(partialTicks);
//        }
//    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "updateCameraAndRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setAngles(FF)V"))
    public void cameraUtil$redirectPlayerAngleSetter(EntityPlayerSP playerSP, float mouseYaw, float mousePitch)
    {
        if (CameraUtil.keyFreelook.isKeyDown())
        {
            if (cameraUtil$freelooker == null)
            {
                playerSP.setAngles(mouseYaw, mousePitch); // TODO: almost definitely redundant but better safe than sorry?
                cameraUtil$freelooker = new Freelooker(playerSP.rotationYaw, playerSP.prevRotationYaw, playerSP.rotationPitch, playerSP.prevRotationPitch,
                        mc.gameSettings.thirdPersonView, thirdPersonDistance); // TODO: maybe could/should make this a singleton?

                if (cameraUtil$freelooker.initialPovSetting == 0)
                {
                    mc.gameSettings.thirdPersonView = 1;
                }
            }

            cameraUtil$freelooker.setAngles(mouseYaw, mousePitch);
            mc.renderGlobal.setDisplayListEntitiesDirty(); // TODO: check where/how this is called in vanilla

            if (cameraUtil$freelooker.initialPovSetting == 0)
            {
                if (cameraUtil$easeAnimation == null)
                {
                    cameraUtil$easeAnimation = new EaseAnimation(mc, EaseType.CUBIC_IN_OUT,
                            400, 0.4F, cameraUtil$freelooker.initialThirdPersonDist, false);
                }

                if (!cameraUtil$easeAnimation.isFinished())
                {
                    float step = (float) cameraUtil$easeAnimation.getCurrentStep();
                    thirdPersonDistance = step;
                    thirdPersonDistanceTemp = step;
//                    System.out.println(thirdPersonDistance + " " + thirdPersonDistanceTemp);
                }
            }
        }
        else
        {
            // animation cleanup
            if (cameraUtil$easeAnimation != null)
            {
                thirdPersonDistance = cameraUtil$freelooker.initialThirdPersonDist;
                thirdPersonDistanceTemp = cameraUtil$freelooker.initialThirdPersonDist;

                cameraUtil$easeAnimation = null;
            }

            // freelooker cleanup
            if (cameraUtil$freelooker != null)
            {
                if (mc.gameSettings.thirdPersonView != cameraUtil$freelooker.initialPovSetting)
                {
                    mc.gameSettings.thirdPersonView = cameraUtil$freelooker.initialPovSetting;
                }

                mc.renderGlobal.setDisplayListEntitiesDirty(); // TODO: check where/how this is called in vanilla
                cameraUtil$freelooker = null;
//                thirdPersonDistance = 4F;
            }

            playerSP.setAngles(mouseYaw, mousePitch);
        }
    }

//    @ModifyArgs(method = "orientCamera",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 4))
//    public void modifyTranslation(Args args)
//    {
//        args.set(1, ((float) args.get(1)) - 1F);
//    }
//
//    @ModifyArg(method = "orientCamera",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 9), index = 0)
//    public float modifyYaw(float angle)
//    {
////        float yaw = Freelooker.INSTANCE.prevRotationYaw + (Freelooker.INSTANCE.rotationYaw - Freelooker.INSTANCE.prevRotationYaw) * Freelooker.INSTANCE.partialTicks + 180.0F;
//        return cameraUtil$freelooker != null ? cameraUtil$freelooker.rotationYaw : angle;
//    }
//
//    @ModifyArg(method = "orientCamera",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 8), index = 0)
//    public float modifyPitch(float angle)
//    {
////        float pitch = Freelooker.INSTANCE.prevRotationPitch + (Freelooker.INSTANCE.rotationPitch - Freelooker.INSTANCE.prevRotationPitch) * Freelooker.INSTANCE.partialTicks;
//        return cameraUtil$freelooker != null ? cameraUtil$freelooker.rotationPitch : angle;
//    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Inject(method = "getFOVModifier", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void cameraUtil$modifyFOVReturnValue(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir)
    {
        // only true when rendering hand (therefore zoom animation will not affect hand)
        if (!useFOVSetting)
        {
            return;
        }

        float defaultFov = cir.getReturnValue();
        float animatedFov = FovAnimator.INSTANCE.animateFov(defaultFov); // TODO: fix this awful design

        if (animatedFov != defaultFov)
        {
            mc.renderGlobal.setDisplayListEntitiesDirty(); // TODO: check where/how this is called in vanilla
            cir.setReturnValue(animatedFov);
        }
    }

//    @Inject(
//            method = "orientCamera",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 4)/*, index = 1*/)
//    public void modifyCameraTranslate(float partialTicks, CallbackInfo ci)
//    {
//        GlStateManager.scale(1F, 1F, 0.5F);
//
////        return -(mc.getRenderViewEntity().height /** 0.5F*/ /** 0.85F*/);
////        return playerOpacity$sameTeamFlag ? 0.3F : colorAlpha;
//    }
}
