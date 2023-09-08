package com.example.phase1;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016J\u0006\u0010\u0018\u001a\u00020\bJ\u0012\u0010\u0019\u001a\u00020\u00142\b\u0010\u001a\u001a\u0004\u0018\u00010\nH\u0014J(\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020\u001dH\u0014J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0016J\u000e\u0010%\u001a\u00020\u00142\u0006\u0010&\u001a\u00020\bR\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012\u00a8\u0006\'"}, d2 = {"Lcom/example/phase1/CustomView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "bitmap", "Landroid/graphics/Bitmap;", "bitmapCanvas", "Landroid/graphics/Canvas;", "paint", "Landroid/graphics/Paint;", "viewModel", "Lcom/example/phase1/SimpleView;", "getViewModel", "()Lcom/example/phase1/SimpleView;", "setViewModel", "(Lcom/example/phase1/SimpleView;)V", "drawCircles", "", "circles", "", "Lcom/example/phase1/SimpleView$Circle;", "getCurrentBitmap", "onDraw", "canvas", "onSizeChanged", "w", "", "h", "oldw", "oldh", "onTouchEvent", "", "event", "Landroid/view/MotionEvent;", "setBitmap", "newBitmap", "app_debug"})
public final class CustomView extends android.view.View {
    @org.jetbrains.annotations.NotNull
    private android.graphics.Bitmap bitmap;
    @org.jetbrains.annotations.NotNull
    private final android.graphics.Canvas bitmapCanvas = null;
    @org.jetbrains.annotations.NotNull
    private final android.graphics.Paint paint = null;
    @org.jetbrains.annotations.Nullable
    private com.example.phase1.SimpleView viewModel;
    
    public CustomView(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.example.phase1.SimpleView getViewModel() {
        return null;
    }
    
    public final void setViewModel(@org.jetbrains.annotations.Nullable
    com.example.phase1.SimpleView p0) {
    }
    
    @java.lang.Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }
    
    @java.lang.Override
    protected void onDraw(@org.jetbrains.annotations.Nullable
    android.graphics.Canvas canvas) {
    }
    
    public final void drawCircles(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.phase1.SimpleView.Circle> circles) {
    }
    
    @java.lang.Override
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull
    android.view.MotionEvent event) {
        return false;
    }
    
    public final void setBitmap(@org.jetbrains.annotations.NotNull
    android.graphics.Bitmap newBitmap) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.graphics.Bitmap getCurrentBitmap() {
        return null;
    }
}