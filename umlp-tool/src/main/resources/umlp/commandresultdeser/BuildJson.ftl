<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("public void buildJson(CommandResult<?> result, JsonPrinter printer)")}

super.buildJson(result, printer);
if(result.isSimpleResult()){
  String tp = result.getTypeParameter();
  if(BaseClassObjectMapper.canDeSer(tp)){
    return;
  }

  if(result.asSimpleResult().isSuccessful()) {
    printer.beginObject("value");
    serSingleValue(result.asSimpleResult().get(), printer, tp);
    printer.endObject();
  }
}

if(result.isListResult()){
  String tp = result.getTypeParameter();
  if(BaseClassObjectMapper.canDeSer(tp)){
    return;
  }

  if(result.asListResult().isSuccessful()) {
    printer.beginArray("list");

    for(var o : result.asListResult().get()){
      printer.beginObject();
      serSingleValue(o, printer, tp);
      printer.endObject();
    }
    printer.endArray();
  }
}
