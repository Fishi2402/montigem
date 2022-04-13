package hardy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

// TODO
public class PersonManager implements DexObservable {

    private static Storage<Person> manager;

    protected static PersonManager instance;

    private Set<DexObserver> observers = new HashSet();

    protected PersonManager() {
    }

    public static void init(Storage<Person> manager) {
        PersonManager.manager = manager;
    }

    public static void init() {
        instance = new PersonManager();
    }

    public static PersonManager get() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public static Response<Person> add(CreatePersonRequest r) {
        checkManager();
        get().notifyObservers(r);
        return manager.add(r);
    }

    public static Response<Person> update(UpdatePersonRequest r) {
        checkManager();
        get().notifyObservers(r);
        return manager.update(r);
    }

    public static void update(Person o) {
        PersonBuilder.over(o).setNickname(o.getNickname()).setAge(o.getAge()).setOwns(Sets.newHashSet(o.iteratorOwnss())).build();
    }

    public static Response<Person> get(Person o) {
        checkManager();
        get().notifyObservers(o);
        return manager.get(o);
    }

    public static Response<Person> remove(Person o) {
        checkManager();
        get().notifyObservers(o);
        return manager.discard(o);
    }

    public static Response<Person> remove(Collection<Person> o) {
        checkManager();
        Iterator var1 = o.iterator();

        Response resp;
        do {
            if (!var1.hasNext()) {
                return new Response(Type.SUCCESS);
            }
            Person e = (Person)var1.next();
            resp = remove(e);
        } while(!resp.getType().equals(Type.FAILURE));
        return resp;
    }

    public static Iterator<Person> iterator(ReadRequest r) {
        checkManager();
        r.setTypes(Lists.newArrayList(new String[]{"Person"}));
        return manager.iterator(r);
    }

    public static Iterator<Person> iterator() {
        checkManager();
        ReadRequest r = new ReadRequest();
        r.setTypes(Lists.newArrayList(new String[]{"Person"}));
        return manager.iterator(r);
    }

    public static int size() {
        checkManager();
        return (Integer)manager.size().getObject();
    }

    public static void clear() {
        checkManager();
        manager.clear();
        get().notifyObservers();
    }

    public static Map<Person, List<String>> searchPerson(String s) {
        checkManager();
        return manager.search(s);
    }

    public static boolean canUpdate() {
        checkManager();
        return manager.canUpdate();
    }

    public static boolean canDelete() {
        checkManager();
        return manager.canDelete();
    }

    public static boolean canRead() {
        checkManager();
        return manager.canRead();
    }

    public static boolean canCreate() {
        checkManager();
        return manager.canCreate();
    }

    public static Response<Person> get(Long id) {
        get().notifyObservers(id);
        checkManager();
        return manager.get(id);
    }

    private static void checkManager() {
        if (manager == null) {
            manager = PersonStorageMock.get();
        }
    }

    public void addObserver(DexObserver observer) {
        this.observers.add(observer);
    }

    public void deleteObserver(DexObserver observer) {
        this.observers.remove(observer);
    }

    public void deleteObservers() {
        this.observers.clear();
    }

    public void notifyObservers() {
        this.notifyObservers((Object)null);
    }

    public void notifyObservers(Object argument) {
        Iterator var2 = this.observers.iterator();

        while(var2.hasNext()) {
            DexObserver o = (DexObserver)var2.next();
            o.update(this, argument);
        }
    }
}
