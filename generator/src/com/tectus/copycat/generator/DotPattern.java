package com.tectus.copycat.generator;

import com.sun.javafx.Logging;

import java.util.*;

public class DotPattern
{
	private static final String TAG = "DotPattern";

	private int columns;
	private int rows;

	private int maxDots;
	private int maxShapes;
	private int maxColors;
	private boolean allowOpen = false;
	private boolean allowDiagonals = false;

	private List<DotShape> shapes;
	private Map<Dot, Integer> shapeDots;

	public DotPattern() {
		this(3, 3);
	}

	public DotPattern(int columns, int rows) {
		setGridSize(columns, rows);
	}

	public List<DotShape> getShapes() {
		return shapes;
	}

	public void setGridSize(int columns, int rows) {
		this.columns = columns;
		this.rows    = rows;
	}

	/**
	 * Initializes this pattern before generate
	 *
	 * @param maxDots Maximum count of dots to use per pattern
	 * @param maxShapes Maximum count of shapes to create
	 * @param maxColors Maximum count of colors to use for shapes
	 * @param allowOpen Is allowed open shapes
	 * @param allowDiagonals Is allowed diagonal lines in shapes
	 */
	public void initialize(int maxDots, int maxShapes, int maxColors, Boolean allowOpen, Boolean allowDiagonals)
	{
		this.maxDots        = maxDots;
		this.maxShapes      = maxShapes;
		this.maxColors      = maxColors;
		this.allowOpen      = allowOpen;
		this.allowDiagonals = allowDiagonals;

		// Log.d(TAG, "Generating pattern " + columns + "x" + rows);

		shapes    = new ArrayList<DotShape>(maxShapes);
		shapeDots = new HashMap<Dot, Integer>();
	}

	/**
	 * Generates pattern
	 * @return Shapes
	 */
	public List<DotShape> generate()
	{
		// initially set all dots as free

		List<Dot> freeDots = new ArrayList<Dot>();
		for (int dotIndex = 0, l = columns * rows; dotIndex < l; ++ dotIndex) {
			Dot dot = new Dot();
			dot.x = dotIndex % columns;
			dot.y = (dotIndex - dot.x) / columns;
			freeDots.add(dot);
		}

		int dpsMin = (!allowDiagonals && !allowOpen ? 4 : (allowOpen ? 2 : 3));
		for ( int sr = maxShapes, dr = maxDots; sr > 0 && dr > 0; --sr)
		{
			int dots;
			if (sr == 1) { // last
				dots = dr;
			}
			else {
				double pcut = 100 - ((sr - 1) * dpsMin / (dr / 100.0));
				dots = new Random().nextInt((int) Math.round(dr * pcut / 100));
				if (dots % 2 != 0 && (!allowDiagonals && !allowOpen))
					dots -= 1;
			}

			dr -= dots = Math.max(dpsMin, dots);

			// Log.d(TAG, String.format("Building a shape of %d dots", dots));

			DotShape shape = new DotShape();
			shape.setMaxSize(dots);
			shape.setId(maxShapes - sr);

			// System.out.printf(String.format("[shape id:%d] Generating shape. Size: %d\n", shape.getId(), dots));

			if (generateShape(new ArrayList<Dot>(freeDots), shape)) {
				// System.out.printf("Shape created: %s\n", shape);
				shapes.add(shape);
				for (Dot d: shape.getDots()) {
					shapeDots.put(d, shape.getId());
					freeDots.remove(d);
				}
			}
			else {
				// Log.d(TAG, "Could not create shape");
				break;
			}
		}

		if (maxColors > 1)
			colorize();

		return shapes;
	}

	private void colorize()
	{
		List<Integer> colorStyles = new ArrayList<Integer>(maxColors);
		for (int i = 0; i < maxColors; ++i)
			colorStyles.add(i);

		for (DotShape shape: shapes)
		{
			Integer colorIndex = colorStyles.size() > 1
					? colorStyles.get(new Random().nextInt(colorStyles.size() - 1))
					: colorStyles.get(0);

			shape.setColorIndex(colorIndex);
			colorStyles.remove(colorIndex);
		}
	}

	/**
	 * Generates dot shape using given number of dots.
	 * Shape can be closed/open and have or not diagonal lines.
	 *
	 * @param freeDots Free dots to use
	 * @return Shape
	 */
	protected Boolean generateShape(List<Dot> freeDots, DotShape shape) {

		if (freeDots.isEmpty())
			return false;

		while (freeDots.size() >= shape.getMaxSize())
		{
			Dot from = freeDots.size() > 1
					? freeDots.get(new Random().nextInt(freeDots.size() - 1))
					: freeDots.get(0);

			/* do not allow to start shape on the grid's edges
			if (from.x >= columns - 1 || from.x < 1 || from.y < 1 || from.y >= rows - 1) {
				freeDots.remove(from);
				continue;
			}
			*/

			if (shapeDots.containsKey(from)) {
				freeDots.remove(from);
				continue;
			}

			// System.out.printf(String.format("[shape] try to begin from %s\n", from));
			if (generateShapeDots(shape, from, freeDots)) {
				return true;
			}

			shape.clear();
			freeDots.remove(from);
		}

		shape.clear();
		return false;
	}

	protected Boolean generateShapeDots(DotShape shape, Dot from, List<Dot> freeDots) {

		if (shape.size() == shape.getMaxSize()) {

			if (!allowOpen) {

				Dot first = shape.getFirst();
				Dot last  = shape.getLast();

				if (allowDiagonals) {
					if (Math.abs(first.x - last.x) > 1 || Math.abs(first.y - last.y) > 1)
						return false;
				} else {
					if (Math.abs(first.x - last.x) + Math.abs(first.y - last.y) > 1)
						return false;
				}

				shape.close(true);
			}

			return true; // shape complete
		}

		if (freeDots.isEmpty()) { // no more free dots
			return false;
		}

		// skip to far dots

		if (!allowOpen)
		{
			Dot first = shape.getFirst();

			if (first != null) {

				int dx = Math.abs(first.x - from.x);
				int dy = Math.abs(first.y - from.y);
				int minDots = allowDiagonals ? Math.max(dx, dy) - 1 : dx + dy -1;
				int remDots = shape.getMaxSize() - shape.size();

				if (minDots >= remDots) {
					// System.out.printf("[dot %s] skipped: dot is to far from the begin %s\n", from, first);
					freeDots.remove(from);
					return false;
				}
			}
		}

		// select next possible dot

		List<Dot> dots = new ArrayList<Dot>();

		dots.add(new Dot(from.x + 1, from.y));
		dots.add(new Dot(from.x, from.y + 1));
		dots.add(new Dot(from.x - 1, from.y));
		dots.add(new Dot(from.x, from.y - 1));

		if (allowDiagonals) {
			dots.add(new Dot(from.x + 1, from.y + 1));
			dots.add(new Dot(from.x - 1, from.y + 1));
			dots.add(new Dot(from.x - 1, from.y - 1));
			dots.add(new Dot(from.x + 1, from.y - 1));
		}

		// checking possible moves

		for (Iterator<Dot> it = dots.iterator(); it.hasNext(); )
		{
			Dot next = it.next();
			if (next.x < 0 || next.x >= columns || next.y < 0 || next.y >= rows ) {
				it.remove();
				continue;
			}

			if (shape.contains(next) || shapeDots.containsKey(next)) {
				it.remove();
				continue;
			}

			if (allowDiagonals)
			{
				Dot dot1 = null, dot2 = null;

				if (next.x - from.x == 1 && next.y - from.y == 1) {
					dot1 = new Dot(from.x + 1, from.y);
					dot2 = new Dot(from.x, from.y + 1);
				} else if (next.x - from.x == -1 && next.y - from.y == 1) {
					dot1 = new Dot(from.x, from.y + 1);
					dot2 = new Dot(from.x - 1, from.y);
				} else if (next.x - from.x == -1 && next.y - from.y == -1) {
					dot1 = new Dot(from.x - 1, from.y);
					dot2 = new Dot(from.x, from.y - 1);
				} else if (next.x - from.x == 1 && next.y - from.y == -1) {
					dot1 = new Dot(from.x, from.y - 1);
					dot2 = new Dot(from.x + 1, from.y);
				}

				if (dot1 != null) { // dot2 is also not null here

					if (shape.contains(dot1) || shape.contains(dot2)) {
						it.remove();
						continue;
					}

					Integer shape1Id = shapeDots.get(dot1);
					Integer shape2Id = shapeDots.get(dot2);

					if (shape1Id != null && shape1Id.equals(shape2Id)) {
						it.remove();
					}
				}
			}
		}

		while (dots.size() > 0) {

			/*
			if (allowDiagonals && dots.size() < 3) {
				shape.remove(from);
				return false;
			}
			*/

			Dot next = dots.size() > 1 ? dots.get(new Random().nextInt(dots.size() - 1)) : dots.get(0);

			shape.add(from);
			if (! generateShapeDots(shape, next, freeDots)) {
				freeDots.remove(from);
				shape.remove(from);
				dots.remove(next);
				continue;
			}

			return true;
		}

		return false;
	}
}
