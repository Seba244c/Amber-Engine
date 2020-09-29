#version 330

in vec2 position;
in vec2 textureCords;

out vec4 color;
out vec2 uvCoords;

uniform mat4 projection;

uniform vec4 matColor;
uniform vec4 offset;

uniform vec2 pixelScale;
uniform vec2 screenPos;

void main()
{
	color = matColor;
	
	gl_Position = projection * vec4((position * pixelScale) + screenPos, 0, 1);
	uvCoords = (textureCords * offset.zw) + offset.xy;
}

ENDVERTEX

#version 330

uniform sampler2D sampler;

in vec4 color;
in vec2 uvCoords;

out vec4 fragColor;

void main()
{
	fragColor = color * texture(sampler, uvCoords);
}