package models;

public class Book {
  private String bookCode;
  private String title;
  private String author;
  private String category;
  private int stock;

  public Book(String bookCode, String title, String author, String category, int stock) {
    this.bookCode = bookCode;
    this.title = title;
    this.author = author;
    this.category = category;
    this.stock = stock;
  }

  public String getBookCode() {
    return bookCode;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getCategory() {
    return category;
  }

  public int getStock() {
    return stock;
  }

  public boolean decreaseStock() {
    if (this.stock > 0) {
      this.stock--;
      return true;
    }
    return false;
  }

  public void increaseStock() {
    this.stock++;
  }
}
