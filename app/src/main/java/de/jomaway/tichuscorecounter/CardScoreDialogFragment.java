package de.jomaway.tichuscorecounter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jsma on 21.02.2017.
 */

public class CardScoreDialogFragment extends DialogFragment {
    private static final String TAG = "CardScoreDialogFragment";

    public final static String EXTRA_TEAM_A_SCORE = "de.jomaway.tichuscorecounter.TEAM_A_SCORE";
    public final static String EXTRA_TEAM_B_SCORE = "de.jomaway.tichuscorecounter.TEAM_B_SCORE";

    private static final int TEAM_MIN_SCORE = 0;
    private static final int TEAM_MAX_SCORE = 30;

    NumberPicker numberPickerTeamA;
    NumberPicker numberPickerTeamB;

    // Container Activity must implement this interface
    public interface OnCardScoreSelectedListener {
        public void onCardScoreSelected(int teamA, int teamB);
    }

    OnCardScoreSelectedListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCardScoreSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCardScoreSelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_card_score, container, false);
        getDialog().setTitle(getString(R.string.dialog_card_title));

        setNumberPickers(rootView);

        Button done = (Button) rootView.findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCardScoreSelected(map(numberPickerTeamA.getValue()),map(numberPickerTeamB.getValue()));
                dismiss();
            }
        });
        return rootView;
    }

    private void setNumberPickers(View rootView) {
        // Set NumberPickers
        numberPickerTeamA = (NumberPicker) rootView.findViewById(R.id.teamA_numberPicker);
        numberPickerTeamB = (NumberPicker) rootView.findViewById(R.id.teamB_numberPicker);


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

        // Gets wheter the selector wheel wraps reaching the min/max value
        numberPickerTeamA.setWrapSelectorWheel(false);
        numberPickerTeamB.setWrapSelectorWheel(false);

        // Set current Value
        numberPickerTeamA.setValue(15);  // 15 * 5 -25 = 50
        numberPickerTeamB.setValue(15);

        // set Formater to NumberPicker
        numberPickerTeamA.setFormatter(formatter);
        numberPickerTeamB.setFormatter(formatter);

        // workaround for NumberPicker bug. -> no default value displayed
        try {
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(numberPickerTeamA);
            inputText.setFilters(new InputFilter[0]);
            inputText = (EditText) f.get(numberPickerTeamB);
            inputText.setFilters(new InputFilter[0]);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

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

    private int map(int np_value) {
        return (np_value * 5 - 25);
    }

    public void done(){
        Log.i(TAG,"done");
        map(numberPickerTeamA.getValue());
         map(numberPickerTeamB.getValue());
    }

















}
