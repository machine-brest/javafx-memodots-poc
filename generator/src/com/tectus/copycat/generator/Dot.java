package com.tectus.copycat.generator;

public class Dot
{
	public int x = 0; // column
	public int y = 0; // row

	public Dot() {
		this(0, 0);
	}

	public Dot(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || !(o == null || getClass() != o.getClass())
				&& this.x == ((Dot) o).x && this.y == ((Dot) o).y;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "}";
	}
}
