package com.example.phase1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView(context: Context, attrs: AttributeSet,) : View(context, attrs) {
    private var bitmap: Bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)
    private val paint = Paint()

    var viewModel: SimpleView? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val oldBitmap = bitmap
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmapCanvas.setBitmap(bitmap)

        // Copy the old content to the new bitmap
        val copyCanvas = Canvas(bitmap)
        copyCanvas.drawBitmap(oldBitmap, 0f, 0f, null) // draw at (0,0)

        viewModel?.updateBitmap(bitmap) // ensure that ViewModel is updated with new bitmap
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = Color.WHITE
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
    }


    fun drawCircles(circles: List<SimpleView.Circle>) {
        circles.forEach { circle ->
            paint.color = circle.color
            bitmapCanvas.drawCircle(circle.x, circle.y, 50f, paint)
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                viewModel?.addCircle(event.x, event.y)  // Update ViewModel
                return true
            }
            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    fun setBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
        invalidate()
    }

    fun getCurrentBitmap(): Bitmap {
        return bitmap
    }


}