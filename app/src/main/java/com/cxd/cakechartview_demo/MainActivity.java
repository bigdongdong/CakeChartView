package com.cxd.cakechartview_demo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cxd.cakechartview.CakeAdapter;
import com.cxd.cakechartview.CakeChartView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CakeChartView ccv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ccv = findViewById(R.id.ccv);

        final int margin = dip2px(10);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ccv.getLayoutParams();
        lp.width = getScreenWidth() - margin*2;
        lp.height = getScreenWidth() - margin*2;
        lp.setMargins(margin,margin,margin,margin);

        cake();
    }


    private void cake(){
        final String[] texts = new String[]{"语文","数学","英语","体育","美术","思修","选修"};
        final float[] ratios = new float[]{0.1f,0.2f,0.1f,0.1f,0.1f,0.25f,0.15f};
        final int[] colors = new int[]{
                0xffffffd2,
                0xffc6fce5,
                0xffaa96da,
                0xfffcbad3,
                0xffdbe2ef,
                0xffe0f9b5,
                0xffff7e67,
                0xffa56cc1
        };
        List<Bean> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Bean bean = new Bean();
            bean.setColor(colors[i]);
            bean.setText(texts[i]);
            bean.setRatio(ratios[i]);
            list.add(bean);
        }

        ccv.setAdapter(new CakeAdapter<Bean>(list) {
            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public BaseConfig base() {
                return new BaseConfig(
                        Color.parseColor("#11111111"),
                        10,
                        Color.BLACK,
                        0.5f,
                        0.65f
                );
            }

            @Override
            public ItemConfig item(Bean bean,int position) {
                return new ItemConfig(
                        bean.getColor(),
                        bean.getText(),
                        bean.getRatio(),
                        position == 5 || position == 1
                );
            }
        });
    }

    class Bean {
        int color;
        String text;
        float ratio;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public float getRatio() {
            return ratio;
        }

        public void setRatio(float ratio) {
            this.ratio = ratio;
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getScreenWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
