package com.justinm.togglepvp;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("togglepvp")
public class TogglePVPMod {
    public static final String MOD_ID = "togglepvp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public TogglePVPMod() {
        LOGGER.info("Toggle PVP mod loaded successfully!");
    }
}
