<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName")}
${cd4c.method("private List<String> get${attrName?cap_first}(any jsonObject)")}
  let list = new Array<any>();
  jsonObject.getMember("${attrName}").getAsJsonArray().getValues().map(e => {
    let value = e.getAsJsonString().getValue();
    list.push(value);
  });
  return list;
