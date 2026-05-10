package dontbesoloud.sidebar;

import dontbesoloud.Main;
import dontbesoloud.general.LessonsController;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class SidebarController {
    @FXML
    private Button btnGeneral, btnVideo, btnLogout;

    @FXML
    private void logOut() {
        Main.deleteToken();
        try {
            Main.loadPage("/dontbesoloud/auth/auth.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setToGeneral() {
        if (Main.getCurrentController() instanceof LessonsController) {
            ((LessonsController) Main.getCurrentController()).stopPythonEngine();
        }
        try {
            Main.loadPage("/dontbesoloud/general/general.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setToVideocall() {
        if (Main.getCurrentController() instanceof LessonsController) {
            ((LessonsController) Main.getCurrentController()).stopPythonEngine();
        }
        try {
            Main.loadPage("/dontbesoloud/videocall/videocall.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
