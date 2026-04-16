package com.jef.justenoughfakepixel;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.gui.GuiScreenElementWrapper;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Set;

public class JefGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraft) {
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return WrappedJefConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class WrappedJefConfig extends GuiScreenElementWrapper {

        private final GuiScreen parent;

        public WrappedJefConfig(GuiScreen parent) {
            super(new ConfigEditor(JefConfig.feature));
            this.parent = parent;
        }

        @Override
        public void handleKeyboardInput() throws IOException {
            if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                Minecraft.getMinecraft().displayGuiScreen(parent);
                return;
            }
            super.handleKeyboardInput();
        }
    }
}