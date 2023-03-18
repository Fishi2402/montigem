<#-- (c) https://github.com/MontiCore/monticore -->
<!DOCTYPE html>
<html>

<head>
  <title>${ast.getCDDefinition().getName()}</title>
  <!-- styles -->
  <style>
    * {
      box-sizing: border-box;
    }

    .info-card {
      padding: 20px;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    }

    .info-card h2 {
      font-size: 24px;
      margin-top: 0;
    }

    .info-card p {
      font-size: 18px;
      line-height: 1.5;
    }

    .title {
      margin: 20px;
      display: flex;
      justify-content: center;
    }

    .card {
      background-color: #fff;
      border-radius: 4px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      margin: 20px;
      padding: 20px;
    }

    .card-header {
      font-size: 24px;
      font-weight: bold;
      display: flex;
      align-items: center;
    }

    .card-button {
      background-color: inherit;
      border: none;
      color: #333;
      cursor: pointer;
      padding: 10px;
      margin-right: 10px;
    }

    .arrow-right {
      width: 0;
      height: 0;
      border-top: 9px solid transparent;
      border-bottom: 9px solid transparent;
      border-left: 9px solid black;
    }

    .arrow-down {
      width: 0;
      height: 0;
      border-left: 9px solid transparent;
      border-right: 9px solid transparent;
      border-top: 9px solid black;
      padding-top: 4px
    }

    table {
      border-collapse: collapse;
      width: 100%;
      table-layout: fixed;
    }

    th,
    td {
      padding: 8px;
      text-align: left;
      border-bottom: 1px solid #ddd;
    }

    th {
      background-color: #f2f2f2;
    }

    .first-col {
      width: 40px;
      font-size: 22px;
      text-align: center;
    }

    .first-col:hover {
      cursor: pointer;
    }

    .card-body.hidden {
      display: none;
    }

    .card-body {
      display: block;
      margin-top: 20px;
    }
  </style>
  <!--
    import data loading functions: file can be directly inserted into next <script>
    tag instead
  -->
  <script type='text/javascript' src='./index.js'></script>
  <script>
    // Update of CRUD
    async function updateObj(cell, className, attr, gemId) {
      // Get the input value from the table cell
      let value = convert(cell.textContent.trim());

      // TODO: form proper command, maybe delete next comment and log line
      // example:
      // {
      //   type: Person_setName,
      //   gemId: 4,
      //   data: Bob
      // }
      const command = {
        "commandType": "${ast.getCDDefinition().getName()?lower_case}.commands." + className + "_set" + capitalizeFirstLetter(attr),
        "gemId": gemId
      };
      command[attr] = value;
      // console.log(command);

      // send request to the server;
      // don't have to wait for result, the cell is already modified
      sendCommand(command);
    }

    // Create of CRUD
    async function createObj(obj, className) {
      // TODO: form proper command, maybe delete next comment and log line
      // example:
      // {
      //   type: Person_create
      //   data: { name: Bob, age: 12 }
      // }
      const freeGemId = {
          "commandType": "${ast.getCDDefinition().getName()?lower_case}.requests.GetFreeGemIdRequest"
      }
      obj["gemId"] = convert((await sendCommand(freeGemId))["value"]);
      const command = {
        "commandType": "${ast.getCDDefinition().getName()?lower_case}.commands." + className + "_create"
      };
      command[uncapitalize(className)] = obj;
      console.log(command);

      // send request to the server
      await sendCommand(command);
      // TODO: remove if you don't want to automatically reload the page
      window.location.reload();
    }

    // Delete of CRUD
    async function deleteObj(className, gemId) {
      // TODO: form proper command, maybe delete next comment and log line
      // example:
      // {
      //   type: Person_delete,
      //   gemId: 4
      // }
      const command = {
        "commandType": "${ast.getCDDefinition().getName()?lower_case}.commands." + className + "_delete",
        "gemId": gemId
      };
      console.log(command);

      // send request to the server
      await sendCommand(command);
      // TODO: remove if you don't want to automatically reload the page
      window.location.reload();
    }

    // update object when user enters something in a @cell of type @className;
    // the update happens 1000 msec (1 sec) after user stops typing
    const timeoutIds = new WeakMap();
    function handleInput(cell, className, attr, gemId) {
      // refresh timer if user still types
      if (timeoutIds.has(cell)) {
        clearTimeout(timeoutIds.get(cell));
      }
      // set timer to 1 sec for update action
      timeoutIds.set(cell, setTimeout(function () {
        updateObj(cell, className, attr, gemId);
      }, 1000));
    }

    function addBodyCell(row, onInput) {
      const td = document.createElement("td");
      // editable cells
      td.contentEditable = 'true';
      // send command 1 second after user stops typing
      if (onInput) {
        td.addEventListener("input", onInput);
      }
      row.appendChild(td);
      return td;
    }

    function capitalizeFirstLetter(str) {
      return str.charAt(0).toUpperCase() + str.slice(1);
    }

    window.onload = loadPage;

    function loadPage() {
      // Read of CRUD
      // load data, then create page. Assuming that we request from server,
      // hence we use async function. loadData is in index.js
      loadData().then(cd => {
        // received data from server
        const dataSet = cd.data;
        const pageTitle = cd.title;

        // always true, unless old or unconventional browser is used
        if ("content" in document.createElement("template")) {

          const body = document.querySelector("body");
          // use templates to create cards, tables, etc. at runtime, when data is loaded
          const titleTpl = document.querySelector("#titletpl");
          const cardTpl = document.querySelector("#cardtpl");
          const tableRowTpl = document.querySelector("#tablerowtpl");
          const cellAddTpl = document.querySelector("#celladdtpl");
          const cellDeleteTpl = document.querySelector("#celldeletetpl");

          // main title ( SELib )
          const titleClone = titleTpl.content.cloneNode(true);
          let h1 = titleClone.querySelector("h1");
          let titleText = document.createTextNode(pageTitle);
          h1.appendChild(titleText);

          body.appendChild(titleClone);

          // create a card for each class
          for (const entry of dataSet) {
            const className = entry.class;
            const cardClone = cardTpl.content.cloneNode(true);
            // button to blend table in/out
            let button = cardClone.querySelector("button.card-button");
            let buttonArrow = cardClone.querySelector("button.card-button>div");
            let cardBody = cardClone.querySelector("div.card-body");
            button.addEventListener("click", () => {
              cardBody.classList.toggle("hidden");
              buttonArrow.classList.toggle("arrow-down");
              buttonArrow.classList.toggle("arrow-right");
            });

            // set Card title, i.e. "Person", "Book", etc.
            let cardHeader = cardClone.querySelector("div.card-header");
            let cardHeaderText = document.createTextNode(className);
            cardHeader.appendChild(cardHeaderText);

            const data = entry.data;

            // create table for each class
            let thead = cardClone.querySelector("thead");
            let tbody = cardClone.querySelector("tbody");

            // add table header
            const tableHeaderClone = tableRowTpl.content.cloneNode(true);
            let tableHeaderRow = tableHeaderClone.querySelector("tr");

            // add first column for adding, deleting rows
            const thAddDelete = document.createElement("th");
            thAddDelete.classList.add("first-col");
            tableHeaderRow.appendChild(thAddDelete);

            // use attributes of the first dataset entry as the column headers
            // except gemId
            const classAttrs = Object.keys(data[0]);
                // .filter(a => a !== "gemId");
            for (const attr of classAttrs) {
              const th = document.createElement("th");
              th.innerText = attr;
              tableHeaderRow.appendChild(th);
            }
            thead.appendChild(tableHeaderRow);

            // add first row for adding new rows
            const tableAddRowClone = tableRowTpl.content.cloneNode(true);
            let tableAddRow = tableAddRowClone.querySelector("tr");

            // add first cell with a symbol for adding rows
            const tdAdd = cellAddTpl.content.cloneNode(true).querySelector("td");
            tableAddRow.appendChild(tdAdd);
            // add empty cells for creation
            const addRowCells = new Map();
            for (const attr of classAttrs) {
              const td = addBodyCell(tableAddRow);
              addRowCells.set(attr, td);
            }
            // event listener for object creation
            tdAdd.addEventListener("click", () => {
              // initial object to create
              const obj = {};
              let valid = false;
              const cells = [];
              for (const key of addRowCells.keys()) {
                // table cell corresponding to the column
                const td = addRowCells.get(key);
                if (td) {
                  cells.push(td);
                  const value = convert(td.textContent.trim());
                  obj[key] = value;
                  // TODO: delete if empty objects are allowed;
                  // object is valid if at least one property is a non-empty value;
                  // prevents creating a bunch of empty objects
                  if (value.length > 0) {
                    valid = true;
                  }
                }
              }
              if (valid) {
                // create object and empty the row;
                // if error happens, the row will not be emptied (for possible recovery?)
                createObj(obj, className).then(_ => {
                  for (const cell of cells) {
                    cell.innerText = "";
                  }
                });
              } else {
                window.alert("Please set at least on field to create a " + className);
              }
            });
            tbody.appendChild(tableAddRow);

            // add row for each object
            for (const entry of data) {
              const gemId = entry.gemId;
              if (gemId != undefined) {
                const tableBodyClone = tableRowTpl.content.cloneNode(true);
                let tableBodyRow = tableBodyClone.querySelector("tr");

                // add first cell with a symbol for deleting rows
                const tdDel = cellDeleteTpl.content.cloneNode(true).querySelector("td");
                tdDel.addEventListener("click", () => {
                  deleteObj(className, gemId);
                })
                tableBodyRow.appendChild(tdDel);

                for (const attr of classAttrs) {
                  const td = addBodyCell(tableBodyRow, () => {
                    handleInput(td, className, attr, gemId);
                  });
                  td.innerText = entry[attr];
                }
                tbody.appendChild(tableBodyRow);
              }
            }

            // add card
            body.appendChild(cardClone);
          }
        } else {
          console.error("Please report this error to the supervisor, error code: AGe");
          // ask AGe to add code, because someone probably uses some weird browser
          // or add code yourself
        }
      });
    };
  </script>
</head>

<!-- body is initially emtpy -->

<body>
  <!-- TODO: remove if you don't want info -->
  <div class="info-card">
    <h2>Tips:</h2>
    <p>
      Update: click into a cell and type a value in. The value will be sent to the server a bit after you stop typing.<br>
      Create: type value into the first (empty) row of a table. Click &#8853; to send data to the server.<br>
      Delete: Click &#128465; in a corresponding row.<br>
    </p>
  </div>
</body>

<!-- page title template ( SELib )-->
<template id="titletpl">
  <h1 class="title"></h1>
  <hr />
</template>
<!-- card template -->
<template id="cardtpl">
  <div class="card">
    <div class="card-header">
      <button class="card-button">
        <div class="arrow-right"></div>
      </button>
    </div>
    <div class="card-body">
      <table>
        <thead>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>
  </div>
</template>
<!-- table row template -->
<template id="tablerowtpl">
  <tr>
  </tr>
</template>
<!-- table add cell template -->
<template id="celladdtpl">
  <td class="first-col">&#8853;</td>
</template>
<!-- table delete cell template -->
<template id="celldeletetpl">
  <td class="first-col">&#128465;</td>
</template>

</html>

