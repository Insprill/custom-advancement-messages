package net.insprill.cam.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;

public class CF {

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
		if (Dependency.VAULT.isEnabled() && Dependency.VAULT.getClazz() != null) {
			prefix = ((Chat) Dependency.VAULT.getClazz()).getPlayerPrefix(player);
			suffix = ((Chat) Dependency.VAULT.getClazz()).getPlayerSuffix(player);
		}
		if (player.getCustomName() != null) {
			msg = msg.replace("[playerDisplayName]", player.getCustomName());
		} else {
			msg = msg.replace("[playerDisplayName]", player.getDisplayName());
		}

		msg = msg.replace("[playerName]", player.getName());
		msg = msg.replace("[prefix]", prefix);
		msg = msg.replace("[suffix]", suffix);
		if (adv != null) {
			msg = msg.replace("[adv]", adv);
		}

		if (Dependency.PAPI.isEnabled()) {
			msg = PlaceholderAPI.setPlaceholders(player, msg);
		}

		return msg;
	}

}
