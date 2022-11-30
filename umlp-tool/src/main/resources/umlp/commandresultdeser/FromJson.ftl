<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("protected Optional<CommandResult<?>> fromJson(JsonObject jsonObject)")}

Optional<CommandResult<?>> fromSuper = super.fromJson(jsonObject);
if(fromSuper.isPresent()){
  return fromSuper;
}

if(jsonObject.getBooleanMemberOpt("simpleResult").orElse(false)){
  String tp = jsonObject.getStringMember("typeParameter");
  Object res = null;
  if(BaseClassObjectMapper.canDeSer(tp)){
    res = BaseClassObjectMapper.deserialize(jsonObject.getMember("value").toString(), tp).orElse(null);
  }else {
    switch (tp) {
<#list classList as className>
      case "${className}": {
        res = new ${className}DeSer().fromJson(jsonObject.getObjectMember("value")).orElse(null);
        break;
      }
</#list>
      default: {
        Log.error("Can not deserialize value of type " + tp);
      }
    }
  }

  if(res == null){
    return Optional.empty();
  }

  return Optional.of(new SimpleResult<>(jsonObject.getLongMember("commandId"), res, tp) );
}

return Optional.empty();

