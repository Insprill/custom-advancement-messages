package net.insprill.cam.filemanagers;

import com.google.common.base.Charsets;
import net.insprill.cam.CAM;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

public class YamlManager {

    private JavaPlugin main;
    private String name;
    private File f;
    private YamlConfiguration cfg;

    public YamlManager(String name) {
        this(null, null, name);
    }

    public YamlManager(JavaPlugin main, File dir, String name) {
        if (main == null) main = JavaPlugin.getPlugin(CAM.class);
        if (dir == null) dir = main.getDataFolder();
        if (!dir.exists() || !dir.isDirectory()) dir.mkdirs();
        if (name == null) return;
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        this.main = main;
        f = new File(dir, this.name);
        if (!f.exists()) initFile();
        if (f.exists()) reload();
    }


    /**
     * Copies over all data from the default file stored in the jar, to the actual file in the plugins data folder.
     */
    private void initFile() {
        try {
            if (main.getResource(f.getName()) != null) {
                printToFile(main.getResource(f.getName()), f);
            } else {
                f.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the configuration file from disk.
     */
    public void reload() {
        if (!f.exists())
            initFile();
        cfg = YamlConfiguration.loadConfiguration(f);
        try (InputStream defConfigStream = main.getResource(f.getName())) {
            if (defConfigStream == null) return;
            try (InputStreamReader inputStreamReader = new InputStreamReader(defConfigStream, Charsets.UTF_8)) {
                cfg.setDefaults(YamlConfiguration.loadConfiguration(inputStreamReader));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the configuration file to disk.
     */
    public void save() {
        try {
            cfg.save(f);
            reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes data from an InputStream to a file.
     *
     * @param is      InputStream to get data from.
     * @param toPrint File to write data to.
     */
    private void printToFile(InputStream is, File toPrint) {
        try (FileOutputStream outputStream = new FileOutputStream(toPrint)) {
            int i;
            StringBuilder fullMessage = new StringBuilder();
            if (is == null) return;
            while ((i = is.read()) != -1) {
                fullMessage.append((char) i);
            }
            byte[] strToBytes = fullMessage.toString().getBytes();
            outputStream.write(strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return cfg;
    }

    public int getInt(String path, int def) {
        return (cfg.getInt(path, def));
    }

    public boolean getBoolean(String path, boolean def) {
        return (cfg.getBoolean(path, def));
    }

    public String getString(String path) {
        return (cfg.getString(path));
    }

    public String getString(String path, String def) {
        return (cfg.getString(path, def));
    }

    public List<String> getStringList(String path) {
        return cfg.getStringList(path);
    }

    public void set(String path, Object value) {
        cfg.set(path, value);
    }

}