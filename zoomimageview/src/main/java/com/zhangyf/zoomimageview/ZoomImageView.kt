package com.zhangyf.zoomimageview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView

class ZoomImageView : AppCompatImageView, OnGlobalLayoutListener {
	private var mContext: Context

	//手势缩放监听器
	private var scaleGestureDetector: ScaleGestureDetector? = null

	//手势点击监听器
	private var gestureDetector: GestureDetector? = null

	//缩放工具
	private var mMatrix: Matrix? = null

	//首次加载,避免onGlobalLayout多次执行
	private var isFirstLoad = true

	//点击事件
	var onClickAction: (() -> Unit)? = null

	constructor(context: Context) : super(context) {
		mContext = context
		init()
	}

	constructor(
		context: Context,
		attrs: AttributeSet?
	) : super(context, attrs) {
		mContext = context
		init()
	}

	var doubleTouchX = 0f //双击事件点x坐标
	var doubleTouchY = 0f //双击事件点y坐标

	fun init() {
		mMatrix = Matrix()
		gestureDetector = GestureDetector(context, object :
			GestureDetector.SimpleOnGestureListener() {
			override fun onDoubleTap(e: MotionEvent?): Boolean {

				//缩小动画插值器
				val animatorLittle = ValueAnimator.ofFloat(scale, mScale)
				//放大动画插值器
				val animatorLarge = ValueAnimator.ofFloat(scale, scale * 2)

				animatorLittle.duration = 100
				animatorLarge.duration = 100
				animatorLittle.interpolator = LinearInterpolator()
				animatorLarge.interpolator = LinearInterpolator()
				animatorLittle.addUpdateListener {
					//以上次双击放大的位置缩小
					mMatrix?.setScale(
						it.animatedValue as Float,
						it.animatedValue as Float,
						doubleTouchX,
						doubleTouchY
					)
					borderAndCenterCheck()
					imageMatrix = mMatrix
				}
				animatorLarge.addUpdateListener {
					//以点击位置放大
					val touchedX = if (mScale > 1.0f) (e?.x ?: 0f) / scale else e?.x ?: 0f / scale
					val touchedY = if (mScale > 1.0f) (e?.y ?: 0f) / scale else e?.y ?: 0f / scale
					mMatrix?.setScale(
						it.animatedValue as Float,
						it.animatedValue as Float,
						touchedX,
						touchedY
					)
					doubleTouchX = touchedX
					doubleTouchY = touchedY
					borderAndCenterCheck()
					imageMatrix = mMatrix
				}
				if (scale > mScale) {
					animatorLittle.start()
				} else {
					animatorLarge.start()
				}
				return true
			}

			override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
				onClickAction?.invoke()
				return true
			}

		})
		scaleGestureDetector = ScaleGestureDetector(mContext, object : OnScaleGestureListener {
			/**
			 * 缩放进行中，返回值表示是否下次缩放需要重置，如果返回ture，那么scaleGestureDetector就会重置缩放事件，如果返回false，scaleGestureDetector会在之前的缩放上继续进行计算
			 */
			override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
				if (null == drawable || mMatrix == null) {
					return true
				}
				//缩放因子,这个是根据两个手指来计算缩放的倍数
				var factor = scaleGestureDetector.scaleFactor
				val scale = scale
				if (scale < mScale * MAX_SCALE && factor > 1.0f || scale > mScale && factor < 1.0f) {
					if (scale * factor < mScale) {
						factor = mScale / scale
					}
					if (scale * factor > mScale * MAX_SCALE) {
						factor =
							mScale * MAX_SCALE / scale
					}
					//以两手中间位置进行缩放
					mMatrix?.postScale(
						factor,
						factor,
						scaleGestureDetector.focusX,
						scaleGestureDetector.focusY
					)
					doubleTouchX = scaleGestureDetector.focusX
					doubleTouchY = scaleGestureDetector.focusY
					borderAndCenterCheck()
					imageMatrix = mMatrix
				}
				return true
			}

			/**
			 * 缩放开始，返回值表示是否受理后续的缩放事件
			 */
			override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
				//缩放开始，这里返回true表示要接收这个事件，必须为true，onScale才能执行
				return true
			}

			/**
			 * 缩放结束
			 */
			override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {
				val scale = scale
				if (scale < mScale) {
					val s = mScale / scale
					mMatrix?.postScale(s, s, width / 2.toFloat(), height / 2.toFloat())
					imageMatrix = mMatrix
				}
			}
		})
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		viewTreeObserver.addOnGlobalLayoutListener(this)
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		viewTreeObserver.removeOnGlobalLayoutListener(this)
	}

	//获取当前缩放值
	private val scale: Float
		get() {
			val values = FloatArray(9)
			mMatrix?.getValues(values)
			return values[Matrix.MSCALE_X]
		}

	var translateStartPointX = 0f
	var translateStartPointY = 0f
	var isDrag = true

	override fun onTouchEvent(event: MotionEvent): Boolean {
		//设置类型，使图片能支持Matrix
		scaleType = ScaleType.MATRIX
		if (gestureDetector?.onTouchEvent(event) == true) {
			return true
		}

		if (event.pointerCount > 1) {
			parent.requestDisallowInterceptTouchEvent(true)
		}

		if (event.pointerCount < 2 && event.action == MotionEvent.ACTION_DOWN && scale > 1) {
			//放大图片后的第一次点击屏幕 记录初始位置
			translateStartPointX = event.x
			translateStartPointY = event.y
		}

		if (event.action == MotionEvent.ACTION_DOWN) {
			isDrag = true
		}

		if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
			//如果是放大之后放大主手指，将拖动状态置为false
			isDrag = false
		}

		if (
			(event.pointerCount < 2
					&& event.action == MotionEvent.ACTION_MOVE
					&& scale - mScale > 0.2)
			|| event.action == MotionEvent.ACTION_CANCEL
		) {
			if (isDrag) { //是拖动状态
				//放大图片后的移动事件
				parent.requestDisallowInterceptTouchEvent(true)
				val deltaX = event.x - translateStartPointX
				val deltaY = event.y - translateStartPointY
				mMatrix?.postTranslate(deltaX * 2, deltaY * 2)
				translateStartPointX = event.x
				translateStartPointY = event.y
				borderAndCenterCheck()
				imageMatrix = mMatrix
				return true
			}
		}
		return scaleGestureDetector?.onTouchEvent(event) ?: false
	}

/*    override fun onGlobalLayout() {
        //mScale = scale
*//*        if (drawable != null) {
            if (isFirstLoad) {
                isFirstLoad = false
                //获取控件的宽度和高度
                val width = width
                val height = height
                //获取到ImageView对应图片的宽度和高度
                val drawable = drawable ?: return
                // 图片固有宽度
                val imgWidth = drawable.intrinsicWidth
                // 图片固有高度
                val imgHeight = drawable.intrinsicHeight
                //接下来对图片做初始的缩放处理，保证图片能看全
                mScale = if (imgWidth >= width && imgHeight >= height) { // 图片宽度和高度都大于控件(缩小)
                    (width * 1.0f / imgWidth).coerceAtMost(height * 1.0f / imgHeight)
                } else if (imgWidth > width && imgHeight < height) { // 图片宽度大于控件,高度小于控件(缩小)
                    width * 1.0f / imgWidth
                } else if (imgWidth < width && imgHeight > height) { // 图片宽度小于控件,高度大于控件(缩小)
                    height * 1.0f / imgHeight
                } else { // 图片宽度小于控件,高度小于控件(放大)
                    (width * 1.0f / imgWidth).coerceAtMost(height * 1.0f / imgHeight)
                }
                // 将图片移动到手机屏幕的中间位置
                val distanceX = width / 2 - imgWidth / 2.toFloat()
                val distanceY = height / 2 - imgHeight / 2.toFloat()
                mMatrix?.postTranslate(distanceX, distanceY)
                mMatrix?.setScale(
                    mScale,
                    mScale,
                    width / 2.toFloat(),
                    height / 2.toFloat()
                )
                imageMatrix = mMatrix
            }
        }*//*
    }*/

	/**
	 * 获得图片放大缩小以后的宽和高
	 */
	private val matrixRectF: RectF
		get() {
			val rectF = RectF()
			val drawable = drawable
			if (drawable != null) {
				rectF[0f, 0f, drawable.intrinsicWidth.toFloat()] =
					drawable.intrinsicHeight.toFloat()
				mMatrix?.mapRect(rectF)
			}
			return rectF
		}

	/**
	 * 图片在缩放时进行边界控制
	 */
	private fun borderAndCenterCheck() {
		val rect = matrixRectF
		var deltaX = 0f
		var deltaY = 0f
		val viewWidth = width
		val viewHeight = height
		// 缩放时进行边界检测，防止出现白边
		if (rect.width() >= viewWidth) {
			if (rect.left > 0) {
				deltaX = -rect.left
			}
			if (rect.right < viewWidth) {
				deltaX = viewWidth - rect.right
			}
		}
		if (rect.height() >= viewHeight) {
			if (rect.top > 0) {
				deltaY = -rect.top
			}
			if (rect.bottom < viewHeight) {
				deltaY = viewHeight - rect.bottom
			}
		}
		// 如果宽度或者高度小于控件的宽或者高；则让其居中
		if (rect.width() < viewWidth) {
			deltaX = viewWidth / 2f - rect.right + rect.width() / 2f
		}
		if (rect.height() < viewHeight) {
			deltaY = viewHeight / 2f - rect.bottom + rect.height() / 2f
		}

		mMatrix?.postTranslate(deltaX, deltaY)

	}

	companion object {

		//默认缩放倍数,初始化后会根据图片大小改变这个值
		private var mScale = 1f

		//最大缩放倍数
		private const val MAX_SCALE = 20
	}

	override fun onGlobalLayout() {

	}

}