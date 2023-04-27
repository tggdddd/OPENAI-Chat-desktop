package icu.stopit.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.stopit.Remote.OpenAiClient;
import icu.stopit.Setting.PropertyUtil;
import icu.stopit.VO.completion.chat.ChatCompletionChoice;
import icu.stopit.VO.completion.chat.ChatMessage;
import icu.stopit.VO.completion.chat.ChatMessageRole;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * @ClassName MessageContainer
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/19 12:55
 * @Version 1.0
 */
public class MessageContainer {
    private static final int MIN_HEIGHT = 80;
    private static OpenAiClient openAiClient = new OpenAiClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private ScrollPane root;
    private volatile boolean isRun;
    private Message aIMessage;
    /* 用户发送的消息 */
    private Message userMessage;

    public MessageContainer() {
        // 创建消息框布局
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinViewportHeight(MIN_HEIGHT);
        scrollPane.setMaxHeight(-1);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMaxWidth(-1);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(-1);
        VBox box = new VBox();
        box.setMaxWidth(Double.MAX_VALUE);
        box.setPrefWidth(-1);
        scrollPane.setContent(box);
        this.root = scrollPane;
    }

    public ScrollPane getRoot() {
        return root;
    }

    public boolean isRun() {
        return isRun;
    }

    /**
     * 得到最近的ai返回
     */
    public Message getAiMessage() {
        return aIMessage;
    }

    /**
     * @Description: 添加全部消息
     * @author: 肖润杰
     * @date: 2023/4/24 13:19
     * @Return:
     */
    public void addAllMessage(List<ChatMessage> messages) {
        if (messages != null && messages.size() > 0) {
            if (ChatMessageRole.SYSTEM.value().equals(messages.get(0).getRole())) {
                messages.remove(0);
            }
        }
        List<Message> convert = (List<Message>) HistoryUtils.convert(messages);
        for (Message message : convert) {
            ((VBox) root.getContent()).getChildren().add(message.getRoot());
        }
    }

    /**
     * 处理消息
     * 显示到视图
     */
    public void addMessage(Message message) {
        userMessage = message;
        // 将发送的消息显示到视图
        ((VBox) root.getContent()).getChildren().add(message.getRoot());
        // 锁定标识
        isRun = true;
        // 预放AI回复
        this.aIMessage = new Message(true);
        ((VBox) root.getContent()).getChildren().add(this.aIMessage.getRoot());
        // TODO: 2023/4/26  根据token判断
        // 发送到远程
        new Thread(() -> {
            try {
                PropertyUtil.pushMessage(HistoryUtils.convert(message));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                openAiClient.chat(PropertyUtil.getChatCompletionRequest(),
                        response -> {
                                ChatCompletionChoice chatCompletionChoice = response.getChoices().get(0);
                                if(chatCompletionChoice.getMessage().getContent()!=null){
                                    aIMessage.setText(aIMessage.getText() + chatCompletionChoice.getMessage().getContent());
                                }
                                if(chatCompletionChoice.getFinishReason()!=null){
                                    isRun = false;
                                    // 添加到请求中
                                    ChatMessage convert = HistoryUtils.convert(userMessage);
                                    ChatMessage convert1 = HistoryUtils.convert(aIMessage);
                                    // TODO: 2023/4/26 length 为长度限制终止    请求继续
                                    // TODO: 2023/4/26 stop  为结束
                                    PropertyUtil.pushMessage(convert1);
                                    //    保存到文件记录中
                                    HistoryUtils.append(convert);
                                    HistoryUtils.append(convert1);
                                }
                                scrollToBottom();
                            }, error -> {
                    if(error.getError().getCode().equals("context_length_exceeded")){
                        // TODO: 2023/4/26 选择性删减请求
                    }
                            aIMessage.setText(aIMessage.getText() + error.getError().getMessage());
                                    PropertyUtil.removeLastMessage();
                                    isRun = false;
                                    scrollToBottom();
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
    private void scrollToBottom() {
        root.setVvalue(1);
    }

    public void clearMessage() {
        ((VBox) root.getContent()).getChildren().clear();
        this.aIMessage = null;
    }
}
