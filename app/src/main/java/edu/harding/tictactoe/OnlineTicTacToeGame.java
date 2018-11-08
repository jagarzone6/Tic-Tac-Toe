package edu.harding.tictactoe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.harding.tictactoe.model.OnlineGame;

public class OnlineTicTacToeGame extends AppCompatActivity {
    private OnlineBoardView mBoardView;
    private OnlineTicTacToeLogic mGame;
    int pos;
    private SharedPreferences mPrefs;
    private DatabaseReference gameRef;
    private DatabaseReference boardRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private OnlineGame oGame;
    private boolean mBoardButtons[];
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    private Boolean mSoundOn;
    int winner;
    private String mySymb;
    private String player2Symb;

    private TextView mInfoTextView;
    private TextView mPlayerTextView;
    private TextView mPlayerTwoTextView;

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click1);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mGame = new OnlineTicTacToeLogic();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_tic_tac_toe_game);
        mBoardView = (OnlineBoardView) findViewById(R.id.online_board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        mBoardView.setColor(mPrefs.getInt("board_color", Color.LTGRAY));
        mInfoTextView = findViewById(R.id.online_information);
        mPlayerTextView = findViewById(R.id.online_player_2);
        mPlayerTwoTextView = findViewById(R.id.online_player_1);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        gameRef = database.getReference("game").child(mPrefs.getString("onlineGameID", ""));
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oGame = dataSnapshot.getValue(OnlineGame.class);
                Log.w("CURRENT ONLINE GAME: ", oGame.gameID.toString());
                char mBoard[] = {oGame.mBoard.get(0).charAt(0), oGame.mBoard.get(1).charAt(0), oGame.mBoard.get(2).charAt(0), oGame.mBoard.get(3).charAt(0), oGame.mBoard.get(4).charAt(0), oGame.mBoard.get(5).charAt(0), oGame.mBoard.get(6).charAt(0), oGame.mBoard.get(7).charAt(0), oGame.mBoard.get(8).charAt(0)};
                mBoardButtons = new boolean[mGame.BOARD_SIZE];
                for (int i = 0; i < 9; i++) {
                    if (mBoard[i] != ' ') {
                        mBoardButtons[i] = true;
                    }
                }
                if (currentUser.getUid().equals(oGame.HUMAN_PLAYER1_ID)) {
                    mySymb = "O";
                    player2Symb = "X";
                } else {
                    mySymb = "X";
                    player2Symb = "O";
                }
                if (oGame.playerOnTurn.equals(mySymb)) {
                    mInfoTextView.setText("It's your turn!");
                } else {
                    mInfoTextView.setText("Waiting for Opponent ...");
                }
                mPlayerTextView.setText("You play with: " + mySymb);
                mPlayerTwoTextView.setText("Opponent ID: " + oGame.HUMAN_PLAYER2_ID);
                mGame.setBoardState(mBoard);
                mBoardView.invalidate();   // Redraw the board
                winner = mGame.checkForWinner();
                if (winner != 0) {
                    blockBoard();
                    mInfoTextView.setText("Game Over");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        ValueEventListener mBoardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (oGame != null) {
                    oGame.mBoard = (List<String>) dataSnapshot.child("mBoard").getValue();
                    char charBoard[] = {oGame.mBoard.get(0).charAt(0), oGame.mBoard.get(1).charAt(0), oGame.mBoard.get(2).charAt(0), oGame.mBoard.get(3).charAt(0), oGame.mBoard.get(4).charAt(0), oGame.mBoard.get(5).charAt(0), oGame.mBoard.get(6).charAt(0), oGame.mBoard.get(7).charAt(0), oGame.mBoard.get(8).charAt(0)};
                    for (int i = 0; i < 9; i++) {
                        if (charBoard[i] != ' ') {
                            mBoardButtons[i] = true;
                        }
                    }
                    mGame.setBoardState(charBoard);
                    oGame.HUMAN_PLAYER1_ID = (String) dataSnapshot.child("HUMAN_PLAYER1_ID").getValue();
                    oGame.playerOnTurn = (String) dataSnapshot.child("playerOnTurn").getValue();
                    oGame.HUMAN_PLAYER2_ID = (String) dataSnapshot.child("HUMAN_PLAYER2_ID").getValue();
                    oGame.gameOver = (Boolean) dataSnapshot.child("gameOver").getValue();

                    if(oGame.HUMAN_PLAYER1_ID.equals(currentUser.getUid())) {
                        mPlayerTwoTextView.setText("Opponent ID: " + oGame.HUMAN_PLAYER2_ID);
                    }else {
                        mPlayerTwoTextView.setText("Opponent ID: " + oGame.HUMAN_PLAYER1_ID);
                    }
                    winner = mGame.checkForWinner();
                    if (winner == 0) {
                        if (oGame.playerOnTurn.equals(mySymb)) {
                            mInfoTextView.setText("It's your turn!");
                        } else {
                            mInfoTextView.setText("Waiting for Opponent ...");
                        }
                    } else if (winner == 1) {
                        mInfoTextView.setText("It's a tie!");
                        blockBoard();
                    } else if (winner == 2) {
                        if (mySymb.equals("X")) {
                            mInfoTextView.setText("You won!");
                        } else {
                            mInfoTextView.setText("Opponent won!");
                        }
                        blockBoard();
                    } else if (winner == 3) {
                        if (mySymb.equals("O")) {
                            mInfoTextView.setText("You won!");
                        } else {
                            mInfoTextView.setText("Opponent won!");
                        }                        blockBoard();
                    }
                    mBoardView.invalidate();
                }
                Log.w("UPDATED mBoard", "mBoardListener");

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Getting Ogame failed", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        boardRef = database.getReference("game").child(mPrefs.getString("onlineGameID", ""));
        boardRef.addValueEventListener(mBoardListener);
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            pos = row * 3 + col;
            if (!mBoardButtons[pos] && oGame.playerOnTurn.equals(mySymb)) {
                setMove(mySymb.charAt(0), pos);
// If no winner yet, let the computer make a move
                winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("Waiting for Opponent ...");
                } else if (winner == 1) {
                    mInfoTextView.setText("It's a tie!");
                    blockBoard();
                } else if (winner == 2) {
                    if (mySymb.equals("X")) {
                        mInfoTextView.setText("You won!");
                    } else {
                        mInfoTextView.setText("Opponent won!");
                    }
                    blockBoard();
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));

                } else if (winner == 3) {
                    if (mySymb.equals("O")) {
                        mInfoTextView.setText("You won!");
                    } else {
                        mInfoTextView.setText("Opponent won!");
                    }
                    blockBoard();
                }


            }
// So we aren't notified of continued events when finger is moved
            return false;
        }

    };

    public void blockBoard() {
        for (int i = 0; i < mGame.BOARD_SIZE; i++) {
            mBoardButtons[i] = true;
        }
        DatabaseReference userRef = database.getReference("game");
        oGame.gameOver = true;
        userRef.child(oGame.gameID.toString()).setValue(oGame);
    }

    private void setMove(char player, int location) {
        if (player == mGame.HUMAN_PLAYER) {
            if (mHumanMediaPlayer != null && mSoundOn == true)
                mHumanMediaPlayer.start();    // Play the sound effect
        } else {
            if (mComputerMediaPlayer != null && mSoundOn == true)
                mComputerMediaPlayer.start();
        }
        DatabaseReference userRef = database.getReference("game");
        oGame.mBoard.set(location, String.valueOf(player));
        oGame.playerOnTurn = player2Symb;
        userRef.child(oGame.gameID.toString()).setValue(oGame);
        mGame.setMove(player, location);
        mBoardButtons[location] = true;
        mBoardView.invalidate();   // Redraw the board
    }
}
