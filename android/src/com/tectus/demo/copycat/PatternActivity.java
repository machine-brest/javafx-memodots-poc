package com.tectus.demo.copycat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.tectus.copycat.generator.DotPattern;
import com.tectus.copycat.generator.DotShape;

import java.util.List;

public class PatternActivity extends Activity
{
	PatternView patternDisplay;
	PatternBackground patternBackground;
	SharedPreferences preferences;

	@Override
	@SuppressWarnings("ConstantConditions")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pattern);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		patternDisplay = (PatternView) findViewById(R.id.patternDisplay);
		patternBackground = (PatternBackground) findViewById(R.id.patternBackground);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pattern_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuSettings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
		}

		return true;
	}

	public void generatePattern(View view)
	{
		int columns = 6;
		int rows    = 6;

		String gridSize = preferences.getString(SettingsKeys.PATTERN_GRID_SIZE, "8x8");
		String[] gridDimensions = gridSize.split("x", 2);
		if (gridDimensions.length == 2) {
			try {
				columns = Integer.parseInt(gridDimensions[0]);
				rows    = Integer.parseInt(gridDimensions[1]);
			}
			catch (NumberFormatException ex) {
				columns = rows = 6;
			}
		}

		patternBackground.setGridSize(columns, rows);
		patternDisplay.setGridSize(columns, rows);
		patternDisplay.setShapes(null);

		PatternParams params = new PatternParams();

		params.columns        = columns;
		params.rows           = rows;
		params.maxDots        = Integer.parseInt(preferences.getString(SettingsKeys.PATTERN_MAX_DOTS, "24"));
		params.maxShapes      = Integer.parseInt(preferences.getString(SettingsKeys.PATTERN_MAX_SHAPES, "2"));
		params.maxColors      = Integer.parseInt(preferences.getString(SettingsKeys.PATTERN_MAX_COLORS, "2"));
		params.allowOpen      = preferences.getBoolean(SettingsKeys.PATTERN_ALLOW_OPEN, false);
		params.allowDiagonals = preferences.getBoolean(SettingsKeys.PATTERN_ALLOW_DIAGONALS, false);

		new GeneratePatternTask().execute(params);
	}

	public class TaskCanceler implements Runnable
	{
		private GeneratePatternTask task;

		public TaskCanceler(GeneratePatternTask task) {
			this.task = task;
		}

		@Override
		public void run() {
			if (task.getStatus() == AsyncTask.Status.RUNNING )
				task.cancel(true);
		}
	}

	class GeneratePatternTask extends AsyncTask<PatternParams, Void, List<DotShape>>
	{
		@Override
		protected List<DotShape> doInBackground(PatternParams... params) {
			DotPattern pattern = new DotPattern(params[0].columns, params[0].rows);
			pattern.initialize(params[0].maxDots, params[0].maxShapes, params[0].maxColors, params[0].allowOpen, params[0].allowDiagonals);
			return pattern.generate();
		}

		protected void onPostExecute(List<DotShape> shapes) {
			patternDisplay.setShapes(shapes);
			patternDisplay.invalidate();
		}
	}

	class PatternParams
	{
		public int columns;
		public int rows;
		public int maxDots;
		public int maxShapes;
		public int maxColors;
		public boolean allowOpen;
		public boolean allowDiagonals;
	}
}
