<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname")}
${cd4c.method("public List<${classname}> _doLoadAll${classname}WithIDs(List<Long> gemIDs)")}
  java.util.Set<Long> idSet = new java.util.HashSet<>();
  for(Long id : gemIDs) {
    idSet.add(id);
  }
  List<${classname}> result = new java.util.ArrayList<>();
  for (${classname} item : this._doGet${classname}List()) {
    if (idSet.contains(item.getGemId())) {
      result.add(item);
    }
  }
  return result;
