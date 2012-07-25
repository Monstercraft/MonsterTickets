package org.monstercraft.support.plugin.util;

public enum Status {
	OPEN(1), CLAIMED(2), CLOSED(3);

	Status(int id) {
		this.id = id;
	}

	public int toInt() {
		return id;
	}

	private int id;
}
