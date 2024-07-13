<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name?cap_first} getInstance()")}

if(this_${name?uncap_first} == null) {
  this_${name?uncap_first} = new ${name?cap_first}();
}

return this._${name?uncap_first};
