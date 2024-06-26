package de.maxhenkel.inhabitor;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.inhabitor.command.InhabitorCommands;
import de.maxhenkel.inhabitor.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Inhabitor implements ModInitializer {

    public static final String MODID = "inhabitor";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ModConfig CONFIG;
    public static final ThreadLocal<Boolean> IS_TICK_SAVE = ThreadLocal.withInitial(() -> false);

    @Override
    public void onInitialize() {
        CONFIG = ConfigBuilder.builder(ModConfig::new).path(FabricLoader.getInstance().getConfigDir().resolve(MODID).resolve("%s.properties".formatted(MODID))).build();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> InhabitorCommands.register(dispatcher));
    }

}
