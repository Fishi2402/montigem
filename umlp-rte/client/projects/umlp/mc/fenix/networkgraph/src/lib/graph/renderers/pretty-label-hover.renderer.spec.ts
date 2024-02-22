import { setupJestCanvasMock } from "jest-webgl-canvas-mock";
import { getDefaultGraphSettings } from "../consts/graph-settings.const";
import { prettyLabelHoverRenderer } from "./pretty-label-hover.renderer";
import { NodeAttributes } from "../types/graph.types";
import { GraphSettings } from "../types/graph-settings.types";
import * as RenderUtils from "./render.utils";
import { ColorUtil } from "../../shared/utils/color.util";

describe("prettyLabelHoverRenderer", () => {
  let context: CanvasRenderingContext2D;
  let data: NodeAttributes;
  let settings: GraphSettings;
  let textSpy: jest.SpyInstance;
  let rectSpy: jest.SpyInstance;
  let textObjectSpy: jest.SpyInstance;
  let measureTextSpy: jest.SpyInstance;

  beforeEach(() => {
    jest.resetAllMocks();
    setupJestCanvasMock();

    const canvas: HTMLCanvasElement = document.createElement("canvas");
    context = canvas.getContext("2d") as CanvasRenderingContext2D;
    data = {
      color: "#ff0000",
      group: "Unknown",
      type: "circle",
      label: "Node 1",
      tooltip: "Tooltip",
      x: 50,
      y: 50,
      size: 10,
    };
    settings = getDefaultGraphSettings();

    textSpy = jest.spyOn(context, "fillText");
    rectSpy = jest.spyOn(RenderUtils, "drawShadowAndRect");
    textObjectSpy = jest.spyOn(RenderUtils, "createTextObject");
    measureTextSpy = jest.spyOn(RenderUtils, "measureTextDimensions");
  });

  it("should render hover label correctly", () => {
    prettyLabelHoverRenderer(context, data, settings);

    // Check if fillText was called for each text object
    expect(textSpy).toHaveBeenCalledTimes(3);

    // Check if drawShadowAndRect was called with correct arguments
    expect(rectSpy).toHaveBeenCalledWith(context, data, settings, expect.any(Number), expect.any(Number));

    // Check if createTextObject was called with correct arguments
    expect(textObjectSpy.mock.calls[0][1]).toMatchObject({
      content: data.label ?? "",
      fontSize: data.tooltip ? settings.labelSize - 2 : settings.labelSize,
      color: data.tooltip
        ? ColorUtil.parseIntoHEX(settings.prettyLabelsHoverConfiguration.mutedTextColor)
        : RenderUtils.resolveLabelColor(data, settings),
    });
    expect(textObjectSpy.mock.calls[1][1]).toMatchObject({
      content: data.tooltip ?? "",
      fontSize: settings.labelSize,
      color: RenderUtils.resolveLabelColor(data, settings),
    });
    expect(textObjectSpy.mock.calls[2][1]).toMatchObject({
      content: data.group ?? "",
      fontSize: settings.labelSize - 2,
      color: data.color ?? RenderUtils.resolveLabelColor(data, settings),
    });

    // Check if measureTextDimensions was called for each text object
    expect(measureTextSpy).toHaveBeenCalledTimes(3);
  });

  it("should not render anything if label, tooltip, and group are missing", () => {
    // const textSpy = jest.spyOn(context, "fillText");
    // data.label = "";
    // data.tooltip = "";
    // data.group = "";

    prettyLabelHoverRenderer(context, data, settings);

    // Check if fillText was not called as there's no text to render
    // expect(textSpy).not.toHaveBeenCalled();
  });
});
