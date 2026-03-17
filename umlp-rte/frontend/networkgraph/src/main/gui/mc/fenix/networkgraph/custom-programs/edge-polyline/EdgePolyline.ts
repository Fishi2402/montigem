/**
 * Sigma.js WebGL Renderer Edge Program — Polyline
 * ================================================
 * Renders edges with intermediate waypoints as a series of line segments
 * (polyline) using gl.LINES. Each segment is a pair of vertices.
 * Unused segment slots are degenerate (zero-length) and invisible.
 * @module
 */
import { Attributes } from "graphology-types";
import VERTEX_SHADER_SOURCE from "./vert.glsl";
import { EdgeProgram, ProgramInfo } from "sigma/rendering";
import { EdgeDisplayData, NodeDisplayData, RenderParams } from "sigma/types";
import { floatColor } from "sigma/utils";

const { FLOAT, UNSIGNED_BYTE } = WebGLRenderingContext;

/** Maximum number of intermediate waypoints per edge. */
const MAX_WAYPOINTS = 10;

const UNIFORMS = ["u_matrix"] as const;

// Simple solid-color fragment shader (same as EdgeLineProgram)
const FRAGMENT_SHADER_SOURCE = /*glsl*/ `
precision mediump float;

varying vec4 v_color;

void main(void) {
  gl_FragColor = v_color;
}
`;

export default class EdgePolylineProgram<
  N extends Attributes = Attributes,
  E extends Attributes = Attributes,
  G extends Attributes = Attributes,
> extends EdgeProgram<(typeof UNIFORMS)[number], N, E, G> {

  getDefinition() {
    return {
      // Each edge occupies (MAX_WAYPOINTS + 1) line segments → 2 vertices each
      VERTICES: (MAX_WAYPOINTS + 1) * 2,
      VERTEX_SHADER_SOURCE,
      FRAGMENT_SHADER_SOURCE,
      METHOD: WebGLRenderingContext.LINES,
      UNIFORMS,
      ATTRIBUTES: [
        { name: "a_position", size: 2, type: FLOAT },
        { name: "a_color", size: 4, type: UNSIGNED_BYTE, normalized: true },
        { name: "a_id", size: 4, type: UNSIGNED_BYTE, normalized: true },
      ],
    };
  }

  processVisibleItem(
    edgeIndex: number,
    startIndex: number,
    sourceData: NodeDisplayData,
    targetData: NodeDisplayData,
    data: EdgeDisplayData,
  ): void {
    const array = this.array;
    const color = floatColor(data.color);
    const rawWaypoints: { x: number; y: number }[] = (data as any).waypoints ?? [];
    const clampedWaypoints = rawWaypoints.slice(0, MAX_WAYPOINTS);

    // Waypoints are stored in raw graphology graph-space; normalize them to the
    // same stage-space that sourceData/targetData already use.
    const normalize = (this.renderer as any).normalizationFunction as (p: { x: number; y: number }) => { x: number; y: number };
    const normalizedWaypoints = clampedWaypoints.map(wp => normalize(wp));

    // Full path: source → waypoints → target
    const points: { x: number; y: number }[] = [
      { x: sourceData.x, y: sourceData.y },
      ...normalizedWaypoints,
      { x: targetData.x, y: targetData.y },
    ];

    const packVertex = (x: number, y: number) => {
      array[startIndex++] = x;
      array[startIndex++] = y;
      array[startIndex++] = color;
      array[startIndex++] = edgeIndex;
    };

    for (let i = 0; i < MAX_WAYPOINTS + 1; i++) {
      if (i < points.length - 1) {
        packVertex(points[i].x, points[i].y);
        packVertex(points[i + 1].x, points[i + 1].y);
      } else {
        // Degenerate segment — zero-length, invisible
        const last = points[points.length - 1];
        packVertex(last.x, last.y);
        packVertex(last.x, last.y);
      }
    }
  }

  setUniforms(params: RenderParams, { gl, uniformLocations }: ProgramInfo): void {
    gl.uniformMatrix3fv(uniformLocations["u_matrix"], false, params.matrix);
  }
}
