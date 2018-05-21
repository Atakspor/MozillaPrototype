package com.wireless.ambeent.mozillaprototype.businesslogic;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.wireless.ambeent.mozillaprototype.activities.MainActivity;
import com.wireless.ambeent.mozillaprototype.helpers.Constants;
import com.wireless.ambeent.mozillaprototype.helpers.DatabaseHelper;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ChatHandler {

    private static final String TAG = "ChatHandler";

    private Context mContext;
    private List<MessageObject> mMessages;

    private SharedPreferences mSharedPreferences;

    public ChatHandler(Context mContext, List<MessageObject> mMessages  ) {
        this.mContext = mContext;
        this.mMessages = mMessages;

        mSharedPreferences  = mContext.getSharedPreferences(Constants.SHARED_PREF, mContext.MODE_PRIVATE);

        //Show the messages when constructed
        updateMessageList(getMessagesFromSQLite());
    }


    public void sendMessage(String message){

        MessageObject messageObject = createMessageObject(message);
        insertMessageToSQLite(messageObject);

        updateMessageList(getLastMessageFromSQLite());
    }

    //Updates message List with the given message list
    public void updateMessageList(ArrayList<MessageObject> savedMessages){

        //Check the message sender and receiver. If one of them matches with this users phone number or is a group message, add it to list to show on the screen.
        for(MessageObject messageObject : savedMessages){
            if(messageObject.getReceiver().equalsIgnoreCase(Constants.USER_PHONE_NUMBER)
                    || messageObject.getSender().equalsIgnoreCase(Constants.USER_PHONE_NUMBER)
                    || messageObject.getReceiver().equalsIgnoreCase("")) mMessages.add(messageObject);
        }

        ((MainActivity)mContext).notifyChatAdapter();

    }

    //Updates the message list with only the given object
    public void updateMessageList(MessageObject message){

        //Check the message sender and receiver. If one of them matches with this users phone number or is a group message, add it to list to show on the screen.
        if(message.getReceiver().equalsIgnoreCase(Constants.USER_PHONE_NUMBER)
                || message.getSender().equalsIgnoreCase(Constants.USER_PHONE_NUMBER)
                || message.getReceiver().equalsIgnoreCase("")) mMessages.add(message);

        ((MainActivity)mContext).notifyChatAdapter();

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

            //Get the target of the message by parsing the message
            receiver = message.substring(1, 13);
            actualMessage = message.substring(14, message.length()-1);

        }



        MessageObject messageObject = new MessageObject(randomUUID, actualMessage, sender, receiver);


        return messageObject;
    }

    //Gets only the last message inserted
    public MessageObject getLastMessageFromSQLite(){

        //Get necessary columns from SQLiite and create MessageObjects
        String table = DatabaseHelper.TABLE_MESSAGES;
        String[] columns = {DatabaseHelper.KEY_MESSAGE_ID,
                DatabaseHelper.KEY_MESSAGE,
                DatabaseHelper.KEY_SENDER,
                DatabaseHelper.KEY_RECEIVER};

        Cursor cursor = DatabaseHelper.getInstance(mContext).getReadableDatabase()
                .query(table, columns, null, null, null, null, null, null);

        //Populate the messages HashSet
        if(cursor.moveToLast()){
            //Constructing every message and their attributes here.
            String messageId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_ID));
            String message = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE));
            String sender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_SENDER));
            String receiver = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_RECEIVER));

            MessageObject messageObject = new MessageObject(messageId, message, sender, receiver);

            return messageObject;
        }

        //Something is wrong...
        return null;


    }

    //Gets saved messages from SQLite database and populates them
    public ArrayList<MessageObject> getMessagesFromSQLite(){

        ArrayList<MessageObject> messages = new ArrayList<>();

        //Get necessary columns from SQLiite and create MessageObjects
        String table = DatabaseHelper.TABLE_MESSAGES;
        String[] columns = {DatabaseHelper.KEY_MESSAGE_ID,
        DatabaseHelper.KEY_MESSAGE,
        DatabaseHelper.KEY_SENDER,
        DatabaseHelper.KEY_RECEIVER};

        Cursor cursor = DatabaseHelper.getInstance(mContext).getReadableDatabase()
                .query(table, columns, null, null, null, null, null, null);

        //Populate the messages HashSet
        while(cursor.moveToNext()){

            //Constructing every message and their attributes here.
            String messageId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE_ID));
            String message = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGE));
            String sender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_SENDER));
            String receiver = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_RECEIVER));

            MessageObject messageObject = new MessageObject(messageId, message, sender, receiver);

            messages.add(messageObject);
        }

        return messages;
    }

    //Inserts a message to SQLite database if it is not already in there
    public void insertMessageToSQLite(MessageObject messageObject){

        //Check database to see whether the message is already inserted
        String table = DatabaseHelper.TABLE_MESSAGES;
        String[] columns = {DatabaseHelper.KEY_MESSAGE_ID};
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
