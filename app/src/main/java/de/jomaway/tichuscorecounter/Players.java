package de.jomaway.tichuscorecounter;

/**
 * Created by jsma on 21.02.2017.
 */

public enum Players {
    P1("Player 1",0,Team.A),
    P2("Player 2",1,Team.B),
    P3("Player 3",2,Team.A),
    P4("Player 4",3,Team.B);

    private String stringValue;
    private int idValue;
    private Team teamValue;

    private Players(String name, int id, Team team) {
        stringValue = name;
        idValue = id;
        teamValue = team;
    }

    public Team getTeam(){
        return this.teamValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}