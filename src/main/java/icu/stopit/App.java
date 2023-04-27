package icu.stopit;

import icu.stopit.Setting.SettingView;
import icu.stopit.View.ControlView;
import icu.stopit.View.HistoryUtils;
import icu.stopit.View.HistoryView;
import icu.stopit.View.Message;
import icu.stopit.View.MessageContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @ClassName App
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/19 9:05
 * @Version 1.0
 */
public class App extends Application {
    private static Scene scene;
    private BorderPane root;
    public static HistoryView historyView;
    @Override
    public void start(Stage stage) {
        stage.setTitle("AI聊天");
        stage.setResizable(true);
        stage.setWidth(500);
        stage.setHeight(500);
        // 消息窗口
        MessageContainer messageContainer = new MessageContainer();
        // 历史消息
        historyView = new HistoryView(e->{
            messageContainer.clearMessage();
            messageContainer.addAllMessage(e);
        });
        BorderPane messagePane = new BorderPane();
        messagePane.setLeft(historyView.getRoot());
        messagePane.setCenter(messageContainer.getRoot());
        // 底部输入
        ControlView controlView = new ControlView();
        // 设置输入监听
        controlView.getSendButton().setOnAction(e->{
            // AI回复中，不响应
            if(messageContainer.isRun()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"");
                alert.setTitle("AI回复中");
                alert.setContentText("请耐心等待");
                alert.setWidth(100);
                alert.setHeight(100);
                // 自动关闭窗口
                alert.setOnShown(dialogEvent -> {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    alert.close();
                });
                alert.show();
                return;
            }
            //发送消息
            String text = controlView.getInputArea().getText();
            messageContainer.addMessage(new Message(text));
        });
        controlView.getClearButton().setOnAction(e->{
            messageContainer.clearMessage();
            HistoryUtils.newFile();
        });
        controlView.getSettingButton().setOnAction(e->{
            SettingView settingView = new SettingView();
            settingView.setClose(event -> scene.setRoot(root));
            scene.setRoot(settingView.getRoot());
        });
        // 设置根页面
        root = new BorderPane();
        root.setCenter(messagePane);
        root.setBottom(controlView.getRoot());
        scene = new Scene(root);
        stage.setScene(scene);
        // 显示页面
        stage.show();
    }
/**启动  */
    public static void main(String[] args) {
        launch();
    }

}
