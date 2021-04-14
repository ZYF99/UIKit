# UIKit
自定义控件库

在app的build.gradle中添加
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.ZYF99:UIKit:1.1'
	}
```

## 可控的ImageView（ZoomImageView）

- [简书](https://www.jianshu.com/p/328e0dcf4f39)

![1589948455277.gif](https://upload-images.jianshu.io/upload_images/17794320-83cc34ab96b2c7de.gif?imageMogr2/auto-orient/strip)

- 手势放大缩小
- 双击放大缩小
- 放大后手势移动
- 点击事件
- 与父控件的滑动冲突解决

### 说明

在布局文件中直接使用
```
    <com.zhangyf.zoomimageview.ZoomImageView
            android:id="@+id/zoom_image"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
```
>如果需要点击事件，这样写（kotlin）
```
zoomImageView.onClickAction = {
			//Todo 点击事件
		}
```


## 中间凹陷的BottomNavigationView（GapBottomNavigationView）

![e9347423eb2031228af77ad63d7b01d7.jpg](https://upload-images.jianshu.io/upload_images/17794320-faea132de3d4305f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 自定中间拐角平滑度
- 自定义凹陷值
- 自定义阴影深度

### 说明

在布局文件中直接使用
```
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab_add"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="30dp"
        android:backgroundTint="#ffffff"
        android:elevation="6dp"
        android:src="@android:drawable/ic_input_add"
        android:tint="#000000"
        app:borderWidth="0dp"
        app:fabSize="normal"
        app:layout_constraintBottom_toBottomOf="@+id/bottomnavigation"
        app:layout_constraintEnd_toEndOf="@+id/bottomnavigation"
        app:layout_constraintStart_toStartOf="@+id/bottomnavigation"
        app:rippleColor="#00FFFFFF"/>

    <com.zhangyf.gapbottomnavigationview.GapBottomNavigationView
        android:id="@+id/bottomnavigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginEnd="32dp"
        android:layout_marginBottom="16dp"
        android:backgroundTint="#ffffff"
        android:clickable="false"
        android:padding="8dp"
        app:center_radius="32dp"
        app:corner_radius="12dp"
        app:elevation="5dp"
        app:itemBackground="@null"
        app:itemIconTint="#000000"
        app:itemTextColor="#000000"
        app:labelVisibilityMode="labeled"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/navigation"
        app:shadow_length="4dp"
        tools:targetApi="lollipop" />
```
  - app:center_radius： 用来指定凹陷的半径
  - shadow_length：用来指定高度（阴影大小）
  - cornerRadius：用来指定拐角处的平滑半径大小
	
## 个人博客
[ZIKI(安卓学弟)](https://zyf99.github.io/Blog/)
	
## License

	Copyright 2020, ZEKI
	
	   Licensed under the Apache License, Version 2.0 (the "License");
	   you may not use this file except in compliance with the License.
	   You may obtain a copy of the License at
	
	       http://www.apache.org/licenses/LICENSE-2.0
	
	   Unless required by applicable law or agreed to in writing, software
	   distributed under the License is distributed on an "AS IS" BASIS,
	   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	   See the License for the specific language governing permissions and
	   limitations under the License.


	

 
