package net.insprill.cam.utils;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;


public enum Dependency {

	PAPI("PlaceholderAPI"),
	VAULT("Vault");

	private final String name;
	@Getter
	private Object clazz;

	Dependency(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(name);
	}

	public static void initClasses() {
		Arrays.stream(Dependency.values()).filter(Dependency::isEnabled).forEach(value -> {
			switch (value) {
				case VAULT -> {
					RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
					if (chatProvider != null)
						value.clazz = chatProvider.getProvider();
				}
			}
		});
	}

}
