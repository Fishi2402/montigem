<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("protected Optional<CommandResult<?>> fromJson(JsonObject jsonObject)")}

let fromSuper: CommandResult<?> = super.fromJson(jsonObject);
if(fromSuper != null){
  return fromSuper;
}

if(jsonObject.getBooleanMemberOpt("simpleResult")){
  let tp: string = jsonObject.getStringMember("typeParameter");
  let res: any = this.deserSingleValue(jsonObject.getMember("value"), tp);

  if(res == null){
    return null;
  }

  return new SimpleResult<>(jsonObject.getLongMember("commandId"), res, tp);
}

if(jsonObject.getBooleanMemberOpt("listResult")){
  let tp: string = jsonObject.getStringMember("typeParameter");
  let res: any[] = [];

  for(let o of jsonObject.getArrayMember("list")){
    res.add(deserSingleValue(o, tp));
  }

  if(res == null){
    return null;
  }

  return ListResult.of(jsonObject.getLongMember("commandId"), res, tp);
}

return null;

