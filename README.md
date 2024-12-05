[![](https://jitpack.io/v/AIDEProjects/GameDevFramework.svg)](https://jitpack.io/#AIDEProjects/GameDevFramework)

# GameDevFramework 0.1.7-alpha
简易AndroidGame开发库框架，使用opengles2.0

# 内容: 
1. GLGameView: 游戏视图基类
	- 在create()初始化
	- 在render()帧绘制
1. ShapeBatch: 几何图形绘制类
	- drawRect()绘制矩形
	- drawStrokeRect()绘制空心矩形
	- drawCircle()绘制圆
	- drawStrokeCircle()绘制空心圆
	- drawLine()绘制线
1. OrthoCamera: 相机类，操作视图矩阵
	- VpMatrix()获取当前视图投影矩阵
	- getViewportSize()获取视图宽高
	- translate()相机位移
	- scale()相机拉伸

# 待办: 

# 更新
## 0.1.7-alpha: 代码优化与调整
- **GLGameView.java**：
	- 新增 `coordSign` 变量，表示坐标系统符号。
	- 增加 `coordinatesSigned()` 方法，用于返回坐标符号。
	- 对 `stageSize` 和 `viewportSize` 变量初始化进行了调整，优化代码结构。
- **OrthoCamera.java**：
	- 移除了 `SclTranslate()` 方法，简化视图矩阵的更新逻辑。
	- 修改 `updateMatrix()` 方法，移除缩放偏移量的应用，确保矩阵变换更加直接。

## 0.1.6: 增加生命周期log启用以及同步新版appdevf_0.6.6手势处理器代码
1. GLGameView 增加静态块来设置TAG.LifeCycle相关log启用
	- 删除旧gestureHandler，使用appdevf0.6.6的新版
	- - 更新gestureHandler相关代码以适应新版接口

## 0.1.5: 转换视口到像素坐标系并同步Shape绘制相关
1. 统一GLGameView的坐标系：使用左下到右上坐标系，大小为0~width,0~height
	- 调整Shape顶点组坐标范围回-0.5~0.5
	- 调整Shape.draw..的坐标范围，增加toAlignRec方法获取对齐方式对应坐标
1. 增加生命周期日志用于检查程序执行顺序
	- 使用Log.banTags.put(GLGameView.TAG.LifeCycle, boo);启用/禁用

## 0.1.4: 封装矩阵变换到renderer.camera

## 0.1.3: 更新appdevf版本到0.6.1-alpha

## 0.1.2: 
### 游戏视图基类GLGameView与视口操作与基本几何图形
1. 设计GLGameView以作为GL游戏视图：并绘制所有Geometry演示
	- 通过抽象接口create()初始化，render()帧绘制
1. 创建GestureHandler用于操作双指移动缩放视图视口属性
1. 创建ShapeBatch用于绘制几何图形
	- 现有如下图形: 
	- - StrokeCircle空心圆, 
	- - Circle实心圆, 
	- - StrokeRectangle空心矩形, 
	- - Rectangle实心矩形, 
	- - Line线
	- - ShapeGroup图形组: 空心矩形+中心圆圈，并有connect方法绘制一条两图形组中心圈连接线


