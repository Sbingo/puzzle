package bingo.puzzle.pages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bingo.puzzle.Bean.Constant;
import bingo.puzzle.R;
import bingo.puzzle.utils.PreferencesUtils;
import bingo.puzzle.utils.ToastUtils;

public class MainActivity extends BaseActivity {

    private GridView mGvPics;
    private TextView mCompletedTimes;
    private List<Bitmap> mPicList = new ArrayList<>();
    private int[] mPicId;
    private final int PUZZLE = 1;
    private long firstTime = 0;
    private CheckBox easyCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initData();

    }

    private void initView() {
        hideBack();
        mCompletedTimes = $(R.id.tv_completed_times);
        mGvPics = $(R.id.gv_pics);
        easyCheckBox = $(R.id.cb_easy);
    }

    private void initData() {
        updateCompletedTimes();
        mPicId = new int[]{R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5, R.drawable.pic6,
                R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10, R.drawable.pic11, R.drawable.pic12, R.drawable.pic13,
                R.drawable.pic14, R.drawable.pic15};
        Bitmap[] bitmaps = new Bitmap[mPicId.length];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), mPicId[i]);
            mPicList.add(bitmaps[i]);
        }
        mGvPics.setAdapter(new PicAdapter(mPicId));
        mGvPics.setOnItemClickListener(new PicOnItemClickListener());
    }

    /**
     * 图片适配器
     */
    private class PicAdapter extends BaseAdapter {

        private int[] list;

        public PicAdapter(int[] list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.length;
        }


        @Override
        public Object getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.gv_pics_item, null);
//                holder.iv = (ImageView) convertView.findViewById(R.id.iv_pic);
//                holder.iv.setImageResource(list[position]);
            ImageView v = (ImageView) convertView.findViewById(R.id.iv_pic);
            v.setImageResource(list[position]);
            convertView.setTag(v);
//                Picasso.with(MainActivity.this).load(list[position]).into(holder.iv);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
            return convertView;
        }

//        private class ViewHolder {
//            private ImageView iv;
//        }
    }

    private class PicOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
            intent.putExtra("picId", mPicId[position]);
            if (easyCheckBox.isChecked()) {
                intent.putExtra("Type", 2);
            } else {
                intent.putExtra("Type", 3);
            }
            startActivityForResult(intent, PUZZLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PUZZLE:
                updateCompletedTimes();
                break;
            default:
                break;
        }
    }

    /**
     * 更新已成功次数
     */
    private void updateCompletedTimes() {
        int times = PreferencesUtils.getInt(MainActivity.this, Constant.COMPLETED_TIMES, 0);
        if (mCompletedTimes != null) {
            mCompletedTimes.setText(String.format("已成功拼图%s次", times));
        }
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {     //如果两次按键时间间隔大于2秒，则不退出
            ToastUtils.show(this, "再按一次退出程序");
            firstTime = secondTime;//更新firstTime
        } else {
            super.onBackPressed();
        }
    }
}
