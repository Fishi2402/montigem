---
hide:
  - navigation
---

ContextMenu displays an organized list of commands or options and save space by hiding until the user needs them. It is used to provide actions that affect a specific item or context.

<img src="https://i0.wp.com/www.cssscript.com/wp-content/uploads/2019/04/Pretty-Clean-Context-Menu-In-Pure-JavaScript-Contextual.js.png?fit=485%2C366&ssl=1" alt="Data Table" width=40%/>

---

## GUI Model

```java
package mc.fenix.table.src.lib.gemtable;

import mc.fenix.table.src.lib.gemtable.TableTypes.TableColumn;

component GemTable(
List rows,
List columns,
Event edit,
Event update,
Event cancelUpdate,
boolean canDelete,
Event delete,
boolean canCreate,
Event create,
Event cancelCreate,

Object cellStyle,
Object tableContainerStyle,
boolean compactStyle,
boolean hasColumnFooter,
GUIViewElement footerTemplate,
Object activeRow,
Object inactiveRowStyle,
boolean isFilterable,
Object sortDirection,
List selection,
Event batchModeToggle,
Event rowsSelect,
Object selectionType,
boolean batchMode,
Object staticRow,
Object extendableRow,
Object groupClass,
Object groupTooltip,
Object rowClass,
GUIViewElement filterTemplate,
boolean canHideColumns,
boolean isGroupable,
boolean hasShowInactiveBtn,
boolean alwaysEdit
)
```

- `rows` - define the data displayed in a table
- `columns` - define the table header, what properties of rows are shown in each column, etc.

The CRUD properties define how objects displayed in the row are managed:

- `edit` - event emitted when a row is activated for an edit action (click on edit button)
- `update` - event emitted when the save button is clicked in a row
- `cancelUpdate` - event emitted when the save is cancelled (click on cancel button for edited row)
- `canDelete` - whenever user should be able to delete a row, automatically set to true if an action for `delete` is defined
- `delete` - event emitted when the delete button is clicked in a row
- `canCreate` - whenever user should be able to create a new row, automatically set to true if an action for `create` is defined.
- `create` - event emitted when the save button is clicked in a create-row
- `cancelCreate` - event emitted when the cancel button is clicked in a create-row

Usage example:

```ts
page RowsTester(
) {
@GemRows(
components = [
@GemContextMenu(

),
//other components...
]
);
}
```

`RowsTester` utilizes `GemRows` as a container and takes `GemRows` as an input
and display in its body.
