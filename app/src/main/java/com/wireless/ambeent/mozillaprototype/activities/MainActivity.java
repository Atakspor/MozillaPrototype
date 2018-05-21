package com.wireless.ambeent.mozillaprototype.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wireless.ambeent.mozillaprototype.R;
import com.wireless.ambeent.mozillaprototype.adapters.ChatAdapter;
import com.wireless.ambeent.mozillaprototype.businesslogic.ChatHandler;
import com.wireless.ambeent.mozillaprototype.businesslogic.HotspotController;
import com.wireless.ambeent.mozillaprototype.customviews.CustomRecyclerView;
import com.wireless.ambeent.mozillaprototype.customviews.EditTextV2;
import com.wireless.ambeent.mozillaprototype.helpers.Constants;
import com.wireless.ambeent.mozillaprototype.pojos.ConnectedDeviceObject;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;
import com.wireless.ambeent.mozillaprototype.server.ServerController;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static boolean isVisible = false;

    //Chat RecyclerView adapter
    private static ChatAdapter mChatAdapter;

    //The set that contains the messages to be shown on the screen
    private List<MessageObject> mMessages = new ArrayList<>();

    //The list that contains the IP address of other devices that are connected to hotspot
    private List<ConnectedDeviceObject> mHotspotNeighboursList = new ArrayList<>();

    //The class that parses messages, insert them to local database and send them.
    private ChatHandler mChatHandler;

    //The class that controls Hotspot and finds connected devices
    private HotspotController mHotspotController;

    //A flag the check whether the user is connected to a hotspot that is created by the app
    private boolean isConnectedToAppHotspot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Set up the PHONE_NUMBER for globall access
        SharedPreferences sharedPreferences  = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        Constants.PHONE_NUMBER = sharedPreferences.getString(Constants.USER_PHONE_NUMBER, "");

        //TEST STUFFFFFFFFFFFFFFFFFFFF
        String str = "bla!/bla/bla/";
        String str2 = "@+905352989257";
        String sub = str2.substring(1,14);
        String parts[] = str.split("@");

        Log.i(TAG, "onCreate: TEST  " + sub);

        activityInitialization();











       /* IRest taskService = ServiceGenerator.createService(IRest.class, "asd");
        Call<ResponseBody> loginCall  = taskService.listTasks("asd");

        loginCall.enqueue(new );

        loginPost(loginCall);*/

    }


  /*  public void loginPost(Call<ResponseBody> call)
    {
        try
        {
            call.enqueue(new Callback<ResultUser>()
            {
                @Override
                public void onResponse(Call<ResultUser> call, Response<ResultUser> response)
                {
                    if (response.isSuccessful())
                    {
                        resultUser = response.body();
                        technicianList = resultUser.technicians;
                        progressDialog.cancel();
                        if (resultUser.technicians.size() != 0){
                            Login2 login2Frag = new Login2();
                            login2Frag.setData(technicianList);
                            myActivity.startFragment(login2Frag);
                        }else {
                            Toast.makeText(myActivity, "Hatalı firma kodu ya da teknisyen bulunamadı", Toast.LENGTH_SHORT).show();
                        }


                    }
                    else
                    {
                        Toast.makeText(myActivity, "Hatalı firma kodu", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                }

                @Override
                public void onFailure(Call<ResultUser> call, Throwable t)
                {
                    // something went completely south (like no internet connection)
                    Toast.makeText(myActivity, "Hata 4675", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    private void activityInitialization(){

        //Toolbar setup
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        //Initializing chat recyclerview
        CustomRecyclerView mChatRecyclerView = ButterKnife.findById(this, R.id.recyclerView_Chat);
        mChatRecyclerView.setShouldIgnoreTouch(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mChatAdapter = new ChatAdapter(this, mMessages);
        mChatRecyclerView.setLayoutManager(mLayoutManager);
        mChatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mChatRecyclerView.setAdapter(mChatAdapter);

        //ChatHandler init
        mChatHandler = new ChatHandler(this, mMessages);

        //HotspotController init
        mHotspotController = new HotspotController(this, mHotspotNeighboursList);
    }

    //Checks the hotspot flag. Returns it after creating a suitable Toast.
    public boolean isTheUserConnectedToHotspot(){

        if(isTheUserConnectedToHotspot()) return true;

        Toast.makeText(this, getResources().getString(R.string.toast_hotspot_warning), Toast.LENGTH_SHORT).show();
        return false;
    }

    public void notifyChatAdapter(){
        mChatAdapter.notifyDataSetChanged();
        CustomRecyclerView mChatRecyclerView = ButterKnife.findById(this, R.id.recyclerView_Chat);
        mChatRecyclerView.scrollToPosition(mMessages.size() -1);
    }

    @OnClick(R.id.imageButton_SendMessage)
    public void sendMessage(View view){

        //Get the text message from EditText
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




    @Override
    protected void onResume() {
        isVisible = true;
        notifyChatAdapter();

        ServerController.getInstance().startServer();

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

        ServerController.getInstance().stopServer();

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
