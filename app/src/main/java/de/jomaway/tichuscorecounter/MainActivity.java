package de.jomaway.tichuscorecounter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static de.jomaway.tichuscorecounter.Players.P1;
import static de.jomaway.tichuscorecounter.Players.P2;
import static de.jomaway.tichuscorecounter.Players.P3;

public class MainActivity extends AppCompatActivity implements PlayerOutCallback, CardScoreDialogFragment.OnCardScoreSelectedListener {
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

    private static int mPlayersOut = 0;

    public static int getPlayersOut() {
        setPlayersOut();
        return mPlayersOut;
    }

    public static void setPlayersOut() {
        MainActivity.mPlayersOut++;
    }


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

    PlayerActionBar playerActionBar1;
    PlayerActionBar playerActionBar2;
    PlayerActionBar playerActionBar3;
    PlayerActionBar playerActionBar4;

    int rounds = 0;

    // Score Variables
    int totalScore_TeamA;
    int totalScore_TeamB;

    int roundScore_TeamA;
    int roundScore_TeamB;

    int lastScore_TeamA;
    int lastScore_TeamB;

    List<Players> outOrder = new ArrayList<Players>();

    // MARK: App Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState != null) {
            Log.d(TAG, "load saveInstanceState ");
            totalScore_TeamA = savedInstanceState.getInt("TeamA");
            totalScore_TeamB = savedInstanceState.getInt("TeamB");
        }

        player1 = (TextView) findViewById(R.id.player1_name);
        player2 = (TextView) findViewById(R.id.player2_name);
        player3 = (TextView) findViewById(R.id.player3_name);
        player4 = (TextView) findViewById(R.id.player4_name);

        playerActionBar1 = (PlayerActionBar) findViewById(R.id.player1_actionbar);
        playerActionBar2 = (PlayerActionBar) findViewById(R.id.player2_actionbar);
        playerActionBar3 = (PlayerActionBar) findViewById(R.id.player3_actionbar);
        playerActionBar4 = (PlayerActionBar) findViewById(R.id.player4_actionbar);

        teamA_score = (TextView) findViewById(R.id.teamA_score);
        teamB_score = (TextView) findViewById(R.id.teamB_score);

        playerActionBar1.setPlayerOutCallback(this, Players.P1);
        playerActionBar2.setPlayerOutCallback(this, Players.P2);
        playerActionBar3.setPlayerOutCallback(this, Players.P3);
        playerActionBar4.setPlayerOutCallback(this, Players.P4);

        // load Game
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
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER1, player1_name);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER2, player2_name);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER3, player3_name);
                intent.putExtra(SetPlayersActivity.EXTRA_PLAYER4, player4_name);
                startActivityForResult(intent, SET_PLAYERS_REQUEST);
                return true;
            case R.id.menu_redoRound:
                // info max one Round can be undone
                redoRound();
                return true;
            case R.id.menu_resetRound:
                resetRound();
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
                if (requestCode == SET_PLAYERS_REQUEST) {
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
        savedInstanceState.putInt("TeamA", totalScore_TeamA);
        savedInstanceState.putInt("TeamB", totalScore_TeamB);
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

    // new Round
    private void newRound() {
        // Save the Score
        lastScore_TeamA = totalScore_TeamA;
        lastScore_TeamB = totalScore_TeamB;
        totalScore_TeamA += roundScore_TeamA;
        totalScore_TeamB += roundScore_TeamB;

        // Check if a Team won already
        checkForWinner();

        // Reset all Round based variables
        resetRound();
        updateTotalScore();
        passCardsToNextShuffler();

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_main), "New Round started", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        redoRound();
                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    private void redoRound() {
        rounds--;
        totalScore_TeamA = lastScore_TeamA;
        totalScore_TeamB = lastScore_TeamB;
        updateTotalScore();
        passCardsToNextShuffler();
        resetRound();
    }

    // Resets all Round based variables
    private void resetRound() {
        mPlayersOut = 0;
        outOrder.clear();
        roundScore_TeamA = 0;
        roundScore_TeamB = 0;
        resetActionBars();
        hideAllPlayerActionBars();
    }

    // Reset all Actionbars bevor a new Round
    private void resetActionBars() {
        playerActionBar1.reset();
        playerActionBar2.reset();
        playerActionBar3.reset();
        playerActionBar4.reset();
    }

    private boolean checkForWinner() {
        if (totalScore_TeamA >= 1000) {
            wonGame("TeamA");
            return true;

        }
        if (totalScore_TeamB >= 1000) {
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
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void newGame() {
        Log.i(TAG, "new Game started");
        totalScore_TeamA = 0;
        totalScore_TeamB = 0;
        newRound();
        updateTotalScore();
        passCardsToNextShuffler();
        Toast.makeText(this, R.string.toast_new_game, Toast.LENGTH_SHORT).show();
    }

    private void saveGame() {
        Log.i(TAG, "total score saved");
        SharedPreferences gamescore = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = gamescore.edit();
        //save score
        editor.putInt(TEAM_A_GAMESCORE, totalScore_TeamA);
        editor.putInt(TEAM_B_GAMESCORE, totalScore_TeamB);
        editor.putInt(GAME_ROUNDS, rounds);
        // save Player names
        editor.putString(PLAYER1_NAME, player1_name);
        editor.putString(PLAYER2_NAME, player2_name);
        editor.putString(PLAYER3_NAME, player3_name);
        editor.putString(PLAYER4_NAME, player4_name);
        // Commit the edits
        editor.commit();
    }

    private void loadGame() {
        Log.i(TAG, "previous score loaded");
        // Set SharedPreferences
        SharedPreferences gamescore = getPreferences(MODE_PRIVATE);
        //load score
        totalScore_TeamA = gamescore.getInt(TEAM_A_GAMESCORE, 0);
        totalScore_TeamB = gamescore.getInt(TEAM_B_GAMESCORE, 0);
        rounds = gamescore.getInt(GAME_ROUNDS, 0);
        // load players
        player1_name = gamescore.getString(PLAYER1_NAME, getString(R.string.player_1));
        player2_name = gamescore.getString(PLAYER2_NAME, getString(R.string.player_2));
        player3_name = gamescore.getString(PLAYER3_NAME, getString(R.string.player_3));
        player4_name = gamescore.getString(PLAYER4_NAME, getString(R.string.player_4));
        // Update the screen and set a toast to notify the user
        updateTotalScore();
        updatePlayerNames();
        passCardsToNextShuffler();
        Toast.makeText(this, R.string.toast_load_game, Toast.LENGTH_SHORT).show();
    }


    // MARK: UI Update Methods
    // updates the score
    private void updateTotalScore() {
        Log.d(TAG, "update Total Score");
        teamA_score.setText(String.valueOf(totalScore_TeamA));
        teamB_score.setText(String.valueOf(totalScore_TeamB));
    }

    // updates all player names on the screen
    private void updatePlayerNames() {
        Log.d(TAG, "update player names:");
        player1.setText(player1_name);
        player2.setText(player2_name);
        player3.setText(player3_name);
        player4.setText(player4_name);
        saveGame();
    }

    // MARK: Methods that get called by User interaction
    // gets called if you tap on an player
    public void selectedPlayer(View view) {
        switch (view.getId()) {
            case R.id.player1_image:
                Log.d(TAG, "player 1 action");
                if (playerActionBar1.getVisibility() == View.GONE) {
                    hideAllPlayerActionBars();
                    playerActionBar1.setVisibility(View.VISIBLE);
                } else {
                    hideAllPlayerActionBars();
                }
                break;
            case R.id.player2_image:
                Log.d(TAG, "player 2 action");
                if (playerActionBar2.getVisibility() == View.GONE) {
                    hideAllPlayerActionBars();
                    playerActionBar2.setVisibility(View.VISIBLE);
                } else {
                    hideAllPlayerActionBars();
                }
                break;
            case R.id.player3_image:
                Log.d(TAG, "player 3 action");
                if (playerActionBar3.getVisibility() == View.GONE) {
                    hideAllPlayerActionBars();
                    playerActionBar3.setVisibility(View.VISIBLE);
                } else {
                    hideAllPlayerActionBars();
                }
                break;
            case R.id.player4_image:
                Log.d(TAG, "player 4 action");
                if (playerActionBar4.getVisibility() == View.GONE) {
                    hideAllPlayerActionBars();
                    playerActionBar4.setVisibility(View.VISIBLE);
                } else {
                    hideAllPlayerActionBars();
                }
                break;
            default:
                hideAllPlayerActionBars();
                return;
        }
    }

    private void hideAllPlayerActionBars() {
        playerActionBar1.setVisibility(View.GONE);
        playerActionBar2.setVisibility(View.GONE);
        playerActionBar3.setVisibility(View.GONE);
        playerActionBar4.setVisibility(View.GONE);
    }

    // finish the Round an start an new Activity for the Round Point Result
    private void finishRound() {
        //if (checkForWinner()) { return; }
        // Add Points for a Won or lost Tichu to the Round Points
        addTichuScore();
        // check for TeamOutWin
        if (teamDidFinishRoundFirst()) {
            Team team = outOrder.get(0).getTeam();
            switch (team) {
                case A:
                    roundScore_TeamA += 200;
                    break;
                case B:
                    roundScore_TeamB += 200;
                    break;
            }

            newRound();

            // Show a Toast that no Card Points are needed!
            Log.i(TAG, "Round finished - no Card Points needed");
            Toast.makeText(this, "Team " + team.toString() + " finished as First and Second", Toast.LENGTH_LONG).show();

        } else {
            showCartPointsDialog();
            //Log.i(TAG, "Round finished - start SetRoundPointsActivity");
            //Intent intent = new Intent(this, SetRoundPointsActivity.class);
            //startActivityForResult(intent, ROUND_POINT_REQUEST);
        }
    }

    private void addTichuScore() {
        // get Tichu Score for Team A
        roundScore_TeamA += playerActionBar1.getTichuScore();
        roundScore_TeamA += playerActionBar3.getTichuScore();
        // get Tichu Score for Team B
        roundScore_TeamB += playerActionBar2.getTichuScore();
        roundScore_TeamB += playerActionBar4.getTichuScore();
    }

    private boolean teamDidFinishRoundFirst() {
        if (outOrder.size() >= 2) {
            Players first = outOrder.get(0);
            Players second = outOrder.get(1);
            return (first.getTeam() == second.getTeam());
        }
        Log.e(TAG, "No Players in List");
        return false;
    }

    private void passCardsToNextShuffler() {
        ImageView cards = (ImageView) findViewById(R.id.game_cards);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards.getLayoutParams());
        switch (rounds % 4) {
            case 0:
                params.addRule(RelativeLayout.RIGHT_OF, R.id.player1_image);
                params.addRule(RelativeLayout.BELOW, R.id.player1_image);
                break;
            case 1:
                params.addRule(RelativeLayout.LEFT_OF, R.id.player2_image);
                params.addRule(RelativeLayout.BELOW, R.id.player2_image);
                break;
            case 2:
                params.addRule(RelativeLayout.LEFT_OF, R.id.player3_image);
                params.addRule(RelativeLayout.ABOVE, R.id.player3_image);
                break;
            case 3:
                params.addRule(RelativeLayout.RIGHT_OF, R.id.player4_image);
                params.addRule(RelativeLayout.ABOVE, R.id.player4_image);
                break;
        }
        cards.setLayoutParams(params);
    }

    public void redoPlayersOut() {
        int last = outOrder.size() - 1;
        Players player = outOrder.get(last);
        outOrder.remove(last);
        switch (player) {
            case P1:
                playerActionBar1.redoPlayerOut();
                break;
            case P2:
                playerActionBar2.redoPlayerOut();
                break;
            case P3:
                playerActionBar3.redoPlayerOut();
                break;
            case P4:
                playerActionBar4.redoPlayerOut();
                break;
        }
        MainActivity.mPlayersOut--;
    }

    // Callback methods
    public void playerOut(Players player) {
        Log.d(TAG, player.toString() + " out _ add");
        outOrder.add(player);
        // Check if all Player finished
        switch (mPlayersOut) {
            case 2:
                if (teamDidFinishRoundFirst()) {
                    Log.d(TAG, "Round finished due to Team out");
                    finishRound();
                } else {
                    showPlayerOutSnackbar(player);
                }
                break;
            case 3:
                Log.d(TAG, "Round finished");
                finishRound();
                break;
            default:
                showPlayerOutSnackbar(player);
                break;
        }
    }

    private void showPlayerOutSnackbar(Players player) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_main), player.toString() + " out!", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        redoPlayersOut();
                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    private void showCartPointsDialog() {
        FragmentManager fm = getFragmentManager();
        CardScoreDialogFragment dialogFragment = new CardScoreDialogFragment();
        dialogFragment.show(fm, "Sample Fragment");
    }

    public void onCardScoreSelected(int cardScoreTeamA, int cardScoreTeamB) {
        if ((cardScoreTeamA + cardScoreTeamB) == 100) {
            roundScore_TeamA += cardScoreTeamA;
            roundScore_TeamB += cardScoreTeamB;
            // start new Round
            newRound();
        } else {
            Log.e(TAG,"wrong Card Points");
        }
    }
}
