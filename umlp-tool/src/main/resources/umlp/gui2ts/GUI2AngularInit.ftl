<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ids")}

${cd4c.method("public void ngOnInit()")}
this._subscriptions.push(this._route.paramMap.subscribe(params => {
  <#list ids as id>
    if (this._route.snapshot.paramMap.get("${id}") != null) {
      this.${id} = +this._route.snapshot.paramMap.get("${id}");
    }
  </#list>
  this.init();
}));