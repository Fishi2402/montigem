<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("public void ngOnInit()")}
this._subscriptions.push(this._route.paramMap.subscribe(params => {
  this.init();
}));