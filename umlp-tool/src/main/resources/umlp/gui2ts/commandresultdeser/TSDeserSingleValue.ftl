<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("protected any deserSingleValue(any val, String tp)")}

let res: any = null;
if(BaseClassObjectMapper.canDeSer(tp)){
  let value: any = val;
  res = BaseClassObjectMapper.deserialize(value, tp);
}else {
  switch (tp) {
<#list classList as initializationTypeName>
    case "${initializationTypeName}":
      res = new ${initializationTypeName?keep_after_last(".")}DeSer().fromJson(val);
      break;
</#list>
    default:
      console.error("0x50006 Can not deserialize value of type " + tp);
  }
}
return res;
