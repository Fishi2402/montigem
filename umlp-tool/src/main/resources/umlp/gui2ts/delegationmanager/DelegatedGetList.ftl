<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superType", "extendingTypes", "typeToManagerMap")}
  ${cd4c.method("public Promise<GemList<${superType}>> _doGet${superType}List()")}

  return Promise.all([
    super._doGet${superType}List(),
  <#list extendingTypes as extendingType>
    ${typeToManagerMap[extendingType]}.get${extendingType}List()
  <#sep>
    ,
  </#sep>
  </#list>
  ]).then((arr : GemList<${superType}>[]) => {
    return arr.reduce((leftList, rightList) => {
      for (let element of rightList) {
        leftList.push(element)
      }
      return leftList;
    })
  })
