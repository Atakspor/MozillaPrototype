package com.wireless.ambeent.mozillaprototype.server;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

public class ServerController {

    private static final String TAG = "ServerController";

    //Any port can be used as long as Android does not occupy it
    private static final int PORT = 8000;

    private static ServerController mServerController;

    private static MyHTTPD mServer;

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
    public void startServer() {
        try {
           initServer();
           mServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Stops the server
    public void stopServer() {
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

            HashMap<String, String> params = new HashMap<String, String>();
            final HashMap<String, String> map = new HashMap<String, String>();
            try {
                session.parseBody(map);
                params = (HashMap<String, String>) session.getParms();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }

            final String json = map.get("postData");
            Log.d(TAG, "Got MAP data: " + map);
            Log.d(TAG, "Got PARAMS data: " + params);
            Log.d(TAG, "Got POST data: " + json);


           /* } catch(IOException ioe) {
                Log.w(TAG,"Httpd: " + ioe.toString());
            }*/

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
