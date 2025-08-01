The component is a dropdown with an optional label. When clicking on the input, it will open a list of options to select from. It is used whenever a user is required, to choose between different predefined options.

## Usage example

```gui
package example_input;

import mc.fenix.input.GemDropdownInput;
import mc.fenix.input.GemDropdownInputTypes.Option;
import mc.fenix.arrange.GemColumn;
import mc.fenix.arrange.GemRow;

page GemDropdownInputExample() {
  @GemRow(
    components = [
      @GemDropdownInput(
        width = "50%",
        labelText = "Academic degree:",
        options = [
          Option("Bachelor"),
          Option("Master"),
          Option("PhD")
        ]
      )
    ]
  );
  @GemColumn(
    rowGap = "15px",
    components = [
      @GemRow(
        components = [
          @GemDropdownInput(
            width = "50%",
            labelText = "Hobby:",
            options = [
              Option("Tennis"),
              Option("Sleeping"),
              Option("Model-driven development"),
              // ...
            ],
            searchEnabled = true
          )
        ]
      ),
      @GemRow(
        components = [
          @GemDropdownInput(
            width = "50%",
            labelText = "How often do you attend your Hobby?",
            options = [
              Option("Once a year"),
              Option("Once a month"),
              Option("Once a week"),
              Option("Three times a week"),
              Option("Everyday"),
              Option("More than once a day")
            ]
          )
        ]
      )
    ]
  );
}
```

<img src="../images/DropdownInput.png" alt="DropdownInput" width="50%">

Options filtered by search input  
<img src="../images/DropdownInputFiltered.png" alt="AutocompleteInput" width="50%">

## GUI Model

`GemDropdownInput.gui` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/input/GemDropdownInput.gui))

```gui
package mc.fenix.input;

import mc.fenix.input.GemDropdownInputTypes.Option;
import mc.fenix.input.GemTextInputTypes.GemTextInputValidity;

component GemDropdownInput(
  String id,
  String labelText = "",
  List<Option> options,
  ?String entry,
  boolean disabled = false,
  String width = "100%",
  String height = "100%",
  Event<Object> keyRelease,
  Event<Object> keyPress,
  GemTextInputValidity validity,
  boolean searchEnabled = false
)
```

| Name | Type | Description | Default value |
|---|---|---|---|
| `id` | `String` | Can be set to ensure the list of options is specific to the input field. | `null` (no id) |
| `labelText` | `String` | Text describing the dropdown. | `""` (empty placeholder) |
| `options` | `List<Option>` | A list of options for the dropdown. | `null` (no options) |
| `entry` | `?String` | The input field's value. If it is linked to a variable, the value of the variable will change when the user enters a new value. | `null` (no value) |
| `disabled` | `boolean` | Whether the input field is disabled. | `false` (input is enabled) |
| `width` | `String` | Width of the dropdown. | `"100%"` |
| `height` | `String` | Height of the dropdown. | `"100%"` |
| `validity` | `GemTextInputValidity` (see [GemTextInputTypes](../GemTextInput/GemTextInput#gemtextinputtypes))| An object representing the validation status of the field. | `null` (input is always valid) |
| `keyRelease` | `Event` | Callback that is executed when a key is released. | `null` (no action) |
| `keyPress` | `Event` | Callback that is executed when a key is pressed. | `null` (no action) |
| `searchEnabled` | `boolean` | Show a search input to filter the options if `true` | `false` (no search input) |

### `GemDropdownInputTypes` 

`GemDropdownInputTypes.cd` ([open](https://git.rwth-aachen.de/monticore/umlp/-/blob/dev/umlp-rte/client/models/mc/fenix/input/GemDropdownInputTypes.cd))

```cd
package mc.fenix.input;

classdiagram GemDropdownInputTypes {
  public class Option {
    String value;
    String display;
  }
}
```

**`Option`**
| Name | Type | Description | Default value |
|---|---|---|---|
| `value` | `String` | Value behind the option in a dropdown. An option typically represents an object, e.g. person, via human-readable name. The value is an identifier that represents the object in the logic implementation. | `null` (no status) |
| `display` | `String` | Human-readable name representing the option. | `null` (no message) |
