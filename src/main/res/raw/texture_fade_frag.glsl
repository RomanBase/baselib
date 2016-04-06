precision mediump float;       	// Set the default precision to medium.
uniform sampler2D u_Texture;    // The input texture.
uniform vec4 u_Color;

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

void main() {

    gl_FragColor = texture2D(u_Texture, v_TexCoordinate) * u_Color;
}