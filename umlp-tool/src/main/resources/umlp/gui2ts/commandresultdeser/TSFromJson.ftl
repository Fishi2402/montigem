<#-- (c) https://github.com/MontiCore/monticore -->
${cd4c.method("public Optional<CommandResult<any>> fromJson(any jsonObject)")}

let fromSuper: CommandResult<any> = super.fromJson(jsonObject);
if(fromSuper != null){
  return fromSuper;
}

let success = jsonObject["successful"];
if(success === null || success === undefined){
  success = false;
}

if(jsonObject["simpleResult"]){
  let tp: string = jsonObject["typeParameter"];
  if(success){
    let res: any = this.deserSingleValue(jsonObject["value"], tp);

    if(res == null){
      return null;
    }

    return new SimpleResult<any>().SimpleResult1(jsonObject["commandId"], res, tp);
  }else{
    return new SimpleResult<any>().SimpleResult2(jsonObject["commandId"], LoadingState.FAILED_TO_LOAD, tp);
  }
}

if(jsonObject["listResult"]){
  let tp: string = jsonObject["typeParameter"];
  if(success){
    let res: any[] = [];

    for(let o of jsonObject["list"]){
      res.push(this.deserSingleValue(o, tp));
    }

    if(res == null){
      return null;
    }

    return ListResult.of(jsonObject["commandId"], res, tp);
  }else {
    return ListResult.error(jsonObject["commandId"], tp);
  }
}

if(jsonObject["validationResult"]) {
  let tp: string = jsonObject["typeParameter"];
  let res: DomainValidationResult = this.deserSingleValue(jsonObject["value"], tp);
  return ValidationResult.of(jsonObject["commandId"], res, tp);
}

return null;

