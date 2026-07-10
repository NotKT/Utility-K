package com.notkt.utilityk.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.phys.Vec3;

public class FlyModule implements Module {

	public enum Mode {
		VANILLA,
		ELYTRA,
		JETPACK,
		GLIDE
	}

	private boolean enabled = false;
	private Mode mode = Mode.VANILLA;
	private double speed = 1.0D;

	private static final double MIN_SPEED = 0.2D;
	private static final double MAX_SPEED = 5.0D;
	private static final double SPEED_STEP = 0.2D;

	// Remembers the player's real ability state so we can restore it on disable.
	private boolean previousFlying = false;
	private boolean previousMayFly = false;

	@Override
	public String getName() {
		return "Fly";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean value) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (value && !this.enabled) {
			// Turning on: remember real state, then force flight.
			if (player != null) {
				Abilities abilities = player.getAbilities();
				previousFlying = abilities.flying;
				previousMayFly = abilities.mayfly;
				abilities.mayfly = true;
				abilities.flying = true;
				player.onUpdateAbilities();
			}
		} else if (!value && this.enabled) {
			// Turning off: restore the real state (unless creative/spectator handles it).
			if (player != null) {
				Abilities abilities = player.getAbilities();
				boolean creativeOrSpectator = player.getAbilities().instabuild || player.isSpectator();
				abilities.flying = creativeOrSpectator && previousFlying;
				abilities.mayfly = creativeOrSpectator || previousMayFly;
				player.onUpdateAbilities();
			}
		}

		this.enabled = value;
	}

	public Mode getMode() {
		return mode;
	}

	public void cycleMode() {
		Mode[] values = Mode.values();
		int next = (mode.ordinal() + 1) % values.length;
		mode = values[next];
	}

	public double getSpeed() {
		return speed;
	}

	public void increaseSpeed() {
		speed = Math.min(MAX_SPEED, speed + SPEED_STEP);
	}

	public void decreaseSpeed() {
		speed = Math.max(MIN_SPEED, speed - SPEED_STEP);
	}

	@Override
	public void tick() {
		if (!enabled) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		// Keep abilities forced on every tick in case vanilla resets them
		// (e.g. respawn, gamemode packets).
		Abilities abilities = player.getAbilities();
		abilities.mayfly = true;
		abilities.flying = true;

		switch (mode) {
			case VANILLA -> tickVanilla(player);
			case ELYTRA -> tickElytra(player);
			case JETPACK -> tickJetpack(player);
			case GLIDE -> tickGlide(player);
		}
	}

	// Plain creative-style flight: vanilla's own movement code handles this once
	// flying=true is set above, so nothing extra needed here besides speed.
	private void tickVanilla(LocalPlayer player) {
		player.getAbilities().setFlyingSpeed((float) (0.05D * speed));
	}

	// Simulated elytra: thrust forward along the look vector, no real elytra needed.
	private void tickElytra(LocalPlayer player) {
		Vec3 look = player.getLookAngle();
		Vec3 motion = player.getDeltaMovement();

		double thrust = 0.08D * speed;
		Vec3 push = look.scale(thrust);

		// Blend towards look direction instead of snapping, for smoother flight.
		Vec3 result = motion.scale(0.9D).add(push);
		player.setDeltaMovement(result.x, result.y, result.z);
	}

	// Vertical boost while jump is held, otherwise fall slowly. Horizontal
	// movement stays under normal player control.
	private void tickJetpack(LocalPlayer player) {
		Vec3 motion = player.getDeltaMovement();
		double verticalSpeed = 0.15D * speed;

		boolean jumpHeld = Minecraft.getInstance().options.keyJump.isDown();
		boolean sneakHeld = player.isShiftKeyDown();

		double newY;
		if (jumpHeld) {
			newY = verticalSpeed;
		} else if (sneakHeld) {
			newY = -verticalSpeed;
		} else {
			newY = 0.0D;
		}

		player.setDeltaMovement(motion.x, newY, motion.z);
	}

	// Slow forward glide based on where the player looks, gentle descent.
	private void tickGlide(LocalPlayer player) {
		Vec3 look = player.getLookAngle();
		double horizontalSpeed = 0.06D * speed;
		double descend = -0.02D * speed;

		Vec3 flatLook = new Vec3(look.x, 0.0D, look.z).normalize();
		player.setDeltaMovement(
				flatLook.x * horizontalSpeed,
				descend,
				flatLook.z * horizontalSpeed
		);
	}
}
