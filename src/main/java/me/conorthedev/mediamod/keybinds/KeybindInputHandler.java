package me.conorthedev.mediamod.keybinds;

import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import me.conorthedev.mediamod.util.PlayerMessager;
import me.conorthedev.mediamod.util.TickScheduler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * The class that handles keybind events
 */
public class KeybindInputHandler {
    /**
     * Fired when a key is pressed
     *
     * @param event - KeyInputEvent
     * @see InputEvent.KeyInputEvent
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeybindManager.INSTANCE.disableKeybind.isPressed()) {
            if (!Settings.SHOW_PLAYER) {
                PlayerMessager.sendMessage("Player Visible");
                Settings.SHOW_PLAYER = true;
            } else {
                PlayerMessager.sendMessage("Player Hidden");
                Settings.SHOW_PLAYER = false;
            }
            Settings.saveConfig();
        }

        if (KeybindManager.INSTANCE.menuKeybind.isPressed()) {
            TickScheduler.INSTANCE.schedule(0, () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMediaModSettings()));
        }
    }
}
