package com.example.project1_terrychen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

	private static final int GRID_ROWS = 12;
	private static final int GRID_COLUMNS = 10;
	private static final int CELL_SIZE_IN_DP = 26;
	private static final int CELL_TEXT_SIZE_IN_SP = 20;
	private static final int CELL_MARGIN_IN_DP = 2;

	private ArrayList<TextView> cellTextViews = new ArrayList<>();
	private ArrayList<Pair<Integer, Integer>> cellIndices = new ArrayList<>();


	private int elapsedTimeInSeconds = 0;
	private boolean isGameOngoing = true;
	private boolean isPickaxeMode = true;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		startGameTimer();
		initializeGameGrid();

	}

	private void initializeGameGrid() {
		GridLayout gridLayout = findViewById(R.id.gridLayout);
		for (int row = 0; row < GRID_ROWS; row++) {
			for (int col = 0; col < GRID_COLUMNS; col++) {
				createAndAddCellToGrid(gridLayout, row, col);
			}
		}
	}

	private void createAndAddCellToGrid(GridLayout gridLayout, int row, int col) {
		TextView cellTextView = new TextView(this);
		cellTextView.setHeight(convertDpToPixels(CELL_SIZE_IN_DP));
		cellTextView.setWidth(convertDpToPixels(CELL_SIZE_IN_DP));
		cellTextView.setTextSize(CELL_TEXT_SIZE_IN_SP);
		cellTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
		cellTextView.setTextColor(Color.WHITE);
		cellTextView.setBackgroundColor(Color.GREEN);

		GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
		layoutParams.setMargins(convertDpToPixels(CELL_MARGIN_IN_DP), convertDpToPixels(CELL_MARGIN_IN_DP), convertDpToPixels(CELL_MARGIN_IN_DP), convertDpToPixels(CELL_MARGIN_IN_DP));
		layoutParams.rowSpec = GridLayout.spec(row);
		layoutParams.columnSpec = GridLayout.spec(col);

		gridLayout.addView(cellTextView, layoutParams);
		cellTextViews.add(cellTextView);
		cellIndices.add(new Pair<>(row, col));
	}

	private int convertDpToPixels(int dp) {
		float density = Resources.getSystem().getDisplayMetrics().density;
		return Math.round(dp * density);
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
