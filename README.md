# 地铁系统项目

## 项目简介
本项目是一个地铁系统，主要用于模拟地铁线路的管理和路径规划。通过加载地铁线路和站点距离数据，实现中转站识别、查询指定距离内站点、查找所有路径、查找最短路径、打印路径以及计算不同类型票价等功能。

## 功能特性
1. **中转站识别**：能够识别出地铁线路中的所有中转站。
2. **站点距离查询**：查询距离指定站点小于特定距离的所有站点。
3. **路径查找**：支持查找两个站点之间的所有路径和最短路径。
4. **路径打印**：将最短路径以易读的方式打印输出。
5. **票价计算**：计算普通单程票、武汉通票和日票的票价。

## 运行步骤
### 环境要求
- Java开发环境
- Maven（如果使用Maven进行项目管理）

### 运行命令
1. 确保你已经安装了Java和Maven。
2. 打开终端，进入项目根目录 `E:\作业\java\last\RouteMap`。
3. 运行以下命令编译项目：
```sh
mvn compile
```
4. 运行测试类 `Test.java` 来测试项目功能：
```sh
mvn exec:java -Dexec.mainClass="main_pkg.Test"
```

## 目录结构
```
RouteMap/
├── .git/                  # Git版本控制目录
├── .idea/                 # IntelliJ IDEA配置目录
├── HTML/                  # HTML相关文件
├── doc/                   # 项目文档
├── file/                  # 数据文件
│   ├── all_lines_station_distance.csv  # 地铁线路和站点距离数据
│   ├── javafx-sdk-24.0.1/  # JavaFX SDK
│   └── subway.txt         # 地铁相关文本数据
├── src/                   # 源代码目录
│   ├── main/              # 主程序代码
│   │   ├── java/          # Java源代码
│   │   └── resources/     # 资源文件
│   └── test/              # 测试代码
│       └── java/          # Java测试代码
├── target/                # 编译输出目录
├── .gitignore             # Git忽略文件
├── README.md              # 项目说明文件
├── SubwaySystemClassDiagram.md  # 类图说明文件
├── pom.xml                # Maven项目配置文件
```

## 使用示例
### 测试代码示例
```java
public class Test {
    public static void main(String[] args) {
        SubwaySystem subwaySystem = new SubwaySystem();
        subwaySystem.loadFromFile("file/all_lines_station_distance.csv");

        // 测试识别中转站
        Set<Map.Entry<String, Set<String>>> transferStations = subwaySystem.getTransferStations();
        System.out.println("所有中转站:");
        for (Map.Entry<String, Set<String>> entry : transferStations) {
            System.out.println("<" + entry.getKey() + ", <" + String.join("、", entry.getValue()) + ">>");
        }

        // 测试查询距离小于n的站点
        try {
            Set<StationDistance> stationsWithinDistance = subwaySystem.getStationsWithinDistance("华中科技大学", 1);
            System.out.println("距离华中科技大学站距离为1的站点:");
            System.out.println(stationsWithinDistance);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // 测试查找最短路径
        try {
            List<String> shortestPath = subwaySystem.findShortestPath("华中科技大学", "径河");
            System.out.println("起点站到终点站的最短路径:");
            System.out.println(shortestPath);

            // 测试打印路径
            System.out.println("最短路径打印结果:");
            subwaySystem.printPath(shortestPath);

            // 测试计算票价
            double fare = subwaySystem.calculateFare(shortestPath);
            System.out.println("普通单程票票价: " + fare + " 元");

            // 测试计算武汉通票价
            double wuhanTongFare = subwaySystem.calculateWuhanTongFare(shortestPath);
            System.out.println("武汉通票价: " + wuhanTongFare + " 元");

            // 测试计算日票票价
            double dayTicketFare = subwaySystem.calculateDayTicketFare(shortestPath);
            System.out.println("日票票价: " + dayTicketFare + " 元");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
```

## 注意事项
- 确保 `file/all_lines_station_distance.csv` 文件存在且格式正确，否则数据加载可能会失败。
- 在计算票价时，武汉通票默认折扣为80%，日票默认折扣为70%，可根据实际情况修改代码中的折扣比例。