package de.jomaway.tichuscorecounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAYER_ACTIONBAR_PLAYER_TAG = 33;

    // Views
    TextView player1;
    TextView player2;
    TextView player3;
    TextView player4;

    TextView score_teamA;
    TextView score_teamB;

    Game game = new Game();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = (TextView) findViewById(R.id.player1_name);
        player2 = (TextView) findViewById(R.id.player2_name);
        player3 = (TextView) findViewById(R.id.player3_name);
        player4 = (TextView) findViewById(R.id.player4_name);

        score_teamA = (TextView) findViewById(R.id.teamA_score);
        score_teamB = (TextView) findViewById(R.id.teamB_score);

        Intent intent = getIntent();
        int roundScoreTeamA = intent.getIntExtra(SetRoundPointsActivity.EXTRA_TEAM_A_SCORE, 0);
        int roundScoreTeamB = intent.getIntExtra(SetRoundPointsActivity.EXTRA_TEAM_B_SCORE, 0);

        game.addPoints(roundScoreTeamA,roundScoreTeamB);
        updateTotalScore();
        //todo add an onclick listener to hide player actionbar
    }

    public Game newGame() {
        Log.d(TAG,"start new game");
        game = new Game();
        if (game != null) {
            updatePlayerNames();
            updateTotalScore();
        }
        return game;
    }


    private void updateTotalScore() {
        Log.d(TAG,"update Total Score");
        score_teamA.setText(String.valueOf(game.getTeamA_score()));
        score_teamB.setText(String.valueOf(game.getTeamB_score()));
    }

    private void updatePlayerNames() {
        Log.d(TAG,"update player names:" + game.getPlayer1_name() + ", " + game.getPlayer2_name());
        player1.setText(game.getPlayer1_name());
        player2.setText(game.getPlayer2_name());
        player3.setText(game.getPlayer3_name());
        player4.setText(game.getPlayer4_name());
    }

    public void selectedPlayer(View view){
        //showPlayerAction(view.getId());
    }

    public void finishRound(View view){
        Log.i(TAG,"Round finished - start SetRoundPointsActivity");
        Intent intent = new Intent(this,SetRoundPointsActivity.class);
        startActivity(intent);
    }

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
}
