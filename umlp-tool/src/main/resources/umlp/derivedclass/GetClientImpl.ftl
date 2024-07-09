<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "typeParameter", "calc", "isList")}
<#if isList>
${cd4c.method("public ${typeParameter} get${name?cap_first}List()")}
<#else>
${cd4c.method("public ${typeParameter} get${name?cap_first}()")}
</#if>
return this.${calc.getName()}();
