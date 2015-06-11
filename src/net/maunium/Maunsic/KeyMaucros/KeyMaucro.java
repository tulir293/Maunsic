package net.maunium.Maunsic.KeyMaucros;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

/**
 * An abstract Key Maucro. Contains the basic parts, but execution is up to subclasses.
 * 
 * @author Tulir293
 * @since 0.1
 * @from Maucros
 */
public abstract class KeyMaucro implements Serializable, Comparable<KeyMaucro> {
	static final long serialVersionUID = 29300001L;
	
	protected String name;
	protected int keyCode;
	protected int[] shiftKeys;
	protected ExecPhase phase;
	
	public KeyMaucro(String name, int keyCode, ExecPhase phase, int... shiftKeys) {
		this.name = name;
		this.keyCode = keyCode;
		this.shiftKeys = shiftKeys;
		this.phase = phase;
	}
	
	@Override
	public int compareTo(KeyMaucro to) {
		int x = shiftKeys.length;
		int y = to.shiftKeys.length;
		return x < y ? 1 : x == y ? 0 : -1;
	}
	
	public static int compare(KeyMaucro km1, KeyMaucro km2) {
		return km1.compareTo(km2);
	}
	
	@Override
	public abstract String toString();
	
	/**
	 * Execute this Key Maucro
	 */
	public abstract void executeMacro();
	
	/**
	 * Get the type of this Key Maucro
	 */
	public abstract Type getType();
	
	/**
	 * Get the data of this Key Maucro.<br>
	 * Usually something such as the Execution data.<br>
	 * Should never return null!
	 */
	public abstract String getData();
	
	/**
	 * Get the name of this Key Maucro
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Set the name of this Key Maucro.
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the key code of this Key Maucro
	 */
	public final int getKeyCode() {
		return keyCode;
	}
	
	/**
	 * Set the key code of this Key Maucro
	 */
	public final void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	
	/**
	 * Get the Execution Phase of this Key Maucro
	 */
	public final ExecPhase getExecutionPhase() {
		return phase;
	}
	
	/**
	 * Set the Execution Phase of this Key Maucro
	 */
	public final void setExecutionPhase(ExecPhase phase) {
		this.phase = phase;
	}
	
	/**
	 * Get the shift keys of this Key Maucro
	 */
	public final int[] getShiftKeys() {
		return shiftKeys;
	}
	
	/**
	 * Set the shift keys of this Key Maucro
	 */
	public final void setShiftKeys(int... shiftKeys) {
		this.shiftKeys = shiftKeys;
	}
	
	public String getShiftKeysToSave() {
		String s = "";
		for (int i : shiftKeys)
			if (s.equals("")) s = i + "";
			else s = s + "-~-" + i;
		return s;
	}
	
	public String getShiftKeysAsString() {
		String s = "";
		for (int i : shiftKeys)
			if (s.equals("")) s = toNaturalCase(Keyboard.getKeyName(i));
			else s = s + " + " + toNaturalCase(Keyboard.getKeyName(i));
		return s;
	}
	
	private String toNaturalCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	/**
	 * Parse a Key Maucro from a String.
	 * 
	 * @param s The String to parse the Key Maucro from.
	 * @param t The type of the Key Maucro
	 * @return The parsed Key Maucro
	 * @throws KeyMaucroFormatException If parsing fails
	 */
	public static KeyMaucro parseKeyMaucro(String s) throws KeyMaucroFormatException {
		if (s == null) throw new KeyMaucroFormatException("You can't parse a Key Maucro from a null String!");
		String[] ss = s.split(Pattern.quote("|"));
		Type t = Type.fromString(ss[0]);
		if (t == null) throw new KeyMaucroFormatException("You can't parse a Key Maucro with Type null!");
		else if (t.equals(Type.COMMANDCHAIN)) return CCKeyMaucro.parseKeyMaucro(ss[1]);
		else if (t.equals(Type.LUA)) return LuaKeyMaucro.parseKeyMaucro(ss[1]);
		else throw new KeyMaucroFormatException("The type " + t.toString() + " can't be recognized.");
	}
	
	/**
	 * An Exception that is thrown when parsing a Key Maucro from a String fails.
	 * 
	 * @author Tulir293
	 * @since 0.1
	 * @from Maucros
	 */
	public static class KeyMaucroFormatException extends IllegalArgumentException {
		private static final long serialVersionUID = 734506366998594210L;
		
		/**
		 * Constructs a KeyMaucroFormatException with the given reason.
		 * 
		 * @param r The reason
		 */
		public KeyMaucroFormatException(String r) {
			super(r);
		}
	}
	
	/**
	 * Returns true if the main key and all the shift keys are pressed.
	 */
	public boolean isMacroDown() {
		for (int i : shiftKeys)
			if (!Keyboard.isKeyDown(i)) return false;
		return Keyboard.isKeyDown(keyCode);
	}
	
	/**
	 * Checks if the keys are down and executes the Maucro if they are.
	 * 
	 * @return If the Maucro was executed or not.
	 */
	public final boolean checkAndExecute() {
		if (isMacroDown()) {
			executeMacro();
			return true;
		} else return false;
	}
	
	/**
	 * Check if this Key Maucro is equal to the given one. This should be overridden by each subclass if they have extra data to check.
	 */
	public boolean equals(KeyMaucro km) {
		if (!name.equals(km.name)) return false;
		if (keyCode != km.keyCode) return false;
		if (!shiftKeys.equals(km.shiftKeys)) return false;
		if (!phase.equals(km.phase)) return false;
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof KeyMaucro) return equals((KeyMaucro) o);
		else return false;
	}
	
	/**
	 * The Key Maucro type.
	 * 
	 * @author Tulir293
	 * @since 0.1
	 * @from Maucros
	 */
	public static enum Type {
		COMMANDCHAIN, LUA;
		public int toGuiState() {
			switch (this) {
				case COMMANDCHAIN:
					return 0;
				case LUA:
					return 1;
				default:
					return -1;
			}
		}
		
		public static Type fromString(String s) {
			if (s.equalsIgnoreCase("commandchain")) return COMMANDCHAIN;
			else if (s.equalsIgnoreCase("lua")) return LUA;
			else return null;
		}
		
		public static Type fromGuiState(int state) {
			switch (state) {
				case 0:
					return COMMANDCHAIN;
				case 1:
					return LUA;
				default:
					return null;
			}
		}
	}
	
	/**
	 * The execution phase of a Key Maucro
	 * 
	 * @author Tulir293
	 * @since 0.1
	 * @from Maucros
	 */
	public static enum ExecPhase {
		PRECHECKS, PREKEYS, POSTKEYS;
		public int toInt() {
			switch (this) {
				case PRECHECKS:
					return 0;
				case PREKEYS:
					return 1;
				case POSTKEYS:
					return 2;
				default:
					return -1;
			}
		}
		
		public static ExecPhase fromString(String s) {
			if (s.equalsIgnoreCase("PRECHECKS")) return PRECHECKS;
			else if (s.equalsIgnoreCase("PREKEYS")) return PREKEYS;
			else if (s.equalsIgnoreCase("POSTKEYS")) return POSTKEYS;
			else return null;
		}
		
		public static ExecPhase fromInt(int i) {
			switch (i) {
				case 0:
					return PRECHECKS;
				case 1:
					return PREKEYS;
				case 2:
					return POSTKEYS;
				default:
					return null;
			}
		}
	}
}
