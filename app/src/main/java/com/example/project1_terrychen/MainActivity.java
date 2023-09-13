package com.example.project1_terrychen;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

	private static final int GRID_ROWS = 12;
	private static final int GRID_COLUMNS = 10;
	private static final int NUMBER_OF_MINES = 4;
	private static final int CELL_SIZE_IN_DP = 26;
	private static final int CELL_TEXT_SIZE_IN_SP = 20;
	private static final int CELL_MARGIN_IN_DP = 2;

	private ArrayList<TextView> cellTextViews = new ArrayList<>();
	private ArrayList<Integer> cellValues = new ArrayList<>();
	private ArrayList<Pair<Integer, Integer>> cellIndices = new ArrayList<>();
	private HashSet<TextView> visitedCells = new HashSet<>();

	private boolean isPickaxeMode = true;
	private int elapsedTimeInSeconds = 0;
	private boolean isGameOngoing = true;
	private boolean hasWon = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startGameTimer();
		initializeGameGrid();
		assignMinesAndCalculateValues();
	}

	private int convertDpToPixels(int dp) {
		float density = Resources.getSystem().getDisplayMetrics().density;
		return Math.round(dp * density);
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
		cellTextView.setOnClickListener(this::handleCellClick);

		GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
		layoutParams.setMargins(convertDpToPixels(CELL_MARGIN_IN_DP), convertDpToPixels(CELL_MARGIN_IN_DP), convertDpToPixels(CELL_MARGIN_IN_DP), convertDpToPixels(CELL_MARGIN_IN_DP));
		layoutParams.rowSpec = GridLayout.spec(row);
		layoutParams.columnSpec = GridLayout.spec(col);

		gridLayout.addView(cellTextView, layoutParams);
		cellTextViews.add(cellTextView);
		cellIndices.add(new Pair<>(row, col));
	}

	private void assignMinesAndCalculateValues() {
		HashSet<Integer> minePositions = generateRandomMinePositions();
		HashMap<Integer, Integer> mineSurroundingCounts = calculateMineSurroundingCounts(minePositions);

		for (int i = 0; i < cellTextViews.size(); i++) {
			if (minePositions.contains(i)) {
				cellValues.add(-1);
			} else {
				cellValues.add(mineSurroundingCounts.getOrDefault(i, 0));
			}
		}
	}

	private HashSet<Integer> generateRandomMinePositions() {
		HashSet<Integer> minePositions = new HashSet<>();
		Random random = new Random();
		while (minePositions.size() < NUMBER_OF_MINES) {
			minePositions.add(random.nextInt(GRID_ROWS * GRID_COLUMNS));
		}
		return minePositions;
	}

	private HashMap<Integer, Integer> calculateMineSurroundingCounts(HashSet<Integer> minePositions) {
		HashMap<Integer, Integer> mineSurroundingCounts = new HashMap<>();
		for (int minePosition : minePositions) {
			Pair<Integer, Integer> index = cellIndices.get(minePosition);
			int row = index.first;
			int col = index.second;

			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == 0 && j == 0) continue;

					Pair<Integer, Integer> neighbor = new Pair<>(row + i, col + j);
					if (cellIndices.contains(neighbor)) {
						int neighborIndex = cellIndices.indexOf(neighbor);
						mineSurroundingCounts.put(neighborIndex, mineSurroundingCounts.getOrDefault(neighborIndex, 0) + 1);
					}
				}
			}
		}
		return mineSurroundingCounts;
	}

	public void handlePickaxeModeToggle(View view) {
		isPickaxeMode = !isPickaxeMode;
		TextView pickButton = findViewById(R.id.pickButton);
		pickButton.setText(getString(isPickaxeMode ? R.string.pick : R.string.flag));
	}

	private int findCellIndex(View view) {
		return cellTextViews.indexOf(view);
	}

	public void handleCellClick(View view) {
		TextView clickedCell = (TextView) view;
		int cellIndex = findCellIndex(view);

		if (!isGameOngoing) {
			navigateToResultsActivity();
		} else if (visitedCells.contains(view)) {
			return;
		} else if (!isPickaxeMode) {
			handleFlagModeClick(clickedCell);
		} else {
			handlePickaxeModeClick(clickedCell, cellIndex);
		}
	}

	private void navigateToResultsActivity() {
		Intent intent = new Intent(this, ResultsActivity.class);
		intent.putExtra("won", hasWon);
		intent.putExtra("seconds", elapsedTimeInSeconds);
		startActivity(intent);
	}

	private void handleFlagModeClick(TextView cell) {
		TextView flagCounter = findViewById(R.id.flagsLeft);
		int flagCount = Integer.parseInt(flagCounter.getText().toString());

		if (cell.getText().equals(getString(R.string.flag))) {
			cell.setText("");
			flagCounter.setText(String.valueOf(flagCount + 1));
		} else {
			cell.setText(getString(R.string.flag));
			flagCounter.setText(String.valueOf(flagCount - 1));

			checkGameEndCondition();
		}
	}

	private void handlePickaxeModeClick(TextView cell, int cellIndex) {
		if (cell.getText().equals(getString(R.string.flag)) || visitedCells.contains(cell)) {
			return;
		}

		if (cellValues.get(cellIndex) == -1) {
			Log.i("MyApp", "This is an info log message");

			revealAllMines();
			isGameOngoing = false;

			Log.i("MyApp", "This is an info log gggggmessage");

			navigateToResultsActivity();
		} else if (cellValues.get(cellIndex) > 0) {
			revealCell(cell, cellIndex);
			checkGameEndCondition();
		} else {
			performDFS(cell);
			checkGameEndCondition();
		}
	}

	private void revealAllMines() {
		for (int i = 0; i < cellValues.size(); i++) {
			if (cellValues.get(i) == -1) {
				TextView mineCell = cellTextViews.get(i);
				mineCell.setText(getString(R.string.mine));
				mineCell.setBackgroundColor(Color.GRAY);
			}
		}
	}

	private void revealCell(TextView cell, int cellIndex) {
		int cellValue = cellValues.get(cellIndex);
		cell.setText(cellValue > 0 ? String.valueOf(cellValue) : "");
		cell.setBackgroundColor(Color.GRAY);
		visitedCells.add(cell);
	}


	private void performDFS(TextView startCell) {
		ArrayList<TextView> queue = new ArrayList<>();
		queue.add(startCell);
		visitedCells.add(startCell);

		while (!queue.isEmpty()) {
			TextView currentCell = queue.remove(0);
			int currentCellIndex = findCellIndex(currentCell);

			if (cellValues.get(currentCellIndex) > 0) {
				revealCell(currentCell, currentCellIndex);
				continue;
			}

			revealCell(currentCell, currentCellIndex);
			queue.addAll(getValidUnvisitedNeighbors(currentCell));
		}
	}

	private ArrayList<TextView> getValidUnvisitedNeighbors(TextView cell) {
		ArrayList<TextView> neighbors = new ArrayList<>();
		Pair<Integer, Integer> cellPosition = cellIndices.get(findCellIndex(cell));

		int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
		for (int[] direction : directions) {
			int newRow = cellPosition.first + direction[0];
			int newCol = cellPosition.second + direction[1];
			Pair<Integer, Integer> neighborPosition = new Pair<>(newRow, newCol);

			if (cellIndices.contains(neighborPosition)) {
				TextView neighborCell = cellTextViews.get(cellIndices.indexOf(neighborPosition));
				if (!visitedCells.contains(neighborCell) && !neighborCell.getText().equals(getString(R.string.flag))) {
					neighbors.add(neighborCell);
					visitedCells.add(neighborCell);
				}
			}
		}

		return neighbors;
	}

	private void checkGameEndCondition() {
		if (visitedCells.size() == (GRID_ROWS * GRID_COLUMNS - NUMBER_OF_MINES)) {
			hasWon = true;
			isGameOngoing = false;

			navigateToResultsActivity();
		}
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
