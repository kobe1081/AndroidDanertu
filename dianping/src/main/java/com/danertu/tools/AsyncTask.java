package com.danertu.tools;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public abstract class AsyncTask<Params, Progress, Results> {
    private Thread thread = null;
    private final int WHAT_UPDATE_PROGRESS = 123;
    private final int WHAT_RESULT = 125;

    @SuppressWarnings("unchecked")
    private Handler handle = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_RESULT) {
                Results result = (Results) msg.obj;
                onPostExecute(result);
            } else if (msg.what == WHAT_UPDATE_PROGRESS) {
                onProgressUpdate((Progress[]) msg.obj);
            }
            return true;
        }
    });

    public void execute(final Params... param) {
        onPreExecute();
        thread = new Thread() {
            public void run() {
                Results result = doInBackground(param);
                handle.sendMessage(getMessage(WHAT_RESULT, result));
            }
        };
        thread.start();
    }

    protected abstract Results doInBackground(Params... param);

    protected void onPostExecute(Results result) {

    }

    protected void onPreExecute() {

    }

    private Message getMessage(int what, Object result) {
        Message msg = Message.obtain();
        msg.obj = result;
        msg.what = what;
        return msg;
    }

    protected void publishProgress(Progress... progresses) {
        handle.sendMessage(getMessage(WHAT_UPDATE_PROGRESS, progresses));
    }

    protected void onProgressUpdate(Progress... progresses) {

    }
}
