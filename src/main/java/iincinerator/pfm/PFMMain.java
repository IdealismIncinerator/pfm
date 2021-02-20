package iincinerator.pfm;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

// Copyright 2020, Idealism Incinerator. All Rights Reserved.

@Mod("pfm")
public class PFMMain {
    public PFMMain() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        Modifications.start();
    }
}
