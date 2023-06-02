<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("primitives", "objects", "primitiveBooleans", "wrappedBooleans", "className")}
${cd4c.method("public boolean equals(any o)")}

if (o instanceof ${className?cap_first}) {

  let ${className?uncap_first}: ${className?cap_first} = <${className?cap_first}>o;

  return this.getGemId() === ${className?uncap_first}.getGemId() &&
  <#list primitives as primitive>
    this._${primitive?uncap_first} === ${className?uncap_first}.get${primitive?cap_first}() &&
  </#list>
  <#list objects as object>
    (this._${object?uncap_first} === ${className?uncap_first}.get${object?cap_first}()
     || this._${object?uncap_first} != null
     && this._${object?uncap_first} === ${className?uncap_first}.get${object?cap_first}()) &&
  </#list>
  <#list primitiveBooleans as bool>
    this._${bool?uncap_first} === ${className?uncap_first}.is${bool?cap_first}() &&
  </#list>
  <#list wrappedBooleans as bool>
    this._${bool?uncap_first} === ${className?uncap_first}.is${bool?cap_first}() &&
  </#list>true;
}
return false;
