package dontbesoloud.general;

import dontbesoloud.Main;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.*;
import java.util.ArrayList;

public class GeneralController {

    @FXML
    private VBox addGroupLayer;
    @FXML
    private FlowPane groupsContainer; // Groups container
    @FXML
    private Button addGroupButton;
    @FXML
    private TextField groupNameField;
    @FXML
    private ImageView groupImage;

    private ArrayList<Group> groupList;
    private boolean isEditMode;
    private GroupController groupController;
    private GeneralApiService generalApiService = new GeneralApiService();

    @FXML
    public void initialize() {
        groupList = generalApiService.loadGroups();
        addGroupsToPage();
        if (Main.isAdmin()) {
            addGroupButton.setVisible(true);
            addGroupButton.setManaged(true);
        }
    }


    private void addGroupsToPage() {
        groupsContainer.getChildren().removeIf(node -> node != addGroupButton);

        if (groupList == null || groupList.isEmpty()) {
            System.out.println("Список групп пуст.");
            return;
        }
        for (Group group : groupList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dontbesoloud/general/group.fxml"));
                Node groupNode = loader.load();
                GroupController controller = loader.getController();

                double progress = 0.0;
                controller.setData(group.id, group.name, progress, "", this);

                groupsContainer.getChildren().add(groupNode);

            } catch (IOException e) {
                System.err.println("Ошибка при добавлении группы: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

//    private void addGroupToUI(String title, double progress) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dontbesoloud/general/group.fxml"));
//            Node groupNode = loader.load();
//
//            GroupController controller = loader.getController();
//            controller.setData(1, title, progress, null, this);
//
//            groupsContainer.getChildren().addFirst(groupNode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @FXML
    private void showAddGroupLayer() {
        addGroupLayer.setVisible(true);
        addGroupLayer.setManaged(true);
    }

    @FXML
    private void hideAddGroupLayer() {
        addGroupLayer.setVisible(false);
        addGroupLayer.setManaged(false);
        isEditMode = false;
        groupNameField.clear();
        groupImage.setImage(null);
    }

    @FXML
    private void addNewGroup() {
        isEditMode = false;
        groupNameField.clear();
        groupImage.setImage(null);
        showAddGroupLayer();
    }

    public void clickOnGroup() {
        if (Main.isAdmin()) showAddGroupLayer();
        else goToLessons();
    }

    private void goToLessons() {
        try {
            Main.loadPage("/dontbesoloud/general/lessons.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void сhooseImage() {
    }

    @FXML
    private void deleteGroup() {
        hideAddGroupLayer();
    }

    @FXML
    private void saveGroup() {
        hideAddGroupLayer();
        goToLessons();
    }


}