package io.jonuuh.camerautil.mixins;

import io.jonuuh.camerautil.CameraUtil;
import io.jonuuh.camerautil.FovAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
    public int cameraUtil$redirectMethodInvokeGetMouseDWheel()
    {
        int dWheel = Mouse.getEventDWheel();
        FovAnimator.INSTANCE.setDWheel(dWheel);

        return CameraUtil.keyZoom.isKeyDown() ? 0 : dWheel;
    }

    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0))
    public int cameraUtil$redirectFieldReadThirdPersonView(GameSettings instance)
    {
        // redirected after field is read but before written (incremented), so decrement to cancel
        return CameraUtil.keyFreelook.isKeyDown() ? --instance.thirdPersonView: instance.thirdPersonView;
    }
}
