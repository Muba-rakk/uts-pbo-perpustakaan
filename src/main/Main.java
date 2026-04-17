package main;

import java.time.LocalDate;

import models.Book;
import models.Member;
import models.Librarian;
import services.Library;

public class Main {
  public static void main(String[] args) {
    System.out.println("=== Inisialisasi Sistem Manajemen Perpustakaan ===");

    Library library = new Library();

    // 1. Menambahkan Pustakawan (Demonstrasi Inheritance & Encapsulation)
    Librarian librarian = new Librarian("Budi Santoso", "L-001", "EMP-101", "Kepala Perpustakaan");
    System.out.println("Pustakawan Bertugas: " + librarian.getName() + " - " + librarian.getPosition());

    // 2. Menambahkan Buku
    System.out.println("\nMenambahkan Koleksi Buku...");
    Book book1 = new Book("B-001", "Pemrograman Java", "John Doe", "Teknologi", 3);
    Book book2 = new Book("B-002", "Algoritma dan Struktur Data", "Jane Smith", "Teknologi", 1);
    Book book3 = new Book("B-003", "Desain Database", "Bob Brown", "Teknologi", 0); // Stok kosong untuk testing
    library.addBook(book1);
    library.addBook(book2);
    library.addBook(book3);

    // 3. Menambahkan Member
    System.out.println("Menambahkan Anggota...");
    Member member1 = new Member("Andi", "M-001", "20230001");
    Member member2 = new Member("Siti", "M-002", "20230005");
    Member member3 = new Member("Agus", "M-003", "20230008");

    library.addMember(member1);
    library.addMember(member2);
    library.addMember(member3);

    // Print Status Awal
    library.printLibraryStatus();

    // 4. Simulasi Transaksi Peminjaman
    System.out.println("\n=== Simulasi Peminjaman Buku ===");
    LocalDate today = LocalDate.now();

    // Uji coba: Member1 meminjam buku B-001 dan B-002
    System.out.println("\n-> Andi meminjam 2 buku:");
    library.borrowBook("M-001", "B-001", today);
    library.borrowBook("M-001", "B-002", today);

    System.out.println("\n-> Andi mencoba meminjam buku ke-3:");
    library.borrowBook("M-001", "B-002", today); // Buku bebas apa saja

    System.out.println("\n-> Siti mencoba meminjam buku yang stoknya kosong:");
    library.borrowBook("M-002", "B-003", today);

    System.out.println("\n-> Siti mencoba meminjam buku B-002 (Stok saat ini 0 karena dipinjam Andi):");
    library.borrowBook("M-002", "B-002", today);

    library.printLibraryStatus();

    System.out.println("\n=== Simulasi Pengembalian Buku ===");

    // Tepat waktu (Tidak ada denda)
    System.out.println("\n-> Andi mengembalikan buku B-001 (Tepat Waktu):");
    LocalDate returnDateOntime = today.plusDays(5);
    library.returnBook("M-001", "B-001", returnDateOntime);

    // Terlambat (Terkena denda)
    System.out.println("\n-> Andi mengembalikan buku B-002 (Terlambat 3 hari):");
    LocalDate returnDateLate = today.plusDays(10);
    library.returnBook("M-001", "B-002", returnDateLate);

    library.printLibraryStatus();

    System.out.println("=== Simulasi Selesai ===");
  }
}
