package icu.stopit.webView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import icu.stopit.Remote.OpenAiClient;
import icu.stopit.Setting.PropertyUtil;
import icu.stopit.VO.completion.chat.ChatCompletionChoice;
import icu.stopit.VO.completion.chat.ChatMessage;
import icu.stopit.View.HistoryUtils;

import java.util.HashMap;
import java.util.Properties;

/**
 * @ClassName Controller
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/26 13:37
 * @Version 1.0
 */
public class Controller {
    private static final String STOP_FLAG = "#￥#@！！#￥";
    private static final String ERROR_FLAG = "#￥#@%！#￥";
    private static final String LENGTH_STOP_FLAG = "#￥@@%！#￥";
    OpenAiClient openAiClient = new OpenAiClient();
    Cache cache = new Cache();
    boolean isRun;
    private String useMessage;
    private StringBuilder aiMessage = new StringBuilder();

    public String getMessage() {
        // System.out.println("getMessage");
        return cache.getNext();
    }

    public void debug(String s) {
        System.out.println(s);
    }

    public void saveSetting(String json) {
        System.out.println("saveSetting:" + json);
        try {
            HashMap hashMap = new ObjectMapper().readValue(json, HashMap.class);
            PropertyUtil.getProperties().putAll(hashMap);
            PropertyUtil.store();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String listSession() {
        System.out.println("listSession:");
        try {
            return new ObjectMapper().writeValueAsString(HistoryUtils.history());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String newSession() {
        System.out.println("newSession:");
        HistoryUtils.newFile();
        try {
            PropertyUtil.init();
            return new ObjectMapper().writeValueAsString(HistoryUtils.history());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String renameSession(String name) {
        System.out.println("renameSession:" + name);
        HistoryUtils.rename(name);
        try {
            return new ObjectMapper().writeValueAsString(HistoryUtils.history());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteSession(String name) {
        System.out.println("deleteSession:" + name);
        HistoryUtils.delete(name);
        try {
            return new ObjectMapper().writeValueAsString(HistoryUtils.history());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadSetting() {
        System.out.println("loadSetting");
        Properties properties = PropertyUtil.getProperties();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("model", properties.getProperty("model"));
        objectObjectHashMap.put("max_tokens", properties.getProperty("max_tokens"));
        objectObjectHashMap.put("temperature", properties.getProperty("temperature"));
        objectObjectHashMap.put("n", properties.getProperty("n"));
        objectObjectHashMap.put("presence_penalty", properties.getProperty("presence_penalty"));
        objectObjectHashMap.put("frequency_penalty", properties.getProperty("frequency_penalty"));
        objectObjectHashMap.put("user", properties.getProperty("user"));
        objectObjectHashMap.put("guide", properties.getProperty("guide"));
        objectObjectHashMap.put("token", properties.getProperty("token"));
        try {
            return new ObjectMapper().writeValueAsString(objectObjectHashMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(String message) {
        System.out.println("send:" + message);
        if (isRun) {
            System.out.println("send:" + "stop:");
            return "stop";
        }
        isRun = true;
        useMessage = message;
        System.out.println("发送消息到远程:");
        new Thread(() -> {
            try {
                PropertyUtil.pushMessage(HistoryUtils.convert(message));
            } catch (Exception e) {
                cache.put(e.getMessage()+ERROR_FLAG);
            }
            try {
                openAiClient.chat(PropertyUtil.getChatCompletionRequest(),
                        response -> {
                            System.out.println("收到返回:" + response);
                            ChatCompletionChoice chatCompletionChoice = response.getChoices().get(0);
                            String content = chatCompletionChoice.getMessage().getContent();
                            if (content != null) {
                                cache.put(content);
                                aiMessage.append(content);
                            }
                            if (chatCompletionChoice.getFinishReason() != null) {
                                isRun = false;
                                // 添加到请求中
                                ChatMessage convert = HistoryUtils.convert(useMessage);
                                ChatMessage convert1 = HistoryUtils.convert(aiMessage.toString());
                                if ("stop".equals(chatCompletionChoice.getFinishReason())) {
                                    cache.put(STOP_FLAG);
                                } else {
                                    cache.put(LENGTH_STOP_FLAG);
                                }
                                PropertyUtil.pushMessage(convert1);
                                //    保存到文件记录中
                                HistoryUtils.append(convert);
                                HistoryUtils.append(convert1);
                                aiMessage.delete(0, aiMessage.length());
                            }
                        }, error -> {
                            System.out.println("收到报错:" + error);
                            if ("context_length_exceeded".equals(error.getError().getCode())) {
                                // TODO: 2023/4/26 选择性删减请求
                            }
                            cache.put(error.getError().getMessage());
                            PropertyUtil.removeLastMessage();
                            isRun = false;
                            aiMessage.delete(0, aiMessage.length());
                        });
            } catch (Exception e) {
                cache.put(e.getMessage());
            }
        }).start();
        return "";
    }
}

class Cache {

    private final int len = 1024;
    private final String[] cache = new String[len];
    private int index = 0;
    private int end = 0;

    public String getNext() {
        if (end == index) {
            return "";
        }
        String s = cache[index++];
        if (index == len) {
            index = 0;
        }
        // System.out.println("getNext:" + s);
        return s;
    }

    public void put(String s) {
        if (end + 1 == index) {
            // TODO: 2023/4/26 不需要   40够用了
        }
        cache[end++] = s;
        // System.out.println("put:" + s);
        if (end == len) {
            end = 0;
        }
    }
}