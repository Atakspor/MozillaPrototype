package com.wireless.ambeent.mozillaprototype.server;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wireless.ambeent.mozillaprototype.activities.MainActivity;
import com.wireless.ambeent.mozillaprototype.helpers.DatabaseHelper;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

public class ServerController {

    private static final String TAG = "ServerController";

    //Any port can be used as long as Android does not occupy it
    private static final int PORT = 8000;

    private static ServerController mServerController;

    private static MyHTTPD mServer;

    //The context for local database access
    private Context mContext;

    private ServerController() {
    }

    //Making server object singleton.
    public static synchronized ServerController getInstance() {

        if (mServerController == null) {
            mServerController = new ServerController();
        }
        return mServerController;
    }

    //Starts the server
    public void startServer(Context context) {

        mContext = context;
        Log.i(TAG, "Starting server... ");
        try {
           initServer();
           mServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Stops the server
    public void stopServer() {
        Log.i(TAG, "Stopping server...");
        try {
            initServer();
            if(!mServer.wasStarted()) return; //If it was not started, do nothing
            mServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Init NanoHTTPD object if it is null
    private void initServer() throws IOException {
        if (mServer == null) {
            mServer = new MyHTTPD();
        }
    }

    private class MyHTTPD extends NanoHTTPD {

        public MyHTTPD() throws IOException {
            super(PORT);
            start();
        }

        @Override
        public Response serve(IHTTPSession session) {

            String response = "true";

            final HashMap<String, String> map = new HashMap<String, String>();
            try {
                session.parseBody(map);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }

            //Getting the POST data as json
            final String messageObjectListJson = map.get("postData");

            Gson gson = new Gson();
            Type objectListType = new TypeToken<ArrayList<MessageObject>>() {
            }.getType();

            final ArrayList<MessageObject> messageObjectList = gson.fromJson(messageObjectListJson, objectListType);

            //Insert posted messages to local SQLite database
            for(MessageObject messageObject : messageObjectList){
                DatabaseHelper.insertMessageToSQLite(mContext, messageObject);
            }

            //Show new messages if the app is visible.
            if(MainActivity.isVisible){
                Handler mainHandler = new Handler(mContext.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity)mContext).getmChatHandler().updateMessageList(messageObjectList);
                        ((MainActivity)mContext).notifyChatAdapter();
                    }
                });
            }

            Log.d(TAG, "Got MAP data: " + map);
            Log.d(TAG, "Got POST data: " + messageObjectListJson);



            return newFixedLengthResponse(response);
        }

        private HashMap<String, String> parseResponse(){

            HashMap<String, String> params = new HashMap<String, String>();

            return params;
        }

    }

    private String messagePostResponse() {


        return "true";
    }

}
