package dontbesoloud.general;

import dontbesoloud.Main;
import dontbesoloud.general.GeneralController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class GroupController {
    @FXML
    private ImageView groupImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label percentLabel;

    private GeneralController generalController;
    private int groupId;

    public void setData(Integer groupId, String title, double progress, String imageUrl, GeneralController generalController) {
        this.groupId = groupId;
        this.generalController = generalController;
        titleLabel.setText(title);
        progressBar.setProgress(progress);
        percentLabel.setText((int) (progress * 100) + "%");

        // Загрузка изображения (пока заглушка)
        // groupImage.setImage(new Image(imageUrl));
    }

    @FXML
    private void onClick() {

        if (generalController != null && Main.isAdmin()) {
            generalController.editGroup();
        } else if (generalController != null && !Main.isAdmin()) {
            try {
                Main.loadPage("/dontbesoloud/general/lessons.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}