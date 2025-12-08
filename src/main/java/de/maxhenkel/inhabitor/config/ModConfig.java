package de.maxhenkel.inhabitor.config;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;

public class ModConfig {

    public ConfigEntry<Integer> inhabitorCommandPermissionLevel;

    public ModConfig(ConfigBuilder builder) {
        inhabitorCommandPermissionLevel = builder.integerEntry("inhabitor_command_permission_level", 2, 0, 4);
    }

}
