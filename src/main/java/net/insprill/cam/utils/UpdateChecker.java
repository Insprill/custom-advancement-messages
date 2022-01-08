package net.insprill.cam.utils;

import lombok.Getter;
import net.insprill.cam.CAM;
import net.insprill.cam.utils.files.YamlFile;
import net.insprill.xenlib.ColourUtils;
import net.insprill.xenlib.XenScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

	@Getter
	private static final UpdateChecker instance = new UpdateChecker();

	public String newVersion;
	private final String pluginID = "86618";

	public boolean checkForUpdates() {
		if (YamlFile.CONFIG.getBoolean("DisableUpdateChecker"))
			return false;

		String currentVersion = CAM.getInstance().getDescription().getVersion();
		if (currentVersion.contains("SNAPSHOT"))
			return false;

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new URL("https://api.insprill.net/v1/minecraft/spigot/plugins/cam/version/")
								.openConnection()
								.getInputStream()))) {
			newVersion = br.readLine();
			if (!newVersion.equals(currentVersion)) return true;
		} catch (Exception e) {
			CAM.getInstance().getLogger().severe("Could not check for updates! Error: " + e.getMessage());
		}
		return false;
	}

	public void sendUpdateMessage(CommandSender sender) {
		XenScheduler.runTaskAsync(() -> {
			if (!UpdateChecker.getInstance().checkForUpdates())
				return;
			if (sender instanceof Player) {
				String message = "{\"text\":\"&2CAM &2" + newVersion + " &ais available! Your version: &2" + CAM.getInstance().getDescription().getVersion() + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/" + pluginID + "/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"https://www.spigotmc.org/resources/" + pluginID + "/updates\"}}";
				Bukkit.getScheduler().runTask(CAM.getInstance(), () -> {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + sender.getName() + " " + message);
				});
			} else {
				CAM.getInstance().getLogger().info(ColourUtils.format("&aCAM &2" + newVersion + " &ais available! Your version: &2" + CAM.getInstance().getDescription().getVersion()));
				CAM.getInstance().getLogger().info(ColourUtils.format("&2https://www.spigotmc.org/resources/" + pluginID + "/"));
			}
		});
	}

}
