package net.maunium.Maunsic.Actions;

import net.maunium.Maunsic.Maunsic;
import net.maunium.Maunsic.Actions.Util.TickAction;
import net.maunium.Maunsic.Util.MaunsiConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.EnumChatFormatting;

public class ActionFreecam implements TickAction {
	private Maunsic host;
	private boolean active = false;
	private double oldX, oldY, oldZ;
	private EntityOtherPlayerMP fake = null;
	
	public ActionFreecam(Maunsic host) {
		this.host = host;
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			oldX = Minecraft.getMinecraft().thePlayer.posX;
			oldY = Minecraft.getMinecraft().thePlayer.posY;
			oldZ = Minecraft.getMinecraft().thePlayer.posZ;
			
			fake = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.getGameProfile());
			fake.clonePlayer(Minecraft.getMinecraft().thePlayer, true);
			fake.copyLocationAndAnglesFrom(Minecraft.getMinecraft().thePlayer);
			fake.rotationYawHead = Minecraft.getMinecraft().thePlayer.rotationYawHead;
			
			Minecraft.getMinecraft().theWorld.addEntityToWorld(-293, fake);
		} else {
			Minecraft.getMinecraft().thePlayer.setPositionAndRotation(oldX, oldY, oldZ, Minecraft.getMinecraft().thePlayer.rotationYaw,
					Minecraft.getMinecraft().thePlayer.rotationPitch);
			
			Minecraft.getMinecraft().theWorld.removeEntityFromWorld(-293);
			fake = null;
			
			Minecraft.getMinecraft().renderGlobal.loadRenderers();
		}
	}
	
	@Override
	public void execute() {
		if (host.actionFly.isActive()) Minecraft.getMinecraft().thePlayer.noClip = true;
		else Minecraft.getMinecraft().thePlayer.noClip = false;
	}
	
	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
	public void saveData(MaunsiConfig conf) {}
	
	@Override
	public void loadData(MaunsiConfig conf) {}
	
	@Override
	public String[] getStatusText() {
		return new String[] { "Freecam " + EnumChatFormatting.GREEN + "ON" };
	}
}
