package net.insprill.cam.commands.cam;

import net.insprill.cam.handlers.AdvancementHandler;
import net.insprill.cam.utils.files.YamlFile;
import net.insprill.xenlib.commands.ICommandArgument;
import net.insprill.xenlib.files.YamlFolder;
import net.insprill.xenlib.localization.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CamArgReload implements ICommandArgument {

	@Override
	public String getBaseArg() {
		return "reload";
	}

	@Override
	public String getDescription() {
		return "Reloads all configuration files";
	}

	@Override
	public @Nullable String getPermission() {
		return "cam.command.reload";
	}

	@Override
	public void process(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		YamlFile.CONFIG.reload();
		YamlFile.ADV_MESSAGES.reload();
		YamlFile.DATA.reload();
		YamlFolder.LOCALE.reload();

		AdvancementHandler.getInstance().init();

		Lang.send(sender, "commands.reload.success");
	}

	@Override
	public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}

}
