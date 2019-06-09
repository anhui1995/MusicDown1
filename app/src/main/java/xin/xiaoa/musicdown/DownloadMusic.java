package xin.xiaoa.musicdown;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DownloadMusic {


    File externalFilesDir;

    AlertDialog dialogDis;
    private String hash = "";
    private String toastMsg;
    boolean lrc = false;
    private Context context1;
    private ListView listView;
    private DownMusicMsg downMusicMsg;
    private  DownloadMusicAdapter downloadMusicAdapter;
    private List<DownloadMusicList> lists;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {// 消息队列
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                Toast mToast = Toast.makeText(context1, null, Toast.LENGTH_SHORT);
                mToast.setText(toastMsg);
                mToast.show();
            }
        }
    };

    private void toast(String str){
        toastMsg=str;
        handler.sendEmptyMessage(1);
    }
    void downLoad(Context context,List<DownloadMusicList> inLists)
    {
        lists=inLists;
        context1=context;
        listView = new ListView(context);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        downloadMusicAdapter = new DownloadMusicAdapter(context,lists);

        listView.setLayoutParams(lp);
        listView.setAdapter(downloadMusicAdapter);
        listView.setOnItemClickListener(new myItemClickListener());
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//              dialog.setIcon(R.drawable.ic_launcher);//窗口头图标
        dialog.setTitle("确认下载");//窗口名
        dialog.setView(listView);



//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
//        { 	@Override
//        public void onDismiss(DialogInterface dialog)
//        {
//            butf5();
//        }
//        });

//        dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which)
//            {
//
//            }
//        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO Auto-generated method stub

            }
        });

        dialogDis = dialog.show();
    }
//    class itemClickListener implements AdapterView.OnItemClickListener {
//
//        public void onItemClick(AdapterView<?> adapter, View view, int i, long id) {
//            DownloadMusicList tmp = lists.get(i);
//
//            System.out.println("短i:"+i+"id:"+id+" - "+ tmp.getMusicName());
//        }
//    }
    class myItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> adapter, View view, int i, long id) {
            DownloadMusicList tmp = lists.get(i);
            hash = tmp.getHash();
            new MyThread().start();
            //dialog.dismiss();
            dialogDis.dismiss();
            //
            //
            // System.out.println("短i:"+tmp.getName());
        }
    }
    class MyThread extends Thread{
        public void run(){
            down();
        }
    }


    private void down(){

        downMusicMsg=new KugouGet().getDownMusicMsg(hash);
        if(fileIsExists(MDApplication.getDownPath()+"/"+downMusicMsg.getMusicAuthor()+"-"+downMusicMsg.getMusicName() + ".mp3")){
            System.out.println("文件已经存在");
            toast(downMusicMsg.getMusicAuthor()+"-"+downMusicMsg.getMusicName()+downMusicMsg.getMusicType()+"已经下载");
        }
        else
//            downloadFile(MDApplication.getDownPath(),downMusicMsg.getMusicAuthor()+"-"+downMusicMsg.getMusicName() + ".mp3",downMusicMsg.getMp3Path());
        {
            Intent intent = new Intent(context1, GetMusic.class);
            intent.putExtra("downUrl", downMusicMsg.getMp3Path());//传递一个参数
            intent.putExtra("downMusicName", downMusicMsg.getMusicName());//传递一个参数
            intent.putExtra("downMusicAuthor", downMusicMsg.getMusicAuthor());//传递一个参数
            intent.putExtra("downMusicPath", MDApplication.getDownPath());//传递一个参数
            intent.putExtra("downMusicType", downMusicMsg.getMusicType());//传递一个参数
            context1.startService(intent);//开启服务

//            System.out.println("下载1:"+downMusicMsg.getMp3Path());
//            System.out.println("下载2:"+downFileName);
//            System.out.println("####################");
        }

       // MDApplication.getDownPath();

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

    /**
     * 下载指定路径的文件，并写入到指定的位置
     * 返回0表示下载成功，返回1表示下载出错
     */
    public int downloadFile(String dirName, String fileName, String urlStr) {



        OutputStream output = null;
        try {
            //将字符串形式的path,转换成一个url
            URL url = new URL(urlStr);
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
                InputStream input = conn.getInputStream();
                File file = createFile(dirName + fileName);
                output = new FileOutputStream(file);
                //读取大文件
                byte[] buffer = new byte[1024 * 20];
                //记录读取内容

                int len = 0;
                //从输入六中读取数据,读到缓冲区中
                while ((len = input.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }

                output.flush();
                input.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                return 0;
            } catch (IOException e) {
                System.out.println("fail");
                e.printStackTrace();
            }
        }
        return 1;
    }

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
