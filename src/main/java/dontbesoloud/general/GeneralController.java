package dontbesoloud.general;

import dontbesoloud.Main;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.*;

public class GeneralController {

    @FXML
    private FlowPane groupsContainer; // Groups container


    @FXML
    public void initialize() {
        loadGroupsFromServer();

    }


    private void loadGroupsFromServer() {
    }

    private void addGroupToUI(String title, double progress) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dontbesoloud/general/group.fxml"));
            Node groupNode = loader.load();

            GroupController controller = loader.getController();
            controller.setData(1, title, progress, null, this);

            groupsContainer.getChildren().addFirst(groupNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private VBox modalOverlay;
    @FXML
    private TextField groupNameField;
    @FXML
    private ImageView groupImage;
    private boolean isEditMode;
    private GroupController groupController;

    @FXML
    private void showGroupModal() {
        modalOverlay.setVisible(true);
    }

    @FXML
    private void addNewGroup() {
        isEditMode = false;
        groupNameField.clear();
        groupImage.setImage(null);
        showGroupModal();
    }

    public void editGroup() {
        showGroupModal();
    }

    @FXML
    private void сhooseImage() {
    }

    @FXML
    private void deleteGroup() {
        modalOverlay.setVisible(false);
    }

    @FXML
    private void saveGroup() {
        modalOverlay.setVisible(false);
        try {
            Main.loadPage("/dontbesoloud/general/lessons.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}