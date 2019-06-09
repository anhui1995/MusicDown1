package xin.xiaoa.musicdown;

public class SpinnerItem {
    private String key;
    private String str;

    public SpinnerItem(String str, String name) {
        this.key = name;
        this.str = str;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String name) {
        this.key = name;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
