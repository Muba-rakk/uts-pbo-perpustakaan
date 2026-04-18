package models;

import java.util.ArrayList;
import java.util.List;

public class Member extends Person {
  private String nim;
  private int maxBorrow;
  private transient List<Loan> activeLoans;

  public Member(String name, String id, String nim) {
    super(name, id);
    this.nim = nim;
    this.activeLoans = new ArrayList<>();
    this.maxBorrow = calculateMaxBorrow();
  }

  private int calculateMaxBorrow() {
    if (nim == null || nim.isEmpty()) {
      return 0;
    }

    char lastChar = nim.charAt(nim.length() - 1);
    int lastDigit = Character.getNumericValue(lastChar);

    if (lastDigit >= 0 && lastDigit <= 3) {
      return 2;
    } else if (lastDigit >= 4 && lastDigit <= 6) {
      return 3;
    } else if (lastDigit >= 7 && lastDigit <= 9) {
      return 5;
    }

    return 0;
  }

  public String getNim() {
    return nim;
  }

  public void setNim(String nim) {
    this.nim = nim;
    this.maxBorrow = calculateMaxBorrow(); // Memperbarui batas pinjaman jika NIM berubah
  }

  public int getMaxBorrow() {
    return maxBorrow;
  }

  public List<Loan> getActiveLoans() {
    return activeLoans;
  }
}
