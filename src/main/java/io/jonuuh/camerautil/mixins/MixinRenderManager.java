package io.jonuuh.camerautil.mixins;

import io.jonuuh.camerautil.Freelooker;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderManager.class)
public class MixinRenderManager
{
//    @Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;playerViewY:F", ordinal = 1))
//    public void cameraUtil$redirectFieldReadPlayerViewY(RenderManager instance, float value)
//    {
//        return Freelooker.INSTANCE.isActive() ? 1 : entity
////        return cameraUtil$freelooker != null ? cameraUtil$freelooker.rotationPitch : entity.rotationPitch;
//    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
    public float cameraUtil$redirectFieldReadsYaw(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getRotationYaw() : entity.rotationYaw;
    }

    @Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F"))
    public float cameraUtil$redirectFieldReadsPrevYaw(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getPrevRotationYaw() : entity.prevRotationYaw;
    }

    //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// //// ////
    @Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F"))
    public float cameraUtil$redirectFieldReadsPitch(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getRotationPitch() : entity.rotationPitch;
    }

    @Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F"))
    public float cameraUtil$redirectFieldReadsPrevPitch(Entity entity)
    {
        return Freelooker.INSTANCE.isActive() ? Freelooker.INSTANCE.getPrevRotationPitch() : entity.prevRotationPitch;
    }
}
