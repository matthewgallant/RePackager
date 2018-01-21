package repackager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    MenuBar mainMenu;

    @FXML
    public void draggingOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    @FXML
    public void dropping(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            String filePath = null;
            for (File file:db.getFiles()) {
                createFolder(file.getName());
                copyApplication(file);
                setPermissions(file);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Installed Application");
                alert.setContentText("The Application has Been Installed Successfully!");
                alert.showAndWait();
                System.out.println("Applications successfully repackaged!");
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    public void setPermissions(File application) {
        System.out.println("Making application executable.");
        File dir = new File(System.getProperty("user.home") + "/Applications/" + application.getName() + "/Contents/MacOS/");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                child.setExecutable(true);
            }
        }
    }

    public void copyApplication(File applicationFolder) {
        File srcDir = new File(applicationFolder.getAbsolutePath() + "/");
        File destDir = new File(System.getProperty("user.home") + "/Applications/" + applicationFolder.getName() + "/");
        System.out.println("Trying to copy files...");
        try {
            FileUtils.copyDirectory(srcDir, destDir);
            System.out.println("Files successfully copied!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error copying files.");
        }
    }

    public void createFolder(String folder) {
        File theDir = new File(System.getProperty("user.home") + "/Applications/" + folder);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("Creating Application: " + theDir.getAbsolutePath());
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                System.out.print("An unexpected error has occurred while creating the folder.");
            }
            if(result) {
                System.out.println("Directory creation successful!");
            }
        }
    }

    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About RePackager");
        alert.setHeaderText("RePackager 1.0");
        alert.setContentText("A simple utility to install non-codesigned macOS applications without admin access.");
        alert.showAndWait();
    }

    public void quit() {
        System.exit(0);
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open macOS Application");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("macOS Application", "*.app")
        );
        File file = fileChooser.showOpenDialog(null);
        createFolder(file.getName());
        copyApplication(file);
        setPermissions(file);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Installed Application");
        alert.setContentText("The Application has Been Installed Successfully!");
        alert.showAndWait();
        System.out.println("Applications successfully repackaged!");
    }

}
