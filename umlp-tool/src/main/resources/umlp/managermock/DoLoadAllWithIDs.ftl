<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname")}
${cd4c.method("public List<${classname}> _doLoadAll${classname}WithIDs(List<Long> gemIDs)")}
  List<${classname}> result = new java.util.ArrayList<>();
  for (Long gemID : gemIDs) {
    java.util.Optional<${classname}> opt = this._do${classname}Opt(gemID);
    if (opt.isPresent()) {
      result.add(opt.get());
    }
  }
  return result;
