package cc.polyfrost.oneconfig.gui.elements.config;

import cc.polyfrost.oneconfig.config.annotations.KeyBind;
import cc.polyfrost.oneconfig.internal.assets.Colors;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.gui.elements.BasicButton;
import cc.polyfrost.oneconfig.internal.config.core.KeyBindHandler;
import cc.polyfrost.oneconfig.renderer.RenderManager;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.internal.assets.SVGs;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;

import java.lang.reflect.Field;

public class ConfigKeyBind extends BasicOption {
    private final BasicButton button;
    private boolean clicked = false;

    public ConfigKeyBind(Field field, Object parent, String name, String category, String subcategory, int size) {
        super(field, parent, name, category, subcategory, size);
        button = new BasicButton(256, 32, "", SVGs.KEYSTROKE, null, BasicButton.ALIGNMENT_JUSTIFIED, ColorPalette.SECONDARY);
        button.setToggleable(true);
        KeyBindHandler.INSTANCE.addKeyBind(getKeyBind());
    }

    public static ConfigKeyBind create(Field field, Object parent) {
        KeyBind keyBind = field.getAnnotation(KeyBind.class);
        return new ConfigKeyBind(field, parent, keyBind.name(), keyBind.category(), keyBind.subcategory(), keyBind.size());
    }

    @Override
    public void draw(long vg, int x, int y) {
        if (!isEnabled()) RenderManager.setAlpha(vg, 0.5f);
        RenderManager.drawText(vg, name, x, y + 17, Colors.WHITE, 14f, Fonts.MEDIUM);
        OneKeyBind keyBind = getKeyBind();
        String text = keyBind.getDisplay();
        button.disable(!isEnabled());
        if (button.isToggled()) {
            if (text.equals("")) text = "Recording... (ESC to clear)";
            if (!clicked) {
                keyBind.clearKeys();
                setKeyBind(keyBind);
                clicked = true;
            } else if (keyBind.getSize() == 0 || keyBind.isActive()) {
                OneConfigGui.INSTANCE.allowClose = false;
            } else {
                button.setToggled(false);
                clicked = false;
                OneConfigGui.INSTANCE.allowClose = true;
            }
        } else if (text.equals("")) text = "None";
        button.setText(text);
        button.draw(vg, x + (size == 1 ? 224 : 736), y);
        RenderManager.setAlpha(vg, 1f);
    }

    @Override
    public void keyTyped(char key, int keyCode) {
        if (!button.isToggled()) return;
        OneKeyBind keyBind = getKeyBind();
        if (keyCode == UKeyboard.KEY_ESCAPE) {
            keyBind.clearKeys();
            button.setToggled(false);
            OneConfigGui.INSTANCE.allowClose = true;
            clicked = false;
        } else keyBind.addKey(keyCode);
        setKeyBind(keyBind);
    }

    private OneKeyBind getKeyBind() {
        OneKeyBind keyBind = new OneKeyBind();
        try {
            field.setAccessible(true);
            keyBind = (OneKeyBind) get();
        } catch (IllegalAccessException ignored) {
        }
        return keyBind;
    }

    private void setKeyBind(OneKeyBind keyBind) {
        try {
            set(keyBind);
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public int getHeight() {
        return 32;
    }
}
