<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "reportError")}
${cd4c.method("protected boolean check${name?cap_first}()")}

let res: boolean = true;

<#if reportError>
if(this._${name} == null){
  res = false;
  ${domain}ErrorCollector.report${name?cap_first}Invalid(<${domain}> <any> this);
} else {
  if(this._${name}.size()<1){
    res = false;
    ${domain}ErrorCollector.report${name?cap_first}Invalid(<${domain}> <any> this);
  }
}
</#if>

return res;
