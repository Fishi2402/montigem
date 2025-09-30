package totaltest;

import webshop.Item;
import webshop.WebshopManager;

//TODO TOP mechanism doesn't work for some reason, so this is a workaround
public class MyTotalTest extends TotalTest {
  @Override
  public Item createItem() {
    return WebshopManager.itemBuilder().amount(2).perUnitCost(3).build().orElseThrow();
  }
}
