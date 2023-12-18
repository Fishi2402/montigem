import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterOutlet} from '@angular/router';
import {NetworkGraphComponent} from "./graph/graph.component";
import {GraphEdge, GraphNode} from "./graph/types/graph.types";
import {ComplexGraphLayouts, InitialGraphLayouts} from "./graph/types/graph-layout.types";
import {GraphSettings} from "./graph/types/graph-settings.types";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NetworkGraphComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  protected settings1: Partial<GraphSettings> = {
    defaultNodeSize: 25,
    defaultNodeColor: "#ff0000",
    draggableNodes: false
  }

  protected settings2: Partial<GraphSettings> = {
    forceEdgeLabels: true,
    forceNodeLabels: true,
    highlightNeighboringElementsOnNodeSelect: false,
    defaultNodeSize: 25,
    draggableNodes: true,
    defaultNodeColor: "#ff0000"
  }

  protected nodes: GraphNode[] = [
    {"id": 1, "label": "Node 1", "group": "Cluster A", size: 35},
    {"id": 2, "label": "Node 2", "group": "Cluster A", size: 35},
    {"id": 3, "label": "Node 3", "group": "Cluster A", size: 44},
    {"id": 4, "label": "Node 4", "group": "Cluster A", size: 55, hoverLabel: "another hover label!"},
    {"id": 5, "label": "Node 5", "group": "Cluster A", fixed: true, x: 0, y: 0, size: 54, tooltip: "custom Tooltip!"},
    {"id": 6, "label": "Node 6", "group": "Cluster B"},
    {"id": 7, "label": "Node 7", "group": "Cluster B"},
    {"id": 8, "label": "Node 8", "group": "Cluster B"},
    {"id": 9, "label": "Node 9", "group": "Cluster B"},
    {"id": 10, "label": "Node 10", "group": "Cluster B"},
    {"id": 11, "label": "Node 11", hover: "No Group Demo!"},
  ]

  protected edges: GraphEdge[] = [
    {"id": 1, "source": 1, "target": 2, "name": "Edge 1-2", size: 25, directed: true},
    {"id": 2, "source": 2, "target": 3, "name": "Edge 2-3"},
    {"id": 3, "source": 3, "target": 4, "name": "Edge 3-4"},
    {"id": 4, "source": 4, "target": 5, "name": "Edge 4-5"},
    {"id": 5, "source": 1, "target": 5, "name": "Edge 1-5"},
    {"id": 6, "source": 2, "target": 4, "name": "Edge 2-4"},
    {"id": 7, "source": 6, "target": 7, "name": "Edge 6-7"},
    {"id": 8, "source": 7, "target": 8, "name": "Edge 7-8"},
    {"id": 9, "source": 8, "target": 9, "name": "Edge 8-9"},
    {"id": 10, "source": 9, "target": 10, "name": "Edge 9-10"},
    {"id": 11, "source": 6, "target": 10, "name": "Edge 6-10"},
    {"id": 12, "source": 7, "target": 9, "name": "Edge 7-9"},
    {"id": 13, "source": 1, "target": 6, "name": "Edge 1-6"},
    {"id": 14, "source": 5, "target": 10, "name": "Edge 5-10"},
    {"id": 15, "source": 3, "target": 8, "name": "Edge 3-8"},
    {"id": 16, "source": 1, "target": 3, "name": "Edge 1-3"},
    {"id": 17, "source": 2, "target": 5, "name": "Edge 2-5"},
    {"id": 18, "source": 4, "target": 1, "name": "Edge 4-1"},
    {"id": 19, "source": 6, "target": 8, "name": "Edge 6-8"},
    {"id": 20, "source": 7, "target": 10, "name": "Edge 7-10"},
    {"id": 21, "source": 9, "target": 6, "name": "Edge 9-6"}
  ]

  protected readonly InitialGraphLayouts = InitialGraphLayouts;
  protected readonly ComplexGraphLayouts = ComplexGraphLayouts;
}
