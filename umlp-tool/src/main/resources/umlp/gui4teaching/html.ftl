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
      box-shadow: 0 4px 8px #c7ddf2;
    }

    .info-card h2 {
      font-size: 24px;
      margin-top: 0;
    }

    .info-card p {
      font-size: 18px;
      line-height: 1.5;
    }

    .graph {
      display: flex;
      justify-content: center;
    }

    .title {
      margin: 20px;
      display: flex;
      justify-content: center;
      color: #00549f;
    }

    .card {
      background-color: #fff;
      border-radius: 4px;
      box-shadow: 0 2px 4px #c7ddf2;
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
      color: #00549f;
      cursor: pointer;
      padding: 10px;
      margin-right: 10px;
    }

    .arrow-right {
      width: 0;
      height: 0;
      border-top: 9px solid transparent;
      border-bottom: 9px solid transparent;
      border-left: 9px solid #00549f;
    }

    .arrow-down {
      width: 0;
      height: 0;
      border-left: 9px solid transparent;
      border-right: 9px solid transparent;
      border-top: 9px solid #407fb7;
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
      border-bottom: 1px solid #407fb7;
    }

    th {
      background-color: #00549f;
      color: white;
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

    .not-editable {
      background-color: #f2f2f2;
    }

    .not-editable:hover {
      cursor: not-allowed;
    }
  </style>
  <!--
    import data loading functions: file can be directly inserted into next <script>
    tag instead
  -->
  <script type='text/javascript' src='./index.js'></script>
  <!-- Load d3.js -->
  <script src="https://d3js.org/d3.v6.js"></script>
  <script>
    // Update of CRUD
    async function updateObj(cell, className, attr, gemId) {
      // Get the input value from the table cell
      try {
        const attrType = getType(className, attr);
        if (attrType === "Unknown" || attrType === "Array") {
          warnChangeUneditable(attr);
          return;
        }
        let value = convert(cell.textContent.trim(), attrType);

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
      } catch (err) {
        showError(err);
      }
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
      obj["gemId"] = convert((await sendCommand(freeGemId))["value"], "Number");
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

    function warnChangeUneditable(attr) {
      console.warn("0x00001", "Ignoring attribute '" + attr + "': Trying to"
        + " set a value for an association, "
        + "non-existing attribute, array, or an attribute of unknown type");
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

    function addReadOnlyBodyCell(row){
      const td = document.createElement("td");
      td.contentEditable = 'false';
      row.appendChild(td);
      td.classList.add("not-editable");
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
          const graphTpl = document.querySelector("#graphtpl");
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

          // create graph
          if (cd.graph
            && ((cd.graph.links && cd.graph.links.length)
              || (cd.graph.objects && cd.graph.objects.length))
          ) {
            const graphClone = graphTpl.content.cloneNode(true);
            body.appendChild(graphClone);
            renderGraph(cd.graph);
          }

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
            const classAttrs = Object.keys(data[0]);
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
              if (isReadonly(className, attr)) {
                const td = addReadOnlyBodyCell(tableAddRow);
                addRowCells.set(attr, td);
              } else {
                const td = addBodyCell(tableAddRow);
                addRowCells.set(attr, td);
              }
            }
            // event listener for object creation
            tdAdd.addEventListener("click", () => {
              // initial object to create
              const obj = {};
              let valid = false;
              const cells = [];
              try {
                for (const key of addRowCells.keys()) {
                  // table cell corresponding to the column
                  const td = addRowCells.get(key);
                  if (td) {
                    cells.push(td);
                    const value = td.textContent.trim();
                    // TODO: delete if empty objects are allowed;
                    // object is valid if at least one property is a non-empty value;
                    // prevents creating a bunch of empty objects
                    if (value.length > 0) {
                      valid = true;
                    }
                    const attrType = getType(className, key);
                    if (attrType !== "Unknown" && attrType !== "Array") {
                      obj[key] = convert(value, attrType);
                    } else {
                      warnChangeUneditable(key);
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
                  window.alert(
                    "Please set at least one field to create a " + className
                  );
                }
              } catch (err) {
                window.alert(
                  err + " Please make sure the entered data is of a valid type"
                );
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
                  if (isReadonly(className, attr)) {
                    const td = addReadOnlyBodyCell(tableBodyRow);
                    td.innerText = entry[attr];
                  } else {
                    const td = addBodyCell(tableBodyRow, () => {
                      handleInput(td, className, attr, gemId);
                    });
                    td.innerText = entry[attr];
                  }
                }
                tbody.appendChild(tableBodyRow);
              }
            }

            // add card
            body.appendChild(cardClone);
          }
        } else {
          console.error("0x00000", "Please report this error to the supervisor");
          // ask AGe to add code, because someone probably uses some weird browser
          // or add code yourself
        }
      });
    };

    async function renderGraph(data) {
      // set the dimensions of the graph
      const width = 1000;
      const height = 400;
      const radius = 25;

      const simulation = d3.forceSimulation(data.objects)
        .force("link", d3.forceLink(data.links).id(d => d.id))
        .force("charge", d3.forceManyBody().strength(-80))
        .force("center", d3.forceCenter(width / 2, height / 2))
        .force("x", d3.forceX())
        .force("y", d3.forceY())
        .force("collide", d3.forceCollide(d => 65))

      // append the svg object to the body of the page
      const svg = d3.select("#graph")
        .append("svg")
        .attr("width", "100%")
        .attr("height", "400px")
        .attr("viewBox", [0, 0, width, height]);

      svg.append("defs")
        .append("marker")
        .attr("id", "arrow")
        .attr("viewBox", "0 -5 10 10")
        .attr("refX", 38)
        .attr("refY", 0)
        .attr("markerWidth", 6)
        .attr("markerHeight", 6)
        .attr("orient", "auto")
        .append("path")
        .attr("fill", "black")
        .attr("d", 'M0,-5L10,0L0,5');

      // Initialize the links
      const link = svg
        .append("g")
        .attr("fill", "none")
        .attr("stroke-width", 1.5)
        .selectAll("path")
        .data(data.links)
        .join("path")
        .attr("stroke", "black")
        .attr("marker-end", "url(#arrow)");

      // Initialize the nodes
      const node = svg.append("g")
        .attr("fill", "currentColor")
        .attr("stroke-linecap", "round")
        .attr("stroke-linejoin", "round")
        .selectAll("g")
        .data(data.objects)
        .join("g")
        .call(drag(simulation));

      node.append("circle")
        .attr("stroke", "black")
        .attr("stroke-width", 1.5)
        .attr("r", radius)
        .attr("fill", "white");

      node.append("text")
        .attr("x", 30 + 4)
        .attr("y", "0.31em")
        .attr("text-decoration", "underline")
        .text(d => d.type.replace(/[a-z]/g, '').toLowerCase() + d.id + ":" + d.type)
        .clone(true).lower()
        .attr("fill", "none")
        .attr("stroke", "white")
        .attr("stroke-width", 3);

      simulation.on("tick", () => {
        link.attr("d", d => "M" + d.source.x + "," + d.source.y + "A0,0 0 0,1 " + d.target.x + "," + d.target.y)
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });
        node
          .attr("transform", d => "translate(" + d.x + "," + d.y + ")")
          .attr("cx", d => { return d.x = Math.max(radius, Math.min(width - radius, d.x)); })
          .attr("cy", d => { return d.y = Math.max(radius, Math.min(height - radius, d.y)); });
      });
    };

    function drag(simulation) {
      function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
      }
      function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
      }
      function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
      }
      return d3.drag()
          .on("start", dragstarted)
          .on("drag", dragged)
          .on("end", dragended);
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
<!-- graph template -->
<template id="graphtpl">
  <!-- Create a div where the graph will be shown -->
  <div id="graph" class="graph"></div>
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

