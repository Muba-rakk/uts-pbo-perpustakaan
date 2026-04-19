package main;

import java.time.LocalDate;
import java.util.Scanner;

import models.Book;
import models.Member;
import services.Library;

// Entry point program
public class Main {
  public static void main(String[] args) {
    System.out.println("=== Sistem Manajemen Perpustakaan ===");

    // Inisialisasi library dan scanner
    Library library = new Library();
    Scanner scanner = new Scanner(System.in);

    // Load data dari JSON, jika kosong lakukan seeding data awal
    if (library.isBooksEmpty() || library.isMembersEmpty()) {
      seedData(library);
    }

    // Menu Utama - loop sampai user memilih 0 (keluar)
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

      // Proses pilihan menu
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
        default:
          if (pilihan != 0) {
            System.out.println("Menu yang anda pilih invalid, pilih 0-5.");
          }
          break;
      }
    } while (pilihan != 0);

    System.out.println("Terima kasih!");
  }

  // Menambahkan data dummy untuk inisialisasi pertama kali
  private static void seedData(Library library) {
    library.addBook(new Book("IF-001", "Pemrograman Java", "Winata Mubarak", "Teknologi dan Komputer", 3));
    library.addBook(new Book("IF-002", "Algoritma dan Struktur Data", "John Smith", "Teknologi dan Komputer", 1));
    library.addBook(new Book("iF-003", "Desain Database", "Luffy D Roger", "Teknologi dan Komputer", 5));

    library.addMember(new Member("Andi", "M-001", "20230001"));
    library.addMember(new Member("Siti", "M-002", "20230005"));
    library.addMember(new Member("Agus", "M-003", "20230008"));
  }

  // Input data buku baru dari user
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

  // Input data member baru dari user
  private static void tambahMember(Scanner scanner, Library library) {
    System.out.print("Nama: ");
    String nama = scanner.nextLine();
    System.out.print("ID Member: ");
    String id = scanner.nextLine();
    System.out.print("NIM: ");
    String nim = scanner.nextLine();

    library.addMember(new Member(nama, id, nim));
  }

  // Proses peminjaman buku
  private static void peminjaman(Scanner scanner, Library library) {
    System.out.print("ID Member: ");
    String memberId = scanner.nextLine();
    System.out.print("Kode Buku: ");
    String bookCode = scanner.nextLine();

    library.borrowBook(memberId, bookCode, LocalDate.now());
  }

  // Proses pengembalian buku
  private static void pengembalian(Scanner scanner, Library library) {
    System.out.print("ID Member: ");
    String memberId = scanner.nextLine();
    System.out.print("Kode Buku: ");
    String bookCode = scanner.nextLine();

    library.returnBook(memberId, bookCode, LocalDate.now());
  }
}
