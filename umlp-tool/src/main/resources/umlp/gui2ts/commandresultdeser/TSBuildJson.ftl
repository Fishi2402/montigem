<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("public void buildJson(CommandResult<?> result, JsonPrinter printer)")}

super.buildJson(result, printer);
if(result.isSimpleResult()){
  let tp: string = result.getTypeParameter();
  if(BaseClassObjectMapper.canDeSer(tp)){
    return;
  }

  printer.beginObject("value");
  this.serSingleValue(result.asSimpleResult(), printer, tp);
  printer.endObject();
}

if(result.isListResult()){
  let tp: string = result.getTypeParameter();
  if(BaseClassObjectMapper.canDeSer(tp)){
    return;
  }

  printer.beginArray("list");

  for(const o of result.asListResult()){
    this.serSingleValue(result.asSimpleResult(), printer, tp);
  }
  printer.endArray();
  printer.endObject();

}
