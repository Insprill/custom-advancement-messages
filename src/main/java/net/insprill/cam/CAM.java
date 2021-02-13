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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CAM extends JavaPlugin {

    private static CAM instance;
    private final Metrics metrics = new Metrics(this, 9613);
    private int minecraftVersion = 0;
    private YamlManager advancementsFile;
    private YamlManager langFile;
    private YamlManager configFile;
    private YamlManager dataFile;
    private Chat chat = null;
    public boolean hasVault = false;
    public boolean hasPapi = false;
    private ExecutorService advancementProcessor;

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
        if (configFile.getBoolean("Store-Completed-Advancements.Enabled", false))
            dataFile = new YamlManager("data.yml");

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

        advancementProcessor = Executors.newSingleThreadExecutor();

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
        advancementProcessor.shutdown();
        try {
            if (!advancementProcessor.awaitTermination(5, TimeUnit.SECONDS))
                advancementProcessor.shutdownNow();
        } catch (InterruptedException ie) {
            advancementProcessor.shutdownNow();
        }
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
            if (key.contains("root") || key.contains("recipes"))
                continue; // Skip if the advancements key contains 'root' or 'recipes'.
            if (!advancementsFile.getConfig().contains(key))
                advancementsFile.set(key, "default");
        }
        advancementsFile.save();
    }

    public void reload() {
        configFile.reload();
        langFile.reload();
        advancementsFile.reload();
        if (configFile.getBoolean("Store-Completed-Advancements", false)) {
            if (dataFile == null)
                dataFile = new YamlManager("data.yml");
            else
                dataFile.reload();
        } else {
            dataFile = null;
        }
        initializeAdvancements();
    }

    public YamlManager getConfigFile() {
        return configFile;
    }

    public YamlManager getLangFile() {
        return langFile;
    }

    public YamlManager getAdvancementsFile() {
        return advancementsFile;
    }

    public YamlManager getDataFile() {
        return dataFile;
    }

    public void initDataFile() {
        dataFile = new YamlManager("data.yml");
    }

    public ExecutorService getAdvancementProcessor() {
        return advancementProcessor;
    }

    public Chat getChat() {
        return chat;
    }

    public int getMinecraftVersion() {
        return minecraftVersion;
    }

}
