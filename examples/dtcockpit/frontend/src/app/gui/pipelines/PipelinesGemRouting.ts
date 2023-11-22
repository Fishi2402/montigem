/* (c) https://github.com/MontiCore/monticore */

import { Routes } from "@angular/router";
import { PipelineGroupStatsComponent } from "@src/gui/pipelines/PipelineGroupStatsComponent";
import { routes as routesTOP } from "@src/gui/pipelines/PipelinesGemRoutingTOP";

export const routes: Routes = [
  ...routesTOP,
  {
    path: "gui/pipelines/PipelineGroupStats/:pRoutingId",
    component: PipelineGroupStatsComponent
  }
]
