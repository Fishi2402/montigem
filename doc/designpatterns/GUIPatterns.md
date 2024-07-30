# GUI Design Patterns for MontiGem Development 

**WARNING** These patterns originated in a time when MontiGem was still volatile and the development
of a new MontiGem App was partly constrained by technical issues. 
As such the used examples may become out of date and in the future as MontiGem evolves there may be 
other more elegant ways to implement your wanted functionality.

It is recommended to use the following patterns in combination as they built upon each other.
All of these pattern have in common that they try to minimize code duplication while also maintaining
high reusability


## Component and Page Pattern

Typically, you want most of your pages, a user can access, to be embedded by the same elements, 
e.g. a Navigation bar or a footer. Naively integrating these components into each of your component
pages as in the example below is an antipattern. 

```guidsl

page Example(Example example) {

  @Header();
  @GemTextInput(labelText = "Text", inputType = "text", entry = example.name);
  @Footer();
}

```

As soon as you want to reuse the example page in another page you also add the `Header` and `Footer`
component to your page and probably does not appear where you want it. 
To avoid this you can simply create one simple `Example` page without the `Header` and `Footer`
and another `ExamplePage` page that embeds the simple `Example` page.

```guidsl

page Example(Example example) {

  @GemTextInput(labelText = "Text", inputType = "text", entry = example.name);
}

```

```guidsl

page ExamplePage(Example example) {
  @Header();
  @Example(example=example);
  @Footer();
}

```

Now you can reuse the `Example` page without any problems and also have the `ExamplePage` page as
a standalone page accessible for your users.

## Edit and Creation Page Pattern 

This design pattern is intended to be used when you want to implement one/ or two forms for 
editing and creating instances of a class, which can be tricky at first glance because Edit pages
require an already existing object while Creation pages require an object to be built. To explain
the pattern we use the following type `BatteryPack` (taken from DemoRec) as example: 

```cd

class BatteryPack  {
  double stateOfCharge;
  Optional<double> health;
}

```

Usually, we want to be able to edit all the attributes that we also have to set when creating a new
instance of the object. For `BatteryPack` this can be achieved the following way: 
 
```guidsl

page PackEdit(BatteryPack pack) {

  @GemTextInput(labelText = "Health (%)", inputType = "number", entry = pack.health);
  @GemTextInput(labelText = "Charge (%)", inputType = "number", entry = pack.stateOfCharge);
}

```

We do not only want to edit `BatteryPacks` but also create new instances of them reusing as much 
code as possible.
Creation pages differ from normal pages, because they are instantiated with a builder object, which
does not yet exist in the database/ backend and thereby does not have a unique `gemId`. The 
MontiGem generator needs these pages to be marked, because in their instantiation the Manager has to
be called for a new builder object. 
Because of this and because we need some form of user-input to let a User indicate that 
they have finished data entry and the fresh object can be instantiated, we cannot simply use the
`PackEdit` page for instantiation of new `BatteryPacks`. The remedy is a second page we call 
`PackCreate` which uses the `PackEdit` page and furthermore adds a user-input component to create a
new instance.

```guidsl
page PackCreate(BatteryPack pack) {

  @PackEdit(pack=pack);
  @GemButton(label="Add BatteryPack", leftClick=createNewBatteryPack(), disabled=false);

}
```

```typescript
  public createNewBatteryPack () {
    return () => {
      if(this.pack.isBuildable()) {
        this.pack.build()
      }
    }
  }
``` 

## Creating new instances where they have to be used

Sometimes we want to create a new instances of a class on page where it should be directly used.
One example is we want to be able to choose a specific `BatteryPack` in a dropdown menu. The dropdown
options have to be updated as soon as the new `BatteryPack` is built. Of course, we do not want to 
copy the code of our `PackCreate` page, but simply reusing it is difficult because we have to update
the dropdown options of our new page. The remedy is to emit a new event, once the pack was created.
To achieve this using the `GUI-DSl` we can adapt our `PackCreate` page from above like this:

```guidsl

page PackCreate(BatteryPack pack, Event<BatteryPack> newPack) {

  @PackEdit(pack=pack);
  @GemButton(label="Add BatteryPack", leftClick=createNewBatteryPack(), disabled=false);

}
```

We furthermore have to specify when the event is emitted in the handwritten code of the `PackCreate`
component. For this we simply adapt the `createNewBatteryPack` method.

```typescript
  public createNewBatteryPack () {
    return () => {
      if(this.pack.isBuildable()) {
        this.newPack.emit(this.pack.build())
      }
    }
  }
```

Now, to be able to embed this page appropriately we have to bind a method to this event on the new 
page. 

```guidsl

page UsingPackCreate() {
  @PackCreate(newPack=addNewBatteryPack());
}

```

```typescript
  public addNewBatteryPack () {
    return (pack) => {
      // This is triggered when newPack emits a new event
      // Do something with the pack e.g. add it to already existing options 
    }
  }
```

## Form for Required Relationships: Choose-Create-Edit Pattern

This design pattern is intended to be used when you have to create a form for a class which has a
required association as e.g. `BatteryPack` in the following CD (simplified version of the DemoRec cd):

```cd

class BatteryPack  {
  double stateOfCharge;
  Optional<double> health;
}

class BatteryPackType {
  Optional<String> carBrand;
  Optional<String> carModel;
  Optional<String> carYear;
}

association [*] BatteryPack -> (type) BatteryPackType [1];
```

Creating a form for `BatteryPack` is a bit tricky here. There are a few thing to keep in mind: 
- in the `BatteryPack` form we want to either choose from a set of already existing `BatteryPackTypes`
  or create a new `BatteryPackType` 
- the form should be reusable for creating new instances and editing existing ones
- the required `type` association must always be set
- it is annoying for a user to have to criss-cross navigate between pages for `BatteryPack` and 
  `BatteryPackType` and it may also pose technical challenges like keeping data entry in a form when
  reloading the page, if the object was not yet built (sent to the backend) 
- we already made use of the `Edit and Creation Page Pattern` from above to implement the forms for
  `BatteryPackType` and have a `BatteryPackTypeCreate` and a `BatteryPackTypeEdit` GUI component

The solution to our problem is implementing a third GUI component for `BatteryPackType` which we 
call `BatteryPackTypeChoose`. The component has a dropdown menu to choose from already existing 
`BatteryPackTypes` and additionally there is an option `NEW` which unhides 




