package main_pkg;

import data_struct.StationDistance;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubwayGUI {
    private SubwaySystem subwaySystem;
    private JComboBox<String> startStationComboBox;
    private JComboBox<String> endStationComboBox;
    private JTextArea resultTextArea;

    public SubwayGUI() {
        subwaySystem = new SubwaySystem();
        subwaySystem.loadFromFile("file/all_lines_station_distance.csv");

        JFrame frame = new JFrame("地铁线路查询系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel startLabel = new JLabel("起点站:");
        startStationComboBox = new JComboBox<>();
        initializeStationComboBox(startStationComboBox);

        JLabel endLabel = new JLabel("终点站:");
        endStationComboBox = new JComboBox<>();
        initializeStationComboBox(endStationComboBox);

        JButton shortestPathButton = new JButton("查找最短路径");
        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findShortestPath();
            }
        });

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        panel.add(startLabel);
        panel.add(startStationComboBox);
        panel.add(endLabel);
        panel.add(endStationComboBox);
        panel.add(shortestPathButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void initializeStationComboBox(JComboBox<String> comboBox) {
        Set<Object> stationNames = subwaySystem.getStations().keySet();
        for (Object station : stationNames) {
            comboBox.addItem((String) station);
        }
    }

    private void findShortestPath() {
        String start = (String) startStationComboBox.getSelectedItem();
        String end = (String) endStationComboBox.getSelectedItem();

        if (start != null && end != null) {
            try {
                List<String> shortestPath = subwaySystem.findShortestPath(start, end);
                resultTextArea.setText("起点站到终点站的最短路径:\n" + subwaySystem.printPath(shortestPath) + "\n\n");

                // 计算票价
                double fare = subwaySystem.calculateFare(shortestPath);
                double wuhanTongFare = subwaySystem.calculateWuhanTongFare(shortestPath);
                double dayTicketFare = subwaySystem.calculateDayTicketFare(shortestPath);

                resultTextArea.append("普通单程票票价: " + fare + " 元\n");
                resultTextArea.append("武汉通票价: " + wuhanTongFare + " 元\n");
                resultTextArea.append("日票票价: " + dayTicketFare + " 元\n");
            } catch (IllegalArgumentException e) {
                resultTextArea.setText(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SubwayGUI();
            }
        });
    }
}