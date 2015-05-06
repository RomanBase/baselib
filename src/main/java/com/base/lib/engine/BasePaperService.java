package com.base.lib.engine;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 *
 */
public abstract class BasePaperService extends WallpaperService {

    public BasePaperService(){

        Base.context = this;
    }

    @Override
    public Engine onCreateEngine() {

        return new PaperEngine(onCreate(new BaseOptions()));
    }

    protected abstract BaseRender onCreate(BaseOptions cfg);

    public class PaperEngine extends Engine {

        private BasePaperView view;

        /** Engine Constructor */
        public PaperEngine(BaseRender render){

            Base.init(getBaseContext());

            this.view = new BasePaperView(render);
            Base.glView = view;
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
                view.onResume();
            } else {
                view.onPause();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            BaseGL.destroy();
            view.destroy();
        }

        private class BasePaperView extends BaseGLView {

            public BasePaperView(BaseRenderer renderer) {
                super(renderer);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            @Override
            public void onPause() {

                stopRenderThread();
            }

            @Override
            public void onResume() {

                startRenderThread();
            }

            @Override
            public void destroy() {
                super.destroy();
                super.onDetachedFromWindow();
            }
        }
    }
}
