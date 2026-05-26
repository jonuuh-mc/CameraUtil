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
    private EaseAnimation cameraUtil$easeAnimation;

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
    public float cameraUtil$redirectFieldReadsYaw(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getRotationYaw() : entity.rotationYaw;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F"))
    public float cameraUtil$redirectFieldReadsPrevYaw(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getPrevRotationYaw() : entity.prevRotationYaw;
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F"))
    public float cameraUtil$redirectFieldReadsPitch(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getRotationPitch() : entity.rotationPitch;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F"))
    public float cameraUtil$redirectFieldReadsPrevPitch(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getPrevRotationPitch() : entity.prevRotationPitch;
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    public MovingObjectPosition cameraUtil$redirectCameraClippingRayTrace(WorldClient instance, Vec3 vec1, Vec3 vec2)
    {
        // redirecting to null is fine because rayTraceBlocks returns null if no hit anyway
        return CameraUtil.shouldNoClip ? null : instance.rayTraceBlocks(vec1, vec2);
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "updateCameraAndRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setAngles(FF)V"))
    public void cameraUtil$redirectPlayerAngleSetter(EntityPlayerSP playerSP, float mouseYaw, float mousePitch)
    {
        if (CameraUtil.keyFreelook.isKeyDown())
        {
            if (!Freelooker.INSTANCE.isActive())
            {
                playerSP.setAngles(mouseYaw, mousePitch); // TODO: almost definitely redundant but better safe than sorry?
                Freelooker.INSTANCE.begin(playerSP.rotationYaw, playerSP.prevRotationYaw, playerSP.rotationPitch, playerSP.prevRotationPitch,
                        mc.gameSettings.thirdPersonView);

                if (Freelooker.INSTANCE.getInitThirdPersonView() == 0)
                {
                    mc.gameSettings.thirdPersonView = 1;
                }
            }

            Freelooker.INSTANCE.setAngles(mouseYaw, mousePitch);
            mc.renderGlobal.setDisplayListEntitiesDirty(); // TODO: check where/how this is called in vanilla
        }
        else
        {
            if (Freelooker.INSTANCE.isActive())
            {
                if (mc.gameSettings.thirdPersonView != Freelooker.INSTANCE.getInitThirdPersonView())
                {
                    mc.gameSettings.thirdPersonView = Freelooker.INSTANCE.getInitThirdPersonView();
                }

                mc.renderGlobal.setDisplayListEntitiesDirty(); // TODO: check where/how this is called in vanilla
                Freelooker.INSTANCE.end();
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
