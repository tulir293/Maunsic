package mauluam;

import java.util.Locale;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.lwjgl.input.Keyboard;

import net.maunium.Maunsic.Maunsic;
import net.maunium.Maunsic.Listeners.KeyHandling.KeyRegistry;
import net.maunium.Maunsic.Listeners.KeyHandling.MauKeybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * MauluaM library to emulate keyboard presses and checking key status.
 * 
 * @author Tulir293
 * @since 0.1
 * @from Maucros
 */
public class MauKeyboardLib extends TwoArgFunction {
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaValue lib = LuaValue.tableOf();
		lib.set("press", new press());
		lib.set("keyup", new keyup());
		lib.set("keydown", new keydown());
		lib.set("iskeydown", new iskeydown());
		env.set("keyboard", lib);
		return lib;
	}
	
	private static int getKeyCode(String s) {
		s = s.toLowerCase(Locale.ENGLISH);
		GameSettings gs = Minecraft.getMinecraft().gameSettings;
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {}
		if (s.equals("forward")) return gs.keyBindForward.getKeyCode();
		else if (s.equals("back")) return gs.keyBindBack.getKeyCode();
		else if (s.equals("left")) return gs.keyBindLeft.getKeyCode();
		else if (s.equals("right")) return gs.keyBindRight.getKeyCode();
		else if (s.equals("sneak")) return gs.keyBindSneak.getKeyCode();
		else if (s.equals("jump")) return gs.keyBindJump.getKeyCode();
		else if (s.equals("inventory")) return gs.keyBindInventory.getKeyCode();
		else if (s.equals("drop")) return gs.keyBindDrop.getKeyCode();
		else if (s.equals("chat")) return gs.keyBindChat.getKeyCode();
		else if (s.equals("attack")) return gs.keyBindAttack.getKeyCode();
		else if (s.equals("use")) return gs.keyBindUseItem.getKeyCode();
		else if (s.equals("pick")) return gs.keyBindPickBlock.getKeyCode();
		else if (s.equals("screenshot")) return gs.keyBindScreenshot.getKeyCode();
		MauKeybind mkb = KeyRegistry.findKeybind(s);
		if (mkb != null) return mkb.getKeyCode();
		else {
			int kc = Keyboard.getKeyIndex(s.toUpperCase());
			if (kc != Keyboard.KEY_NONE) return kc;
			else for (KeyBinding kb : gs.keyBindings)
				if (s.equals(kb.getKeyDescription())) return kb.getKeyCode();
			return Keyboard.KEY_NONE;
		}
	}
	
	public static class iskeydown extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue key) {
			if (key.isint()) return LuaValue.valueOf(Keyboard.isKeyDown(key.toint()));
			else if (key.isstring()) return LuaValue.valueOf(Keyboard.isKeyDown(Keyboard.getKeyIndex(key.tojstring())));
			else return LuaValue.NIL;
		}
	}
	
	public static class keydown extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue key) {
			if (key.isstring()) {
				KeyBinding.setKeyBindState(MauKeyboardLib.getKeyCode(key.tojstring()), true);
				return LuaValue.TRUE;
			} else return LuaValue.FALSE;
		}
	}
	
	public static class keyup extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue key) {
			if (key.isstring()) {
				KeyBinding.setKeyBindState(MauKeyboardLib.getKeyCode(key.tojstring()), false);
				return LuaValue.TRUE;
			} else return LuaValue.FALSE;
		}
	}
	
	public static class press extends VarArgFunction {
		@Override
		public LuaValue invoke(final Varargs args) {
			if (args.narg() == 0) return LuaValue.valueOf(1);
			if (!args.isstring(1)) return LuaValue.valueOf(2);
			if (args.narg() > 1 && !args.isnumber(2)) return LuaValue.valueOf(3);
			
			int keycode = MauKeyboardLib.getKeyCode(args.tojstring(1).toLowerCase());
			
			if (keycode == Keyboard.KEY_NONE) return LuaValue.valueOf(2);
			if (args.narg() == 1) pressfor(keycode, 1);
			else if (args.narg() == 2) pressfor(keycode, args.toint(2));
			else if (args.narg() == 3) {
				if (!args.toboolean(2)) pressfor(keycode, args.toint(2));
				else pressthread(keycode, args.toint(1), false).start();
			} else if (args.toboolean(4)) {
				if (!args.toboolean(2)) forcePressFor(keycode, args.toint(1));
				else pressthread(keycode, args.toint(1), true).start();
			} else if (!args.toboolean(2)) pressfor(keycode, args.toint(1));
			else pressthread(keycode, args.toint(1), false).start();
			return LuaValue.valueOf(0);
		}
		
		private Thread pressthread(final int keycode, final int i, boolean force) {
			if (force) return new Thread() {
				@Override
				public void run() {
					forcePressFor(keycode, i);
				}
			};
			else return new Thread() {
				@Override
				public void run() {
					pressfor(keycode, i);
				}
			};
		}
		
		private void pressfor(int keycode, int i) {
			KeyBinding.setKeyBindState(keycode, true);
			try {
				Thread.sleep(i);
			} catch (InterruptedException e) {
				Maunsic.getLogger().catching(e);
			}
			KeyBinding.setKeyBindState(keycode, false);
		}
		
		private void forcePressFor(int keycode, int i) {
			Minecraft mc = Minecraft.getMinecraft();
			long st = Minecraft.getSystemTime();
			synchronized (mc) {
				while (st + i > Minecraft.getSystemTime()) {
					KeyBinding.setKeyBindState(keycode, true);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						Maunsic.getLogger().catching(e);
						break;
					}
				}
				KeyBinding.setKeyBindState(keycode, false);
			}
		}
	}
}
