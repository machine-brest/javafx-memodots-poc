package com.tectus.copycat.generator;

import java.util.*;

public class DotShape
{
	private int id = -1;
	private int colorIndex = 0;
	private List<Dot> dots = new LinkedList<Dot>();
	private Boolean isClosed = false;

	/**
	 * Maximum size of this shape
	 */
	private int maxSize;

	public DotShape() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public List<Dot> getDots() {
		return dots;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public Dot get(int index) {
		return index < dots.size() ? dots.get(index) : null;
	}

	public Dot getFirst() {
		return dots.isEmpty() ? null : dots.get(0);
	}

	public Dot getLast() {
		return dots.isEmpty() ? null : dots.get(dots.size() - 1);
	}

	public void add(Dot dot) {
		dots.add(dot);
		close(false);
	}

	public void remove(Dot dot) {
		dots.remove(dot);
		close(false);
	}

	public void remove(int index) {
		dots.remove(index);
		close(false);
	}

	public Boolean contains(Dot dot) {
		return dots.contains(dot);
	}

	public Boolean contains(int x, int y) {
		return dots.contains(new Dot(x, y));
	}

	public int size() {
		return dots.size();
	}

	public void close(Boolean isClosed) {
		this.isClosed = isClosed;
	}

	public void clear() {
		dots.clear();
		close(false);
	}

	/**
	 * Returns true if shape's first point has the same
	 * coordinates as the last one.
	 *
	 * @return true is shape is closed
	 */
	public Boolean isClosed() {
		return isClosed;
	}

	public boolean equals(DotShape shape)
	{
		if (getId() != -1 && shape.getId() != -1)
			return getId() == shape.getId();

		if (size() != shape.size())
			return false;

		for (int i = 0, l = size(); i < l; ++i)
			if (get(i) == shape.get(i))
				return false;

		return true;
	}

	@Override
	public String toString() {
		String str = "";
		Iterator it = dots.iterator();
		while (it.hasNext())
			str += (it.next().toString() + (it.hasNext() ? " " : ""));
		return "[ " + str + " ]" + (isClosed ? " closed," : " open,") + " size: " + size();
	}
}
