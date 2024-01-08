/* (c) https://github.com/MontiCore/monticore */
// always imported
import { Route } from "@angular/router";
// imports of pages in the package
import { WeatherView } from "./WeatherView";
import { routes as routesTOP} from "@src/gui/GuiGemRoutingTOP";
export const routes: Route[] = [
    ... routesTOP,

]
