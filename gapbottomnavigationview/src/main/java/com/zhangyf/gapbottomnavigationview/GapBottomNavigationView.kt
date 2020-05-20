package com.zhangyf.gapbottomnavigationview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class GapBottomNavigationView : BottomNavigationView {
	
	private var fabId = 0 //凹陷View的id
	private var centerRadius: Float = 0.toFloat() //中间凹陷的半径
	private var cornerRadius = 12f //拐角处的圆滑大小（越大越平滑）
	private var shadowLength = 6f //阴影大小
	
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
		context,
		attrs,
		defStyleAttr
	) {
		background = GradientDrawable().apply { setColor(Color.TRANSPARENT) }
		val ta = context.obtainStyledAttributes(attrs, R.styleable.GapBottomNavigationView)
		fabId = ta.getResourceId(R.styleable.GapBottomNavigationView_anchor_fab, 0)
		shadowLength = ta.getFloat(R.styleable.GapBottomNavigationView_shadow_length, 6.toFloat())
		cornerRadius = ta.getFloat(R.styleable.GapBottomNavigationView_corner_radius, 12.toFloat())
		ta.recycle()
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		//测量时才能拿到凹陷的View
		val fab = (parent as ViewGroup).findViewById<View>(fabId)
		centerRadius = fab.width / 2.toFloat() + cornerRadius
		invalidate()
	}
	
	
	@SuppressLint("DrawAllocation")
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		val paint = Paint()
		val path = Path()
		
		//左边的半圆
		val rectL = RectF(
			shadowLength,
			shadowLength,
			height.toFloat() + shadowLength,
			height.toFloat() - shadowLength
		)
		path.arcTo(rectL, 90.toFloat(), 180.toFloat(), false)
		
		path.lineTo(width / 2 - centerRadius - cornerRadius, shadowLength)
		
		//左边转角处
		path.quadTo(
			width / 2 - centerRadius,
			shadowLength,
			width / 2 - centerRadius,
			cornerRadius + shadowLength
		)
		
		//中间凹陷的半圆
		val rectCenter = RectF(
			width / 2 - centerRadius,
			cornerRadius + shadowLength - centerRadius,
			width / 2 + centerRadius,
			cornerRadius + centerRadius + shadowLength
		)
		
		path.arcTo(rectCenter, 180.toFloat(), (-180).toFloat(), false)
		
		
		//利用贝塞尔曲线画中间凹陷（非半圆）
/*		path.quadTo(
			width.toFloat() / 2,
			centerRadius.toFloat(),
			width / 2 + centerRadius - cornerRadius - cornerRadius / sqrt(2.toFloat()),
			cornerRadius / sqrt(2.toFloat())
		)*/
		
		//右边转角处
		path.quadTo(
			width / 2 + centerRadius,
			shadowLength,
			width / 2 + centerRadius + cornerRadius,
			shadowLength
		)
		path.lineTo((width - shadowLength - height / 2), shadowLength)
		
		
		//右边的半圆
		val rectR = RectF(
			width.toFloat() - shadowLength - height,
			shadowLength,
			width.toFloat() - shadowLength,
			height.toFloat() - shadowLength
		)
		path.arcTo(rectR, 270.toFloat(), 180.toFloat(), false)
		
		//最后的直线
		path.moveTo((width - shadowLength - height / 2), height.toFloat() - shadowLength)
		path.lineTo(height / 2.toFloat() + shadowLength, height.toFloat() - shadowLength)
		path.close()
		
		//关闭硬件加速才能有阴影效果
		setLayerType(LAYER_TYPE_SOFTWARE, paint)
		
		//按背景色填充背景
		paint.apply {
			style = Paint.Style.FILL
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				color = backgroundTintList?.defaultColor ?: Color.BLACK
			}
			maskFilter = null
			isAntiAlias = true
			//添加阴影
			setShadowLayer(shadowLength, 0.toFloat(), 0.toFloat(), Color.LTGRAY)
		}
		canvas.drawPath(path, paint)
	}
	
}
