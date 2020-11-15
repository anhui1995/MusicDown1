package xin.xiaoa.musicdown.download;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import xin.xiaoa.musicdown.R;
//DialogListAdapter
public class DownloadMusicAdapter extends BaseAdapter {

    private Context context;
    private List<DownloadMusicList> lists;
    private HolderView holderView = null;
    public DownloadMusicAdapter(Context context, List<DownloadMusicList> lists) {
        super();
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    //public View getView(int position, View convertView, ViewGroup parent) {
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        holderView = null;
        DownloadMusicList entity = lists.get(arg0);
        if (holderView == null) {
            holderView = new HolderView();
            arg1 = View.inflate(context, R.layout.downloadmusicitem,null);
            holderView.name = (TextView) arg1.findViewById(R.id.downloadmusicitem_textView);


//            if(entity.getId()==0){
//                holderView.sellSuchItemName.setText("商品名");
//                holderView.sellSuchItemunit.setText("规格");
//                holderView.sellSuchItemper.setText("单价");
//
//            }

                holderView.name.setText(entity.getName());
            //arg1.setTag(holderView);
            arg1.setTag(arg0);
        } else {
            holderView = (HolderView) arg1.getTag();
        }

        return arg1;
    }


    class HolderView {
        TextView name;
    }
}
