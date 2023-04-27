package icu.stopit.View;

import icu.stopit.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @ClassName MessageView
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/19 12:16
 * @Version 1.0
 */
public class Message {
    private VBox root;
    private static final int MESSAGE_MAX_WIDTH = 600;
    public VBox getRoot() {
        return root;
    }
    private Text text;
    private boolean isAI;

    public boolean isAI() {
        return isAI;
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return text.getText();
    }

    public Message(String message){
      this(message,false);
    }
    public Message(boolean isAI){
        this("",isAI);
    }
    public Message(){
        this("",false);
    }
    public Message(String message,boolean isAI){
        // 创建左侧头像区域
        Image avatarImage;
        this.isAI = isAI;
        if(isAI){
            avatarImage = new Image(App.class.getResourceAsStream("AIAvatar.png"));
        }else {
            avatarImage = new Image(App.class.getResourceAsStream("userAvatar.png"));
        }
        ImageView avatar = new ImageView(avatarImage);
        avatar.setFitHeight(30);
        avatar.setFitWidth(30);
        // 创建消息文本区域
        text = new Text(message);
        text.setSelectionStart(0);
        text.setSelectionEnd(1);
        text.setSelectionFill(Color.RED);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPrefWidth(-1); // 设置TextFlow的宽度，超过部分会自动换行
        textFlow.setStyle("-fx-background-color: #F4F4F4; -fx-padding: 5px; -fx-background-radius: 10px;");
        // 创建消息框布局
        VBox messageBox = new VBox();
        messageBox.setSpacing(5);
        messageBox.setStyle("-fx-background-color: rgba(177,177,157,0.22); -fx-padding: 1px; -fx-background-radius: 10px;");
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.getChildren().add(textFlow);
        // 创建左侧布局
        HBox leftBox = new HBox();
        if(isAI){
            leftBox.setAlignment(Pos.CENTER_LEFT);
        }else {
            leftBox.setAlignment(Pos.CENTER_RIGHT);
        }
        leftBox.setSpacing(10);
        if(isAI){
            leftBox.getChildren().addAll(avatar, messageBox);
        }else {
            leftBox.getChildren().addAll(messageBox, avatar);
        }

        // 创建根布局
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.getChildren().addAll(leftBox);
        this.root = root;
    }
}
