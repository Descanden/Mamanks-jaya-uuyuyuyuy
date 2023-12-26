package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.Others.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;

public class BioskopTicketApp extends Application {

    private Stage primaryStage;
    private String pembeli;
    private String jamPenayangan;
    private Film film;
    private List<Receipt> daftarPemesanan = new ArrayList<>();
    private Set<Integer> kursiRandomTerisi = new HashSet<>();
    private Set<Integer> kursiPilihanUser = new HashSet<>();
    private TableView<Receipt> tableView = new TableView<>();
    private Tab inputTab;
    private Label inputDetailsLabel = new Label();

    // Declare UI components at the class level
    private TextField pembeliField;
    private ComboBox<String> jamPenayanganComboBox;
    private ComboBox<Film> filmComboBox;
    private GridPane seatGridPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Aplikasi Pemesanan Tiket Bioskop");

        TabPane tabPane = new TabPane();
        inputTab = createInputTab();
        Tab dataTab = createDataTab();

        tabPane.getTabs().addAll(inputTab, dataTab);

        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createDataTab() {
        Tab dataTab = new Tab("Hasil Data");
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        TableColumn<Receipt, String> namaCol = new TableColumn<>("Nama");
        namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));

        TableColumn<Receipt, String> jamPenayanganCol = new TableColumn<>("Jam Penayangan");
        jamPenayanganCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJamPenayangan()));

        TableColumn<Receipt, String> filmCol = new TableColumn<>("Film");
        filmCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFilm()));

        TableColumn<Receipt, String> kursiCol = new TableColumn<>("Kursi Terpilih");
        kursiCol.setCellValueFactory(cellData -> {
            Set<Integer> kursiPilihanUser = cellData.getValue().getKursiPilihanUser(); // Use the correct method
            return new SimpleStringProperty(kursiPilihanUser.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        });

        tableView.getColumns().addAll(namaCol, jamPenayanganCol, filmCol, kursiCol);
        tableView.setItems(FXCollections.observableArrayList(daftarPemesanan));

        Label inputDetailsLabel = new Label("Input Details:\n");

        vbox.getChildren().addAll(inputDetailsLabel, tableView);
        dataTab.setContent(vbox);

        return dataTab;
    }
    

    private Tab createInputTab() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        pembeliField = new TextField();
        pembeliField.setPromptText("Nama Pembeli");
        gridPane.add(new Label("Nama Pembeli:"), 0, 0);
        gridPane.add(pembeliField, 1, 0);

        jamPenayanganComboBox = new ComboBox<>();
        jamPenayanganComboBox.getItems().addAll("12:00", "15:00", "18:00", "21:00");
        jamPenayanganComboBox.setPromptText("Pilih");
        gridPane.add(new Label("Jam Penayangan:"), 0, 1);
        gridPane.add(jamPenayanganComboBox, 1, 1);

        filmComboBox = new ComboBox<>();
        filmComboBox.getItems().addAll(
                new Film("Kimi No Nawa", "/com/example/IMG/kimi_no_nawa_poster.jpg"),
                new Film("Spiderman Home Coming", "/com/example/IMG/spi.jpg"),
                new Film("Spirited Away", "/com/example/IMG/spirited_away_poster.jpg"));
        filmComboBox.setPromptText("Pilih");
        gridPane.add(new Label("Nama Film:"), 0, 2);
        gridPane.add(filmComboBox, 1, 2);

        Button lihatKursiButton = new Button("Lihat Kursi Tersedia");
        gridPane.add(lihatKursiButton, 1, 3);

        lihatKursiButton.setOnAction(event -> {
            try {
                validateInput(pembeliField, jamPenayanganComboBox, filmComboBox);
                pembeli = pembeliField.getText();
                jamPenayangan = jamPenayanganComboBox.getValue();
                film = filmComboBox.getValue();
        
                generateRandomSeats();
        
                showSeatSelection();
            } catch (InputValidationException e) {
                showAlert("Error", e.getMessage());
            }
        });

        Tab inputTab = new Tab("Input Data");
        inputTab.setContent(gridPane);

        return inputTab;
    }

    private void showSeatSelection() {
        seatGridPane = new GridPane();
        seatGridPane.setPadding(new Insets(20, 20, 20, 20));
        seatGridPane.setVgap(10);
        seatGridPane.setHgap(10);

        int rowCount = 0;
        int colCount = 0;

        for (int i = 1; i <= 80; i++) {
            CheckBox checkBox = new CheckBox(String.valueOf(i));
            checkBox.setPrefWidth(60);
            checkBox.setPadding(new Insets(5));

            if (kursiRandomTerisi.contains(i)) {
                checkBox.setDisable(true);
                checkBox.setSelected(true);
            } else if (kursiPilihanUser.contains(i)) {
                checkBox.setDisable(true);
            }

            seatGridPane.add(checkBox, colCount, rowCount);

            int kursiNumber = i;
            checkBox.setOnAction(event -> {
                if (!checkBox.isDisabled()) {
                    if (checkBox.isSelected()) {
                        if (kursiPilihanUser.size() >= 3) {
                            showAlert("Info", "Anda hanya dapat memesan maksimal 3 kursi.");
                            checkBox.setSelected(false);
                        } else {
                            kursiPilihanUser.add(kursiNumber);
                        }
                    } else {
                        kursiPilihanUser.remove(Integer.valueOf(kursiNumber));
                    }
                }
            });

            colCount++;
            if (colCount == 10) {
                colCount = 0;
                rowCount++;
            }
        }

        Button pesanButton = new Button("Pesan");
        GridPane.setMargin(pesanButton, new Insets(10, 0, 0, 0));
        seatGridPane.add(pesanButton, 5, rowCount + 1);

        pesanButton.setOnAction(event -> {
            showNotaAndRecordPurchase();
            resetUI();
        });
    

        Scene seatSelectionScene = new Scene(seatGridPane, 600, 400);
        primaryStage.setScene(seatSelectionScene);
    }

    private void showNotaAndRecordPurchase() {
        StringBuilder nota = new StringBuilder();
        nota.append("===== Nota Pemesanan =====\n");
        nota.append("Nama Pembeli: ").append(pembeli).append("\n");
        nota.append("Jam Penayangan: ").append(jamPenayangan).append("\n");
        nota.append("Nama Film: ").append(film.getName()).append("\n");
        nota.append("Kursi Terpilih: ");

        for (int kursi : kursiPilihanUser) {
            nota.append("Kursi ").append(kursi).append(", ");
        }
        if (!kursiPilihanUser.isEmpty()) {
            nota.delete(nota.length() - 2, nota.length());
        }

        showNotaAndRecordPurchase(nota.toString());
    }

    private void showNotaAndRecordPurchase(String nota) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nota Pemesanan");
        alert.setHeaderText(null);
        alert.setContentText(nota);
        alert.showAndWait();

        Receipt receipt = new Receipt(pembeli, jamPenayangan, film.getName(), kursiPilihanUser);
        daftarPemesanan.add(receipt);

        tableView.setItems(FXCollections.observableArrayList(daftarPemesanan));

        inputDetailsLabel.setText("Input Details:\nNama Pembeli: " + pembeli +
                "\nJam Penayangan: " + jamPenayangan + "\nNama Film: " + film.getName());

        primaryStage.setScene(inputTab.getContent().getScene());
        clearInputFields();
    }

    private void clearInputFields() {
        pembeli = "";
        jamPenayangan = "";
        film = null;
        kursiPilihanUser.clear();
    }

    private void validateInput(TextField pembeliField, ComboBox<String> jamPenayanganComboBox, ComboBox<Film> filmComboBox) throws InputValidationException {
        if (pembeliField.getText().isEmpty() || jamPenayanganComboBox.getValue() == null || filmComboBox.getValue() == null) {
            throw new InputValidationException("Mohon lengkapi semua informasi sebelum melihat kursi tersedia.");
        }
    }

    private void generateRandomSeats() {
        kursiRandomTerisi.clear();
        Random random = new Random();
        int jumlahKursiTerisi = (int) (80 * 0.75);

        for (int i = 0; i < jumlahKursiTerisi; i++) {
            int kursiNumber = random.nextInt(80) + 1;
            kursiRandomTerisi.add(kursiNumber);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class InputValidationException extends Exception {
        public InputValidationException(String message) {
            super(message);
        }
    }

    private void resetUI() {
        pembeliField.clear();
        jamPenayanganComboBox.getSelectionModel().clearSelection();
        filmComboBox.getSelectionModel().clearSelection();

        for (Node node : seatGridPane.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setSelected(false);
                checkBox.setDisable(false);
            }
        }
    }

    public static void setRoot(String string) {
    }
}
