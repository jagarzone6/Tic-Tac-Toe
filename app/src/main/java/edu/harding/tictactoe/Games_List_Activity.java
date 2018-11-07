package edu.harding.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.harding.tictactoe.model.OnlineGame;

public class Games_List_Activity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ListView listview;
    private ArrayList<String> list;
    private StableArrayAdapter adapter;
    private  Button mNewOnlineGameButton;
    private DatabaseReference gameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games__list_);
        mNewOnlineGameButton = (Button) findViewById(R.id.add_new_online_game);
        listview = (ListView) findViewById(R.id.gamelistview);

        mNewOnlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewOnlineGameButton.setEnabled(false);
                mNewOnlineGameButton.setClickable(false);
                mNewOnlineGameButton.setBackgroundColor(Color.GRAY);
                attemptCreateNewGame();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mNewOnlineGameButton.setEnabled(true);
                                mNewOnlineGameButton.setClickable(true);
                                mNewOnlineGameButton.setBackgroundColor(Color.parseColor("#ff669900"));
                            }
                        });
                    }
                }, 2000);
            }
        });
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        gameRef = database.getReference("game");
        list = new ArrayList<String>();

        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            OnlineGame oGame = postSnapshot.getValue(OnlineGame.class);
                    list.add(oGame.gameName);
                }
                adapter = new StableArrayAdapter(Games_List_Activity.super.getApplicationContext(),
                        R.xml.game_item,R.id.firstLine, list);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final String item = (String) parent.getItemAtPosition(position);
                        view.animate().setDuration(1000).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                        startActivityForResult(new Intent(Games_List_Activity.this, OnlineTicTacToeGame.class), 0);

                    }

                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    private void attemptCreateNewGame() {
        Log.w("NEW ONLINE GAME", "attemptCreateNewGame:exec");
        Date currentTime = Calendar.getInstance().getTime();
        Random r = new Random();
        Integer rn =  r.nextInt(9000000) + 1000000;
        OnlineGame newGame = new OnlineGame("New game " + currentTime.toString(), rn, currentUser.getUid());
        DatabaseReference userRef = database.getReference("game");
        userRef.child(rn.toString()).setValue(newGame);
        list.clear();
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    OnlineGame oGame = postSnapshot.getValue(OnlineGame.class);
                    if(oGame.gameOver==false){
                    list.add(oGame.gameName);
                    }
                }
                adapter = new StableArrayAdapter(Games_List_Activity.super.getApplicationContext(),
                        R.xml.game_item,R.id.firstLine, list);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  int textViewResourceId2,
                                  List<String> objects) {
            super(context, textViewResourceId, textViewResourceId2 ,objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}

