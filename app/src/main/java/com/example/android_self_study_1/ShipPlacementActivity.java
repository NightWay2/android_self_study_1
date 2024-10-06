package com.example.android_self_study_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
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

public class ShipPlacementActivity extends AppCompatActivity {

    private int[] visited_arr;
    private int[] clicked_cells_arr;

    private int selectedTypeOfObject;
    private int onePartShipsNum;
    private int twoPartShipsNum;
    private int threePartShipsNum;
    private int fourPartShipsNum;

    private int clicksInARow;

    public static final String VISITED_ARR = "VISITED_ARR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ship_placement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.relativeLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        onePartShipsNum = 4;
        twoPartShipsNum = 3;
        threePartShipsNum = 2;
        fourPartShipsNum = 1;

        clicksInARow = 0;
        selectedTypeOfObject = -1;

        TextView textViewOnePartShipsNum = findViewById(R.id.numberOfOnePartShips_id);
        TextView textViewTwoPartShipsNum = findViewById(R.id.numberOfTwoPartShips_id);
        TextView textViewThreePartShipsNum = findViewById(R.id.numberOfThreePartShips_id);
        TextView textViewFourPartShipsNum = findViewById(R.id.numberOfFourPartShips_id);

        ImageButton buttonOnePartShips = findViewById(R.id.onePartShips_id);
        ImageButton buttonTwoPartShips = findViewById(R.id.twoPartShips_id);
        ImageButton buttonThreePartShips = findViewById(R.id.threePartShips_id);
        ImageButton buttonFourPartShips = findViewById(R.id.fourPartShips_id);

        Button buttonContinue = findViewById(R.id.continueButton_id);
        buttonContinue.setEnabled(false);

        findViewById(R.id.onePartShips_id).setOnClickListener(butOnePartShips -> {
            butOnePartShips.setBackgroundColor(getResources().getColor(R.color.orange));
            buttonTwoPartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            buttonThreePartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            buttonFourPartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            selectedTypeOfObject = 1;
        });

        findViewById(R.id.twoPartShips_id).setOnClickListener(butTwoPartShips -> {
            buttonOnePartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            butTwoPartShips.setBackgroundColor(getResources().getColor(R.color.orange));
            buttonThreePartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            buttonFourPartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            selectedTypeOfObject = 2;
        });

        findViewById(R.id.threePartShips_id).setOnClickListener(butThreePartShips -> {
            buttonOnePartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            buttonTwoPartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            butThreePartShips.setBackgroundColor(getResources().getColor(R.color.orange));
            buttonFourPartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            selectedTypeOfObject = 3;
        });

        findViewById(R.id.fourPartShips_id).setOnClickListener(butFourPartShips -> {
            buttonOnePartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            buttonTwoPartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            buttonThreePartShips.setBackgroundColor(getResources().getColor(R.color.gray));
            butFourPartShips.setBackgroundColor(getResources().getColor(R.color.orange));
            selectedTypeOfObject = 4;
        });

        findViewById(R.id.continueButton_id).setOnClickListener(butContinue -> {
            Intent intent = new Intent(this, WarMapActivity.class);
            intent.putExtra(VISITED_ARR, visited_arr);
            startActivity(intent);
        });

        visited_arr = new int[10 * 10];

        TableLayout tableLayout;
        ImageView imageButton;
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        // create ImageView components (buttons) for game board
        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                tableLayout = findViewById(R.id.buttonsPanel_id);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
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
            tableLayout = findViewById(R.id.buttonsPanel_id);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT)));
                    textView.setText(letters[i - 1]);
                    tableRow.addView(textView);
                    continue;
                }
                imageButton = new ImageView(this);
                imageButton.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                imageButton.setId((i - 1) * 10 + (j - 1));
                visited_arr[(i - 1) * 10 + (j - 1)] = 0;
                imageButton.setImageResource(R.drawable.non_clicked_cell);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // handler for CLICK on this image (button)
                imageButton.setOnClickListener(v -> {
                    ImageView iView = (ImageView) v;

                    // TODO fix bug with round ships

                    // if one-part ship was selected to place
                    if (selectedTypeOfObject == 1 && visited_arr[iView.getId()] == 0
                            && onePartShipsNum > 0) {
                        setIconToButton(iView, 1);
                        visited_arr[iView.getId()] = 1;
                        onePartShipsNum--;
                        clicksInARow = 0;
                        textViewOnePartShipsNum.setText(String.format("%d", onePartShipsNum));
                        int id = iView.getId();
                        reserveNeighbourCells(id);
                        if (checkIfEnd()) {
                            buttonContinue.setEnabled(true);
                        }
                    }

                    // if two-part ship was selected to place
                    if (selectedTypeOfObject == 2 && visited_arr[iView.getId()] == 0
                            && twoPartShipsNum > 0) {
                        if (clicksInARow <= 0) {
                            clicked_cells_arr = new int[2];
                            buttonOnePartShips.setEnabled(false);
                            buttonThreePartShips.setEnabled(false);
                            buttonFourPartShips.setEnabled(false);
                            clicked_cells_arr[0] = iView.getId();
                            visited_arr[iView.getId()] = 2;
                            clicksInARow++;
                            setIconToButton(iView, 2);
                        } else {
                            if (checkIfIsPartOfShip(clicked_cells_arr, iView.getId())) {
                                clicksInARow = 0;
                                clicked_cells_arr[1] = iView.getId();
                                visited_arr[iView.getId()] = 2;
                                buttonOnePartShips.setEnabled(true);
                                buttonThreePartShips.setEnabled(true);
                                buttonFourPartShips.setEnabled(true);
                                setIconToButton(iView, 2);
                            } else {
                                Toast.makeText(this,
                                        "Incorrect cell!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int id : clicked_cells_arr) {
                                reserveNeighbourCells(id);
                            }

                            twoPartShipsNum--;
                            textViewTwoPartShipsNum.setText(String.format("%d", twoPartShipsNum));
                            if (checkIfEnd()) {
                                buttonContinue.setEnabled(true);
                            }
                        }
                    }

                    // if three-part ship was selected to place
                    if (selectedTypeOfObject == 3 && visited_arr[iView.getId()] == 0
                            && threePartShipsNum > 0) {
                        if (clicksInARow <= 0) {
                            clicked_cells_arr = new int[3];
                            buttonOnePartShips.setEnabled(false);
                            buttonTwoPartShips.setEnabled(false);
                            buttonFourPartShips.setEnabled(false);
                            clicked_cells_arr[0] = iView.getId();
                            visited_arr[iView.getId()] = 3;
                            clicksInARow++;
                            setIconToButton(iView, 3);
                        } else if (clicksInARow == 1) {
                            if (checkIfIsPartOfShip(clicked_cells_arr, iView.getId())) {
                                clicked_cells_arr[1] = iView.getId();
                                visited_arr[iView.getId()] = 3;
                                clicksInARow++;
                                setIconToButton(iView, 3);
                            } else {
                                Toast.makeText(this,
                                        "Incorrect cell!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (checkIfIsPartOfShip(clicked_cells_arr, iView.getId())) {
                                clicksInARow = 0;
                                clicked_cells_arr[2] = iView.getId();
                                visited_arr[iView.getId()] = 3;
                                buttonOnePartShips.setEnabled(true);
                                buttonTwoPartShips.setEnabled(true);
                                buttonFourPartShips.setEnabled(true);
                                setIconToButton(iView, 3);
                            } else {
                                Toast.makeText(this,
                                        "Incorrect cell!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int id : clicked_cells_arr) {
                                reserveNeighbourCells(id);
                            }

                            threePartShipsNum--;
                            textViewThreePartShipsNum.setText(
                                    String.format("%d", threePartShipsNum));
                            if (checkIfEnd()) {
                                buttonContinue.setEnabled(true);
                            }
                        }
                    }

                    // if four-part ship was selected to place
                    if (selectedTypeOfObject == 4 && visited_arr[iView.getId()] == 0
                            && fourPartShipsNum > 0) {
                        if (clicksInARow <= 0) {
                            clicked_cells_arr = new int[4];
                            buttonOnePartShips.setEnabled(false);
                            buttonTwoPartShips.setEnabled(false);
                            buttonThreePartShips.setEnabled(false);
                            clicked_cells_arr[0] = iView.getId();
                            visited_arr[iView.getId()] = 4;
                            clicksInARow++;
                            setIconToButton(iView, 4);
                        } else if (clicksInARow == 1) {
                            if (checkIfIsPartOfShip(clicked_cells_arr, iView.getId())) {
                                clicked_cells_arr[1] = iView.getId();
                                visited_arr[iView.getId()] = 4;
                                clicksInARow++;
                                setIconToButton(iView, 4);
                            } else {
                                Toast.makeText(this,
                                        "Incorrect cell!", Toast.LENGTH_SHORT).show();
                            }
                        } else if (clicksInARow == 2) {
                            if (checkIfIsPartOfShip(clicked_cells_arr, iView.getId())) {
                                clicked_cells_arr[2] = iView.getId();
                                visited_arr[iView.getId()] = 4;
                                clicksInARow++;
                                setIconToButton(iView, 4);
                            } else {
                                Toast.makeText(this,
                                        "Incorrect cell!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (checkIfIsPartOfShip(clicked_cells_arr, iView.getId())) {
                                clicksInARow = 0;
                                clicked_cells_arr[3] = iView.getId();
                                visited_arr[iView.getId()] = 4;
                                buttonOnePartShips.setEnabled(true);
                                buttonTwoPartShips.setEnabled(true);
                                buttonThreePartShips.setEnabled(true);
                                setIconToButton(iView, 4);
                            } else {
                                Toast.makeText(this,
                                        "Incorrect cell!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int id : clicked_cells_arr) {
                                reserveNeighbourCells(id);
                            }

                            fourPartShipsNum--;
                            textViewFourPartShipsNum.setText(
                                    String.format("%d", fourPartShipsNum));
                            if (checkIfEnd()) {
                                buttonContinue.setEnabled(true);
                            }
                        }
                    }
                });

                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    void setIconToButton(ImageView imageView, int type) {

        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        // TODO change pictures

        if (type == 1) {
            imageView.setImageResource(R.drawable.digit_1);
        } else if (type == 2) {
            imageView.setImageResource(R.drawable.digit_2);
        } else if (type == 3) {
            imageView.setImageResource(R.drawable.digit_3);
        } else if (type == 4) {
            imageView.setImageResource(R.drawable.digit_4);
        } else if (type == 5) {
            imageView.setImageResource(R.drawable.non_clicked_cell);
        }
    }

    boolean checkIfEnd() {
        return onePartShipsNum == 0
                && twoPartShipsNum == 0
                && threePartShipsNum == 0
                && fourPartShipsNum == 0;
    }

    // mark neighbour cells in order to prevent other ship placement
    void reserveNeighbourCells(int id) {
        if (checkIfValidCoord(id, id - 1) && visited_arr[id - 1] == 0) {
            ImageView imageView = findViewById(id - 1);
            visited_arr[id - 1] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id - 1 - 10) && visited_arr[id - 1 - 10] == 0) {
            ImageView imageView = findViewById(id - 1 - 10);
            visited_arr[id - 1 - 10] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id - 10) && visited_arr[id - 10] == 0) {
            ImageView imageView = findViewById(id - 10);
            visited_arr[id - 10] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id - 10 + 1) && visited_arr[id - 10 + 1] == 0) {
            ImageView imageView = findViewById(id - 10 + 1);
            visited_arr[id - 10 + 1] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id + 1) && visited_arr[id + 1] == 0) {
            ImageView imageView = findViewById(id + 1);
            visited_arr[id + 1] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id + 1 + 10) && visited_arr[id + 1 + 10] == 0) {
            ImageView imageView = findViewById(id + 1 + 10);
            visited_arr[id + 1 + 10] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id + 10) && visited_arr[id + 10] == 0) {
            ImageView imageView = findViewById(id + 10);
            visited_arr[id + 10] = 5;
            setIconToButton(imageView, 5);
        }
        if (checkIfValidCoord(id, id - 1 + 10) && visited_arr[id - 1 + 10] == 0) {
            ImageView imageView = findViewById(id - 1 + 10);
            visited_arr[id - 1 + 10] = 5;
            setIconToButton(imageView, 5);
        }
    }

    boolean checkIfValidCoord(int firstParam, int num) {
        // transform num into x and y coordinates
        int x = num % 10;
        int y = num / 10;
        int x_prev = firstParam % 10;

        // check if cell is out of range
        return x >= 0 && y >= 0 && x < 10 && y < 10 &&
                (x != 0 || x_prev != (10 - 1)) && (x != (10 - 1) || x_prev != 0);
    }

    boolean checkIfIsPartOfShip(int[] clicked_cells_arr, int last) {
        for(int i = 0; i < clicked_cells_arr.length-1; i++) {
            int x_prev = clicked_cells_arr[i] / 10;
            int y_prev = clicked_cells_arr[i] % 10;

            int x_last = last / 10;
            int y_last = last % 10;

            if ((((x_last - 1) == x_prev) && (y_last == y_prev)) || (((x_last + 1) == x_prev)
                    && (y_last == y_prev)) || (((y_last - 1) == y_prev)
                    && (x_last == x_prev)) || (((y_last + 1) == y_prev) && (x_last == x_prev))) {
                return true;
            }
        }
        return false;
    }
}