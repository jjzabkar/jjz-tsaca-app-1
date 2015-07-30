package com.jjz.tsaca.domain;

public enum SortableColor {

	PURPLE("purple", "ff00ff"), //
	GREEN("green", "00ff00"), //
	YELLOW("yellow", "daca00"), //
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

	public String toRGB(final String subDelimiter) {
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.decode("0x" + (hex.substring(0, 2))));
		sb.append(subDelimiter);
		sb.append(Integer.decode("0x" + (hex.substring(2, 4))));
		sb.append(subDelimiter);
		sb.append(Integer.decode("0x" + (hex.substring(4, 6))));
		return sb.toString();
	}
}
