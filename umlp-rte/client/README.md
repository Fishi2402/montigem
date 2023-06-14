## Version Control
​
# Must Be Done
​
If you make any changes to a library, please run `gradle build` to update the library version before publishing it.
​
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
​
# Usage of the Library
​
To use the library, add the following line to your package.json file:
"library name": "^7.5.0". The "^" symbol ensures that the latest version of the library is used.
