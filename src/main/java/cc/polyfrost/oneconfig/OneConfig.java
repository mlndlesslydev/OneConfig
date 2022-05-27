package cc.polyfrost.oneconfig;

import cc.polyfrost.oneconfig.command.OneConfigCommand;
import cc.polyfrost.oneconfig.config.OneConfigConfig;
import cc.polyfrost.oneconfig.config.core.ConfigCore;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.hud.HudCore;
import cc.polyfrost.oneconfig.lwjgl.BlurHandler;
import cc.polyfrost.oneconfig.lwjgl.RenderManager;
import cc.polyfrost.oneconfig.lwjgl.font.Fonts;
import cc.polyfrost.oneconfig.lwjgl.image.Images;
import cc.polyfrost.oneconfig.test.TestConfig;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@net.minecraftforge.fml.common.Mod(modid = "@ID@", name = "@NAME@", version = "@VER@")
public class OneConfig {
    public static File jarFile;
    public static File oneConfigDir = new File("./OneConfig");
    public static File themesDir = new File(oneConfigDir, "themes");
    public static OneConfigConfig config;
    public static TestConfig testConfig;
    public static List<Mod> loadedMods = new ArrayList<>();
    public static List<ModMetadata> loadedOtherMods = new ArrayList<>();

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onPreFMLInit(net.minecraftforge.fml.common.event.FMLPreInitializationEvent event) {
        if (!Launch.blackboard.containsKey("oneconfig.initialized")) {
            throw new RuntimeException("OneConfig has not been initialized! Please add the OneConfig tweaker or call OneConfigInit via an ITweaker or a FMLLoadingPlugin!");
        }
        jarFile = event.getSourceFile();
        oneConfigDir.mkdirs();
        themesDir.mkdirs();
        config = new OneConfigConfig();
    }

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onFMLInitialization(net.minecraftforge.fml.common.event.FMLInitializationEvent event) {
        BlurHandler.INSTANCE.load();
        testConfig = new TestConfig();
        ClientCommandHandler.instance.registerCommand(new OneConfigCommand());
        EventManager.INSTANCE.getEventBus().register(new HudCore());
        RenderManager.setupAndDraw((vg) -> {
            RenderManager.drawRoundedRect(vg, -100, -100, 50, 50, -1, 12f);
            RenderManager.drawString(vg, "OneConfig loading...", -100, -100, -1, 12f, Fonts.MEDIUM);
            RenderManager.drawImage(vg, Images.HUE_GRADIENT, -100, -100, 50, 50);
        });
    }

    @net.minecraftforge.fml.common.Mod.EventHandler
    public void onPostFMLInit(net.minecraftforge.fml.common.event.FMLPostInitializationEvent event) {
        reloadModsList();
    }

    public static void reloadModsList() {
        loadedMods.addAll(ConfigCore.oneConfigMods);
        LinkedHashSet<Mod> modData = new LinkedHashSet<>(ConfigCore.oneConfigMods);
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            ModMetadata metadata = mod.getMetadata();
            loadedOtherMods.add(metadata);
            String imageName = Loader.instance().activeModContainer() == null || Loader.instance().activeModContainer().getMetadata().logoFile.trim().equals("") ? null : "/" + Loader.instance().activeModContainer().getMetadata().logoFile;
            Mod newMod = new Mod(metadata.name, ModType.THIRD_PARTY, imageName);
            newMod.isShortCut = true;
            if (mod instanceof DummyModContainer || newMod.name.equals("OneConfig")) continue;
            if (modData.add(newMod)) loadedMods.add(newMod);
        }
    }
}
