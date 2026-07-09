package com.notkt.utilityk.module;

/**
 * Base interface for a Utility-K hack module.
 * Keep implementations simple: toggle a boolean, do per-tick work in tick().
 */
public interface Module {

	String getName();

	boolean isEnabled();

	void setEnabled(boolean enabled);

	default void toggle() {
		setEnabled(!isEnabled());
	}

	/**
	 * Called every client tick, whether enabled or not (check isEnabled() inside).
	 */
	void tick();
}
