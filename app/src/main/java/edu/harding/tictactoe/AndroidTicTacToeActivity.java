package edu.harding.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private TicTacToeGame mGame;

    private BoardView mBoardView;
    // Buttons making up the board
    private boolean mBoardButtons[];

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanScoreTextView;
    private TextView mTieScoreTextView;
    private TextView mComputerScoreTextView;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT=2;
    static final int DIALOG_SCORE=3;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    int winner;
    int pos;
    boolean turno = true;
    boolean mGameOver = false;
    int mHumanWins= 0;
    int mComputerWins = 0;
    int mTies = 0;
    char mGoFirst = 'X';
    private SharedPreferences mPrefs;

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click1);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click2);
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.reset_score:
                showDialog(DIALOG_SCORE);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        mBoardButtons = new boolean[mGame.BOARD_SIZE];
        mInfoTextView = findViewById(R.id.information);
        mComputerScoreTextView = findViewById(R.id.computer);
        mHumanScoreTextView = findViewById(R.id.human);
        mTieScoreTextView = findViewById(R.id.ties);

        if (savedInstanceState == null) {
            startNewGame();
        }
        else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mHumanWins = savedInstanceState.getInt("mHumanWins");
            mComputerWins = savedInstanceState.getInt("mComputerWins");
            mTies = savedInstanceState.getInt("mTies");
            mGoFirst = savedInstanceState.getChar("mGoFirst");
            mBoardButtons =savedInstanceState.getBooleanArray("mBoardButtons");
            winner = savedInstanceState.getInt("winner");
        }
       displayScores();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.commit();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
// TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
// selected is the radio button that should be selected.
                TicTacToeGame.DifficultyLevel selectedD =  mGame.getDifficultyLevel();
                int selected = 0;
                if (selectedD == TicTacToeGame.DifficultyLevel.Easy){
                    selected = 0;
                } else if (selectedD== TicTacToeGame.DifficultyLevel.Harder){
                    selected =1;
                } else {
                    selected = 2;
                }
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
// TODO: Set the diff level of mGame based on which item was selected.
// Display the selected difficulty level
                                if (item == 0){
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                    startNewGame();
                                } else if (item== 1){
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                    startNewGame();
                                } else {
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                    startNewGame();
                                }
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
// Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_ABOUT:
                builder = new AlertDialog.Builder(this);
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
            case DIALOG_SCORE:
                builder.setMessage(R.string.reset_score_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes_rs, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //
                                SharedPreferences.Editor ed = mPrefs.edit();
                                ed.putInt("mHumanWins", 0);
                                ed.putInt("mComputerWins", 0);
                                ed.putInt("mTies", 0);
                                ed.commit();
                                mHumanWins = 0;
                                mComputerWins = 0;
                                mTies = 0;
                                displayScores();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        outState.putInt("mComputerWins", Integer.valueOf(mComputerWins));
        outState.putInt("mTies", Integer.valueOf(mTies));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putBooleanArray("mBoardButtons",mBoardButtons);
        outState.putInt("winner",winner);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mHumanWins = savedInstanceState.getInt("mHumanWins");
        mComputerWins = savedInstanceState.getInt("mComputerWins");
        mTies = savedInstanceState.getInt("mTies");
        mGoFirst = savedInstanceState.getChar("mGoFirst");
        mBoardButtons = savedInstanceState.getBooleanArray("mBoardButtons");
        winner = savedInstanceState.getInt("winner");
    }

    private void startNewGame(){
        for (int i=0;i<mGame.BOARD_SIZE;i++){
            mBoardButtons[i]=false;
        }
        mGame.clearBoard();
        mBoardView.invalidate();
        Random r = new Random();
        int rn =  r.nextInt((1 - 0) + 1) + 0;
        if(rn==1){
            setMove(TicTacToeGame.COMPUTER_PLAYER, r.nextInt((8 - 0) + 1) + 0);
            mInfoTextView.setText("It's your turn!");
        }else {
            mInfoTextView.setText("You go first.");
        }

    }

    private void displayScores() {
        while (mHumanScoreTextView == null || mComputerScoreTextView == null || mTieScoreTextView==null){
            System.out.println("Waiting...");
        }
        mHumanScoreTextView.setText("Human wins: "+Integer.toString(mHumanWins));
        mComputerScoreTextView.setText("Computer wins: "+Integer.toString(mComputerWins));
        mTieScoreTextView.setText("Ties: "+Integer.toString(mTies));
    }

    private void setMove(char player, int location) {
        if(player==mGame.HUMAN_PLAYER){
            if(mHumanMediaPlayer!= null)
            mHumanMediaPlayer.start();    // Play the sound effect
        }else {
            if(mComputerMediaPlayer!= null)
            mComputerMediaPlayer.start();
        }
        mGame.setMove(player, location);
        mBoardButtons[location]=true;
        mBoardView.invalidate();   // Redraw the board
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            pos = row * 3 + col;

            if (!mBoardButtons[pos]&&turno) {
                mInfoTextView.setText("It's your turn!");
                setMove(TicTacToeGame.HUMAN_PLAYER, pos);
// If no winner yet, let the computer make a move
                winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("It's Android's turn!");
                    turno=false;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int move = mGame.getComputerMove();
                            int conv = move +1;
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            winner = mGame.checkForWinner();
                            if (winner == 0)
                                mInfoTextView.setText("It's your turn!");
                            else if (winner == 1)
                            {mInfoTextView.setText("It's a tie!");
                                blockBoard();
                                mGameOver =true;
                                mTies++;
                            }
                            else if (winner == 2){
                                mInfoTextView.setText("You won!");
                                blockBoard();
                                mGameOver =true;
                                mHumanWins++;
                            }
                            else if (winner == 3)
                            {
                                mInfoTextView.setText("Android won!");
                                blockBoard();
                                mGameOver =true;
                                mComputerWins++;
                            }
                            turno=true;
                            displayScores();
                        }
                    }, 1000);

                } else if (winner == 1)
                {mInfoTextView.setText("It's a tie!");
                    blockBoard();
                    mTies++;
                }
                else if (winner == 2){
                    mInfoTextView.setText("You won!");
                    blockBoard();
                    mHumanWins++;
                }
                else if (winner == 3)
                {mInfoTextView.setText("Android won!");
                    blockBoard();
                    mComputerWins++;
                }
                displayScores();


            }
// So we aren't notified of continued events when finger is moved
            return false;
        }
        public void blockBoard(){
                for(int i = 0; i<mGame.BOARD_SIZE; i++){
                    mBoardButtons[i]=true;
                }
        }
    };
}

