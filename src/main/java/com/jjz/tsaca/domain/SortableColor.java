package com.jjz.tsaca.domain;

public enum SortableColor {

	PURPLE("purple"), //
	GREEN("green"), //
	YELLOW("yellow"), //
	RED("red"), //
	OFF("off");

	private String color;

	private SortableColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return this.color;
	}
}
