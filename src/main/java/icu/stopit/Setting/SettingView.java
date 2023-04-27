package icu.stopit.Setting;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.Set;

/**
 * @ClassName SettingView
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/21 16:16
 * @Version 1.0
 */
public class SettingView {
    BorderPane root;
    Button save = new Button("保存");
    Button cancel = new Button("取消");
    public BorderPane getRoot() {
        return root;
    }

    public SettingView(){
        // 创建消息框布局
        BorderPane borderPane = new BorderPane();
        VBox box = new VBox();
        box.setMaxWidth(Double.MAX_VALUE);
        box.setPrefWidth(-1);
        box.getChildren().addAll(
                getItem("model","模型"),
                getItem("max_tokens","最大文本"),
                getItem("temperature","随机性(0,2)"),
                getItem("n","生成数"),
                getItem("presence_penalty","创造性(-2,2)"),
                getItem("frequency_penalty","重复性(-2,2)"),
                getItem("user","用户标识"));
        FlowPane buttons = new FlowPane();
        save.setTranslateX(40);
        cancel.setTranslateX(80);
        buttons.getChildren().addAll(save,cancel);
        borderPane.setCenter(box);
        borderPane.setBottom(buttons);
        save.setOnAction(e->{
                    setItem("model",((TextField)((FlowPane)box.getChildren().get(0)).getChildren().get(1)).getText());
                    setItem("max_tokens",((TextField)((FlowPane)box.getChildren().get(1)).getChildren().get(1)).getText());
                    setItem("temperature",((TextField)((FlowPane)box.getChildren().get(2)).getChildren().get(1)).getText());
                    setItem("n",((TextField)((FlowPane)box.getChildren().get(3)).getChildren().get(1)).getText());
                    setItem("presence_penalty",((TextField)((FlowPane)box.getChildren().get(4)).getChildren().get(1)).getText());
                    setItem("frequency_penalty",((TextField)((FlowPane)box.getChildren().get(5)).getChildren().get(1)).getText());
                    setItem("user",((TextField)((FlowPane)box.getChildren().get(6)).getChildren().get(1)).getText());
                    PropertyUtil.store();
                    cancel.fire();
        });
        this.root = borderPane;
    }
    public void setClose(EventHandler<ActionEvent> handler){
        cancel.setOnAction(handler);
    }
    private FlowPane getItem(String key,String labelText){
        FlowPane borderPane = new FlowPane();
        Label label = new Label(labelText);
        TextField textField = new TextField((String) PropertyUtil.getProperties().get(key));
        label.setLabelFor(textField);
        label.setPrefWidth(90);
        label.setTranslateX(12);
        borderPane.getChildren().addAll(label,textField);
        return borderPane;
    }
    private void setItem(String key,String value){
        PropertyUtil.getProperties().setProperty(key,value);

    }
}
