package com.danertu.tools;

import android.widget.ImageView;

import com.danertu.dianping.HuDongActivity;

/*第一个为doInBackground接受的参数，第二个为显示进度的参数，
 * 第三个为doInBackground返回和onPostExecute传入的参数*/
public class RecodingAsyncTask extends AsyncTask<Integer, Integer, String> {
    public final String module = "RecodingAsyncTask";
    ImageView imageView;
    int pic[] = null;
    RecordThread rThread;


    public RecodingAsyncTask(ImageView imageView, int[] pic, RecordThread rThread) {
        this.imageView = imageView;
        this.pic = pic;
        this.rThread = rThread;
    }

    //该方法并不运行在UI线程当中，所以在该方法当中，不能对UI当中的控件进行设置和修改
    protected String doInBackground(Integer... param) {
        //用于发布更新消息
        int i = 0;
        for (i = 0; i < this.pic.length; i++) {
            //延时1秒
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //用于发布更新消息
            publishProgress(i);
        }
        return "";
    }

    //在doInBackground方法执行结束之后再运行，并且运行在UI线程当中。
    //主要用于将异步任务执行的结果展示给客户
    @Override
    protected void onPostExecute(String result) {
        //rThread.start();
        HuDongActivity.imgisrun = false;
    }

    //该方法运行在UI线程当中,主要用于进行异步操作之前的UI准备工作
    @Override
    protected void onPreExecute() {
        //rThread.pause();
        HuDongActivity.imgisrun = true;
    }

    //在doInBackground方法当中，每次调用publishProgress()方法之后，都会触发该方法
    //更新按钮图片
    @Override
    protected void onProgressUpdate(Integer... values) {
        int value = values[0];
        imageView.setBackgroundResource(pic[value]);
    }

}
