package net.insprill.cam.utils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.insprill.cam.CAM;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Debug {

    private static final Debug instance = new Debug();

    public static Debug getInstance() {
        return instance;
    }

    public String createDebugLink() {
        StringBuilder builder = new StringBuilder();

        builder.append("CAM: ").append(CAM.getInstance().getDescription().getVersion()).append("\n");
        builder.append("Server: ").append(Bukkit.getVersion()).append("\n");
        builder.append("API: ").append(Bukkit.getBukkitVersion()).append("\n");
        builder.append("Java: ").append(System.getProperty("java.version")).append("\n");
        builder.append("OS: ").append(System.getProperty("os.name")).append("\n");

        builder.append("Plugins:").append("\n");

        List<Plugin> plugins = new ArrayList<>(Arrays.asList(Bukkit.getPluginManager().getPlugins()));
        plugins.sort(Comparator.comparing(Plugin::getName));
        for (Plugin pl : plugins) {
            builder
                    .append("    ")
                    .append(pl.getName()).append(":").append("\n")
                    .append("        Version: ").append(pl.getDescription().getVersion()).append("\n")
                    .append("        Enabled: ").append(pl.isEnabled()).append("\n");
        }

        try {
            URL url = new URL("https://paste.insprill.net/documents");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            http.setConnectTimeout(10 * 1000);
            http.setReadTimeout(10 * 1000);
            http.setDoOutput(true);
            http.getOutputStream().write(builder.toString().getBytes(Charsets.UTF_8));
            JsonObject object = new Gson().fromJson(new InputStreamReader(http.getInputStream(), Charsets.UTF_8), JsonObject.class);
            return "https://paste.insprill.net/" + object.get("key").getAsString() + ".yaml";
        } catch (IOException ex) {
            return "&cCould not create debug link! Error: " + ex.getMessage();
        }
    }

}
