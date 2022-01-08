package net.insprill.cam.utils.files;

import java.io.File;
import java.io.InputStreamReader;

public class YamlFile extends net.insprill.xenlib.files.YamlFile {

	public static final YamlFile ADV_MESSAGES = new YamlFile("advancementMessages");
	public static final YamlFile DATA = new YamlFile("data");

	public YamlFile(String name) {
		super(name);
	}

	public YamlFile(String name, boolean autoUpdate) {
		super(name, autoUpdate);
	}

	public YamlFile(File file) {
		super(file);
	}

	public YamlFile(File file, boolean autoUpdate) {
		super(file, autoUpdate);
	}

	public YamlFile(InputStreamReader reader) {
		super(reader);
	}

}
