package net.insprill.cam;

import net.insprill.cam.commands.Commands;
import net.insprill.cam.filemanagers.YamlManager;
import net.insprill.cam.listeners.AdvancementEvent;
import net.insprill.cam.metrics.Metrics;
import net.insprill.cam.utils.CF;
import net.insprill.cam.utils.JvmChecker;
import net.insprill.cam.utils.StopWatch;
import net.insprill.cam.utils.UpdateChecker;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class CAM extends JavaPlugin {

    static CAM instance;
    final Metrics metrics = new Metrics(this, 0);
    public int minecraftVersion = 0;
    public YamlManager advancementsFile;
    public YamlManager langFile;
    public YamlManager configFile;
    public Chat chat = null;
    public boolean hasVault = false;
    public boolean hasPapi = false;

    public static CAM getInstance() {
        return instance;
    }

    //TODO CHANGE METRICS ID BEFORE COMPILING

    @Override
    public void onEnable() {

        instance = this;
        StopWatch startingPluginTimer = new StopWatch();
        startingPluginTimer.start();

        advancementsFile = new YamlManager("advancementMessages.yml");
        langFile = new YamlManager("lang.yml");
        configFile = new YamlManager("config.yml");

        String a = Bukkit.getServer().getClass().getPackage().getName();
        String mcv = a.substring(a.lastIndexOf('.') + 2);
        mcv = mcv.substring(0, mcv.indexOf('R') - 1);
        minecraftVersion = Integer.parseInt(mcv.replace("_", ""));

        if (minecraftVersion < 1_12) {
            CF.sendConsoleMessage("&cCAM is only compatible with Minecraft 1.12+. Please upgrade to at least 1.12 to use CAM.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (JvmChecker.getJvmVersion() < 8) {
            CF.sendConsoleMessage("&cCAM is only compatible with Java 8 and up. Please upgrade to Java 8 or better yet, Java 11 to use CAM.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        String serverVersion = Bukkit.getVersion().toLowerCase();
        if (serverVersion.contains("spigot"))
            JvmChecker.checkJvm();

        hasVault = Bukkit.getPluginManager().isPluginEnabled("Vault");
        hasPapi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (hasVault)
            setupChat();

        StopWatch initializeAdvancementsTimer = new StopWatch();
        initializeAdvancementsTimer.start();
        initializeAdvancements();
        initializeAdvancementsTimer.stop();
        CF.sendConsoleMessage("&3Initialized advancements! &6" + initializeAdvancementsTimer.getElapsedTime().toMillis() + "ms");

        new AdvancementEvent(this);
        new Commands(this);
        startingPluginTimer.stop();
        CF.sendConsoleMessage("&3CAM Started! &6" + startingPluginTimer.getElapsedTime().toMillis() + "ms");
        UpdateChecker.getInstance().sendUpdateMessage(Bukkit.getConsoleSender());

        // bStats
        String radius_messages = (configFile.getBoolean("Radius.Enabled", false)) ? "Enabled" : "Disabled";
        metrics.addCustomChart(new Metrics.SimplePie("radius_messages", () -> radius_messages));
    }

    @Override
    public void onDisable() {

    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null)
            chat = chatProvider.getProvider();
    }

    @SuppressWarnings("deprecation")
    void initializeAdvancements() {
        for (World world : getServer().getWorlds()) {
            if (Bukkit.getVersion().contains("1.12"))
                world.setGameRuleValue("ANNOUNCE_ADVANCEMENTS", "false");
            else
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }
        Iterator<Advancement> advancementIterator = getServer().advancementIterator();
        while (advancementIterator.hasNext()) {
            Advancement advancement = advancementIterator.next();
            String key = CF.formatKey(advancement);
            if (!advancementsFile.getConfig().contains(key))
                advancementsFile.set(key, "default");
        }
        advancementsFile.save();
    }

    public void reload() {
        advancementsFile.reload();
        initializeAdvancements();
    }

}
