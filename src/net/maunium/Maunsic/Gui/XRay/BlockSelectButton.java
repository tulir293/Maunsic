package net.maunium.Maunsic.Gui.XRay;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mcf.davidee.guilib.core.Button;
import com.mcf.davidee.guilib.core.Scrollbar.Shiftable;
import com.mcf.davidee.guilib.core.Widget;

import net.maunium.Maunsic.Util.GLHelper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Used by GuiXrayBlocks.
 * 
 * @author Tulir293
 * @since 0.1
 * @from Maucros
 */
public class BlockSelectButton extends Button implements Shiftable {
	public static final int defWidth = 140, defHeight = 18;
	
	protected ItemStack item;
	protected Block base;
	protected List<Widget> tooltip;
	
	private GuiScreen parent;
	protected boolean hover, selected;
	
	public BlockSelectButton(Block b, int dmg, ButtonHandler handler) {
		super(defWidth, defHeight, handler);
		
		parent = mc.currentScreen;
		zLevel = 100;
		base = b;
		item = new ItemStack(b, 0, dmg);
		
		tooltip = Arrays.asList((Widget) new BlockTooltip(item, getBlockName(base), Block.blockRegistry.getIDForObject(base), parent));
	}
	
	public String getBlockName(Block b) {
		if (b.getLocalizedName().equals("tile.null.name")) {
			if (Block.getIdFromBlock(b) == 119) return "End Portal";
			else if (Block.getIdFromBlock(b) == 34) return "Piston Part";
			else if (Block.getIdFromBlock(b) == 36) return "Piston Part";
			else return "Unknown";
		} else if (b.getLocalizedName().equals("tile.brewingStand.name")) return "Brewing Stand";
		else if (b.getLocalizedName().equals("tile.pumpkinStem.name")) return "Pumpkin Stem";
		else if (b.getLocalizedName().equals("tile.diode.name")) return "Redstone Repeater";
		else if (b.getLocalizedName().equals("tile.doorOak.name")) return "Oak Door";
		else if (b.getLocalizedName().equals("tile.doorSpruce.name")) return "Spruce Door";
		else if (b.getLocalizedName().equals("tile.doorBirch.name")) return "Birch Door";
		else if (b.getLocalizedName().equals("tile.doorJungle.name")) return "Jungle Door";
		else if (b.getLocalizedName().equals("tile.doorAcacia.name")) return "Acacia Door";
		else if (b.getLocalizedName().equals("tile.doorDarkOak.name")) return "Dark Oak Door";
		else if (b.getLocalizedName().equals("tile.flowerPot.name")) return "Flower Pot";
		else if (b.getLocalizedName().equals("tile.skull.name")) return "Skull";
		else if (b.getLocalizedName().equals("tile.comparator.name")) return "Redstone Comparator";
		else if (b.getLocalizedName().equals("tile.banner.name")) return "Banner";
		else if (b.getLocalizedName().equals("tile.stoneSlab2.name")) return "Stone Slab";
		else return b.getLocalizedName();
	}
	
	public BlockSelectButton(Block b, ButtonHandler handler) {
		this(b, 0, handler);
	}
	
	@Override
	public void draw(int mx, int my) {
		int red = GLHelper.toInt(255, 25, 25, 200);
		int def = GLHelper.toInt(100, 100, 100, 100);
		if (selected) Gui.drawRect(x, y, x + width, y + height, red);
		else Gui.drawRect(x, y, x + width, y + height, def);
		
		hover = inBounds(mx, my);
		if (hover) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			drawRect(x, y, x + width, y + height, 0x55909090);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			tooltip.get(0).setPosition(mx, my);
		}
		if (item.getItem() != null) {
			RenderHelper.enableGUIStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			renderItem.renderItemIntoGUI(item, x + 1, y + 1);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
		}
		
		if (item != null && item.getItem() != null) mc.fontRendererObj.drawString(item.getDisplayName(), x + 24, y + 5, 0xffffff);
		else mc.fontRendererObj.drawString(getBlockName(base), x + 24, y + 5, 0xffffff);
	}
	
	@Override
	public List<Widget> getTooltips() {
		return hover ? tooltip : super.getTooltips();
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public Block getBase() {
		return base;
	}
	
	@Override
	public void handleClick(int mx, int my, int code) {
		if (isSelected()) setSelected(false);
		else setSelected(true);
		mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
		if (handler != null) handler.buttonClicked(this, code);
	}
	
	@Override
	public boolean click(int mx, int my, int code) {
		return enabled && code == 0 && inBounds(mx, my);
	}
	
	@Override
	public void shiftY(int dy) {
		y += dy;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
