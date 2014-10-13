package com.tectus.demo.copycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.tectus.copycat.generator.Dot;
import com.tectus.copycat.generator.DotShape;

import java.util.List;

public class PatternView extends View
{
	private int columns = 8;
	private int rows = 8;
	private List<DotShape> shapes;
	private Path shapePath = new Path(); // here just for reusing
	private Paint dotPaint = new Paint();
	private Paint shapePaint = new Paint();

	public PatternView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		dotPaint.setAntiAlias(true);
		dotPaint.setStyle(Paint.Style.FILL);

		shapePaint.setStyle(Paint.Style.STROKE);
		shapePaint.setStrokeCap(Paint.Cap.ROUND);
		shapePaint.setStrokeWidth(12f);
	}

	public void setGridSize(int columns, int rows)
	{
		this.columns = columns;
		this.rows    = rows;
	}

	public void setShapes(List<DotShape> shapes) {
		this.shapes = shapes;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (shapes == null)
			return;

		float gridSide = .95f * Math.min(canvas.getWidth(), canvas.getHeight());
		float spacing = gridSide / Math.max(columns, rows);
		float radius  = spacing * .2f;

		// grid bounds
		float gridBWidth  = spacing * (columns - 1);
		float gridBHeight = spacing * (rows - 1);

		// drawing circles
		canvas.translate((canvas.getWidth() - gridBWidth) / 2, (canvas.getHeight() - gridBHeight) / 2);

		for (DotShape shape: shapes) {

			int styleIndex = shape.getColorIndex();
			int resid_style_circle_fill  = getResources().getIdentifier("style" + styleIndex + "_circle_fill", "color", getContext().getPackageName());
			int resid_style_shape_stroke = getResources().getIdentifier("style" + styleIndex + "_shape_stroke", "color", getContext().getPackageName());
			int resid_style_shape_fill   = getResources().getIdentifier("style" + styleIndex + "_shape_fill", "color", getContext().getPackageName());

			dotPaint.setColor(getResources().getColor(resid_style_circle_fill));
			shapePaint.setColor(getResources().getColor(resid_style_shape_stroke));

			shapePath.reset();

			Dot first = shape.getFirst();
			shapePath.moveTo(first.x * spacing, first.y * spacing);

			for (Dot dot : shape.getDots()) {
				canvas.drawCircle(dot.x * spacing, dot.y * spacing, radius, dotPaint);
				shapePath.lineTo(dot.x * spacing, dot.y * spacing);
			}

			if (shape.isClosed())
				shapePath.close();

			canvas.drawPath(shapePath, shapePaint);
		}
	}
}
