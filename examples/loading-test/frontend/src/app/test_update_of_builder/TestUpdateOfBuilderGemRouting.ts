
import { Route } from "@angular/router";
import { routes as routesTOP } from "testUpdateOfBuilder/TestUpdateOfBuilderGemRoutingTOP";
import { AnimalCreate } from "testUpdateOfBuilder/AnimalCreate";


export const routes: Route[] = [
  ...routesTOP,
  {
    path: "AnimalCreate/:testUpdateOfBuilder_AnimalCreate_animal_RoutingId",
    component: AnimalCreate
  },
]
