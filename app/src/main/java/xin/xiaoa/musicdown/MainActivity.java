package xin.xiaoa.musicdown;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import xin.xiaoa.musicdown.adapter.MusicListAdapter;
import  xin.xiaoa.musicdown.adapter.MusicListAdapter.myItemListener;
import xin.xiaoa.musicdown.download.DownMusicMsg;
import xin.xiaoa.musicdown.download.DownloadMusic;
import xin.xiaoa.musicdown.download.DownloadMusicList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    boolean debug=false;
    private static final int GET_UNKNOWN_APP_SOURCES = 10;
    ImageButton buttonSearch; //搜索按钮
    ConstraintLayout layoutSearch;
    String keyword = "";
    Context contextMainActivity;
    ActionBar actionBar;
    private MusicListAdapter listAdapt;
    private List<MusicListItem> musicList = new ArrayList<>();
    private List<SpinnerItem> spinnerList = new ArrayList<>();
    private ListView listView;
    private Spinner spinner;
    private TextView textViewTip;
    private KugouGet kugouGet;
    DisplayMetrics dm = new DisplayMetrics();
    private EditText textViewKeyWord;
    private int pageNum=1;
    private int listviewFirstVisiblePosition=0;
    DownMusicMsg playMusicMsg;
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private static final int INSTALL_PACKAGES_REQUEST_CODE = 3;
    private MusicService musicService;
    private SeekBar seekBar;
    private TextView musicName, playerTime;
    private ImageButton playerButtonPause, playerButtonStop;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)iBinder).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };
    private void bindServiceConnection() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, sc, BIND_AUTO_CREATE);
    }
    public android.os.Handler handlerPlay = new android.os.Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(MDApplication.isPlay())
            {
                playerTime.setText(time.format(musicService.mp.getCurrentPosition()) + "/"
                        + time.format(musicService.mp.getDuration()));
                seekBar.setProgress(musicService.mp.getCurrentPosition());
                handlerPlay.postDelayed(runnable, 300);
            }
            else actionBar.hide();
        }
    };

    private List<DownloadMusicList> downloadMusicLists;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    setListView();
                    break;
                case 2:
                    startPlayService(playMusicMsg.getMp3Path(),playMusicMsg.getMusicName(),playMusicMsg.getMusicAuthor());
                    break;
                default:
                    break;
            }
        }
    };

    void showToast(String str) {
        Toast mToast = Toast.makeText(MainActivity.this, null, Toast.LENGTH_SHORT);
        mToast.setText(str);
        mToast.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextMainActivity = this;
        setContentView(R.layout.activity_main);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        kugouGet = new KugouGet();
        init();
        String musicFrom = PreferencesUtils.getSharePreStr(this, "MusicFrom");
        System.out.println("PreferencesUtils.getSharePreStr:"+musicFrom);
        if("".equals(musicFrom)) { //第一次打开，从未设置过spinner
            PreferencesUtils.putSharePre(this,"MusicFrom","kugou");
            spinner.setSelection(0);
            MDApplication.setMusicFrom("kugou");
            PreferencesUtils.putSharePre(contextMainActivity,"SpinnerListPos",0);
        }
        else {
            int spinnerListPos = PreferencesUtils.getSharePreInt(this,"SpinnerListPos");
            MDApplication.setMusicFrom(musicFrom);
            spinner.setSelection(spinnerListPos);
        }
        //为方便测试而直接搜索本兮
        if(debug){
            textViewKeyWord.setText("张艺兴");
            suchStart();
        }
        musicService = new MusicService();
    }
    @Override
    protected void onResume() {
        if(MDApplication.isPlay())
        {
            musicName.setText(MDApplication.getSeekBarText());
            actionBar.show();
            MDApplication.setActionBar(actionBar);
            handlerPlay.postDelayed(runnable, 30);
        }
        super.onResume();

    }
    @Override
    public void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }

    //所有的初始化
    void init(){
        viewInit();
        permissionCheck();
        filesPathCheck();
        notifyInit();
        actionBarInit();
        versionControl ();
        spinnerInit();
    }
    //spinner初始化
    void spinnerInit(){
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new MySpinnerSelectListener());
        spinnerList.add(new SpinnerItem("kugou","酷狗"));
        spinnerList.add(new SpinnerItem("kuwo","酷我"));
        spinnerList.add(new SpinnerItem("qq","QQ"));
        spinnerList.add(new SpinnerItem("netease","网易"));
        spinnerList.add(new SpinnerItem("xiami","虾米"));
        spinner.setAdapter(new SpinnerAdapter(this, spinnerList));
    }
    class MySpinnerSelectListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id) {
            SpinnerItem item = spinnerList.get(pos);
            String strFrom = item.getStr();
            MDApplication.setMusicFrom(strFrom);
            PreferencesUtils.putSharePre(contextMainActivity,"MusicFrom",strFrom);
            PreferencesUtils.putSharePre(contextMainActivity,"SpinnerListPos",pos);
        }
        @Override
        public void onNothingSelected(AdapterView<?> p1) {}
    }
    //视图初始化
    void viewInit(){
        buttonSearch = findViewById(R.id.button);
        buttonSearch.setOnClickListener(new MyOnClickListener());
        layoutSearch = findViewById(R.id.constraintLayout);
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new itemClickListener());
        listView.setOnItemLongClickListener(new itemClickLongListener());
        textViewKeyWord = findViewById(R.id.mainkeyword);
        textViewKeyWord.setText("");
        textViewTip = findViewById(R.id.main_free);
    }
    //权限检查
    void permissionCheck(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }
    //文件夹检查
    void filesPathCheck(){

        File externalFilesDir = getExternalFilesDir(null);
        //System.out.println("文件"+externalFilesDir.getPath());
        assert externalFilesDir != null;

        if (externalFilesDir.getPath() != null){
            String tmpPath = externalFilesDir.getPath();

            MDApplication.setDownUpdatePath(tmpPath + "/update/");
            String[] tmp = tmpPath.split("/Android/data/xin.xiaoa.musicdown");

            System.out.println("文件:"+tmpPath);
            MDApplication.setDownPath(tmp[0] + "/DownMusic");
        }
        else System.out.println("###################$$$$$$$$$$");
        System.out.println("下载得得得文件:"+MDApplication.getDownPath());
        makeRootDirectory(MDApplication.getDownPath());
        makeRootDirectory(MDApplication.getDownUpdatePath());
    }
    //  版本控制 versionCode
    void versionControl (){
        MDApplication.setDownUpdateUrl("https://www.xiaoa.top/download/musicdown.apk");
        MDApplication.setDownUpdateFileName("musicdown.apk");
        MDApplication.setDownVersionCodeUrl("https://www.xiaoa.top/download/");
        int saveVersionCode= PreferencesUtils.getSharePreInt(this, "versionCode");//用户名
        int ApkVersionCode = APKVersionCodeUtils.getVersionCode(this);
        System.out.println(ApkVersionCode+">"+saveVersionCode);
        if(ApkVersionCode>saveVersionCode) MDApplication.setVersionCode(ApkVersionCode);
        else MDApplication.setVersionCode(saveVersionCode);
        Update update1 = new Update(this);
        update1.checkUpdate();
    }
    //ActionBar初始化
    void actionBarInit(){
        actionBar =getSupportActionBar();
        //actionBar.hide();
        assert actionBar != null;
        // actionBar.setTitle("天气");
        //  actionBar.setTit
        // 左侧图标点击事件使能
        actionBar.setHomeButtonEnabled(true);
        // 使左上角图标(系统)是否显示
        actionBar.setDisplayShowHomeEnabled(false);
        // 显示标题
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        @SuppressLint("InflateParams") View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.actionbar_layout, null);
        actionBar.setCustomView(actionbarLayout);
        actionBar.hide();

        bindServiceConnection();
        seekBar = this.findViewById(R.id.playerSeekBar);
        seekBar.setProgress(musicService.mp.getCurrentPosition());
        seekBar.setMax(musicService.mp.getDuration());

        musicName = this.findViewById(R.id.playerName);
        playerTime = this.findViewById(R.id.playerTime);
        playerButtonStop = this.findViewById(R.id.playerButtonStop);
        playerButtonPause = this.findViewById(R.id.playerButtonPause);
        playerButtonStop.setOnClickListener(new ActionBarClickListener());
        playerButtonPause.setOnClickListener(new ActionBarClickListener());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        musicService.mp.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
    }
    //通知初始化
    void notifyInit(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "updateing";
            String channelName = "更新下载";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "player";
            channelName = "播放器";
            importance = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "player123";
            channelName = "播放器";
            importance = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "downloading";
            channelName = "下载进度";
            importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    //播放本地音乐
    @SuppressLint("SetTextI18n")
    void startPlayLoction(String paht, String name, String author){
       // System.out.println("在线试听4+startPlay");
        //System.out.println("当前播放"+paht);

        if(musicService.showNotification){
            musicService.clearNotification();
        }

        //musicService = new MusicService();
        playerButtonPause.setImageResource(R.drawable.actionbar_play);
        musicService.setPath(paht,this,playerButtonPause,name,author);
        MDApplication.setActionBar(actionBar);
        musicName.setText("《"+name+"》- "+author);
        MDApplication.setSeekBarText("《"+name+"》- "+author);
        actionBar.show();
        musicService.playStart();
        seekBar.setMax(musicService.mp.getDuration());
        handlerPlay.postDelayed(runnable, 300);
    }
    //播放在线音乐
    @SuppressLint("SetTextI18n")
    void startPlayService(String paht, String name, String author){
        if(playMusicMsg.getErrorCode() == 200){

            if(musicService.showNotification){
                musicService.clearNotification();
            }

            //musicService = new MusicService();
            playerButtonPause.setImageResource(R.drawable.actionbar_play);
            musicService.setPath(paht,this,playerButtonPause,name,author);
            MDApplication.setActionBar(actionBar);
            musicName.setText("《"+name+"》- "+author);
            MDApplication.setSeekBarText("《"+name+"》- "+author);
            actionBar.show();
            musicService.playStart();
            seekBar.setMax(musicService.mp.getDuration());
            handlerPlay.postDelayed(runnable, 300);
        }
        else {
            showToast("这个曲库暂时不能试听，换个来源试试");
        }
    }

    //ActionBar中按钮的点击事件
    class ActionBarClickListener implements View.OnClickListener{
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.playerButtonPause:
                    musicService.playOrPause();
                    break;
                case R.id.playerButtonStop:
                    musicService.stop();
                    seekBar.setProgress(0);
                    actionBar.hide();
//                    handler.removeCallbacks(runnable);
//                    unbindService(sc);
                    break;
                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

    //权限申请回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                System.out.println("申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
            makeRootDirectory(MDApplication.getDownPath());
        }

        switch (requestCode) {
            case INSTALL_PACKAGES_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ;//mMainPresenter.installApk();
                } else {
                    //  引导用户手动开启安装权限
                    System.out.println("报名:"+APKVersionCodeUtils.getPackageName(this));
                    //Uri packageURI = Uri.parse(APKVersionCodeUtils.getPackageName(this)); //设置包名，可直接跳转当前软件的设置页面
                    Uri packageURI = Uri.parse("package:"+getPackageName());
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
//                    startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);  //ok
//                  Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                    intent.setData(packageURI);   ok
//                    intent.setDataAndNormalize(packageURI);  ok
                    startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                }
                break;
            default:
                break;

        }


    }

    //隐藏键盘之类相关方法(下面)
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }
    //是否需要隐藏键盘
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isSoftShowing() && isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return super.dispatchTouchEvent(ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    //隐藏键盘之类相关方法(上面)

    //每一个条目上按钮的点击事件
    private myItemListener mListener = new myItemListener() {

        public void myOnClick(int i, View v) {
            MusicListItem tmp = musicList.get(i);
            if(tmp.isLastItem()){        //点击的是下一页
                new GetNextPageThread().start();
            }
            else {      //点击的是普通条目
                downloadMusicLists = new ArrayList<>();
                downloadMusicLists.add(new DownloadMusicList(tmp.getMusicID(),"《"+tmp.getMusicName()+"》"));
                new DownloadMusic().downLoad(contextMainActivity,downloadMusicLists);
            }
        }
    };

    //获取到搜索结果后生成ListView
    void setListView(){
        listAdapt = new MusicListAdapter(this, musicList, mListener);
        listView.setAdapter(listAdapt);
        //listView.getFirstVisiblePosition()
        listView.setSelection(listviewFirstVisiblePosition);
    }

    //获取第一页搜索结果
    void getFirstPage(){
        listviewFirstVisiblePosition=0;
        musicList = kugouGet.getFirstPage(keyword);
        pageNum=1;
        handler.sendEmptyMessage(1);
    }

    //获取其他页的ListView
    void getNextPage(){
        listviewFirstVisiblePosition=listView.getFirstVisiblePosition();
        pageNum++;
        musicList = kugouGet.getNextPage(keyword,pageNum,musicList);
        handler.sendEmptyMessage(1);
    }

    //请求搜索结果的线程
    class GetFirstPageThread extends Thread{ public void run(){ getFirstPage();}}
    class GetNextPageThread extends Thread{ public void run(){ getNextPage();}}

    //搜索按钮点击事件的服务方法
    void suchStart(){
        //new KugouGet().get();
        textViewKeyWord.clearFocus();
        keyword = textViewKeyWord.getText().toString();
        if(keyword.equals("")){
            showToast("多少写一点关键字吧");
            return;
        }
        new GetFirstPageThread().start();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layoutSearch.getLayoutParams();
        params.setMargins(0, 43, 0, 0);
        layoutSearch.setLayoutParams(params);
        textViewTip.setVisibility(View.INVISIBLE);
    }

    //搜索按钮点击事件的监听类
    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    suchStart();
                    break;
                default:
                    break;
            }
        }
    }

    //ListView长按事件的监听类 弹出下载对话框
    class itemClickLongListener implements AdapterView.OnItemLongClickListener {
        public boolean onItemLongClick(AdapterView<?> adapter, View view, int i, long id) {
            MusicListItem tmp = musicList.get(i);
            if(tmp.isLastItem()) return true;
            downloadMusicLists = new ArrayList<>();
            downloadMusicLists.add(new DownloadMusicList(tmp.getMusicID(),"《"+tmp.getMusicName()+"》"));
            new DownloadMusic().downLoad(contextMainActivity,downloadMusicLists);
            return true;
        }
    }

    //ListView点击事件的监听类 试听音乐
    class itemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> adapter, View view, int i, long id) {
            MusicListItem tmp = musicList.get(i);
            String path = MDApplication.getDownPath()+"/"+tmp.getMusicAuthor()+"-"+tmp.getMusicName()+".mp3";
            if(fileIsExists(path))
                startPlayLoction(path,tmp.getMusicName(),tmp.getMusicAuthor());
            else new PlayThread(tmp.getMusicID()).start();
        }
    }

    //通过歌曲hash获取歌曲链接的线程
    class PlayThread extends Thread{
        String hash;
        public PlayThread(String tmp){
            hash=tmp;
        }
        @Override
        public void run() {
            super.run();
            playMusicMsg = new KugouGet().getDownMusicMsg(hash);
            handler.sendEmptyMessage(2);
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

    //生成文件夹
    void makeRootDirectory(String filePath) {
        File file;

        try {
            file = new File(filePath);
            if (!file.exists()) {//判断指定的路径或者指定的目录文件是否已经存在。
                if (file.mkdir())//建立文件夹
                    System.out.println("新建文件夹成功,路径为：" + filePath);
                else System.out.println("新建文件夹失败");
            }
        } catch (Exception e) {
            System.out.println("makeRootDirectory异常" + e);
        }
    }


}
