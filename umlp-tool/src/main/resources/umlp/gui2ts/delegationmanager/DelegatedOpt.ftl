<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes" , "typeToManagerMap")}
${cd4c.method("public Promise<${superType}> _do${superType}Opt(long gemId)")}

  return Promise.all([
   super._do${superType}Opt(gemId),
  <#list extendingTypes as extendingType>
    ${typeToManagerMap[extendingType]}.${extendingType?uncap_first}Opt(gemId)
  <#sep>
    ,
  </#sep>
  </#list>
   ]).then((arr : ${superType}[]) => {
      return arr.find(e => (typeof (e) !== 'undefined' && e !== null))
    })
