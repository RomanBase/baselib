precision mediump float;

uniform sampler2D u_Texture;
  
varying vec2 v_TexCoordinate;
varying vec3 v_Center;

void main()
{
    vec4 sum = vec4(0.0);

    vec3 u_Center = normalize(v_Center);
	vec2 len = abs(vec2(u_Center.x, u_Center.y) - v_TexCoordinate.xy);
    float ratio = ((len.x + len.y) * 0.8);

    float vv = 0.25;
    float v = vv/12.0;

    sum += texture2D(u_Texture, v_TexCoordinate) * (0.25-vv);

    sum += texture2D(u_Texture, v_TexCoordinate + vec2(-0.0050*ratio, 0.00625*ratio)) * (0.125+v);
    sum += texture2D(u_Texture, v_TexCoordinate + vec2(+0.0075*ratio, 0.01250*ratio)) * (0.09375+v);
    sum += texture2D(u_Texture, v_TexCoordinate + vec2(-0.0125*ratio, 0.02500*ratio)) * (0.0625+v);
    sum += texture2D(u_Texture, v_TexCoordinate + vec2(+0.0150*ratio, 0.03750*ratio)) * (0.046875+v);
    sum += texture2D(u_Texture, v_TexCoordinate + vec2(-0.0175*ratio, 0.05000*ratio)) * (0.03125+v);
    sum += texture2D(u_Texture, v_TexCoordinate + vec2(+0.0250*ratio, 0.06250*ratio)) * (0.015625+v);

    sum += texture2D(u_Texture, v_TexCoordinate - vec2(-0.0050*ratio, 0.00625*ratio)) * (0.125+v);
    sum += texture2D(u_Texture, v_TexCoordinate - vec2(+0.0075*ratio, 0.01250*ratio)) * (0.09375+v);
    sum += texture2D(u_Texture, v_TexCoordinate - vec2(-0.0125*ratio, 0.02500*ratio)) * (0.0625+v);
    sum += texture2D(u_Texture, v_TexCoordinate - vec2(+0.0150*ratio, 0.03750*ratio)) * (0.046875+v);
    sum += texture2D(u_Texture, v_TexCoordinate - vec2(-0.0175*ratio, 0.05000*ratio)) * (0.03125+v);
    sum += texture2D(u_Texture, v_TexCoordinate - vec2(+0.0250*ratio, 0.06250*ratio)) * (0.015625+v);

    gl_FragColor = sum;
}