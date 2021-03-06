package net.maunium.Maunsic.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.maunium.Maunsic.Util.MaunsiConfig;

import net.minecraft.entity.EntityLivingBase;

public class Attacking {
	private static List<Class<? extends EntityLivingBase>> targets = Arrays.asList(EntityLivingBase.class);
	private static Set<String> friends = new HashSet<String>();
	public static double range = 4.0D;
	
	public static void save(MaunsiConfig conf) {
		JsonArray targetsa = new JsonArray();
		for (Class<?> c : targets)
			targetsa.add(new JsonPrimitive(c.getName()));
		
		conf.set("attacking.targets", targetsa);
		conf.set("attacking.friends", friends);
		conf.set("attacking.range", range);
	}
	
	@SuppressWarnings("unchecked")
	public static void load(MaunsiConfig conf) {
		targets = new ArrayList<Class<? extends EntityLivingBase>>();
		JsonElement e1 = conf.get("attacking.targets");
		if (e1.isJsonArray()) {
			JsonArray ja = e1.getAsJsonArray();
			for (JsonElement je : ja) {
				try {
					if (je.isJsonPrimitive()) targets.add((Class<? extends EntityLivingBase>) Class.forName(je.getAsString()));
				} catch (Throwable t) {
					continue;
				}
			}
		}
		
		friends = new HashSet<String>(conf.getStringList("attacking.friends"));
		range = conf.getDouble("attacking.range");
	}
	
	public static boolean isFriend(String name) {
		return friends.contains(name.toLowerCase(Locale.ENGLISH));
	}
	
	public static boolean isTarget(Class<? extends EntityLivingBase> c) {
		return targets.contains(c);
	}
	
	public static void addFriend(String name) {
		friends.add(name);
	}
	
	public static void addTarget(Class<? extends EntityLivingBase> c) {
		targets.add(c);
	}
	
	public static void clearFriends() {
		friends.clear();
	}
	
	public static void clearTargets() {
		targets.clear();
	}
	
	public static Set<String> getFriends() {
		return friends;
	}
	
	public static List<Class<? extends EntityLivingBase>> getTargets() {
		return targets;
	}
}
