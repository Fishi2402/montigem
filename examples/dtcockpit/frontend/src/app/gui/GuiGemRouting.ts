import {Route} from "@angular/router";
import {routes as routesTOP} from "@src/gui/GuiGemRoutingTOP"
import { routes as LoginmoduleRoutes } from "@src/gui/loginmodule/LoginmoduleGemRouting";

export const routes: Route[] = [
  ...routesTOP,
  // ...LoginmoduleRoutes, // This route is now added by the LoginFrontend_Routes_Package.ftl
]
