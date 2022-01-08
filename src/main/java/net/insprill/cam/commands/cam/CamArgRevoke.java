package net.insprill.cam.commands.cam;

import net.insprill.cam.utils.files.YamlFile;
import net.insprill.xenlib.commands.ICommandArgument;
import net.insprill.xenlib.localization.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CamArgRevoke implements ICommandArgument {

	@Override
	public String getBaseArg() {
		return "revoke";
	}

	@Override
	public String getDescription() {
		return "Removes an advancement from a player in the local data file.";
	}

	@Override
	public @Nullable String getPermission() {
		return "cam.command.revoke";
	}

	@Override
	public void process(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			Lang.send(sender, "commands.revoke.no-player");
			return;
		}
		if (args.length == 2) {
			Lang.send(sender, "commands.no-advancement");
			return;
		}

		OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
		if (op == null || !op.hasPlayedBefore()) {
			Lang.send(sender, "commands.revoke.player-not-found", "%player%;" + args[1]);
			return;
		}

		String uuid = op.getUniqueId().toString();
		if (args[2].equalsIgnoreCase("everything")) {
			YamlFile.DATA.set(uuid, null);
		} else {
			List<String> advancements = YamlFile.DATA.getStringList(uuid);
			if (!advancements.contains(args[2])) {
				Lang.send(sender, "commands.revoke.doesnt-have");
				return;
			}
			advancements.remove(args[2]);
			YamlFile.DATA.set(uuid, advancements);
		}
		YamlFile.DATA.save();
		Lang.send(sender, "commands.revoke.success", "%adv%;" + args[2], "%player%;" + op.getName());
	}

	@Override
	public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		switch (args.length) {
			case 2 -> {
				return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
			}
			case 3 -> {
				List<String> returnArgs = new ArrayList<>();
				returnArgs.add("everything");
				Iterator<Advancement> iter = Bukkit.advancementIterator();
				while (iter.hasNext()) {
					Advancement advancement = iter.next();
					if (YamlFile.CONFIG.getBoolean("Store-Completed-Advancements.Only-Custom", true)) {
						if (advancement.getKey().toString().startsWith("minecraft:")) {
							continue;
						}
					}
					returnArgs.add(advancement.getKey().toString());
				}
				return returnArgs;
			}
		}
		return null;
	}

}
