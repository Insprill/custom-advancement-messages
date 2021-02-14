package net.insprill.cam.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JvmChecker {

    // Taken & modified from https://github.com/PaperMC/Paper/blob/master/Spigot-Server-Patches/0610-Add-warning-for-servers-not-running-on-Java-11.patch

    public static int getJvmVersion() {
        String javaVersion = System.getProperty("java.version");
        Matcher matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);
        if (!matcher.find()) {
            CF.sendConsoleMessage("Failed to determine Java version; Could not parse: {" + javaVersion + "}");
            return -1;
        }

        String version = matcher.group(1);
        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException e) {
            CF.sendConsoleMessage("Failed to determine Java version; Could not parse: {" + version + "} from {" + javaVersion + "}");
            return -1;
        }
    }

    public static void checkJvm() {
        if (getJvmVersion() < 11) {
            CF.sendConsoleMessage("&e************************************************************");
            CF.sendConsoleMessage("&e* WARNING - YOU ARE RUNNING AN OUTDATED VERSION OF JAVA.");
            CF.sendConsoleMessage("&e* Please update the version of Java you use to at least Java 11.");
            CF.sendConsoleMessage("&e*");
            CF.sendConsoleMessage("&e* Current Java version: " + System.getProperty("java.version"));
            CF.sendConsoleMessage("&e*");
            CF.sendConsoleMessage("&e* Check this forum post from PaperMC for more information: ");
            CF.sendConsoleMessage("&e*   https://papermc.io/java11");
            CF.sendConsoleMessage("&e************************************************************");
        }
    }


}
