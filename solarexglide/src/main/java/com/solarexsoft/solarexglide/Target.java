package com.solarexsoft.solarexglide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 21:14/2020-01-27
 *    Desc:
 * </pre>
 */

public class Target {
    public interface SizeReadyCallback {
        void onSizeReady(int width, int height);
    }

    private static final class LayoutListener implements ViewTreeObserver.OnPreDrawListener{
        private Target target;

        public LayoutListener(Target target) {
            this.target = target;
        }

        @Override
        public boolean onPreDraw() {
            if (target != null) {
                target.checkCurrentDimens();
            }
            return true;
        }

        public void release() {
            target = null;
        }
    }

    private static int maxDisplayLength = -1;
    private SizeReadyCallback sizeReadyCallback;
    private LayoutListener layoutListener;
    private ImageView imageView;

    public Target(ImageView imageView) {
        this.imageView = imageView;
    }

    public void cancel() {
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnPreDrawListener(layoutListener);
        }
        if (layoutListener != null) {
            layoutListener.release();
        }
        layoutListener = null;
        sizeReadyCallback = null;
    }

    public void onLoadFailed(Drawable error) {
        imageView.setImageDrawable(error);
    }

    public void onLoadStarted(Drawable placeholder) {
        imageView.setImageDrawable(placeholder);
    }

    public void onResourceReady(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void getSize(SizeReadyCallback callback) {
        int currentWidth = getTargetWidth();
        int currentHeight = getTargetHeight();

        if (currentHeight > 0 && currentWidth > 0) {
            callback.onSizeReady(currentWidth, currentHeight);
            return;
        }
        this.sizeReadyCallback = callback;
        if (layoutListener == null) {
            ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
            layoutListener = new LayoutListener(this);
            viewTreeObserver.addOnPreDrawListener(layoutListener);
        }
    }

    private int getTargetHeight() {
        int verticalPadding = imageView.getPaddingTop() + imageView.getPaddingBottom();
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int layoutParamsSize = layoutParams != null ? layoutParams.height : 0;
        return getTargetDimension(imageView.getHeight(), layoutParamsSize, verticalPadding);
    }

    private int getTargetWidth() {
        int horizontalPadding = imageView.getPaddingLeft() + imageView.getPaddingRight();
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int layoutParamsSize = layoutParams != null ? layoutParams.width : 0;
        return getTargetDimension(imageView.getWidth(), layoutParamsSize, horizontalPadding);
    }

    private int getTargetDimension(int viewSize, int paramsSize, int paddingSize) {
        int adjustedParamSize = paramsSize - paddingSize;
        if (adjustedParamSize > 0) {
            return adjustedParamSize;
        }

        int adjustedViewSize = viewSize - paddingSize;
        if (adjustedViewSize > 0) {
            return adjustedViewSize;
        }

        if (!imageView.isLayoutRequested() && paramsSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return getMaxDisplayLength(imageView.getContext());
        }
        return 0;
    }

    private int getMaxDisplayLength(Context context) {
        if (maxDisplayLength == -1) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            maxDisplayLength = Math.max(point.x, point.y);
        }
        return maxDisplayLength;
    }

    private void checkCurrentDimens() {
        if (sizeReadyCallback == null) {
            return;
        }
        int currentWidth = getTargetWidth();
        int currentHeight = getTargetHeight();
        if (currentWidth <= 0 && currentHeight <= 0) {
            return;
        }
        sizeReadyCallback.onSizeReady(currentWidth, currentHeight);
        cancel();
    }


}
