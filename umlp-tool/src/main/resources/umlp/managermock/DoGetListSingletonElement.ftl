<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
List<Long> ids = new ArrayList<>();
if(dummyProvider != null){
  if (${name?uncap_first}Instance.isEmpty()){
    dummyProvider.invent${name}();
  }
}

ids.addAll(${name?uncap_first}GemList.getIDs());
${name?cap_first}GemList list = new ${name?cap_first}GemList(ids);
return list.build().get();
