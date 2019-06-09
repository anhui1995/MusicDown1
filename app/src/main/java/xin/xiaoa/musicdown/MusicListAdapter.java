package xin.xiaoa.musicdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {

    private Context context;
    private List<MusicListItem> lists;

    private myItemListener mListener;
//    private myTextListener mTextLis;
    private String mp3;
    private HolderView holderView = null;

    float textSize = 0;

//    private TextPaint textPaint = new TextPaint();


    public MusicListAdapter(Context context, List<MusicListItem> lists, myItemListener listener) {
        super();
        this.context = context;
        this.lists = lists;
        mListener = listener;
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


    @SuppressLint("SetTextI18n")
    @Override
   // public View getView(int position, View convertView, ViewGroup parent) {
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        holderView = null;
        MusicListItem entity = lists.get(arg0);
        if (holderView == null) {
            holderView = new HolderView();

            holderView.id = arg0;

            if(entity.isLastItem()){
                arg1 = View.inflate(context, R.layout.musiclistlastitem, null);
                holderView.itemMusicNextPage = arg1.findViewById(R.id.itemMusicNextPage);
                holderView.itemMusicNextPage.setTag(arg0);
                holderView.itemMusicNextPage.setOnClickListener(mListener);
            }
            else {
                arg1 = View.inflate(context, R.layout.musiclistitem, null);
                holderView.itemMusicAuthor = arg1.findViewById(R.id.itemMusicAuthor);
                holderView.itemMusicName = arg1.findViewById(R.id.itemMusicName);
                holderView.itemMusicDown = arg1.findViewById(R.id.itemMusicDown);
                holderView.itemMusicDown.setTag(arg0);


                holderView.itemMusicAuthor.setText(entity.getMusicAuthor());
                holderView.itemMusicName.setText(entity.getMusicName());
                holderView.itemMusicDown.setOnClickListener(mListener);
            }

//            textSize = textPaint.measureText(entity.getEnglish());
//mListener
//            if (textSize > 59)
//                holderView.wordItemTextViewEnglish.setTextSize((int) (900 / textSize));
//            holderView.wordItemButtonMore.setOnClickListener(mListener);

//            holderView.wordItemButtonMore.setTag(arg0);
//            holderView.wordItemTextViewEnglish.setTag(arg0);
            arg1.setTag(arg0);
        } else {
            holderView = (HolderView) arg1.getTag();
        }
        return arg1;
    }

    class HolderView {
        TextView itemMusicName;
        TextView itemMusicAuthor;
        Button itemMusicDown;
        Button itemMusicNextPage;
        int id;
    }
    public static abstract class myItemListener implements View.OnClickListener {

        @Override
        public void onClick(View view)
        {
            myOnClick((int) view.getTag(), view);
        }
        public abstract void myOnClick(int position, View v);

    }

//    public static abstract class myTextListener implements View.OnTouchListener {
//
//        @Override
//        public boolean onTouch(View p1, MotionEvent p2) {
//            return myOnTouch(p1, p2, (Integer) p1.getTag());
//        }
//        public abstract boolean myOnTouch(View v, MotionEvent me, int a);
//    }
}
