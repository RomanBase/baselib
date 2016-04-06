package com.base.lib.wall;

import android.content.res.Configuration;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseGLView;
import com.base.lib.engine.BaseRenderer;
import com.base.lib.engine.builders.CameraBuilder;
import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.interfaces.ActivityStateListener;

/**
 *
 */
public abstract class BasePaperService extends WallpaperService {

    protected Base base;
    protected PaperBase paperBase;

    @Override
    public Engine onCreateEngine() {

        Engine engine = new PaperEngine();

        Base.appContext = this;
        base = new Base(null, this);
        paperBase = new PaperBase(base, this);

        return engine;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        base.onConfigrationChanged();

        if (base.render != null) {
            paperBase.onConfigurationChanged(newConfig);
        }
    }

    protected abstract BasePaperRender onCreate(RenderConfig config);

    protected abstract BaseCamera onCreateCamera(CameraBuilder builder);

    protected abstract View onCreatePreview();

    public class PaperEngine extends Engine {

        private View view;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            if (isPreview()) {
                view = onCreatePreview();
                if (view == null) {
                    initPaperRender();
                }
            } else {
                initPaperRender();
            }
        }

        private void initPaperRender() {

            RenderConfig config = new RenderConfig(base, paperBase);
            base.init(onCreateCamera(new CameraBuilder(base)), paperBase.service.onCreate(config));
            base.render.setGLView(new BasePaperGLView(config));
            view = base.render.getView();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);

            if (base.render != null) { //// TODO: 8. 3. 2016 ??
                paperBase.onOffsetChange(xOffset, xOffsetStep);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {

            view.onTouchEvent(event);
            super.onTouchEvent(event);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                if (view instanceof ActivityStateListener) {
                    ((ActivityStateListener) view).onResume();
                }
                onResumePaper();
            } else {
                if (view instanceof ActivityStateListener) {
                    ((ActivityStateListener) view).onPause();
                }
                onPausePaper();
            }
        }

        public class BasePaperGLView extends BaseGLView {

            public BasePaperGLView(RenderConfig config) {
                super(config);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            @Override
            public void onPause() {

                stopRenderThread();

                BaseRenderer renderer = getRenderer();
                if (renderer != null) {
                    renderer.onPause();
                }
            }

            @Override
            public void onResume() {

                BaseRenderer renderer = getRenderer();
                if (renderer != null) {
                    renderer.onResume();
                }

                startRenderThread();
            }

            @Override
            public void destroy() {
                super.destroy();
                super.onDetachedFromWindow();
            }
        }
    }

    protected void onResumePaper() {

    }

    protected void onPausePaper() {

    }
}
