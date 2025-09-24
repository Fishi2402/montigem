<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("primitives", "objects", "primitiveBooleans", "wrappedBooleans", "initializationTypeName")}
${cd4c.method("public boolean equals(any o)")}

if (o instanceof ${initializationTypeName?cap_first}) {

  let ${initializationTypeName?uncap_first}: ${initializationTypeName?cap_first} = <${initializationTypeName?cap_first}>o;

  return this.getGemId() === ${initializationTypeName?uncap_first}.getGemId() &&
  <#list primitives as primitive>
    this._${primitive?uncap_first} === ${initializationTypeName?uncap_first}.get${primitive?cap_first}() &&
  </#list>
  <#list objects as object>
    (this._${object?uncap_first} === ${initializationTypeName?uncap_first}._${object?uncap_first}
     || this._${object?uncap_first} != null
     && this._${object?uncap_first} === ${initializationTypeName?uncap_first}._${object?uncap_first}) &&
  </#list>
  <#list primitiveBooleans as bool>
    this._${bool?uncap_first} === ${initializationTypeName?uncap_first}.is${bool?cap_first}() &&
  </#list>
  <#list wrappedBooleans as bool>
    this._${bool?uncap_first} === ${initializationTypeName?uncap_first}.is${bool?cap_first}() &&
  </#list>true;
}
return false;
