module dontbesoloud {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens dontbesoloud to javafx.fxml;
    opens dontbesoloud.auth to javafx.fxml;

    exports dontbesoloud;
}