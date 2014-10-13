package com.tectus.demo.copycat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher);
	}

	public void showSettings(View view) {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	public void showGenerator(View view) {
		startActivity(new Intent(this, PatternActivity.class));
	}
}
