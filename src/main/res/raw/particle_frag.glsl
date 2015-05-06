precision mediump float;

uniform sampler2D u_Texture;

uniform float u_SpriteSize;

varying vec4 v_Color;
varying vec2 v_Texture;

void main() {

    vec2 realTexCoord = v_Texture + (gl_PointCoord * u_SpriteSize);

    gl_FragColor = texture2D(u_Texture, realTexCoord) * v_Color;
}