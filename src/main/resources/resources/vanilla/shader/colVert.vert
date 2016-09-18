#version 120
varying vec3 position;
varying vec3 normal;

varying vec4 color;

void main()
{
        gl_Position=gl_ModelViewProjectionMatrix*gl_Vertex;     //output position with projection
        position=vec3(gl_ModelViewMatrix*gl_Vertex);    //get the position of the vertex after translation, rotation, scaling
        normal=gl_NormalMatrix*gl_Normal;       //get the normal direction, after translation, rotation, scaling
        color = gl_Color;
}