#version 150
in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;
uniform float RainAlpha;
uniform float NightBrightness;
uniform vec3 CameraPosition;
out vec2 texCoord0;
out float vertexDistance;
out vec4 vertexColor;
vec3 ichor_cloud_color(vec2 worldXZ) {
    float t = GameTime * 95.0;
    float broad = sin(dot(worldXZ, vec2(0.021, 0.014)) + t * 0.31)
                + sin(dot(worldXZ, vec2(-0.013, 0.026)) - t * 0.23)
                + sin(dot(worldXZ, vec2(0.033, -0.019)) + t * 0.17);
    float detail = sin(dot(worldXZ, vec2(0.049, 0.037)) - t * 0.41)
                 + sin(dot(worldXZ, vec2(-0.041, 0.052)) + t * 0.34);
    float pinkBlob = smoothstep(-0.48, 0.56, broad / 3.0 + detail * 0.10);
    float goldBlob = smoothstep(0.57, 0.94,
            sin(dot(worldXZ, vec2(0.017, -0.029)) + t * 0.19)
            * 0.72 + sin(dot(worldXZ, vec2(0.036, 0.011)) - t * 0.27) * 0.28);
    float mintBlob = smoothstep(0.58, 0.96,
            sin(dot(worldXZ, vec2(-0.024, -0.018)) + t * 0.22));

    vec3 cyan = vec3(0.03, 0.93, 1.00);
    vec3 pink = vec3(1.00, 0.08, 0.82);
    vec3 gold = vec3(1.00, 0.58, 0.06);
    vec3 mint = vec3(0.28, 1.00, 0.70);
    vec3 color = mix(cyan, pink, pinkBlob);
    color = mix(color, gold, goldBlob * 0.76);
    return mix(color, mint, mintBlob * (1.0 - goldBlob) * 0.62);
}

void main(){
    vec4 view=ModelViewMat*vec4(Position,1.0);
    gl_Position=ProjMat*view;
    vertexDistance=length(view.xyz);texCoord0=UV0;
    float shade=Normal.y>0.5?1.0:(Normal.y< -0.5?0.7:abs(Normal.x)>0.5?0.9:0.8);
    vertexColor=vec4(ichor_cloud_color(UV0*3072.0)*shade*NightBrightness,RainAlpha);
}
