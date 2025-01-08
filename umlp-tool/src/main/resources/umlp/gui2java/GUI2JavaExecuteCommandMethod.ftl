<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "params")}
<#assign mcType = type.printType()>
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("public ${mcType} ${name}(${paramString})")}

<#list params as param, symType>
  <#if genHelper.isDomainType(symType)>
  var arg__${param?index} = ${param.getName()}.getGemId();
  <#elseif genHelper.isDomainListType(symType)>
  UMLPArrayList<Long> arg__${param?index} = new UMLPArrayList<Long>();
  for(var i : ${param.getName()}) {
    arg__${param?index}.add(i.getGemId());
  }
  <#else>
  var arg__${param?index} = ${param.getName()};
  </#if>
</#list>

CommandResult<${mcType}> res = CommandManager.executeCommand(new ${name?cap_first}(
  <#list params as param, symType> arg__${param?index}<sep>, </#list>
  ));

if(res.isSuccessful() && res.isSimpleResult()) {
  return res.asSimpleResult().get();
}
return null;
