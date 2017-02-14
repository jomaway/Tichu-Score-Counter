package de.jomaway.tichuscorecounter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import static android.R.attr.id;
import static android.R.attr.value;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.os.Build.VERSION_CODES.M;

public class SetRoundPointsActivity extends AppCompatActivity {
    public final static String EXTRA_TEAM_A_SCORE = "de.jomaway.tichuscorecounter.TEAM_A_SCORE";
    public final static String EXTRA_TEAM_B_SCORE = "de.jomaway.tichuscorecounter.TEAM_B_SCORE";

    private static final String TAG = "SetRoundPointsActivity";
    private static final int TEAM_MIN_SCORE = 0;
    private static final int TEAM_MAX_SCORE = 30;

    NumberPicker numberPickerTeamA;
    NumberPicker numberPickerTeamB;

    private int totalScoreTeamA = 0;
    private int totalScoreTeamB = 0;

    private int extraScoreTeamA = 0;
    private int extraScoreTeamB = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent returnIntent = new Intent();
                //returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_CANCELED,returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_round_points);

        // Add an Back Button to the Action Bar
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        // Set NumberPickers
        Log.d(TAG,"onCreate()");
        numberPickerTeamA = (NumberPicker) findViewById(R.id.teamA_numberPicker);
        numberPickerTeamB = (NumberPicker) findViewById(R.id.teamB_numberPicker);

        // Block Soft Keyboard
        numberPickerTeamA.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerTeamB.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // Set Team Background Color   - doesn't work is gray
        //numberPickerTeamA.setBackgroundColor(R.color.teamA);
        //numberPickerTeamB.setBackgroundColor(R.color.teamB);

        // Set the MIN and MAX Value of the NumberPickers
        numberPickerTeamA.setMinValue(TEAM_MIN_SCORE);
        numberPickerTeamA.setMaxValue(TEAM_MAX_SCORE);
        numberPickerTeamB.setMinValue(TEAM_MIN_SCORE);
        numberPickerTeamB.setMaxValue(TEAM_MAX_SCORE);


        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                value = map(value);  // Get Range from -25 to 125
                return "" + value;
            }
        };

        numberPickerTeamA.setFormatter(formatter);
        numberPickerTeamB.setFormatter(formatter);

        // Gets wheter the selector wheel wraps reaching the min/max value
        numberPickerTeamA.setWrapSelectorWheel(false);
        numberPickerTeamB.setWrapSelectorWheel(false);

        // Set current Value
        numberPickerTeamA.setValue(15);  // 15 * 5 -25 = 50
        numberPickerTeamB.setValue(15);

        numberPickerTeamA.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d("t","on value change"+ newVal);
                numberPickerTeamB.setValue(TEAM_MAX_SCORE - numberPickerTeamA.getValue());
            }
        });

        numberPickerTeamB.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberPickerTeamA.setValue(TEAM_MAX_SCORE - numberPickerTeamB.getValue());
            }
        });
    }

    // takes the numberpicker Value and maps it to the score value
    private int map(int np_value) {
        return (np_value * 5 - 25);
    }

    private void getCheckBoxResults() {
        CheckBox cbAwT = (CheckBox)findViewById(R.id.teamA_wonT);
        CheckBox cbAlT = (CheckBox)findViewById(R.id.teamA_lostT);
        CheckBox cbAwGT = (CheckBox)findViewById(R.id.teamA_wonGT);
        CheckBox cbAlGT = (CheckBox)findViewById(R.id.teamA_lostGT);
        CheckBox cbAo12 = (CheckBox)findViewById(R.id.teamA_out12);

        CheckBox cbBwT = (CheckBox)findViewById(R.id.teamB_wonT);
        CheckBox cbBlT = (CheckBox)findViewById(R.id.teamB_lostT);
        CheckBox cbBwGT = (CheckBox)findViewById(R.id.teamB_wonGT);
        CheckBox cbBlGT = (CheckBox)findViewById(R.id.teamB_lostGT);
        CheckBox cbBo12 = (CheckBox)findViewById(R.id.teamB_out12);

        int scoreA = 0;
        int scoreB = 0;

        if (cbAwT.isChecked()){
            scoreA += 100;
        } if (cbAlT.isChecked()) {
            scoreA -= 100;
        } if (cbAwGT.isChecked()) {
            scoreA += 200;
        } if (cbAlGT.isChecked()) {
            scoreA -= 200;
        } if (cbAo12.isChecked() ) {
            scoreA += 200;
        }

        if (cbBwT.isChecked()){
            scoreB += 100;
        } if (cbBlT.isChecked()) {
            scoreB -= 100;
        } if (cbBwGT.isChecked()  ) {
            scoreB += 200;
        } if (cbBlGT.isChecked()) {
            scoreB -= 200;
        } if (cbBo12.isChecked()) {
            scoreB += 200;
        }

        extraScoreTeamA = scoreA;
        extraScoreTeamB = scoreB;
    }

    private void calculatePoints() {
        getCheckBoxResults();

        totalScoreTeamA = extraScoreTeamA + map(numberPickerTeamA.getValue());
        totalScoreTeamB = extraScoreTeamB + map(numberPickerTeamB.getValue());


    }

    public void done(View view){
        Log.i(TAG,"done");
        calculatePoints();
        Intent returnIntent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        returnIntent.putExtra(EXTRA_TEAM_A_SCORE, totalScoreTeamA);
        returnIntent.putExtra(EXTRA_TEAM_B_SCORE, totalScoreTeamB);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
