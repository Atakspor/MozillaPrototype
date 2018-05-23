package com.wireless.ambeent.mozillaprototype.businesslogic;

import android.content.Context;
import android.util.Log;

import com.wireless.ambeent.mozillaprototype.activities.MainActivity;
import com.wireless.ambeent.mozillaprototype.helpers.ActivityHelpers;
import com.wireless.ambeent.mozillaprototype.helpers.Constants;
import com.wireless.ambeent.mozillaprototype.helpers.DatabaseHelper;
import com.wireless.ambeent.mozillaprototype.httprequests.IRest;
import com.wireless.ambeent.mozillaprototype.httprequests.RetrofitRequester;
import com.wireless.ambeent.mozillaprototype.pojos.ConnectedDeviceObject;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHandler {

    private static final String TAG = "ChatHandler";

    private Context mContext;
    private List<MessageObject> mMessages;
    private List<ConnectedDeviceObject> mConnectedDeviceList;

    //To prevent multiple sending of the messages,
    public static long lastMessaceSyncTimestmap = 0;

    public ChatHandler(Context mContext, List<MessageObject> mMessages, List<ConnectedDeviceObject> mConnectedDeviceList  ) {
        this.mContext = mContext;
        this.mMessages = mMessages;
        this.mConnectedDeviceList = mConnectedDeviceList;


        //Show the messages when constructed
        updateMessageList(DatabaseHelper.getMessagesFromSQLite(mContext));
    }


    //Put the necessary methods in order to send a message
    public void sendMessage(String message){


        MessageObject messageObject = createMessageObject(message);
        DatabaseHelper.insertMessageToSQLite(mContext, messageObject);

        //Update message list by this message
        updateMessageList(messageObject);

        //Send this message to other devices that are connected
        ArrayList<MessageObject> newMessage = new ArrayList<>();
        newMessage.add(messageObject);
        postMessagesToNetwork(newMessage);

        Log.i(TAG, "sendMessage: " +messageObject);

    }

    //Send the given message list to everyone in the same network
    public void postMessagesToNetwork(ArrayList<MessageObject> outgoingMessageList){

        for (ConnectedDeviceObject connectedDeviceObject : mConnectedDeviceList){

            String ipAddress = "http://"+connectedDeviceObject.getIpAddress()+"/";
            IRest taskService = RetrofitRequester.createService(IRest.class, ipAddress);
            Call<ResponseBody> postCall  = taskService.sendMessagesToNetwork((ArrayList<MessageObject>) outgoingMessageList);

            postRequest(postCall);
        }
    }

    private void postRequest(Call<ResponseBody> call) {
        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.i(TAG, "onResponse: " + call.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Updates message List with the given message list
    public void updateMessageList(ArrayList<MessageObject> savedMessages){

        //Check the message sender and receiver. If one of them matches with this users phone number or is a group message, add it to list to show on the screen.
        for(MessageObject messageObject : savedMessages){
            if(messageObject.getReceiver().equalsIgnoreCase(Constants.PHONE_NUMBER)
                    || messageObject.getSender().equalsIgnoreCase(Constants.PHONE_NUMBER)
                    || messageObject.getReceiver().equalsIgnoreCase("")) mMessages.add(messageObject);
        }

        ((MainActivity)mContext).notifyChatAdapter();

    }

    //Updates the message list with only the given object
    public void updateMessageList(MessageObject message){

        //Check the message sender and receiver. If one of them matches with this users phone number or is a group message, add it to list to show on the screen.
        if(message.getReceiver().equalsIgnoreCase(Constants.PHONE_NUMBER)
                || message.getSender().equalsIgnoreCase(Constants.PHONE_NUMBER)
                || message.getReceiver().equalsIgnoreCase("")) mMessages.add(message);

        ((MainActivity)mContext).notifyChatAdapter();

    }

    //This method parses the message and determines if the message has a receiver or a group message.
    //Then creates a suitable MessageObject to be sent and returns it.
    public MessageObject createMessageObject(String message){

        //Create a globally unique key.
        String randomUUID = UUID.randomUUID().toString();

        //Get senders phone number from SharedPreferences
        String sender = Constants.PHONE_NUMBER;

        //Create receiver string empty. If it stays empty, then it is a group message
        String receiver = "";

        //If the message is not targeted, then message and actualMessage are the same.
        //If the message is targeted, the targeting part will be removed
        String actualMessage = message;

        //If the message starts with '@' then it is most likely a targeted message.
        char firstChar = message.charAt(0);
        boolean isTargetedMessage = firstChar=='@';

        if(isTargetedMessage){
            //TODO: We are cheating here by using only turkish phone numbers right now. fix it

            //Get the target of the message by parsing the message
            receiver = message.substring(1, 14);
            actualMessage = message.substring(15, message.length());

        }

        long timestamp = ActivityHelpers.getCurrentTimeSeconds();

        MessageObject messageObject = new MessageObject(randomUUID, actualMessage, sender, receiver, timestamp);

        Log.i(TAG, "createMessageObject: " +messageObject.toString());

        return messageObject;
    }



}
