package com.base.lib.engine.other;

import com.base.lib.engine.Base;
import com.base.lib.interfaces.ActivityStateListener;

/**
 *
 */
public class ScreenDecorationHider implements ActivityStateListener {

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

        if(Base.hasContext()) {
            Base.activity.hideScreenDeco();
        }
    }

    @Override
    public void destroy() {

    }
}
