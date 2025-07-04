import { routes as routesTOP } from "test_update_of_builder/Test_update_of_builderGemRoutingTOP";
import { AnimalCreate } from "test_update_of_builder/AnimalCreate";
import { Route } from "@angular/router";


export const routes: Route[] = [
  ...routesTOP,
  {
    path: "AnimalCreate/:testUpdateOfBuilder_AnimalCreate_animal_RoutingId",
    component: AnimalCreate
  },
]
