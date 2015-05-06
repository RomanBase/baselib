uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.
uniform vec3 u_Light;           // A constant light position

attribute vec4 a_Position;		// Per-vertex position information we will pass in.
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.
		   
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.
varying vec3 v_LightPos;        // This will be passed into the fragment shader.

// The entry point for our vertex shader.  
void main()                                                 	
{
	v_TexCoordinate = a_TexCoordinate;

	v_LightPos = normalize(u_Light);

	gl_Position = u_MVPMatrix * a_Position;                       		  
}                                                          