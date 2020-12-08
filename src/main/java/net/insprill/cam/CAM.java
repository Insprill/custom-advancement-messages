package net.insprill.cam;

import net.insprill.cam.commands.Commands;
import net.insprill.cam.filemanagers.YamlManager;
import net.insprill.cam.listeners.AdvancementEvent;
import net.insprill.cam.metrics.Metrics;
import net.insprill.cam.utils.CF;
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
    }

    @Override
    public void onDisable() {

    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null)
            chat = chatProvider.getProvider();
    }

    void initializeAdvancements() {
        for (World world : getServer().getWorlds())
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
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
