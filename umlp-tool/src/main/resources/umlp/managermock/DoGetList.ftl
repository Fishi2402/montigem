<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
List<Long> ids = new ArrayList<>();
<#list concreteSubclassesInclSelf as subclass>
  if(dummyProvider != null){
    while (${subclass.name?uncap_first}s.size() < dummyProvider.getMax${subclass.name}()){
      dummyProvider.invent${subclass.name}();
    }
  }

  ids.addAll(${subclass.name?uncap_first}GemList.getIDs());
</#list>
${name?cap_first}GemList list = new ${name?cap_first}GemList(ids);
return list.build().get();
