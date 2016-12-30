package com.dmplayer.utility;

import android.content.Context;

import com.dmplayer.R;

import fi.iki.elonen.NanoHTTPD;

public class RemotePlaylistServer extends NanoHTTPD {

    private Context context;

    public RemotePlaylistServer(Context context, int port) {
        super(port);
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String page = context.getResources().getString(R.string.remote_playlist_page);
        return newFixedLengthResponse(page);
    }


}