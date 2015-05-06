package com.base.lib.engine.other.dev;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseUpdateable;
import com.base.lib.engine.common.BaseTimer;

/**
 *
 */
public class StepLogger {

    public static void log(final Object o){

        Base.render.addUpdateable(new BaseUpdateable() {
            @Override
            public void update() {
                Base.logV(o.toString());
            }

            @Override
            public void destroy() {

            }
        });
    }

    public static void log(final Object o, final long logDelay){

        Base.logV(o.toString());
        (new BaseTimer(logDelay) {
            @Override
            public void onDone() {
                Base.logV(o.toString());
                reset();
            }
        }).use();
    }
}
