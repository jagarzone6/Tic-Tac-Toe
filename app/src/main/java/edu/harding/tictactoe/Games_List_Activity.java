package edu.harding.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Games_List_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games__list_);
        Button mNewOnlineGameButton = (Button) findViewById(R.id.add_new_online_game);
        mNewOnlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateNewGame();
            }
        });

    }

    private void attemptCreateNewGame() {
        Log.w("NEW ONLINE GAME", "attemptCreateNewGame:exec");

    }
}
