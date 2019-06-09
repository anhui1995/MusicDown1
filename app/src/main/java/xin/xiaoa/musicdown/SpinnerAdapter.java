package xin.xiaoa.musicdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter  extends BaseAdapter {

    private Context context;
    private List<SpinnerItem> lists;

    public SpinnerAdapter(Context context, List<SpinnerItem> lists) {
        super();
        this.context = context;
        this.lists = lists;
    }


    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return lists.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        HolderView holderView = null;
        SpinnerItem entity = lists.get(arg0);
        if (holderView == null) {
            holderView = new HolderView();
            arg1 = View.inflate(context, R.layout.spinner_item, null);
            holderView.tvId = arg1.findViewById(R.id.spinner_item_textview);

            holderView.tvId.setText(entity.getKey());

            arg1.setTag(holderView);
        } else {
            holderView = (HolderView) arg1.getTag();
        }
        return arg1;
    }

    @Override
    public int getCount() {
        // TODO: Implement this method
        return lists.size();
    }
    class HolderView {
        TextView tvId;
    }
}


