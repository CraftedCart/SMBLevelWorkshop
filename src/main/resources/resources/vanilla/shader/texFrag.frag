#version 120
varying vec3 position;
varying vec3 normal;
varying vec2 texture_coordinate;

uniform sampler2D tex;

uniform vec3 lightPos = vec3(10000, 15000, 5000);

uniform vec3 mambient = vec3(0.7, 0.7, 0.7);  //gl_FrontMaterial
uniform vec3 mdiffuse = vec3(1.3, 1.3, 1.3);
uniform vec3 mspecular = vec3(0.2, 0.2, 0.2);
uniform float shininess = 0.2f;

uniform vec3 lambient = vec3(0.3, 0.3, 0.3);  //gl_LightSource[0]
uniform vec3 ldiffuse = vec3(0.8, 0.8, 0.8);
uniform vec3 lspecular = vec3(0.3, 0.3, 0.3);


void main()
{
        float dist=length(position-lightPos);   //distance from light-source to surface
//        float att=1.0/(1.0+0.1*dist+0.01*dist*dist);    //attenuation (constant,linear,quadric)
        vec3 ambient=mambient*lambient; //the ambient light

        vec3 surf2light=normalize(lightPos-position);
        vec3 norm=normalize(normal);
        float dcont=max(0.0,dot(norm,surf2light));
        vec3 diffuse=dcont*(mdiffuse*ldiffuse);

        vec3 surf2view=normalize(-position);
        vec3 reflection=reflect(-surf2light,norm);

        float scont=pow(max(0.0,dot(surf2view,reflection)),shininess);
        vec3 specular=scont*lspecular*mspecular;

        gl_FragColor=vec4((ambient+diffuse/*+specular*/)/**att*/,1.0) * texture2D(tex, fract(texture_coordinate));  //<- don't forget the paranthesis (ambient+diffuse+specular)
}