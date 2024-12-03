package com.goldsprite.gamedevframework;

import android.view.*;

public class GestureHandler
{
    private float lastCenterX = 0;
    private float lastCenterY = 0;
    private float lastDistance = 0;

    private final GestureListener listener;

    public interface GestureListener
	{
        void onDoublePointerMove(float dx, float dy);
        void onScale(float scale);
    }

    public GestureHandler(GestureListener listener) {
        this.listener = listener;
    }

    public boolean handleTouchEvent(MotionEvent event, int viewWidth, int viewHeight) {
        int pointerCount = event.getPointerCount();
        switch (pointerCount) {
            case 2: // 双指拖动与缩放
                return handleDoublePointer(event, viewWidth, viewHeight);
        }
		return false;
    }



	private boolean handleDoublePointer(MotionEvent ev, int viewWidth, int viewHeight) {
		if (ev.getPointerCount() < 2) return false;

		// 计算双指中心点
		float centerX = (ev.getX(0) + ev.getX(1)) / 2;
		float centerY = (ev.getY(0) + ev.getY(1)) / 2;
		// 计算双指距离
		float distance = calculateDistance(ev);

		if (ev.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
			lastCenterX = centerX;lastCenterY = centerY;
			lastDistance = distance;
		}
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {

			// 计算平移量
			if (lastCenterX != 0 && lastCenterY != 0) {
				float dx = centerX - lastCenterX;
				float dy = centerY - lastCenterY;
				float vel = Math.min(viewWidth, viewHeight) / 1f;
				vel = 1;
				listener.onDoublePointerMove(dx / vel, -dy / vel);
			}

			// 计算双指距离
			if (lastDistance != 0) {
				float scale = distance / lastDistance;
				listener.onScale(scale); // 缩放围绕视口中心
			}

			// 更新状态
			lastCenterX = centerX;
			lastCenterY = centerY;
			lastDistance = distance;
		}
		
		return true;
	}

    private float calculateDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}

