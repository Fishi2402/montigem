
import { Route } from "@angular/router";
import { routes as routesTOP } from "@src/testUpdateOfBuilder/TestUpdateOfBuilderGemRoutingTOP";
import { AnimalCreate } from "@src/testUpdateOfBuilder/AnimalCreate";


export const routes: Route[] = [
  ...routesTOP,
  {
    path: "AnimalCreate/:testUpdateOfBuilder_AnimalCreate_animal_RoutingId",
    component: AnimalCreate
  },
]
