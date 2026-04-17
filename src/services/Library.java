package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.Book;
import models.Loan;
import models.Member;

public class Library {
  private List<Book> books;
  private List<Member> members;
  private List<Loan> loans;

  public Library() {
    this.books = new ArrayList<>();
    this.members = new ArrayList<>();
    this.loans = new ArrayList<>();
  }

  public void addBook(Book book) {
    books.add(book);
  }

  public void addMember(Member member) {
    members.add(member);
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

    // Cek batas pinjaman berdasarkan aturan NIM
    if (member.getActiveLoans().size() >= member.getMaxBorrow()) {
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
    member.getActiveLoans().add(newLoan);

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
    for (Loan loan : member.getActiveLoans()) {
      if (loan.getBook().getBookCode().equals(bookCode) && !loan.isReturned()) {
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
    member.getActiveLoans().remove(activeLoanToReturn);

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
      System.out.println("- " + member.getId() + " | " + member.getName() + " | NIM: " + member.getNim()
          + " | Max Pinjam: " + member.getMaxBorrow() + " | Sedang dipinjam: " + member.getActiveLoans().size());
    }
    System.out.println("---------------------------\n");
  }
}
