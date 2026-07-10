package com.notkt.utilityk;

import com.mojang.blaze3d.platform.InputConstants;
import com.notkt.utilityk.gui.UtilityMenuScreen;
import com.notkt.utilityk.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class UtilityKClient implements ClientModInitializer {

	private static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath("utilityk", "controls"));

	private static KeyMapping flyToggleKey;
	private static KeyMapping flyModeKey;
	private static KeyMapping speedUpKey;
	private static KeyMapping speedDownKey;
	private static KeyMapping openMenuKey;

	@Override
	public void onInitializeClient() {
		flyToggleKey = registerKey("key.utilityk.fly_toggle", GLFW.GLFW_KEY_R);
		flyModeKey = registerKey("key.utilityk.fly_mode", GLFW.GLFW_KEY_Y);
		speedUpKey = registerKey("key.utilityk.speed_up", GLFW.GLFW_KEY_EQUAL);
		speedDownKey = registerKey("key.utilityk.speed_down", GLFW.GLFW_KEY_MINUS);
		openMenuKey = registerKey("key.utilityk.open_menu", GLFW.GLFW_KEY_K);

		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
	}

	private KeyMapping registerKey(String translationKey, int glfwKey) {
		return KeyMappingHelper.registerKeyMapping(new KeyMapping(
				translationKey,
				InputConstants.Type.KEYSYM,
				glfwKey,
				CATEGORY
		));
	}

	private void onTick(Minecraft client) {
		while (flyToggleKey.consumeClick()) {
			ModuleManager.FLY.toggle();
		}

		while (flyModeKey.consumeClick()) {
			ModuleManager.FLY.cycleMode();
		}

		while (speedUpKey.consumeClick()) {
			ModuleManager.FLY.increaseSpeed();
		}

		while (speedDownKey.consumeClick()) {
			ModuleManager.FLY.decreaseSpeed();
		}

		while (openMenuKey.consumeClick()) {
			if (client.screen == null) {
				client.setScreen(new UtilityMenuScreen());
			}
		}

		ModuleManager.tickAll();
	}
}
