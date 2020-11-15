package xin.xiaoa.musicdown;

import android.app.Application;
import android.support.v7.app.ActionBar;


public class MDApplication extends Application {



    public static Config config = new Config();
    private static String downPath;
    private static String downUpdatePath;
    private static String downUpdateFileName;
    private static int versionCode=0;
    private static String downUpdateUrl;
    private static String downVersionCodeUrl;
    private static MusicService musicService = null;
    private static boolean isPlay = false;
    private static ActionBar actionBar;

    private static String notificationName;
    private static String notificationAuthor;
    private static String musicFrom;

    private static boolean showNotification = false;

    public static String getMusicFrom() {
        return musicFrom;
    }

    public static void setMusicFrom(String musicFrom) {
        MDApplication.musicFrom = musicFrom;
    }








//    public static  boolean isShowNotification() {
//        return showNotification;
//    }
//
//    public static void setShowNotification(boolean showNotification) {
//        MDApplication.showNotification = showNotification;
//    }

    public static String getNotificationName() {
        return notificationName;
    }

    public static void setNotificationName(String notificationName) {
        MDApplication.notificationName = notificationName;
    }

    public static String getNotificationAuthor() {
        return notificationAuthor;
    }

    public static void setNotificationAuthor(String notificationAuthor) {
        MDApplication.notificationAuthor = notificationAuthor;
    }


    public static ActionBar getActionBar() {
        return actionBar;
    }

    public static void setActionBar(ActionBar actionBar) {
        MDApplication.actionBar = actionBar;
    }

    public static boolean isPlay() {
        return isPlay;
    }

    public static void setPlay(boolean play) {
        isPlay = play;
    }

    private static String seekBarText="";

    public static String getSeekBarText() {
        return seekBarText;
    }

    public static void setSeekBarText(String seekBarText) {
        MDApplication.seekBarText = seekBarText;
    }
    public static MusicService getMusicService() {
        return musicService;
    }
    public static void setMusicService(MusicService musicService) {
        MDApplication.musicService = musicService;
    }


    @SuppressWarnings("unused")
    public static String getDownVersionCodeUrl() {
        return downVersionCodeUrl;
    }
    @SuppressWarnings("unused")
    public static void setDownVersionCodeUrl(String downVersionCodeUrl) {
        MDApplication.downVersionCodeUrl = downVersionCodeUrl;
    }
    @SuppressWarnings("unused")
    public static String getDownUpdateUrl() {
        return downUpdateUrl;
    }
    @SuppressWarnings("unused")
    public static void setDownUpdateUrl(String downUpdateUrl) {
        MDApplication.downUpdateUrl = downUpdateUrl;
    }
    @SuppressWarnings("unused")
    public static String getDownUpdateFileName() {
        return downUpdateFileName;
    }
    @SuppressWarnings("unused")
    public static void setDownUpdateFileName(String downUpdateFileName) {
        MDApplication.downUpdateFileName = downUpdateFileName;
    }
    @SuppressWarnings("unused")
    public static String getDownUpdatePath() {
        return downUpdatePath;
    }
    @SuppressWarnings("unused")
    public static void setDownUpdatePath(String downUpdatePath) {
        MDApplication.downUpdatePath = downUpdatePath;
    }
    @SuppressWarnings("unused")
    public static int getVersionCode() {
        return versionCode;
    }
    @SuppressWarnings("unused")
    public static void setVersionCode(int versionCode) {
        MDApplication.versionCode = versionCode;
    }

    @SuppressWarnings("unused")
    public static String getDownPath() {
        return downPath;
    }

    @SuppressWarnings("unused")
    public static void setDownPath(String tmp) {
        MDApplication.downPath = tmp;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

}
