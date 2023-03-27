<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("public void buildJson(CommandResult<?> result, JsonPrinter printer)")}

super.buildJson(result, printer);
if(result.isSimpleResult()){
  String tp = result.getTypeParameter();
  if(tp.startsWith("java.lang")){
    return;
  }

  printer.beginObject("value");
  serSingleValue(result.asSimpleResult().get(), printer, tp);
  printer.endObject();
}

if(result.isListResult()){
  String tp = result.getTypeParameter();
  if(tp.startsWith("java.lang")){
    return;
  }

  printer.beginArray("list");

  for(var o : result.asListResult().get()){
    serSingleValue(result.asSimpleResult().get(), printer, tp);
  }
  printer.endArray();
  printer.endObject();

}