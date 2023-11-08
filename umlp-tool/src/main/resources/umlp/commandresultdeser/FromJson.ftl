<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("protected Optional<CommandResult<?>> fromJson(JsonObject jsonObject)")}

Optional<CommandResult<?>> fromSuper = super.fromJson(jsonObject);
if(fromSuper.isPresent()){
  return fromSuper;
}

boolean success = jsonObject.getBooleanMemberOpt("successful").orElse(false);

if(jsonObject.getBooleanMemberOpt("simpleResult").orElse(false)){
  String tp = jsonObject.getStringMember("typeParameter");
  if(success){
    Object res = deserSingleValue(jsonObject.getMember("value"), tp);
    if(res == null){
      return Optional.empty();
    }

    return Optional.of(new SimpleResult<>(jsonObject.getLongMember("commandId"), res, tp) );
  }else{
    return Optional.of(new SimpleResult<>(jsonObject.getLongMember("commandId"), LoadingState.FAILED_TO_LOAD, tp));
  }
}

if(jsonObject.getBooleanMemberOpt("listResult").orElse(false)){
  String tp = jsonObject.getStringMember("typeParameter");
  if(success){
    List<Object> res = new ArrayList();

    for(var o : jsonObject.getArrayMember("list")){
      res.add(deserSingleValue(o, tp));
    }

    if(res == null){
      return Optional.empty();
    }

    return Optional.of(ListResult.of(jsonObject.getLongMember("commandId"), res, tp) );
  }else {
    return Optional.of(ListResult.error(jsonObject.getLongMember("commandId"), tp));
  }
}

return Optional.empty();

