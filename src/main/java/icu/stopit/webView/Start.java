package icu.stopit.webView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @ClassName Start
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/26 12:53
 * @Version 1.0
 */
public class Start extends Application {
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("AI聊天");
        Browser browser = new Browser(this);
        Scene scene=new Scene(browser,700,500, Color.web("#666970"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
