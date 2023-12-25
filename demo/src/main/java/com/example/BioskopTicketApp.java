package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.example.Others.Film;
import com.example.Others.Receipt;


import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class BioskopTicketApp extends Application {

    private String pembeli;
    private String jamPenayangan;
    private Film film;
    private Set<Integer> kursiRandomTerisi = new HashSet<>();
    private Set<Integer> kursiPilihanUser = new HashSet<>();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Pemesanan Tiket Bioskop");

        // Create a list of Film objects
        List<Film> films = new ArrayList<>();
        films.add(new Film("Kimi No Nawa", "/com/example/IMG/kimi_no_nawa_poster.jpg"));
        films.add(new Film("Spiderman Home Coming", "/com/example/IMG/spi.jpg"));
        films.add(new Film("Spirited Away", "/com/example/IMG/spirited_away_poster.jpg"));

        // Layout GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Nama Pembeli
        TextField pembeliField = new TextField();
        pembeliField.setPromptText("Nama Pembeli");
        gridPane.add(new Label("Nama Pembeli:"), 0, 0);
        gridPane.add(pembeliField, 1, 0);

        // Jam Penayangan
        ComboBox<String> jamPenayanganComboBox = new ComboBox<>();
        jamPenayanganComboBox.getItems().addAll("12:00", "15:00", "18:00", "21:00");
        jamPenayanganComboBox.setPromptText("Pilih");
        gridPane.add(new Label("Jam Penayangan:"), 0, 1);
        gridPane.add(jamPenayanganComboBox, 1, 1);

        ComboBox<Film> filmComboBox = new ComboBox<>();
        filmComboBox.getItems().addAll(films);
        filmComboBox.setPromptText("Pilih");
        gridPane.add(new Label("Nama Film:"), 0, 2);
        gridPane.add(filmComboBox, 1, 2);

        // Tombol Lihat Kursi Tersedia
        Button lihatKursiButton = new Button("Lihat Kursi Tersedia");
        gridPane.add(lihatKursiButton, 1, 3);

        // Event Handler untuk Tombol Lihat Kursi Tersedia
        lihatKursiButton.setOnAction(event -> {
            try {
                validateInput(pembeliField, jamPenayanganComboBox, filmComboBox);
                pembeli = pembeliField.getText();
                jamPenayangan = jamPenayanganComboBox.getValue();
                film = filmComboBox.getValue();
        
                // Generate kursi terisi secara acak
                generateRandomSeats();
        
                // Tampilkan Scene Pemilihan Kursi
                showSeatSelection(primaryStage, film, jamPenayangan);
            } catch (InputValidationException e) {
                showAlert("Error", e.getMessage());
            }
        });
        

        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

// Fungsi untuk menampilkan Scene Pemilihan Kursi
private void showSeatSelection(Stage primaryStage, Film selectedFilm, String jamPenayangan) {
    GridPane seatGridPane = new GridPane();
    seatGridPane.setPadding(new Insets(20, 20, 20, 20));
    seatGridPane.setVgap(10);
    seatGridPane.setHgap(10);

    // Create CheckBox controls for each seat
    int rowCount = 0;
    int colCount = 0;

    for (int i = 1; i <= 80; i++) {
        CheckBox checkBox = new CheckBox("Kursi " + i);

        // Jika kursi terisi secara acak, set disable dan selected
        if (kursiRandomTerisi.contains(i)) {
            checkBox.setDisable(true);
            checkBox.setSelected(true);
        }

        seatGridPane.add(checkBox, colCount, rowCount);

        // Event Handler untuk pemilihan kursi
        int kursiNumber = i;
        checkBox.setOnAction(event -> {
            if (!checkBox.isDisabled()) {
                if (checkBox.isSelected()) {
                    kursiPilihanUser.add(kursiNumber);
                } else {
                    kursiPilihanUser.remove(Integer.valueOf(kursiNumber));
                }
            }
        });

        colCount++;
        if (colCount == 20) {
            colCount = 0;
            rowCount++;
        }
    }

// Get the selected film's poster path
String posterPath = selectedFilm.getPosterPath();
Image moviePoster = new Image(getClass().getResource(posterPath).toExternalForm());

// ImageView for the movie poster
ImageView posterImageView = new ImageView(moviePoster);
posterImageView.setFitWidth(400); // Set the width of the poster
posterImageView.setFitHeight(200); // Set the height of the poster
posterImageView.setPreserveRatio(true);

// Add the poster below the seat selection grid
seatGridPane.add(posterImageView, 0, rowCount + 2, 20, 1);

// Display Film Title
Label filmTitleLabel = new Label("Film: " + selectedFilm.getName());
seatGridPane.add(filmTitleLabel, 0, rowCount + 3, 20, 1);

// Display Jam Penayangan
Label jamPenayanganLabel = new Label("Jam Penayangan: " + jamPenayangan);
seatGridPane.add(jamPenayanganLabel, 0, rowCount + 4, 20, 1);

    // Tombol Pesan
    Button pesanButton = new Button("Pesan");
    seatGridPane.add(pesanButton, 10, rowCount + 5);

    // Event Handler untuk Tombol Pesan
    pesanButton.setOnAction(event -> showNota(primaryStage));

    Scene seatSelectionScene = new Scene(seatGridPane, 600, 800); // Adjusted height to accommodate additional information
    primaryStage.setScene(seatSelectionScene);
}

    // Fungsi untuk menampilkan Nota

    private void showNota(Stage primaryStage) {
    // Extract relevant information from the Film object
    String filmTitle = film.getName(); // Use the getName() method

    // Create the Receipt object
    Receipt receipt = new Receipt(pembeli, jamPenayangan, filmTitle, kursiPilihanUser);
    String notaText = receipt.generateReceipt();

    // Write receipt to a text file
    writeReceiptToFile(notaText);

        TextArea notaTextArea = new TextArea(notaText);
        notaTextArea.setEditable(false);

        // Tampilkan nota dalam dialog
        Dialog<String> notaDialog = new Dialog<>();
        notaDialog.setTitle("Nota Pemesanan");
        notaDialog.setHeaderText(null);
        notaDialog.getDialogPane().setContent(notaTextArea);

        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        notaDialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        notaDialog.showAndWait();

        // Kembali ke Scene utama
        start(primaryStage);
    }

    private void writeReceiptToFile(String notaText) {
        try {
            // If the file doesn't exist, create it
            if (!Files.exists(Paths.get("receipt.txt"))) {
                Files.createFile(Paths.get("receipt.txt"));
            }
    
            // Append the new receipt content to the file
            Files.write(Paths.get("receipt.txt"), notaText.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    // Fungsi untuk menghasilkan kursi terisi secara acak
    private void generateRandomSeats() {
        kursiRandomTerisi.clear();
        Random random = new Random();

        // Menentukan jumlah kursi terisi secara acak (sekitar 3/4 dari total kursi)
        int jumlahKursiTerisi = (int) (80 * 0.75);

        // Menentukan kursi terisi secara acak
        for (int i = 0; i < jumlahKursiTerisi; i++) {
            int kursiNumber = random.nextInt(80) + 1;
            kursiRandomTerisi.add(kursiNumber);
        }
    }

    // Fungsi untuk validasi input
    private void validateInput(TextField pembeliField, ComboBox<String> jamPenayanganComboBox, ComboBox<Film> filmComboBox) throws InputValidationException {
        if (pembeliField.getText().isEmpty() || jamPenayanganComboBox.getValue() == null || filmComboBox.getValue() == null) {
            throw new InputValidationException("Mohon lengkapi semua informasi sebelum melihat kursi tersedia.");
        }
    }


    // Exception untuk validasi input
    private static class InputValidationException extends Exception {
        public InputValidationException(String message) {
            super(message);
        }
    }

    // Fungsi untuk menampilkan alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void setRoot(String string) {
    }

    
}
