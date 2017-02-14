package de.jomaway.tichuscorecounter;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jsma on 11.02.2017.
 */

public class Game {
    public static final boolean WON = true;
    public static final boolean LOST = false;
    private static final int TICHU = 889;
    private static final int GREAT_TICHU = 886;
    private static final int TEAM_A = 355;
    private static final int TEAM_B = 359;
    private static final int PLAYER_1 = 869;
    private static final int PLAYER_2 = 977;
    private static final int PLAYER_3 = 22;
    private static final int PLAYER_4 = 896;

    private String player1_name = "Jonas";
    private String player2_name = "Anja";
    private String player3_name = "Lena";
    private String player4_name = "Christian";

    private int teamA_score = 0;
    private int teamB_score = 0;

    // Setter Methods
    public void addPoints(int teamA_Points, int teamB_Points) {
        this.teamA_score += teamA_Points;
        this.teamB_score += teamB_Points;
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




