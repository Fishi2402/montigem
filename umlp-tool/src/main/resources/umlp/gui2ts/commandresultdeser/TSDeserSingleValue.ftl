<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("protected Object deserSingleValue(any val, String tp)")}

let res: any = null;
if(BaseClassObjectMapper.canDeSer(tp)){
  let value: any = val;
  res = BaseClassObjectMapper.deserialize(typeof value === 'string' ? '"' + value + '"' : String(value), tp);
}else {
  switch (tp) {
<#list classList as className>
    case "${className}":
      res = new ${className}DeSer().fromJson(val);
      break;
</#list>
    default:
      console.error("0x50006 Can not deserialize value of type " + tp);
  }
}
return res;
