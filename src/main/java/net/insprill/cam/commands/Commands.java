package net.insprill.cam.commands;

import net.insprill.cam.CAM;
import net.insprill.cam.utils.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Commands implements CommandExecutor {

    private final CAM plugin;

    public Commands(CAM plugin) {
        this.plugin = plugin;
        plugin.getCommand("cam").setExecutor(this);
        plugin.getCommand("cam").setTabCompleter(new Tabcomplete());
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        if (args.length == 0) {
            CF.sendMessage(sender, "&eYou are running version &a" + plugin.getDescription().getVersion());
            CF.sendMessage(sender, "&eFor a list of commands, type /cam help");
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("cam.command.help")) {
                sender.sendMessage(CF.consoleFormat(Lang.get("No-Permission")));
                return true;
            }
            String helpPage1 = "&e&l========< &c&lCAM Help &e&l>========" + "\n" +
                    "&a&l/cam help &7-> &2Opens help page" + "\n" +
                    "&a&l/cam reload &7-> &2Reloads all config files." + "\n" +
                    "&a&l/cam revoke <player> <advancement> &7-> &2Removes advancement for player from data file if enabled." + "\n" +
                    "&a&l/cam version &7-> &2Shows versions for various things." + "\n" +
                    "&a&l/cam debug &7-> &2Create debug link. Only used for for support if you have any issues." + "\n" +
                    "&e&l==========================";
            if (args.length == 1) {
                sender.sendMessage(CF.format(helpPage1));
            }
            else {
                if (args[1].equalsIgnoreCase("1"))
                    sender.sendMessage(CF.format(helpPage1));
                else
                    CF.sendMessage(sender, "&cWhoops! This page doesn't exist!");
            }
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("cam.command.reload")) {
                sender.sendMessage(CF.consoleFormat(Lang.get("No-Permission")));
                return true;
            }
            StopWatch reloadPluginTimer = new StopWatch();
            reloadPluginTimer.start();
            plugin.reload();
            reloadPluginTimer.stop();
            CF.sendMessage(sender, "&aPlugin Successfully Reloaded! &eTime taken: &6" + reloadPluginTimer.getElapsedTime().toMillis() + " &ems");

        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("cam.command.set")) {
                sender.sendMessage(CF.consoleFormat(Lang.get("No-Permission")));
                return true;
            }

            if (args.length == 1) {
                CF.sendMessage(sender, "&cPlease specify an advancement & a message.");
                return true;
            }
            if (args.length == 2) {
                CF.sendMessage(sender, "&cPlease specify a message.");
                return true;
            }

            if (plugin.advancementsFile.getConfig().contains(CF.formatKey(args[1]))) {
                String message = StringUtils.join(args, " ", 2, args.length);
                plugin.advancementsFile.set(CF.formatKey(args[1]), message);
                plugin.advancementsFile.save();
                CF.sendMessage(sender, "&aAdvancement successfully set!");
            }
            else {
                CF.sendMessage(sender, Lang.get("Advancement-Not-Found"));
            }

        }
        else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
            if (!sender.hasPermission("cam.command.version")) {
                sender.sendMessage(CF.consoleFormat(Lang.get("No-Permission")));
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(CF.consoleFormat("&e&l==============================="));
                sender.sendMessage(CF.consoleFormat("&2CAM: &a" + plugin.getDescription().getVersion()));
                sender.sendMessage(CF.consoleFormat("&2Server: &a" + Bukkit.getVersion()));

                if (plugin.hasVault)
                    sender.sendMessage(CF.consoleFormat("&2Vault Version: &a" + Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion()));
                else
                    sender.sendMessage(CF.consoleFormat("&2Vault Version: &a" + "N/A"));

                if (plugin.hasPapi)
                    sender.sendMessage(CF.consoleFormat("&2PAPI Version: &a" + Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion()));
                else
                    sender.sendMessage(CF.consoleFormat("&2PAPI Version: &a" + "N/A"));

                sender.sendMessage(CF.consoleFormat("&2Java: &a" + System.getProperty("java.version")));
                sender.sendMessage(CF.consoleFormat("&2OS: &a" + System.getProperty("os.name")));
                sender.sendMessage(CF.consoleFormat("&e&l==============================="));

                UpdateChecker.getInstance().sendUpdateMessage(sender);

            }

        }
        else if (args[0].equalsIgnoreCase("debug")) {
            if (!sender.hasPermission("cam.command.debug")) {
                sender.sendMessage(CF.consoleFormat(Lang.get("No-Permission")));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CAM.getInstance(), () -> {
                CF.sendMessage(sender, "&2Creating debug link, please wait...");
                CF.sendMessage(sender, "&a" + Debug.getInstance().createDebugLink());
            });

        }
        else if (args[0].equalsIgnoreCase("revoke")) {
            if (!sender.hasPermission("cam.command.revoke")) {
                sender.sendMessage(CF.consoleFormat(Lang.get("No-Permission")));
                return true;
            }
            if (plugin.dataFile == null) return true;
            if (args.length == 1) {
                CF.sendMessage(sender, "&cPlease specify a player & an advancement.");
                return true;
            }
            if (args.length == 2) {
                CF.sendMessage(sender, "&cPlease specify an advancement.");
                return true;
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
            if (op == null || !op.hasPlayedBefore()) {
                CF.sendMessage(sender, Lang.get("Player-Not-Found"));
                return true;
            }
            String uuid = op.getUniqueId().toString();
            if (args[2].equalsIgnoreCase("everything")) {
                plugin.dataFile.set(uuid, null);
            }
            else {
                List<String> advancements = plugin.dataFile.getStringList(uuid);
                if (!advancements.contains(args[2])) {
                    CF.sendMessage(sender, "&c" + op.getName() + " does not have that advancement!");
                    return true;
                }
                advancements.remove(args[2]);
                plugin.dataFile.set(uuid, advancements);
            }
            plugin.dataFile.save();
            CF.sendMessage(sender, "&aRemoved " + args[2] + " from " + op.getName() + "!");
        }

        // Doesn't match any commands.
        else {
            CF.sendMessage(sender, Lang.get("Unknown-Command"));
        }
        return true;
    }
}
