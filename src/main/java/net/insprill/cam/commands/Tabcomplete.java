package net.insprill.cam.commands;

import net.insprill.cam.CAM;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tabcomplete implements TabCompleter {

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] commandArgs) {
        List<String> args = new ArrayList<>();

        // No args
        if (commandArgs.length == 1) {
            if (sender.hasPermission("cam.command.help")) {
                args.add("help");
            }
            if (sender.hasPermission("cam.command.reload")) {
                args.add("reload");
            }
            if (sender.hasPermission("cam.command.set")) {
                args.add("set");
            }
            if (sender.hasPermission("cam.command.revoke") && CAM.getInstance().getDataFile() != null) {
                args.add("revoke");
            }
            if (sender.hasPermission("cam.command.version")) {
                args.add("version");
            }
            if (sender.hasPermission("cam.command.debug")) {
                args.add("debug");
            }
            return StringUtil.copyPartialMatches(commandArgs[0], new ArrayList<>(), args);
        }

        // At least 1 arg.
        switch (commandArgs[0].toLowerCase()) {
            case "set": {
                if (sender.hasPermission("cam.command.set")) {
                    Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
                    while (advancementIterator.hasNext()) {
                        Advancement advancement = advancementIterator.next();
                        args.add(advancement.getKey().toString());
                    }
                }
                break;
            }

            case "revoke": {
                if (sender.hasPermission("cam.command.revoke") && CAM.getInstance().getDataFile() != null) {
                    if (commandArgs.length == 2) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            args.add(player.getName());
                        }
                    } else if (commandArgs.length == 3) {
                        args.add("everything");
                        Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
                        while (advancementIterator.hasNext()) {
                            Advancement advancement = advancementIterator.next();
                            if (CAM.getInstance().getConfigFile().getBoolean("Store-Completed-Advancements.Only-Custom", true))
                                if (advancement.getKey().toString().startsWith("minecraft:"))
                                    continue;
                            args.add(advancement.getKey().toString());
                        }
                    }
                }
                break;
            }

        }

        int argToMatch = (commandArgs.length == 0) ? 0 : commandArgs.length - 1;
        StringUtil.copyPartialMatches(commandArgs[argToMatch], args, new ArrayList<>());
        Collections.sort(args);

        return args;
    }

}
