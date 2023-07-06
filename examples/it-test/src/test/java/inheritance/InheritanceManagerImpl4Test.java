package inheritance;

class InheritanceManagerImpl4Test extends InheritanceManagerImpl {

  public static void init () {
    InheritanceManagerImpl4Test impl = new InheritanceManagerImpl4Test();
    impl.inheritanceObservers.add(new InheritanceDbObserver());
    InheritanceManager.setInstance(impl);
  }

  public void clearCache() {
    knownMs.clear();
    knownNs.clear();
    knownPs.clear();
    knownQs.clear();
    knownRs.clear();
  }
}
