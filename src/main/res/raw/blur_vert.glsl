
uniform mat4 u_MVPMatrix;
uniform vec3 u_Center;

attribute vec4 a_Position;
attribute vec2 a_TexCoordinate;

varying vec2 v_TexCoordinate;
varying vec3 v_Center;

void main() {

    v_TexCoordinate = a_TexCoordinate;
    v_Center = u_Center;

    gl_Position = u_MVPMatrix * a_Position;
}