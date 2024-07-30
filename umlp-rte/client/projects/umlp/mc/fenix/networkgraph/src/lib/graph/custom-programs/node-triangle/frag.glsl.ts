// See Sigma JS node-cirecle/frag.glsl.ts for more information

// language=GLSL
const SHADER_SOURCE = /*glsl*/ `
precision highp float;

varying vec4 v_color;

uniform float u_correctionRatio;

void main(void) {
  // No antialiasing needed because we can directly draw a triangle:
  gl_FragColor = v_color;
}
`;

export default SHADER_SOURCE;
