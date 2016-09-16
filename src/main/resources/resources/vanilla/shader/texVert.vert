varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;

varying vec2 texture_coordinate;

void main()
{
    /* first transform the normal into eye space and
    normalize the result */
    normal = normalize(gl_NormalMatrix * gl_Normal);

    /* pass the halfVector to the fragment shader */
    halfVector = gl_LightSource[0].halfVector.xyz;

    /* Compute the diffuse, ambient and globalAmbient terms */
    diffuse = gl_FrontMaterial.diffuse * gl_Color;
    ambient = vec4(0.3, 0.3, 0.3, 1.0);
    gl_Position = ftransform();

    texture_coordinate = vec2(gl_MultiTexCoord0);
}