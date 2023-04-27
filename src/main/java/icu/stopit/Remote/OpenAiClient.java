package icu.stopit.Remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.stopit.Setting.PropertyUtil;
import icu.stopit.VO.OpenAiError;
import icu.stopit.VO.completion.chat.ChatCompletionChunk;
import icu.stopit.VO.completion.chat.ChatCompletionRequest;
import io.reactivex.functions.Consumer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;

/**
 * @ClassName ChatController
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/19 14:14
 * @Version 1.0
 */
public class OpenAiClient {
    private static final String BASE_URL = "https://api.openai.com";
    private static ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient client = HttpClient.newHttpClient();
    private String token;
    byte[] bytes = new byte[1024];
    HttpURLConnection conn;
    public OpenAiClient() {
        this("");
    }
    public OpenAiClient(String token) {
        this.token = token;
    }
    public void chat(ChatCompletionRequest params,Consumer<ChatCompletionChunk> reduce, Consumer<? super OpenAiError > error) {
        try {
            // System.out.println(objectMapper.writeValueAsString(params.getMessages()));
            conn = (HttpURLConnection) new URL("https://api.openai.com/v1/chat/completions").openConnection();
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setChunkedStreamingMode(-1);
            conn.setRequestProperty("Authorization", "Bearer "+ PropertyUtil.getProperties().get("token"));
            conn.setRequestProperty("Content-Type","application/json");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(new ObjectMapper().writeValueAsBytes(params));
            outputStream.close();
            InputStream is = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            // 解决开头
            String line =null;
            boolean hasMore = true;
            while ((line = bufferedReader.readLine())!= null || hasMore){
                hasMore = reduce(line, reduce);
            }
            is.close();
        } catch (Exception e) {
            try {
                InputStream errorStream = conn.getErrorStream();
                if(errorStream!=null){
                    byte[] bytes1 = errorStream.readAllBytes();
                    OpenAiError openAiError = objectMapper.readValue(bytes1, OpenAiError.class);
                    error.accept(openAiError);
                    errorStream.close();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            conn.disconnect();
        }
    }
    private boolean reduce(String line,Consumer<ChatCompletionChunk> consume){
        if (line == null || line.length()<5) {
            return false;
        }
        String content = line.substring(5);
        if (content.contains("[DONE]")){
            return false;
        }
        try {
            ChatCompletionChunk chatCompletionChunk = objectMapper.readValue(content, ChatCompletionChunk.class);
            consume.accept(chatCompletionChunk);
        } catch (Exception e) {
            System.err.println("reduce出错："+content);
            throw new RuntimeException(e);
        }
        return true;
    }
}
