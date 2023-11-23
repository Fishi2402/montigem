<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("protected Object deserSingleValue(JsonElement val, String tp)")}

Object res = null;
if(BaseClassObjectMapper.canDeSer(tp)){
  JsonElement value = val;
  res = BaseClassObjectMapper.deserialize(value.isJsonString() ? '"' + value.getAsJsonString().getValue() + '"' : value.toString(), tp).orElse(null);
}else {
  switch (tp) {
<#list classList as className>
    case "${className}": {
      res = new ${className}DeSer().fromJson(val).orElse(null);
      break;
    }
</#list>
    default: {
      Log.error("Can not deserialize value of type " + tp);
    }
  }
}
return res;
