package xin.xiaoa.musicdown;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;

public class MusicService extends Service {


    private RemoteViews remoteViews;
    private NotificationManager notificationManager;
    private Notification notification;
    boolean showNotification = false;
    private Context context;
    ImageButton actionBarButtonPlayAndPause;

    public android.os.Handler handlerPlay = new android.os.Handler();
    //通知栏刷新线程
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String notificationTime = time.format(mp.getCurrentPosition()) + "/"
               + time.format(mp.getDuration());
            remoteViews.setTextViewText(R.id.notificationtime, notificationTime);         // 设置时间显示
            Intent mainIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
            long[] vibrates = {0};
            notification = new NotificationCompat.Builder(context, "player123")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setContent(remoteViews)
                    .setVibrate(vibrates)
                    .setSmallIcon(R.mipmap.ic_launcher_my)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_my))
                    .setOngoing(true)
                    .build();
            if(showNotification) {
                notificationManager.notify(5, notification);
                if(mp.isPlaying()) handlerPlay.postDelayed(runnable, 300);
            }
        }
    };

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");
    public final IBinder binder = new MyBinder();

    //返回这个服务
    public class MyBinder extends Binder{
        MusicService getService() {
            return MusicService.this;
        }
    }

    //歌曲播放完成后的后续处理
    void actionBarHide(){
        MDApplication.getActionBar().hide();
        MDApplication.setPlay(false);
        showNotification = false;
        notificationManager.cancel(5);
        unregisterReceiver(playMusicReceiver);
    }
    public static MediaPlayer mp = new MediaPlayer();

    //初始化播放器，准备播放音乐
    void setPath(String paht,Context cont,ImageButton imageButton,String name,String author){
        context =cont;
        actionBarButtonPlayAndPause = imageButton;
        //设置歌曲播放完成监听 QWVQ
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                actionBarHide();
            }
        });
        try {

            mp.reset();
            mp.setDataSource(paht);
            //mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/仙剑奇侠传六-主题曲-《剑客不能说》.mp3");
            //mp.setDataSource(Environment.getDataDirectory().getAbsolutePath()+"/You.mp3");
            mp.prepare();
            mp.start();
            MDApplication.setPlay(true);
            showNotification = true;
            //注册通知栏按钮事件监听
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MUSICDOWN_ACTION_PLAY_AND_PAUSE);
            intentFilter.addAction(MUSICDOWN_ACTION_STOP);
            context.registerReceiver(playMusicReceiver,intentFilter);
            initPlayNotification(context,name,author);
            handlerPlay.postDelayed(runnable, 300);
        } catch (Exception e) {
            System.out.println("can't asdsad"+e);
            e.printStackTrace();
        }
    }
    public MusicService() {
        //mp = new MediaPlayer();
    }
    public void playStart() {
          //  mp.start();
    }
    public void playOrPause() {
        if(mp.isPlaying()){
            mp.pause();
            remoteViews.setImageViewResource(R.id.notificationplayandpause, R.drawable.actionbar_pause);
            actionBarButtonPlayAndPause.setImageResource(R.drawable.actionbar_pause);
        } else {
            mp.start();
            remoteViews.setImageViewResource(R.id.notificationplayandpause, R.drawable.actionbar_play);
            actionBarButtonPlayAndPause.setImageResource(R.drawable.actionbar_play);
        }
        handlerPlay.postDelayed(runnable, 10);
    }

    public void stop() {
        try{
            if(mp != null) {
                mp.stop();
                MDApplication.setPlay(false);
                showNotification = false;
                notificationManager.cancel(5);
                context.unregisterReceiver(playMusicReceiver);
                try {
                    mp.prepare();
                    mp.seekTo(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            System.out.println("stop问题:"+e);
        }
    }

//    public void nextMusic() {
//        if(mp != null && musicIndex < 3) {
//            mp.stop();
//            try {
//                mp.reset();
//                mp.setDataSource(musicDir[musicIndex+1]);
//                musicIndex++;
//                mp.prepare();
//                mp.seekTo(0);
//                mp.start();
//            } catch (Exception e) {
//                Log.d("hint", "can't jump next music");
//                e.printStackTrace();
//            }
//        }
//    }
//    public void preMusic() {
//        if(mp != null && musicIndex > 0) {
//            mp.stop();
//            try {
//                mp.reset();
//                mp.setDataSource(musicDir[musicIndex-1]);
//                musicIndex--;
//                mp.prepare();
//                mp.seekTo(0);
//                mp.start();
//            } catch (Exception e) {
//                Log.d("hint", "can't jump pre music");
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        super.onDestroy();
    }
//$###############################################################################################################################################################################################
    void initPlayNotification(Context context,String name,String author){
        //initNotification();

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        try{
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notificationname, "《"+name+"》");         // 设置标题显示
            remoteViews.setTextViewText(R.id.notificationauthor,author);         // 设置歌手显示
            remoteViews.setTextViewText(R.id.notificationtime, "1:32/3:45");         // 设置时间显示
            //播放/暂停添加点击监听
            Intent playPauseButtonIntent = new Intent(MUSICDOWN_ACTION_PLAY_AND_PAUSE);
            PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, 0, playPauseButtonIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.notificationplayandpause, playPausePendingIntent);

            //退出监听
            Intent exitButton = new Intent(MUSICDOWN_ACTION_STOP);
            PendingIntent pendingExitButtonIntent = PendingIntent.getBroadcast(context,0,exitButton,0);
            remoteViews.setOnClickPendingIntent(R.id.notificationstop,pendingExitButtonIntent);

        }
        catch(Exception e){
            System.out.println("remoteViews错误"+e);
        }
        long[] vibrates = {0};
        notification = new NotificationCompat.Builder(context, "player123")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher_my)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_my))
                .setVibrate(vibrates)
                .setOngoing(true)
                .build();

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(5, notification);
       // System.out.println("播放器控件通知OKOKOKOKOKOKOk");
    }

    Notification getNotificationlay() {
        notification = new NotificationCompat.Builder(context, "player123")
               // .setContentTitle(title)
                //.setProgress(100, progress_value, false)
//                .setContentText("地铁沿线30万商铺抢购中！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_my)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_my))
//               .setSmallIcon(R.drawable.ic_launcher_background)
//               .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setAutoCancel(true)
                .build();
        return notification;
    }
    //初始化通知栏播放器控件 setPath()
    void initNotification() {

        notification = new Notification();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, mainIntent, 0);
//

        notification.when = System.currentTimeMillis();
        notification.contentIntent = pi;//点击通知跳转到MainActivity
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.notificationname, MDApplication.getNotificationName());         // 设置标题显示
        remoteViews.setTextViewText(R.id.notificationauthor, MDApplication.getNotificationAuthor());         // 设置歌手显示

        //播放/暂停添加点击监听
        Intent playPauseButtonIntent = new Intent(MUSICDOWN_ACTION_PLAY_AND_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseButtonIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.notificationplayandpause, playPausePendingIntent);

        //退出监听
        Intent stopButtonIntent = new Intent(MUSICDOWN_ACTION_STOP);
        PendingIntent pendingExitButtonIntent = PendingIntent.getBroadcast(this,0,stopButtonIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.notificationstop,pendingExitButtonIntent);
    }
    String MUSICDOWN_ACTION_PLAY_AND_PAUSE = "ACTION_PLAY_AND_PAUSE";
    String MUSICDOWN_ACTION_STOP = "ACTION_STOP";
    //通知栏播放器控件事件监听
    private BroadcastReceiver playMusicReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try{
                String action = intent.getAction();
               // System.out.println("通知栏播放器控件事件监听:"+action);
                if (action.equals(MUSICDOWN_ACTION_STOP)) {
                    stop();
                }
                else if(action.equals(MUSICDOWN_ACTION_PLAY_AND_PAUSE)){
                    playOrPause();
                }
            }catch(Exception e){
                System.out.println("123456df"+e);
            }
        }

    };
    /**
     * onBind 是 Service 的虚方法，因此我们不得不实现它。
     * 返回 null，表示客服端不能建立到此服务的连接。
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}




//通知栏播放器控件
//    public class myTast12 extends Thread implements Runnable {
//        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            super.run();
//            try {
//                //准备通知
//                notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//                progress_value = (int) ((len_data / (float) len_long) * 100);//进度条刻度计算
//                notificationManager.notify(3, getNotificationPlay(progress_value,"正在下载最新版本"));
//                notificationManager.cancel(3);
//
//                try {
//                    handler.sendEmptyMessage(1);
//                } catch (Exception e) {
//                    System.out.println("fail");
//                    System.out.println("error151"+e);
//                }
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                System.out.println("error156"+e);
//            }
//        }
//    }

//    Notification getNotificationPlay(int progress_value, String title) {
//        notification = new NotificationCompat.Builder(context, "updateing")
//                .setContentTitle(title)
//                .setCustomBigContentView(new RemoteViews())
//                .setAutoCancel(true)
//                .build();
//        return notification;
//    }

//
//    String ACTION_PLAY_AND_PAUSE = "ACTION_PLAY_AND_PAUSE";
//    String ACTION_STOP = "ACTION_STOP";
//    //通知栏播放器控件事件监听

//    private BroadcastReceiver playMusicReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            System.out.println("通知栏播放器控件事件监听:"+action);
////            if (action.equals(ACTION_NEXT_SONG)) {
////                nextSong();
////            }
//        }
//
//    };
//
//    //初始化通知栏播放器控件 setPath()
//    void initNotification() {
//        //NotificationManager的获取
////        notification = new NotificationCompat.Builder(context, "player");
//        notification = new Notification();
//        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//
//        Intent mainIntent = new Intent(context, MainActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(context, 0, mainIntent, 0);
////
//
//        notification.when = System.currentTimeMillis();
//        notification.contentIntent = pi;//点击通知跳转到MainActivity
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
//        remoteViews.setTextViewText(R.id.notificationname, MDApplication.getNotificationName());         // 设置标题显示
//        remoteViews.setTextViewText(R.id.notificationauthor, MDApplication.getNotificationAuthor());         // 设置歌手显示
////        remoteViews.setTextViewText(R.id.notificationtime, strTime);         // 设置时间显示
////        contentViews.setOnClickPendingIntent(R.id.notificationplaytag, pi);
////        contentViews.setOnClickPendingIntent(R.id.notificationcurrentmusic, pi);
//
//        //播放/暂停添加点击监听
//        Intent playPauseButtonIntent = new Intent(ACTION_PLAY_AND_PAUSE);
//        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseButtonIntent, 0);
//        remoteViews.setOnClickPendingIntent(R.id.notificationplayandpause, playPausePendingIntent);
//
//        //退出监听
//        Intent exitButton = new Intent(ACTION_STOP);
//        PendingIntent pendingExitButtonIntent = PendingIntent.getBroadcast(this,0,exitButton,0);
//        remoteViews.setOnClickPendingIntent(R.id.notificationstop,pendingExitButtonIntent);
//    }
//    String ACTION_PLAY_AND_PAUSE = "ACTION_PLAY_AND_PAUSE";
//    String ACTION_STOP = "ACTION_STOP";
//    //通知栏播放器控件事件监听
//    private BroadcastReceiver playMusicReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            System.out.println("通知栏播放器控件事件监听:"+action);
////            if (action.equals(ACTION_NEXT_SONG)) {
////                nextSong();
////            }
//        }
//
//    };
//
//    //循环显示
//    private void showNotification() {
//
//        if(mp.isPlaying()){
//            remoteViews.setImageViewResource(R.id.notificationplayandpause,R.drawable.actionbar_play);
//        }
//        else{
//            remoteViews.setImageViewResource(R.id.notificationplayandpause,R.drawable.actionbar_pause);
//        }
////        remoteViews.setTextViewText(R.id.notificationname, MDApplication.getNotificationName());         // 设置标题显示
////        remoteViews.setTextViewText(R.id.notificationauthor, MDApplication.getNotificationAuthor());         // 设置时间显示
//
//        String notificationTime = time.format(mp.getCurrentPosition()) + "/"
//                + time.format(mp.getDuration());
//        remoteViews.setTextViewText(R.id.notificationtime, notificationTime);         // 设置时间显示
//
//        notification.contentView = remoteViews;
//
//        notificationManager.notify(NOTIFICATION_ID, notification);//调用notify方法后即可显示通知
//    }
//

//    Notification getNotificationPlay(int progress_value, String title) {
//        notification = new NotificationCompat.Builder(context, "updateing")
//                .setContentTitle(title)
//                .setProgress(100, progress_value, false)
////                .setContentText("地铁沿线30万商铺抢购中！")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher_my)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_my))
//
//                .setAutoCancel(true)
//                .build();
//        return notification;
//    }


