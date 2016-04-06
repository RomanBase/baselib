package com.base.lib.wall;

import android.graphics.Bitmap;

import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseRenderer;
import com.base.lib.engine.BaseRootCollection;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.engine.controls.BaseUILayer;

public class BasePaperRender extends BaseRenderer {

    protected PaperBase paperBase;

    protected BaseUILayer uiLayer;
    protected BaseTexture background;

    protected BaseRootCollection postRenderables;

    public BasePaperRender(RenderConfig config) {
        super(config);
        paperBase = config.getPaperBase();

        postRenderables = new BaseRootCollection(base, 64);
    }

    public void setBackground(final Bitmap bitmap) {

        if (bitmap == null) {
            loadDefaultBackground();
            return;
        }

        base.gl.glRunAsync(new Runnable() {
            @Override
            public void run() {
                paperBase.init((float) bitmap.getWidth() / (float) bitmap.getHeight());

                if (background == null) {
                    background = base.factory.gen.textureBitmap("BaseWallTexture", bitmap);
                } else {
                    background.load(bitmap);
                }

                render();
                System.gc();
            }
        });
    }

    protected void loadDefaultBackground() {

    }

    /**
     * sets ui layer and register touch listener if required
     */
    public void setUiLayer(BaseUILayer ui, boolean registerListener) {

        uiLayer = ui;
        if (registerListener) {
            setTouchListener(uiLayer);
        }
    }

    /**
     * destroys ui layer and sets its reference to null
     */
    public void destroyUiLayer() {

        setTouchListener(null);
        if (uiLayer != null) {
            uiLayer.destroy();
            uiLayer = null;
        }
    }

    /**
     * @return current ui layer
     */
    public BaseUILayer getUiLayer() {

        return uiLayer;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onUpdate() {

        if (uiLayer != null) {
            uiLayer.update();
        }
    }

    @Override
    protected void onPreDraw() {

    }

    @Override
    protected void onPostDraw() {

        if (uiLayer != null) {
            uiLayer.draw();
        }

        postRenderables.updateToDraw();
    }

    @Override
    protected void onDestroy() {

        if (uiLayer != null) {
            uiLayer.destroy();
        }
    }

    public void addPostDrawable(BaseRenderable renderable) {

        postRenderables.add(renderable);
    }

}
