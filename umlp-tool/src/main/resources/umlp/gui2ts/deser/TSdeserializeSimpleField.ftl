<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attr", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
<#assign fieldName=attr.getName()>
<#if !jsonStr?has_content && !useDeSer>
<#--    TODO Kommentar einfuegen, wie das zu fixen waere-->
    ${cd4c.method("abstract protected ${fieldType} deserialize${fieldName?cap_first} (any json)")}
<#else>
    ${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (any json)")}
    <#if isList>let result: any[] = [];
      if(!json["${fieldName}"]) {
      return result;
      }
      for(let e of json["${fieldName}"]) {
      <#if useDeSer>
      result.push(${objectMapperName}.deserialize(e, "${innerType}"));
      <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
<#--        TODO MB: ist das hier so richtig, oder sollte lieber innerType richtig berechnet werden? -->
      <#-- TODO AGe: find out if that's ok -->
      result.push(${innerType?keep_after_last(".")}[json["${fieldName}"] as keyof typeof ${innerType?keep_after_last(".")}]);
      <#else>
      result.push(e);
      </#if>
      }
      return result;
    <#elseif isOptional>
      if(json["${fieldName}"] !== undefined){
      <#if useDeSer>
      let value: any = json["${fieldName}"];
      return ${objectMapperName}.deserialize(value, "${innerType}");
      <#else>
        <#if genService.isInnerCDEnum(attr.getSymbol().getType())>
          return ${innerType?keep_after_last(".")}[json["${fieldName}"] as keyof typeof ${innerType?keep_after_last(".")}];
        <#else>
          return json["${fieldName}"];
        </#if>
      </#if>
      } else {
      return null;
      }
    <#else>
    <#-- TODO: default values -->
      <#if useDeSer>
      let value: any = json["${fieldName}"];
      return ${objectMapperName}.deserialize(value, "${fieldType}");
      <#else>
        <#if genService.isCDEnum(attr.getSymbol().getType())>
      return ${fieldType}[json["${fieldName}"] as keyof typeof ${attr.getSymbol().getType().getTypeInfo().getName()}];
        <#else>
      return json["${fieldName}"];
          </#if>
      </#if>
    </#if>

</#if>
