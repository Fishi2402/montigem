package hardy;

//+ GEN-P        : generated for the product

import org.hibernate.annotations.GenericGenerator;
import umlp.rte.GemIdentifiable;

import javax.persistence.*;
import java.util.Observable;
import java.util.Optional;

/* Source:
    class Address{
        String city;
    }
*/

// --------------------------

@Entity
@Table(name = "address")
public class Address extends Observable implements GemIdentifiable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    protected long gemId = -32;

    @Column(name = "city")
    protected String city;

    @Override
    public long getGemId() {
        return gemId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        checkCity();
    }

    public boolean checkCity(){
        boolean res = true;
        if(city == null){
            res = false;
            AddressErrorCollector.reportCityInvalid(this);
        }

        return res;
    }

    protected Address() {
        init();
    }

    protected void init() {
    }

    //+ Pseudo build for all classes: normally nothing happens
    public Optional<Address> build() {
        return Optional.empty();
    }
}
