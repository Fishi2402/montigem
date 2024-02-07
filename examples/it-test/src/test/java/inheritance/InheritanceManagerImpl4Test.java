/* (c) https://github.com/MontiCore/monticore */
package inheritance;

class InheritanceManagerImpl4Test extends InheritanceManagerImpl {

  public static void init () {
    InheritanceManagerImpl4Test impl = new InheritanceManagerImpl4Test();
    impl.inheritanceObservers.add(new InheritanceDbObserver());
    InheritanceManager.setInstance(impl);
  }

  public void clearCache() {

    knownAs.clear();
    knownBs.clear();
    // knownCs.clear(); is abstract -> does not exist
    knownDs.clear();
    knownEs.clear();
    // knownFs.clear(); is abstract -> does not exist
    knownGs.clear();
    knownHs.clear();
    // knownIs.clear(); is interface -> does not exist
    // knownJs.clear(); is interface -> does not exist
    // knownKs.clear(); is abstract -> does not exist
    knownLs.clear();
    knownMs.clear();
    knownNs.clear();
    knownPs.clear();
    knownQs.clear();
    knownRs.clear();
    knownLs.clear();
    knownAListAttrs.clear();
    knownBListAttrs.clear();
  }
}
