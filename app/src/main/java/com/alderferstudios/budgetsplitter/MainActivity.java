package com.alderferstudios.budgetsplitter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

/**
 * Main window of the Budget Splitter
 *
 * @author Ben Alderfer
 * Alderfer Studios
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The number corresponding to the number of days
     * Since the number is calculated at the end of the week,
     * This number is how many days to add back
     */
    private int day = 7; //monday by default

    /** The results TextViews */
    private TextView initialResultsHeader, initialResultsText, currentResultsHeader, currentResultsText;

    /** The EditText fields */
    private EditText initalBalanceEditText, numWeeksEditText, currentWeekEditText, currentBalanceEditText;

    /** The text input in the fields */
    private String initalBalance, numWeeks, currentWeek, currentBalance;

    /** Whether the fields have been entered */
    private boolean initialBalanceIsEntered, numWeeksIsEntered, currentWeeksIsEntered, currentBalanceIsEntered;

    /**
     * Sets up the toolbar
     * Saves the Views for later
     * Hides the results headers
     * Adds listeners to the fields and buttons
     * @param savedInstanceState - the previous state (not used)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        bar.setTitle(R.string.app_name);

        initalBalanceEditText = (EditText) findViewById(R.id.initialBalanceText);
        numWeeksEditText = (EditText) findViewById(R.id.numWeeksText);
        currentWeekEditText = (EditText) findViewById(R.id.currentWeekText);
        currentBalanceEditText = (EditText) findViewById(R.id.currentBalanceText);

        initialResultsHeader = (TextView) findViewById(R.id.initalHeader);
        initialResultsText = (TextView) findViewById(R.id.initalText);
        currentResultsHeader = (TextView) findViewById(R.id.currentHeader);
        currentResultsText = (TextView) findViewById(R.id.currentText);

        initialResultsHeader.setVisibility(View.INVISIBLE);
        currentResultsHeader.setVisibility(View.INVISIBLE);

        addTextListeners();
        addButtonListeners();
    }

    /**
     * Sets up the menu (none for now)
     * @param menu - the menu to set up
     * @return true (useless)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles menu option clicks
     * @param item - the MenuItem that was clicked
     * @return either true or handed off to super class (useless)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds a listener to the EditTexts
     * Text is saved to reduce method calls
     * Updates the results if possible
     */
    private void addTextListeners() {
        initalBalanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initalBalance = initalBalanceEditText.getText().toString();
                //if it ends with a "." remove the "." before getting the number
                if (initalBalance.length() > 0 && initalBalance.substring(initalBalance.length() - 1, initalBalance.length()).equals(".")) {
                    initalBalance = initalBalance.substring(0, initalBalance.length());
                }
                initialBalanceIsEntered = !initalBalance.equals("");
                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        numWeeksEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numWeeks = numWeeksEditText.getText().toString();
                numWeeksIsEntered = !numWeeks.equals("");
                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        currentBalanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentBalance = currentBalanceEditText.getText().toString();
                //if it ends with a "." remove the "." before getting the number
                if (currentBalance.length() > 0 && currentBalance.substring(currentBalance.length() - 1, currentBalance.length()).equals(".")) {
                    currentBalance = currentBalance.substring(0, currentBalance.length());
                }
                //if current balance > initial, fix that
                if (!initalBalance.equals("") && !currentBalance.equals("") &&
                    Double.parseDouble(currentBalance) > Double.parseDouble(initalBalance)) {
                    currentBalance = initalBalance;
                    currentBalanceEditText.setText(currentBalance);
                }
                currentBalanceIsEntered = !currentBalance.equals("");
                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        currentWeekEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentWeek = currentWeekEditText.getText().toString();
                //if current week > num weeks, fix that
                if (!numWeeks.equals("") && !currentWeek.equals("") &&
                        Integer.parseInt(currentWeek) > Integer.parseInt(numWeeks)) {
                    currentWeek = numWeeks;
                    currentWeekEditText.setText(currentWeek);
                }
                currentWeeksIsEntered = !currentWeek.equals("");
                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Adds a listener to the radio group
     * Updates the current day to the corresponding number
     */
    private void addButtonListeners() {
        final RadioGroup  radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.mondayButton:
                        day = 7;
                        break;
                    case R.id.tuesdayButton:
                        day = 6;
                        break;
                    case R.id.wednesdayButton:
                        day = 5;
                        break;
                    case R.id.thursdayButton:
                        day = 4;
                        break;
                    case R.id.fridayButton:
                        day = 3;
                        break;
                    case R.id.saturdayButton:
                        day = 2;
                        break;
                    case R.id.sundayButton:
                        day = 1;
                        break;
                }
                attemptUpdate();
            }
        });
    }

    /**
     * Checks if results can be updated
     * If possible, update
     * If not, clear results
     */
    private void attemptUpdate() {
        if (canUpdateResults()) {
            updateResults();
        } else {
            clearResults();
        }
    }

    /**
     * Checks if the results can be updated
     * Only the first 2 are necessary
     * @return true if the initial balance and number of weeks have been entered
     */
    private boolean canUpdateResults() {
        return initialBalanceIsEntered && numWeeksIsEntered;
    }

    /**
     * Updates the results text
     */
    private void updateResults() {
        initialResultsHeader.setVisibility(View.VISIBLE);

        String initialResults = "";
        DecimalFormat twoDecimal = new DecimalFormat("#.00");
        double initial = Double.parseDouble(initalBalance);
        int weeks = Integer.parseInt(numWeeks);

        double weekly = initial / weeks;
        double daily = weekly / 7;
        initialResults += getString(R.string.daily) + " " + twoDecimal.format(daily) + '\n' +
                   getString(R.string.weekly) + " " + twoDecimal.format(weekly);

        initialResultsText.setText(initialResults);

        String currentResults = "";
        if (currentWeeksIsEntered && currentBalanceIsEntered) {
            currentResultsHeader.setVisibility(View.VISIBLE);

            double curBalance = Double.parseDouble(currentBalance);
            int curWeeks = Integer.parseInt(currentWeek);

            // current balance - amount that should be spent + number of remaining days in the week * daily rate
            double diff = curBalance - (initial - weekly * curWeeks + day * daily);

            int weekDiff = weeks - curWeeks;
            double currentWeekly;
            if (weekDiff > 0) {
                currentWeekly = curBalance / (weekDiff + day / 7.0);
            } else {
                currentWeekly = curBalance;
            }

            double currentDaily = currentWeekly / 7;

            currentResults += getString(R.string.difference) + " ";

            if (diff > 0) {
                currentResults += "+" + twoDecimal.format(diff);
            } else {
                currentResults += twoDecimal.format(diff);
            }

            currentResults += '\n' + getString(R.string.daily) + " " + twoDecimal.format(currentDaily) + '\n' +
                       getString(R.string.weekly) + " " + twoDecimal.format(currentWeekly);
        }

        currentResultsText.setText(currentResults);
    }

    /**
     * If the results cannot be updated, then nothing should be displayed
     */
    private void clearResults() {
        initialResultsHeader.setVisibility(View.INVISIBLE);
        currentResultsHeader.setVisibility(View.INVISIBLE);
        initialResultsText.setText("");
        currentResultsText.setText("");
    }
}
