<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName")}
${cd4c.method("private List<Long> get${attrName?cap_first}(any jsonObject)")}
  let list = new Array<any>();
  jsonObject.getMember("${attrName}").getAsJsonArray().getValues().map(e => {
    list.push(e);
  });
  return list;
