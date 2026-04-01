package dontbesoloud.utils; // проверьте соответствие вашему пакету

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

public class camera {
    public static Image mat2Image(Mat frame) {
        try {
            MatOfByte buffer = new MatOfByte();
            // Кодируем кадр в формат памяти (PNG или JPG)
            Imgcodecs.imencode(".png", frame, buffer);
            // Возвращаем поток байтов как изображение
            return new Image(new ByteArrayInputStream(buffer.toArray()));
        } catch (Exception e) {
            System.err.println("Ошибка конвертации кадра: " + e.getMessage());
            return null;
        }
    }
}