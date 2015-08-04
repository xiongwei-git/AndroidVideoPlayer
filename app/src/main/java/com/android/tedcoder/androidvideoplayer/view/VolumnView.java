package com.android.tedcoder.androidvideoplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.android.tedcoder.androidvideoplayer.R;

/**
 * 仿小米声音调节环形进度条
 */
public class VolumnView extends View {

	// 半径
	float r1 = 0;
	float r2 = 0;
	float r3 = 0;
	// 外圆宽度
	float w1 = 15;
	// 内圆宽度
	float w2 = 30;
	Paint paint;

	// 进度
	float progress = 0;

	Bitmap bitmap;
	
	RectF oval;

	public VolumnView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VolumnView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VolumnView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context) {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.STROKE);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ling);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float cx = getMeasuredWidth() / 2;
		float cy = getMeasuredHeight() / 2;
		r1 = cx - w1 / 2;
		r2 = cx - w1 / 2 - w2 / 2;
		r3 = cx - w1 / 2 - w2;

		// 绘制外圆
		paint.setStrokeWidth(w1);
		paint.setColor(Color.parseColor("#454547"));
		canvas.drawCircle(cx, cy, r1, paint);

		// 绘制中间圆环
		paint.setColor(Color.parseColor("#747476"));
		paint.setStrokeWidth(w2);
		canvas.drawCircle(cx, cy, r2, paint);

		// 绘制内圆
		paint.setColor(Color.parseColor("#464648"));
		paint.setStyle(Style.FILL);
		canvas.drawCircle(cx, cy, r3, paint);

		// 绘制中间的图片
		canvas.drawBitmap(bitmap, cx - bitmap.getWidth() / 2,
				cx - bitmap.getHeight() / 2, paint);

		// 绘制文本
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(0);
		paint.setTextSize(40);
		float textWidth = paint.measureText("铃声"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间

		canvas.drawText("铃声", cx - textWidth / 2, cx + bitmap.getHeight() / 2
				+ 40, paint);

		// 绘制进度
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(w2);
		paint.setColor(Color.WHITE);
		if (oval == null) {
			oval = new RectF(cx - r2, cy - r2, cx + r2, cy + r2); // 用于定义的圆弧的形状和大小的界限
		}
		canvas.drawArc(oval, 270, 360 * progress / 100, false, paint);

		super.onDraw(canvas);
	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 *            范围(0-100)
	 */
	public void setProgress(float progress) {
		this.progress = progress;
		if (this.progress >= 100)
			this.progress = 100;
		if (this.progress <= 0)
			this.progress = 0;
		postInvalidate();
	}
}
