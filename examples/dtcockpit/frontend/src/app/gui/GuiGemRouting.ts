import {Route} from "@angular/router";
import {routes as routesTOP} from "gui/GuiGemRoutingTOP"
import { routes as LoginmoduleRoutes } from "gui/loginmodule/LoginmoduleGemRouting";

export const routes: Route[] = [
  ...routesTOP,
  // ...LoginmoduleRoutes, // This route is now added by the LoginFrontend_Routes_Package.ftl
]
