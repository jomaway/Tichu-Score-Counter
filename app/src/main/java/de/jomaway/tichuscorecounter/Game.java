package de.jomaway.tichuscorecounter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static de.jomaway.tichuscorecounter.Players.P1;
import static de.jomaway.tichuscorecounter.Players.P2;
import static de.jomaway.tichuscorecounter.Players.P3;
import static de.jomaway.tichuscorecounter.Players.P4;

/**
 * Created by jsma on 11.02.2017.
 */

public class Game {
    // Set some Constants
    public static final boolean WON = true;
    public static final boolean LOST = false;
    public static final int TICHU = 889;
    public static final int GREAT_TICHU = 886;
    public static final int PLAYER_1 = 0;
    public static final int PLAYER_2 = 1;
    public static final int PLAYER_3 = 2;
    public static final int PLAYER_4 = 3;
    private static final Players[] TEAM_A = new Players[] {P1,P3};
    private static final Players[] TEAM_B = new Players[] {P2,P4};

    public static boolean p1_saidTichu = false;
    public static boolean p2_saidTichu = false;
    public static boolean p3_saidTichu = false;
    public static boolean p4_saidTichu = false;




    public static boolean p1_saidGreatTichu = false;
    public static boolean p2_saidGreatTichu = false;
    public static boolean p3_saidGreatTichu = false;
    public static boolean p4_saidGreatTichu = false;

    // Set Variables
    private String player1_name = "Jonas";
    private String player2_name = "Anja";
    private String player3_name = "Lena";
    private String player4_name = "Christian";

    // Variables for the Game
    private int teamA_score = 0;
    private int teamB_score = 0;
    private int round = 0;

    // Variables for a Round > Reset in newRound
    private List<Integer> out = new ArrayList<>();
    private boolean[] player_saidTichu = new boolean[] {false,false,false,false};
    private boolean[] player_saidGreatTichu = new boolean[] {false,false,false,false};
    private int[] player_out = new int[] {0,0,0,0};

    // Methods
    public void newGame() {
        round = 0;
        newRound();

    }
    public void newRound() {
        out.clear();
        round ++;

    }

    // adds roundScore to the totalScore
    private void addRoundPoints(int teamA_roundScore, int teamB_roundScore) {
        this.teamA_score += teamA_roundScore;
        this.teamB_score += teamB_roundScore;
    }

    public void endRound(int teamA_cardScore, int teamB_cardScore){
        int teamA_roundScore = 0, teamB_roundScore = 0;
        // calculate Extra Points

        // add Points to totalScore
        addRoundPoints(teamA_roundScore,teamB_roundScore);
        // start new Round
        newRound();
    }
    public void endGame() {

    }

    public boolean playerSaidTichu(int player, int tichu) {
        // Check if player is a valid PLAYER
        if (player < PLAYER_1 || player > PLAYER_4) {
            return false;
        } else {
            if (!player_saidTichu[player]) {
                player_saidTichu[player] = true;
                return true;
            }
        }

        return false;
    }

    public void playerSaidGreatTichu(int player) {

    }

    public int playerOut(int player){
        switch (player) {
            case PLAYER_1:
                out.add(PLAYER_1);
                player_out[PLAYER_1] = out.size();
                return out.size();
            case PLAYER_2:
                out.add(PLAYER_2);
                player_out[PLAYER_2] = out.size();
                return out.size();
            case PLAYER_3:
                out.add(PLAYER_3);
                player_out[PLAYER_3] = out.size();
                return out.size();
            case PLAYER_4:
                out.add(PLAYER_4);
                player_out[PLAYER_4] = out.size();
                return out.size();
            default:
                return 0;
        }
    }



    // Getter Methods
    public int getTeamA_score() {
        return teamA_score;
    }
    public int getTeamB_score() {
        return teamB_score;
    }

    public String getPlayer1_name() {
        return player1_name;
    }

    public String getPlayer2_name() {
        return player2_name;
    }

    public String getPlayer3_name() {
        return player3_name;
    }

    public String getPlayer4_name() {
        return player4_name;
    }
}




