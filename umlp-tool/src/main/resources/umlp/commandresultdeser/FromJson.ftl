<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("protected Optional<CommandResult<?>> fromJson(JsonObject jsonObject)")}

Optional<CommandResult<?>> fromSuper = super.fromJson(jsonObject);
if(fromSuper.isPresent()){
  return fromSuper;
}

if(jsonObject.getBooleanMemberOpt("simpleResult").orElse(false)){
  String tp = jsonObject.getStringMember("typeParameter");
  Object res = deserSingleValue(jsonObject.getMember("value"), tp);

  if(res == null){
    return Optional.empty();
  }

  return Optional.of(new SimpleResult<>(jsonObject.getLongMember("commandId"), res, tp) );
}

if(jsonObject.getBooleanMemberOpt("listResult").orElse(false)){
  String tp = jsonObject.getStringMember("typeParameter");
  List<Object> res = new ArrayList();

  for(var o : jsonObject.getArrayMember("list")){
    res.add(deserSingleValue(o, tp));
  }

  if(res == null){
    return Optional.empty();
  }

  return Optional.of(ListResult.of(jsonObject.getLongMember("commandId"), res, tp) );
}

return Optional.empty();

