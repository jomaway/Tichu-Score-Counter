package de.jomaway.tichuscorecounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.R.attr.data;
import static de.jomaway.tichuscorecounter.R.id.player1_name;
import static de.jomaway.tichuscorecounter.R.id.player2_name;
import static de.jomaway.tichuscorecounter.R.id.player3_name;
import static de.jomaway.tichuscorecounter.R.id.player4_name;
import static de.jomaway.tichuscorecounter.SetRoundPointsActivity.EXTRA_TEAM_A_SCORE;
import static de.jomaway.tichuscorecounter.SetRoundPointsActivity.EXTRA_TEAM_B_SCORE;

public class SetPlayersActivity extends AppCompatActivity {
    private static final String TAG = "SetPlayersActivity";

    public final static String EXTRA_PLAYER1 = "de.jomaway.tichuscorecounter.PLAYER1";
    public final static String EXTRA_PLAYER2 = "de.jomaway.tichuscorecounter.PLAYER2";
    public final static String EXTRA_PLAYER3 = "de.jomaway.tichuscorecounter.PLAYER3";
    public final static String EXTRA_PLAYER4 = "de.jomaway.tichuscorecounter.PLAYER4";


    EditText player1;
    EditText player2;
    EditText player3;
    EditText player4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_players);

        player1 = (EditText) findViewById(R.id.player1_edit);
        player2 = (EditText) findViewById(R.id.player2_edit);
        player3 = (EditText) findViewById(R.id.player3_edit);
        player4 = (EditText) findViewById(R.id.player4_edit);

        Intent intent = getIntent();
        player1.setText(intent.getStringExtra(EXTRA_PLAYER1));
        player2.setText(intent.getStringExtra(EXTRA_PLAYER2));
        player3.setText(intent.getStringExtra(EXTRA_PLAYER3));
        player4.setText(intent.getStringExtra(EXTRA_PLAYER4));
    }

    public void done(View view){
        Log.i(TAG,"done");
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_PLAYER1, String.valueOf(player1.getText()));
        returnIntent.putExtra(EXTRA_PLAYER2, String.valueOf(player2.getText()));
        returnIntent.putExtra(EXTRA_PLAYER3, String.valueOf(player3.getText()));
        returnIntent.putExtra(EXTRA_PLAYER4, String.valueOf(player4.getText()));

        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
