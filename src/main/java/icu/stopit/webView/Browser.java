package icu.stopit.webView;

import icu.stopit.App;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * @ClassName Webview
 * @Description
 * @Author 肖润杰
 * @Time 2023/4/26 12:54
 * @Version 1.0
 */
public class Browser extends Region {
    WebView webView=new WebView();
    WebEngine webEnging=webView.getEngine();
    private static final Controller app = new Controller();

    public Browser(Start start) {
        webEnging.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
            if(t1==Worker.State.SUCCEEDED){
                JSObject win=(JSObject)webEnging.executeScript("window");
                win.setMember("app", app);
            }
        });
        webEnging.load(Browser.class.getResource("/web/index.html").toExternalForm());
        getChildren().add(webView);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(webView,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double width) {
        return 800;
    }

    @Override
    protected double computePrefHeight(double height) {
        return 600;
    }

}