package kitchencompanion;


public class Search extends SearchTOP {
  /**
   * @param userType represents which type of user we are searching for extra, lead, ordinary or hiwi comes from the searchX() methods
   * @return true if the user is actually searched for and false if not
   */
  private static Boolean search(Search s, User user, boolean userType) {
    String nickname = user.getNickname().toLowerCase();
    String username = user.getName() + " " + user.getLastName();
    username = username.toLowerCase();

    if (s == null) {
      return userType;
    } else if (s.isSearched()) {
      return search(null, user, userType) && (nickname.startsWith(s.getSearchStr().toLowerCase()) || (user.getLastName().startsWith(s.getSearchStr())||user.getLastName().startsWith(s.getSearchStr().toLowerCase())));
    }
    return search(null, user, userType);
  }

  public static Boolean offerSearch(Search s, Offer offer, boolean inStock) {
    String name = offer.getName().toLowerCase();
    String search;
    if (s == null) {
      return true;
    } else if (s.isSearched()) {
      search = s.getSearchStr();
      search = search.toLowerCase();
      return offerSearch(null, offer, inStock) && (name.startsWith(search));
    }
    return offerSearch(null, offer, inStock);
  }

  /**
   * @param user
   * @return returns true if the user is searched for as a lead user or if there is no search string and the user is a lead user, otherwise false
   */
  public static Boolean searchLead(Search s, User user) {
    return search(s, user, (user.getRole() == KitchenCompanionRole.VIP));
  }

  /**
   * @param user
   * @return returns true if the user is searched for as an extra user or if there is no search string and the user is an extra user, otherwise false
   */
  public static Boolean searchExtra(Search s, User user) {
    return search(s, user, (user.getNickname().compareTo("Chair") == 0));
  }

  /**
   * @param user
   * @return returns true if the user is searched for as ordinary or if there is no search string and the user is an ordinary user, otherwise false
   */
  public static Boolean searchOrdinary(Search s, User user) {
    return search(s, user, (user.getRole() == KitchenCompanionRole.USER || user.getRole() == KitchenCompanionRole.ADMIN)
        && (user.getNickname().compareToIgnoreCase("Chair") != 0));
  }

  /**
   * @param user
   * @return returns true if the user is searched for as a hiwi or if there is no search string and the user is a hiwi, otherwise false
   */
  public static Boolean searchHiwi(Search s, User user) {
    return search(s, user, (user.getRole() == KitchenCompanionRole.HIWI));
  }
}
