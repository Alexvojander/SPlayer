package com.dmplayer.utility.asynctaskabstraction;

import android.os.AsyncTask;

import com.dmplayer.models.AsyncTaskResult;

public abstract class AbstractAsyncTask<T> extends AsyncTask<Void, Void, AsyncTaskResult<T>> {
    private TaskStateListener<T> listener;

    public AbstractAsyncTask(TaskStateListener<T> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listener.onLoadingStarted();
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<T> result) {
        super.onPostExecute(result);
        if (result.getError() != null) {
            listener.onError(result.getError());
        } else  {
            listener.onLoadingSuccessful(result.getResult());
        }
    }
}

