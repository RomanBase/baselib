precision mediump float;       	// Set the default precision to medium.
uniform sampler2D u_Texture;    // The input texture.
  
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  
// The entry point for our fragment shader.
void main()
{

    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}                                                                     	

