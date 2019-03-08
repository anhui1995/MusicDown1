package xin.xiaoa.musicdown;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Update {
    private static final int INSTALL_PACKAGES_REQUEST_CODE = 3;
    private AlertDialog dialogDis;
    Context context;
    private JSONObject jsonObj;
   // private Notification.Builder builder;
    private NotificationManager manager;
    private Notification notification;
    private int handerVisionCode=0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    installApk(handerVisionCode); //开始安装应用 opendownJson(jsonObj);
                    break;
                case 2:
                    opendownJson(); //检查服务器版本
                    break;
                default:
                    break;
            }
        }
    };
    //handler.sendEmptyMessage(1);

    //构造方法
   public Update( Context con){
       context = con;
       manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
      // builder = new Notification.Builder(context.getApplicationContext());

    }


    private void opendownJson(){
        // System.out.println("openListJson");
        try{
            int versionCode = jsonObj.getInt("versionCode");  //获取服务器版本代码
            String versionContent = jsonObj.getString("versionContent");  //获取本次更新内容
            if(versionCode>MDApplication.getVersionCode()){ // 对比版本号
                askDialog(versionCode,false,versionContent); //询问是否更新
            }
        }catch(Exception e){
            System.out.println("opendownJson错误-"+e);
        }
    }

    //从服务器获取版本号
    private void getVersionCodeThread(){

        //http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=%E8%96%9B%E4%B9%8B%E8%B0%A6&page=1&pagesize=20
        //urlName = "本兮";
        String urlString = MDApplication.getDownVersionCodeUrl();
        BufferedReader reader ;

        StringBuilder stringBuilder = new StringBuilder();
        try {
            // System.out.println(urlString);
            //将字符串形式的path,转换成一个url
            URL url = new URL(urlString);
            //得到url之后，将要开始连接网络，以为是连接网络的具体代码
            //首先，实例化一个HTTP连接对象conn
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //定义请求方式为GET，其中GET的大小写不要搞错了。
            conn.setRequestMethod("GET");
            //定义请求时间，在ANDROID中最好是不好超过10秒。否则将被系统回收。
            conn.setConnectTimeout(6 * 1000);
            //请求成功之后，服务器会返回一个响应码。如果是GET方式请求，服务器返回的响应码是200，post请求服务器返回的响应码是206（貌似）。
            if (conn.getResponseCode() == 200) {
                //返回码为真
                //从服务器传递过来数据，是一个输入的动作。定义一个输入流，获取从服务器返回的数据
                //InputStream input = conn.getInputStream();
                InputStream is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead ;
                while ((strRead = reader.readLine()) != null) {
                    stringBuilder.append(strRead);
                    stringBuilder.append("\n");
                }
                reader.close();
                String result = stringBuilder.toString();
                jsonObj = new JSONObject(result);
                handler.sendEmptyMessage(2);
                is.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVersionCode(){//创建线程,获取服务器版本  getVersionCodeThread();
        new GetVersionCodeThread().start();
    }

    class GetVersionCodeThread extends Thread{
        @Override
        public void run() {
            super.run();
            getVersionCodeThread();
        }
    }

    //安装应用
    private void installApk(int versionCode) {
        if(fileIsExists(MDApplication.getDownUpdatePath()+MDApplication.getDownUpdateFileName())){
            try {

                //System.out.println( "开始执行安装: " + MDApplication.getDownUpdatePath() + MDApplication.getDownUpdateFileName());
                File apkFile = new File(MDApplication.getDownUpdatePath() + MDApplication.getDownUpdateFileName());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    System.out.println( "版本大于 N ，开始使用 fileProvider 进行安装");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(
                            context.getApplicationContext()
                            , "xin.xiaoa.musicdown.FileProvider"
                            , apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                }
                context.startActivity(intent);
                PreferencesUtils.putSharePre(context, "versionCode", versionCode);
            }
            catch(Exception e){System.out.println("安装APK错误"+e);}
        }
        else System.out.println("未下载");
//        Uri installUri = Uri.fromParts("package", "xxx", null);
//        returnIt = new Intent(Intent.ACTION_PACKAGE_ADDED, installUri);
    }

    @TargetApi(Build.VERSION_CODES.P)
    void checkUpdate(){
       //                       MDApplication.getDownUpdatePath()+MDApplication.getDownUpdateFileName()
        if(fileIsExists(MDApplication.getDownUpdatePath()+MDApplication.getDownUpdateFileName())){ //已经下载某一版本

            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(MDApplication.getDownUpdatePath()+MDApplication.getDownUpdateFileName(),
                    PackageManager.GET_ACTIVITIES);
            int versionCode = info.versionCode;
            if(versionCode>MDApplication.getVersionCode()){ // 最新版已经下载，询问是否安装
                //System.out.println("文件存在,不重新下载询问是否安装"+versionCode+">"+MDApplication.getVersionCode());
                askDialog(versionCode,true,""); //询问是否安装
                //return;
            }
            else{  /// 已经下载的版本不大于当前版本，直接删除, 然后去查服务器
                deletefile(MDApplication.getDownUpdatePath()+MDApplication.getDownUpdateFileName());
                //检查服务器
                //System.out.println("文件存在,不重新下载检查服务器");
                getVersionCode();
            }
        }
        else getVersionCode();
        // 没有下载任何版本，查询服务器版本
        // 若需要更新，则提示是否更新
    }

    //申请权限
    private void checkIsAndroidO() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = context.getPackageManager().canRequestPackageInstalls();
            if (!b) {
                //请求安装未知应用来源的权限
                System.out.println("请求安装未知应用来源的权限");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},INSTALL_PACKAGES_REQUEST_CODE);
            }
            else System.out.println("已经有安装应用的权限");
        }
    }
    //询问是否更新的弹窗
    //content 更新内容
    private void askDialog(final int versionCode, final boolean isExists,String content)
    {
        //检查并申请权限
        checkIsAndroidO();
//        TextView textView = new TextView(context);
//        content = "\n" +
//                "就不告诉你我们更新了什么-。-\n" +
//                "\n" +
//                "----------万能的分割线-----------\n" +
//                "\n" +
//                "(ㄒoㄒ) 被老板打了一顿，还是来告诉你吧：\n" +
//
//                "1.下架商品误买了？恩。。。我搞了点小动作就不会出现了\n" +
//                "2.侧边栏、弹框优化 —— 这个你自己去探索吧，总得留点悬念嘛-。-\n";//更新内容
//        textView.setText(content);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//              dialog.setIcon(R.drawable.ic_launcher);//窗口头图标
        if(isExists) {
            dialog.setTitle("发现新版本  (已经下载)");//窗口名
        }
        else {
            dialog.setTitle("发现新版本");//窗口名
            TextView textView = new TextView(context);
            textView.setText(content);
            dialog.setView(textView);


        }
//        dialog.setView(textView);

        dialog.setNegativeButton("更新最新版本", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                //开始更新
                if(isExists){ //已下载
                    installApk(versionCode);
                }
                else {  //下载最新版本
                    handerVisionCode = versionCode;
                    new myTast().start();
                }
            }
        });

        dialog.setNeutralButton("忽略本次更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                //当前版本设为服务器版本
                PreferencesUtils.putSharePre(context, "versionCode", versionCode);
                MDApplication.setVersionCode(versionCode);
            }
        });

        dialog.setPositiveButton("暂时忽略更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
            //空着就行
            }
        });

        dialogDis = dialog.show();
    }


    //下载文件的类
    public class myTast extends Thread implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            String downUrl = "http://www.xiaoa.xin/download/musicdown.apk";
            //System.out.println("开始下载"+downMusicName);
            // TODO Auto-generated method stub
            super.run();
            System.out.println("开始下载更新"+MDApplication.getDownUpdateUrl());
            HttpClient client = new DefaultHttpClient();
            //HttpGet get = new HttpGet(MDApplication.getDownUpdateUrl()); //设置下载地址
            HttpGet get = new HttpGet(downUrl); //设置下载地址

           // System.out.println("麻婆豆腐："+downUrl);
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
                  //  System.out.println("下载存储路径"+MDApplication.getDownPath()+MDApplication.getDownUpdateFileName());
                    File file = createFile(MDApplication.getDownUpdatePath()+MDApplication.getDownUpdateFileName());
                    output = new FileOutputStream(file);

                    inputStream = httpResponse.getEntity().getContent();
                    //准备通知
                    manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    while ((len = inputStream.read(data)) != -1) {
                        len_data += len;
                        System.out.println("len_data:"+len_data+"len:"+len+"progress_value:"+progress_value);
                        progress_value = (int) ((len_data / (float) len_long) * 100);//进度条刻度计算
//                        sendSubscribeMsg(progress_value,"正在下载  "+downMusicName);
//                        getNotification(progress_value,"正在下载  "+downMusicName);
                        manager.notify(2, getNotification(progress_value,"正在下载最新版本"));
                        output.write(data, 0, len);

//                        arrayOutputStream.write(data, 0, data.length);


                        //builder.setProgress(100, progress_value, false);

                        //manager.notify(1000, builder.build());
                    }
                    //manager.notify(2, getNotification(99,downMusicName+"  下载完成"));
                    manager.cancel(2);
                    //manager.notify(2, builder.getNotification(100,"《"+downMusicName+"》  下载完成"));
                    System.out.println("len_data:"+len_data+"len:"+len+"progress_value:"+progress_value);
                    output.flush();
                    inputStream.close();
                    System.out.println("下载完成");
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
    Notification getNotification(int progress_value,String title) {
        long[] vibrates = {0, 0, 0, 0};
        notification = new NotificationCompat.Builder(context, "updateing")
                .setContentTitle(title)
                .setProgress(100, progress_value, false)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_my)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_my))
                .setVibrate(vibrates)
                .setAutoCancel(true)
                .build();
        return notification;
    }
    /**
     * 在SD卡的指定目录上创建文件
     *
     * @param fileName
     */
    private File createFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.createNewFile())
                System.out.println("createFile_OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    /**
     * 删除已存储的文件
     */
    public static boolean deletefile(String fileName) {
        System.out.println("删除已存储的文件"+fileName);
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            return file.delete();
        } catch (Exception e) {
            System.out.println("删除文件错误"+e);
            return false;
        }
    }
    /**
     * 判断文件是否存在
     */
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
