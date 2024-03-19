<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "assocName")}
${cd4c.method("public ${type?cap_first} get${assocName?cap_first}ValidationResult()")}
if(!this.has${assocName?cap_first}ValidationResult()){
  this.${assocName}ValidationResult = new ${type?cap_first}();
}
return this.${assocName}ValidationResult;
