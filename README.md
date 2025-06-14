# 地铁系统项目

## 项目简介

本项目是一个地铁系统，主要用于模拟武汉地铁线路的管理和路径规划。通过加载地铁线路和站点距离数据，实现中转站识别、查询指定距离内站点、查找所有路径、查找最短路径、打印路径以及计算不同类型票价等功能。

## 功能特性

1. **中转站识别**：能够识别出地铁线路中的所有中转站。
2. **站点距离查询**：查询距离指定站点小于特定距离的所有站点。
3. **路径查找**：支持查找两个站点之间的所有路径和最短路径。
4. **最短路径规划**：将最短路径以易读的方式打印输出，并且采用可视化效果展示路径规划详情。
5. **票价计算**：计算普通单程票、武汉通票和日票的票价。

## 项目亮点

* **高可视化**：调用高德地铁图api，内嵌HTML+JavaScript，实现目标路径高亮展示
* **简明易用**：使用javafx控件实现图形编程，提高易用性

## 环境要求

- Java17—openjdk24开发环境
- JavaFX SDK 24.0.1
- Maven

## 目录结构

```
RouteMap/
├── .git/                  # Git版本控制目录
├── .idea/                 # IntelliJ IDEA配置目录
├── doc/                   # 项目文档
├── source/                  # 数据文件
│   ├── all_lines_station_distance.csv  # 地铁线路和站点距离数据
│   └── subway.txt         # 地铁相关文本数据
├── src/                   # 源代码目录
│   ├── main/              # 主程序代码
│   │   ├── java/          # Java源代码
│   │   │   └── main_pkg/  # 主程序包
│   │   │   │   ├──RouteMap_Gui.java  # 图形化界面
│   │   │   │   ├── SubwaySystem.java  # 地铁系统类
│   │   │   │   ├──Test.java     # 测试类  
│   │   │   ├── data_struct      #数据结构包
│   │   │   │   ├── Edge.java  # 边类
│   │   │   │   ├── Station.java # 车站类
│   │   │   │   ├── StationDistance.java  # 距离类
│   │   ├── resources/     # 资源文件
│   │   └──module-info.java         # Java模块信息
│   └── test/              # 测试代码
│       └── java/          # Java测试代码
├── target/                # 编译输出目录
├── .gitignore             # Git忽略文件
├── README.md              # 项目说明文件
├── pom.xml                # Maven项目配置文件
```

## 演示效果

### 路径规划效果展示

![1749211600429](image/README/1749211600429.gif)

### 查找附近站点功能展示

![1749211653111](image/README/1749211653111.gif)

### 查找所有中转站功能展示

![1749211692189](image/README/1749211692189.gif)

ps：觉得不错可以给个高分吗⸜(๑'ᵕ'๑)⸝⋆*
