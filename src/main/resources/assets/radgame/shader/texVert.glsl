#version 330 core

in layout (location = 0) vec3 vertexPosition;
in layout (location = 1) vec2 uvPos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

out vec2 uv;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
    uv = uvPos;
}
