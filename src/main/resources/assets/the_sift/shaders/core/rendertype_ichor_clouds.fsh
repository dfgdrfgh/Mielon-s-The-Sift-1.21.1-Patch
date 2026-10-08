#version 150
uniform sampler2D Sampler0;
uniform float FogEnd;
in vec2 texCoord0;
in float vertexDistance;
in vec4 vertexColor;
out vec4 fragColor;
void main(){vec4 color=texture(Sampler0,texCoord0)*vertexColor;if(color.a<0.01)discard;color.a*=1.0-smoothstep(0.0,FogEnd,vertexDistance);fragColor=color;}
