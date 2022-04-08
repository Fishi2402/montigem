package builder;

import java.util.*;

public class PersonBuilder extends HumanBuilder {

    protected String nickname = "";

    protected List<Car> owns = new LinkedList<>();

    protected List<Person> child = new LinkedList<>();

    protected List<Person> parent = new LinkedList<>();

    public PersonBuilder() {
    }

    public PersonBuilder nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getNickname() {
        return this.nickname;
    }

    public PersonBuilder age(int age) {
        super.age(age);
        return this;
    }

    public PersonBuilder owns(List<Car> o) {
        this.owns = o;
        return this;
    }

    public PersonBuilder addOwns(Car o) {
        this.owns.add(o);
        return this;
    }

    public List<Car> getOwns() {
        return this.owns;
    }

    public PersonBuilder child(List<Person> o) {
        this.child = o;
        return this;
    }

    public PersonBuilder addChild(Person o) {
        this.child.add(o);
        return this;
    }

    public List<Person> getChild() {
        return this.child;
    }

    public PersonBuilder parent(List<Person> o) {
        this.parent = o;
        return this;
    }

    public PersonBuilder addParent(Person o) {
        this.parent.add(o);
        return this;
    }

    public List<Person> getParent() {
        return this.parent;
    }

    public Person build() {
        return build(false);
    }

    public Person build(boolean addToBidirectionalAssociation) {
        Person newObj = new Person().rawInitAttrs(nickname, age, owns, child, parent);
        if (addToBidirectionalAssociation) {
            for (Person o : child) {
                o.addParent(newObj);
            }
            for (Person o : parent) {
                o.addChild(newObj);
            }
        }
        return newObj;
    }
}
