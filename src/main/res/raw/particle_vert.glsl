precision mediump float;
uniform mat4 u_VPMatrix;
uniform float u_ScaleRatio;

attribute vec4 a_Position;
attribute vec2 a_Texture;
attribute vec4 a_Color;

varying vec4 v_Color;
varying vec2 v_Texture;

void main() {

    v_Color = a_Color;
    v_Texture = a_Texture;

//todo z scaling by far ratio
    gl_Position =  u_VPMatrix * vec4(a_Position.xyz, 1.0);
    gl_PointSize = a_Position.w * u_ScaleRatio;
}