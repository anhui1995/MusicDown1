package xin.xiaoa.musicdown;

    public class MusicListItem {

    private String musicID;
    private String musicName;
    private String musicAuthor;
    private boolean isLastItem;

    public boolean isLastItem() {
        return isLastItem;
    }

    public void setLastItem(boolean lastItem) {
        isLastItem = lastItem;
    }

    public String getMusicID() {
        return musicID;
    }

    public void setMusicID(String musicID) {
        this.musicID = musicID;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public MusicListItem(String musicName, String musicAuthor, String musicID) {
        this.musicID = musicID;
        this.musicName = musicName;
        this.musicAuthor = musicAuthor;
        this.isLastItem = false;
    }

    public MusicListItem(boolean isLastItem) {
        this.isLastItem = isLastItem;
    }

}
