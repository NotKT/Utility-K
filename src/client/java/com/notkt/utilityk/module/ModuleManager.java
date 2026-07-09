package com.notkt.utilityk.module;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds every module in the mod. Add new hacks here as they're built.
 */
public class ModuleManager {

	private static final List<Module> MODULES = new ArrayList<>();

	public static final FlyModule FLY = new FlyModule();

	static {
		MODULES.add(FLY);
		// Next modules (No Fall, etc.) get added here.
	}

	public static List<Module> getModules() {
		return MODULES;
	}

	public static void tickAll() {
		for (Module module : MODULES) {
			module.tick();
		}
	}
}
