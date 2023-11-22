<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr", "innerType")}
<#assign attrName=attr.getName()?cap_first>
${cd4c.method("public abstract List<${innerType}> select${attrName}(long gemId)")}
