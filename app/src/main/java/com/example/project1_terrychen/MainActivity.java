package com.example.project1_terrychen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private int elapsedTimeInSeconds = 0;
	private boolean isGameOngoing = true;
	private boolean isPickaxeMode = true;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		startGameTimer();

	}

	public void handlePickaxeModeToggle(View view) {
		isPickaxeMode = !isPickaxeMode;
		TextView pickButton = findViewById(R.id.pickButton);
		pickButton.setText(getString(isPickaxeMode ? R.string.pick : R.string.flag));
	}

	private void startGameTimer() {
		TextView timerTextView = findViewById(R.id.clockTimer);
		Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (isGameOngoing) {
					elapsedTimeInSeconds++;
					timerTextView.setText(String.valueOf(elapsedTimeInSeconds));
				}
				handler.postDelayed(this, 1000);
			}
		});
	}
}
