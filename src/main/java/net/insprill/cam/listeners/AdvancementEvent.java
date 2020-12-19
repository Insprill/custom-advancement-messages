package net.insprill.cam.listeners;

import net.insprill.cam.CAM;
import net.insprill.cam.filemanagers.YamlManager;
import net.insprill.cam.utils.CF;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.AdvancementProgress;
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
        plugin.advancementProcessor.execute(() -> {
            String advKey = e.getAdvancement().getKey().toString();
            if (advKey.contains("root") || advKey.contains("recipes"))
                return; // Return if the advancements key contains 'root' or 'recipes'.
            if (plugin.configFile.getStringList("Disabled-Advancements").contains(advKey))
                return; // Return if the advancement is disabled.
            Player player = e.getPlayer(); // Looks prettier then e.getPlayer() a bunch of times.
            List<String> criteria = new ArrayList<>(e.getAdvancement().getCriteria()); // List of all criteria for advancement.
            if (criteria.isEmpty()) return; // If the advancement has no criteria, return;
            AdvancementProgress ap = player.getAdvancementProgress(e.getAdvancement()); // Get players advancement progress for the advancement they got.
            if (ap == null) return; // if the progress is null, return.
            if (ap.getDateAwarded(criteria.get(criteria.size() - 1)) != null) { // If we can get the date the last criteria was awarded.
                if (ap.getDateAwarded(criteria.get(criteria.size() - 1)).getTime() < System.currentTimeMillis() - 5 * 1000) { // If the last criteria was awarded more then 5 second ago, return;
                    return;
                }
            }
            String uuid = player.getUniqueId().toString();
            if (plugin.configFile.getBoolean("Store-Completed-Advancements.Enabled", true)) {
                if (plugin.dataFile == null)
                    plugin.dataFile = new YamlManager("data.yml");
                if (!plugin.configFile.getBoolean("Store-Completed-Advancements.Only-Custom", true)
                        || !advKey.startsWith("minecraft:")) {// If SCA is enabled and only custom is true, break out of this.
                    List<String> advancements = plugin.dataFile.getStringList(uuid);
                    if (advancements.contains(advKey)) // If the player got this advancement already, return.
                        return;
                    advancements.add(advKey); // Add advancement to list of ones they have.
                    plugin.dataFile.set(uuid, advancements);
                    plugin.dataFile.save();
                }
            }
            String advName = null;
            String message = plugin.advancementsFile.getString(CF.formatKey(e.getAdvancement()), "none"); // Message string we modify.
            if (message.equals("none")) return; // Return if the message is set to 'none'.

            if (message.contains("-{{") && message.endsWith("}}")) { // Check if string contains custom name.
                advName = message.substring(StringUtils.indexOf(message, "-{{") + 3, StringUtils.lastIndexOf(message, "}}")); // Create substring, getting just the custom name.
                advName = CF.format(advName);
                message = message.substring(0, StringUtils.indexOf(message, "-{{")); // Don't include custom name section is actual message.
            }

            if (message.startsWith("custom.")) // If we should use a custom 'default'
                message = plugin.advancementsFile.getString(message, "default"); // Try to get the custom default, but get the normal one if it doesn't exist.
            if (message.equals("default")) // If the message is still 'default', get the actual default message.
                message = plugin.advancementsFile.getString("default", "&2[playerName] &ahas gotten the advancement &2[adv]&a!");

            if (advName == null) { // If no custom name is specified.
                advName = advKey; // Advancement name from key.
                advName = advName.substring(advName.lastIndexOf('/') + 1); // Get the lowest key. That's the advancements name
                advName = StringUtils.replace(advName, "_", " "); // Replace the '_' in the name with a space.
                advName = WordUtils.capitalizeFully(advName); // Capitalize the first letter in each work and make all others lowercase.
            }

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
            if (plugin.configFile.getBoolean("Send-Message-To-Console", true)) {
                CF.sendConsoleMessage(CF.setPlaceholders(player, plugin.advancementsFile.getString("default"), advName)); // Send default message to console if it's enabled.
            }
        });
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
