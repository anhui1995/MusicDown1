package xin.xiaoa.musicdown;

public class DownMusicMsg {


    private String mp3Path;
    private String lrc;
    private String musicName;
    private String musicAuthor;
    private String musicType;
    private String musicFrom;
    private int musicTrate;
//    private int musicTimeLength;
//    private String musicAlbumName;
//    private int musicFileSize;

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public String getMusicType() {
        return musicType;
    }

//    public int getMusicFileSize() {
//        return musicFileSize;
//    }
//
//    public void setMusicFileSize(int musicFileSize) {
//        this.musicFileSize = musicFileSize;
//    }
//
//    public String getMusicAlbumName() {
//        return musicAlbumName;
//    }
//
//    public void setMusicAlbumName(String musicAlbumName) {
//        this.musicAlbumName = musicAlbumName;
//    }

    public String getMusicFrom() {return musicFrom;}
    public void setMusicFrom(String musicType) { this.musicFrom = musicType; }

    public int getMusicTrate() {
        return musicTrate;
    }

//    public int getMusicTimeLength() {
//        return musicTimeLength;
//    }

    public void setMusicTrate(int musicTrate) {
        this.musicTrate = musicTrate;
    }

//    public void setMusicTimeLength(int musicTimeLength) {
//        this.musicTimeLength = musicTimeLength;
//    }

    protected void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public void setLrc(String lrcPath) {
        this.lrc = lrcPath;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public String getLrc() {
        return lrc;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }
}
