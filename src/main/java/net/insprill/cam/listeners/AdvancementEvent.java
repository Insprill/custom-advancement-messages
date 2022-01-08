package net.insprill.cam.listeners;

import net.insprill.cam.CAM;
import net.insprill.cam.handlers.AdvancementHandler;
import net.insprill.cam.utils.CF;
import net.insprill.cam.utils.files.YamlFile;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AdvancementEvent implements Listener {

	public AdvancementEvent(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin); // Register PlayerAdvancementDoneEvent event.
	}

	@EventHandler(priority = EventPriority.MONITOR) //todo: rewrite all of this
	public void onAdvancement(PlayerAdvancementDoneEvent e) {
		Player player = e.getPlayer(); // Looks prettier then e.getPlayer() a bunch of times.

		// Check if player is in a disabled world.
		for (String worldName : YamlFile.CONFIG.getStringList("Disabled-Worlds")) {
			if ((worldName.startsWith("[regex]")) &&
					(player.getWorld().getName().matches(worldName.replace("[regex]", ""))))
				return;
			if (worldName.equals(player.getWorld().getName()))
				return;
		}

		// Check if player is in a disabled gamemode.
		for (String gameModeString : YamlFile.CONFIG.getStringList("Disabled-Gamemodes")) {
			GameMode gameMode = GameMode.valueOf(gameModeString.toUpperCase());
			if (gameMode == null) {
				CAM.getInstance().getLogger().severe("&4" + gameMode + " &cis not a valid gamemode!");
				break;
			}
			if (player.getGameMode() == gameMode)
				return;
		}

		Advancement advancement = e.getAdvancement();
		String advKey = advancement.getKey().toString();

		if (advKey.contains("root") || advKey.contains("recipes"))
			return; // Return if the advancements key contains 'root' or 'recipes'.
		if (YamlFile.CONFIG.getStringList("Disabled-Advancements").contains(advKey))
			return; // Return if the advancement is disabled.
		List<String> criteria = new ArrayList<>(advancement.getCriteria()); // List of all criteria for advancement.
		if (criteria.isEmpty()) return; // If the advancement has no criteria, return;
		AdvancementProgress ap = player.getAdvancementProgress(advancement); // Get players advancement progress for the advancement they got.
		if (ap == null) return; // if the progress is null, return.
		if (ap.getDateAwarded(criteria.get(criteria.size() - 1)) != null) { // If we can get the date the last criteria was awarded.
			if (ap.getDateAwarded(criteria.get(criteria.size() - 1)).getTime() < System.currentTimeMillis() - 5 * 1000) { // If the last criteria was awarded more then 5 second ago, return;
				return;
			}
		}

		// If store completed advancements is enabled.
		if (YamlFile.CONFIG.getBoolean("Store-Completed-Advancements.Enabled", true)) {
			if (!YamlFile.CONFIG.getBoolean("Store-Completed-Advancements.Only-Custom", true)
					|| !advKey.startsWith("minecraft:")) { // If SCA is enabled and only custom is true, break out of this.
				String uuid = player.getUniqueId().toString();
				List<String> advancements = YamlFile.DATA.getStringList(uuid);
				if (advancements.contains(advKey)) // If the player got this advancement already, return.
					return;
				advancements.add(advKey); // Add advancement to list of ones they have.
				YamlFile.DATA.set(uuid, advancements);
				YamlFile.DATA.save();
			}
		}

		String advName = null;
		String message = YamlFile.ADV_MESSAGES.getString(AdvancementHandler.formatKey(advancement), "none"); // Message string we modify.
		if (message.equals("none")) return; // Return if the message is set to 'none'.

		if (message.contains("-{{") && message.endsWith("}}")) { // Check if string contains custom name.
			advName = message.substring(StringUtils.indexOf(message, "-{{") + 3, StringUtils.lastIndexOf(message, "}}")); // Create substring, getting just the custom name.
			message = message.substring(0, StringUtils.indexOf(message, "-{{")); // Don't include custom name section is actual message.
		}

		if (message.startsWith("custom.")) // If we should use a custom 'default'
			message = YamlFile.ADV_MESSAGES.getString(message, "default"); // Try to get the custom default, but get the normal one if it doesn't exist.
		if (message.equals("default")) // If the message is still 'default', get the actual default message.
			message = YamlFile.ADV_MESSAGES.getString("default", "&2[playerName] &ahas gotten the advancement &2[adv]&a!");

		if (advName == null) { // If no custom name is specified.
			advName = advKey; // Advancement name from key.
			advName = advName.substring(advName.lastIndexOf('/') + 1); // Get the lowest key. That's the advancements name
			advName = StringUtils.replace(advName, "_", " "); // Replace the '_' in the name with a space.
			advName = WordUtils.capitalizeFully(advName); // Capitalize the first letter in each work and make all others lowercase.
		}

		message = CF.setPlaceholders(player, message, advName); // Set placeholders.

		if (YamlFile.CONFIG.getBoolean("Radius.Enabled", false)) { // If radius messages are enabled.
			for (Player p : getNearbyPlayers(player, YamlFile.CONFIG.getDouble("Radius.Range"))) { // For all players close to player who got advancement.
				sendMessage(p, message); // Send the message.
			}
		} else { // If radius messages are NOT enabled.
			for (Player p : Bukkit.getOnlinePlayers()) { // For all players on the server.
				sendMessage(p, message); // Send the message.
			}
		}
		if (YamlFile.CONFIG.getBoolean("Send-Message-To-Console", true)) {
			Bukkit.getConsoleSender().sendMessage(CF.setPlaceholders(player, YamlFile.ADV_MESSAGES.getString("default"), advName));
		}
	}

	private void sendMessage(Player player, String message) {
		if (message.contains("{\"text\":")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " " + message); // If it contains "{\"text\":", it's JSON. send it as so.
		} else {
			player.sendMessage(message); // Not JSON. just format & send the message.
		}
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
