package services;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
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

  // Path untuk file JSON
  private static final String DATA_DIR = "data";
  private static final String BOOKS_FILE = DATA_DIR + "/books.json";
  private static final String MEMBERS_FILE = DATA_DIR + "/members.json";
  private static final String LOANS_FILE = DATA_DIR + "/loans.json";

  public Library() {
    // Inisialisasi folder data jika belum ada
    new File(DATA_DIR).mkdirs();

    // Load data dari JSON saat aplikasi dimulai
    this.books = loadBooks();
    this.members = loadMembers();
    this.loans = loadLoans();

    // Re-link active loans untuk setiap member berdasarkan data loans
    linkActiveLoans();
  }

  // Method Load Data dari JSON

  private List<Book> loadBooks() {
    Type bookListType = new TypeToken<List<Book>>() {
    }.getType();
    return JsonHelper.loadListFromJson(BOOKS_FILE, bookListType);
  }

  private List<Member> loadMembers() {
    Type memberListType = new TypeToken<List<Member>>() {
    }.getType();
    return JsonHelper.loadListFromJson(MEMBERS_FILE, memberListType);
  }

  private List<Loan> loadLoans() {
    Type loanListType = new TypeToken<List<Loan>>() {
    }.getType();
    return JsonHelper.loadListFromJson(LOANS_FILE, loanListType);
  }

  private void linkActiveLoans() {
    // Menghubungkan kembali active loans ke objek Member (agar tidak null)
    for (Member member : members) {
      List<Loan> memberActiveLoans = new ArrayList<>();
      for (Loan loan : loans) {
        if (loan.getMember().getId().equals(member.getId()) && !loan.isReturned()) {
          memberActiveLoans.add(loan);
        }
      }
    }
  }

  // Method Save Data ke JSON
  private void saveBooks() {
    JsonHelper.saveListToJson(books, BOOKS_FILE);
  }

  private void saveMembers() {
    JsonHelper.saveListToJson(members, MEMBERS_FILE);
  }

  private void saveLoans() {
    JsonHelper.saveListToJson(loans, LOANS_FILE);
  }

  // Method Utama
  public void addBook(Book book) {
    books.add(book);
    saveBooks();
  }

  public void addMember(Member member) {
    members.add(member);
    saveMembers();
  }

  private Member findMemberById(String id) {
    for (Member member : members) {
      if (member.getId().equals(id)) {
        return member;
      }
    }
    return null;
  }

  private Book findBookByCode(String code) {
    for (Book book : books) {
      if (book.getBookCode().equals(code)) {
        return book;
      }
    }
    return null;
  }

  public void borrowBook(String memberId, String bookCode, LocalDate borrowDate) {
    Member member = findMemberById(memberId);
    Book book = findBookByCode(bookCode);

    if (member == null) {
      System.out.println("Gagal: Member dengan ID " + memberId + " tidak ditemukan.");
      return;
    }

    if (book == null) {
      System.out.println("Gagal: Buku dengan kode " + bookCode + " tidak ditemukan.");
      return;
    }

    // Hitung jumlah pinjaman aktif dari list loans global
    int activeLoanCount = 0;
    for (Loan loan : loans) {
      if (loan.getMember().getId().equals(memberId) && !loan.isReturned()) {
        activeLoanCount++;
      }
    }

    // Cek batas pinjaman berdasarkan aturan NIM
    if (activeLoanCount >= member.getMaxBorrow()) {
      System.out.println("Gagal: Member " + member.getName() + " telah mencapai batas maksimal peminjaman ("
          + member.getMaxBorrow() + " buku).");
      return;
    }

    // Cek stok buku dan kurangi jika tersedia
    if (!book.decreaseStock()) {
      System.out.println("Gagal: Stok buku '" + book.getTitle() + "' sedang kosong.");
      return;
    }

    // Buat transaksi peminjaman baru
    Loan newLoan = new Loan(member, book, borrowDate);
    loans.add(newLoan);

    // Langsung simpan ke JSON setelah transaksi
    saveLoans();
    saveBooks(); // Perubahan stick buku

    System.out.println("Sukses: " + member.getName() + " berhasil meminjam buku '" + book.getTitle() + "'.");
    System.out.println("Tanggal Jatuh Tempo: " + newLoan.getDueDate());
  }

  public void returnBook(String memberId, String bookCode, LocalDate returnDate) {
    Member member = findMemberById(memberId);

    if (member == null) {
      System.out.println("Gagal: Member dengan ID " + memberId + " tidak ditemukan.");
      return;
    }

    // Cari pinjaman aktif untuk buku ini oleh member ini
    Loan activeLoanToReturn = null;
    for (Loan loan : loans) {
      if (loan.getMember().getId().equals(memberId) &&
          loan.getBook().getBookCode().equals(bookCode) &&
          !loan.isReturned()) {
        activeLoanToReturn = loan;
        break;
      }
    }

    if (activeLoanToReturn == null) {
      System.out.println("Gagal: Tidak ditemukan data peminjaman aktif buku dengan kode " + bookCode + " oleh member "
          + member.getName() + ".");
      return;
    }

    // Proses pengembalian
    activeLoanToReturn.completeLoan(returnDate);
    activeLoanToReturn.getBook().increaseStock();

    // Langsung simpan ke JSON setelah transaksi
    saveLoans();
    saveBooks(); // Perubahan stock buku

    System.out.println("Sukses: Buku '" + activeLoanToReturn.getBook().getTitle() + "' berhasil dikembalikan oleh "
        + member.getName() + ".");

    // Cek apakah ada denda
    if (activeLoanToReturn.getFine() > 0) {
      System.out
          .println("PERINGATAN: Pengembalian terlambat! Denda yang harus dibayar: Rp " + activeLoanToReturn.getFine());
    } else {
      System.out.println("Pengembalian tepat waktu. Tidak ada denda.");
    }
  }

  // Menampilkan status perpustakaan
  public void printLibraryStatus() {
    System.out.println("\n--- Status Perpustakaan ---");
    System.out.println("Daftar Buku:");
    for (Book book : books) {
      System.out.println("- " + book.getBookCode() + " | " + book.getTitle() + " | Stok: " + book.getStock());
    }
    System.out.println("\nDaftar Member:");
    for (Member member : members) {
      // Hitung active loans dari loans list
      int activeCount = 0;
      for (Loan loan : loans) {
        if (loan.getMember().getId().equals(member.getId()) && !loan.isReturned()) {
          activeCount++;
        }
      }
      System.out.println("- " + member.getId() + " | " + member.getName() + " | NIM: " + member.getNim()
          + " | Max Pinjam: " + member.getMaxBorrow() + " | Sedang dipinjam: " + activeCount);
    }
    System.out.println("---------------------------\n");
  }
}
