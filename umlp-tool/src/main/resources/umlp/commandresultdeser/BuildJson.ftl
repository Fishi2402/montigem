<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("public void buildJson(CommandResult<?> result, JsonPrinter printer)")}

super.buildJson(result, printer);
if(result.isSimpleResult()){
  String tp = result.getTypeParameter();
  if(tp.startsWith("java.lang")){
    return;
  }

  // TODO: create ListCommandResult and extract
  if("umlp.command.LongList".equals(tp)){
    printer.beginArray("value");
    for (Long l : ((LongList) result.asSimpleResult().get())) {
      printer.value(l);
    }
    printer.endArray();
    return;
  }

  printer.beginObject("value");
  // Java type system restrictions force us to use casts here
  switch (tp){
    <#list classList as className>
      case "${className}": {
        new ${className}DeSer().buildJson((${className})result.asSimpleResult().get(), printer);
        break;
      }
    </#list>
    default:{
      Log.error("Can not serialize value of type " + tp);
    }
  }
  printer.endObject();
}
