package edu.harding.tictactoe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class OnlineTicTacToeGame extends AppCompatActivity {
    private OnlineBoardView mBoardView;
    private OnlineTicTacToeLogic mGame;
    int pos;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mGame = new OnlineTicTacToeLogic();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_tic_tac_toe_game);
        mBoardView = (OnlineBoardView) findViewById(R.id.online_board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        mBoardView.setColor(mPrefs.getInt("board_color", Color.LTGRAY));

    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            pos = row * 3 + col;

// So we aren't notified of continued events when finger is moved
            return false;
        }
        public void blockBoard(){
            for(int i = 0; i<mGame.BOARD_SIZE; i++){
                /*mBoardButtons[i]=true;*/
            }
        }
    };
}
