import { Route } from "@angular/router";
import { FunctionDetail } from "./FunctionDetail";
import { FunctionOverview } from "./FunctionOverview";
import { Start } from "./Start";

export const routes: Route[] = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "gui/FunctionOverview"
  },
  {
    path: "gui/FunctionDetail/:gui_FunctionDetail_f_RoutingId/:gui_FunctionDetail_selectedFunctions_RoutingId/:gui_FunctionDetail_selectedChannels_RoutingId/:gui_FunctionDetail_selectedPorts_RoutingId",
    component: FunctionDetail
  },
  {
    path: "gui/FunctionDetail/:gui_FunctionDetail_f_RoutingId",
    component: FunctionDetail
  },
  {
    path: "gui/FunctionOverview",
    component: FunctionOverview
  },
  {
    path: "gui/Start",
    component: Start
  },
]

