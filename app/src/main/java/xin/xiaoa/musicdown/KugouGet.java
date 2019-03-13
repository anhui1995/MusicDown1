package xin.xiaoa.musicdown;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KugouGet {

    String urlType = "kugou";
    String urlName = "";
    String urlAuthor = "";

    String musicName = "";
    String musicAuthor = "";
    int musicTimeLength = 0;
    String musicTrate = "";
    String musicHash = "";
    String musicFileSize = "";
    MusicListItem musicListItem;
    private List<MusicListItem> kugouMusicList;
    DownMusicMsg downMusicMsg;
//    private void myMain(){
//        System.out.println("KugouGet_myMain()");
//        //getSuchList();
//    }

//    void get(){ new MyThread().start();}
//    class MyThread extends Thread{ public void run(){ myMain();}}
    List<MusicListItem> getFirstPage(String such){
        kugouMusicList = new ArrayList<>();
        getSuchList(such,1);
        return kugouMusicList;
    }

    List<MusicListItem> getNextPage(String such,int page,List<MusicListItem> oldLists){
        kugouMusicList = oldLists;
        if(kugouMusicList.size()>1)
            kugouMusicList.remove(kugouMusicList.size() - 1);
        getSuchList(such,page);
        return kugouMusicList;
    }

    public void getSuchList(String such,int page) {
        //http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=%E8%96%9B%E4%B9%8B%E8%B0%A6&page=1&pagesize=20

        String urlString = "http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=" + such+ "&page="+page+"&pagesize=15";

        BufferedReader reader ;

        StringBuilder stringBuilder = new StringBuilder();
        try {
            System.out.println(urlString);
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
                JSONObject jsonObj = new JSONObject(result);
                openListJson(jsonObj);
                is.close();
            }
            else System.out.println("响应码getSuchList："+conn.getResponseCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    void openListJson(JSONObject json){
        System.out.println("openListJson");
        try{
            JSONObject musicMsgTmp;
            JSONArray info;
            info=json.getJSONObject("data").getJSONArray("info");
            System.out.println("#############");
            for(int info_i = 0; info_i<info.length();info_i++){
                musicMsgTmp = info.getJSONObject(info_i);
                if(musicMsgTmp.getInt("privilege")>9)
                    continue;
                musicListItem = new MusicListItem();
                musicListItem.setMusicName( musicMsgTmp.getString("songname"));
                musicAuthor = musicMsgTmp.getString("singername");
                musicListItem.setMusicAuthor( musicMsgTmp.getString("singername"));
                musicListItem.setMusicAlbumName( musicMsgTmp.getString("album_name"));

                musicListItem.setMusicHash( musicMsgTmp.getString("hash"));
                musicListItem.setMusicHash320( musicMsgTmp.getString("320hash"));
                musicListItem.setMusicHashSq( musicMsgTmp.getString("sqhash"));
                musicListItem.setMusicHash320Price( musicMsgTmp.getInt("price_320"));
                musicListItem.setMusicHashSqPrice( musicMsgTmp.getInt("price_sq"));
                musicListItem.setTheLastItem(false);
                //System.out.println("musicListItem.320hash:"+musicListItem.getMusicName()+musicListItem.getMusicHash320());
                kugouMusicList.add(musicListItem);
//                musicName = musicMsgTmp.getString("songname");
//                musicAuthor = musicMsgTmp.getString("singername");
//                musicTimeLength = musicMsgTmp.getInt("duration");
//                musicHash = musicMsgTmp.getString("hash");
//                System.out.println("music:"+musicAuthor+" - "+musicName);
               // getDownMsg(musicHash);
            }
            musicListItem = new MusicListItem();
            musicListItem.setTheLastItem(true);
            kugouMusicList.add(musicListItem);
            System.out.println("#############");
        }catch(Exception e){
            System.out.println("openJson错误-"+e);
        }
    }



    public void getDownMsg(String hash) {
        //http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=%E8%96%9B%E4%B9%8B%E8%B0%A6&page=1&pagesize=20
        //urlName = "本兮";
        String urlString = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + hash;
        System.out.println("麻婆豆腐hash:"+hash);
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
                JSONObject jsonObj = new JSONObject(result);
                opendownJson(jsonObj);
                is.close();
            }
            else System.out.println("响应码getDownMsg："+conn.getResponseCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    DownMusicMsg getDownMusicMsg(String hash){
        downMusicMsg = new DownMusicMsg();
        getDownMsg(hash);
        return downMusicMsg;
    }

    void opendownJson(JSONObject json){
       // System.out.println("openListJson");
        try{
            JSONObject data;
            data=json.getJSONObject("data");
           // String musicDown = data.getString("play_url");
            //System.out.println(musicDown);
            downMusicMsg.setMusicName(data.getString("song_name"));  //获取歌名
            downMusicMsg.setMusicAuthor(data.getString("author_name"));  //歌手名
            downMusicMsg.setMp3Path(data.getString("play_url"));  //下载地址
            downMusicMsg.setMusicFrom("酷狗");  //音乐来源
            downMusicMsg.setMusicType(".mp3");  //音乐后缀
            downMusicMsg.setMusicFileSize(data.getInt("filesize"));   //文件大小
            downMusicMsg.setMusicTimeLength(data.getInt("timelength"));  //歌曲时长
            downMusicMsg.setLrc(data.getString("lyrics"));  //歌词

        }catch(Exception e){
            System.out.println("openJson错误-"+e);
        }
    }



}
