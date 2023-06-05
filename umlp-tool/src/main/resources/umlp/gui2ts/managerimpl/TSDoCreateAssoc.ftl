<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "managerName")}

${cd4c.method("public Promise<GemList<${type}>> _doCreate${name?cap_first}(long ownerGemID, int size, List<Long> initialIDs)")}
return ${managerName}.create${name?cap_first}Builder(ownerGemID, [])
  .then(list => {
    list._internal_add(0, initialIDs);
    return <GemList<${type}>> list.build();
  });