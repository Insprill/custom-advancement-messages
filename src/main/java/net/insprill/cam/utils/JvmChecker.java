package net.insprill.cam.utils;

import org.apache.commons.lang.math.NumberUtils;

public final class JvmChecker {

    // Taken & modified from https://github.com/PaperMC/Paper/commit/eb89fd5deabcaf7ab044f8f7953dde5c6e5c1ce1

    public static int getJvmVersion() {
        String javaVersion = System.getProperty("java.version");
        int dotIndex = javaVersion.indexOf('.');

        if (javaVersion.startsWith("1.")) {
            // For Java 8 and below, trim off the 1. prefix
            javaVersion = javaVersion.substring(2);
            dotIndex = javaVersion.indexOf('.');
        }

        int endIndex = dotIndex == -1 ? javaVersion.length() : dotIndex;
        String version = javaVersion.substring(0, endIndex);
        if (NumberUtils.isNumber(version))
            return Integer.parseInt(version);
        else
            return -1;
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
