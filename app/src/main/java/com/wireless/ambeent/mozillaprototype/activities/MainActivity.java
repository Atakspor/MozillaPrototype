package com.wireless.ambeent.mozillaprototype.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.perf.FirebasePerformance;
import com.wireless.ambeent.mozillaprototype.R;
import com.wireless.ambeent.mozillaprototype.adapters.ChatAdapter;
import com.wireless.ambeent.mozillaprototype.businesslogic.ChatHandler;
import com.wireless.ambeent.mozillaprototype.customviews.CustomRecyclerView;
import com.wireless.ambeent.mozillaprototype.customviews.EditTextV2;
import com.wireless.ambeent.mozillaprototype.helpers.Constants;
import com.wireless.ambeent.mozillaprototype.pojos.ConnectedDeviceObject;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static boolean isVisible = false;

    //Chat RecyclerView adapter
    private static ChatAdapter mChatAdapter;

    //The list that contains the messages to be shown on the screen
    private List<MessageObject> mMessages = new ArrayList<>();

    //The list that contains the IP address of other devices that are connected to hotspot
    private List<ConnectedDeviceObject> mHotspotNeighboursList = new ArrayList<>();


    //The object that parses messages, insert them to local database and send them.
    private ChatHandler mChatHandler;

    //A flag the check whether the user is connected to a hotspot that is created by the app
    private boolean isConnectedToAppHotspot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        SharedPreferences sharedPreferences  = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        sharedPreferences.edit().putString(Constants.USER_PHONE_NUMBER, "+905352989257").apply();

        //TEST STUFFFFFFFFFFFFFFFFFFFF
        String str = "bla!/bla/bla/";
        String str2 = "@+905352989257";
        String sub = str2.substring(1,14);
        String parts[] = str.split("@");

        Log.i(TAG, "onCreate: TEST  " + sub);


    }

    private void activityInitialization(){

        //Toolbar setup
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        //Initializing chat recyclerview
        CustomRecyclerView mChatRecyclerView = ButterKnife.findById(this, R.id.recyclerView_Chat);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mChatAdapter = new ChatAdapter(this, mMessages);
        mChatRecyclerView.setLayoutManager(mLayoutManager);
        mChatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mChatRecyclerView.setAdapter(mChatAdapter);

        //ChatHandler init
        mChatHandler = new ChatHandler(this);
    }

    //Checks the hotspot flag. Returns it after creating a suitable Toast.
    public boolean isTheUserConnectedToHotspot(){

        if(isTheUserConnectedToHotspot()) return true;

        Toast.makeText(this, getResources().getString(R.string.toast_hotspot_warning), Toast.LENGTH_SHORT).show();
        return false;
    }

    @OnClick(R.id.imageButton_SendMessage)
    public void sendMessage(){

        //
        EditTextV2 messageEditText = (EditTextV2) ButterKnife.findById(this, R.id.editText_Message);
        String message = messageEditText.getText().toString();

        if (message.length() < 1) {
            //Do not send empty message.
            Log.i(TAG, "sendMessage: empty");
            return;
        }

        //Clear message EditText.
        messageEditText.setText("");

        //Pass it to ChatHandler to prepare and send the message.
        mChatHandler.sendMessage(message);

        Log.i(TAG, "sendMessage: " + message);
    }

    public static void notifyChatAdapter(){
        mChatAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        isVisible = true;
        Log.i(TAG, "Lifecycle: onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        isVisible = true;
        Log.i(TAG, "Lifecycle: onStart");
        super.onStart();

    }

    @Override
    protected void onPause() {
        isVisible = false;
        Log.i(TAG, "Lifecycle: onPause ");
        super.onPause();
    }


    @Override
    protected void onStop() {
        isVisible = false;
        Log.i(TAG, "Lifecycle: onStop ");
        super.onStop();
    }
}
