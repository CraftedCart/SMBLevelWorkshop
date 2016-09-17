varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;

uniform sampler2D tex;
varying vec2 texture_coordinate;

void main()
{
    vec3 n,halfV,lightDir;
    float NdotL,NdotHV;

    lightDir = vec3(5, 5, 5);

    /* The ambient term will always be present */
    vec4 color = ambient;
    /* a fragment shader can't write a varying variable, hence we need
    a new variable to store the normalized interpolated normal */
    n = normalize(normal);
    /* compute the dot product between normal and ldir */

    NdotL = max(dot(n,lightDir),0.0);
	if (NdotL > 0.0) {
        color += diffuse * NdotL;
        halfV = normalize(halfVector);
        NdotHV = max(dot(n,halfV),0.0);
        color += gl_FrontMaterial.specular *
                0.3 *
                pow(NdotHV, gl_FrontMaterial.shininess);
    }

    gl_FragColor = clamp(color, 0.0, 1.0) * texture2D(tex, fract(texture_coordinate));
}