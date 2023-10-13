package security;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import dtcockpit.DtcockpitManager;
import umlp.backendrte.common.GemList;

import java.util.ArrayList;
import java.util.Collection;
/**/

public class CustomUserDetailsService implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    GemList<dtcockpit.User> profilList = DtcockpitManager.getUserList();
    dtcockpit.User profil = null;
    for (dtcockpit.User x : profilList) {
      if (String.valueOf(x.getUsername()).equals(username)) {
        profil = x;
      }
    }
    if (profil == null) {
      throw new UsernameNotFoundException("Username not found");
    }

    Collection<SimpleGrantedAuthority> foundAuthorities = new ArrayList<>();
    foundAuthorities.add(new SimpleGrantedAuthority("ROLE_" + profil.getRole().name().toUpperCase()));
    return new User(profil.getUsername(), profil.getPassword(), foundAuthorities);
  }
  /**/
}
