# VehicleSim2026_P4_Demo_02 Documentation

本文档总结当前项目的实际代码结构、运行逻辑，以及源码中显式标注 `Codex` / `CodeX` / `codex` 的片段归属。本文内的 citation 使用脚注格式，引用的是当前工作区源码文件与行号。[^1]

## 1. 项目定位

`VehicleSim2026_P4_Demo_02` 是一个基于 Greenfoot 的车道战斗模拟项目。它保留了课堂车辆模拟的道路、刷怪和行人骨架，但当前版本的核心玩法已经变成了“装甲车辆对抗地雷投放者，同时兼顾平民救援与空袭事件”的混合系统。世界负责生成车辆和行人、更新计数器、维护计时器，并在 `MineDropper` 击杀数达到阈值时触发空袭。[^2]

## 2. 当前版本的核心系统

### 2.1 世界与生成系统

`VehicleWorld` 定义了四车道、双向道路、背景碰撞遮罩、计数器和全局循环。当前刷新的车辆类型是 `MineCleaner`、`Ambulance`、`Ifv` 和 `Tank`；行人类型是 `MineDropper` 和 `Civilian`。世界每帧会执行刷怪、`zSort`、FPS 统计、平民/敌军计数更新、空袭触发和计时器更新。[^2]

其中，空袭提示 `"Air Strike Arrived"` 会在空袭触发前显示在屏幕中央 60 帧，实际的空袭触发条件是 `MineDropper` 击杀数每达到 20 的倍数且尚未触发过该阈值。[^3]

### 2.2 车辆系统

`Vehicle` 现在已经不是简单的课堂模板基类，而是带有血量、血条、跟车、变道、地雷触发、车辆碰撞和伤害处理的综合基类。车辆在 `act()` 中会依次执行驾驶、行人交互、出界移除、地雷触发、车辆碰撞和血条刷新。[^4]

当前各车辆职责如下：

- `Tank`：生成独立的 `TankTower`，与炮塔联动攻击目标，碾压接触到的行人。[^5]
- `Ifv`：生成 `IfvTower`，对 `MineDropper` 造成接触伤害；遇到 `Civilian` 时停车救援，完成后增加 `civilianSaved` 计数。[^6]
- `MineCleaner`：检测前方地雷，停车等待一段时间后直接移除地雷，再继续行驶。[^7]
- `Ambulance`：记录已处理过的行人，接触倒地行人时执行治疗。[^8]

### 2.3 行人系统

`Pedestrian` 现在包含血量、动画帧、倒地、持续掉血、死亡倒计时和血条。醒着的行人会尝试沿垂直方向前进，并在移动前先检查前方是否有车辆以及目标位置是否被背景碰撞遮罩阻挡。[^9]

当前两类行人分别承担不同目标：

- `MineDropper`：沿纵向移动，并在靠近车道时按概率放置 `Mine`；其普通死亡会增加 `mineDropperKilledCount`，空袭致死则走单独分支，不计入普通击杀统计。[^10]
- `Civilian`：不放置地雷，死亡时增加 `civilianKilledCount`。[^11]

### 2.4 武器、炮塔与空袭系统

`Tower` 是坦克类武器系统的核心。它会跟随车体移动、寻找最近的清醒 `MineDropper`、按每帧最大转角缓慢瞄准、在角度足够接近时开火，并根据已发射炮弹回传的命中结果切换侦察模式与实弹模式。[^12]

`TankShell` 根据 `type` 不同拥有不同速度、伤害与爆炸效果，并能命中车辆、`MineDropper` 和 `Civilian`。[^13]

世界级空袭链路如下：

1. `VehicleWorld` 发现 `MineDropper` 击杀达到阈值时，先显示屏幕提示，再生成 `AirStrike`。[^3]
2. `AirStrike` 先等待，再生成 `F15` 飞越，随后为世界中的每辆车生成一枚 `JDAM`。[^14]
3. `JDAM` 追踪目标并在接触目标时生成空袭专用 `Explosion`。[^15]
4. `Explosion` 根据 `isAirStrike` 分流到普通伤害逻辑或空袭伤害逻辑；空袭分支会对车辆调用 `damageByAirStrike()`，对 `MineDropper` 调用 `damageByAirStrike()`。[^16]

### 2.5 UI 与计数系统

当前 UI 主要由 `Counter` 与 `ScreenMessage` 组成。`VehicleWorld` 已经常驻显示 `FPS`、`Civilian Saved`、`Civilian Killed`、`Mine Dropper killed`、以及小时/分钟/秒计时器。空袭提示由 `ScreenMessage` 单独负责，它是一个限时存在的中心横幅提示 actor。[^2][^17]

## 3. 明确标注为 Codex 编写的代码片段

本节只说明源码中已经明确写有 `Codex` / `CodeX` / `codex` 注释的片段。换句话说，下面列出的部分都可以直接说明为“由 Codex 编写的代码片段”；未标注的其他代码即使后来被调整过，也不在这个“显式标注”范围内。[^18]

### 3.1 `VehicleWorld.java`

以下片段在源码中已经明确写有 `Codex change` 注释，可说明为 Codex 编写：

- 行人生成尝试次数、生成边距和碰撞遮罩字段：用于让行人在出生前避开背景障碍。[^19]
- `trySpawnPedestrian()`：反复尝试生成行人，直到找到未与遮罩冲突的位置。[^20]
- `isPedestrianAreaBlocked(...)`：逐像素检查背景碰撞遮罩，决定某个行人位置是否合法。[^21]

### 3.2 `Pedestrian.java`

`canMoveTo(...)` 在源码注释中明确写有 `Codex change`，这一方法负责把行人移动和 `VehicleWorld` 的背景碰撞遮罩连接起来。[^22]

### 3.3 `Tower.java`

`Tower` 中以下标注片段可说明为 Codex 编写：

- 车体不存在时，炮塔自我移除的保护逻辑。[^23]
- 利用 `atan2` 计算目标角度、限制每帧最大转角并进行平滑瞄准的逻辑。[^24]
- `normalizeAngle(...)`：把角度差归一化到 `[-180, 180]` 区间。[^25]

### 3.4 `ScreenMessage.java`

整个 `ScreenMessage` 文件开头就写有 `This part is created by codex`，可以直接说明这个文件是 Codex 编写的 UI 提示 actor。它负责构建一块半透明提示条，并在倒计时结束后自我移除。[^26]

### 3.5 `JDAM.java`

`JDAM` 构造函数里关于炸弹图像的绘制代码，源码中写有 `This part is created by CodeX`，这一段可以明确归为 Codex 编写。[^27]

## 4. 建议的引用写法

如果你之后要在 handoff、展示文档、说明书里引用这些内容，可以直接用下面这种写法：

- “The pedestrian collision-mask spawning logic in `VehicleWorld` was implemented in a Codex-authored block.”[^20][^21]
- “The turret angle normalization and incremental aiming logic in `Tower` are from Codex-marked code sections.”[^24][^25]
- “The centered `Air Strike Arrived` banner is implemented by the Codex-authored `ScreenMessage` actor.”[^3][^26]

如果你要更正式一点，也可以在文末单独放一个 `Sources` 或 `Citations` 小节，直接保留本文脚注格式即可。[^1]

## 5. 脚注与 Citation Index

[^1]: Citation format for this document: source file path plus line ranges from the current workspace snapshot.
[^2]: Source: `VehicleWorld.java` lines 31-72, 83-147, 327-337, 370-430.
[^3]: Source: `VehicleWorld.java` lines 361-366.
[^4]: Source: `Vehicle.java` lines 44-75, 111-158, 258-347.
[^5]: Source: `Tank.java` lines 6-21, 27-52.
[^6]: Source: `Ifv.java` lines 13-34, 40-74, 76-121.
[^7]: Source: `MineCleaner.java` lines 6-18, 22-43, 45-87, 111-139.
[^8]: Source: `Ambulance.java` lines 13-24, 26-50.
[^9]: Source: `Pedestrian.java` lines 19-39, 41-69, 79-133, 156-275.
[^10]: Source: `MineDropper.java` lines 6-18, 20-49, 51-94.
[^11]: Source: `Civilian.java` lines 9-44.
[^12]: Source: `Tower.java` lines 46-109, 112-171.
[^13]: Source: `TankShell.java` lines 17-50, 53-126.
[^14]: Source: `AirStrike.java` lines 14-76.
[^15]: Source: `JDAM.java` lines 16-57.
[^16]: Source: `Explosion.java` lines 33-53, 59-80, 90-152.
[^17]: Source: `Counter.java` lines 33-126; `ScreenMessage.java` lines 1-37.
[^18]: Source marker scan from comments in: `VehicleWorld.java`, `Pedestrian.java`, `Tower.java`, `ScreenMessage.java`, `JDAM.java`.
[^19]: Source: `VehicleWorld.java` lines 35-47.
[^20]: Source: `VehicleWorld.java` lines 394-418.
[^21]: Source: `VehicleWorld.java` lines 433-466.
[^22]: Source: `Pedestrian.java` lines 261-275.
[^23]: Source: `Tower.java` lines 50-58.
[^24]: Source: `Tower.java` lines 79-86.
[^25]: Source: `Tower.java` lines 100-109.
[^26]: Source: `ScreenMessage.java` lines 1-37.
[^27]: Source: `JDAM.java` lines 19-30.
