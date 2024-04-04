<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("public void Set${name?cap_first}(${type} val)")}
this._${name} = val;