package com.example.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * SignatureView 类是一个自定义视图，用于捕获用户的签名输入。
 * 它提供了签名板的绘制功能，并允许用户通过触摸来绘制自己的签名。
 */
public class SignatureView extends View {
    private Paint paint = new Paint();  // 画笔，用于绘制路径
    private Path path = new Path();     // 路径，用于记录用户绘制的轨迹

    /**
     * 检查签名视图是否为空。
     * @return 如果没有签名返回 true，否则返回 false。
     */
    public boolean isEmpty() {
        return path.isEmpty();
    }

    /**
     * 构造函数，初始化视图的绘制属性。
     * @param context 上下文
     * @param attrs 属性集，从XML布局传递
     */
    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);  // 开启抗锯齿，使画出的线条更平滑
        paint.setColor(Color.BLACK);  // 设置画笔颜色为黑色
        paint.setStyle(Paint.Style.STROKE);  // 设置画笔样式为描边
        paint.setStrokeJoin(Paint.Join.ROUND);  // 设置描边的拐角为圆角
        paint.setStrokeWidth(5f);  // 设置描边宽度
    }

    /**
     * 测量视图的大小，确保它是正方形。
     * @param widthMeasureSpec 宽度测量规格
     * @param heightMeasureSpec 高度测量规格
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);  // 取宽和高的最小值，使视图成为正方形
        setMeasuredDimension(size, size);  // 设置测量的尺寸为正方形
    }

    /**
     * 绘制视图内容。
     * @param canvas 画布，用于在其中绘制内容
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);  // 在画布上绘制路径
    }

    /**
     * 处理触摸事件，用于绘制签名。
     * @param event 触摸事件对象
     * @return 返回是否处理了触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();  // 获取触摸点的X坐标
        float eventY = event.getY();  // 获取触摸点的Y坐标

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:  // 手指触碰屏幕
                path.moveTo(eventX, eventY);  // 将绘制起点移至触碰点
                return true;
            case MotionEvent.ACTION_MOVE:  // 手指在屏幕上移动
                path.lineTo(eventX, eventY);  // 绘制线条
                break;
            case MotionEvent.ACTION_UP:  // 手指离开屏幕
                break;
            default:
                return false;
        }

        invalidate();  // 通知视图重绘
        return true;
    }

    /**
     * 清除签名。
     */
    public void clear() {
        path.reset();  // 重置路径
        invalidate();  // 通知视图重绘
    }

    /**
     * 获取签名的位图。
     * @return 返回包含签名的位图
     */
    public Bitmap getSignatureBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);  // 创建位图
        Canvas canvas = new Canvas(bitmap);  // 创建一个画布用于绘制位图
        // 移除canvas.drawColor(Color.WHITE);让背景保持透明
        draw(canvas);  // 将视图内容绘制到画布上
        return bitmap;  // 返回绘制完成的位图
    }
}
