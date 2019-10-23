package com.example.study_buddy;

import android.app.Application;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ChatApplication extends Application {
    private Socket mSocket;
    {   //get a global socket
        try {
            mSocket = IO.socket("server,uri");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
