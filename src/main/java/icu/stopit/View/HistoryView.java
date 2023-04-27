package icu.stopit.View;

import icu.stopit.VO.completion.chat.ChatMessage;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @ClassName HistoryView
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/23 13:56
 * @Version 1.0
 */
public class HistoryView {
    private static ScrollPane root;
    private static Consumer<List<ChatMessage>> consumer;
    public ScrollPane getRoot() {
        return root;
    }
    /**
    *
    * @Description:
    * @author: 肖润杰
    * @date: 2023/4/23 14:13
    * @param consumer: 加载消息的方法
    * @Return:
    */
    public HistoryView(Consumer<List<ChatMessage>> consumer){
        init(consumer,null);
    }
    public static void init(Consumer<List<ChatMessage>> consumer,String activeName){
        if(consumer != null){
            HistoryView.consumer = consumer;
        }
        VBox vBox = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(-1);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMaxWidth(-1);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(-1);
        vBox.setMaxWidth(Double.MAX_VALUE);
        vBox.setPrefWidth(-1);
        scrollPane.setContent(vBox);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setSpacing(1);
        String[] history = HistoryUtils.history();
        for (String s : history) {
            Label label = new Label(s);
            label.setPrefWidth(-1);
            label.setStyle("-fx-padding: 2px 5px;" +
                    "  -fx-text-alignment: center;\n" +
                    "  -fx-text-fill: white;\n" +
                    "  -fx-background-color: #bababa;\n" +
                    "  -fx-border-color: transparent;\n" +
                    "  -fx-border-radius: 5px;\n" +
                    "  -fx-cursor: hand;" +
                    "-fx-start-margin: 5px;" +
                    "-fx-end-margin: 5px");
            label.setOnMouseClicked(e->{
                for (Node child : vBox.getChildren()) {
                    child.setStyle("-fx-padding: 2px 5px;" +
                            "  -fx-text-alignment: center;\n" +
                            "  -fx-text-fill: white;\n" +
                            "  -fx-background-color: #bababa;\n" +
                            "  -fx-border-color: transparent;\n" +
                            "  -fx-border-radius: 5px;\n" +
                            "  -fx-cursor: hand;" +
                            "-fx-start-margin: 5px;" +
                            "-fx-end-margin: 5px");
                }
                label.setStyle("-fx-padding: 2px 5px;" +
                        "  -fx-text-alignment: center;\n" +
                        "  -fx-text-fill: white;\n" +
                        "  -fx-background-color: #ac5051;\n" +
                        "  -fx-border-color: transparent;\n" +
                        "  -fx-border-radius: 5px;\n" +
                        "-fx-start-margin: 5px;" +
                        "-fx-end-margin: 5px;" +
                        "  -fx-cursor: hand;");
                try {
                    // 加载消息
                    List<ChatMessage> messages = HistoryUtils.load(label.getText());
                    // 执行操作
                    HistoryView.consumer.accept(messages);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            vBox.getChildren().add(label);
        }
        if(activeName != null){
            for (Node child : vBox.getChildren()) {
                Label t = (Label) child;
                if (activeName.equals(t.getText())) {
                    t.setStyle("-fx-padding: 2px 5px;" +
                            "  -fx-text-alignment: center;\n" +
                            "  -fx-text-fill: white;\n" +
                            "  -fx-background-color: #ac5051;\n" +
                            "  -fx-border-color: transparent;\n" +
                            "  -fx-border-radius: 5px;\n" +
                            "-fx-start-margin: 5px;" +
                            "-fx-end-margin: 5px;" +
                            "  -fx-cursor: hand;");
                }
            }
        }
        root = scrollPane;
    }
    public static void flash(String activeName){
        init(null,activeName);
    }
}
