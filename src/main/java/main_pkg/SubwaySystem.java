package  main_pkg;
import data_struct.Edge;
import data_struct.Station;
import data_struct.StationDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class SubwaySystem {
    private Map<String, Station> stations;
    private Map<String, List<String>> lineToStations;

    public SubwaySystem() {
        stations = new HashMap<>();
        lineToStations = new HashMap<>();
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String lineName = parts[0];
                    String station1 = parts[1];
                    String station2 = parts[2];
                    double distance = Double.parseDouble(parts[3]);

                    addStation(station1);
                    addStation(station2);
                    addEdge(station1, station2, distance, lineName);

                    lineToStations.computeIfAbsent(lineName, k -> new ArrayList<>());
                    if (!lineToStations.get(lineName).contains(station1)) {
                        lineToStations.get(lineName).add(station1);
                    }
                    if (!lineToStations.get(lineName).contains(station2)) {
                        lineToStations.get(lineName).add(station2);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStation(String name) {
        stations.putIfAbsent(name, new Station(name));
    }

    public void addEdge(String station1, String station2, double distance, String line) {
        Station s1 = stations.get(station1);
        Station s2 = stations.get(station2);
        if (s1 != null && s2 != null) {
            s1.addNeighbor(s2, distance, line);
            s2.addNeighbor(s1, distance, line);
        }
    }

    public Set<Map.Entry<String, Set<String>>> getTransferStations() {
        Map<String, Set<String>> transferStations = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : lineToStations.entrySet()) {
            String line = entry.getKey();
            for (String station : entry.getValue()) {
                transferStations.computeIfAbsent(station, k -> new HashSet<>()).add(line);
            }
        }
        return transferStations.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toSet());
    }

    public Set<StationDistance> getStationsWithinDistance(String startStation, double n) {
        Set<StationDistance> result = new HashSet<>();
        Station start = stations.get(startStation);
        if (start == null) {
            throw new IllegalArgumentException("起始站点不存在");
        }

        Queue<StationDistance> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new StationDistance(start, 0, ""));
        visited.add(start.getName());

        while (!queue.isEmpty()) {
            StationDistance current = queue.poll();
            if (current.distance <= n && current.distance > 0) {
                result.add(current);
            }
            if (current.distance + 1 <= n) {
                for (Edge edge : current.station.getEdges()) {
                    if (!visited.contains(edge.getNeighbor().getName())) {
                        visited.add(edge.getNeighbor().getName());
                        queue.add(new StationDistance(edge.getNeighbor(), current.distance + edge.getDistance(), edge.getLine()));
                    }
                }
            }
        }
        return result;
    }

    public List<List<String>> findAllPaths(String start, String end) {
        List<List<String>> paths = new ArrayList<>();
        Station startStation = stations.get(start);
        Station endStation = stations.get(end);
        if (startStation == null || endStation == null) {
            throw new IllegalArgumentException("站点不存在");
        }
        dfs(startStation, endStation, new ArrayList<>(), paths);
        return paths;
    }

    private void dfs(Station current, Station end, List<String> path, List<List<String>> paths) {
        path.add(current.getName());
        if (current.equals(end)) {
            paths.add(new ArrayList<>(path));
        } else {
            for (Edge edge : current.getEdges()) {
                if (!path.contains(edge.getNeighbor().getName())) {
                    dfs(edge.getNeighbor(), end, path, paths);
                }
            }
        }
        // 修改为使用 remove 方法移除最后一个元素
        path.remove(path.size() - 1);
    }

    public List<String> findShortestPath(String start, String end) {
        Map<String, Double> distance = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<StationDistance> queue = new PriorityQueue<>(Comparator.comparingDouble(sd -> sd.distance));

        for (Station station : stations.values()) {
            distance.put(station.getName(), Double.MAX_VALUE);
        }
        distance.put(start, 0.0);
        queue.add(new StationDistance(stations.get(start), 0, ""));

        while (!queue.isEmpty()) {
            StationDistance current = queue.poll();
            String currentName = current.station.getName();
            if (current.distance > distance.get(currentName)) {
                continue;
            }
            if (currentName.equals(end)) {
                break;
            }
            for (Edge edge : current.station.getEdges()) {
                Station neighbor = edge.getNeighbor();
                double newDist = distance.get(currentName) + edge.getDistance();
                if (newDist < distance.get(neighbor.getName())) {
                    distance.put(neighbor.getName(), newDist);
                    prev.put(neighbor.getName(), currentName);
                    queue.add(new StationDistance(neighbor, newDist, edge.getLine()));
                }
            }
        }
        return buildPath(prev, end);
    }

    private List<String> buildPath(Map<String, String> prev, String end) {
        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public String printPath(List<String> path) {
        if (path.isEmpty()) {
            System.out.println("未找到路径");
            return "";
        }
        String output_s="";
        String currentLine = "";
        // 修改为使用 get(0) 方法获取第一个元素
        String startStation = path.get(0);

        for (int i = 1; i < path.size(); i++) {
            String prev = path.get(i - 1);
            String current = path.get(i);
            Station prevStation = stations.get(prev);
            for (Edge edge : prevStation.getEdges()) {
                if (edge.getNeighbor().getName().equals(current)) {
                    if (currentLine.isEmpty()) {
                        currentLine = edge.getLine();
                    } else if (!currentLine.equals(edge.getLine())) {
                        System.out.printf("坐%s号线从%s站到%s站，在%s站换乘", currentLine, startStation, prev, prev);
                        output_s+="坐"+currentLine+"从"+startStation+"站到"+prev+"站，在"+prev+"站换乘，";
                        currentLine = edge.getLine();
                        startStation = prev;
                    }
                    break;
                }
            }
        }
        // 修改为使用 get 方法获取最后一个元素
        System.out.printf("%s号线到%s站\n", currentLine, path.get(path.size() - 1));
        // 修改为使用 get 方法获取最后一个元素
        output_s+="乘坐"+currentLine+"到"+path.get(path.size() - 1)+"站\n";
        return output_s;
    }

    public double calculateFare(List<String> path) {
        double totalDistance = 0;
        for (int i = 1; i < path.size(); i++) {
            String prev = path.get(i - 1);
            String current = path.get(i);
            Station prevStation = stations.get(prev);
            for (Edge edge : prevStation.getEdges()) {
                if (edge.getNeighbor().getName().equals(current)) {
                    totalDistance += edge.getDistance();
                    break;
                }
            }
        }
        if (totalDistance <= 9) {
            return 2;
        } else if (totalDistance <= 14) {
            return 3;
        } else {
            return 3 + Math.ceil((totalDistance - 14) / 5);
        }
    }

    public double calculateWuhanTongFare(List<String> path) {// 假设武汉通票的折扣为80%
        double fare = calculateFare(path);
        return fare * 0.8;
    }

    public double calculateDayTicketFare(List<String> path) {// 假设日票的折扣为70%
        double fare = calculateFare(path);
        return fare * 0.7;
    }

    public Map<Object, Object> getStations() {
        Map<Object, Object> result = new HashMap<>();
        for (Map.Entry<String, Station> entry : stations.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getEdges());
        }
        return result;
    }
}


