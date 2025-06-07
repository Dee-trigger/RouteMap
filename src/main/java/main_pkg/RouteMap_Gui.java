package main_pkg;

import data_struct.StationDistance;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RouteMap_Gui extends Application {

    private SubwaySystem subwaySystem;
    private ComboBox<String> startStationComboBox;
    private ComboBox<String> endStationComboBox;
    private TextArea resultTextArea;
    private final List<String> stations = new ArrayList<>();
    private WebView webView;
    private WebEngine webEngine;
    private String startStationId = "四新大道";
    private String endStationId = "华中科技大学";

    @Override
    public void start(Stage primaryStage) {
        // 初始化站点信息
        initStations();

        // 设置全局字体
        Font labelFont = Font.font("Arial", 14);
        Font buttonFont = Font.font("Arial", 14);

        // 创建界面组件
        Label startLabel = new Label("起点站:");
        startLabel.setFont(labelFont);
        startStationComboBox = new ComboBox<>();
        startStationComboBox.getItems().addAll(stations);
        startStationComboBox.setPrefWidth(120);

        Label endLabel = new Label("终点站:");
        endLabel.setFont(labelFont);
        endStationComboBox = new ComboBox<>();
        endStationComboBox.getItems().addAll(stations);
        endStationComboBox.setPrefWidth(120);

        Button planButton = new Button("规划路径");
        planButton.setFont(buttonFont);
        planButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
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

        // 添加查找距离小于 n 的站点按钮
        Button findNearbyStationsButton = new Button("查找距离小于 n 的站点");
        findNearbyStationsButton.setFont(buttonFont);
        findNearbyStationsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        findNearbyStationsButton.setOnAction(e -> {
            // 创建选择站点的 ComboBox
            ComboBox<String> stationComboBox = new ComboBox<>();
            stationComboBox.getItems().addAll(stations);
            stationComboBox.setPromptText("选择站点");

            TextInputDialog distanceDialog = new TextInputDialog();
            distanceDialog.setTitle("输入距离");
            distanceDialog.setHeaderText("请选择一个站点并输入距离 n");
            distanceDialog.setContentText("距离 n:");

            // 创建包含 ComboBox 的自定义对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("选择站点");
            dialog.setHeaderText("请选择一个站点");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(stationComboBox);

            dialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK && stationComboBox.getValue() != null) {
                    String station = stationComboBox.getValue();
                    distanceDialog.showAndWait().ifPresent(distanceStr -> {
                        try {
                            double distance = Double.parseDouble(distanceStr);
                            Set<StationDistance> stationsWithinDistance = subwaySystem.getStationsWithinDistance(station, distance);
                            showNearbyStations(stationsWithinDistance, distance);
                        } catch (NumberFormatException ex) {
                            resultTextArea.setText("输入的距离不是有效的数字，请重新输入。");
                        }
                    });
                }
            });
        });

        // 添加打印中转站按钮
        Button printTransferButton = new Button("打印中转站");
        printTransferButton.setFont(buttonFont);
        printTransferButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        printTransferButton.setOnAction(e -> {
            showTransferStations();
        });

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setWrapText(true);
        resultTextArea.setStyle("-fx-font-size: 14px;");

        // 布局组件
        HBox inputBox = new HBox(15);
        inputBox.setPadding(new Insets(20));
        inputBox.getChildren().addAll(startLabel, startStationComboBox, endLabel, endStationComboBox, planButton, findNearbyStationsButton, printTransferButton);

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.getChildren().addAll(inputBox, resultTextArea);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");
        root.setTop(mainBox);

        // 创建场景并显示窗口
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Route_Map");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showNearbyStations(Set<StationDistance> stationsWithinDistance, double distance) {
        Stage nearbyStationsStage = new Stage();
        nearbyStationsStage.setTitle("距离小于 " + distance + " 的站点");

        TextArea nearbyStationsTextArea = new TextArea();
        nearbyStationsTextArea.setEditable(false);
        nearbyStationsTextArea.setWrapText(true);
        nearbyStationsTextArea.setStyle("-fx-font-size: 14px;");

        StringBuilder nearbyStationsBuilder = new StringBuilder();
        if (stationsWithinDistance.isEmpty()) {
            nearbyStationsBuilder.append("未找到符合条件的站点\n");
        } else {
            for (StationDistance stationDistance : stationsWithinDistance) {
                nearbyStationsBuilder.append(stationDistance).append("\n");
            }
        }
        nearbyStationsTextArea.setText(nearbyStationsBuilder.toString());

        VBox nearbyStationsBox = new VBox(10);
        nearbyStationsBox.setPadding(new Insets(10));
        nearbyStationsBox.getChildren().add(nearbyStationsTextArea);

        Scene nearbyStationsScene = new Scene(nearbyStationsBox, 300, 300);
        nearbyStationsStage.setScene(nearbyStationsScene);
        nearbyStationsStage.show();
    }

    private void showTransferStations() {
        Stage transferStage = new Stage();
        transferStage.setTitle("中转站信息");

        TextArea transferTextArea = new TextArea();
        transferTextArea.setEditable(false);
        transferTextArea.setWrapText(true);
        transferTextArea.setStyle("-fx-font-size: 14px;");

        StringBuilder transferBuilder = new StringBuilder("中转站信息:\n");
        Set<Map.Entry<String, Set<String>>> transferStations = subwaySystem.getTransferStations();
        System.out.println("所有中转站:");
        for (Map.Entry<String, Set<String>> entry : transferStations) {
            System.out.println("<" + entry.getKey() + ", <" + String.join("、", entry.getValue()) + ">>");
            transferBuilder.append("<").append(entry.getKey()).append(", <").append(String.join("、", entry.getValue())).append(">>\n");
        }
        transferTextArea.setText(transferBuilder.toString());

        VBox transferBox = new VBox(10);
        transferBox.setPadding(new Insets(10));
        transferBox.getChildren().add(transferTextArea);

        Scene transferScene = new Scene(transferBox, 400, 300);
        transferStage.setScene(transferScene);
        transferStage.show();
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
                "            var startId = \"" + startStationId + "\";\n" +
                "            var endId = \"" + endStationId + "\";\n" +
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
        startStationId = startStation;
        endStationId = endStation;

        if (startStation == null || endStation == null) {
            resultTextArea.setText("请选择起点站和终点站");
            return;
        }

        if (startStation.equals(endStation)) {
            resultTextArea.setText("起点站和终点站不能相同");
            return;
        }

        StringBuilder resultBuilder = new StringBuilder(startStation+"到"+endStation+"终点站的最短路径:\n");
        List<String> shortestPath = subwaySystem.findShortestPath(startStation, endStation);
        resultBuilder.append(shortestPath.toString()).append("\n\n");
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
