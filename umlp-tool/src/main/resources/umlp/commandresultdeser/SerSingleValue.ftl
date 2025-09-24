${tc.signature("classList")}
${cd4c.method("protected void serSingleValue(Object o, JsonPrinter printer, String tp)")}

switch (tp){
// Java type system restrictions force us to use casts here
<#list classList as initializationTypeName>
  case "${initializationTypeName}": {
    new ${initializationTypeName}DeSer().buildJson((${initializationTypeName})o, printer);
  break;
  }
</#list>
  default:{
    Log.error("Can not serialize value of type " + tp);
  }
}
