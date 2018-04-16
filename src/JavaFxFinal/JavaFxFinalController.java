package JavaFxFinal;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class JavaFxFinalController implements Initializable {

    @FXML
    private TextField txtItemName;
    @FXML
    private Button btnSave;
    @FXML
    private ListView<String> listItem;
    @FXML
    private AnchorPane pane;

    private boolean editMode = false;
    private int editIndex = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listItem.setPlaceholder(new Label("Please Insert New Data"));

        Platform.runLater(() -> {
            txtItemName.requestFocus();
        });

        // bisa disingkat menjadi lambda expression (karena cuma 1 baris
        // expression, tidak boleh dibungkus dengan { } dan tidak boleh pakai ;
        // Platform.runLater(() -> txtNama.requestFocus());
        // atau bisa disingkat dengan mengirimkan method reference, karena
        // isi lambda cuma akan menjalankan method requestFocus dari txtNama
        //Platform.runLater(txtNama::requestFocus);
        // code jika menggunakan java <= 7 yang belum memiliki lambda
        /* Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtNama.requestFocus();
            }
        }); */
        // atau buat inner class dan kemudian instantiate
        //Platform.runLater(new X());
        btnSave.setDefaultButton(true);
        listItem.setOnKeyPressed((event) -> this.onListViewKeyPressed(event));
        listItem.setOnMouseClicked((event) -> this.onListViewMouseClicked(event));

        // process event ketika event pertama kali di-capture (memberikan kesempatan kepada parent untuk meng-handle
        // event terlebih dahulu sebelum child)
        pane.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> this.onKeyEscapePressed(event));
    }

    private void onKeyEscapePressed(KeyEvent event) {
        // cancel edit jika user menekan tombol esc dan aplikasi dalam mode editMode
        if (event.getCode().equals(KeyCode.ESCAPE) && editMode) {
            cancelEdit();
        }
    }

    // inner class
    /* private class X implements Runnable {
        public void run() {
            txtNama.requestFocus();
        }
    } */
    private void cancelEdit() {
        if (editMode) {
            disableEditMode();
            txtItemName.clear();
            txtItemName.requestFocus();
            messageBox("Edit", "Edit Canceled");
        }
    }

    public void onListViewMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (listItem.getSelectionModel().getSelectedIndex() >= 0) {
                enableEditMode(listItem.getSelectionModel().getSelectedIndex());
                txtItemName.setText(listItem.getSelectionModel().getSelectedItem());
                txtItemName.requestFocus();
            }
        }
    }

    private void enableEditMode(int index) {
        this.editMode = true;
        this.editIndex = index;
    }

    private void disableEditMode() {
        this.editMode = false;
        this.editIndex = -1;
    }

    public void onListViewKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DELETE) || event.getCode().equals(KeyCode.BACK_SPACE)) {
            int index = listItem.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                String data = listItem.getItems().get(index);
                if (confirm("Delete", "Delete " + data + "?")
                        .orElse(ButtonType.CANCEL).equals(ButtonType.OK)) {
                    deleteItem(index);
                }
            }
        }
    }

    private Optional<ButtonType> confirm(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setContentText(message);
        Optional<ButtonType> result = confirm.showAndWait();
        return result;
    }

    private void deleteItem(int index) {
        if (index >= 0 && index < listItem.getItems().size()) {
            listItem.getItems().remove(index);
        }
    }

    private void addItem(String newItem) {
        listItem.getItems().add(newItem);
    }

    private void updateItem(int index, String updatedItem) {
        listItem.getItems().set(index, updatedItem);
    }

    @FXML
    public void onBtnSaveClick(ActionEvent event) {
        String nama = txtItemName.getText().trim();

        if (!nama.isEmpty()) {
            if (editMode) {
                updateItem(editIndex, txtItemName.getText());
                disableEditMode();
            } else {
                addItem(txtItemName.getText());
            }
            txtItemName.clear();
            txtItemName.requestFocus();
        } else {
            messageBox("Warning", "Item Name Must Have Value");
        }
    }

    public void messageBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
