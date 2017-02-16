package de.jomaway.tichuscorecounter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

import static android.R.attr.data;
import static android.R.attr.switchMinWidth;

public class MainActivity extends AppCompatActivity {
    // Constants
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAYER_ACTIONBAR_PLAYER_TAG = 33;
    private static final int ROUND_POINT_REQUEST = 968;
    private static final int SET_PLAYERS_REQUEST = 250;
    private static final String TEAM_A_GAMESCORE = "pref_gamescoreTeamA";
    private static final String TEAM_B_GAMESCORE = "pref_gamescoreTeamB";
    private static final String GAME_ROUNDS = "pref_gameRounds";
    private static final String PLAYER1_NAME = "pref_player1name";
    private static final String PLAYER2_NAME = "pref_player2name";
    private static final String PLAYER3_NAME = "pref_player3name";
    private static final String PLAYER4_NAME = "pref_player4name";


    // MARK: Model
    // Views
    TextView player1;
    TextView player2;
    TextView player3;
    TextView player4;

    TextView teamA_score;
    TextView teamB_score;

    String player1_name;
    String player2_name;
    String player3_name;
    String player4_name;

    int rounds = 0;

    // Score Variables
    int totalScore_TeamA;
    int totalScore_TeamB;

    // MARK: App Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(savedInstanceState != null) {
            Log.d(TAG,"load saveInstanceState ");
            totalScore_TeamA = savedInstanceState.getInt("TeamA");
            totalScore_TeamB = savedInstanceState.getInt("TeamB");
        }



        player1 = (TextView) findViewById(R.id.player1_name);
        player2 = (TextView) findViewById(R.id.player2_name);
        player3 = (TextView) findViewById(R.id.player3_name);
        player4 = (TextView) findViewById(R.id.player4_name);

        teamA_score = (TextView) findViewById(R.id.teamA_score);
        teamB_score = (TextView) findViewById(R.id.teamB_score);

        loadGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch (item.getItemId()) {
            case R.id.menu_newGame:
                newGame();
                return true;
            case R.id.menu_setPlayers:
                Intent intent = new Intent(this, SetPlayersActivity.class);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER1,player1_name);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER2,player2_name);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER3,player3_name);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER4,player4_name);
                startActivityForResult(intent, SET_PLAYERS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Gets the Result back from an startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check for the right Result
            switch (resultCode) {
                case RESULT_OK:
                    if (requestCode == ROUND_POINT_REQUEST) {
                        rounds ++;
                        int roundScoreTeamA = data.getIntExtra(SetRoundPointsActivity.EXTRA_TEAM_A_SCORE, 0);
                        int roundScoreTeamB = data.getIntExtra(SetRoundPointsActivity.EXTRA_TEAM_B_SCORE, 0);
                        addPoints(roundScoreTeamA, roundScoreTeamB);
                        updateTotalScore();
                        passCardsToNextShuffler();
                    } else if (requestCode == SET_PLAYERS_REQUEST) {
                        player1_name = data.getStringExtra(SetPlayersActivity.EXTRA_PLAYER1);
                        player2_name = data.getStringExtra(SetPlayersActivity.EXTRA_PLAYER2);
                        player3_name = data.getStringExtra(SetPlayersActivity.EXTRA_PLAYER3);
                        player4_name = data.getStringExtra(SetPlayersActivity.EXTRA_PLAYER4);
                        updatePlayerNames();
                    }
                    break;
                case RESULT_CANCELED:
                    break;
                default:
                    break;
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveGame();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("TeamA",totalScore_TeamA);
        savedInstanceState.putInt("TeamB",totalScore_TeamB);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        totalScore_TeamA = savedInstanceState.getInt("TeamA");
        totalScore_TeamB = savedInstanceState.getInt("TeamB");
    }

    // MARK: game Methods
    // Add Points to the total Score of both Teams
    private void addPoints(int teamA_Points, int teamB_Points) {
        totalScore_TeamA += teamA_Points;
        totalScore_TeamB += teamB_Points;
        // Check if a Team won already
        checkForWinner();
    }

    private boolean checkForWinner() {
        if (totalScore_TeamA >= 1000){
            wonGame("TeamA");
            return true;

        } if (totalScore_TeamB >= 1000) {
            wonGame("TeamB");
            return true;
        }
        return false;
    }

    private void wonGame(String team) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Start an new Game?")
                .setTitle(team + " won!!")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newGame();
                    }
                })
                .setNegativeButton("no",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void newGame() {
        Log.i(TAG,"new Game started");
        totalScore_TeamA = 0;
        totalScore_TeamB = 0;
        rounds = 0;

        updateTotalScore();
        passCardsToNextShuffler();
        Toast.makeText(this, R.string.toast_new_game,Toast.LENGTH_SHORT).show();
    }

    private void saveGame() {
        Log.i(TAG,"total score saved");
        SharedPreferences gamescore = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = gamescore.edit();
        //save score
        editor.putInt(TEAM_A_GAMESCORE, totalScore_TeamA);
        editor.putInt(TEAM_B_GAMESCORE, totalScore_TeamB);
        editor.putInt(GAME_ROUNDS,rounds);
        // save Player names
        editor.putString(PLAYER1_NAME, player1_name);
        editor.putString(PLAYER2_NAME, player2_name);
        editor.putString(PLAYER3_NAME, player3_name);
        editor.putString(PLAYER4_NAME, player4_name);
        // Commit the edits
        editor.commit();
    }
    private void loadGame() {
        Log.i(TAG,"previous score loaded");
        // Set SharedPreferences
        SharedPreferences gamescore = getPreferences(MODE_PRIVATE);
        //load score
        totalScore_TeamA = gamescore.getInt(TEAM_A_GAMESCORE,0);
        totalScore_TeamB = gamescore.getInt(TEAM_B_GAMESCORE,0);
        rounds = gamescore.getInt(GAME_ROUNDS,0);
        // load players
        player1_name = gamescore.getString(PLAYER1_NAME, getString(R.string.player_1));
        player2_name = gamescore.getString(PLAYER2_NAME,getString(R.string.player_2));
        player3_name = gamescore.getString(PLAYER3_NAME, getString(R.string.player_3));
        player4_name = gamescore.getString(PLAYER4_NAME, getString(R.string.player_4));
        // Update the screen and set a toast to notify the user
        updateTotalScore();
        updatePlayerNames();
        passCardsToNextShuffler();
        Toast.makeText(this, R.string.toast_load_game,Toast.LENGTH_SHORT).show();
    }


    // MARK: UI Update Methods
    // updates the score
    private void updateTotalScore() {
        Log.d(TAG,"update Total Score");
        teamA_score.setText(String.valueOf(totalScore_TeamA));
        teamB_score.setText(String.valueOf(totalScore_TeamB));
    }

    // updates all player names on the screen
    private void updatePlayerNames() {
        Log.d(TAG,"update player names:");
        player1.setText(player1_name);
        player2.setText(player2_name);
        player3.setText(player3_name);
        player4.setText(player4_name);
        saveGame();
    }

    // MARK: Methods that get called by User interaction
    // gets called if you tap on an player
    public void selectedPlayer(View view){
        //showPlayerAction(view.getId());
    }

    // finish the Round an start an new Activity for the Round Point Result
    public void finishRound(View view){
        if (!checkForWinner()) {
            Log.i(TAG, "Round finished - start SetRoundPointsActivity");
            Intent intent = new Intent(this, SetRoundPointsActivity.class);
            startActivityForResult(intent, ROUND_POINT_REQUEST);
        }
    }

    private void passCardsToNextShuffler() {
        ImageView cards = (ImageView) findViewById(R.id.game_cards);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards.getLayoutParams());
        switch (rounds%4) {
            case 0:
                params.addRule(RelativeLayout.RIGHT_OF, R.id.player1_image);
                params.addRule(RelativeLayout.BELOW,R.id.player1_image);
                break;
            case 1:
                params.addRule(RelativeLayout.LEFT_OF, R.id.player2_image);
                params.addRule(RelativeLayout.BELOW,R.id.player2_image);
                break;
            case 2:
                params.addRule(RelativeLayout.LEFT_OF, R.id.player3_image);
                params.addRule(RelativeLayout.ABOVE,R.id.player3_image);
                break;
            case 3:
                params.addRule(RelativeLayout.RIGHT_OF, R.id.player4_image);
                params.addRule(RelativeLayout.ABOVE,R.id.player4_image);
                break;
        }
        cards.setLayoutParams(params);
    }

    /*
    private void showPlayerAction(int playerID){
        LinearLayout playerActionBar = (LinearLayout) findViewById(R.id.player_actionbar);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(playerActionBar.getLayoutParams());
        switch (playerID){
            case (R.id.player1_image):
                params.addRule(RelativeLayout.BELOW, R.id.player1_name);
                playerActionBar.setGravity(Gravity.LEFT);
                break;
            case (R.id.player2_image):
                params.addRule(RelativeLayout.BELOW, R.id.player2_name);
                playerActionBar.setGravity(Gravity.RIGHT);
                break;
            case (R.id.player3_image):
                params.addRule(RelativeLayout.ABOVE, R.id.player3_name);
                playerActionBar.setGravity(Gravity.RIGHT);
                break;
            case (R.id.player4_image):
                params.addRule(RelativeLayout.ABOVE, R.id.player4_name);
                playerActionBar.setGravity(Gravity.LEFT);
                break;
            default:
                Log.e(TAG,"Not an valid Player ID");
                return;
        }
        playerActionBar.setTag(PLAYER_ACTIONBAR_PLAYER_TAG);
        playerActionBar.setLayoutParams(params);
        playerActionBar.setVisibility(View.VISIBLE);
    }

    // hide the Player Action Bar
    private void hidePlayerAction(){
        LinearLayout playerAction = (LinearLayout) findViewById(R.id.player_actionbar);
        playerAction.setVisibility(View.GONE);
    }
    */
}
