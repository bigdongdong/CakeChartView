# CakeChartView
饼图

# 截图预览（Screen Recrod）
<img  width = "350" src = "https://github.com/bigdongdong/CakeChartView/blob/master/preview/1.jpg"></img>
<img  width = "350" src = "https://github.com/bigdongdong/CakeChartView/blob/master/preview/2.jpg"></img></br>
<img  width = "350" src = "https://github.com/bigdongdong/CakeChartView/blob/master/preview/3.png"></img>
<img  width = "350" src = "https://github.com/bigdongdong/CakeChartView/blob/master/preview/4.png"></img></br>
<!--
<img  width = "350" src = "https://github.com/bigdongdong/ChatView/blob/master/preview/chat_screen.jpg"></img>
<img  width = "350" src = "https://github.com/bigdongdong/ChatView/blob/master/preview/chat_screen2.jpg"></img>
-->

# 项目配置

```
  allprojects {
      repositories {
          ...
          maven { url 'https://jitpack.io' }  //添加jitpack仓库
      }
  }
  
  dependencies {
	  implementation 'com.github.bigdongdong:CakeChartView:1.0' //添加依赖
  }
```

# 使用说明
## xml
```xml
<com.cxd.cakechartview.CakeChartView
        android:id="@+id/ccv"
        android:padding="30dp"         //padding是饼图选中时偏移的距离
        android:layout_width="300dp"
        android:layout_height="300dp"/>
```

## java

```java
List<T> list = new ArrayList<T>();
.........  //填充list
ccv.setAdapter(new CakeAdapter<T>(list) {
            /**
             * 设置是否为静态
             */
            @Override
            public boolean isStatic() {
	    	/**
		* 返回true-则从{@link CakeAdapter#item(Object, int)}中获取选中状态
             	* 返回false-则从手指点击选中获取选中状态
		*/
                return true;
            }

             /**
             * 设置cake的基础样式
             * @return
             */
            @Override
            public BaseConfig base() {
                return new BaseConfig(
                        Color.parseColor("#11111111"),    //底色，影子颜色
                        10,                               //文字大小，单位：sp
                        Color.BLACK,                      //文字颜色
                        0.5f,                             //中心镂空半径与整个cake的半径比
                        0.65f                             //文字所在位置半径与整个cake的半径比
                );
            }

            /**
             * 设置cake中每一块的数据
             * @param T 泛型
             * @param position 坐标
             * @return
             */
            @Override
            public ItemConfig item(T t,int position) {
                return new ItemConfig(
                        t.getColor(),           //从泛型元素中获取单个item的颜色
                        t.getText(),            //从泛型元素中获取单个item的文字
                        t.getRatio(),           //从泛型元素中获取单个item所占整个cake的比例
                        position == 5           //设置当前item的选中状态，【如果isStatic()返回false，则这里设置失效，
											可以选择三个参数的构造函数】
                );
            }
        });
   
```

