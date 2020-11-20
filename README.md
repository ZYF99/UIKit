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


	

 
