#version 120

uniform sampler2D tex;
varying vec2 texture_coordinate;

void main()
{
    gl_FragColor = texture2D(tex, fract(texture_coordinate));
}