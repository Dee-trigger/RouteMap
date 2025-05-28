import plotly.graph_objects as go
import networkx as nx
import pandas as pd

# 读取 CSV 文件
csv_file_path = "file/all_lines_station_distance.csv"  # 替换为实际的 CSV 文件路径
df = pd.read_csv(csv_file_path)

# 创建一个空的无向图
G = nx.Graph()

# 从 CSV 数据中提取站点和线路信息
stations = set()
edges = []
for index, row in df.iterrows():
    start_station = row[1]
    end_station = row[2]
    stations.add(start_station)
    stations.add(end_station)
    edges.append((start_station, end_station))

# 添加站点节点
G.add_nodes_from(stations)

# 添加线路边
G.add_edges_from(edges)

# 为节点设置位置，这里简单使用 spring_layout 算法
pos = nx.spring_layout(G)

# 提取节点位置
node_x = []
node_y = []
for node in G.nodes():
    x, y = pos[node]
    node_x.append(x)
    node_y.append(y)

# 创建节点散点图
node_trace = go.Scatter(
    x=node_x, y=node_y,
    mode='markers+text',
    hoverinfo='text',
    marker=dict(
        showscale=False,
        color='lightblue',
        size=50
    ),
    text=list(G.nodes()),
    textposition="top center"
)

# 提取边位置
edge_x = []
edge_y = []
for edge in G.edges():
    x0, y0 = pos[edge[0]]
    x1, y1 = pos[edge[1]]
    edge_x.extend([x0, x1, None])
    edge_y.extend([y0, y1, None])

# 创建边线图
edge_trace = go.Scatter(
    x=edge_x, y=edge_y,
    line=dict(width=2, color='gray'),
    hoverinfo='none',
    mode='lines'
)

# 创建图表
fig = go.Figure(data=[edge_trace, node_trace],
                layout=go.Layout(
                    # 使用新的 title 嵌套结构设置标题和字体
                    title=dict(
                        text='地铁线路图',
                        font=dict(size=16)
                    ),
                    showlegend=False,
                    hovermode='closest',
                    margin=dict(b=20, l=5, r=5, t=40),
                    xaxis=dict(showgrid=False, zeroline=False, showticklabels=False),
                    yaxis=dict(showgrid=False, zeroline=False, showticklabels=False))
                )

# 保存为 HTML 文件
fig.write_html("HTML/subway_map.html")
