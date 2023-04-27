package icu.stopit.View;

import icu.stopit.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * @ClassName ControlView
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/19 12:03
 * @Version 1.0
 */
public class ControlView{

    private static final int MAX_HEIGHT = 400;
    private static final int PREFERRED_HEIGHT = 80;
    private BorderPane root;

    public BorderPane getRoot() {
        return root;
    }

    private TextArea inputArea;
    private Button sendButton;
    private Button clearButton;
    private Button settingButton;

    public Button getSettingButton() {
        return settingButton;
    }

    public TextArea getInputArea() {
        return inputArea;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public ControlView() {
        // 创建输入区域
        inputArea = new TextArea();
        inputArea.setWrapText(true);
        inputArea.setPrefHeight(PREFERRED_HEIGHT);
        inputArea.setFont(Font.font("Verdana", 14));
        inputArea.textProperty().addListener((obs, oldVal, newVal) -> resizeInputArea());

        // 创建发送和清空按钮
        sendButton = new Button("发送");
        clearButton = new Button("新对话");
        sendButton.setPrefSize(60, 30);
        clearButton.setPrefSize(60, 30);

        // 创建设置和复选框按钮
        Image settingImage = new Image(App.class.getResourceAsStream("setting.png"));
        ImageView settingIcon = new ImageView(settingImage);
        settingIcon.setFitWidth(20);
        settingIcon.setFitHeight(20);
        settingButton = new Button("设置", settingIcon);
        CheckBox checkBox = new CheckBox("连续对话");

        // 创建左侧工具栏
        VBox leftBox = new VBox();
        leftBox.setPadding(new Insets(10));
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setSpacing(10);
        leftBox.getChildren().addAll(checkBox, settingButton);

        // 创建右侧工具栏
        VBox rightBox = new VBox();
        rightBox.setPadding(new Insets(10));
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setSpacing(10);
        rightBox.getChildren().addAll(clearButton, sendButton);

        // 创建根布局
        BorderPane root = new BorderPane();
        root.setLeft(leftBox);
        root.setCenter(inputArea);
        root.setRight(rightBox);
        BorderPane.setMargin(inputArea, new Insets(10));
        this.root = root;
    }

    private void resizeInputArea() {
        double height = inputArea.getLayoutBounds().getHeight();
        if (height > PREFERRED_HEIGHT && height <= MAX_HEIGHT) {
            inputArea.setPrefHeight(height);
        } else if (height > MAX_HEIGHT) {
            inputArea.setPrefHeight(MAX_HEIGHT);
        }
    }

}
