package models;

public class Librarian extends Person {
  private String employeeId;
  protected String position;

  public Librarian(String name, String id, String employeeId, String position) {
    super(name, id);
    this.employeeId = employeeId;
    this.position = position;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}
