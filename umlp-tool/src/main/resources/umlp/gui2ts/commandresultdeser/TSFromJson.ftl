<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("public Optional<CommandResult<any>> fromJson(any jsonObject)")}

let fromSuper: CommandResult<any> = super.fromJson(jsonObject);
if(fromSuper != null){
  return fromSuper;
}

if(jsonObject["simpleResult"]){
  let tp: string = jsonObject["typeParameter"];
  let res: any = this.deserSingleValue(jsonObject["value"], tp);

  if(res == null){
    return null;
  }

  return new SimpleResult<any>().SimpleResult1(jsonObject["commandId"], res, tp);
}

if(jsonObject["listResult"]){
  let tp: string = jsonObject["typeParameter"];
  let res: any[] = [];

  for(let o of jsonObject["list"]){
    res.push(this.deserSingleValue(o, tp));
  }

  if(res == null){
    return null;
  }

  return ListResult.of(jsonObject["commandId"], res, tp);
}

return null;

