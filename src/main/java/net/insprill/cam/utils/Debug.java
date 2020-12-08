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
        for (Plugin pl : Bukkit.getPluginManager().getPlugins())
            builder
                    .append("    ")
                    .append(pl.getName())
                    .append(" - ")
                    .append(pl.getDescription().getVersion())
                    .append("\n");

        try {
            URL url = new URL("https://paste.md-5.net/documents");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            http.setConnectTimeout(5 * 1000);
            http.setReadTimeout(5 * 1000);
            http.setDoOutput(true);
            http.getOutputStream().write(builder.toString().getBytes(Charsets.UTF_8));
            JsonObject object = new Gson().fromJson(new InputStreamReader(http.getInputStream(), Charsets.UTF_8), JsonObject.class);
            return "https://paste.md-5.net/" + object.get("key").getAsString() + ".yaml";
        } catch (IOException exception) {
            return "&cCould not create debug link! Error: " + exception.getMessage();
        }
    }

}
