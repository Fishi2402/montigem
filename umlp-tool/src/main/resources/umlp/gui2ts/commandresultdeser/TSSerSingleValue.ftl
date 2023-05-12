${tc.signature("classList")}
${cd4c.method("protected void serSingleValue(Object o, JsonPrinter printer, String tp)")}

switch (tp){
// Java type system restrictions force us to use casts here
<#list classList as className>
  case "${className}":
    new ${className}DeSer().buildJson((${className})o, printer);
    break;
</#list>
  default:
    console.error("0x50007 Can not serialize value of type " + tp);
}