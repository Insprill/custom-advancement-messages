package net.insprill.cam.commands.cam;

import net.insprill.cam.handlers.AdvancementHandler;
import net.insprill.cam.utils.files.YamlFile;
import net.insprill.xenlib.commands.ICommandArgument;
import net.insprill.xenlib.localization.Lang;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CamArgSet implements ICommandArgument {

	@Override
	public String getBaseArg() {
		return "set";
	}

	@Override
	public String getDescription() {
		return "Sets the message for an advancement";
	}

	@Override
	public @Nullable String getPermission() {
		return "cam.command.set";
	}

	@Override
	public void process(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			Lang.send(sender, "commands.no-advancement");
			return;
		}
		if (args.length == 2) {
			Lang.send(sender, "commands.set.no-message");
			return;
		}

		if (YamlFile.ADV_MESSAGES.contains(AdvancementHandler.formatKey(args[1]))) {
			String message = StringUtils.join(args, " ", 2, args.length);
			YamlFile.ADV_MESSAGES.set(AdvancementHandler.formatKey(args[1]), message);
			YamlFile.ADV_MESSAGES.save();
			Lang.send(sender, "commands.set.success");
		} else {
			Lang.send(sender, "commands.set.advancement-not-found");
		}
	}

	@Override
	public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		if (args.length != 2)
			return null;

		List<String> advancements = new ArrayList<>();
		Iterator<Advancement> iter = Bukkit.getServer().advancementIterator();
		while (iter.hasNext()) {
			advancements.add(iter.next().getKey().toString());
		}
		return advancements;
	}

}
