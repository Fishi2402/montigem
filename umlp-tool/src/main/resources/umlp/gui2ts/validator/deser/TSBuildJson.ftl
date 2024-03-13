<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attributes", "assocTypes", "assocs", "listAssocTypes", "listAssocs")}
${cd4c.method("public void buildJson(${className}ValidationResult obj, any printer)")}
<#list attributes as attr>
  printer["${attr}Messages"] = [];
  const tmpA${attr_index} = printer;
  printer = printer["${attr}Messages"];
  for(let msg of obj.get${attr?cap_first}ValidationMessages()) {
    const a_tmp = printer;
    printer = {};
    a_tmp.push(printer);
    new ValidationMessageDeSer().buildJson(msg, printer);
    printer = a_tmp;
  }
  printer = tmpA${attr_index};
</#list>
<#list assocTypes as assocType>
  if(obj.has${assocs[assocType_index]?cap_first}ValidationResult()) {
    printer["${assocs[assocType_index]}ValidationResult"] = new ${assocType}ValidationResultDeSer().serialize(obj.get${assocs[assocType_index]?cap_first}ValidationResult());
  }
</#list>
<#list listAssocTypes as listAssocType>
  printer["${listAssocType}ValidationResults"] = [];
  const tmpB${listAssocType_index} = printer;
  printer = printer["${listAssocType}ValidationResults"];
  for(let object of obj.get${listAssocs[listAssocType_index]?cap_first}ValidationResults()) {
    const a_tmp = printer;
    printer = {};
    a_tmp.push(printer);
    new ${listAssocType}ValidationResultDeSer().buildJson(object, printer);
    printer = a_tmp;
  }
  printer = tmpB${listAssocType_index};
</#list>
