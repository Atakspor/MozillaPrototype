package com.wireless.ambeent.mozillaprototype.businesslogic;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.wireless.ambeent.mozillaprototype.helpers.Constants;
import com.wireless.ambeent.mozillaprototype.helpers.DatabaseHelper;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import java.util.UUID;

public class ChatHandler {

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public ChatHandler(Context mContext) {
        this.mContext = mContext;

        mSharedPreferences  = mContext.getSharedPreferences(Constants.SHARED_PREF, mContext.MODE_PRIVATE);

    }

    public void sendMessage(String message){

        MessageObject messageObject = createMessageObject(message);
        insertMessageToLocalDatabase(messageObject);

    }

    //This method parses the message and determines if the message has a receiver or a group message.
    //Then creates a suitable MessageObject to be sent and returns it.
    public MessageObject createMessageObject(String message){

        //Create a globally unique key.
        String randomUUID = UUID.randomUUID().toString();

        //Get senders phone number from SharedPreferences
        String sender = mSharedPreferences.getString(Constants.USER_PHONE_NUMBER, "000");

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

            actualMessage = message.substring(1, 13);


        }



        MessageObject messageObject = new MessageObject(randomUUID, actualMessage, sender, receiver);


        return messageObject;
    }

    //Inserts a message to SQLite database if it is not already in there
    public void insertMessageToLocalDatabase(MessageObject messageObject){

        //Check database to see whether the message is already inserted
        String table = DatabaseHelper.TABLE_MESSAGES;
        String[] columns = {DatabaseHelper.KEY_MESSAGE_ID,};
        String[] args = { messageObject.getId()};

        Cursor cursor = DatabaseHelper.getInstance(mContext).getReadableDatabase()
                .query(table, columns, DatabaseHelper.KEY_MESSAGE_ID +"=?", args, null, null, null, null);

        //If this returns true, the message is already in database
        if(cursor.moveToFirst()) return;

        //New message. Insert it to database.
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_MESSAGE_ID, messageObject.getId());
        values.put(DatabaseHelper.KEY_MESSAGE, messageObject.getMessage());
        values.put(DatabaseHelper.KEY_SENDER, messageObject.getSender());
        values.put(DatabaseHelper.KEY_RECEIVER, messageObject.getReceiver());

        DatabaseHelper.getInstance(mContext)
                .getWritableDatabase()
                .insert(DatabaseHelper.TABLE_MESSAGES, null, values);
    }

}
