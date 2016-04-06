precision mediump float;       	// Set the default precision to medium.
uniform sampler2D u_Texture;    // The input texture.
uniform sampler2D u_Normals;    // The input normals texture.
  
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
varying vec3 v_LightPos;
  
// The entry point for our fragment shader.
void main()
{
    // lookup normal from normal map, move from [0,1] to  [-1, 1] range, normalize
    vec3 normal = normalize(2.0 * texture2D(u_Normals, v_TexCoordinate.st).rgb - 1.0);

    float factor = max(dot(normal, v_LightPos), 0.15);

    vec4 diffuse = texture2D(u_Texture, v_TexCoordinate);

    vec3 color = factor * diffuse.rgb;

    gl_FragColor = vec4(color, diffuse.a);
}                                                                     	

