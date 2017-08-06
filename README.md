# Sticker  [![](https://jitpack.io/v/miaoyongjun/Sticker.svg)](https://jitpack.io/#miaoyongjun/Sticker)

贴纸¸ Sticker 

> 感谢好友 **Vincent** 提供的思路以及关键代码！



### 效果

![](https://github.com/miaoyongjun/Media/blob/master/sticker.gif?raw=true)

### 依赖

```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        compile 'com.github.miaoyongjun:Sticker:1.1'
	}
```

### 使用

```java
<miaoyongjun.stickerview.StickerView
        android:id="@+id/stickerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:src="@drawable/bg"/>
```

### 自定义属性

```java
<declare-styleable name="StickerView">
        <!--初始图片大小缩放比例  默认为0.5f 相对于view的宽度 范围 0到1-->
        <attr name="m_image_init_scale" format="float"/>
        <!--贴纸的最大数量 默认为20个-->
        <attr name="m_max_count" format="integer"/>
        <!--贴纸最小值的缩放比例  默认初始图片斜边的二分之一 根据宽度来定制  范围 0到1-->
        <attr name="m_image_min_size_scale" format="float"/>
        <!--关闭按钮图标-->
        <attr name="m_close_icon" format="reference"/>
        <!--旋转按钮图标-->
        <attr name="m_rotate_icon" format="reference"/>
        <!--关闭按钮图标大小 默认20dp-->
        <attr name="m_close_icon_size" format="dimension"/>
        <!--旋转按钮图标大小 默认20dp-->
        <attr name="m_rotate_icon_size" format="dimension"/>
        <!--边框宽度 默认1dp-->
        <attr name="m_outline_width" format="dimension"/>
        <!--边框颜色 默认白色-->
        <attr name="m_outline_color" format="color"/>
    </declare-styleable>
```

### 后续计划

- 文字水印和标签

### 更改日志

#### v1.1  

- 增加双手触摸
- 解决图片过大ANR
- 添加点击排到最前的处理