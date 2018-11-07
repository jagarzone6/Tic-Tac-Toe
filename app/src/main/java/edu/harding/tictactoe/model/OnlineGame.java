package edu.harding.tictactoe.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OnlineGame {

    public static final String HUMAN_PLAYER1 = "X";
    public static final String HUMAN_PLAYER2 = "O";
    public static final String OPEN_SPOT = " ";

    public String gameName;
    public Integer gameID;
    public List<String> mBoard;
    public String playerOnTurn;

    public String HUMAN_PLAYER1_ID;
    public String HUMAN_PLAYER2_ID;

    public OnlineGame() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public OnlineGame(String gameName, Integer gameID, String HUMAN_PLAYER1_ID) {
        this.gameName = gameName;
        this.gameID = gameID;
        this.HUMAN_PLAYER1_ID = HUMAN_PLAYER1_ID;
        this.HUMAN_PLAYER2_ID = "";
        this.mBoard = Arrays.asList(OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT);
        Random r = new Random();
        int rn = r.nextInt((1 - 0) + 1) + 0;
        if (rn == 1) {
            this.playerOnTurn = HUMAN_PLAYER1;
        } else {
            this.playerOnTurn = HUMAN_PLAYER2;
        }
    }
}
