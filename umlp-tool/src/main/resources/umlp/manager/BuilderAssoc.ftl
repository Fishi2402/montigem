<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type")}
${cd4c.method("public static GemList<${type}> create${name?cap_first}Builder(long gemListId)")}

return getSingleton()._doCreate${name?cap_first}Builder(gemListId);





