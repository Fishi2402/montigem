import { setupJestCanvasMock } from "jest-webgl-canvas-mock";
import * as RenderUtils from "./render.utils";
import { NodeAttributes } from "../types/graph.types";
import { GraphSettings } from "../types/graph-settings.types";
import { TextObject } from "../types/custom-renderer.types";
import { ColorUtil } from "../../shared/utils/color.util";
import { getDefaultGraphSettings } from "../consts/graph-settings.const";
import { Settings } from "sigma/settings";

describe("Rendering Utilities", () => {
  let context: CanvasRenderingContext2D;
  let data: NodeAttributes;
  let settings: GraphSettings;
  let textObject: TextObject;

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
    textObject = RenderUtils.createTextObject(settings as Settings, {
      content: "Test",
      fontSize: 14,
      color: "#000000",
    });
  });

  it("should draw a rounded rectangle correctly", () => {
    const rectSpy = jest.spyOn(context, "fill");
    RenderUtils.drawRoundedRect(context, 10, 10, 100, 50, 10);
    expect(rectSpy).toHaveBeenCalled();
  });

  it("should measure text dimensions correctly", () => {
    const spy = jest.spyOn(context, "measureText");
    RenderUtils.measureTextDimensions(context, textObject);
    expect(spy).toHaveBeenCalled();
  });

  it("should create a TextObject correctly", () => {
    expect(textObject).toMatchObject({
      baseLine: "bottom",
      width: 0,
      height: 0,
      font: settings.labelFont,
      weight: settings.labelWeight,
      content: "Test",
      fontSize: 14,
      color: "#000000",
    });
  });

  it("should resolve label color correctly", () => {
    const resolvedColor = RenderUtils.resolveLabelColor(data, settings);
    expect(resolvedColor).toBe(ColorUtil.parseIntoHEX(settings.labelColor.color));
  });

  it("should draw shadow and rectangle correctly", () => {
    const rectSpy = jest.spyOn(context, "fill");
    RenderUtils.drawShadowAndRect(context, data, settings, 100, 50);
    expect(rectSpy).toHaveBeenCalled();
  });
});
