package com.example.android_self_study_1;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarMapActivity extends AppCompatActivity {

    private static int[] visited_your_arr;   // cols*rows array of each button id
    private int[] visited_opponent_arr = null;
    private static boolean isYourMove;
    private TextView textViewMove;

    private Thread threadGs;
    private ImageView imageView;
    private TextView textViewYourBoard;
    private TextView textViewOpponentBoard;

    private static final List<ImageView> imageViewList = new ArrayList<>();

    private int numOfYourRuinsLeft;
    private int numOfOpponentRuinsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_war_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.war_map_activity_id), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startInit(savedInstanceState);
        createButtonsForBoards();
    }

    private void startInit(Bundle savedInstanceState) {
        textViewOpponentBoard = findViewById(R.id.opponentBoard_id);
        textViewYourBoard = findViewById(R.id.yourBoard_id);

        visited_your_arr = getIntent().getIntArrayExtra(ShipPlacementActivity.VISITED_ARR);

        numOfYourRuinsLeft = 20;
        numOfOpponentRuinsLeft = 20;

        placeOpponentShips(); // Place opponent's ships
    }

    private void setIconToButton(ImageView imageView, int type) {
        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (type == 1) {
            imageView.setImageResource(R.drawable.one_part_ship);
        } else if (type == 2) {
            imageView.setImageResource(R.drawable.two_part_ship);
        } else if (type == 3) {
            imageView.setImageResource(R.drawable.three_part_ship);
        } else if (type == 4) {
            imageView.setImageResource(R.drawable.one_part_ship);
        } else if (type == 5 || type == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell);
        }
    }
    // Total number of ships
    private static final int BUFFER_ZONE = 1; // Buffer zone around each ship

    private void placeOpponentShips() {
        visited_opponent_arr = new int[100]; // 10x10 grid
        Random random = new Random();

        // Ship sizes and their respective counts
        int[] shipSizes = {1, 1, 1, 1,   // 4 one-part ships
                2, 2,         // 3 two-part ships
                3, 3,         // 2 three-part ships
                4};           // 1 four-part ship

        for (int shipSize : shipSizes) {
            boolean placed = false;
            while (!placed) {
                // Randomly choose whether to place horizontally or vertically
                boolean horizontal = random.nextBoolean();
                int startX = random.nextInt(10);
                int startY = random.nextInt(10);

                // Check if the ship can be placed
                if (canPlaceShip(startX, startY, shipSize, horizontal)) {
                    placeShip(startX, startY, shipSize, horizontal);
                    placed = true;
                }
            }
        }
    }

    private boolean canPlaceShip(int startX, int startY, int shipSize, boolean horizontal) {
        for (int i = 0; i < shipSize; i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;

            // Check bounds and overlap with existing ships
            if (x >= 10 || y >= 10 || visited_opponent_arr[y * 10 + x] != 0) {
                return false; // Ship goes out of bounds or overlaps another ship
            }
        }

        // Check buffer zone around the ship
        for (int i = -BUFFER_ZONE; i <= shipSize; i++) {
            for (int j = -BUFFER_ZONE; j <= BUFFER_ZONE; j++) {
                int x = horizontal ? startX + i : startX + j;
                int y = horizontal ? startY + j : startY + i;

                if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                    if (visited_opponent_arr[y * 10 + x] != 0) {
                        return false; // Another ship is too close
                    }
                }
            }
        }

        return true; // Ship can be placed
    }

    private void placeShip(int startX, int startY, int shipSize, boolean horizontal) {
        for (int i = 0; i < shipSize; i++) {
            int x = horizontal ? startX + i : startX;
            int y = horizontal ? startY : startY + i;
            visited_opponent_arr[y * 10 + x] = shipSize; // Mark the ship size on the board
        }
    }

    private void createButtonsForBoards() {
        TableLayout tableLayout;
        ImageView imageButton;
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        // create ImageView components (buttons) for game board
        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                tableLayout = findViewById(R.id.yourButtonsPanel_id);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.setGravity(Gravity.CENTER);
                for (int k = 0; k < 11; k++) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(60,
                            60)));
                    textView.setPadding(5, 5, 5, 5);
                    if (k == 0) {
                        textView.setText(" ");
                    } else {
                        textView.setText(numbers[k - 1]);
                        textView.setGravity(Gravity.CENTER);
                    }
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow, new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                continue;
            }
            tableLayout = findViewById(R.id.yourButtonsPanel_id);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT)));
                    textView.setText(letters[i - 1]);
                    textView.setPadding(5, 5, 5, 5);
                    tableRow.addView(textView);
                    continue;
                }
                imageButton = new ImageView(this);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                imageButton.setId((i - 1) * 10 + (j - 1));
                imageButton.setTag(String.valueOf((i - 1) * 10 + (j - 1)));
                setIconToButton(imageButton, visited_your_arr[(i - 1) * 10 + (j - 1)]);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                tableRow.addView(imageButton);
                imageViewList.add(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }

        // Setup ships for opponent
        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                tableLayout = findViewById(R.id.opponentButtonsPanel_id);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.setGravity(Gravity.CENTER);
                for (int k = 0; k < 11; k++) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(60,
                            60)));
                    textView.setPadding(5, 5, 5, 5);
                    if (k == 0) {
                        textView.setText(" ");
                    } else {
                        textView.setText(numbers[k - 1]);
                        textView.setGravity(Gravity.CENTER);
                    }
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow, new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                continue;
            }
            tableLayout = findViewById(R.id.opponentButtonsPanel_id);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT)));
                    textView.setText(letters[i - 1]);
                    textView.setPadding(5, 5, 5, 5);
                    tableRow.addView(textView);
                    continue;
                }
                imageButton = new ImageView(this);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                imageButton.setId((i - 1) * 10 + (j - 1) + 100); // Offset for opponent buttons
                imageButton.setTag(String.valueOf((i - 1) * 10 + (j - 1) + 100));

                // comment one
                //imageButton.setImageResource(R.drawable.non_clicked_cell);
                setIconToButton(imageButton, visited_opponent_arr[(i - 1) * 10 + (j - 1)]);

                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                tableRow.addView(imageButton);
                imageViewList.add(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }
}
