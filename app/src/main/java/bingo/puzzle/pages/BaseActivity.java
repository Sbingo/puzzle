package bingo.puzzle.pages;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bingo.puzzle.R;

/**
 * Created by sjb on 2016/4/30.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout rootLayout;
    private ImageView iv_left,iv_right;
    private ImageButton btn_back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            btn_back = (ImageButton) findViewById(R.id.btn_back);
            iv_left = (ImageView) findViewById(R.id.iv_left);
            iv_right = (ImageView) findViewById(R.id.iv_right);
            title = (TextView) findViewById(R.id.tv_title);

            btn_back.setOnClickListener(this);
            iv_left.setOnClickListener(this);
            iv_right.setOnClickListener(this);

        }
    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (rootLayout == null) return;
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
    }

    public void hideBack() {
        btn_back.setVisibility(View.GONE);
    }

    public void setTitle(String string) {
        if (title != null) {
            title.setText(string);
        }
    }

    public void setRightImage(Context context, int drawable) {
        iv_right.setVisibility(View.VISIBLE);
        Picasso.with(context).load(drawable).into(iv_right);
    }

    public void hideRightImage() {
        iv_right.setVisibility(View.GONE);
    }

    public void setLeftImage(Context context, int drawable) {
        iv_left.setVisibility(View.VISIBLE);
        Picasso.with(context).load(drawable).into(iv_left);
    }

    public void hideLeftImage() {
        iv_left.setVisibility(View.GONE);
    }

    public void setLeftListener(View.OnClickListener listener) {
        iv_left.setOnClickListener(listener);
    }

    public void setFinishListener(View.OnClickListener listener) {
        btn_back.setOnClickListener(listener);
    }

    public void setRightListener(View.OnClickListener listener) {
        iv_right.setOnClickListener(listener);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 简化findViewById
     *
     * @param viewId
     * @param <T>
     * @return
     */
    protected <T extends View> T $(int viewId) {
        try {
            return (T) findViewById(viewId);
        } catch (ClassCastException e) {
            Log.e("", "cant cast the view to concrete class");
            throw e;
        }
    }
}
