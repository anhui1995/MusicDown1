package xin.xiaoa.musicdown;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GetMusic extends Service {
    private String downMusicName = "";
    private String downMusicType = "";

    private String downMusicAuthor = "";
    private String downMusicPath = "";
    private String downUrl = "";
    private Notification.Builder builder;
    private NotificationManager manager;
    Context context;
    private Notification notification;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {// 消息队列
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                stopSelf();
                Toast mToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                mToast.setText("《"+downMusicName+"》 下载完成");
                mToast.show();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_launcher_background).setTicker("下载图片")
                .setContentTitle("图片下载").setContentText("正在下载图片")
                .setAutoCancel(true);// 用户点击浏览一次后，通知消失;

//        builder.setSmallIcon(R.drawable.ic_launcher).setTicker("下载图片")
//                .setContentTitle("图片下载").setContentText("正在下载图片")
//                .setAutoCancel(true);// 用户点击浏览一次后，通知消失;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        downUrl = intent.getStringExtra("downUrl");// 接受传递过来的参数
        downMusicAuthor = intent.getStringExtra("downMusicAuthor");// 接受传递过来的参数
        downMusicName = intent.getStringExtra("downMusicName");// 接受传递过来的参数
        downMusicPath = intent.getStringExtra("downMusicPath");// 接受传递过来的参数
        downMusicType = intent.getStringExtra("downMusicType");// 接受传递过来的参数downMusicType
        new myTast().start();// 创建一个新的线程并且启动
        return super.onStartCommand(intent, flags, startId);

    }

    public class myTast extends Thread implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            System.out.println("开始下载"+downMusicName);
            // TODO Auto-generated method stub
            super.run();
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(downUrl);
            System.out.println("麻婆豆腐："+downUrl);
            HttpResponse httpResponse;
            InputStream inputStream = null;
            OutputStream output = null;
            //ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            int len = 0;
            int len_data = 0;
            int progress_value=0;
            byte[] data = new byte[1024 * 20];
           // byte[] buffer = new byte[1024 * 20];
            //sendSubscribeMsg();
            try {
                httpResponse = client.execute(get);
                long len_long = httpResponse.getEntity().getContentLength();
                System.out.println("len_long"+len_long);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {

                    File file = createFile(downMusicPath+"/"+downMusicAuthor+"-"+downMusicName+downMusicType);
                    output = new FileOutputStream(file);

                    inputStream = httpResponse.getEntity().getContent();
                    //准备通知
                    manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    while ((len = inputStream.read(data)) != -1) {
                        len_data += len;
                        System.out.println("len_data:"+len_data+"len:"+len+"progress_value:"+progress_value);
                        progress_value = (int) ((len_data / (float) len_long) * 100);//进度条刻度计算
//                        sendSubscribeMsg(progress_value,"正在下载  "+downMusicName);
//                        getNotification(progress_value,"正在下载  "+downMusicName);
                        manager.notify(2, getNotification(progress_value,"正在下载  《"+downMusicName+"》",false));

                        output.write(data, 0, len);

//                        arrayOutputStream.write(data, 0, data.length);


                        //builder.setProgress(100, progress_value, false);

                        //manager.notify(1000, builder.build());
                    }
                    //manager.notify(2, getNotification(99,downMusicName+"  下载完成"));
                    manager.cancel(2);

                    manager.notify(2, getNotification(100,"《"+downMusicName+"》  下载完成",true));
                    System.out.println("len_data:"+len_data+"len:"+len+"progress_value:"+progress_value);
//                    sendSubscribeMsg(99,downMusicName+"  下载完成");
//                    boolean flag = to.saver("bb.png",//保存的文件名
//                            arrayOutputStream.toByteArray());// 数据保存到本地Sdcard
                    output.flush();
                    inputStream.close();

                    System.out.println(downMusicName+"下载完成");
                   // sendChatMsg();
                    //sendSubscribeMsg();
                   // builder.setContentText("下载完成1234");
                    //manager.notify(1000, builder.build());

                    try {
                        output.close();
                        handler.sendEmptyMessage(1);
                    } catch (IOException e) {
                        System.out.println("fail");
                        System.out.println("error151"+e);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("error156"+e);
            }
        }
    }




   // public void sendChatMsg(View view) {
//    public void sendChatMsg() {
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(this, "chat")
//                .setContentTitle("下载完成")
//                .setContentText("下载完成")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
//                .setAutoCancel(true)
//                .build();
//        manager.notify(1, notification);
//    }
   Notification getNotification(int progress_value, String title,boolean down) {
       Intent mainIntent = new Intent(this, MainActivity.class);
       PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

       if (down) {
           long[] vibrates = {0};
           notification = new NotificationCompat.Builder(this, "downloading")
                   .setContentTitle(title)
                   .setProgress(100, progress_value, false)
                   .setWhen(System.currentTimeMillis())
                   .setContentIntent(pendingIntent)
                   .setSmallIcon(R.mipmap.ic_launcher_my)
                   .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_my))
                   .setVibrate(vibrates)
                   .setAutoCancel(true)
                   .build();
       }
       else {
           notification = new NotificationCompat.Builder(this, "downloading")
                   .setContentTitle(title)
                   .setProgress(100, progress_value, false)
                   .setWhen(System.currentTimeMillis())
                   .setSmallIcon(R.mipmap.ic_launcher_my)
                   .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_my))
//               .setSmallIcon(R.drawable.ic_launcher_background)
//               .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                   .setAutoCancel(true)
                   .build();
       }
       return notification;
   }
//    public void sendSubscribeMsg(int progress_value,String title) {
//        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notification = new NotificationCompat.Builder(this, "subscribe")
//                .setContentTitle(title)
//                .setProgress(100, progress_value, false)
////                .setContentText("地铁沿线30万商铺抢购中！")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
//                .setAutoCancel(true)
//                .build();
//        manager.notify(2, notification);
//    }
//     int downloadFile(String dirName, String fileName, String urlStr) {
//
//        OutputStream output = null;
//        try {
//            //将字符串形式的path,转换成一个url
//            URL url = new URL(urlStr);
//            //得到url之后，将要开始连接网络，以为是连接网络的具体代码
//            //首先，实例化一个HTTP连接对象conn
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            //定义请求方式为GET，其中GET的大小写不要搞错了。
//            conn.setRequestMethod("GET");
//            //定义请求时间，在ANDROID中最好是不好超过10秒。否则将被系统回收。
//            conn.setConnectTimeout(8 * 1000);
//            //请求成功之后，服务器会返回一个响应码。如果是GET方式请求，服务器返回的响应码是200，post请求服务器返回的响应码是206（貌似）。
//            if (conn.getResponseCode() == 200) {
//                //返回码为真
//                //从服务器传递过来数据，是一个输入的动作。定义一个输入流，获取从服务器返回的数据
//                InputStream input = conn.getInputStream();
//                File file = createFile(dirName + fileName);
//                output = new FileOutputStream(file);
//                //读取大文件
//                byte[] buffer = new byte[1024 * 20];
//                //记录读取内容
//
//                int len = 0;
//                //从输入六中读取数据,读到缓冲区中
//                while ((len = input.read(buffer)) > 0) {
//                    output.write(buffer, 0, len);
//                }
//
//                output.flush();
//                input.close();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                output.close();
//                return 0;
//            } catch (IOException e) {
//                System.out.println("fail");
//                e.printStackTrace();
//            }
//        }
//        return 1;
//    }
    /**
     * 在SD卡的指定目录上创建文件
     *
     * @param fileName
     */
    public File createFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.createNewFile())
                System.out.println("createFile_OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}