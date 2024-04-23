<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "setter")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("public void ${setter}(${type} val)")}
this.${name} = val;