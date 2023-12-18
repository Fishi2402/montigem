import { TestBed } from "@angular/core/testing";
import { GraphDataService } from "../data/graph-data.service";
import { GraphSettingsService } from "../services/graph-settings.service";
import { ProcessorServiceTemplate } from "./processor-service-template.service";
import { GraphNode } from "../types/graph.types";
import { SigmaGraphService } from "../data/sigma-graph.service";

describe("ProcessorServiceTemplate", () => {
  let service: ProcessorServiceTemplate<any, any, any>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SigmaGraphService, ProcessorServiceTemplate, GraphDataService, GraphSettingsService],
    });
    service = TestBed.inject(ProcessorServiceTemplate);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  describe("getAttribute", () => {
    it("should return the correct attribute value", () => {
      const node: GraphNode = {
        id: "node1",
        label: "Node 1",
        attributes: {
          color: "red",
        },
      };
      const attrValue = (ProcessorServiceTemplate as any).getAttribute(node, "color");
      expect(attrValue).toBe("red");
    });

    it("should return undefined for non-existing attributes", () => {
      const node: GraphNode = {
        id: "node1",
        label: "Node 1",
        attributes: {
          color: "#f00",
        },
      };
      const attrValue = (ProcessorServiceTemplate as any).getAttribute(node, "size");
      expect(attrValue).toBeUndefined();
    });
  });

  describe("toStringIfDefined", () => {
    it("should return string representation of value", () => {
      expect((ProcessorServiceTemplate as any).toStringIfDefined(123)).toBe("123");
      expect((ProcessorServiceTemplate as any).toStringIfDefined("abc")).toBe("abc");
    });

    it("should return undefined for null or undefined", () => {
      expect((ProcessorServiceTemplate as any).toStringIfDefined(null)).toBeUndefined();
      expect((ProcessorServiceTemplate as any).toStringIfDefined(undefined)).toBeUndefined();
    });
  });

  describe("isGroupUnknown", () => {
    it("should return true if group is unknown", () => {
      expect((service as any).isGroupUnknown((service as any).unknownGroupName)).toBe(true);
    });

    it("should return false if group is known", () => {
      expect((service as any).isGroupUnknown("Known")).toBe(false);
    });
  });
});
