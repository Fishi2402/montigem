<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("className", "attr", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
<#assign fieldName=attr.getName()>
<#if !jsonStr?has_content && !useDeSer>
<#--    TODO Kommentar einfuegen, wie das zu fixen waere-->
    ${cd4c.method("abstract protected void serialize" + fieldName?cap_first + "(" + className + " obj, any printer)")}
<#else>
    ${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, any printer)")}

    <#if isList>
      printer["${fieldName}"] = [];
      <#if useDeSer>
      for (const e of obj.get${fieldName?cap_first}()) {
        printer["${fieldName}"].push(${objectMapperName}.serialize(e, "${innerType}"));
      }
      <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
      for (let e of obj.get${fieldName?cap_first}()) {
        printer["${fieldName}"].push(${attr.getSymbol().getType().getArgument(0).print()}[e]);
      }
      <#else>
      for (let e of obj.get${fieldName?cap_first}()) {
        printer["${fieldName}"].push(e);
      }
      </#if>
    <#elseif isOptional>
      if (obj.get${fieldName?cap_first}() != null) {
        <#if useDeSer>
        printer["${fieldName}"] = ${objectMapperName}.serialize(obj.get${fieldName?cap_first}(), "${innerType}");
        <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
        printer["${fieldName}"] = ${attr.getSymbol().getType().getArgument(0).print()}[obj.get${fieldName?cap_first}()];
        <#else>
        printer["${fieldName}"] = obj.get${fieldName?cap_first}();
        </#if>
      }
    <#else>
      <#if useDeSer>
      printer["${fieldName}"] = ${objectMapperName}.serialize(obj.${genService.getGetterName(fieldName, attr.getSymbol().getType())}(), "${fieldType}");
      <#elseif genService.isCDEnum(attr.getSymbol().getType())>
      printer["${fieldName}"] = ${fieldType}[obj.${genService.getGetterName(fieldName, attr.getSymbol().getType())}()];
      <#else>
      printer["${fieldName}"] = obj.${genService.getGetterName(fieldName, attr.getSymbol().getType())}();
      </#if>
    </#if>
</#if>
