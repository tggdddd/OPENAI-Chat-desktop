package icu.stopit.Setting;

import icu.stopit.App;
import icu.stopit.VO.completion.chat.ChatCompletionRequest;
import icu.stopit.VO.completion.chat.ChatMessage;
import icu.stopit.VO.completion.chat.ChatMessageRole;
import lombok.Builder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * @ClassName PropertyUtil
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/21 15:48
 * @Version 1.0
 */
@Builder
public class PropertyUtil {
    private static String guide;

    public static String getGuide() {
        return guide;
    }

    public static void setGuide(String guide) {
        PropertyUtil.guide = guide;
    }

    private static ChatCompletionRequest chatCompletionRequest;
    private static Properties properties;

    public static void init() throws Exception {
        init(null);
    }

    public static void init(List<ChatMessage> list) throws Exception{
        System.out.println("初始化请求init");
        chatCompletionRequest = new ChatCompletionRequest();
        properties = new Properties();
        try {
            System.out.println("初始化请求加载文件");
            properties.load(App.class.getResourceAsStream("setting.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("初始化请求设置参数");
        chatCompletionRequest.setModel(properties.getProperty("model","gpt-3.5-turbo"));
        chatCompletionRequest.setMaxTokens(Integer.valueOf(properties.getProperty("max_tokens","50")));
        chatCompletionRequest.setTemperature(Double.valueOf(properties.getProperty("temperature","1.0")));
        chatCompletionRequest.setN(Integer.valueOf(properties.getProperty("n","1")));
        chatCompletionRequest.setPresencePenalty(Double.valueOf(properties.getProperty("presence_penalty","1.0")));
        chatCompletionRequest.setFrequencyPenalty(Double.valueOf(properties.getProperty("frequency_penalty","1.0")));
        chatCompletionRequest.setUser(properties.getProperty("user","user"));
        guide =  properties.getProperty("guide",Instructions.System.getMessage().getContent());
        System.out.println("初始化请求设置消息");
        if (list == null || list.size() == 0) {
            // TODO: 2023/4/24 动态修改指令消息
            ArrayList<ChatMessage> objects = new ArrayList<>();
            objects.add(new ChatMessage(ChatMessageRole.SYSTEM.value(),guide));
            chatCompletionRequest.setMessages(objects);
        } else {
            chatCompletionRequest.setMessages(list);
        }
    }

    public static ChatCompletionRequest getChatCompletionRequest() throws Exception {
        if (chatCompletionRequest == null) {
            System.out.println("初始化请求");
            init();
            System.out.println("初始化请求成功");
        }
        return chatCompletionRequest;
    }

    /**
     * @Description: 添加消息到请求中
     * @author: 肖润杰
     * @date: 2023/4/24 13:25
     * @Return:
     */
    public static void pushMessage(ChatMessage chatMessage) throws Exception {
        // System.out.println("123:");
        List<ChatMessage> messages = getChatCompletionRequest().getMessages();
        // System.out.println("124413:");
        ArrayList<ChatMessage> chatMessages = new ArrayList<>(messages);
        // System.out.println("124243:");
        chatMessages.add(chatMessage);
        // System.out.println("124143:");
        chatCompletionRequest.setMessages(chatMessages);
        // System.out.println("12443:");
    }

    /**
     * @Description: 删除最后的消息
     * @author: 肖润杰
     * @date: 2023/4/24 13:25
     * @Return:
     */
    public static void removeLastMessage() {
        List<ChatMessage> messages = chatCompletionRequest.getMessages();
        ArrayList<ChatMessage> chatMessages = new ArrayList<>(messages);
        chatMessages.remove(messages.size() - 1);
        chatCompletionRequest.setMessages(chatMessages);
    }

    /**
     * @Description: 使得消息令牌可以发送
     * @author: 肖润杰
     * @date: 2023/4/24 13:25
     * @Return:
     */
    public static void trimMessage() {
        // TODO: 2023/4/24 减去历史消息，直到达到发送要求 
    }

    public static Properties getProperties() {
        if (properties == null) {
            try {
                init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }


    public static void store() {
        try {
            properties.store(new FileWriter(Objects.requireNonNull(App.class.getResource("setting.properties")).getFile()), "");
            chatCompletionRequest.setModel(properties.getProperty("model"));
            chatCompletionRequest.setMaxTokens(Integer.valueOf(properties.getProperty("max_tokens")));
            chatCompletionRequest.setTemperature(Double.valueOf(properties.getProperty("temperature")));
            chatCompletionRequest.setN(Integer.valueOf(properties.getProperty("n")));
            chatCompletionRequest.setPresencePenalty(Double.valueOf(properties.getProperty("presence_penalty")));
            chatCompletionRequest.setFrequencyPenalty(Double.valueOf(properties.getProperty("frequency_penalty")));
            chatCompletionRequest.setUser(properties.getProperty("user"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
