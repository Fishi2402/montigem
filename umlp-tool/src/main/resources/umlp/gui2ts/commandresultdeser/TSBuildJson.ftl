<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("public void buildJson(CommandResult<any> result, any printer)")}

super.buildJson(result, printer);
if(result.isSimpleResult()){
  let tp: string = result.getTypeParameter();
  if(BaseClassObjectMapper.canDeSer(tp)){
    return;
  }

  printer["value"] = {};
  const tmp = printer;
  printer = printer["value"];
  this.serSingleValue(result.asSimpleResult(), printer, tp);
  printer = tmp;
}

if(result.isListResult()){
  let tp: string = result.getTypeParameter();
  if(BaseClassObjectMapper.canDeSer(tp)){
    return;
  }

  printer["list"] = [];
  const tmp = printer;
  printer = printer["list"];

  for(const o of result.asListResult().get()){
    this.serSingleValue(result.asSimpleResult(), printer, tp);
  }
  printer = tmp;

}
