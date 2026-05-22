<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter", "toArray")}
<#assign mcType = type.printType()>


${cd4c.method("private ${mcType} ${getter}ForAngular()")}
<#if toArray>
return GemListUtil.toArraySync(this.${getter}());
<#else>
return this.${getter}();
</#if>
