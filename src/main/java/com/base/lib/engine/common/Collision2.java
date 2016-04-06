package com.base.lib.engine.common;

import com.base.lib.engine.common.other.Point2;

/**
 * 14 Created by doctor on 16.7.13.
 */
public class Collision2 { //todo revise, rework

    /**
     * returns axis distance between points
     * */
    public static float length(float p1, float p2){

        final float length = p1 - p2;

        return Math.abs(length);
    }

    /**
     * returns distance between Point(p1X, p2X) and Point2(p2X, p2Y)
     * */
    public static float distance(float p1X, float p1Y, float p2X, float p2Y){

        return (float) Math.sqrt( (Math.pow(length(p1X, p2X), 2) + Math.pow(length(p1Y, p2Y), 2)));
    }

    /**
     * returns distance between Points
     * */
    public static float distance(Point2 p1, Point2 p2){

        return distance(p1.x, p1.y, p2.x, p2.y);
    }

    public static boolean isOnScrean(float cameraPosX, float cameraPosY, float posX, float posY, float hWidth, float hHeight, float hScreenWidth, float hScreenHeight){

        if(length(cameraPosX, posX)-hWidth <= hScreenWidth){
            if(length(cameraPosY, posY)-hHeight <= hScreenHeight){
                return true;
            }
        }

        return false;
    }

    /**
     * returns collision result between circle and point
     * */
    public static boolean circleHit(Point2 circlePosition, float circleRadius, Point2 pointerPosition){

        if(circleRadius > distance(circlePosition.x, circlePosition.y, pointerPosition.x, pointerPosition.y)){
            return true;
        }

        return false;
    }

    /**
     * returns collision result between circle and point
     * */
    public static boolean circleHit(float circlePositionX, float circlePositionY, float circleRadius, Point2 pointerPosition){

        if(circleRadius > distance(circlePositionX, circlePositionY, pointerPosition.x, pointerPosition.y)){
            return true;
        }

        return false;
    }

    /**
     * returns collision result between two circles
     * */
    public static boolean circlesHit(Point2 stCirclePos, float stCircleRadius, Point2 ndCirclePos, float ndCircleRadius){

        return circleHit(stCirclePos, stCircleRadius+ndCircleRadius, ndCirclePos);
    }

    /**
     * returns collision result between rectangle and point
     * */
    public static boolean rectHit(Point2 rectPosition, float width, float height, Point2 pointerPosition){

        if(length(rectPosition.x, pointerPosition.x) < width/2){
            if(length(rectPosition.y, pointerPosition.y) < height/2){
                return true;
            }
        }

        return false;
    }

    /**
     * returns collision result between rectangle and point
     * */
    public static boolean rectHit(float rectPosX, float rectPosY, float width, float height, Point2 pointerPosition){

        if(length(rectPosX, pointerPosition.x) < width/2){
            if(length(rectPosY, pointerPosition.y) < height/2){
                return true;
            }
        }

        return false;
    }

    /**
     * returns collision result between two rectangles
     * */
    public static boolean rectsHit(Point2 stRectPos, float stWidth, float stHeight, Point2 ndRectPos, float ndWidth, float ndHeight){

        return rectHit(stRectPos, stWidth+ndWidth, stHeight+ndHeight, ndRectPos);
    }

    /**
     * returns collision result between two rectangles
     * */
    public static boolean rectsHit(float r1x, float r1y, float r1hW, float r1hH, float r2x, float r2y, float r2hW, float r2hH){

        if(length(r1x, r2x) < r1hW+r2hW){
            if(length(r1y , r2y) < r1hH+r2hH){
                return true;
            }
        }
        return false;
    }

    /**
     * returns collision result between square and point
     * */
    public static boolean squareHit(Point2 squarePosition, float width, Point2 pointerPosition){

        return rectHit(squarePosition, width, width, pointerPosition);
    }

    /**
     * returns collision result between two squares
     * */
    public static boolean squaresHit(Point2 stSquarePos, float stWidth, Point2 ndSquarePos, float ndWidth){

        return squareHit(stSquarePos, stWidth+ndWidth, ndSquarePos);
    }


    //todo point/circle/rect line hit

}


