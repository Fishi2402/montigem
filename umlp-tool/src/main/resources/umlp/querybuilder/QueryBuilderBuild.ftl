<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tableName", "attributes", "optionalAttributes")}
${cd4c.method("public String build()")}
  return "SELECT "
      + printAttr("id")
    <#list attributes as attrName>
      + ", " + printAttr("${attrName}")
    </#list>
    <#list optionalAttributes as attrDbName>
      + ", " + printAttr("${attrDbName}")
    </#list>
      + " FROM "
      + printTable("${tableName}") + " "
      + printWhere();