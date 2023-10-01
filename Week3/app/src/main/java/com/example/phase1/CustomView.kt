package com.example.phase1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.os.Handler



class CustomView(context: Context, attrs: AttributeSet,) : View(context, attrs) {
    private var bitmap: Bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)
    val paint = Paint()

    var viewModel: SimpleView? = null

    private var previousX = 0f
    private var previousY = 0f

    // Handler to handle idle state
    private val handler = Handler()
    private val idleRunnable = Runnable {
        // Code to run when view is idle
    }
    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 10f
    }


    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 10f

    }

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
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = x
                previousY = y
            }
            MotionEvent.ACTION_MOVE -> {
                bitmapCanvas.drawLine(previousX, previousY, x, y, paint)
                previousX = x
                previousY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Save the drawing state when the touch event ends
                viewModel?.updateBitmap(bitmap) // Update ViewModel with the final drawing
                handler.postDelayed(idleRunnable, 2000) // wait for 2 seconds to consider idle
            }
        }
        return true
    }

    fun setBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
        invalidate()
    }
    fun getCurrentBitmap(): Bitmap {
        return bitmap
    }

}