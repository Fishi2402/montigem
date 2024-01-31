<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "builder", "isList")}
${cd4c.method("public ${builder} ${name}(${type} ${name})")}

<#if isList>
this._${name} = new ArrayList(${name});
<#else>
this._${name} = ${name};
</#if>
${name}Present = true;
return this;
