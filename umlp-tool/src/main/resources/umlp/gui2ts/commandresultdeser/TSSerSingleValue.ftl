${tc.signature("classList")}
${cd4c.method("protected void serSingleValue(any o, any printer, String tp)")}

switch (tp){
// Java type system restrictions force us to use casts here
<#list classList as initializationTypeName>
  case "${initializationTypeName}":
    new ${initializationTypeName?keep_after_last(".")}DeSer().buildJson(<${initializationTypeName?keep_after_last(".")}>o, printer);
    break;
</#list>
  default:
    console.error("0x50007 Can not serialize value of type " + tp);
}
