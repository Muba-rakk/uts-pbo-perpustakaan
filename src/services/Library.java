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

  // Lokasi file JSON untuk penyimpanan data
  private static final String DATA_DIR = "data";
  private static final String BOOKS_FILE = DATA_DIR + "/books.json";
  private static final String MEMBERS_FILE = DATA_DIR + "/members.json";
  private static final String LOANS_FILE = DATA_DIR + "/loans.json";

  // Konstruktor - load data dari JSON saat inisialisasi
  public Library() {
    new File(DATA_DIR).mkdirs(); // Buat folder data jika belum ada
    this.books = loadBooks();
    this.members = loadMembers();
    this.loans = loadLoans();
  }

  // Load data buku dari file JSON
  private List<Book> loadBooks() {
    Type type = new TypeToken<List<Book>>() {
    }.getType();
    return JsonHelper.loadListFromJson(BOOKS_FILE, type);
  }

  // Load data member dari file JSON
  private List<Member> loadMembers() {
    Type type = new TypeToken<List<Member>>() {
    }.getType();
    return JsonHelper.loadListFromJson(MEMBERS_FILE, type);
  }

  // Load data peminjaman dari file JSON
  private List<Loan> loadLoans() {
    Type type = new TypeToken<List<Loan>>() {
    }.getType();
    return JsonHelper.loadListFromJson(LOANS_FILE, type);
  }

  // Simpan data buku ke file JSON
  private void saveBooks() {
    JsonHelper.saveListToJson(books, BOOKS_FILE);
  }

  // Simpan data member ke file JSON
  private void saveMembers() {
    JsonHelper.saveListToJson(members, MEMBERS_FILE);
  }

  // Simpan data peminjaman ke file JSON
  private void saveLoans() {
    JsonHelper.saveListToJson(loans, LOANS_FILE);
  }

  // Tambah buku baru ke koleksi
  public void addBook(Book book) {
    books.add(book);
    saveBooks();
  }

  // Tambah member baru ke sistem
  public void addMember(Member member) {
    members.add(member);
    saveMembers();
  }

  // Cek apakah daftar buku kosong
  public boolean isBooksEmpty() {
    return books.isEmpty();
  }

  // Cek apakah daftar member kosong
  public boolean isMembersEmpty() {
    return members.isEmpty();
  }

  // Cari member berdasarkan ID
  private Member findMemberById(String id) {
    for (Member m : members) {
      if (m.getId().equals(id))
        return m;
    }
    return null;
  }

  // Cari buku berdasarkan kode
  private Book findBookByCode(String code) {
    for (Book b : books) {
      if (b.getBookCode().equals(code))
        return b;
    }
    return null;
  }

  // Proses peminjaman buku
  public void borrowBook(String memberId, String bookCode, LocalDate borrowDate) {
    // Validasi member dan buku ada
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

    // Hitung jumlah buku yang sedang dipinjam
    int activeLoanCount = 0;
    for (Loan loan : loans) {
      if (loan.getMember().getId().equals(memberId) && !loan.isReturned()) {
        activeLoanCount++;
      }
    }

    // Cek batas peminjaman berdasarkan NIM
    if (activeLoanCount >= member.getMaxBorrow()) {
      System.out.println("Gagal: Batas peminjaman tercapai.");
      return;
    }
    // Cek stok tersedia
    if (!book.decreaseStock()) {
      System.out.println("Gagal: Stok habis.");
      return;
    }

    // Buat transaksi baru dan simpan
    Loan newLoan = new Loan(member, book, borrowDate);
    loans.add(newLoan);
    saveLoans();
    saveBooks();

    System.out.println("Sukses: " + member.getName() + " meminjam " + book.getTitle());
  }

  // Proses pengembalian buku
  public void returnBook(String memberId, String bookCode, LocalDate returnDate) {
    // Validasi member ada
    Member member = findMemberById(memberId);
    if (member == null) {
      System.out.println("Gagal: Member tidak ditemukan.");
      return;
    }

    // Cari transaksi peminjaman yang aktif
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

    // Proses pengembalian dan hitung denda jika telat
    activeLoan.completeLoan(returnDate);
    activeLoan.getBook().increaseStock();
    saveLoans();
    saveBooks();

    System.out.println("Buku dikembalikan.");
    if (activeLoan.getFine() > 0) {
      System.out.println("Denda: Rp " + activeLoan.getFine());
    }
  }

  // Tampilkan semua data buku dan member
  public void printLibraryStatus() {
    System.out.println("\n--- Status Perpustakaan ---");
    // Tampilkan daftar buku
    for (Book b : books) {
      System.out.println(b.getBookCode() + " | " + b.getTitle() + " | Stok: " + b.getStock());
    }
    // Tampilkan daftar member dan jumlah peminjaman aktif
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
