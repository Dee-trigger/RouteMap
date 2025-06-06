package main_pkg;
import data_struct.StationDistance;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        SubwaySystem subwaySystem = new SubwaySystem();
        subwaySystem.loadFromFile("source/all_lines_station_distance.csv");

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

        // 测试查找所有路径
//        try {
//            List<List<String>> allPaths = subwaySystem.findAllPaths("华中科技大学", "汉口火车站");
//            System.out.println("起点站到终点站的最短路径:");
//            for (List<String> path : allPaths) {
//                System.out.println(path);
//            }
//        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
//        }

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