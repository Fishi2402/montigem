import {NgModule} from '@angular/core';
import {CommonModule} from "@angular/common";
import {GraphDataService} from "./data/graph-data.service";
import {GraphManagerService} from "./data/graph-manager.service";
import {GraphProcessorService} from "./graph-processing/graph-processor.service";
import {GroupMappingService} from "./graph-processing/group-mapping.service";
import {EdgeProcessorService} from "./graph-processing/edge-processor.service";
import {NodeProcessorService} from "./graph-processing/node-processor.service";
import {SigmaGraphService} from "./data/sigma-graph.service";
import {GraphLayoutService} from "./services/graph-layout.service";
import {GraphMetricsService} from "./services/graph-metrics.service";
import {GraphSettingsService} from "./services/graph-settings.service";
import {GemGraphComponent} from "./graph.component";
import {ControlButtonsComponent} from "./control-buttons/control-buttons.component";
import {FilterGroupComponent} from "./filter-group/filter-group.component";
import {MetricInformationComponent} from "./metric-information/metric-information.component";
import {SearchNodeComponent} from "./search-node/search-node.component";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";
import {SpeedDialModule} from "primeng/speeddial";
import {CapitalizeFirstLetterPipe} from "../shared/pipes/capitalize-first-letter.pipe";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {MultiSelectModule} from "primeng/multiselect";
import {OverlayPanelModule} from "primeng/overlaypanel";
import {FormatValuePipe} from "../shared/pipes/value-formatter.pipe";
import {ObjectKeysPipePipe} from "../shared/pipes/object-keys.pipe";
import {NodeKeyFormatterPipe} from "./search-node/node-key-formatter.pipe";
import {ListboxModule} from "primeng/listbox";

@NgModule(
  {
    "declarations": [GemGraphComponent, ControlButtonsComponent, FilterGroupComponent, MetricInformationComponent, SearchNodeComponent],
    "exports": [GemGraphComponent],
    imports: [
      CommonModule,
      // From Control Buttons
      ButtonModule,
      TooltipModule,
      SpeedDialModule,
      // From Filter-Group
      MultiSelectModule,
      FormsModule,
      DropdownModule,
      CapitalizeFirstLetterPipe,
      // From Metrics
      OverlayPanelModule,
      FormatValuePipe,
      ObjectKeysPipePipe,
      // From Search Node
      NodeKeyFormatterPipe,
      ListboxModule,
    ],
    providers: [
      GraphDataService,
      GraphManagerService,
      GraphProcessorService,
      GroupMappingService,
      EdgeProcessorService,
      NodeProcessorService,
      SigmaGraphService,
      GraphLayoutService,
      GraphMetricsService,
      GraphSettingsService,
    ]
  })
export class GemgraphModule {

}
