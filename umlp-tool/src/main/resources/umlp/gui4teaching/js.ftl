<#-- (c) https://github.com/MontiCore/monticore -->
// --------------- always the same (start) --------------- //
${signature("cdClasses")}

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

async function sendCommand(command) {
  console.log("Send command: " + command);
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
    return await response.json();
  } catch (err) {
    console.error('Error:', err);
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

const TYPE_INFO = {
<#list cdClasses as c>
  "${c.getName()}": {
  <#list c.getCDAttributeList() as attr>
    <#assign attrType = attr.getMCType().printType()>
    "${attr.getName()}": {
      type: "${java2TS(attrType)}",
      editable: ${(java2TS(attrType) != "Array" && !attr.getModifier().isDerived())?c},
    },
  </#list>
  },
</#list>
}

function convert(value, type) {
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
  return new Error("Expected value of type " + type + ", but got " + value + ".");
}

function getType(className, attr) {
  if (TYPE_INFO[className][attr] != null) {
    return TYPE_INFO[className][attr].type;
  } else {
    return "Unknown";
  }
}

function isReadonly(className, attr) {
  return attr === "gemId" || !TYPE_INFO[className][attr] || !TYPE_INFO[className][attr].editable;
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

<#list cdClasses as c>
async function load${c.getName()}() {
  // TODO: something like that
  // return loadClassData("${c.getName()}");
  let response = await fetch(GETALL_URL + "${c.getName()}");
  data = await response.json();
  if (data.length == 0) {
    data.push({
          "gemId": undefined,
      <#list c.getCDAttributeList() as attr>
          "${attr.getName()}": undefined,
      </#list>
    });
  }
  return data;

  <#-- dummy data for Person and Book -->
<#--<#if c.getName() == "Person">-->
<#--  // dummy data-->
<#--  return [-->
<#--    { "gemId" : 1, "name": "Alice", "email": "a@se.de", "password": "asd", "isManager": true, "rented": ["Harry", "Potter", "LotR"] },-->
<#--    { "gemId" : 2, "name": "Bob", "email": "b@se.de", "password": "ewv43", "isManager": false, "rented": ["LotR"] },-->
<#--    { "gemId" : 3, "name": "Charlie", "email": "c@se.de", "password": "d3", "isManager": true, "rented": [] }-->
<#--  ];-->
<#--<#elseif c.getName() == "Book">-->
<#--  // dummy data-->
<#--  // actual code would send an async request, which will return a bit later-->
<#--  // written with a timeout to see if it works-->
<#--  return new Promise(resolve => setTimeout(-->
<#--    () => resolve([-->
<#--      { "gemId" : 4, "title": "Harry", "description": "lorem", "image": "x.png" },-->
<#--      { "gemId" : 5, "title": "Potter", "description": "ipsum", "image": "y.png" },-->
<#--      { "gemId" : 6, "title": "LotR", "description": "foo", "image": "z.png" }-->
<#--    ]),-->
<#--    500-->
<#--  ));-->
<#--<#else>-->
<#--  return [-->
<#--    {  }-->
<#--  ];-->
<#--</#if>-->
}

</#list>


