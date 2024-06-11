<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("id")}

if(this._route.snapshot.paramMap.get("${id}") != null) {
  this.${id} = +this._route.snapshot.paramMap.get("${id}");
}