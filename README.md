# UIKit
自定义控件库

## 可控的ImageView（ZoomImageView）

- 手势放大缩小
- 双击放大缩小
- 放大后手势移动
- 点击事件
- 与父控件的滑动冲突解决

## 中间凹陷的BottomNavigationView（GapBottomNavigationView）

- 可自定中间拐角平滑度
- 手动添加中间按钮 设置anchor后自动计算凹陷值
- 支持更改阴影深度

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.ZYF99:UIKit:1.4'
	}
