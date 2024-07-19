<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name?cap_first} getInstance()")}

if(_${name?uncap_first} == null) {
  _${name?uncap_first} = new ${name?cap_first}();
}

return _${name?uncap_first};
