package com.alderferstudios.budgetsplitter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;

/**
 * Main window of the Budget Splitter
 *
 * @author Ben Alderfer
 * Alderfer Studios
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The starting date
     */
    private int startYear, startMonth, startDay;

    /**
     * The ending date
     */
    private int endYear, endMonth, endDay;

    /**
     * week and day difference between dates
     */
    private int weekDiff, dayDiff, currentWeekDiff, currentDayDiff;

    /**
     * Results cards
     */
    private CardView initialCard, currentCard;

    /**
     * The results TextViews
     *
     * 0 - Initial Header
     * 1 - Initial Daily Line Header
     * 2 - Initial Daily
     * 3 - Initial Weekly Line Header
     * 4 - Initial Weekly
     *
     * 5 - Current Header
     * 6 - Diff Line Header
     * 7 - Diff
     * 8 - Current Daily Line Header
     * 9 - Current Daily
     * 10 - Current Weekly Line Header
     * 11 - Current Weekly
     * */
    private TextView[] tvs = new TextView[12];

    /**
     * Start and end date TextViews
     */
    private TextView startDateText, endDateText;

    /**
     * The EditText fields
     */
    private EditText initalBalanceEditText, currentBalanceEditText, totalDaysOffEditText, currentDaysOffEditText;

    /**
     * The text input in the fields
     */
    private String initalBalance, currentBalance = "", totalDaysOff = "", currentDaysOff = "";

    /**
     * Whether the fields have been entered
     */
    private boolean initialBalanceIsEntered, currentBalanceIsEntered, currentDateIsInRange;

    /**
     * Either start or end date
     */
    private String dateBeingSet;

    /**
     * Context reference for later
     */
    private Context c;

    /**
     * Checks if the device is a tablet
     *
     * @param context the Context
     * @return true if a tablet
     */
    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

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
        JodaTimeAndroid.init(this);

        c = this;

        if (!isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        bar.setTitle(R.string.app_name);

        initalBalanceEditText = (EditText) findViewById(R.id.initialBalanceText);
        currentBalanceEditText = (EditText) findViewById(R.id.currentBalanceText);
        startDateText = (TextView) findViewById(R.id.startDate);
        endDateText = (TextView) findViewById(R.id.endDate);
        totalDaysOffEditText = (EditText) findViewById(R.id.totalDaysOffText);
        currentDaysOffEditText = (EditText) findViewById(R.id.currentDaysOffText);

        tvs[0] = (TextView) findViewById(R.id.initialHeader);
        tvs[1] = (TextView) findViewById(R.id.initDaily);
        tvs[2] = (TextView) findViewById(R.id.initDailyText);
        tvs[3] = (TextView) findViewById(R.id.initWeekly);
        tvs[4] = (TextView) findViewById(R.id.initWeeklyText);

        tvs[5] = (TextView) findViewById(R.id.currentHeader);
        tvs[6] = (TextView) findViewById(R.id.diff);
        tvs[7] = (TextView) findViewById(R.id.diffText);
        tvs[8] = (TextView) findViewById(R.id.currentDaily);
        tvs[9] = (TextView) findViewById(R.id.currentDailyText);
        tvs[10] = (TextView) findViewById(R.id.currentWeekly);
        tvs[11] = (TextView) findViewById(R.id.currentWeeklyText);

        initialCard = (CardView) findViewById(R.id.initialCard);
        currentCard = (CardView) findViewById(R.id.currentCard);

        clearResults();
        addTextListeners();

        startMonth = Integer.parseInt(getString(R.string.startMonth));
        startDay = Integer.parseInt(getString(R.string.startDay));
        startYear = Integer.parseInt(getString(R.string.startYear));
        endMonth = Integer.parseInt(getString(R.string.endMonth));
        endDay = Integer.parseInt(getString(R.string.endDay));
        endYear = Integer.parseInt(getString(R.string.endYear));

        startDateText.setText(startMonth + "/" + startDay + "/" + startYear);
        endDateText.setText(endMonth + "/" + endDay + "/" + endYear);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {
            }
        });

        currentBalanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentBalance = currentBalanceEditText.getText().toString();
                currentBalanceIsEntered = !currentBalance.equals("");
                if (currentBalanceIsEntered) {
                    //if it ends with a "." remove the "." before getting the number
                    if (currentBalance.length() > 0 && currentBalance.substring(currentBalance.length() - 1, currentBalance.length()).equals(".")) {
                        currentBalance = currentBalance.substring(0, currentBalance.length());
                    }
                    //if current balance > initial, fix that
                    if (!initalBalance.equals("") && !currentBalance.equals("") &&
                            Double.parseDouble(currentBalance) > Double.parseDouble(initalBalance)) {
                        currentBalance = initalBalance;
                        currentBalanceEditText.setText(currentBalance);
                        Toast.makeText(c, R.string.remainingGreaterThanInitial, Toast.LENGTH_LONG).show();
                    }
                }

                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        totalDaysOffEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                totalDaysOff = totalDaysOffEditText.getText().toString();
                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        currentDaysOffEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentDaysOff = currentDaysOffEditText.getText().toString();

                //if total days off is blank and current days off is entered, change total to equal current
                if (totalDaysOff.equals("") && !currentDaysOff.equals("")) {
                    totalDaysOff = currentDaysOff;
                    totalDaysOffEditText.setText(totalDaysOff);
                    Toast.makeText(c, R.string.totalNotEntered, Toast.LENGTH_LONG).show();
                }

                // if current days off > total days off, change it to the total
                if (!totalDaysOff.equals("") && !currentDaysOff.equals("") && Integer.parseInt(currentDaysOff) > Integer.parseInt(totalDaysOff)) {
                    currentDaysOff = totalDaysOff;
                    currentDaysOffEditText.setText(totalDaysOff);
                    Toast.makeText(c, R.string.pastGreaterThanTotal, Toast.LENGTH_LONG).show();
                }

                attemptUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Checks if results can be updated
     * If possible, update
     * If not, clear results
     */
    private void attemptUpdate() {
        if (initialBalanceIsEntered) {
            updateResults();
        } else {
            clearResults();
        }
    }

    /**
     * Updates the results text
     */
    private void updateResults() {
        calculateDateDiff(findViewById(R.id.initialBalanceText));

        initialCard.setVisibility(View.VISIBLE);

        DecimalFormat twoDecimal = new DecimalFormat("0.00");
        double initial = Double.parseDouble(initalBalance);

        double daily, weekly;
        if (weekDiff > 0) {
            daily = initial / ((weekDiff * 7) + dayDiff);
            weekly = daily * 7;
        } else {    //only 1 week or less
            weekly = initial;
            daily = weekly / currentDayDiff;
        }

        tvs[2].setText(twoDecimal.format(daily));
        tvs[4].setText(twoDecimal.format(weekly));

        if (currentDateIsInRange && currentBalanceIsEntered) {
            currentCard.setVisibility(View.VISIBLE);

            double curBalance = Double.parseDouble(currentBalance);

            // current balance - amount that should be left initially
            double diff = curBalance - (weekly * currentWeekDiff + daily * currentDayDiff);

            double currentWeekly, currentDaily;
            if (currentWeekDiff > 0) {
                currentDaily = curBalance / ((currentWeekDiff * 7) + currentDayDiff);
                currentWeekly = currentDaily * 7;
            } else {    //only 1 week or less
                currentWeekly = curBalance;
                currentDaily = currentWeekly / currentDayDiff;
            }

            if (diff > 0) {
                tvs[7].setText("+" + twoDecimal.format(diff));
            } else {
                tvs[7].setText(twoDecimal.format(diff));
            }

            tvs[9].setText(twoDecimal.format(currentDaily));
            tvs[11].setText(twoDecimal.format(currentWeekly));
        } else { //if it can't be displayed, make sure its hidden
            currentCard.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * If the results cannot be updated, then nothing should be displayed
     * Makes all TextViews invisible
     */
    private void clearResults() {
        initialCard.setVisibility(View.INVISIBLE);
        currentCard.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets the start date
     */
    public void setStartDate(View v) {
        dateBeingSet = "start";
        Intent popUp = new Intent(this, CalendarDialog.class);
        startActivityForResult(popUp, 1);
    }

    /**
     * Sets the end date
     */
    public void setEndDate(View v) {
        dateBeingSet = "end";
        Intent popUp = new Intent(this, CalendarDialog.class);
        startActivityForResult(popUp, 1);
    }

    /**
     * When the pop up returns a date
     * Months are reported as 1 off so 1 added
     *
     * @param requestCode - useless
     * @param resultCode  - useless
     * @param data        - the Intent with the date
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {    //only gets info if they didn't press back
            if (dateBeingSet.equals("start")) {
                startYear = data.getIntExtra("year", 2016);
                startMonth = data.getIntExtra("month", 1) + 1;
                startDay = data.getIntExtra("day", 25);
                startDateText.setText(startMonth + "/" + startDay + "/" + startYear);
                attemptUpdate();
            } else {
                endYear = data.getIntExtra("year", 2016);
                endMonth = data.getIntExtra("month", 1) + 1;
                endDay = data.getIntExtra("day", 25);
                endDateText.setText(endMonth + "/" + endDay + "/" + endYear);
                attemptUpdate();
            }
        }
    }

    /**
     * Calculates the difference in the two dates
     */
    public void calculateDateDiff(View v) {
        DateTimeZone Eastern = DateTimeZone.forID("America/New_York");
        DateTime start = new DateTime(startYear, startMonth, startDay, 0, 0, 0, Eastern);
        DateTime end = new DateTime(endYear, endMonth, endDay, 0, 0, 0, Eastern);
        dayDiff = Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();

        int totalDaysOffNumber = 0;
        if (!totalDaysOff.equals("")) {
            totalDaysOffNumber = Integer.parseInt(totalDaysOff);
            dayDiff -= totalDaysOffNumber;
        }

        /**
         * if end date is not at least 1 day after the start after removing days off
         * change the end date to be 1 month after start + number of months in days off
         */
        if (dayDiff < 1) {
            endYear = startYear;
            endMonth = startMonth + 1 + (totalDaysOffNumber / 29);
            if (endMonth > 12) {
                endMonth -= 12;
                ++endYear;
            }

            endDay = startDay;
            endDateText.setText(endMonth + "/" + endDay + "/" + endYear);
            Toast.makeText(this, R.string.endDateBeforeStart, Toast.LENGTH_LONG).show();
            attemptUpdate();
            return;
        }

        weekDiff = dayDiff / 7;
        dayDiff -= weekDiff * 7;

        LocalDate localDate = new LocalDate();
        String currentDate = localDate.toString();
        String currentYear = currentDate.substring(0, 4);
        String currentMonth = currentDate.substring(5, 7);
        String currentDay = currentDate.substring(8, 10);
        int year = 0, month = 0, day = 0;
        if (!currentYear.equals("")) {
            year = Integer.parseInt(currentYear);
        }
        if (!currentMonth.equals("")) {
            month = Integer.parseInt(currentMonth);
        }
        if (!currentDay.equals("")) {
            day = Integer.parseInt(currentDay);
        }

        //check year
        if (year <= endYear && year >= startYear) {
            //check month
            if (month <= endMonth && month >= startMonth) {
                //if month not equal, no need to check day (default)
                currentDateIsInRange = true;

                //if a month is equal, check day
                if (month == endMonth) {
                    if (day > endDay) {
                        currentDateIsInRange = false;
                    }
                } else if (month == startMonth) {
                    if (day < startDay) {
                        currentDateIsInRange = false;
                    }
                }
            } else {
                currentDateIsInRange = false;
            }
        } else {
            currentDateIsInRange = false;
        }

        if (currentDateIsInRange) {
            currentDayDiff = Days.daysBetween(localDate, end.toLocalDate()).getDays();

            //subtract the total days off like before but add back any days that have passed
            if (!totalDaysOff.equals("")) {
                currentDayDiff -= Integer.parseInt(totalDaysOff);
            }
            if (!currentDaysOff.equals("")) {
                currentDayDiff += Integer.parseInt(currentDaysOff);
            }

            currentWeekDiff = currentDayDiff / 7;
            currentDayDiff -= currentWeekDiff * 7;
        } else if (currentBalanceIsEntered) {
            Toast.makeText(this, getString(R.string.dateOutOfRangeMessage), Toast.LENGTH_LONG).show();
        }
    }
}