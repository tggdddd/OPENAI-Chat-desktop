package icu.stopit.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.stopit.App;
import icu.stopit.Setting.PropertyUtil;
import icu.stopit.VO.completion.chat.ChatMessage;
import icu.stopit.VO.completion.chat.ChatMessageRole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName HistoryView
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/19 12:16
 * @Version 1.0
 */
public class HistoryUtils{

    private static final File[] useFile = new File[1];
    private static final String rootPath = Objects.requireNonNull(App.class.getResource("setting.properties")).getPath()+ File.separator+".."+File.separator+"history";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static File getUseFile() {
        return useFile[0];
    }

    public static void setUseFile(File useFile) {
        HistoryUtils.useFile[0] = useFile;
    }

    public static String[] history(){
        File file = new File(rootPath);
        return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .map(File::getName).toArray(String[]::new);
    }

    /**
    *
    * @Description: 加载历史消息
    * @author: 肖润杰
    * @date: 2023/4/23 13:09
    * @param fileName 文件名:
    * @Return:
    */
    public static List<ChatMessage> load(String fileName) throws IOException,Exception {
        File file = new File(rootPath, fileName);
        if(!file.exists()){
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<ChatMessage> list = new ArrayList<>();
        String line = null;
        while ((line = bufferedReader.readLine())!= null){
            ChatMessage chatMessage = objectMapper.readValue(line, ChatMessage.class);
            list.add(chatMessage);
        }
        PropertyUtil.init(list);
        setUseFile(file);
        return list;
    }
    public static Message convert(ChatMessage message){
        if(ChatMessageRole.SYSTEM.value().equals(message.getRole())){
            return null;
        }else
        if(ChatMessageRole.USER.value().equals(message.getRole())){
            return new Message(message.getContent(),false);
        }else {
            return new Message(message.getContent(),true);
        }
    }
    public static ChatMessage convert(Message message){
        if (message.isAI()) {
            return new ChatMessage(ChatMessageRole.ASSISTANT.value(), message.getText());
        }else {
            return new ChatMessage(message.getText());
        }
    }
    public static ChatMessage convert(String message){
        return new ChatMessage(message);
    }
    public static ChatMessage convert(String message,boolean isAI){
        return new ChatMessage(isAI?ChatMessageRole.ASSISTANT.value():ChatMessageRole.USER.value(),message);
    }
    public static List<?> convert(List<?> messages){
        List<Object> objects = new ArrayList<>();
        if(messages == null || messages.size() == 0){
            return objects;
        }
        if(messages.get(0) instanceof ChatMessage){
            for (Object message : messages) {
                Message convert = convert((ChatMessage)message);
                if (convert != null) {
                    objects.add(convert);
                }
            }
        }else if(messages.get(0) instanceof Message){
            for (Object message : messages) {
                ChatMessage convert = convert((Message) message);
                objects.add(convert);
            }
        }
        return objects;
    }
    /**
    *
    * @Description: 追加历史消息
    * @author: 肖润杰
    * @date: 2023/4/23 13:09
    * @Return:
    */
    public static void append(ChatMessage message){
        if(getUseFile() == null){
            newFile();
        }
        try {
            FileWriter fileWriter = new FileWriter(getUseFile(),true);
            fileWriter.write(objectMapper.writeValueAsString(message));
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static File newFile(){
        return newFile("新对话",0);
    }
    public static File newFile(String fileName){
        return newFile(fileName,0);
    }
    public static File newFile(String fileName, int i){
        File file = new File(rootPath, fileName+ (i==0?"":i));
        if(file.exists()){
            return  newFile(fileName,++i);
        }
        try {
            boolean newFile = file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setUseFile(file);
        HistoryView.flash(file.getName());
        return file;
    }
    public static void rename(String fileName){
        if(getUseFile() == null){
            newFile(fileName);
        }else {

            boolean b = getUseFile().renameTo(new File(rootPath,getNoRepeat(fileName)));
        }
    }
    private static String getNoRepeat(String name){
        return getNoRepeat(name,0);
    }
    private static String getNoRepeat(String name,int i){
        String realName = name+((i==0)?"":i);
        long count = Arrays.stream(HistoryUtils.history()).filter(e -> e.equals(realName)).count();
        if(count!=0){
            return getNoRepeat(name,(i+1));
        }else {
            return realName;
        }
    }
    public static void delete(String fileName){
        File file = new File(rootPath, fileName);
        if(file.exists()){
            file.delete();
        }
    }
}
