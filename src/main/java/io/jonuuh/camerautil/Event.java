package io.jonuuh.camerautil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Event
{
//    private long startTime = -1;
//    private float startFov = -1;
//
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
//        if (Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isKeyDown())
//        {
//            System.out.println("middle click down");
//        render();
//        }

//        System.out.println(Minecraft.getMinecraft().thePlayer.rotationYaw + " " + Minecraft.getMinecraft().thePlayer.cameraYaw);

//        Minecraft.getMinecraft().thePlayer.cameraYaw = 45F;

    }
//
//    @SubscribeEvent
//    public void modifyFovHook(EntityViewRenderEvent.FOVModifier event)
//    {
//        if (Minecraft.getMinecraft().gameSettings.keyBindPickBlock.isKeyDown())
//        {
//            if (startTime == -1)
//            {
//                startTime = Minecraft.getSystemTime();
//                startFov = Minecraft.getMinecraft().gameSettings.fovSetting /*- 5*/;
//            }
//
//            float finalFOV = getEasedFov(750, 10, startFov);
//
////            if (startFov - finalFOV > 3)
////            {
//            event.setFOV(finalFOV);
////            }
//        }
//        else if (startTime != -1)
//        {
//            startTime = -1;
//            startFov = -1;
//        }
//
////        // TODO: check if runs by default before FML event is cancelled
//        Minecraft.getMinecraft().renderGlobal.setDisplayListEntitiesDirty();
//    }
//
//    private float getEasedFov(int animationTimeMs, float minFov, float maxFov)
//    {
//        long elapsed = Minecraft.getSystemTime() - startTime;
//        double normElapsed = normalize(0, animationTimeMs, elapsed);
////        normElapsed = Math.min(normElapsed, 1.0) /*clamp(0.0, 1.0, progress)*/;
//
//        if (normElapsed >= 1.0)
//        {
//            return minFov;
//        }
//
//        double eased = easeInOut(2, normElapsed);
//
//        double inOutFov = denormalize(minFov, maxFov, eased);
//        double outInFov = -inOutFov + minFov + maxFov;
//
////        System.out.println(elapsed + " " + normElapsed + " " + eased + " " + outInFov + " " + maxFov);
//
////        float finalFOV = (float) clamp(5, startFov, (startFov - fov));
//
//        return (float) outInFov;
//    }
//
//    private double easeInOut(float scale, double x)
//    {
//        double firstHalf = (0.5F / Math.pow(0.5, scale)) * Math.pow(x, scale);
//        double secondHalf = 1 - (Math.pow(-2 * x + 2, scale) / 2);
//
//        return (x < 0.5) ? (firstHalf) : (secondHalf);
//    }
//
//    private double normalize(double min, double max, double value)
//    {
//        return (value - min) / (max - min);
//    }
//
//    private double denormalize(double min, double max, double value)
//    {
//        return value * (max - min) + min;
//    }
//
//    protected double clamp(double min, double max, double value)
//    {
//        return Math.min((Math.max(value, min)), max);
//    }


    private void render()
    {
//        long elapsed = Minecraft.getSystemTime() - startTime;
//
//        double normElapsed = normalize(0, 1500, elapsed);
//        normElapsed = Math.min(normElapsed, 1.0) /*clamp(0.0, 1.0, progress)*/;

//        GL11.glPushMatrix();
//        GL11.glDepthMask(false);
////        GL11.glEnable(GL11.GL_BLEND);
////        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GL11.glEnable(GL11.GL_LINE_SMOOTH);
//        GL11.glLineWidth(2F);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//
//        Color c = new Color("#ff0000");
//        GL11.glColor4f(c.getR(), c.getG(), c.getB(), c.getA());
//
//        glHead();
//        GL11.glBegin(GL11.GL_LINE_STRIP);
//
//        for (double x = 0; x <= 1; x += 0.01)
//        {
//            double easedY = easeInOut(3, x);
//            GL11.glVertex3d(x, easedY, 0);
////            double fov = denormalize(0, 99, eased);
//        }
//
//        GL11.glEnd();
//        glTail();

//
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glDepthMask(true);
//        GL11.glPopMatrix();

//        double eased = easeInOut(normElapsed);
//
//        double fov = denormalize(0, startFov, eased);
//
//        System.out.println(elapsed + " " + normElapsed + " " + eased + " " + fov + " " + startFov);

//        float finalFOV = (float) clamp(5, startFov, (startFov - fov));
    }

    public void glHead()
    {
        // GL setup
        GL11.glPushMatrix();
        GL11.glDepthMask(false);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(3F);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);

        Vec3 renderPos = getEntityPosForRender(Minecraft.getMinecraft(), Minecraft.getMinecraft().thePlayer, 0.0F);
        Vec3 finalPos = new Vec3(0, 5, 1);

        GL11.glTranslated((finalPos.xCoord - renderPos.xCoord), (finalPos.yCoord - renderPos.yCoord), (finalPos.zCoord - renderPos.zCoord));
    }

    public void glTail()
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glLineWidth(1F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
    }

    public Vec3 getEntityPosForRender(Minecraft mc, EntityPlayer player, float partialTicks)
    {
        RenderManager renderManager = mc.getRenderManager();

        if (player == mc.thePlayer && renderManager.livingPlayer == mc.thePlayer)
        {
            // viewer pos (usually client player pos) is cached by render manager during each render pass
            return new Vec3(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
        }

        double x = (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
        double y = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
        double z = (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);
        return new Vec3(x, y, z);
    }
}
