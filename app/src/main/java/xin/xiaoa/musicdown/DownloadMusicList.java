package xin.xiaoa.musicdown;

public class DownloadMusicList {
    private String hash;
    private String name;
    public DownloadMusicList(String hash,String name){
        this.hash = hash;
        this.name = name;
    }
    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setName(String name) {
        this.name = name;
    }


}
