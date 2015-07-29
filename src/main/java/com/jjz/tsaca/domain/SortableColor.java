package com.jjz.tsaca.domain;

public enum SortableColor {

	PURPLE("purple", "ff00ff"), //
	GREEN("green", "00ff00"), //
	YELLOW("yellow", "dada00"), //
	RED("red", "ff0000"), //
	OFF("off", "000000");

	private final String color;
	private final String hex;

	private SortableColor(String color, String hex) {
		this.color = color;
		this.hex = hex;
	}

	@Override
	public String toString() {
		return this.color;
	}

	public String toHex() {
		return this.hex;
	}
}
