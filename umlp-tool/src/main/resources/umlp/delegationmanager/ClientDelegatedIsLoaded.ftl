<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes", "typeToManagerMap")}
${cd4c.method("protected boolean _isLoaded${superType}(long gemId)")}

<#-- Objects of extending types register in their concrete type's manager, not in the
     super type's known-map. Without this override every _isLoaded check on the super
     manager misses and LoadAll<X>WithIDs reloads already-loaded objects on each call. -->
<#list extendingTypes as extendingType>
if (${typeToManagerMap[extendingType]}.isLoaded${extendingType}(gemId)) {
  return true;
}
</#list>
return super._isLoaded${superType}(gemId);
