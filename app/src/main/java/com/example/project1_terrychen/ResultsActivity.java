package com.example.project1_terrychen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

	// Checks in passed in information to show whether won or lost and seconds taken
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		// Getting intent
		Intent intent = getIntent();
		int seconds = intent.getIntExtra("seconds", 0);
		Boolean won = intent.getBooleanExtra("won", false);
		// Setting results
		TextView results = (TextView) findViewById(R.id.results);
		if (won) {
			results.setText("Used " + String.valueOf(seconds) + " seconds. \nYou won.\nGood job!");
		}
		else {
			results.setText("Used " + String.valueOf(seconds) + " seconds. \nYou lost.\nNice try.");
		}
	}

	// Reset button clicked
	public void resetClicked(View view) {
		// Goes back to main activity and clears information
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}

