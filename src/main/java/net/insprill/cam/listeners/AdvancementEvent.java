package net.insprill.cam.listeners;

import net.insprill.cam.CAM;
import net.insprill.cam.utils.CF;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.ArrayList;
import java.util.List;

public class AdvancementEvent implements Listener {

    private final CAM plugin;

    public AdvancementEvent(CAM plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin); // Register PlayerAdvancementDoneEvent event.
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        Player player = e.getPlayer(); // Looks prettier then e.getPlayer() a bunch of times.
        if (player.getAdvancementProgress(e.getAdvancement()).isDone()) return;
        String message = plugin.advancementsFile.getString(CF.formatKey(e.getAdvancement())); // Message string we modify.
        if (message.equals("default"))
            message = plugin.advancementsFile.getString("default"); // If it's default, use the default message.

        String advName = e.getAdvancement().getKey().toString(); // Advancement name from key.
        advName = advName.substring(advName.lastIndexOf('/')); // Only get everything after the last '/'.
        advName = StringUtils.replace(advName, "/", ""); // Remove the '/'.
        advName = StringUtils.replace(advName, "_", " "); // Replace the '_' in the "name" with a space.
        advName = WordUtils.capitalizeFully(advName); // Capitalize the first letter in each work and make all others lowercase.
        message = CF.setPlaceholders(player, message, advName); // Set placeholders.
        if (plugin.configFile.getBoolean("Radius.Enabled", false)) { // If radius messages are enabled.
            for (Player p : getNearbyPlayers(player, plugin.configFile.getConfig().getDouble("Radius.Range"))) { // For all players close to player who got advancement.
                sendMessage(p, message); // Send the message.
            }
        }
        else { // If radius messages are NOT enabled.
            for (Player p : Bukkit.getOnlinePlayers()) { // For all players on the server.
                sendMessage(p, message); // Send the message.
            }
        }
    }

    void sendMessage(Player player, String message) {
        if (message.contains("{\"text\":"))
            CF.sendJsonMessage(player, message); // If it contains "{\"text\":", it's JSON. send it as so.
        else
            player.sendMessage(CF.format(message)); // Not JSON. just format & send the message.
    }

    /**
     * Gets all players near to the target player.
     *
     * @param targetPlayer Player to check for nearby players,
     * @param range        Range in blocks to check.
     * @return A list of all nearby players.
     */
    public List<Player> getNearbyPlayers(Player targetPlayer, double range) {
        List<Player> nearbyPlayers = new ArrayList<>();
        List<Entity> entities = targetPlayer.getNearbyEntities(range, range, range); // Get list of all nearby entities.
        for (Entity entity : entities) { // For each entity.
            if (entity instanceof Player) { // If the entity is a player, add them to the list.
                nearbyPlayers.add((Player) entity);
            }
        }
        return nearbyPlayers; // Return the list of players.
    }

}
