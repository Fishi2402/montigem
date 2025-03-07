package test_load_inner;

import umlp.jsweet.extension.annotation.Component;

@Component
public class NavContainer extends NavContainerTOP {
  @Override
  public String getDashboardTarget() {
    if (this.getGame() == null || this.getPlayer() == null)
      return null;
    // localhost:4200/test_load_inner/PageA/17/10
    return "/test_load_inner/PageC/" + this.getGame().getGemId() + "/" + this.getPlayer().getGemId();
  }

  @Override
  public String getRulesTarget() {
    if (this.getGame() == null || this.getPlayer() == null)
      return null;
    // localhost:4200/test_load_inner/PageA/17/10
    return "/test_load_inner/PageCC/" + this.getGame().getGemId() + "/" + this.getPlayer().getGemId();
  }
}
