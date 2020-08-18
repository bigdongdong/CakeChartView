package com.cxd.cakechartview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.text.DecimalFormat;

/**
 * create by bigdongdong on 2020/8/18
 * 饼图
 */
@SuppressLint("DrawAllocation")
public class CakeChartView extends View {
    private final DecimalFormat df2 = new DecimalFormat("###.0");//留两位小数
    private Context context ;
    private int w,h ;
    private Path path ;
    private Paint paint ;
    private RectF outerRect , innerRect ;
    private int offset; //离心偏移量 = padding
    private boolean isLayoutOver = false ;
    private float curSelectedAngle = -10 ;

    /*内部使用数据*/
    private Sector[] sectors ;
    /*基础设置*/
    private CakeAdapter.BaseConfig config ;
    private boolean isStatic = false;


    public CakeChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setWillNotDraw(false);
        this.setClickable(true);

        path = new Path();
        outerRect = new RectF();
        innerRect = new RectF();

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        offset = getPaddingLeft() ;


        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isLayoutOver = true ;
                CakeChartView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        w = getMeasuredWidth();
        h = getMeasuredHeight();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float x = event.getX();
                float y = event.getY();
                int dinstance = (int) Math.sqrt(Math.pow(x-w/2,2)+Math.pow(y-h/2,2));
                int dx = (int) Math.abs(x - w/2);
                int dy = (int) Math.abs(y - h/2);
                if(dinstance <= w/2){
                    double a = Math.atan2(dx,dy) * 180 / Math.PI;
                    if(x >= w/2){
                        if(y <= h/2){
                            //第一象限
                            a = a ;
                        }else{
                            //第二象限
                            a = 180 - a;
                        }
                    }else{
                        if(y >= h/2){
                            //第三象限
                            a += 180 ;
                        }else{
                            //第四象限
                            a = 360 - a ;
                        }
                    }

                    curSelectedAngle = (float) a;
                    invalidate();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(config == null){return;}

        //扇面经长度
        final int p = (int) ((w/2- offset)*(1-config.hollowRatio)) ;

        /*绘制底色*/
        if(config.shadowColor != Color.TRANSPARENT){
            Paint shadowPaint = new Paint();
            shadowPaint.setAntiAlias(true);
            shadowPaint.setColor(config.shadowColor);
            shadowPaint.setStrokeWidth(p);
            shadowPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(w/2,h/2,w/2-offset-p/2 ,shadowPaint);
        }

        /*填充扇面*/
        if(sectors != null){
            float angle = 0 ;
            float posiAngle = 0 ;
            for (int i = 0; i < sectors.length; i++) {
                Sector sector = sectors[i];

                outerRect.set(offset, offset,w- offset,h- offset);
                innerRect.set(offset +p, offset +p,w- offset -p,h- offset -p);

                /*是否选中*/
                boolean isSelected;
                if(!isStatic){
                    isSelected = (curSelectedAngle >= posiAngle && curSelectedAngle <= posiAngle + sector.angle) ;
                }else{
                    isSelected = sector.selected ;
                }

                if(isSelected){
                    outerRect.offset(sector.dx,sector.dy);
                    innerRect.offset(sector.dx,sector.dy);
                }
                //绘制扇面
                paint.setColor(sector.color);
                path.reset();
                path.arcTo(outerRect,(angle+270)%360,sector.angle);
                path.arcTo(innerRect,(angle+sector.angle+270)%360,-sector.angle);
                path.close();
                canvas.drawPath(path,paint);

                //绘制文字
                // eg.(10.0% \n 语文)
                paint.setColor(config.fontColor);
                paint.setTextSize(sp2px(context,config.fontSize));
                int wordx = sector.wordPoint.x ;
                int wordy = sector.wordPoint.y ;
                if(isSelected){
                    wordx += sector.dx ;
                    wordy += sector.dy ;
                }
                canvas.drawText(df2.format(sector.ratio* 100)+"%",wordx,wordy,paint);
                if(sector.word != null){
                    canvas.drawText(sector.word,wordx,wordy+sp2px(context,config.fontSize),paint);
                }

                //角度叠加
                angle = (angle + sector.angle)%360 ;
                posiAngle = (posiAngle + sector.angle)%360;
            }
        }

    }

    /**
     * 适配
     * @param adapter
     */
    public void setAdapter(final CakeAdapter adapter){
        if(!isLayoutOver){
            this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    adaptAndRefreshUI(adapter);
                    isLayoutOver = true ;
                    CakeChartView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }else{
            adaptAndRefreshUI(adapter);
        }
    }

    /**
     * 填充数据并更新UI
     * @param adapter
     */
    private void adaptAndRefreshUI(CakeAdapter adapter){
        if(adapter != null && adapter.list != null){
            final int size = adapter.list.size() ;
            sectors = new Sector[size];
            config = adapter.base();
            isStatic = adapter.isStatic();
            this.setClickable(!isStatic);

            /*数据加工*/
            float angle = 0 ;
            final int wordRadius = (int) (h/2 * config.wordRatio) ;
            final Point iWordStart = new Point(w/2, h /2 - wordRadius);
            for (int i = 0; i < size; i++) {
                CakeAdapter.ItemConfig itemConfig = adapter.item(adapter.list.get(i),i);
                Sector sector = new Sector();
                sectors[i] = sector ;
                sector.ratio = itemConfig.ratio;
                sector.angle = sector.ratio * 360 ;
                //扇面中心线累加角度
                float centerAngle = (angle + sector.angle/2)%360 ;
                sector.wordPoint = calcNewPoint(iWordStart,angle+sector.angle/2) ;
                sector.color = itemConfig.color;
                sector.word = itemConfig.text;
                sector.selected = itemConfig.selected ;

                //计算位移方向和距离
                float tempAngle = 0 ;
                int directionX = 1 ; //位移x的方向
                int directionY = 1 ; //位移y的方向
                if(centerAngle >0 && centerAngle < 90){
                    //第一象限
                    tempAngle = centerAngle ;
                    directionX = 1 ;
                    directionY = -1 ;
                }else if(centerAngle > 90 && centerAngle < 180){
                    //第二象限
                    tempAngle = 180 - centerAngle ;
                    directionX = 1 ;
                    directionY = 1 ;
                }else if(centerAngle > 180 && centerAngle < 270){
                    //第三象限
                    tempAngle = centerAngle - 180;
                    directionX = -1 ;
                    directionY = 1 ;
                }else{
                    //第四象限
                    tempAngle = 360 - centerAngle ;
                    directionX = -1 ;
                    directionY = -1 ;
                }

                sector.dx = (int) (Math.sin(tempAngle* Math.PI/180) * offset * directionX);
                sector.dy = (int) (Math.cos(tempAngle* Math.PI/180) * offset * directionY);

                //角度累加
                angle += sector.angle;
            }

            invalidate();
        }
    }

    /**
     * 某个点旋转一定角度后，得到一个新的点
     * @param start 起始点
     * @param angle 旋转角度
     * @return
     */
    private Point calcNewPoint(Point start ,float angle) {
        Point center = new Point(w/2,h/2) ;
        // calc arc
        float l = (float) ((angle * Math.PI) / 180);

        //sin/cos value
        float cosv = (float) Math.cos(l);
        float sinv = (float) Math.sin(l);

        // calc new point
        float newX = (start.x - center.x) * cosv - (start.y - center.y) * sinv + center.x;
        float newY = (start.x - center.x) * sinv + (start.y - center.y) * cosv + center.y;
        return new Point((int) newX, (int) newY);
    }

    /*内部扇面bean*/
    private class Sector{
        private float angle ; //扇面角度
        private Point wordPoint ; //文字点
        private int color ; //扇面颜色
        private String word ; //文字
        private float ratio ; //比率 0~1f
        private int dx ; //选中状态下rect的x轴偏移量
        private int dy ; //选中状态下rect的y轴偏移量
        private boolean selected ; //选中状态
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @return
     */
    public int sp2px(Context context ,float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
