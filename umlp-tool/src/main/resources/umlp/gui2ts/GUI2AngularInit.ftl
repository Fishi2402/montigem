<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("pageName")}
${cd4c.method("public void ngOnInit()")}
${tc.include("umlp.gui2ts.GUI2AngularInitBodyInner")}
this._subscriptions.push(this._route.paramMap.subscribe(params => {
  ${tc.include("umlp.gui2ts.GUI2AngularInitBody")}
  Promise.resolve(this.init()).then(_ => {
    if ((<any> window).allVarsInited2 === undefined) {
      (<any> window).allVarsInited2 = {};
    }
    (<any> window).allVarsInited2["${pageName}"] = true;
  });
}));
