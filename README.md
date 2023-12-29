
README Aplikasi Pemesanan Tiket Bioskop
Penulis: Elang Dwi Setiawan Diqlas

Deskripsi
Aplikasi ini adalah sistem pemesanan tiket bioskop sederhana yang memungkinkan pengguna untuk pesan tiket, memilih kursi, film, dan jam penayangan. Aplikasi ini dibangun menggunakan JavaFX dan menyediakan UI pengguna yang intuitif.

Cara Menjalankan Aplikasi
Pastikan Anda memiliki Java Development Kit (JDK) terinstal di komputer Anda.
Clone atau unduh repositori ini ke dalam sistem Anda.
Buka proyek menggunakan Integrated Development Environment (IDE) yang mendukung Java, seperti IntelliJ IDEA atau Eclipse.
Jalankan kelas BioskopTicketApp yang berisi metode BioskopTicketApp.java.
Fitur Utama
1. Pemesanan Tiket
Pengguna dapat memasukkan nama pembeli, memilih jam penayangan, dan memilih film.
Setelah memasukkan informasi, pengguna dapat melihat kursi yang tersedia.
2. Melihat Kursi Tersedia
Pengguna dapat melihat kursi yang tersedia untuk film dan jam penayangan tertentu.
Kursi yang sudah dipesan akan ditandai dan tidak dapat dipilih.
3. Pembayaran
Pengguna dapat memilih kursi dan melanjutkan ke proses pembayaran.
Aplikasi akan menampilkan total biaya dan meminta pengguna memasukkan jumlah pembayaran.
4. Nota Pemesanan
Setelah pembayaran berhasil, aplikasi akan menampilkan nota pemesanan yang berisi informasi pembeli, film, jam penayangan, dan kursi terpilih.

Struktur Proyek
BioskopTicketApp: class utama yang menginisialisasi aplikasi.
Receipt: class untuk merepresentasikan nota pemesanan dan pengambilan data.
Film: class untuk merepresentasikan informasi film nama dan poster.
PaymentManager: Kelas untuk memproses pembayaran.
Catatan Penting
Pastikan untuk menyimpan data pemesanan ke dalam file data_pemesanan.txt untuk mempertahankan data saat aplikasi ditutup.

Catatan: Pastikan Anda telah mengonfigurasi direktori file dengan benar sesuai dengan kebutuhan aplikasi.