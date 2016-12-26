package com.dmplayer.utility.asynctaskabstraction;

public interface TaskStateListener<T> {
        void onLoadingStarted();
        void onLoadingSuccessful(T result);
        void onError(Exception e);
}