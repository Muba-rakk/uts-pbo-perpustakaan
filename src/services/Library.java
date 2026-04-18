package services;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import models.Book;
import models.Loan;
import models.Member;
import utils.JsonHelper;

public class Library {
  private List<Book> books;
  private List<Member> members;
  private List<Loan> loans;

  private static final String DATA_DIR = "data";
  private static final String BOOKS_FILE = DATA_DIR + "/books.json";
  private static final String MEMBERS_FILE = DATA_DIR + "/members.json";
  private static final String LOANS_FILE = DATA_DIR + "/loans.json";

  public Library() {
    new File(DATA_DIR).mkdirs();
    this.books = loadBooks();
    this.members = loadMembers();
    this.loans = loadLoans();
  }

  private List<Book> loadBooks() {
    Type type = new TypeToken<List<Book>>() {
    }.getType();
    return JsonHelper.loadListFromJson(BOOKS_FILE, type);
  }

  private List<Member> loadMembers() {
    Type type = new TypeToken<List<Member>>() {
    }.getType();
    return JsonHelper.loadListFromJson(MEMBERS_FILE, type);
  }

  private List<Loan> loadLoans() {
    Type type = new TypeToken<List<Loan>>() {
    }.getType();
    return JsonHelper.loadListFromJson(LOANS_FILE, type);
  }

  private void saveBooks() {
    JsonHelper.saveListToJson(books, BOOKS_FILE);
  }

  private void saveMembers() {
    JsonHelper.saveListToJson(members, MEMBERS_FILE);
  }

  private void saveLoans() {
    JsonHelper.saveListToJson(loans, LOANS_FILE);
  }

  public void addBook(Book book) {
    books.add(book);
    saveBooks();
  }

  public void addMember(Member member) {
    members.add(member);
    saveMembers();
  }

  public boolean isBooksEmpty() {
    return books.isEmpty();
  }

  public boolean isMembersEmpty() {
    return members.isEmpty();
  }

  private Member findMemberById(String id) {
    for (Member m : members) {
      if (m.getId().equals(id))
        return m;
    }
    return null;
  }

  private Book findBookByCode(String code) {
    for (Book b : books) {
      if (b.getBookCode().equals(code))
        return b;
    }
    return null;
  }

  public void borrowBook(String memberId, String bookCode, LocalDate borrowDate) {
    Member member = findMemberById(memberId);
    Book book = findBookByCode(bookCode);

    if (member == null) {
      System.out.println("Gagal: Member tidak ditemukan.");
      return;
    }
    if (book == null) {
      System.out.println("Gagal: Buku tidak ditemukan.");
      return;
    }

    int activeLoanCount = 0;
    for (Loan loan : loans) {
      if (loan.getMember().getId().equals(memberId) && !loan.isReturned()) {
        activeLoanCount++;
      }
    }

    if (activeLoanCount >= member.getMaxBorrow()) {
      System.out.println("Gagal: Batas peminjaman tercapai.");
      return;
    }
    if (!book.decreaseStock()) {
      System.out.println("Gagal: Stok habis.");
      return;
    }

    Loan newLoan = new Loan(member, book, borrowDate);
    loans.add(newLoan);
    saveLoans();
    saveBooks();

    System.out.println("Sukses: " + member.getName() + " meminjam " + book.getTitle());
  }

  public void returnBook(String memberId, String bookCode, LocalDate returnDate) {
    Member member = findMemberById(memberId);
    if (member == null) {
      System.out.println("Gagal: Member tidak ditemukan.");
      return;
    }

    Loan activeLoan = null;
    for (Loan loan : loans) {
      if (loan.getMember().getId().equals(memberId) &&
          loan.getBook().getBookCode().equals(bookCode) &&
          !loan.isReturned()) {
        activeLoan = loan;
        break;
      }
    }

    if (activeLoan == null) {
      System.out.println("Gagal: Peminjaman tidak ditemukan.");
      return;
    }

    activeLoan.completeLoan(returnDate);
    activeLoan.getBook().increaseStock();
    saveLoans();
    saveBooks();

    System.out.println("Buku dikembalikan.");
    if (activeLoan.getFine() > 0) {
      System.out.println("Denda: Rp " + activeLoan.getFine());
    }
  }

  public void printLibraryStatus() {
    System.out.println("\n--- Status Perpustakaan ---");
    for (Book b : books) {
      System.out.println(b.getBookCode() + " | " + b.getTitle() + " | Stok: " + b.getStock());
    }
    for (Member m : members) {
      int aktif = 0;
      for (Loan l : loans) {
        if (l.getMember().getId().equals(m.getId()) && !l.isReturned())
          aktif++;
      }
      System.out.println(m.getId() + " | " + m.getName() + " | Pinjam: " + aktif + "/" + m.getMaxBorrow());
    }
  }
}
