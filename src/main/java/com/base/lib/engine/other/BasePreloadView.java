package com.base.lib.engine.other;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.base.lib.engine.BaseActivity;
import com.base.lib.engine.Screen;

/**
 *
 */
public class BasePreloadView extends View {

    private int dimDelay = 250;
    private long hintTime;
    private RelativeLayout layout;
    private LoaderAction loaderAction;
    private Paint paintView;
    private float x, y, w, h;
    private boolean rising;

    private OnDimListener dimListener;

    private Activity activity;
    private Screen screen;

    public BasePreloadView(BaseActivity activity) {
        super(activity);

        this.activity = activity;

        screenDimensions();
        setCenterPos();

        addView();
    }

    public BasePreloadView(BaseActivity activity, float widthPercentage, float heightPercentage) {
        super(activity);

        this.activity = activity;

        screenDimensions();
        setCenterPos();
        x *= widthPercentage;
        y *= heightPercentage;

        addView();
    }

    private void screenDimensions() {

        x = screen.width;
        y = screen.height;
    }

    private void addView() {

        paintView = new Paint();
        paintView.setColor(Color.BLACK);
        layout = new RelativeLayout(activity);
        layout.addView(this);
    }

    public void show() {

        /*if(takeScreen){
            screen = BaseGL.getScreen(0, 0, (int)Base.screenWidth, (int)Base.screenHeight);
        }*/

        if (layout.getParent() != null) {
            ((ViewGroup) layout.getParent()).removeView(layout);
        }

        rising = true;
        hintTime = SystemClock.uptimeMillis();
        layout.setVisibility(View.VISIBLE);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.addContentView(layout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    public void showAsContentView() {

        rising = true;
        hintTime = 0;
        layout.setVisibility(View.VISIBLE);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setContentView(layout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    public void hide() {

        rising = false;
        hintTime = SystemClock.uptimeMillis();
        invalidate();
    }

    public void setCenterPos() {

        x = screen.width * 0.5f - w * 0.5f;
        y = screen.height * 0.5f - h * 0.5f;
    }

    public void setVerticalPos(float yPercentage) {

        y = screen.height * yPercentage;
    }

    public void setHorizontalPos(float xPercentage) {

        x = screen.width * xPercentage;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas == null) {
            return;
        }

        long currentDelay = SystemClock.uptimeMillis() - hintTime;
        if (rising) {
            if (currentDelay < dimDelay) {
                float progress = (float) currentDelay / (float) dimDelay;
                if (progress > 1.0f) progress = 1.0f;
                paintView.setAlpha((int) (255.0f * progress));
                canvas.drawRect(new RectF(0, 0, screen.width, screen.height), paintView);
                if (loaderAction != null) {
                    loaderAction.onRising(canvas, progress);
                }
            } else {
                paintView.setAlpha(255);
                canvas.drawRect(new RectF(0, 0, screen.width, screen.height), paintView);

                if (loaderAction != null) {
                    loaderAction.onWaiting(canvas);
                }

                if (dimListener != null) {
                    dimListener.onDimmed();
                    dimListener = null;
                    invalidate();
                    return;
                }
            }
        } else {
            if (currentDelay < dimDelay) {
                float progress = 1.0f - ((float) currentDelay / (float) dimDelay);
                paintView.setAlpha((int) (255.0f * progress));
                canvas.drawRect(new RectF(0, 0, screen.width, screen.height), paintView);
                if (loaderAction != null) {
                    loaderAction.onHiding(canvas, progress);
                }
            } else {
                ((ViewGroup) layout.getParent()).removeView(layout);
                layout.setVisibility(View.GONE);
            }
        }

        invalidate();
    }

    public void setDimDelay(int dimDelay) {
        this.dimDelay = dimDelay;
    }

    public void setDimListener(OnDimListener dimListener) {
        this.dimListener = dimListener;
    }

    public void setLoaderAction(LoaderAction loaderAction) {
        this.loaderAction = loaderAction;
    }

    public abstract class LoaderAction {

        protected Matrix matrix;
        protected Paint paintAction;

        public LoaderAction() {

            matrix = new Matrix();
            paintAction = new Paint();
        }

        public abstract void onRising(Canvas canvas, float progress);

        public abstract void onWaiting(Canvas canvas);

        public abstract void onHiding(Canvas canvas, float progress);

        public void rect(Canvas canvas, Bitmap bitmap, float yposratio, float rot) {

            matrix.reset();
            matrix.setTranslate(screen.width * 0.5f - bitmap.getWidth() * 0.5f, screen.height * yposratio - bitmap.getHeight() * 0.5f);
            matrix.postRotate(rot, screen.width * 0.5f, screen.height * yposratio);

            canvas.drawBitmap(bitmap, matrix, paintAction);
        }

        public void rect(Canvas canvas, Bitmap bitmap) {

            canvas.drawBitmap(bitmap, matrix, paintAction);
        }
    }

    public interface OnDimListener {

        public void onDimmed();
    }
}
