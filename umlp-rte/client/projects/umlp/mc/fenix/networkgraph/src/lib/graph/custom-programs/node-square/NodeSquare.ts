/**
 * Sigma.js WebGL Renderer Node Program
 * =====================================
 *
 * Simple program rendering nodes as discs, shaped by triangles using the
 * `gl.TRIANGLES` display mode. So, to draw one node, it will need to store
 * three times the center of the node, with the color, the size and an angle
 * indicating which "corner" of the triangle to draw.
 * @module
 */
import { Attributes } from "graphology-types";

import FRAGMENT_SHADER_SOURCE from "../node-triangle/frag.glsl";
import VERTEX_SHADER_SOURCE from "./vert.glsl";
import {NodeProgram, ProgramInfo} from "sigma/rendering";
import {NodeDisplayData, RenderParams} from "sigma/types";
import {floatColor} from "sigma/utils";

const { UNSIGNED_BYTE, FLOAT } = WebGLRenderingContext;

const UNIFORMS = ["u_sizeRatio", "u_correctionRatio", "u_matrix"] as const;

export default class NodeSquareProgram<
  N extends Attributes = Attributes,
  E extends Attributes = Attributes,
  G extends Attributes = Attributes,
> extends NodeProgram<(typeof UNIFORMS)[number]> {

  static readonly ANGLE_1 = (7 * Math.PI) / 4;
  static readonly ANGLE_2 = (1 * Math.PI) / 4;
  static readonly ANGLE_3 = (3 * Math.PI) / 4;
  static readonly ANGLE_4 = (3 * Math.PI) / 4;
  static readonly ANGLE_5 = (5 * Math.PI) / 4;
  static readonly ANGLE_6 = (7 * Math.PI) / 4;

  getDefinition() {
    return {
      VERTICES: 6,
      VERTEX_SHADER_SOURCE,
      FRAGMENT_SHADER_SOURCE,
      METHOD: WebGLRenderingContext.TRIANGLES,
      UNIFORMS,
      ATTRIBUTES: [
        { name: "a_position", size: 2, type: FLOAT },
        { name: "a_size", size: 1, type: FLOAT },
        { name: "a_color", size: 4, type: UNSIGNED_BYTE, normalized: true },
        { name: "a_id", size: 4, type: UNSIGNED_BYTE, normalized: true },
      ],
      CONSTANT_ATTRIBUTES: [{ name: "a_angle", size: 1, type: FLOAT }],
      CONSTANT_DATA: [[NodeSquareProgram.ANGLE_1], [NodeSquareProgram.ANGLE_2], [NodeSquareProgram.ANGLE_3],
        [NodeSquareProgram.ANGLE_4], [NodeSquareProgram.ANGLE_5], [NodeSquareProgram.ANGLE_6]],
    };
  }

  processVisibleItem(nodeIndex: number, startIndex: number, data: NodeDisplayData) {
    const array = this.array;
    const color = floatColor(data.color);

    array[startIndex++] = data.x;
    array[startIndex++] = data.y;
    array[startIndex++] = data.size;
    array[startIndex++] = color;
    array[startIndex++] = nodeIndex;
  }

  setUniforms(params: RenderParams, { gl, uniformLocations }: ProgramInfo): void {
    const { u_sizeRatio, u_correctionRatio, u_matrix } = uniformLocations;

    gl.uniform1f(u_correctionRatio, params.correctionRatio);
    gl.uniform1f(u_sizeRatio, params.sizeRatio);
    gl.uniformMatrix3fv(u_matrix, false, params.matrix);
  }
}
