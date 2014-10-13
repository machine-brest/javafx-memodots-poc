package com.tectus.demo.copycat;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class PatternDrawingThread extends Thread
{
	SurfaceHolder surfaceHolder;
	PatternView patternView;

	public PatternDrawingThread(SurfaceHolder surfaceHolder, PatternView patternView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.patternView   = patternView;
	}

	@Override
	public void run() {
		Canvas canvas;
	}
}
