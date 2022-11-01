package de.maxhenkel.inhabitor;

import de.maxhenkel.inhabitor.command.InhabitorCommands;
import de.maxhenkel.inhabitor.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Inhabitor implements ModInitializer {

    public static final String MODID = "inhabitor";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ModConfig CONFIG;

    @Override
    public void onInitialize() {
        // CONFIG = ConfigBuilder.build(FabricLoader.getInstance().getConfigDir().resolve(MODID).resolve("%s.properties".formatted(MODID)), ServerConfig::new);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> InhabitorCommands.register(dispatcher));
    }

}
