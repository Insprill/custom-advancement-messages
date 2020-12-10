package net.insprill.cam.commands;

import net.insprill.cam.CAM;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tabcomplete implements TabCompleter {

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] commandArgs) {
        List<String> args = new ArrayList<>();

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
            if (CAM.getInstance().dataFile != null)
                if (sender.hasPermission("cam.command.revoke")) {
                    args.add("revoke");
                }
            if (sender.hasPermission("cam.command.version")) {
                args.add("version");
            }
            if (sender.hasPermission("cam.command.debug")) {
                args.add("debug");
            }
            return match(args, commandArgs[0]);

        }
        else if (commandArgs.length == 2) {
            if (commandArgs[0].equalsIgnoreCase("set")) {
                if (sender.hasPermission("cam.command.set")) {
                    Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
                    while (advancementIterator.hasNext()) {
                        Advancement advancement = advancementIterator.next();
                        args.add(advancement.getKey().toString());
                    }
                }
            }
            else if (commandArgs[0].equalsIgnoreCase("revoke")) {
                if (CAM.getInstance().dataFile != null) {
                    if (sender.hasPermission("cam.command.revoke")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            args.add((player.getName()));
                        }
                    }
                }
            }
            return match(args, commandArgs[1]);
        }
        else if (commandArgs.length == 3) {
            if (commandArgs[0].equalsIgnoreCase("revoke")) {
                if (CAM.getInstance().dataFile != null) {
                    if (sender.hasPermission("cam.command.revoke")) {
                        args.add("everything");
                        Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
                        while (advancementIterator.hasNext()) {
                            Advancement advancement = advancementIterator.next();
                            args.add(advancement.getKey().toString());
                        }
                    }
                }
            }
            return match(args, commandArgs[2]);
        }

        return Collections.emptyList();
    }

    List<String> match(List<String> Args, String arg) {
        List<String> finalOne = new ArrayList<>();
        for (String s : Args) {
            if (!s.toLowerCase().startsWith(arg.toLowerCase())) continue;
            finalOne.add(s);
        }
        return finalOne;
    }

}