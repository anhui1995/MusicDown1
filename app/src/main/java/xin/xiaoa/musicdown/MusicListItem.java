package xin.xiaoa.musicdown;

public class MusicListItem {

    private String mp3Path;
    private String lrcPath;
    private String musicName;
    private String musicAuthor;
    private String musicType;
    private String musicTrate;
    private String musicTimeLength;
    private String musicAlbumName;
    private String musicHash;
    private String musicHash320;
    private String musicHashSq;
    private int musicHash320Price;
    private int musicHashSqPrice;

    private int musicListPageNum;

    public int getMusicListPageNum() {
        return musicListPageNum;
    }

    public void setMusicListPageNum(int musicListPageNum) {
        this.musicListPageNum = musicListPageNum;
    }

    private boolean theLastItem;

    public void setTheLastItem(boolean theLastItem) {
        this.theLastItem = theLastItem;
    }

    public boolean isTheLastItem() {
        return theLastItem;
    }

    private boolean theLastPage;

    public void setTheLastPage(boolean theLastPage) {
        this.theLastPage = theLastPage;
    }

    public boolean isTheLastPage() {
        return theLastPage;
    }

    public void setMusicHash(String musicHash) {
        this.musicHash = musicHash;
    }

    public void setMusicHash320(String musicHash320) {
        this.musicHash320 = musicHash320;
    }

    public void setMusicHashSq(String musicHashSq) {
        this.musicHashSq = musicHashSq;
    }

    public void setMusicHash320Price(int musicHash320Price) {
        this.musicHash320Price = musicHash320Price;
    }

    public void setMusicHashSqPrice(int musicHashSqPrice) {
        this.musicHashSqPrice = musicHashSqPrice;
    }




    public String getMusicHash() {
        return musicHash;
    }

    public String getMusicHash320() {
        return musicHash320;
    }

    public String getMusicHashSq() {
        return musicHashSq;
    }

    public int getMusicHash320Price() {
        return musicHash320Price;
    }

    public int getMusicHashSqPrice() {
        return musicHashSqPrice;
    }



    public String getMusicAlbumName() {
        return musicAlbumName;
    }

    public void setMusicAlbumName(String musicAlbumName) {
        this.musicAlbumName = musicAlbumName;
    }

    public String getMusicType() {
        return musicType;
    }

    public String getMusicTrate() {
        return musicTrate;
    }

    public String getMusicTimeLength() {
        return musicTimeLength;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public void setMusicTrate(String musicTrate) {
        this.musicTrate = musicTrate;
    }

    public void setMusicTimeLength(String musicTimeLength) {
        this.musicTimeLength = musicTimeLength;
    }

    protected void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
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

    public String getLrcPath() {
        return lrcPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }
}
