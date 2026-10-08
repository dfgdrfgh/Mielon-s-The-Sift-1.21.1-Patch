package mielon.thesift.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mielon.thesift.world.SiftDayNightCycle;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class SiftDimensionEffects extends DimensionSpecialEffects {
   public SiftDimensionEffects() {
      super(192.0F, true, SkyType.NORMAL, false, false);
   }

   public Vec3 getBrightnessDependentFogColor(Vec3 original, float brightness) {
      ClientLevel level = Minecraft.getInstance().level;
      float night = level == null ? 0.0F : SiftDayNightCycle.nightBlend(level.getDayTime());
      return new Vec3((201.0F + -188.0F * night) / 255.0, (239.0F + -178.0F * night) / 255.0, (241.0F + -121.0F * night) / 255.0);
   }

   public boolean isFoggyAt(int x, int z) {
      return false;
   }

   public float[] getSunriseColor(float time, float partial) {
      return null;
   }

   public boolean renderSky(
      ClientLevel level, int ticks, float partial, Matrix4f modelView, Camera camera, Matrix4f projection, boolean foggy, Runnable setupFog
   ) {
      setupFog.run();
      PoseStack poses = new PoseStack();
      poses.mulPose(modelView);
      SiftProceduralSkyRenderer.render(poses, level, projection);
      return true;
   }

   public void adjustLightmapColors(ClientLevel level, float partial, float darken, float flicker, float skyLight, int block, int sky, Vector3f colors) {
      float night = SiftDayNightCycle.nightBlend(level.getDayTime());
      float skyAmount = sky / 15.0F * (1.0F - night * 0.4F);
      colors.set(Math.max(colors.x, skyAmount * (1.0F - night * 0.6F)), Math.max(colors.y, skyAmount * (1.0F - night / 3.0F)), Math.max(colors.z, skyAmount));
      colors.mul(1.0F - night * 0.56078434F * (1.0F - block / 15.0F), 1.0F - night * 0.38823527F * (1.0F - block / 15.0F), 1.0F);
   }
}
