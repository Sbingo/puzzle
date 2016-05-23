package bingo.puzzle.pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bingo.puzzle.Bean.Constant;
import bingo.puzzle.Bean.PicItem;
import bingo.puzzle.R;
import bingo.puzzle.utils.PreferencesUtils;
import bingo.puzzle.utils.ToastUtils;

/**
 * Created by sjb on 2016/4/30 0030.
 */
public class PuzzleActivity extends BaseActivity implements View.OnClickListener {

    private GridView mGvPics;
    private TextView mTime, mStep;
    private Bitmap selectedPic;
    private PicItem picItem;
    /**
     * 分割后的小图片列表
     */
    private List<PicItem> mItemBeans = new ArrayList<>();
    private PicItem mBlankPicItem;
    private PicAdapter picAdapter;
    private ImageView successAnim;
    private Button createAgain, showOriginalPic, startGame;
    private boolean isStarted = false;
    /**
     * 最后一张拼图
     */
    private Bitmap mLastBitmap;
    /**
     * N 阶，默认3 X 3
     */
    private int type;
    /**
     * 时长
     */
    private int timeCount;
    /**
     * 步长
     */
    private int stepCount;
    private static final int TIME = 0;
    private static final int STEP = 1;

    private Timer mTimer;
    private UpdateHandler updateHandler;

    class UpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME:
                    timeCount++;
                    mTime.setText(timeCount + "s");
                    break;
                case STEP:
                    stepCount++;
                    mStep.setText(stepCount + "");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        getIntentData();
        initView();
        initData();

    }

    private void getIntentData() {
        int id = getIntent().getIntExtra("picId", R.drawable.pic1);
        type = getIntent().getIntExtra("Type", 3);
        selectedPic = BitmapFactory.decodeResource(getResources(), id);
    }

    private void initView() {
        hideBack();
        setFinishListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PuzzleActivity.this.finish();
            }
        });
        mGvPics = $(R.id.gv_pics);
        mTime = $(R.id.tv_time);
        mStep = $(R.id.tv_step);
        createAgain = $(R.id.btn_create_again);
        successAnim = $(R.id.iv_success_anim);
        startGame = $(R.id.btn_start);
        updateHandler = new UpdateHandler();
        if (type == 2) {
            mGvPics.setNumColumns(2);
        }

        createAgain.setOnClickListener(PuzzleActivity.this);
        startGame.setOnClickListener(PuzzleActivity.this);
    }

    private void initData() {
        Bitmap resizedBitmap = resizeBitmap(720, 720, selectedPic);
        createBitmaps(resizedBitmap);
        getPuzzle();
        picAdapter = new PicAdapter(mItemBeans);
        mGvPics.setAdapter(picAdapter);
        mGvPics.setOnItemClickListener(new PicOnItemClickListener());
    }

    /**
     * 分割小图块
     *
     * @param pic 图片
     */
    private void createBitmaps(Bitmap pic) {
        Bitmap bitmap = null;
        int itemWidth = pic.getWidth() / type;
        int itemHeight = pic.getHeight() / type;
        for (int i = 1; i <= type; i++) {
            for (int j = 1; j <= type; j++) {
                bitmap = Bitmap.createBitmap(pic, (j - 1) * itemWidth, (i - 1) * itemHeight, itemWidth, itemHeight);
                picItem = new PicItem((i - 1) * type + j, (i - 1) * type + j, bitmap);
                mItemBeans.add(picItem);
            }
        }
        mLastBitmap = mItemBeans.get(type * type - 1).getmBitmap();
        mItemBeans.remove(type * type - 1);
        Bitmap blankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blank);
        blankBitmap = Bitmap.createBitmap(blankBitmap, 0, 0, itemWidth, itemHeight);
        mItemBeans.add(new PicItem(type * type, 0, blankBitmap));
        mBlankPicItem = mItemBeans.get(type * type - 1);
    }

    /**
     * 调整图片大小
     *
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param bitmap    原图
     * @return
     */
    private Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(newWidth / bitmap.getWidth(), newHeight / bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return newBitmap;
    }

    /**
     * 生成随机的item
     */
    private void getPuzzle() {
        int index = 0;
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() * type * type);
            swapItems(mItemBeans.get(index), mBlankPicItem);
        }
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getmBitmapId());
        }
        if (canSolve(data)) {
            return;
        } else {
            getPuzzle();
        }
    }

    /**
     * 交换两个小图块
     *
     * @param from  待交换图
     * @param blank 空白图
     */
    private void swapItems(PicItem from, PicItem blank) {
        PicItem tempPicItem = new PicItem();

        tempPicItem.setmBitmapId(from.getmBitmapId());
        from.setmBitmapId(blank.getmBitmapId());
        blank.setmBitmapId(tempPicItem.getmBitmapId());

        tempPicItem.setmBitmap(from.getmBitmap());
        from.setmBitmap(blank.getmBitmap());
        blank.setmBitmap(tempPicItem.getmBitmap());

        mBlankPicItem = from;
    }


    /**
     * 判断该数据是否有解
     *
     * @param data 拼图数组数据
     * @return 是否有解
     */
    private boolean canSolve(List<Integer> data) {
        int blankId = mBlankPicItem.getmItemId();
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            //从下往上数，空格位于奇数行
            if (((blankId - 1) / type) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
                //从下往上数，空格位于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     */
    private int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    /**
     * 图片适配器
     */
    private class PicAdapter extends BaseAdapter {

        private List<PicItem> list;

        public PicAdapter(List<PicItem> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }


        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(PuzzleActivity.this).inflate(R.layout.gv_pics_item, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv_pic);
            if (type == 2) {
                ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                layoutParams.width = 360;
                layoutParams.height = 360;
                iv.setLayoutParams(layoutParams);
            }
            iv.setImageBitmap(mItemBeans.get(position).getmBitmap());
            convertView.setTag(iv);
            return convertView;
        }

    }

    private boolean isMoveable(int position) {
        int blankId = mBlankPicItem.getmItemId() - 1;
        //不同行相差TYPE时可移动
        if (Math.abs(blankId - position) == type) {
            return true;
        }
        //同行相差为1时可移动
        if ((blankId / type == position / type) && (Math.abs(blankId - position) == 1)) {
            return true;
        }
        return false;
    }

    private boolean isSuccess() {
        for (PicItem tempItem : mItemBeans) {
            if (tempItem.getmBitmapId() != 0 &&
                    (tempItem.getmItemId() == tempItem.getmBitmapId())) {
                continue;
            } else if (tempItem.getmBitmapId() == 0 &&
                    tempItem.getmItemId() == type * type) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private class PicOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
            if (!isStarted) {
                ToastUtils.displayCustomToast(PuzzleActivity.this, "请先开始游戏", Gravity.TOP);
                return;
            }
            if (isMoveable(position)) {
                swapItems(mItemBeans.get(position), mBlankPicItem);
                picAdapter.notifyDataSetChanged();
                //计步
                Message msg = new Message();
                msg.what = STEP;
                updateHandler.sendMessage(msg);
                if (isSuccess()) {
                    //补全图片
                    mItemBeans.get(mBlankPicItem.getmItemId() - 1).setmBitmap(mLastBitmap);
                    picAdapter.notifyDataSetChanged();
                    //成功次数加1
                    int i = PreferencesUtils.getInt(PuzzleActivity.this, Constant.COMPLETED_TIMES, 0);
                    PreferencesUtils.putInt(PuzzleActivity.this, Constant.COMPLETED_TIMES, ++i);
                    ToastUtils.displayCustomToast(PuzzleActivity.this, "拼图成功！", Gravity.TOP);
                    startGame.setEnabled(false);
                    setResult(RESULT_OK);
                    //计时停止
                    if (mTimer != null) {
                        mTimer.cancel();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PuzzleActivity.this.finish();
                        }
                    }, 2000);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_create_again:
                getPuzzle();
                picAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_start:
                startOrStopGame();
                break;
            default:
                break;
        }
    }

    private void startOrStopGame() {
        if (!isStarted) {
            startGame.setText("停止");
            createAgain.setEnabled(false);
            mTimer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = TIME;
                    updateHandler.sendMessage(msg);
                }
            };
            mTimer.schedule(timerTask, 0, 1000);
            isStarted = true;
        } else {
            startGame.setText("开始");
            createAgain.setEnabled(true);
            mTime.setText("0s");
            mStep.setText("0");
            timeCount = 0;
            stepCount = 0;
            mTimer.cancel();
            isStarted = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ToastUtils.toast != null) {
            ToastUtils.toast.cancel();
        }
    }
}
