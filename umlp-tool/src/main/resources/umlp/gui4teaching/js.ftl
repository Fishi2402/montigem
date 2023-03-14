<#-- (c) https://github.com/MontiCore/monticore -->
// --------------- always the same (start) --------------- //
${signature("cdClasses")}

// TODO: replace with the end-point
const COMMAND_URL = 'https://example.com/api/command';

// send command to the server
async function sendCommand(command) {
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
    window.alert("There was an error. Tell you supervisor and take a look in the browser console");
    return Promise.reject(err);
  };
}

// load all objects of a class
async function loadClassData(className) {
  return sendCommand({
    "type": className + "_geAll"
  });
}

// --------------- always the same (end) --------------- //

// load all data
async function loadData() {
  const cd = {};
  cd.title = await loadTitle();

  const data = [];
  data.push({
    "class": "Person",
    "data": await loadPerson()
  });
  data.push({
    "class": "Book",
    "data": await loadBook()
  });

  cd.data = data;
  return cd;
}

async function loadDataGen() {
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

  <#-- dummy data for Person and Book -->
<#if c.getName() == "Person">
  // dummy data
  return [
    { "gemId" : 1, "name": "Alice", "email": "a@se.de", "password": "asd", "isManager": true, "rented": ["Harry", "Potter", "LotR"] },
    { "gemId" : 2, "name": "Bob", "email": "b@se.de", "password": "ewv43", "isManager": false, "rented": ["LotR"] },
    { "gemId" : 3, "name": "Charlie", "email": "c@se.de", "password": "d3", "isManager": true, "rented": [] }
  ];
</#if>

<#if c.getName() == "Book">
  // dummy data
  // actual code would send an async request, which will return a bit later
  // written with a timeout to see if it works
  return new Promise(resolve => setTimeout(
    () => resolve([
      { "gemId" : 4, "title": "Harry", "description": "lorem", "image": "x.png" },
      { "gemId" : 5, "title": "Potter", "description": "ipsum", "image": "y.png" },
      { "gemId" : 6, "title": "LotR", "description": "foo", "image": "z.png" }
    ]),
    500
  ));
</#if>
}

</#list>


