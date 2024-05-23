<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("manager")}
${cd4c.method("public void ngOnDestroy()")}
for (const sub of this._subscriptions) {
  sub.unsubscribe();
}
${tc.include("umlp.gui2ts.GUI2AngularDestroyBody")}