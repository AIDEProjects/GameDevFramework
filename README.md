[![](https://jitpack.io/v/AIDEProjects/GameDevFramework.svg)](https://jitpack.io/#AIDEProjects/GameDevFramework)

# GameDevFramework 0.1.2
简易AndroidGame开发库框架，使用opengles2.0

# 待办: 
1. 统一GLGameView的坐标系：使用左下到右上坐标系，大小为0~width,0~height

# 更新
## 0.1.2: 
### 游戏视图基类GLGameView与视口操作与基本几何图形
1. 设计GLGameView以作为GL游戏视图：并绘制所有Geometry演示
- - 通过抽象接口create()初始化，render()帧绘制
2. 创建GestureHandler用于操作双指移动缩放视图视口属性
3. 创建ShapeBatch用于绘制几何图形
- - 现有如下图形: 
- - - StrokeCircle空心圆, 
- - - Circle实心圆, 
- - - StrokeRectangle空心矩形, 
- - - Rectangle实心矩形, 
- - - Line线
- - - ShapeGroup图形组: 空心矩形+中心圆圈，并有connect方法绘制一条两图形组中心圈连接线


