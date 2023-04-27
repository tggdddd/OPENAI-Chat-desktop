/**
 * @ClassName module-info
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/27 10:10
 * @Version 1.0
 */module AiChat {
    requires javafx.graphics;
    requires javafx.controls;
    requires lombok;
    requires com.fasterxml.jackson.databind;
    requires io.reactivex.rxjava2;
    requires java.net.http;
    requires javafx.web;
    requires jdk.jsobject;
    
    exports icu.stopit;
    exports icu.stopit.Remote;
    exports icu.stopit.Setting;
    exports icu.stopit.View;
    exports icu.stopit.VO;
    exports icu.stopit.webView;
    exports icu.stopit.VO.completion.chat;
    exports icu.stopit.VO.completion;


    opens icu.stopit;
    opens icu.stopit.Remote;
    opens icu.stopit.Setting;
    opens icu.stopit.View;
    opens icu.stopit.VO;
    opens icu.stopit.webView;
    opens icu.stopit.VO.completion.chat;
    opens icu.stopit.VO.completion;
}