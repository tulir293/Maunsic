package net.maunium.Maunsic;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonParseException;

import net.maunium.Maunsic.Actions.ActionAimbot;
import net.maunium.Maunsic.Actions.ActionAttackaura;
import net.maunium.Maunsic.Actions.ActionAutosoup;
import net.maunium.Maunsic.Actions.ActionAutouse;
import net.maunium.Maunsic.Actions.ActionBlink;
import net.maunium.Maunsic.Actions.ActionEsp;
import net.maunium.Maunsic.Actions.ActionFastbow;
import net.maunium.Maunsic.Actions.ActionFly;
import net.maunium.Maunsic.Actions.ActionFollow;
import net.maunium.Maunsic.Actions.ActionFreecam;
import net.maunium.Maunsic.Actions.ActionFullbright;
import net.maunium.Maunsic.Actions.ActionNofall;
import net.maunium.Maunsic.Actions.ActionPhase;
import net.maunium.Maunsic.Actions.ActionRegen;
import net.maunium.Maunsic.Actions.ActionSpammer;
import net.maunium.Maunsic.Actions.ActionTracer;
import net.maunium.Maunsic.Actions.ActionTrajectories;
import net.maunium.Maunsic.Actions.ActionTriggerbot;
import net.maunium.Maunsic.Actions.ActionXray;
import net.maunium.Maunsic.Actions.Util.ActionHandler;
import net.maunium.Maunsic.Actions.Util.StatusAction;
import net.maunium.Maunsic.KeyMaucros.KeyMaucro;
import net.maunium.Maunsic.Listeners.InChatListener;
import net.maunium.Maunsic.Listeners.OutChatListener;
import net.maunium.Maunsic.Listeners.TickListener;
import net.maunium.Maunsic.Listeners.KeyHandling.InputHandler;
import net.maunium.Maunsic.Listeners.KeyHandling.KeyRegistry;
import net.maunium.Maunsic.Server.ServerHandler;
import net.maunium.Maunsic.Settings.AltAccounts;
import net.maunium.Maunsic.Settings.Attacking;
import net.maunium.Maunsic.Util.I18n;
import net.maunium.Maunsic.Util.MaunsiConfig;
import net.maunium.Maunsic.Util.Logging.ChatLogger;
import net.maunium.Maunsic.Util.Logging.MaunsicLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * The main class of Maunsic.
 * 
 * @author Tulir293
 * @since 0.1
 * @version 0.1 Beta 2. Same as {@link #version}
 */
@Mod(modid = Maunsic.name, version = Maunsic.version, name = Maunsic.name, acceptedMinecraftVersions = "[1.8,1.9)", clientSideOnly = true, dependencies = "required-after:Forge")
public class Maunsic {
	/** The official instance of Maunsic */
	@Instance(Maunsic.name)
	private static Maunsic instance;
	/** "Maunsic", which is the name of this mod. Most likely will never change. */
	public static final String name = "Maunsic";
	/** The version of Minecraft that this version of Maunsic is made for. */
	public static final String forMC = "1.8";
	/** The current version of Maunsic */
	public static final String version = "0.1-B2", longVersion = "0.1 Beta 2";
	private static final File maunsicDir = new File(Minecraft.getMinecraft().mcDataDir, "config" + File.separator + "Maunsic" + File.separator);
	/** A standard Minecraft logger with the name "Maunsic" ({@link #name}) */
	private Logger log;
	/** The real out channel since Forge overrides System.out */
	public static PrintStream sout = new PrintStream(new FileOutputStream(FileDescriptor.out));
	/** The real err channel since Forge overrides System.err */
	public static PrintStream serr = new PrintStream(new FileOutputStream(FileDescriptor.err));
	/** The color of the standard output messages */
	public static final ChatStyle stdStyle = new ChatStyle().setColor(EnumChatFormatting.GRAY);
	/** A colorful IChatComponent that should be used as the start of each Maucros standard output message. */
	public static final IChatComponent stdTag = new ChatComponentText("")
			.appendSibling(new ChatComponentText("[").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)))
			.appendSibling(new ChatComponentText(Maunsic.name).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN).setBold(true)))
			.appendSibling(new ChatComponentText("]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)))
			.appendSibling(new ChatComponentText(" ").setChatStyle(stdStyle));
	/** The color of the error output messages */
	public static final ChatStyle errStyle = new ChatStyle().setColor(EnumChatFormatting.RED);
	/** A colorful IChatComponent that should be used as the start of each Maucros error output message. */
	public static final IChatComponent errTag = new ChatComponentText("")
			.appendSibling(new ChatComponentText("[").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)))
			.appendSibling(new ChatComponentText(Maunsic.name).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED).setBold(true)))
			.appendSibling(new ChatComponentText("]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)))
			.appendSibling(new ChatComponentText(" ").setChatStyle(errStyle));
	/** Initialization time or -1 if the initialization has not finished. */
	private int construct = -1, preInit = -1, init = -1, postInit = -1;
	/** Maunsic Configuration */
	private MaunsiConfig conf;
	private File confFile = new File(getConfDir(), "conf.maudat");
	
	/** Alt list for Maunsic */
	private AltAccounts alts;
	/** The ActionHandler for Maunsic */
	private ActionHandler ach;
	/** Input handler for Maunsic */
	private InputHandler ih;
	/* Tick actions */
	public ActionFly actionFly;
	public ActionPhase actionPhase;
	public ActionTriggerbot actionTriggerbot;
	public ActionFullbright actionFullbright;
	public ActionFastbow actionFastbow;
	public ActionAutosoup actionAutosoup;
	public StatusAction actionNofall, actionBlink, actionSpammer, actionAttackaura, actionAimbot, actionXray, actionTracer, actionEsp, actionAutouse,
			actionRegen, actionFreecam, actionTrajectories, actionFollow;
			
	/**
	 * Constructor
	 */
	public Maunsic() {
		if (!ServerHandler.canUse()) return;
		long st = Minecraft.getSystemTime();
		log = LogManager.getLogger("Maunsic");
		log.info("Loading Maunsic Logger (see logs/Maunsic/ for log files)");
		MaunsicLogger.create();
		log.info("Loading Chat Logger (see logs/Chat/ for log files)");
		ChatLogger.create();
		getLogger().info("Maunsic Loggers opened from constructor in " + (construct = (int) (Minecraft.getSystemTime() - st)) + "ms.");
	}
	
	/**
	 * PreInitializer
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		long st = Minecraft.getSystemTime();
		if (ServerHandler.canUse()) getLogger().trace("Setting mod metadata");
		ModMetadata mm = evt.getModMetadata();
		mm.autogenerated = false;
		mm.name = name;
		mm.modId = name;
		mm.version = version;
		mm.authorList = Arrays.asList("Tulir293");
		mm.url = "http://maunium.net/forge/";
		mm.description = "The follower of Maucros, Maunsic. Made from scratch.\nContains modified versions of GuiLib by Davidee, Expr by Darius and small parts of Wurst Client by Alexander01998. Also contains LuaJ and Apache BCEL";
		
		if (!ServerHandler.canUse()) {
			if (ServerHandler.killswitched) mm.description += "\nNOTE: Maunsic disabled due to using a killswitched version.";
			else if (!ServerHandler.licenced) mm.description += "\nNOTE: Maunsic disabled due to not having a licence.";
			return;
		}
		
		try {
			getLogger().trace("Saving default language files to disk");
			saveLangFiles();
		} catch (IOException e) {
			getLogger().error("Failed to save language files:");
			getLogger().catching(e);
		}
		
		getLogger().trace("Loading config from disk");
		conf = new MaunsiConfig();
		loadConfig();
		
		getLogger().trace("Adding JVM shutdown hook to save config");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				saveConfig();
			}
		});
		
		getLogger().trace("Initializing I18n");
		I18n.reinitMaunsicI18n(this, false);
		
		getLogger().info("PreInit complete in " + (init = (int) (System.currentTimeMillis() - st)) + "ms.");
	}
	
	/**
	 * Initializer
	 */
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		if (!ServerHandler.canUse()) return;
		long st = Minecraft.getSystemTime();
		
		getLogger().trace("Creating and registering ActionHandler");
		FMLCommonHandler.instance().bus().register(ach = new ActionHandler(this));
		MinecraftForge.EVENT_BUS.register(ach);
		
		getLogger().trace("Creating and registering tick listener");
		FMLCommonHandler.instance().bus().register(new TickListener(this));
		
		getLogger().trace("Creating and registering chat listeners");
		MinecraftForge.EVENT_BUS.register(new InChatListener(this));
		MinecraftForge.EVENT_BUS.register(new OutChatListener());
		
		getLogger().trace("Creating alt manager and loading alts");
		alts = new AltAccounts();
		alts.load(getConfig());
		getLogger().trace("Loading Key Maucros");
		KeyMaucro.load(getConfig());
		getLogger().trace("Loading friends and targets");
		Attacking.load(getConfig());
		getLogger().trace("Initializing input handler");
		MinecraftForge.EVENT_BUS.register(ih = new InputHandler(this));
		
		getLogger().trace("Registering actions");
		actionFly = ach.registerAction(new ActionFly(this), ActionHandler.Phase.LIVING);
		actionNofall = ach.registerAction(new ActionNofall(), ActionHandler.Phase.LIVING);
		actionBlink = ach.registerAction(new ActionBlink(), ActionHandler.Phase.STATUS);
		actionPhase = ach.registerAction(new ActionPhase(), ActionHandler.Phase.LIVING);
		actionAttackaura = ach.registerAction(new ActionAttackaura(), ActionHandler.Phase.LIVING);
		actionSpammer = ach.registerAction(new ActionSpammer(), ActionHandler.Phase.TICKSTART);
		actionTriggerbot = ach.registerAction(new ActionTriggerbot(), ActionHandler.Phase.LIVING);
		actionAutosoup = ach.registerAction(new ActionAutosoup(), ActionHandler.Phase.LIVING);
		actionAimbot = ach.registerAction(new ActionAimbot(), ActionHandler.Phase.LIVING);
		actionXray = ach.registerAction(new ActionXray(), ActionHandler.Phase.STATUS);
		actionTracer = ach.registerAction(new ActionTracer(), ActionHandler.Phase.WORLD);
		actionEsp = ach.registerAction(new ActionEsp(), ActionHandler.Phase.WORLD);
		actionAutouse = ach.registerAction(new ActionAutouse(), ActionHandler.Phase.LIVING);
		actionFastbow = ach.registerAction(new ActionFastbow(), ActionHandler.Phase.LIVING);
		actionRegen = ach.registerAction(new ActionRegen(), ActionHandler.Phase.LIVING);
		actionFullbright = ach.registerAction(new ActionFullbright(), ActionHandler.Phase.TICKEND);
		actionFreecam = ach.registerAction(new ActionFreecam(this), ActionHandler.Phase.LIVING);
		actionTrajectories = ach.registerAction(new ActionTrajectories(), ActionHandler.Phase.WORLD);
		actionFollow = ach.registerAction(new ActionFollow(), ActionHandler.Phase.LIVING);
		
		getLogger().info("Init complete in " + (init = (int) (System.currentTimeMillis() - st)) + "ms.");
	}
	
	/**
	 * PostInitializer
	 */
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		if (!ServerHandler.canUse()) return;
		long st = Minecraft.getSystemTime();
		
		getLogger().trace("Refreshing cached I18n");
		I18n.refreshCachedI18n(this);
		getLogger().trace("Loading key binds");
		KeyRegistry.load(getConfig());
		getLogger().trace("Loading data of all actions");
		ach.loadAll();
		
		getLogger().info("PostInit complete in " + (init = (int) (System.currentTimeMillis() - st)) + "ms.");
		getLogger().info(name + " v" + longVersion + " for Minecraft " + forMC + " enabled in " + (construct + preInit + init + postInit) + "ms.");
	}
	
	/*
	 * Config actions
	 */
	
	public MaunsiConfig getConfig() {
		return conf;
	}
	
	public void saveConfig() {
		try {
			conf.save(confFile);
		} catch (IOException e) {
			getLogger().error("Failed to save configuration:");
			getLogger().catching(e);
		}
	}
	
	public void loadConfig() {
		try {
			conf.load(confFile);
		} catch (JsonParseException e) {
			getLogger().error("Failed to load configuration:");
			getLogger().catching(e);
		}
	}
	
	/*
	 * Getters
	 */
	
	/**
	 * Get the main logger for Maunsic.
	 */
	public static final MaunsicLogger getLogger() {
		return MaunsicLogger.getMaunsicLogger();
	}
	
	/**
	 * Get the chat logger by Maunsic which should be used for all chat-related logs.
	 */
	public static final ChatLogger getChatLogger() {
		return ChatLogger.getChatLogger();
	}
	
	/**
	 * Returns the current instance of Maunsic.
	 */
	public static Maunsic getInstance() {
		return instance;
	}
	
	/**
	 * Get the action handler of this Maunsic instance.
	 */
	public ActionHandler getActionHandler() {
		return ach;
	}
	
	/**
	 * Get the input handler of this Maunsic instance.
	 */
	public InputHandler getInputHandler() {
		return ih;
	}
	
	/**
	 * Get the alt handler of this Maunsic instance.
	 */
	public AltAccounts getAlts() {
		return alts;
	}
	
	/*
	 * Language saving methods
	 */
	
	/**
	 * Saves all premade language files.
	 */
	public void saveLangFiles() throws IOException {
		saveLangFile("en_US");
		saveLangFile("fi_FI");
	}
	
	/**
	 * Copies a language file from the lang folder inside the jar to minecraft/config/Maunsic/language/.
	 * 
	 * @param lang The name of the language file without the ending.
	 * @throws IOException If reading (Resource as Stream) or writing (bytestreams.copy) fails
	 */
	private void saveLangFile(String lang) throws IOException {
		File f = new File(getConfDir("language"), lang + ".lang");
		if (f.exists()) f.delete();
		InputStream input = Maunsic.class.getResourceAsStream("/lang/" + lang + ".lang");
		OutputStream output = new FileOutputStream(f);
		ByteStreams.copy(input, output);
		output.flush();
		output.close();
		input.close();
	}
	
	/*
	 * Get conf methods
	 */
	
	/**
	 * Get a config subdirectory with the given name.
	 * 
	 * @param dir The name of the subdirectory
	 * @return A file instance. The directory in the instance will always exist if using this method.
	 */
	public static File getConfDir(String dir) {
		File f = new File(maunsicDir, dir);
		if (!f.exists()) f.mkdirs();
		return f;
	}
	
	/**
	 * Get the main Maunsic config directory
	 * 
	 * @return A file instance. The directory in the instance will always exist if using this method.
	 */
	public static File getConfDir() {
		if (!maunsicDir.exists()) maunsicDir.mkdirs();
		return maunsicDir;
	}
	
	/*
	 * Chat methods
	 */
	
	/**
	 * Localizes a message with I18n and prints it to chat.
	 * 
	 * @param key The I18n tag
	 * @param args The I18n args
	 */
	public static void printChat(String key, Object... args) {
		ChatLogger.printChat(stdTag.createCopy().appendText(I18n.translate(key, args)).setChatStyle(stdStyle));
	}
	
	/**
	 * Localizes a message with I18n and prints it to chat as an error.
	 * 
	 * @param key The I18n tag
	 * @param args The I18n args
	 */
	public static void printChatError(String key, Object... args) {
		ChatLogger.printChat(errTag.createCopy().appendText(I18n.translate(key, args)).setChatStyle(errStyle));
	}
	
	/**
	 * Localizes a message with I18n and prints it to chat using the given style.
	 * 
	 * @param style The Chat Style to use.
	 * @param key The I18n tag
	 * @param args The I18n args
	 */
	public static void printChatStyled(ChatStyle style, String key, Object... args) {
		ChatLogger.printChat(new ChatComponentText(I18n.translate(key, args)).setChatStyle(style));
	}
	
	/**
	 * Localizes a message with I18n and prints it.
	 * 
	 * @param key The I18n tag
	 * @param args The I18n args
	 */
	public static void printChatPlain(String key, Object... args) {
		ChatLogger.printChat(new ChatComponentText(I18n.translate(key, args)));
	}
	
	/**
	 * Sends a chat message to the server. Color support is up to the server.<br>
	 * The server has the ability to modify this all it wants.
	 * 
	 * @param message The message to send
	 */
	public static void sendChat(String message) {
		if (Minecraft.getMinecraft() == null) return;
		if (Minecraft.getMinecraft().thePlayer == null) return;
		Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
	}
}
