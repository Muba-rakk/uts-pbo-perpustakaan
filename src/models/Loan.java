package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
  private Member member;
  private Book book;
  private LocalDate borrowDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private boolean isReturned;
  private double fine;

  public Loan(Member member, Book book, LocalDate borrowDate) {
    this.member = member;
    this.book = book;
    this.borrowDate = borrowDate;
    this.dueDate = borrowDate.plusDays(7); // Masa pinjam default 7 hari
    this.isReturned = false;
    this.fine = 0.0;
  }

  // Method Overloading 1: Denda dengan tarif default (misal Rp 1000 per hari
  // keterlambatan)
  public void calculateFine(LocalDate returnDate) {
    calculateFine(returnDate, 1000.0); // Memanggil method overloading kedua
  }

  // Method Overloading 2: Denda dengan tarif custom
  public void calculateFine(LocalDate returnDate, double finePerDay) {
    if (returnDate.isAfter(this.dueDate)) {
      long daysLate = ChronoUnit.DAYS.between(this.dueDate, returnDate);
      this.fine = daysLate * finePerDay;
    } else {
      this.fine = 0.0;
    }
  }

  public void completeLoan(LocalDate returnDate) {
    this.returnDate = returnDate;
    this.isReturned = true;
    calculateFine(returnDate); // Menghitung denda secara otomatis saat dikembalikan (default)
  }

  // Overloaded completeLoan jika ingin menggunakan tarif denda custom
  public void completeLoan(LocalDate returnDate, double customFinePerDay) {
    this.returnDate = returnDate;
    this.isReturned = true;
    calculateFine(returnDate, customFinePerDay);
  }

  public Member getMember() {
    return member;
  }

  public Book getBook() {
    return book;
  }

  public LocalDate getBorrowDate() {
    return borrowDate;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public boolean isReturned() {
    return isReturned;
  }

  public double getFine() {
    return fine;
  }
}
