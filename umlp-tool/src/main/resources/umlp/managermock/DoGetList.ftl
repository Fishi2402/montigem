<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected GemList<${name}> _doGet${name}List()")}
if(dummyProvider != null){
  while (${name?uncap_first}s.size() < dummyProvider.getMax${name}()){
    dummyProvider.invent${name}();
  }
}
return ${name?uncap_first}GemList;