# navigation

This library adds NavItems and NavLists.

NavItems render as buttons with the class `nav-item`.
If they have a target and the current routers url start with their target, they also gain the `active` class.
Clicking on will result in an attempted navigation to their target.

```gui
 @NavItem(title = "Home" , target = "/gui/Start")
```

Instead of a target, a leftClick event may als be present.

```gui
 @NavItem(title = "Logout" , leftClick = logout)
```

A somewhat nicer look for lists of buttons are NavLists.

```gui
@NavList( components = [
  @iterate(Project p : projects) {
    @GemRow(height="auto",components = [
      @NavItem(title = p.getName() , target = "/gui/project/" + p.getName() + "/show")
    ]);
  }
]
```
