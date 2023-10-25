package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.os.Handler
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.min
import kotlin.math.max
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.Log


class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs), SensorEventListener {
    private var bitmap: Bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
    private val bitmapCanvas = Canvas(bitmap)
    val paint = Paint()

    var viewModel: SimpleView? = null

    // Sensor manager and gravity sensor
    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    private var ballX = 400f
    private var ballY = 400f
    private var ballRadius = 20f

    private var ballColor = Color.BLACK
    private var ballSize = 20f


    private var previousX = 0f
    private var previousY = 0f

    private val ballPaint = Paint()


    // Handler to handle idle state
    private val handler = Handler()
    private val idleRunnable = Runnable {
        // Code to run when view is idle
    }

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 10f

        // Set the paint for the ball
        ballPaint.color = Color.BLACK
        ballPaint.style = Paint.Style.FILL

        ballPaint.isAntiAlias = true

        gravitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Update paint color and size
        ballPaint.color = ballColor

        // Draw the ball on the bitmapCanvas
        bitmapCanvas.drawCircle(ballX, ballY, ballSize, ballPaint)

        // draw the update bitmap
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
            // Updating the position of the ball
            ballX -= event.values[0] * 3
            ballY += event.values[1] * 3

            // Ensure the ball stays within the bounds of the view
            ballX = max(ballRadius, min(width - ballRadius, ballX))
            ballY = max(ballRadius, min(height - ballRadius, ballY))

            Log.d("Sensor", "Sensor values: x = ${event.values[0]}, y = ${event.values[1]}")
            Log.d("BallPosition", "Ball position: x = $ballX, y = $ballY")

            // Update the ViewModel's ball position
            viewModel?.updateBallPosition(ballX, ballY)

            // Draw the ball at its new position onto the bitmap
            bitmapCanvas.drawCircle(ballX, ballY, ballRadius, ballPaint)

            // Make sure the ball is redrawn
            invalidate()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
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

    fun setBallSize(size: Float) {
        ballSize = size
        ballRadius = size
        invalidate()
    }

    fun setBallPosition(x: Float, y: Float) {
        ballX = x
        ballY = y
        invalidate()
    }

    fun setBallColor(color: Int) {
        ballColor = color
        ballPaint.color = color
        invalidate()
    }

    fun setBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
        invalidate()
    }

    fun getCurrentBitmap(): Bitmap {
        return bitmap
    }

    fun clearBitmap() {
        bitmap.eraseColor(Color.TRANSPARENT)
        invalidate()
    }
}