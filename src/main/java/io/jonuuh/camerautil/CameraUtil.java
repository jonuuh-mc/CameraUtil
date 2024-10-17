package io.jonuuh.camerautil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(
        modid = "camerautil",
        version = "1.0.0",
        acceptedMinecraftVersions = "[1.8.9]"
)
public class CameraUtil
{
    public static KeyBinding keyZoom;
    public static KeyBinding keyFreelook;
    public static KeyBinding keyNoclip;

    @Mod.EventHandler
    public void FMLInit(FMLInitializationEvent event)
    {
        FovAnimator.createInstance(Minecraft.getMinecraft(), 250, 20);

        keyZoom = new KeyBinding("CU-Zoom", -98, "CameraUtil");
        keyFreelook = new KeyBinding("CU-Freelook", Keyboard.KEY_F, "CameraUtil");
        keyNoclip = new KeyBinding("CU-Noclip", Keyboard.KEY_C, "CameraUtil");

        // TODO: keys still work if not registered which is a bit unexpected to me
        // only for gameSettings map / controls gui?
        ClientRegistry.registerKeyBinding(keyZoom);
        ClientRegistry.registerKeyBinding(keyFreelook);
        ClientRegistry.registerKeyBinding(keyNoclip);

//        MinecraftForge.EVENT_BUS.register(new Event());
    }
}
