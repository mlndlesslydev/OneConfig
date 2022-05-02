package io.polyfrost.oneconfig.gui.pages;

import io.polyfrost.oneconfig.config.OneConfigConfig;
import io.polyfrost.oneconfig.gui.elements.BasicButton;
import io.polyfrost.oneconfig.lwjgl.RenderManager;
import io.polyfrost.oneconfig.lwjgl.font.Fonts;
import io.polyfrost.oneconfig.lwjgl.image.Images;

public class HomePage extends Page {
    private final BasicButton btn = new BasicButton(184, 36, "Socials", Images.SHARE, Images.LAUNCH, 1, BasicButton.ALIGNMENT_CENTER);

    public HomePage() {
        super("Home Dashboard");
    }

    public void draw(long vg, int x, int y) {
        RenderManager.drawRoundedRect(vg, x, y, 184, 36, -1, 12f);
        RenderManager.drawString(vg, "This is a cool string to test pages", x + 32, y + 72, -1, 36f, Fonts.INTER_BOLD);
        RenderManager.drawRoundedRect(vg, x + 350, y + 310, 300, 200, OneConfigConfig.BLUE_600, 14f);
        //RenderManager.drawRoundedRect(vg);
        btn.draw(vg, x + 432, y + 658);
    }

    @Override
    public boolean isBase() {
        return true;
    }
}
