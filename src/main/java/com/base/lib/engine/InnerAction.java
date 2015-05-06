package com.base.lib.engine;

import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.Point3;

/**
 *
 */
public class InnerAction {

    private Point3 sRot;
    private Point3 sPos;
    private float sScale;

    private Point3 fRot;
    private Point3 fPos;
    private float fScale;

    private Colorf sColor;
    private Colorf fColor;

    protected float step;

    public float rotX;
    public float rotY;
    public float rotZ;
    public float posX;
    public float posY;
    public float posZ;
    public float scale;
    public Colorf color;

    public float t;
    public double s;

    public InnerAction() {

        init();
    }

    public InnerAction(InnerAction action){

        init();
        Point3.copy(action.sRot, sRot);
        Point3.copy(action.sPos, sPos);
        sScale = action.sScale;
        sColor = new Colorf(action.sColor);
        Point3.copy(action.fRot, fRot);
        Point3.copy(action.fPos, fPos);
        fScale = action.fScale;
        fColor = new Colorf(action.fColor);
        s = action.s;
    }

    private void init() {

        sRot = new Point3();
        sPos = new Point3();
        sScale = 1.0f;
        sColor = new Colorf(1.0f, 1.0f, 1.0f, 1.0f);

        fRot = new Point3();
        fPos = new Point3();
        fScale = 1.0f;
        fColor = new Colorf(1.0f, 1.0f, 1.0f, 1.0f);

        color = new Colorf();

        t = 0.0f;
        s = 0.01f;
    }

    public void setStart(float px, float py, float pz, float rx, float ry, float rz, float s){

        setStartPos(px, py, pz);
        setStartRot(rx, ry, rz);
        setStartScale(s);
    }

    public void setStartRot(float x, float y, float z){

        sRot.x = x;
        sRot.y = y;
        sRot.z = z;
    }

    public void setStartPos(float x, float y, float z){

        sPos.x = x;
        sPos.y = y;
        sPos.z = z;
    }

    public void setStartScale(float s){

        sScale = s;
    }

    public void setStartColor(float r, float g, float b, float a){

        sColor.r = r;
        sColor.g = g;
        sColor.b = b;
        sColor.a = a;
    }

    public void setFinal(float px, float py, float pz, float rx, float ry, float rz, float s){

        setFinalPos(px, py, pz);
        setFinalRot(rx, ry, rz);
        setFinalScale(s);
    }

    public void setFinal(float dirX, float dirY, float dirZ, float step, float rx, float ry, float rz, float s){

        setFinalPos(dirX, dirY, dirZ, step);
        setFinalRot(rx, ry, rz);
        setFinalScale(s);
    }

    public void setFinalRot(float x, float y, float z){

        fRot.x = x;
        fRot.y = y;
        fRot.z = z;
    }

    public void setFinalPos(float x, float y, float z){

        fPos.x = x;
        fPos.y = y;
        fPos.z = z;

        step = (float) (Point3.distance(sPos, fPos)/(1.0/s));
    }

    /** dir vector */
    public void setFinalPos(float dirX, float dirY, float dirZ, float step){ //todo

        double mag = Math.sqrt(dirX*dirX + dirY*dirY + dirZ*dirZ);
        dirX /= mag;
        dirY /= mag;
        dirZ /= mag;

        float dist = (float) ((1.0/s) * step);
        setFinalPos(sPos.x+dirX*dist, sPos.y+dirY*dist, sPos.z+dirZ*dist);
    }

    public void setFinalScale(float s){

        fScale = s;
    }

    public void setFinalColor(float r, float g, float b, float a){

        fColor.r = r;
        fColor.g = g;
        fColor.b = b;
        fColor.a = a;
    }

    public void modifyStartPos(float x, float y, float z){

        sPos.x += x;
        sPos.y += y;
        sPos.z += z;
    }

    public void reverse(){

        t = 1.0f - t;
        Point3 temp = new Point3();
        Point3.copy(sPos, temp);
        Point3.copy(fPos, sPos);
        Point3.copy(temp, fPos);

        Point3.copy(sRot, temp);
        Point3.copy(fRot, sRot);
        Point3.copy(temp, fRot);

        Colorf tempc = new Colorf(sColor);
        sColor = fColor;
        fColor = tempc;

        float t = sScale;
        sScale = fScale;
        fScale = t;
    }

    public void setTime(long millis){

        s = 1.0/(millis/Base.render.getRequestedFPS());
    }

    public InnerAction next() {

        t += s;

        if(t > 1.0f){
            t = 1.0f;
        }

        return next(t);
    }

    public InnerAction next(float t){

        posX = interpolate(sPos.x, fPos.x, t);
        posY = interpolate(sPos.y, fPos.y, t);
        posZ = interpolate(sPos.z, fPos.z, t);

        rotX = interpolate(sRot.x, fRot.x, t);
        rotY = interpolate(sRot.y, fRot.y, t);
        rotZ = interpolate(sRot.z, fRot.z, t);

        scale = interpolate(sScale, fScale, t);

        color.r = interpolate(sColor.r, fColor.r, t);
        color.g = interpolate(sColor.g, fColor.g, t);
        color.b = interpolate(sColor.b, fColor.b, t);
        color.a = interpolate(sColor.a, fColor.a, t);

        return this;
    }

    public boolean isDone(){

        return t >= 1.0f;
    }

    private float interpolate(float i, float ii, float t) {

        return i + (ii - i) * t;
    }

    public Point3 getsRot() {
        return sRot;
    }

    public void setsRot(Point3 sRot) {
        this.sRot = sRot;
    }

    public Point3 getsPos() {
        return sPos;
    }

    public void setsPos(Point3 sPos) {
        this.sPos = sPos;
    }

    public float getsScale() {
        return sScale;
    }

    public void setsScale(float sScale) {
        this.sScale = sScale;
    }

    public Point3 getfRot() {
        return fRot;
    }

    public void setfRot(Point3 fRot) {
        this.fRot = fRot;
    }

    public Point3 getfPos() {
        return fPos;
    }

    public void setfPos(Point3 fPos) {
        this.fPos = fPos;
    }

    public void setfPos(float[] fPos) {
        this.fPos.x = fPos[0];
        this.fPos.y = fPos[1];
        this.fPos.z = fPos[2];
    }

    public float getfScale() {
        return fScale;
    }

    public void setfScale(float fScale) {
        this.fScale = fScale;
    }

    public Colorf getsColor() {
        return sColor;
    }

    public void setsColor(Colorf sColor) {
        this.sColor = sColor;
    }

    public Colorf getfColor() {
        return fColor;
    }

    public void setfColor(Colorf fColor) {
        this.fColor = fColor;
    }

    public int getStepsCount(){

        return (int) (1.0/s);
    }
}
