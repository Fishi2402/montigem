<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("pageName")}
${cd4c.method("public void ngOnDestroy()")}
for (const sub of this._subscriptions) {
  sub.unsubscribe();
}
this.destroy();
if ((<any> window).allVarsInited2)
  (<any> window).allVarsInited2["${pageName}"] = false;
${tc.include("umlp.gui2ts.GUI2AngularDestroyBody")}
