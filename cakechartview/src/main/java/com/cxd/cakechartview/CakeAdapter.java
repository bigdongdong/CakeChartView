package com.cxd.cakechartview;

import java.util.List;

/**
 * create by bigdongdong on 2020/8/18
 * 饼图的适配器
 * {@link CakeChartView#setAdapter(CakeAdapter)}
 * @param <T>
 */
public abstract class CakeAdapter<T> {
    public List<T> list ;
    public CakeAdapter(List<T> list) {
        this.list = list;
    }

    /**
     * 是否静态
     * @return true-则从{@link CakeAdapter#item(Object, int)}中获取选中状态
     * @       false-则从手指点击选中获取选中状态
     */
    public abstract boolean isStatic();

    /**
     * 配置cake基础设置
     * @return
     */
    public abstract CakeAdapter.BaseConfig base();

    /**
     * 配置每一个Item的数据设置
     * @param t
     * @param position
     * @return
     */
    public abstract CakeAdapter.ItemConfig item(T t , int position);

    public class BaseConfig{
        int shadowColor ;
        int fontSize ;
        int fontColor ;
        float hollowRatio ;
        float wordRatio;

        public BaseConfig(int shadowColor, int fontSize, int fontColor, float hollowRatio, float wordRatio) {
            this.shadowColor = shadowColor;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.hollowRatio = hollowRatio;
            this.wordRatio = wordRatio;
        }
    }

    public class ItemConfig{
        int color ;
        String text ;
        float ratio ;
        boolean selected ;

        public ItemConfig(int color, String text, float ratio) {
            this.color = color;
            this.text = text;
            this.ratio = ratio;
            this.selected = false;
        }

        public ItemConfig(int color, String text, float ratio, boolean selected) {
            this.color = color;
            this.text = text;
            this.ratio = ratio;
            this.selected = selected;
        }
    }
}
