package cz.cvut.fel.pjv.golyakat.dungeonescape;

import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DungeonEscape extends Application {

    private final Image bgrImage = new Image("background.png");
    private final Image birdImage = new Image("bird0.png");
    // Считаем, что изображения действительно загружены, иначе getWidth() может вернуть 0
    private final double appWidth = bgrImage.getWidth();
    private final double appHeight = bgrImage.getHeight();

    @Override
    public void start(Stage stage) throws IOException {
        // Создаём канву нужного размера
        Canvas canvas = new Canvas(appWidth, appHeight);

        // Создаём корневой контейнер и добавляем туда канву
        StackPane root = new StackPane(canvas);

        // Рисуем фон и объекты (например, птицу)
        drawBackground(canvas);
        drawItems(canvas);

        // Создаём сцену нужного размера
        Scene scene = new Scene(root, appWidth, appHeight);

        stage.setTitle("Dungeon Escape");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Рисует фон на канве.
     */
    private void drawBackground(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(bgrImage, 0, 0);
    }

    /**
     * Рисует предметы/объекты игры на канве (например, птичку).
     */
    private void drawItems(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Допустим, рисуем птичку в координатах (100, 100):
        gc.drawImage(birdImage, 100, 100);
    }

    public static void main(String[] args) {
        launch();
    }
}
