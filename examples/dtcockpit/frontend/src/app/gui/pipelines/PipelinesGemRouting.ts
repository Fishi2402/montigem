/* (c) https://github.com/MontiCore/monticore */

import { Routes } from "@angular/router";
import { PipelineGroupStatsComponent } from "gui/pipelines/PipelineGroupStatsComponent";
import { routes as routesTOP } from "gui/pipelines/PipelinesGemRoutingTOP";

export const routes: Routes = [
  ...routesTOP,
  {
    path: "gui/pipelines/PipelineGroupStats/:pRoutingId",
    component: PipelineGroupStatsComponent
  }
]
