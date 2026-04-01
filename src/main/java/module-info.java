module dontbesoloud {
    requires java.prefs;
    requires java.net.http;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires com.dlsc.formsfx;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    requires opencv;

    opens dontbesoloud to javafx.fxml;
    opens dontbesoloud.auth to javafx.fxml;

    opens dontbesoloud.general to javafx.fxml;
    opens dontbesoloud.sidebar to javafx.fxml;
    opens dontbesoloud.utils to javafx.fxml;

    exports dontbesoloud;
}