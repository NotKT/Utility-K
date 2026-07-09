package com.notkt.utilityk.gui;

import com.notkt.utilityk.module.FlyModule;
import com.notkt.utilityk.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UtilityMenuScreen extends Screen {

	private static final int ROW_HEIGHT = 22;
	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 20;

	private Button flyToggleButton;
	private Button flyModeButton;

	public UtilityMenuScreen() {
		super(Component.literal("Utility-K"));
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int startY = this.height / 2 - 40;

		FlyModule fly = ModuleManager.FLY;

		flyToggleButton = Button.builder(flyToggleLabel(fly), button -> {
					fly.toggle();
					button.setMessage(flyToggleLabel(fly));
				})
				.bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();
		this.addRenderableWidget(flyToggleButton);

		flyModeButton = Button.builder(flyModeLabel(fly), button -> {
					fly.cycleMode();
					button.setMessage(flyModeLabel(fly));
				})
				.bounds(centerX - BUTTON_WIDTH / 2, startY + ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build();
		this.addRenderableWidget(flyModeButton);

		this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
				.bounds(centerX - BUTTON_WIDTH / 2, startY + ROW_HEIGHT * 2, BUTTON_WIDTH, BUTTON_HEIGHT)
				.build());
	}

	private Component flyToggleLabel(FlyModule fly) {
		return Component.literal("Fly: " + (fly.isEnabled() ? "ON" : "OFF"));
	}

	private Component flyModeLabel(FlyModule fly) {
		return Component.literal("Mode: " + fly.getMode().name());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
