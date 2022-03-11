package jp.ac.uhyogo.mapDisplay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.*
import android.util.Log
import android.view.*
import jp.ac.uhyogo.mapDisplay.MainActivity.Companion.mapPath
import kotlin.math.max
import kotlin.math.min


class MapView(context: Context?) : View(context){
    // 表示画面サイズの取得
    private val displaySize = Point().also {
        (context!!.getSystemService(WINDOW_SERVICE) as WindowManager)
            .defaultDisplay
            .apply { getSize(it) }
    }

    // 表示mapの取得
    private var bmp: Bitmap = BitmapFactory.decodeFile(mapPath)
    private val paint = Paint()

    // map上の表示領域の中心座標
    private var centerX = bmp.width  / 2f
    private var centerY = bmp.height / 2f
    // 画像のスケール
    private var mScaleFactor = 1f
    // 画面と地図の比率
    private val xRatio = displaySize.x / bmp.width.toFloat()
    private val yRatio = displaySize.y / bmp.height.toFloat()
    // 最大・最小スケール
    private val maxScale = max(2.5f, max(xRatio, yRatio))
    private val minScale = min(xRatio, yRatio)

    // 拡大・縮小のイベントリスナー
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = max(minScale, min(mScaleFactor, maxScale))

            invalidate()
            return true
        }
    }

    // スクロールのイベントリスナー
    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            centerX += distanceX / mScaleFactor / 1.2f      // 画面移動が速くなりすぎないように/1.2fで調整
            centerY += distanceY / mScaleFactor / 1.2f

            invalidate()
            return true
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
    private val mGestureDetector = GestureDetector(context, gestureListener)

    /*
    * タッチイベント
    * */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(ev)
        mGestureDetector.onTouchEvent(ev)
        return true
    }

    /*
    * 描画（invalidateで実行出来る）
    * */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        setMapLimit()
        val displayLeft = -centerX + (displaySize.x / 2f / mScaleFactor)
        val displayTop  = -centerY + (displaySize.y / 2f / mScaleFactor)

        canvas?.scale(mScaleFactor, mScaleFactor)
        canvas?.drawBitmap(bmp, displayLeft, displayTop, paint)
    }

    /*
    * mapの領域外にならないように制限
    * */
    private fun setMapLimit(){
        val leftLim   = displaySize.x / 2f / mScaleFactor
        val topLim    = displaySize.y / 2f / mScaleFactor
        val rightLim  = bmp.width  - displaySize.x / 2f / mScaleFactor
        val bottomLim = bmp.height - displaySize.y / 2f / mScaleFactor

        centerX =
            if (mScaleFactor < xRatio) bmp.width / 2f
            else max(leftLim, min(centerX, rightLim))
        centerY =
            if (mScaleFactor < yRatio) bmp.height / 2f
            else max(topLim,  min(centerY, bottomLim))
    }
}
