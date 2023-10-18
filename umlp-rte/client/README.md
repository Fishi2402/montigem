<!--
If you make any changes to a library, please run `gradle build` to update the library version before publishing it.

# Creating a New Library
​
To create a new library, use the gradle script and run `gradle create library name`.
This script will assign the library a default version.
​
# Update a Library
​
Whenever changes are made to a library, use the gradle script to
update its version. For example, if a table has version 7.5.4
and you make changes to the table library, running `gradle build`
will upgrade the version from 7.5.4 to 7.5.5, as defined in the
gradle script.
-->
# Example setup

- Example library: [arrange](./projects/umlp/arrange). `arrange` (full name: `@umlp/arrange`) can be
  replaced with any other library name in this instruction.
- [Full library list](./projects/umlp)
- [Project root](./). Will be referred to as "root" in this instruction.

# Updating a library

Whenever changes are made to a library, the version needs to be updated in the `package.json` file
of the library. Example (all links lead to the `arrange` example, use paths relevant for you case
instead):

- A file is changed in `arrange` library, i.e. any file inside [the library
  folder](./projects/umlp/arrange).
- Open [`package.json` file](./projects/umlp/arrange/package.json) of the changed library. 
- Increment the last version number of the `"version"` field.
  - e.g., current value is `"7.5.1"`. Set new value `"7.5.2"`.
- Call gradle command for publishing the library:
  ```
  gradle publish_arrange
  ```

<!--
# Usage of the Library
​
To use the library, add the following line to your `package.json` file:
"library name": "^7.5.0". The "^" symbol ensures that the latest version of the library is used.
-->
