package net.insprill.cam.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.insprill.cam.CAM;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CF {
    private static final Pattern pattern = Pattern.compile("\\{#[a-fA-F0-9]{6}}");

    /**
     * Replace color codes with actual colors, and if on 1.16+ hex codes codes.
     *
     * @param string String to insert color codes on.
     * @return String with colors.
     */
    public static String format(String string) {
        if (string == null || string.isEmpty()) return string;
        string = ChatColor.translateAlternateColorCodes('&', string);
        if (Bukkit.getVersion().contains("1.16")) {
            Matcher match = pattern.matcher(string);
            while (match.find()) {
                String hex = string.substring(match.start(), match.end());
                string = StringUtils.replace(string, hex, "" + ChatColor.of(hex.replace("{", "").replace("}", "")));
                match = pattern.matcher(string);
            }
            if (string.startsWith("<center>")) {
                string = StringUtils.replace(string, "<center>", "");
                string = CenteredMessages.centerMessage(string);
            }
            return string;
        }
        else {
            return (consoleFormat(string));
        }
    }

    /**
     * Replace color codes with actual colors, and removes hex codes codes.
     *
     * @param string string to modify.
     * @return Modified string.
     */
    public static String consoleFormat(String string) {
        if (string == null || string.isEmpty()) return string;
        Matcher match = pattern.matcher(string);
        while (match.find()) {
            String hex = string.substring(match.start(), match.end());
            string = StringUtils.replace(string, hex, "");
            match = pattern.matcher(string);
        }
        if (string.startsWith("<center>")) {
            string = StringUtils.replace(string, "<center>", "");
            string = CenteredMessages.centerMessage(string);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Format, then print a message to console.
     *
     * @param message Message to format and send.
     */
    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(consoleFormat(Lang.get("Prefix") + message));
    }

    /**
     * Sends message.
     *
     * @param sender  Who to send the message to.
     * @param message Message to format and send.
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player)
            sender.sendMessage(format(Lang.get("Prefix") + message));
        else if (sender instanceof ConsoleCommandSender)
            sender.sendMessage(consoleFormat(Lang.get("Prefix") + message));
    }

    /**
     * Set placeholder in a String.
     *
     * @param player Player to set placeholders for.
     * @param msg    String to insert placeholders into.
     * @return String with placeholder set.
     */
    public static String setPlaceholders(Player player, String msg, String adv) {

        String prefix = "";
        String suffix = "";
        if ((CAM.getInstance().hasVault)
                && (CAM.getInstance().chat != null)) {
            prefix = CAM.getInstance().chat.getPlayerPrefix(player);
            suffix = CAM.getInstance().chat.getPlayerSuffix(player);
        }
        if (player.getCustomName() != null)
            msg = StringUtils.replace(msg, "[playerDisplayName]", player.getCustomName());
        else
            msg = StringUtils.replace(msg, "[playerDisplayName]", player.getDisplayName());

        msg = StringUtils.replace(msg, "[playerName]", player.getName());
        msg = StringUtils.replace(msg, "[prefix]", prefix);
        msg = StringUtils.replace(msg, "[suffix]", suffix);
        if (adv != null)
            msg = StringUtils.replace(msg, "[adv]", adv);

        if (CAM.getInstance().hasPapi)
            msg = PlaceholderAPI.setPlaceholders(player, msg);

        return msg;
    }

    /**
     * Sends a /tellraw format JSON message.
     *
     * @param player  Player to send message to.
     * @param message Message to send.
     */
    public static void sendJsonMessage(Player player, String message) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " " + CF.format(message));
    }

    /**
     * @param key Format key into config-friendly format.
     * @return Key in a format that can be put in a config file.
     */
    public static String formatKey(Keyed key) {
        return formatKey(key.getKey().toString());
    }

    /**
     * @param key Format string-tuned-key into config-friendly format.
     * @return String that can be put in a config file.
     */
    public static String formatKey(String key) {
        key = StringUtils.replace(key, "/", ".");
        key = StringUtils.replace(key, ":", ".");
        return key;
    }
}
