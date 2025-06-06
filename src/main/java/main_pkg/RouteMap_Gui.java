package main_pkg;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RouteMap_Gui extends Application {

    private SubwaySystem subwaySystem;
    private ComboBox<String> startStationComboBox;
    private ComboBox<String> endStationComboBox;
    private TextArea resultTextArea;
    private final List<String> stations = new ArrayList<>();
    private WebView webView;
    private WebEngine webEngine;
    private String startStationId="四新大道";
    private String endStationId="华中科技大学";

    @Override
    public void start(Stage primaryStage) {
        // 初始化站点信息
        initStations();

        // 创建界面组件
        Label startLabel = new Label("起点站:");
        startStationComboBox = new ComboBox<>();
        startStationComboBox.getItems().addAll(stations);

        Label endLabel = new Label("终点站:");
        endStationComboBox = new ComboBox<>();
        endStationComboBox.getItems().addAll(stations);

        Button planButton = new Button("规划路径");
        planButton.setOnAction(e -> {
            planRoute();
            // 动态加载 WebView 内容
            if (webEngine == null) {
                webView = new WebView();
                webEngine = webView.getEngine();
                BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
                root.setCenter(webView);
            }
            webEngine.loadContent(getMapHtml());
        });

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);

        // 布局组件
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(startLabel, startStationComboBox, endLabel, endStationComboBox, planButton);

        VBox mainBox = new VBox(10);
        mainBox.setPadding(new Insets(10));
        mainBox.getChildren().addAll(inputBox, resultTextArea);

        BorderPane root = new BorderPane();
        root.setTop(mainBox);
        // 初始时不设置 WebView
        // root.setCenter(webView);

        // 创建场景并显示窗口
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("地铁线路规划");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getMapHtml() {
        // 获取起点和终点信息
        String startStation = startStationComboBox.getValue();
        String endStation = endStationComboBox.getValue();
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <!--重要meta, 必须!-->\n" +
                "    <meta name=\"viewport\" content=\"width=320, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0,shrink-to-fit=no\" />\n" +
                "    <title>SUBWAY</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"mybox\"></div>\n" +
                "    <script src=\"https://webapi.amap.com/subway?v=1.0&key=46097447be66399ca3b2ab66b509e16d&callback=cbk\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "    window.cbk = function() {\n" +
                "        var mySubway = subway(\"mybox\", {\n" +
                "            adcode: 4201,\n" +
                "            theme: \"colorful\",\n" +
                "            client: 0,\n" +
                "            doubleclick: {\n" +
                "                switch: true\n" +
                "            }\n" +
                "        });\n" +
                "        // 添加直接路线规划方法\n" +
                "        function directRoute(startId, endId) {\n" +
                "            mySubway.clearRoute();\n" +
                "            mySubway.setStart(startId);\n" +
                "            mySubway.setEnd(endId);\n" +
                "            mySubway.route(startId, endId, {\n" +
                "                closeBtn: false\n" +
                "            });\n" +
                "        }\n" +
                "        // 地铁加载完成后自动执行示例路线（北京西站到北京南站）\n" +
                "        mySubway.event.on(\"subway.complete\", function() {\n" +
                "            // 示例ID（实际ID需通过mySubway.getStations()获取）\n" +
                "            var startId = \""+startStationId+"\";\n" +
                "            var endId = \""+endStationId+"\";\n" +
                "            directRoute(startId, endId);\n" +
                "        });\n" +
                "    };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private void initStations() {
        // 模拟地铁站点
        subwaySystem = new SubwaySystem();
        subwaySystem.loadFromFile("source/all_lines_station_distance.csv");
        Set<Object> stationNames = subwaySystem.getStations().keySet();
        for (Object stationName : stationNames) {
            stations.add((String) stationName);
        }
    }

    private void planRoute() {
        String startStation = startStationComboBox.getValue();
        String endStation = endStationComboBox.getValue();
        startStationId=startStation;
        endStationId=endStation;

        if (startStation == null || endStation == null) {
            resultTextArea.setText("请选择起点站和终点站");
            return;
        }

        if (startStation.equals(endStation)) {
            resultTextArea.setText("起点站和终点站不能相同");
            return;
        }

        StringBuilder resultBuilder = new StringBuilder("起点站到终点站的最短路径:\n");
        List<String> shortestPath = subwaySystem.findShortestPath(startStation, endStation);
        resultBuilder.append(subwaySystem.printPath(shortestPath)).append("\n\n");

        // 计算票价
        double fare = subwaySystem.calculateFare(shortestPath);
        double wuhanTongFare = subwaySystem.calculateWuhanTongFare(shortestPath);
        double dayTicketFare = subwaySystem.calculateDayTicketFare(shortestPath);

        String F = String.format("%.2f", fare);
        resultBuilder.append("普通单程票票价: ").append(F).append(" 元\n");
        String wuhanTongF = String.format("%.2f", wuhanTongFare);
        resultBuilder.append("武汉通票价: ").append(wuhanTongF).append(" 元\n");
        resultBuilder.append("日票票价: ").append(dayTicketFare).append(" 元\n");

        resultTextArea.setText(resultBuilder.toString());

    }

    public static void main(String[] args) {
        launch(args);
    }
}
