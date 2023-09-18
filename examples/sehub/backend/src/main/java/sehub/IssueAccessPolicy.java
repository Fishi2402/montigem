package sehub;

public class IssueAccessPolicy extends IssueAccessPolicyTOP {
  @Override
  public boolean checkPermission(umlp.backendrte.command.Command command) {
    // System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    System.err.println("I am checking Permission of Command + " + command.getCommandType());
    return true;
  }
}

