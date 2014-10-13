package com.tectus.demo.copycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PatternBackground extends View
{
	private int columns = 8;
	private int rows    = 8;

	private Paint paint = new Paint();

	public PatternBackground(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(getResources().getColor(R.color.pattern_background_circle));
	}

	public void setGridSize(int columns, int rows)
	{
		this.columns = columns;
		this.rows    = rows;

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		float gridSide = .95f * Math.min(canvas.getWidth(), canvas.getHeight());
		float spacing = gridSide / Math.max(columns, rows);
		float radius  = spacing * .2f;

		// grid bounds
		float gridBWidth  = spacing * (columns - 1);
		float gridBHeight = spacing * (rows - 1);

		// drawing circles
		canvas.translate((canvas.getWidth() - gridBWidth) / 2, (canvas.getHeight() - gridBHeight) / 2);

		paint.setStyle(Paint.Style.FILL);
		for (int x = 0; x < columns; ++x) {
			for (int y = 0; y < rows; ++y) {
				canvas.drawCircle(x * spacing, y * spacing, radius, paint);
			}
		}
	}
}
