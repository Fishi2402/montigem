package hardy;

//+ GEN-P        : generated for the product

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AddressBuilder extends Address {

    protected boolean buildFinished = false;
    protected boolean isPresentCity = false;

    // Bei der Deserialiserung kann davon ausgegangen werden, dass alle Objekte gebaut sind, und das
    // FlensburgManager#addX nicht vom Builder aufgerufen werden muss. Entweder das Objekt wird von Client geladen,
    // dann ist es bereits beim Server registiert, oder es wird am Ende der Erstellung and den Server gesendet, und
    // der X_create Command ruft danach FlensburgManager#addX auf.
    protected boolean INTERNAL_deser_mode = false;

    public void setINTERNAL_deser_mode(boolean value){
      this.INTERNAL_deser_mode = value;
    }

    public AddressBuilder(long gemId) {
       this.gemId = gemId;
       this.visitors = FlensburgManager.createVisitorsBuilder(gemId, FlensburgManager.getFreeGemListId(), Collections.emptyList());
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public AddressBuilder city(String city){
        this.city = city;
        isPresentCity = true;
        return this;
    }

    public Optional<Address> build() {
        if (!buildFinished) {
            if(isBuildable()) {
                if(!INTERNAL_deser_mode) {
                  visitors.build();
                  FlensburgManager.addAddress(this);
                }
                addressObservers.forEach(obs -> obs.notifyBuild(this));
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    private boolean isBuildable() {
        return checkCityPresent()
                & checkCity();
    }

    private boolean checkCityPresent() {
        if(!isPresentCity){
            AddressErrorCollector.reportCityInvalid(this);
        }
        return isPresentCity;
    }

  public AddressBuilder visitorGemListIds(List<Long> ids) {
    visitors._internal_add(visitors.size(), ids);
    return this;
  }
}


