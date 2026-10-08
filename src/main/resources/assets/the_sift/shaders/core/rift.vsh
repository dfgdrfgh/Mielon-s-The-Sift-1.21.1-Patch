#version 150
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;


in vec3 Position;
in vec2 UV0;
in vec4 Color;
out vec2 coord;
out vec4 material;
out vec3 viewPosition;
void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * view;
    viewPosition = view.xyz;
    coord = UV0;
    material = Color;
}
