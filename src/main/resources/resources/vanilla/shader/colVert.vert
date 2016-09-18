#version 120

varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;

void main()
{
    /* first transform the normal into eye space and
    normalize the result */
    normal = normalize(gl_NormalMatrix * gl_Normal);

    /* pass the halfVector to the fragment shader */
    halfVector = gl_LightSource[0].halfVector.xyz;

    /* Compute the diffuse, ambient and globalAmbient terms */
    diffuse = gl_FrontMaterial.diffuse * gl_Color;
    ambient = gl_FrontMaterial.ambient * gl_Color;
    ambient += gl_LightModel.ambient * gl_Color;
    gl_Position = ftransform();
}