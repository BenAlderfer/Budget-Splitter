package com.alderferstudios.budgetsplitter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private String day;
    private EditText initalBalanceEditText, numWeeksEditText, currentWeekEditText, currentBalanceEditText;
    private String initalBalance, numWeeks, currentWeek, currentBalance;
    private boolean initialBalanceIsEntered, numWeeksIsEntered, currentWeeksIsEntered, currentBalanceIsEntered;

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

        addTextListeners();
        addButtonListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
                if (!initalBalance.equals("")) {
                    initialBalanceIsEntered = true;
                    if (canUpdateResults()) {
                        updateResults();
                    }
                } else {
                    initialBalanceIsEntered = false;
                }
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
                if (!numWeeks.equals("")) {
                    numWeeksIsEntered = true;
                    if (canUpdateResults()) {
                        updateResults();
                    }
                } else {
                    numWeeksIsEntered = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        currentWeekEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentWeek = currentWeekEditText.getText().toString();
                if (!currentWeek.equals("")) {
                    currentWeeksIsEntered = true;
                    if (canUpdateResults()) {
                        updateResults();
                    }
                } else {
                    currentWeeksIsEntered = false;
                }
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
                if (!currentBalance.equals("")) {
                    currentBalanceIsEntered = true;
                    if (canUpdateResults()) {
                        updateResults();
                    }
                } else {
                    currentBalanceIsEntered = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Adds a listener to the radio group
     * Updates the current day
     */
    private void addButtonListeners() {
        final RadioGroup  radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.mondayButton:
                        day = "Monday";
                        break;
                    case R.id.tuesdayButton:
                        day = "Tuesday";
                        break;
                    case R.id.wednesdayButton:
                        day = "Wednesday";
                        break;
                    case R.id.thursdayButton:
                        day = "Thursday";
                        break;
                    case R.id.fridayButton:
                        day = "Friday";
                        break;
                    case R.id.saturdayButton:
                        day = "Saturday";
                        break;
                    case R.id.sundayButton:
                        day = "Sunday";
                        break;
                }
            }
        });
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
        String results = "";
        DecimalFormat twoDecimal = new DecimalFormat("#.00");
        double initial = Double.parseDouble(initalBalance);
        int weeks = Integer.parseInt(numWeeks);

        double weekly = initial / weeks;
        double daily = weekly / 7;
        results += getString(R.string.daily) + " " + twoDecimal.format(daily) + '\n' +
                   getString(R.string.weekly) + " " + twoDecimal.format(weekly);

        if (currentWeeksIsEntered && currentBalanceIsEntered) {
            double curBalance = Double.parseDouble(currentBalance);
            int curWeeks = Integer.parseInt(currentWeek);

            double diff = curBalance - (initial - weekly * curWeeks);
            double currentWeekly = curBalance / (weeks - curWeeks);
            double currentDaily = currentWeekly / 7;

            results += "\n\n" + getString(R.string.difference) + " ";

            if (diff > 0) {
                results += "+" + twoDecimal.format(diff);
            } else {
                results += twoDecimal.format(diff);
            }

            results += '\n' + getString(R.string.currentDaily) + " " + twoDecimal.format(currentDaily) + '\n' +
                       getString(R.string.currentWeekly) + " " + twoDecimal.format(currentWeekly);
        }

        ((TextView) findViewById(R.id.results)).setText(results);
    }
}
