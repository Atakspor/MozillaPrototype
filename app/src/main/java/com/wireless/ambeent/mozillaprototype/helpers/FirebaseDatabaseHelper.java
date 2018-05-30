package com.wireless.ambeent.mozillaprototype.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.wireless.ambeent.mozillaprototype.activities.MainActivity;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import java.util.ArrayList;

public class FirebaseDatabaseHelper {

    private static final String TAG = "FirebaseDatabaseHelper";

    private static DatabaseReference dbMessageContentReference;
    private static DatabaseReference dbUsersMessagesReference;



    public static synchronized DatabaseReference getMessageContentInstance(){

        if(dbMessageContentReference == null){
            Log.i(TAG, "getMessageContentInstance: init");
            dbMessageContentReference =  FirebaseDatabase.getInstance().getReference().child("mozillaPrototype").child("messageContent");
        }

        return dbMessageContentReference;
    }

    public static synchronized DatabaseReference getUsersMessagesInstance(){

        if(dbUsersMessagesReference == null){
            Log.i(TAG, "getUsersMessagesInstance: init");
            dbUsersMessagesReference =  FirebaseDatabase.getInstance().getReference().child("mozillaPrototype").child("usersMessages");
        }

        return dbUsersMessagesReference;
    }


    //TODO: optimize this with timestamps...
    //Gets every targeted message from SQLite database and pushes them to Firebase Database
    public static void pushMessagesToFirebase(Context mContext){

        //Get every targeted message from SQLite
        ArrayList<MessageObject> messageObjects = DatabaseHelper.getTargetedMessagesFromSQLite(mContext);

        //Push the values to Firebase database
        for(MessageObject messageObject : messageObjects){

            pushMessageToFirebase(messageObject);

        }

    }

    //Push the given message to Firebase
    public static void pushMessageToFirebase(MessageObject messageObject){

        String id = messageObject.getId();
        String message = messageObject.getMessage();
        String sender = messageObject.getSender();
        String receiver = messageObject.getReceiver();
        long timestamp = messageObject.getTimestamp();

        //The message id is the key
        getMessageContentInstance().child(id).child("message").setValue(message);
        getMessageContentInstance().child(id).child("sender").setValue(sender);
        getMessageContentInstance().child(id).child("receiver").setValue(receiver);
        getMessageContentInstance().child(id).child("timestamp").setValue(String.valueOf(timestamp));

        //The receiver is the main the key
        getUsersMessagesInstance().child(receiver).child(id).child("message").setValue(message);
        getUsersMessagesInstance().child(receiver).child(id).child("sender").setValue(sender);
        getUsersMessagesInstance().child(receiver).child(id).child("timestamp").setValue(timestamp);

    }

    //Listens to the messages that are send to users phone number
    public static synchronized void initPrivateMessageListener(final Context context, SharedPreferences sharedPreferences){

        Query query = getUsersMessagesInstance().child(Constants.PHONE_NUMBER).orderByChild("timestamp");

        //Add listener to phone number
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.i(TAG, "onChildAdded: " + dataSnapshot);

                //Create the received message object
                String id = dataSnapshot.getKey();
                String message =  dataSnapshot.child("message").getValue(String.class);
                String sender =  dataSnapshot.child("sender").getValue(String.class);
                long timestamp =  dataSnapshot.child("timestamp").getValue(long.class);

                MessageObject receivedMessage = new MessageObject(id, message, sender, Constants.PHONE_NUMBER, timestamp);

                //Insert message to local database
                DatabaseHelper.insertMessageToSQLite(context, receivedMessage);

                //Notify the chat adapter if the app is visible
                if(MainActivity.isVisible) ((MainActivity) context).notifyChatAdapter();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.i(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
