package com.base.lib.engine.controls;

import java.util.ArrayList;
import java.util.List;

public class BaseUILayerNavigator {

    protected List<BaseUILayer> layerStack;

    public BaseUILayerNavigator(BaseUILayer root) {

        layerStack = new ArrayList<>();
        layerStack.add(root);
    }

    public void replaceRoot(BaseUILayer newRoot) {

        layerStack.remove(0);
        layerStack.add(0, newRoot);
    }

    public void addLayer(BaseUILayer layer) {

        layerStack.add(layer);
    }

    public BaseUILayer getTopLayer() {

        return layerStack.get(layerStack.size() - 1);
    }

    public BaseUILayer getPreviousLayer() {

        int count = layerStack.size();
        if (count > 1) {
            layerStack.get(count - 2);
        }

        return layerStack.get(0);
    }

    public void presentTopLayer() {

        //todo ((BaseRender) Base.render).setUiLayer(getTopLayer(), true);
    }

    public boolean navigateBack() {

        int count = layerStack.size();
        if (count > 1) {
            BaseUILayer layer = layerStack.remove(count - 1);
            BaseUILayer ui = layerStack.get(count - 2);
            return true;
        } else {
            return false;
        }
    }

    public void clearStack() {

        int count = layerStack.size();
        if (count > 1) {
            BaseUILayer root = layerStack.get(0);
            layerStack.clear();
            layerStack.add(root);
        }
    }
}
