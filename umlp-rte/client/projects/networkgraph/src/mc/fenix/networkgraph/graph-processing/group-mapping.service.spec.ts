import { TestBed } from "@angular/core/testing";
import { GroupMappingService } from "./group-mapping.service";
import { ProcessorServiceTemplate } from "./processor-service-template.service";
import { GraphSettingsService } from "../services/graph-settings.service";
import { EdgeProcessorService } from "./edge-processor.service";
import { GraphDataService } from "../data/graph-data.service";
import { SigmaGraphService } from "../data/sigma-graph.service";

describe("GroupMappingService", () => {
  let service: GroupMappingService<any, any, any>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SigmaGraphService,
        GraphDataService,
        GroupMappingService,
        ProcessorServiceTemplate,
        GraphSettingsService,
        EdgeProcessorService,
      ],
    });
    service = TestBed.inject(GroupMappingService);
    const settings = TestBed.inject(GraphSettingsService);
    settings.updateSetting("colorMap", ["#ff0000", "#00ff00", "#0000ff"]);
    service.addNodeGroup("node1", "group1");
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  describe("reset", () => {
    it("should reset the group-color and node-group mappings", () => {
      service.reset();
      // Assuming that service exposes groupColorMapping and nodeGroupMapping for testing.
      expect((service as any).groupColorMapping.size).toBe(0);
      expect((service as any).nodeGroupMapping.size).toBe(0);
    });
  });

  describe("addNodeGroup", () => {
    it("should associate a node key with a group", () => {
      expect((service as any).nodeGroupMapping.get("node1")).toBe("group1");
    });
  });

  describe("getNodeGroup", () => {
    it("should determine the group of a node", () => {
      const node = { attributes: { group: "group1" } };
      const group = service.getNodeGroup(node);
      expect(group).toBe("group1");
    });
  });

  describe("getEdgeGroup", () => {
    it("should determine the group of an edge", () => {
      const edge = { source: "node1", target: "node2" };
      service.addNodeGroup("node1", "group1");
      const group = service.getEdgeGroup(edge);
      expect(group).toBe("group1");
    });
  });

  describe("assignGroupColors", () => {
    it("should assign colors to groups based on nodes and settings", () => {
      const nodes = [{ attributes: { group: "group1" } }, { attributes: { group: "group2" } }];
      service.assignGroupColors(nodes);
      // Assuming groupColorMapping is exposed for testing, or you have a method to check its contents
      expect((service as any).groupColorMapping.get("group1")).toBe("#ff0000");
      expect((service as any).groupColorMapping.get("group2")).toBe("#00ff00");
    });
  });

  describe("getColorForGroup", () => {
    it("should retrieve the color for a specific group", () => {
      const nodes = [{ attributes: { group: "group1" } }, { attributes: { group: "group2" } }];
      service.assignGroupColors(nodes);

      const color = service.getColorForGroup("group1", "#ffffff");
      expect(color).toBe("#ff0000");
    });
  });
});
