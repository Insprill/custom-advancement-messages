package net.insprill.cam.utils;

import net.insprill.cam.CAM;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    private static final UpdateChecker instance = new UpdateChecker();
    public String newVersion;
    public String currentVersion;
    private final String pluginID = "86618";

    public static UpdateChecker getInstance() {
        return instance;
    }

    public boolean checkForUpdates() {
        if (!CAM.getInstance().getConfigFile().getBoolean("DisableUpdateChecker", false)) {
            currentVersion = CAM.getInstance().getDescription().getVersion();
            if (currentVersion.contains("BETA"))
                return false;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new URL("https://api.insprill.net/v1/minecraft/spigot/plugins/cam/version/")
                                    .openConnection()
                                    .getInputStream()))) {
                newVersion = br.readLine();
                if (!newVersion.equals(currentVersion)) return true;
            } catch (Exception e) {
                CF.sendConsoleMessage("&cCould not check for updates! Error: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public void sendUpdateMessage(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(CAM.getInstance(), () -> {
            if (UpdateChecker.getInstance().checkForUpdates()) {
                if (sender instanceof Player) {
                    String message = "{\"text\":\"&2CAM &2" + newVersion + " &ais available! Your version: &2" + CAM.getInstance().getDescription().getVersion() + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/" + pluginID + "/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"https://www.spigotmc.org/resources/" + pluginID + "/updates\"}}";
                    Bukkit.getScheduler().runTask(CAM.getInstance(), () -> CF.sendJsonMessage(((Player) sender), message));
                }
                else {
                    CF.sendConsoleMessage("&aCAM &2" + newVersion + " &ais available! Your version: &2" + CAM.getInstance().getDescription().getVersion());
                    CF.sendConsoleMessage("&2https://www.spigotmc.org/resources/" + pluginID + "/");
                }
            }
        });
    }
}