package com.base.lib.engine.other;

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

import com.base.lib.engine.Base;

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

    public BasePreloadView() {
        super(Base.context);

        screenDimensions();
        setCenterPos();

        addView();
    }

    public BasePreloadView(float widthPercentage, float heightPercentage) {
        super(Base.context);

        screenDimensions();
        setCenterPos();
        x *= widthPercentage;
        y *= heightPercentage;

        addView();
    }

    private void screenDimensions(){

        float[] dim = Base.getScreenDimensions(false);
        x = dim[0];
        y = dim[1];
    }

    private void addView(){

        paintView = new Paint();
        paintView.setColor(Color.BLACK);
        layout = new RelativeLayout(Base.context);
        layout.addView(this);
    }

    public void show(){

        /*if(takeScreen){
            screen = BaseGL.getScreen(0, 0, (int)Base.screenWidth, (int)Base.screenHeight);
        }*/

        if(layout.getParent() != null){
            ((ViewGroup)layout.getParent()).removeView(layout);
        }

        rising = true;
        hintTime = SystemClock.uptimeMillis();
        layout.setVisibility(View.VISIBLE);
        Base.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Base.activity.addContentView(layout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    public void showAsContentView(){

        rising = true;
        hintTime = 0;
        layout.setVisibility(View.VISIBLE);
        Base.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Base.activity.setContentView(layout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    public void hide(){

        rising = false;
        hintTime = SystemClock.uptimeMillis();
        invalidate();
    }

    public void setCenterPos(){

        x = Base.screenWidth * 0.5f - w*0.5f;
        y = Base.screenHeight * 0.5f - h*0.5f;
    }

    public void setVerticalPos(float yPercentage){

        y = Base.screenHeight * yPercentage;
    }

    public void setHorizontalPos(float xPercentage){

        x = Base.screenWidth * xPercentage;
    }

    @Override
    public void draw(Canvas canvas) {

        if(canvas == null){
            return;
        }

        long currentDelay = SystemClock.uptimeMillis() - hintTime;
        if(rising) {
            if (currentDelay < dimDelay) {
                float progress = (float) currentDelay / (float) dimDelay;
                if(progress > 1.0f) progress = 1.0f;
                paintView.setAlpha((int) (255.0f * progress));
                canvas.drawRect(new RectF(0, 0, Base.screenWidth, Base.screenHeight), paintView);
                if(loaderAction != null){
                    loaderAction.onRising(canvas, progress);
                }
            } else {
                paintView.setAlpha(255);
                canvas.drawRect(new RectF(0, 0, Base.screenWidth, Base.screenHeight), paintView);

                if(loaderAction != null){
                    loaderAction.onWaiting(canvas);
                }

                if(dimListener != null){
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
                canvas.drawRect(new RectF(0, 0, Base.screenWidth, Base.screenHeight), paintView);
                if(loaderAction != null){
                    loaderAction.onHiding(canvas, progress);
                }
            } else {
                ((ViewGroup)layout.getParent()).removeView(layout);
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

        public LoaderAction(){

            matrix = new Matrix();
            paintAction = new Paint();
        }

        public abstract void onRising(Canvas canvas, float progress);
        public abstract void onWaiting(Canvas canvas);
        public abstract void onHiding(Canvas canvas, float progress);

        public void rect(Canvas canvas, Bitmap bitmap, float yposratio, float rot){

            matrix.reset();
            matrix.setTranslate(Base.screenWidth*0.5f-bitmap.getWidth()*0.5f, Base.screenHeight * yposratio - bitmap.getHeight()*0.5f);
            matrix.postRotate(rot, Base.screenWidth*0.5f, Base.screenHeight * yposratio);

            canvas.drawBitmap(bitmap, matrix, paintAction);
        }

        public void rect(Canvas canvas, Bitmap bitmap){

            canvas.drawBitmap(bitmap, matrix, paintAction);
        }
    }

    public interface OnDimListener {

        public void onDimmed();
    }
}
