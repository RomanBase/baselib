package com.base.lib.engine.builders;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseObject;

public class CameraBuilder extends BaseObject {

    private boolean perspective = true;
    private boolean landscape = true;
    private float size = 10.0f;
    private float near;
    private float far;
    private float fovy;

    public CameraBuilder(Base base) {
        super(base);
    }

    public CameraBuilder setDimensions(int dimensions) {

        if (dimensions != 2 && dimensions != 3) {
            throw new RuntimeException(String.format("Monkeys says: Maybe next time we will see you in your special %d dimensional world. But not this time! [2, 3]", dimensions));
        }

        perspective = dimensions == 3;
        return this;
    }

    public CameraBuilder setViewAngle(float fovy) {
        this.fovy = fovy;
        return this;
    }

    public CameraBuilder setSizeOrientationLandscape(boolean landscape) {
        this.landscape = landscape;
        return this;
    }

    public CameraBuilder setSize(float size) {
        this.size = size;
        return this;
    }

    public CameraBuilder setNear(float near) {
        this.near = near;
        return this;
    }

    public CameraBuilder setFar(float far) {
        this.far = far;
        return this;
    }

    public BaseCamera build() {

        size = landscape ? size : size * base.screen.ratio;

        BaseCamera camera = new BaseCamera(base);
        if (perspective) {
            if (near == 0.0f) {
                near = base.screen.ratio;
            }

            if (far == 0.0f) {
                far = 1000.0f;
            }

            if (fovy == 0.0f) {
                fovy = base.screen.width > base.screen.height ? 45.0f : 30.0f;
            }

            camera.set3DCamera(fovy, size, near, far);
        } else {
            if (near == 0.0f) {
                near = size * 0.5f;
            }

            if (far == 0.0f) {
                far = 1.0f;
            }

            camera.set2DCamera(size, near, far);
        }

        return camera;
    }
}
