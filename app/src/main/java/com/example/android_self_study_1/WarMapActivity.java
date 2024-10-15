package com.example.android_self_study_1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private ImageView imageView;
    private TextView textViewYourBoard;
    private TextView textViewOpponentBoard;

    private static final List<ImageView> imageViewList = new ArrayList<>();

    private int numOfYourRuinsLeft;
    private int numOfOpponentRuinsLeft;

    private static final boolean ARE_OPPONENT_SHIPS_VISIBLE = false;

    ConstraintLayout main;

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
        main = findViewById(R.id.war_map_activity_id);

        textViewOpponentBoard = findViewById(R.id.opponentBoard_id);
        textViewYourBoard = findViewById(R.id.yourBoard_id);

        visited_your_arr = getIntent().getIntArrayExtra(ShipPlacementActivity.VISITED_ARR);

        numOfYourRuinsLeft = 20;
        numOfOpponentRuinsLeft = 20;

        placeOpponentShips(); // Place opponent's ships

        isYourMove = true; // Player starts first
        textViewMove = findViewById(R.id.textViewMove);
    }

    private void setIconToButton(ImageView imageView, int type) {
        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (type == 1) {
            imageView.setImageResource(R.drawable.digit_1);
        } else if (type == 2) {
            imageView.setImageResource(R.drawable.digit_2);
        } else if (type == 3) {
            imageView.setImageResource(R.drawable.digit_3);
        } else if (type == 4) {
            imageView.setImageResource(R.drawable.digit_4);
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
        int[] shipSizes = {1, 1, 1, 1, 2, 2, 2, 3, 3, 4};

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

                // comment one of them to change visibility of opponent ships
                if (!ARE_OPPONENT_SHIPS_VISIBLE) {
                    imageButton.setImageResource(R.drawable.non_clicked_cell);
                } else {
                    setIconToButton(imageButton, visited_opponent_arr[(i - 1) * 10 + (j - 1)]);
                }

                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                imageButton.setOnClickListener(v -> {
                    if (isYourMove) {
                        int index = Integer.parseInt((String) v.getTag());
                        //Toast.makeText(WarMapActivity.this, "Натиснуто: " + index, Toast.LENGTH_SHORT).show();
                        checkShot(index, v);
                    } else {
                        Toast.makeText(WarMapActivity.this, "Not your turn",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                tableRow.addView(imageButton);
                imageViewList.add(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private final int[] our_shots = new int[100];
    private final int[] opponent_shots = new int[100];

    private void checkShot(int indexOfShot, View v) {
        // first shot always us, so after first shot we just start bot shot if we missed
        indexOfShot = indexOfShot % 100;
        if (our_shots[indexOfShot] == 0) {
            our_shots[indexOfShot] = visited_opponent_arr[indexOfShot];
            if (visited_opponent_arr[indexOfShot] == 0) {
                our_shots[indexOfShot] = 5;
                setRuinToButton((ImageView) v, 5); // empty ruin
                isYourMove = false;
                textViewMove.setText("Bot`s turn");
                textViewMove.setTextColor(Color.argb(255, 250, 2, 2));
                botTurn();
            } else {
                our_shots[indexOfShot] = visited_opponent_arr[indexOfShot];
                setRuinToButton((ImageView) v, our_shots[indexOfShot]);
                numOfOpponentRuinsLeft--;
                if (numOfOpponentRuinsLeft == 0) {
                    Toast.makeText(WarMapActivity.this, "You are winner!",
                            Toast.LENGTH_SHORT).show();
                    textViewMove.setText("You are winner!");
                    gameEnd();
                }
            }
            /*Toast.makeText(WarMapActivity.this, "Index: " + our_shots[indexOfShot],
                    Toast.LENGTH_SHORT).show();*/
        } else {
            Toast.makeText(WarMapActivity.this, "Already shot!", Toast.LENGTH_SHORT).show();
        }
    }

    private void botTurn() {
        int indexOfShot = new Random().nextInt(100);
        if (opponent_shots[indexOfShot] == 0) {
            if (visited_your_arr[indexOfShot] != 5 && visited_your_arr[indexOfShot] != 0) {
                opponent_shots[indexOfShot] = visited_your_arr[indexOfShot];
                ImageView targetButton = imageViewList.get(indexOfShot);
                setRuinToButton(targetButton, opponent_shots[indexOfShot]);
                numOfYourRuinsLeft--;
                botNextShot(indexOfShot); //TODO possibility to upgrade logic if someone wants
                /*Toast.makeText(WarMapActivity.this, "Index bot: " + visited_your_arr[indexOfShot],
                        Toast.LENGTH_SHORT).show();*/
                if (numOfYourRuinsLeft == 0) {
                    Toast.makeText(WarMapActivity.this, "Bot is winner!",
                            Toast.LENGTH_SHORT).show();
                    textViewMove.setText("Bot is winner!");
                    gameEnd();
                }
            } else {
                opponent_shots[indexOfShot] = 5;
                ImageView targetButton = imageViewList.get(indexOfShot);
                setRuinToButton(targetButton, opponent_shots[indexOfShot]);
                textViewMove.setText("Your turn");
                textViewMove.setTextColor(Color.argb(255, 65, 179, 30));
                isYourMove = true;
                /*Toast.makeText(WarMapActivity.this, "Index bot miss: " + visited_your_arr[indexOfShot],
                        Toast.LENGTH_SHORT).show();*/
            }
        } else {
            botTurn();
        }
    }

    private void botNextShot(int indexOfPreviousShot) {
        List<Integer> possibleShots = getPossibleShots(indexOfPreviousShot);

        for (int shot : possibleShots) {
            if (opponent_shots[shot] == 0) {
                if (visited_your_arr[shot] != 5 && visited_your_arr[shot] != 0) {
                    opponent_shots[shot] = visited_your_arr[shot];
                    ImageView targetButton = imageViewList.get(shot);
                    setRuinToButton(targetButton, opponent_shots[shot]);
                    numOfYourRuinsLeft--;

                    if (numOfYourRuinsLeft == 0) {
                        Toast.makeText(WarMapActivity.this, "Bot is winner!", Toast.LENGTH_SHORT).show();
                        textViewMove.setText("Bot is winner!");
                        gameEnd();
                    } else {
                        botNextShot(shot);
                    }
                    return;
                } else {
                    opponent_shots[shot] = 5;
                    ImageView targetButton = imageViewList.get(shot);
                    setRuinToButton(targetButton, opponent_shots[shot]);
                    textViewMove.setText("Your turn");
                    textViewMove.setTextColor(Color.argb(255, 65, 179, 30));
                    isYourMove = true;
                    return;
                }
            }
        }
        botTurn();
    }

    private List<Integer> getPossibleShots(int indexOfPreviousShot) {
        List<Integer> possibleShots = new ArrayList<>();

        if (indexOfPreviousShot % 10 != 0) {
            possibleShots.add(indexOfPreviousShot - 1);
        }
        if (indexOfPreviousShot % 10 != 9) {
            possibleShots.add(indexOfPreviousShot + 1);
        }
        if (indexOfPreviousShot >= 10) {
            possibleShots.add(indexOfPreviousShot - 10);
        }
        if (indexOfPreviousShot < 90) {
            possibleShots.add(indexOfPreviousShot + 10);
        }

        return possibleShots;
    }

    private void gameEnd() {
        isYourMove = false;
        showInfo();
    }

    private void showInfo() {
        View view = View.inflate(WarMapActivity.this, R.layout.pop_up_info, null);
        Button restart = view.findViewById(R.id.restartButton);
        TextView winnerText = view.findViewById(R.id.winnerTextView); // Отримання TextView з view

        PopupWindow popupWindow = new PopupWindow(view, 800, 850, false);
        popupWindow.showAtLocation(main, Gravity.CENTER, 0, 0); // Переконайся, що 'main' ініціалізовано

        if (numOfYourRuinsLeft == 0) {
            winnerText.setText("Bot won!");
            winnerText.setTextColor(Color.argb(255, 250, 2, 2));
        } else {
            winnerText.setText("You won!");
            winnerText.setTextColor(Color.argb(255, 65, 179, 30));
        }

        restart.setOnClickListener(c -> {
            Intent intent = new Intent(WarMapActivity.this, ShipPlacementActivity.class);
            startActivity(intent);
            popupWindow.dismiss();
        });
    }


    private void setRuinToButton(ImageView imageView, int type) {
        if (imageView == null) {
            Log.d("setRuinToButton", "Button was not found!");
            return;
        }

        if (type == 1) {
            imageView.setImageResource(R.drawable.digit_1_ruin);
        } else if (type == 2) {
            imageView.setImageResource(R.drawable.digit_2_ruin);
        } else if (type == 3) {
            imageView.setImageResource(R.drawable.digit_3_ruin);
        } else if (type == 4) {
            imageView.setImageResource(R.drawable.digit_4_ruin);
        } else if (type == 5 || type == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell_ruin);
        }
    }
}
