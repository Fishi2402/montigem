<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "toArray")}
<#assign mcType = type.printType()>

${cd4c.method("private ${mcType} get${name?cap_first}ForAngular()")}
<#if toArray>
return GemListUtil.toArraySync(this.get${name?cap_first}());
<#else>
return this.get${name?cap_first}();
</#if>

