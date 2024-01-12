/* (c) https://github.com/MontiCore/monticore */

package hardy.gui;

import hardy.flensburg.Address;
import hardy.flensburg.Person;
import umlp.jsweet.extension.annotation.Directive;
import umlp.jsweet.extension.annotation.Input;
import umlp.jsweet.extension.annotation.UMLPSynchronized;

import java.util.List;

@Directive(cdName="Flensburg")
public class PersonAddAddress {

  @Input
  public Person person = null;

  @Input
  public Address address = null;

  public PersonAddAddress(Person person, Address address) {

    this.person = person;
    this.address = address;

    init();

  }

  public void init() {

  }

  @UMLPSynchronized
  public List<Person> getChildren() {
    return this.person.getChildren();
  }

  public Person getPerson() {

    return person;
  }

  public Address getAddress() {

    return address;
  }

  public String getGemCard0Title() {

    return "Add to Favorites";
  }

  public String getGemText4Value() {

    return "Favorites:";
  }

  public String getGemRow2RowGap() {

    return "5px";
  }

  public String getGemText6Value(Person fav) {

    return fav.getNickname();
  }

  public String getGemColumn1HAlign() {

    return "center";
  }

  public String getGemColumn1RowGap() {

    return "10px";
  }

  public String getAddbuttonLabel() {

    return "+";
  }

  public void addbuttonLeftClick(Object event) {

    this.add2Favorite(event);
  }

  public void add2Favorite(Object event) {

  }

  public String getGemText3Value() {

    return " ";
  }

  public String getCleanLabel() {

    return "Clear Favorites";
  }

  public void cleanLeftClick(Object event) {

    this.clearFavorites(event);
  }

  public void clearFavorites(Object event) {

  }

}


