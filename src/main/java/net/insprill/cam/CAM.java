package net.insprill.cam;

import lombok.Getter;
import net.insprill.cam.handlers.AdvancementHandler;
import net.insprill.cam.listeners.AdvancementEvent;
import net.insprill.cam.utils.Dependency;
import net.insprill.cam.utils.UpdateChecker;
import net.insprill.xenlib.MinecraftVersion;
import net.insprill.xenlib.XenLib;
import net.insprill.xenlib.commands.Command;
import net.insprill.xenlib.files.YamlFile;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CAM extends JavaPlugin {

	@Getter
	private static CAM instance;

	private final Metrics metrics = new Metrics(this, 9613);

	@Override
	public void onEnable() {
		instance = this;

		if (!MinecraftVersion.isNew()) {
			getLogger().severe("CAM is only compatible with Minecraft 1.13+. Please upgrade to at least 1.13 to use CAM.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		new XenLib(this);

		Dependency.initClasses();

		new AdvancementHandler(this);
		new AdvancementEvent(this);
		new Command("cam", "net.insprill.cam.commands.cam");

		UpdateChecker.getInstance().sendUpdateMessage(Bukkit.getConsoleSender());

		// bStats
		String radius_messages = (YamlFile.CONFIG.getBoolean("Radius.Enabled", false)) ? "Enabled" : "Disabled";
		metrics.addCustomChart(new SimplePie("radius_messages", () -> radius_messages));
	}

}
