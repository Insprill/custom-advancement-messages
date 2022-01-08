package net.insprill.cam.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.apache.commons.lang.StringUtils;
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
			msg = StringUtils.replace(msg, "[playerDisplayName]", player.getCustomName());
		} else {
			msg = StringUtils.replace(msg, "[playerDisplayName]", player.getDisplayName());
		}

		msg = StringUtils.replace(msg, "[playerName]", player.getName());
		msg = StringUtils.replace(msg, "[prefix]", prefix);
		msg = StringUtils.replace(msg, "[suffix]", suffix);
		if (adv != null) {
			msg = StringUtils.replace(msg, "[adv]", adv);
		}

		if (Dependency.PAPI.isEnabled()) {
			msg = PlaceholderAPI.setPlaceholders(player, msg);
		}

		return msg;
	}

}
