package main;

import java.time.LocalDate;
import java.util.Scanner;

import models.Book;
import models.Member;
import services.Library;

public class Main {
  public static void main(String[] args) {
    System.out.println("=== Sistem Manajemen Perpustakaan ===");

    Library library = new Library();
    Scanner scanner = new Scanner(System.in);

    // Load atau seed data
    if (library.isBooksEmpty() || library.isMembersEmpty()) {
      seedData(library);
    }

    // Menu Utama
    int pilihan;
    do {
      System.out.println("\n--- Menu ---");
      System.out.println("1. Tambah Buku");
      System.out.println("2. Tambah Member");
      System.out.println("3. Peminjaman Buku");
      System.out.println("4. Pengembalian Buku");
      System.out.println("5. Lihat Status");
      System.out.println("0. Keluar");
      System.out.print("Pilih: ");
      pilihan = scanner.nextInt();
      scanner.nextLine();

      switch (pilihan) {
        case 1:
          tambahBuku(scanner, library);
          break;
        case 2:
          tambahMember(scanner, library);
          break;
        case 3:
          peminjaman(scanner, library);
          break;
        case 4:
          pengembalian(scanner, library);
          break;
        case 5:
          library.printLibraryStatus();
          break;
      }
    } while (pilihan != 0);

    System.out.println("Terima kasih!");
  }

  private static void seedData(Library library) {
    library.addBook(new Book("B-001", "Pemrograman Java", "John Doe", "Teknologi", 3));
    library.addBook(new Book("B-002", "Algoritma dan Struktur Data", "Jane Smith", "Teknologi", 1));
    library.addBook(new Book("B-003", "Desain Database", "Bob Brown", "Teknologi", 0));

    library.addMember(new Member("Andi", "M-001", "20230001"));
    library.addMember(new Member("Siti", "M-002", "20230005"));
    library.addMember(new Member("Agus", "M-003", "20230008"));
  }

  private static void tambahBuku(Scanner scanner, Library library) {
    System.out.print("Kode Buku: ");
    String kode = scanner.nextLine();
    System.out.print("Judul: ");
    String judul = scanner.nextLine();
    System.out.print("Penulis: ");
    String penulis = scanner.nextLine();
    System.out.print("Kategori: ");
    String kategori = scanner.nextLine();
    System.out.print("Stok: ");
    int stok = scanner.nextInt();
    scanner.nextLine();

    library.addBook(new Book(kode, judul, penulis, kategori, stok));
  }

  private static void tambahMember(Scanner scanner, Library library) {
    System.out.print("Nama: ");
    String nama = scanner.nextLine();
    System.out.print("ID Member: ");
    String id = scanner.nextLine();
    System.out.print("NIM: ");
    String nim = scanner.nextLine();

    library.addMember(new Member(nama, id, nim));
  }

  private static void peminjaman(Scanner scanner, Library library) {
    System.out.print("ID Member: ");
    String memberId = scanner.nextLine();
    System.out.print("Kode Buku: ");
    String bookCode = scanner.nextLine();

    library.borrowBook(memberId, bookCode, LocalDate.now());
  }

  private static void pengembalian(Scanner scanner, Library library) {
    System.out.print("ID Member: ");
    String memberId = scanner.nextLine();
    System.out.print("Kode Buku: ");
    String bookCode = scanner.nextLine();

    library.returnBook(memberId, bookCode, LocalDate.now());
  }
}
