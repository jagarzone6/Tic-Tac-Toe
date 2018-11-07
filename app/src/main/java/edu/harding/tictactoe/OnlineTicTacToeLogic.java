package edu.harding.tictactoe;

import java.util.Random;

public class OnlineTicTacToeLogic {

    private char mBoard[] = {OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT};
    public final int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char HUMAN_PLAYER_2 = 'O';
    public static final char OPEN_SPOT = ' ';

    private Random mRand;

    public char getBoardOccupant(int i) {
        return mBoard[i];
    }
    
    public OnlineTicTacToeLogic(){
        
        
    }

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 1] == HUMAN_PLAYER &&
                    mBoard[i + 2] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == HUMAN_PLAYER_2 &&
                    mBoard[i + 1] == HUMAN_PLAYER_2 &&
                    mBoard[i + 2] == HUMAN_PLAYER_2)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 3] == HUMAN_PLAYER &&
                    mBoard[i + 6] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == HUMAN_PLAYER_2 &&
                    mBoard[i + 3] == HUMAN_PLAYER_2 &&
                    mBoard[i + 6] == HUMAN_PLAYER_2)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER)){
            return 2;}
        if ((mBoard[0] == HUMAN_PLAYER_2 &&
                mBoard[4] == HUMAN_PLAYER_2 &&
                mBoard[8] == HUMAN_PLAYER_2) ||
                (mBoard[2] == HUMAN_PLAYER_2 &&
                        mBoard[4] == HUMAN_PLAYER_2 &&
                        mBoard[6] == HUMAN_PLAYER_2)){
            return 3;}

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != HUMAN_PLAYER_2)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(){
        mBoard = new char[]{OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT, OPEN_SPOT};
    }

    /** Set the given player at the given location on the game board.
     *  The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or HUMAN_PLAYER_2
     * @param location - The location (0-8) to place the move
     */
    public void setMove(char player, int location){
        if(mBoard[location] == OPEN_SPOT)
            mBoard[location] = player;
    }

    public char[] getBoardState(){
        return  mBoard;

    }


    public void setBoardState(char[] newBoard){
        mBoard = newBoard;
    }


    /** Return the best move for the computer to make. You must call setMove()
     * to actually make the computer move to that location.
     * @return The best move for the computer to make (0-8).
     */
}
