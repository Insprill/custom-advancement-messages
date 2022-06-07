package net.insprill.cam.handlers;

import lombok.Getter;
import net.insprill.cam.utils.files.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Keyed;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class AdvancementHandler implements Listener {

	@Getter
	public static AdvancementHandler instance;

	public AdvancementHandler(JavaPlugin plugin) {
		if (instance == null)
			instance = this;

		Bukkit.getPluginManager().registerEvents(this, plugin);
		init();
	}

	public void init() {
		for (World world : Bukkit.getWorlds()) {
			world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		}
		Iterator<Advancement> iter = Bukkit.advancementIterator();
		while (iter.hasNext()) {
			Advancement advancement = iter.next();
			String key = AdvancementHandler.formatKey(advancement);
			if (key.contains("root") || key.contains("recipes"))
				continue;
			if (!YamlFile.ADV_MESSAGES.contains(key)) {
				YamlFile.ADV_MESSAGES.set(key, "default");
			}
		}
		YamlFile.ADV_MESSAGES.save();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldCreation(WorldLoadEvent e) {
		e.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
	}

	/**
	 * @param key Format key into config-friendly format.
	 * @return Key in a format that can be put in a config file.
	 */
	public static String formatKey(Keyed key) {
		if (key == null) return "none";
		return formatKey(key.getKey().toString());
	}

	/**
	 * @param key Format string-tuned-key into config-friendly format.
	 * @return String that can be put in a config file.
	 */
	public static String formatKey(String key) {
		if (key == null) return "none";
		key = key.replace("/", ".");
		key = key.replace(":", ".");
		return key;
	}

}
