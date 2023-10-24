<#-- (c) https://github.com/MontiCore/monticore -->
// --------------- always the same (start) --------------- //
${signature("cdClasses", "cdEnums", "generator", "attributesInHierarchy", "associationsInHierarchy")}

<#function java2TS type>
  <#if type == "Float"
    || type == "float"
    || type == "Double"
    || type == "double"
    || type == "Integer"
    || type == "int"
    || type == "Long"
    || type == "long"
  >
    <#return "Number">
  <#elseif type == "String">
    <#return "String">
  <#elseif type == "Boolean"
    || type == "boolean"
  >
    <#return "Boolean">
  <#elseif type?contains("List")
    || type?contains("Set")
    || type?contains("Array")
  >
    <#return "Array">
  <#else>
    <#return type>
  </#if>
</#function>

// Calc endpoints
let href = window.location.href;
if(!href.endsWith("/")){
  href = href + "/";
}
const COMMAND_URL = new URL(href + 'api/');
const GETALL_URL = new URL(href + 'getall/');
const GETGRAPH_URL = new URL(href + 'graph/');

async function sendCommand(command) {
  console.log("Send command: ", command);
  try {
    const response = await fetch(COMMAND_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(command)
    })
    // TODO: delete if you don't care what the result is
    console.log(response);
    const json = await response.json();
    console.log(json);
    return json;
  } catch (err) {
    console.error(err);
    // TODO: remove if you don't want that
    showError(err);
    return Promise.reject(err);
  };
}

function showError(err) {
  window.alert(err
   + " Take a look in the browser console."
   + " Tell you supervisor if you can't figure out what that means.");
}

<#list cdEnums as cdEnum>
const ${cdEnum.getName()} = {
  forwardMap: {
    <#list cdEnum.getCDEnumConstantList() as cdEnumConst>
    ${cdEnumConst.getName()}: ${cdEnumConst?index},
    </#list>
  },
  reverseMap: {
    <#list cdEnum.getCDEnumConstantList() as cdEnumConst>
    ${cdEnumConst?index}: "${cdEnumConst.getName()}",
    </#list>
  }
}
</#list>

const TYPE_INFO = {
<#list attributesInHierarchy as c, attrs>
  "${c.getName()}": {
    <#list attrs as attr>
        <#assign isArray = genService.isList(attr)>
        <#assign attrType = attr.getMCType().printType()>
        <#if isArray><#assign attrType = genService.getInnerType(attr.getMCType())></#if>
        <#assign isEnum = generator.isEnum(attrType, cdEnums)>
      "${attr.getName()}": {
      type: <#if isEnum>${java2TS(attrType)}<#else>"${java2TS(attrType)}"</#if>,
      typeName: "${java2TS(attrType)}",
      isEnum: ${isEnum?string("true", "false")},
      editable: ${(!attr.getModifier().isDerived())?c},
      isArray: ${(isArray)?c}
      },
    </#list>
    <#if associationsInHierarchy?keys?seq_contains(c)>
      <#assign seq_index = associationsInHierarchy?keys?seq_index_of(c) />
      <#assign assocs = associationsInHierarchy?values[seq_index]/>
      <#list assocs as assoc>
        <#assign assocType = assoc.getMCQualifiedType().printType()>
      "${assoc.getName()}": {
      type: "Number",
      typeName: "Number",
      isEnum: false,
      editable: ${(!assoc.getModifier().isDerived())?c},
      isArray: ${(genService.isList(assoc))?c}
      },
      </#list>
    </#if>
  },
</#list>
}

function convert(value, type, isArray = false) {
  if (isArray) {
    let array = [];
    array = value.trim().split(",");
    if (type === "Number"){
      for (const arrayElement of array) {
        if (isNaN(Number(arrayElement))) {
          throw typeError(value, type);
        }
      }
      array = array.map(Number);
    }
    return array;
  }
  if (type === "Number") {
    if (isNaN(Number(value))) {
      throw typeError(value, type);
    }
    return Number(value);
  }
  if (type === "Boolean") {
    if (value === "true") {
      return true;
    } else if (value === "false" || value === "") {
      return false;
    } else {
      throw typeError(value, type);
    }
  }
  if (type === "Array") {
    if (value) {
      return JSON.parse(value);
    } else {
      return [];
    }
  }
  return value;
}

function typeError(value, type) {
  return new Error("0x00002 Expected value of type " + type + ", but got " + value + ".");
}

function getType(className, attr) {
  if (TYPE_INFO[className][attr] != null) {
    return TYPE_INFO[className][attr].type;
  } else {
    return "Unknown";
  }
}

function getIsArray(className, attr) {
  if (TYPE_INFO[className][attr] != null) {
    return TYPE_INFO[className][attr].isArray;
  } else {
    return false;
  }
}

function getTypeName(className, attr) {
  if (TYPE_INFO[className][attr] != null) {
    return TYPE_INFO[className][attr].typeName;
  } else {
    return "Unknown";
  }
}

function isReadonly(className, attr) {
  return attr === "gemId" || !TYPE_INFO[className][attr] || !TYPE_INFO[className][attr].editable;
}

function isEnum(className, attr) {
  return attr !== "gemId" && TYPE_INFO[className][attr] && TYPE_INFO[className][attr].isEnum;
}

function uncapitalize(str1){
  return str1.charAt(0).toLowerCase() + str1.slice(1);
}

// load all objects of a class
async function loadClassData(className) {
  return sendCommand({
    "type": className + "_getAll"
  });
}

// --------------- always the same (end) --------------- //

// load all data
async function loadData() {
  const cd = {};
  cd.title = await loadTitle();
  cd.graph = await loadGraph();
  console.log(cd.graph);
  const data = [];
<#list cdClasses as c>
  data.push({
    "class": "${c.getName()}",
    "data": await load${c.getName()}()
  });
</#list>

  cd.data = data;
  return cd;
}


async function loadTitle() {
  // do not really need a command as this title never changes
  return "${ast.getCDDefinition().getName()}";
}

async function loadGraph() {
  let response = await fetch(GETGRAPH_URL);
  data = await response.json();
  return data;
}

<#list attributesInHierarchy as c, attrs>
async function load${c.getName()}() {
  let response = await fetch(GETALL_URL + "${c.getName()}");
  data = await response.json();
  if (data.length == 0) {
    data.push({
          "gemId": undefined,
      <#list attrs as attr>
          "${attr.getName()}": undefined,
      </#list>
      <#if associationsInHierarchy?keys?seq_contains(c)>
        <#assign seq_index = associationsInHierarchy?keys?seq_index_of(c) />
        <#assign assocs = associationsInHierarchy?values[seq_index]/>
          <#list assocs as assoc>
          "${assoc.getName()}": undefined,
          </#list>
      </#if>
    });
  }else{
    for(const d of data){
    <#list attrs as attr>
      <#if attr.modifier.isDerived()>
        try{
          const res = await sendCommand(
          {"commandType": "${ast.getCDDefinition().getName()?lower_case}.commands.${c.getName()}_calculate${attr.name?cap_first}",
          "gemId": d.gemId}
          );
          d.${attr.getName()} = res.value;
        }catch(error){
          d.${attr.getName()} = undefined;
          console.log("Can not load ${attr.name}", error);
        }
     </#if>
    </#list>
    <#if associationsInHierarchy?keys?seq_contains(c)>
      <#assign seq_index = associationsInHierarchy?keys?seq_index_of(c) />
      <#assign assocs = associationsInHierarchy?values[seq_index]/>
      <#list assocs as assoc>
        <#if assoc.modifier.isDerived()>
        try{
          const res = await sendCommand(
          {"commandType": "${ast.getCDDefinition().getName()?lower_case}.commands.${c.getName()}_calculate${assoc.name?cap_first}",
          "gemId": d.gemId}
          );
          d.${assoc.getName()} = res.value;
        }catch(error){
          d.${assoc.getName()} = undefined;
          console.log("Can not load ${assoc.name}", error);
        }
        </#if>
      </#list>
    </#if>
    }
  }
  return data;
}

</#list>


