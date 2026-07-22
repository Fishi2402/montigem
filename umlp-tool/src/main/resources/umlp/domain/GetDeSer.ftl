<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "isAbstract")}
<#if isAbstract>
${cd4c.method("public abstract JsonDeSer<? extends GemIdentifiable> getDeSer()")}
<#else>
${cd4c.method("public JsonDeSer<? extends GemIdentifiable> getDeSer()")}

<#-- Wildcard return: subclass DeSers do not extend their superclass DeSers. -->
return new ${className}DeSer();
</#if>
