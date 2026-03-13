package totaltest;

import webshop.Item;
import webshop.WebshopManager;

public class TotalTest extends TotalTestTOP {
  @Override
  public Item createItem() {
    return WebshopManager.itemBuilder().amount(2).perUnitCost(3).build().orElseThrow();
  }
}
