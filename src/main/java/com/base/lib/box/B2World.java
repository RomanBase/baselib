package com.base.lib.box;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.BaseRenderer;
import com.base.lib.engine.BaseUpdateable;
import com.base.lib.interfaces.ActivityStateListener;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 12 Created by doctor on 12.8.13.
 */
public class B2World extends BaseUpdateable implements ActivityStateListener { //todo remove bodyqueue

    private World world;
    private List<B2Body> bodyQueue;

    private BaseRenderer renderer;

    public static int velocityIter = 8;
    public static int positionIter = 3;

    private boolean worldUpdated;

    public B2World(Base base, float gx, float gy, BaseRenderer renderer) {
        super(base);
        init(new World(new Vec2(gx, gy)), renderer);
    }

    /**
     * request to call init method !!
     */
    protected B2World(Base base) {
        super(base);
    }

    protected void init(World world, BaseRenderer renderer) {

        B2.b2World = world;
        B2.world = this;

        this.world = world;
        this.renderer = renderer;

        B2.calcRatio(base);

        world.setAllowSleep(true);
        worldUpdated = true;

        bodyQueue = Collections.synchronizedList(new ArrayList<B2Body>());
    }

    public void useB2Profiles() {

        if (Base.debug) {
            B2Profile.useProfile = true;
        }
    }

    public void setContactListener(ContactListener listener) {

        world.setContactListener(listener);
    }

    public void remove(Body body) {

        if (worldUpdated) {
            removeBody(body);
        } else {
            synchronized (bodyQueue) {
                bodyQueue.add(new B2Body(body, Action.DESTROY));
            }
        }
    }

    public void removeBody(Body body) {

        if (body != null) {
            Fixture fixture = body.m_fixtureList;
            if (fixture != null) {
                fixture.m_userData = null;
                body.destroyFixture(fixture);
                fixture = null;
            }
            body.m_userData = null;
            world.destroyBody(body);
            body = null;
        }
    }

    public void remove(List<Body> bodies) {

        for (Body body : bodies) {
            remove(body);
        }
    }

    public void remove(Joint joint) {

        world.destroyJoint(joint);
    }

    public Joint create(JointDef jd) {

        return world.createJoint(jd);
    }

    public Body createBody(BodyDef bd, FixtureDef fd) {

        return world.createBody(bd).createFixture(fd).getBody();
    }

    public Body createBody(BodyDef bd) {

        return world.createBody(bd);
    }

    public void create(BodyDef bd, FixtureDef fd) {

        create(null, bd, fd);
    }

    public void create(B2BodyListener listener, BodyDef bd, FixtureDef fd) {

        if (worldUpdated) {
            Body body = world.createBody(bd).createFixture(fd).m_body;
            BaseDrawable profile = B2Profile.body(body);
            if (profile != null) {
                profile.use();
            }
            if (listener != null) {
                listener.onCreate(body, profile);
            }
        } else {
            synchronized (bodyQueue) {
                bodyQueue.add(new B2Body(listener, bd, fd, Action.CREATE));
                Base.logV("used queue");
            }
        }
    }

    public World world() {

        return world;
    }

    public void createBodies() {

        preUpdate();
    }

    private void preUpdate() {

        if (!bodyQueue.isEmpty()) {
            synchronized (bodyQueue) {
                Iterator<B2Body> iterator = bodyQueue.iterator();
                while (iterator.hasNext()) {
                    B2Body b2 = iterator.next();

                    switch (b2.act) {
                        case CREATE:
                            Body body = world.createBody(b2.bd);
                            body.createFixture(b2.fd);

                            BaseDrawable profile = B2Profile.body(body);
                            if (b2.b2object != null)
                                b2.b2object.onCreate(body, profile);

                            if (profile != null)
                                renderer.addDrawable(profile);
                            break;
                        case DESTROY:
                            removeBody(b2.body);
                            break;
                    }

                    iterator.remove();
                    b2 = null;
                }
            }
        }
    }

    @Override
    public void update() {

        if (worldUpdated) {
            worldUpdated = false;
            preUpdate();
            world.step(base.time.delta, velocityIter, positionIter);
            worldUpdated = true;
        }
    }

    public boolean isWorldUpdated() {

        return worldUpdated;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    private void removeBodyIteration(Body body) {

        if (body != null) {
            removeFixtureIteration(body, body.getFixtureList());
            body.m_userData = null;
            Body next = body.getNext();
            world.destroyBody(body);
            removeBodyIteration(next);
        }
    }

    private void removeFixtureIteration(Body body, Fixture fixture) {

        if (fixture != null) {
            fixture.m_userData = null;
            Fixture next = fixture.getNext();
            body.destroyFixture(fixture);
            removeFixtureIteration(body, next);
        }
    }

    public void clearWorld() {

        removeBodyIteration(world.getBodyList());
    }

    @Override
    public void destroy() {

        clearWorld();
    }

    private enum Action {CREATE, DESTROY}

    private class B2Body {

        protected B2BodyListener b2object;
        protected BodyDef bd;
        protected FixtureDef fd;
        protected Action act;
        protected Body body;

        protected B2Body(B2BodyListener listener, BodyDef bodyDef, FixtureDef fixtureDef, Action action) {

            b2object = listener;
            bd = bodyDef;
            fd = fixtureDef;
            act = action;
        }

        protected B2Body(Body body, Action action) {
            this.body = body;
            act = action;
        }
    }

}
