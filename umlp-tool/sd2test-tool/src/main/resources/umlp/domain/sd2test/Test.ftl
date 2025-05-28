<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("objects", "statements")}
${cd4c.method("void testSequence()")}
  //create instances needed for test
<#list objects as obj>
  var ${obj} = create${obj?cap_first}();
</#list>

  //assert sequence matches
<#list statements as stmt>
  ${stmt};
</#list>
