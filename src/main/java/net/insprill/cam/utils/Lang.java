package net.insprill.cam.utils;

import net.insprill.cam.CAM;

public class Lang {

    public static String get(String path) {
        return get(path, null);
    }

    public static String get(String path, String def) {
        return CAM.getInstance().langFile.getString(path, def);
    }

}
