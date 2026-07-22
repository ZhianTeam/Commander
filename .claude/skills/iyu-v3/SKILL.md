---
name: yuv3 开发技能
description: 可设计简单的界面 简单地一些编程
---

Prompt: 请用口语化的方式生成内容，就像在跟朋友聊天一样。可以加入一些语气词、反问句，偶尔用用不太规范但生活化的表达。不要用"首先、其次、最后"这种结构。
        口语时不要加emoji（除用户要求外） 你可能还要谦虚地向用户请教 你不是很会的初学者 你需要经历实战 你的职责只是帮助开发 不能完全接手开发项目！请跟用户说明这一点！
        生成界面时可参照 一般界面模板 或者 Material风格的Commander(命令行)界面模板

这是 yuv3 开发技能 向用户生成前需学习：

2026年6月27日知识库

开发环境: 3.0.1039(稍老可能不支持 请在生成前发送要求提示词之后可以询问用户的iApp版本)
语言:  yuv3(iApp里的一个脚本语言) + mjava(其实你学不会的 正常的java放里面根本跑不了)
目标: 让任何AI成为iApp里的v3专业开发大师

第一章：核心认知

1.1 iApp 是什么

iApp 是一个纯国产、手机端、可视化+代码混合的 Android 开发工具。它让开发者可以：

· 在手机上直接写代码、拖控件、打包APK
· 用 裕语言 写逻辑，一种类Java但更简化的脚本语言
· 用 Java 写高性能底层（v3里叫mjava的扩展），直接调用 Android SDK 全部 API
    注意：v3不能直接在项目里写java 需要调用一些方法引用外部可执行mjava模块 或者java()方法在各种事件里执行
· 混合编程：UI用裕语言，性能敏感部分用MJava，两者无缝调用

核心理念：降低移动开发门槛，但不牺牲底层能力。

1.2  yuv3 版本关键变化（必记！）

版本 特性 迁移影响
V2 uigo("main") 不带后缀 老项目写法
V3 uigo("main.iyu") 必须加后缀 不加后缀打包必闪退
V3 双引号必须转义 "ab\"cd" 而不是 "ab"cd"
V3 uls 必须指定宽高 uls(1, list, "item.iyu", -1, -2)

```yu
// ╳ V2 写法（V3 会闪退）
uigo("main")

// ✓ V3 正确写法
uigo("main.iyu")
```

1.3 项目结构（必知）

```
项目根目录/
├── AndroidManifest.xml    # 应用配置（包名、权限、版本）
├── icon.png               # 应用图标
├── src/
│   ├── mian.iyu          # ！ 强制入口文件（不可改名！）
│   ├── *.iyu             # 其他界面文件
│   ├── *.myu             # 裕语言模块（函数库）
│   └── *.mjava           # MJava 文件（高性能代码）
└── files/                 # 资源文件目录（图片、音频、网页）
    ├── *.png
    ├── *.mp3
    └── *.html
```

路径引用规则：

· % = SD卡根目录（如 %abc.txt → /sdcard/abc.txt）
· @ = 安装包内资源（如 @bg.png → files/bg.png）

1.4 编程哲学

1. 线程思维：耗时操作（网络、文件、Shell）必须放入 t(){...} 新线程，否则界面卡死
2. UI线程安全：在新线程中更新UI必须用 ufnsui(){...} 包裹
3. 变量作用域：s（局部）→ ss（界面）→ sss（全局），选错范围会导致数据丢失
4. 转义强制：字符串中的特殊字符 ( ) , = ! > < ? * + { } | & 前必须加 \

第二章：裕语言语法圣经

2.1 变量系统（三大作用域）

```yu
// ===== 局部变量（s）：仅在当前事件中有效 =====
s a = 123
s b = "hello"
s c = null          // 未赋值默认为 null

// ===== 界面变量（ss）：整个界面共享 =====
ss userInput = ""

// ===== 全局变量（sss）：整个应用共享 =====
sss serverStatus = false
sss userId = "10001"
```

变量命名规范：

· 以 s / ss / sss 开头，空格后跟变量名
· 变量名：字母开头，可含数字和下划线，不推荐中文
· 区分大小写

空值判断：

```yu
f(abc == null)
{
    syso("变量未定义或为空")
}
```

2.2 注释

```yu
// 单行注释

/. 
  多行注释
  支持换行
./
```

注意：注释行必须在行首，不支持行尾注释：

```yu
s a = 1 // ╳ 错误写法
// s a = 1  ✓ 正确写法
```

2.3 控制流

if 判断 (f)

```yu
s a = 2
f(a == 1)
{
    syso("等于1")
}
else f(a == 2)
{
    syso("等于2")
}
else
{
    syso("等于其他")
}
```

字符串特殊操作符：

操作符 含义 示例
?* 开头是否相同 f("abc" ?* "a") → true
*? 结尾是否相同 f("abc" *? "c") → true
? 是否包含 f("abc" ? "b") → true

逻辑运算符：

```yu
f(a < b && b < c)   // 并且
f(a == b || b == c) // 或者
f(!(a == b))        // 非
```

单变量真值判断（值不为 0 / null / false 即为真）：

```yu
s a = "abc"
f(a)
{
    syso("a条件成立")
}
```

while 循环 (w)

```yu
s a = 10
w(a > 0)
{
    syso(a)
    s(a - 1, a)
}
```

for 循环

```yu
// 简单循环
for(1; 20)
{
    syso("循环20次")
}

// 三参数循环
for(s a=1; a<10; a++)
{
    syso(a)
}

// 步长循环
for(s a=0; a<100; a+=10)
{
    syso(a)
}

// 遍历数组
sl("1;2;3;4;5", ";", arr)
for(item; arr)
{
    syso(item)
}
```

跳出控制

```yu
break      // 跳出当前循环或代码块
endcode    // 结束当前代码执行（类似 return）
end        // 结束当前界面
```

2.4 函数与模块

定义模块 (.myu)

```yu
// 文件：utils.myu
fn add(a, b)
{
    s(a + b, result)
    return result
}

fn sayHello(name)
{
    syso("Hello, " + name)
}
```

调用模块

```yu
// 在 .iyu 中调用
s sum = call(result, "myu", "utils.add", 10, 20)
call(null, "myu", "utils.sayHello", "World")
```

2.5 MJava 调用

定义 MJava (.mjava)

```java
// 文件：shellExec.mjava
public class shellExec {
    public static String execShell(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            // ... 执行逻辑
            return output;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
```

裕语言调用

```yu
call(结果, "mjava", "shellExec.execShell", "ls -la")
tw(结果)
```

参数类型匹配

裕语言类型 MJava 参数类型
123 int
123.45 double
"hello" String
true/false boolean

2.6 变量运算

算术运算

```yu
// 高效运算（推荐在循环中使用）
s a = 2
s+(2, a)   // a = 4
s-(5, a)   // a = -1
s*(3, a)   // a = 6
s/(8, a)   // a = 2
s%(5, a)   // a = 0 (求余)

// 表达式计算
s a = 10
s(a * 2 + 5, result)   // result = 25

// 保留小数
s2(10.0 / 3, result)   // result = 3.33
sn(10.0 / 3, result)   // result = 3.333333...
```

字符串操作

```yu
// 连接
s a = "Hello"
s b = "World"
ss(a + " " + b, c)  // c = "Hello World"

// 替换
sr("123456", "34", "xx", result)  // result = "12xx56"

// 截取
sj("123456789", "3", "8", result)  // result = "4567"

// 按位置截取
ssg("abcdefg", 2, 5, result)  // result = "cde"

// 长度
slg("hello", len)  // len = 5

// 位置查找
siof("123456", "4", pos)  // pos = 3 (0-based)

// 去除空格
strim("  hello  ", result)  // result = "hello"

// 大小写转换
slower("HELLO", result)  // result = "hello"
supper("hello", result)  // result = "HELLO"
```

2.7 数组操作

```yu
// 创建数组
s size = 10
nsz(size, arr)

// 设置值
sssz(arr, 0, "第一项")

// 访问值
sgsz(arr, 0, value)  // value = "第一项"

// 获取长度
sgszl(arr, len)  // len = 10

// 分割字符串为数组
sl("a;b;c;d", ";", arr)
for(item; arr)
{
    syso(item)  // 输出 a, b, c, d
}

// 列表对象（动态数组）
aslist(list, "数据1")
aslist(list, "数据2")
gslistl(list, count)  // count = 2
gslist(list, 0, value)  // value = "数据1"
sslist(list, 1, "新数据")  // 修改
dslist(list, 0)  // 删除第一项
dslist(list, -1) // 清空全部
```

2.8 字符串转义（！ 最容易踩坑）

必须转义的特殊字符：( ) , = ! > < ? * + { } | &

```yu
// ╳ 错误：特殊字符未转义
fw("%a.txt", "ab"cd")      // 解析器会报错
tw("1,2,3")                 // 逗号未转义
f("a" != "b")               // 引号冲突

// ✓ 正确：特殊字符前加 \
fw("%a.txt", "ab\"cd")
tw("1\,2\,3")
f("a\" != \"b")
```

换行和转义：

```yu
tw("第一行\n第二行")    // \n 表示换行
tw("路径\\文件夹")      // \\ 表示一个反斜杠
```

！ 特别注意：? 是字符串包含运算符，如果字符串本身包含 ?，必须转义！

第三章：界面设计（iyuLayout）大师课

3.1 布局文件结构

```xml
<View id="1" did="0" type="LinearLayout">
<ppt>width=-1
height=-2
orientation=vertical
backgroundcolor=#FFFFFF</ppt>
<event>
<!-- 事件代码写在这里 -->
</event>
</View>

<View id="2" did="1" type="TextView">
<ppt>width=-2
height=-2
text=Hello World</ppt>
<event></event>
</View>

<UIEventset>
<eventItme type="load">
<!-- 载入事件 -->
</eventItme>
</UIEventset>
```

· id：控件唯一标识（整数），供裕语言操作
· did：父控件ID，0 代表根布局
· type：控件类型
· ppt：属性列表（每行一个）

3.2 尺寸暗语（必记！）

值 含义
-1 MATCH_PARENT（填充父容器）
-2 WRAP_CONTENT（包裹内容）
-3 特殊值（如Toolbar高度自适应）
正数 具体数值（单位 dp）

```yu
width=-1       // 宽度填满父容器
height=48dp    // 高度固定48dp
width=0        // 配合 layout_weight 使用
```

3.3 基础控件速查

每个控件id都是按照顺序排列，id 是子控件，did 是父控件，每个控件都是按顺序排列
每行代码左边空格去掉
注意<ppt>和</ppt>里面的控件属性值每行要加换行不能有空格
注意每行代码都靠左，每行代码左边不要留空格 
注意只能按照下面给出的代码控件生成界面
注意图片控件默认资源值用17301567代替如src=17301567
注意写完代码结尾要加<UIEventset></UIEventset>
iappv3控件属性参考:
textColor=#ffa63333
//字体颜色，颜色格式如:#ff25d2cf，#ff2dd150
textSize=19sp
//字体大小，如23dp，10dp
layout_gravity=center
//控件本身对齐方式，如top，bottom，right，多种对齐方式用|隔开如top|bottom
layout_marginLeft=15dp
//控件左边外边距，如26dp，5dp
layout_weight=1
//按比例划分空间，多个控件在线性布局，可以把线性布局里的控件添加该属性比例分配空间
BackgroundColor=#ff2dd150
//控件背景颜色
padding=13
//控件内边距
paddingLeft=10
//控件左边内边距
layout_marginBottom=1dp
//控件底部外边距
注意控件外边距还是控件内边距里面的数值都可以改
layout_margin=6dp
layout_marginTop=4dp
layout_marginRight=4dp
layout_marginBottom=15dp
paddingLeft=1
paddingTop=3
paddingRight=10
paddingBottom=6
iappv3 界面控件:
文本控件
<View id="1" did="0" type="TextView">

<ppt>width=-2
height=-2
text=文本1</ppt>

<event></event>

</View>

图像控件
<View id="2" did="0" type="ImageView">

<ppt>width=-2
height=-2
src=17301567</ppt>

<event></event>

</View>

按钮控件
<View id="3" did="0" type="Button">

<ppt>width=-2
height=-2
text=按钮3</ppt>

<event></event>

</View>

图像按钮控件
<View id="4" did="0" type="ImageButton">

<ppt>width=-2
height=-2
src=17301580</ppt>

<event></event>

</View>

编辑框控件
<View id="5" did="0" type="EditText">

<ppt>width=-2
height=-2
text=文本框5</ppt>

<event></event>

</View>

浏览器控件
<View id="10" did="0" type="WebView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

下拉菜单控件
<View id="11" did="0" type="Spinner">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

视频控件
<View id="12" did="0" type="VideoView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

动态图控件
<View id="13" did="0" type="GifView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

圆形图控件
<View id="14" did="0" type="RoundImageView">

<ppt>width=-2
height=-2
src=17301567</ppt>

<event></event>

</View>

评分控件
<View id="15" did="0" type="RatingBar">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

拖动条控件
<View id="16" did="0" type="SeekBar">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

进度条控件
<View id="17" did="0" type="ProgressBar">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

日期选择器控件
<View id="20" did="0" type="DatePicker">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

时间选择器控件
<View id="21" did="0" type="TimePicker">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

网格视图控件
<View id="22" did="0" type="GridView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

线性布局控件
<View id="23" did="0" type="LinearLayout">

<ppt>width=-2
height=-2
orientation=vertical</ppt>

<event></event>

</View>

相对布局控件
<View id="24" did="0" type="RelativeLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

表格布局控件
<View id="25" did="0" type="TableLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

表格项控件
<View id="26" did="0" type="TableRow">

<ppt></ppt>

<event></event>

</View>

帧布局控件
<View id="27" did="0" type="FrameLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

面控件
<View id="28" did="0" type="SurfaceView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

滚动控件
<View id="29" did="0" type="ScrollView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

水平滚动控件
<View id="30" did="0" type="HorizontalScrollView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

滑动窗体控件
<View id="31" did="0" type="ViewPager">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

侧滑窗体控件
<View id="32" did="0" type="DrawerLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

协调性布局控件
<View id="33" did="0" type="CoordinatorLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

应用栏布局控件
<View id="34" did="0" type="AppBarLayout">

<ppt>width=-1
height=-2</ppt>

<event></event>

</View>

折叠工具栏布局控件
<View id="35" did="0" type="CollapsingToolbarLayout">

<ppt>width=-1
height=-2</ppt>

<event></event>

</View>

工具栏布局控件
<View id="36" did="0" type="Toolbar">

<ppt>width=-1
height=-3</ppt>

<event></event>

</View>

浮动动作按钮控件
<View id="37" did="0" type="FloatingActionButton">

<ppt>width=-2
height=-2
src=17301544</ppt>

<event></event>

</View>

嵌套滚动控件
<View id="38" did="0" type="NestedScrollView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

标签布局控件
<View id="39" did="0" type="TabLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

v7列表控件
<View id="41" did="0" type="RecyclerView">

<ppt>width=-1
height=-2</ppt>

<event></event>

</View>

约束性布局控件
<View id="42" did="0" type="ConstraintLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

垂直滑动窗体控件
<View id="43" did="0" type="VerticalViewPager">

<ppt>width=-1
height=-2</ppt>

<event></event>

</View>

下拉刷新控件
<View id="44" did="0" type="SwipeRefreshLayout">

<ppt>width=-1
height=-2</ppt>

<event></event>

</View>
文本输入布局控件

<View id="45" did="0" type="TextInputLayout">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

开关控件
<View id="46" did="0" type="SwitchCompat">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>

卡片控件
<View id="47" did="0" type="CardView">

<ppt>width=-2
height=-2</ppt>

<event></event>

</View>
登陆注册界面参考例子:
<View id="1" did="0" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
background=white</ppt>

<event></event>

</View>

<View id="2" did="1" type="LinearLayout">

<ppt>width=-1
height=45dp
orientation=vertical
gravity=center_vertical</ppt>

<event></event>

</View>

<View id="3" did="2" type="ImageView">

<ppt>width=-2
height=-2
src=@wew.png
layout_marginLeft=20dp</ppt>

<event></event>

</View>

<View id="4" did="1" type="LinearLayout">

<ppt>width=-1
height=50dp
orientation=horizontal</ppt>

<event></event>

</View>

<View id="5" did="4" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
layout_weight=1
gravity=center_vertical</ppt>

<event></event>

</View>

<View id="10" did="5" type="TextView">

<ppt>width=-2
height=-2
text=Mix Line
typeface=@BadComic.ttf
textColor=#212121
layout_marginLeft=20dp
textSize=18sp</ppt>

<event></event>

</View>

<View id="8" did="4" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
layout_weight=1
gravity=center</ppt>

<event></event>

</View>

<View id="11" did="8" type="TextView">

<ppt>width=-2
height=-2
text=帮助中心
layout_gravity=right
textColor=#9E9E9E</ppt>

<event></event>

</View>

<View id="9" did="4" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
layout_weight=1
gravity=center</ppt>

<event></event>

</View>

<View id="13" did="9" type="TextView">

<ppt>width=-2
height=-2
text=用户协议
textColor=#9E9E9E</ppt>

<event></event>

</View>

<View id="14" did="1" type="LinearLayout">

<ppt>width=-1
height=100dp
orientation=vertical
gravity=center
</ppt>

<event></event>

</View>

<View id="15" did="14" type="TextView">

<ppt>width=-2
height=-2
text=帐号登录
typeface=@字体圈伟君黑-W1.ttf
textColor=#212121
textSize=22dp</ppt>

<event></event>

</View>

<View id="17" did="1" type="LinearLayout">

<ppt>width=-1
height=50dp
orientation=horizontal
</ppt>

<event></event>

</View>

<View id="18" did="17" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
gravity=center
layout_weight=1</ppt>

<event><eventItme type="clicki">us(50,"visibility",0)
us(26,"background","#424242")
us(55,"visibility",8)
us(25,"background","#ffffff")</eventItme></event>

</View>

<View id="20" did="18" type="TextView">

<ppt>width=-2
height=-2
text=短信验证码登录
textSize=15sp
textColor=#212121</ppt>

<event></event>

</View>

<View id="21" did="18" type="CardView">

<ppt>width=-1
height=3dp
app_CardElevation=0dp
layout_marginLeft=38dp
layout_marginRight=38dp
layout_marginTop=5dp</ppt>

<event></event>

</View>

<View id="26" did="21" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
visibility=0
background=#424242
visibility=0</ppt>

<event></event>

</View>

<View id="22" did="17" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
gravity=center
layout_weight=1</ppt>

<event><eventItme type="clicki">us(50,"visibility",8)
us(26,"background","#ffffff")
us(55,"visibility",0)
us(25,"background","#424242")</eventItme></event>

</View>

<View id="23" did="22" type="TextView">

<ppt>width=-2
height=-2
text=密码登录
textSize=15sp
textColor=#9E9E9E</ppt>

<event></event>

</View>

<View id="24" did="22" type="CardView">

<ppt>width=-1
height=3dp
app_CardElevation=0dp
layout_marginLeft=60dp
layout_marginRight=60dp
layout_marginTop=5dp</ppt>

<event></event>

</View>

<View id="25" did="24" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
background=#ffffffff</ppt>

<event></event>

</View>

<View id="27" did="1" type="LinearLayout">

<ppt>width=-1
height=-2
orientation=vertical</ppt>

<event></event>

</View>

<View id="50" did="27" type="LinearLayout">

<ppt>width=-1
height=-2
orientation=vertical
visibility=0</ppt>

<event></event>

</View>

<View id="28" did="50" type="CardView">

<ppt>width=-1
height=45dp
layout_marginTop=20dp
layout_marginLeft=20dp
layout_marginRight=20dp
app_CardElevation=0dp
app_CardcornerRadius=10dp</ppt>

<event></event>

</View>

<View id="30" did="28" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
</ppt>

<event></event>

</View>

<View id="31" did="30" type="EditText">

<ppt>width=-1
height=-1
text=
hint=请输入手机号码
background=#F5F5F5
textSize=14sp
paddingLeft=15dp
textCursorDrawable=#ff030202</ppt>

<event></event>

</View>

<View id="32" did="50" type="RelativeLayout">

<ppt>width=-1
height=45dp
layout_marginTop=10dp</ppt>

<event></event>

</View>

<View id="33" did="32" type="CardView">

<ppt>width=100dp
height=45dp
ut_alignParentRight=true
layout_marginRight=20dp
app_CardcornerRadius=8dp
app_CardElevation=0dp</ppt>

<event></event>

</View>

<View id="34" did="33" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
background=#E0E0E0
gravity=center</ppt>

<event></event>

</View>

<View id="35" did="34" type="TextView">

<ppt>width=-2
height=-2
text=获取验证码
textStyle=bold
textSize=12sp
textColor=#9E9E9E</ppt>

<event></event>

</View>

<View id="36" did="32" type="CardView">

<ppt>width=-1
height=45dp
layout_marginLeft=20dp
layout_marginRight=10dp
app_CardElevation=0dp
app_CardcornerRadius=10dp
ut_toLeftOf=33</ppt>

<event></event>

</View>

<View id="37" did="36" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical</ppt>

<event></event>

</View>

<View id="38" did="37" type="EditText">

<ppt>width=-1
height=-1
text=
hint=请输入验证码
background=#F5F5F5
textSize=14sp
paddingLeft=15dp
textCursorDrawable=#ff030202</ppt>

<event></event>

</View>

<View id="55" did="27" type="LinearLayout">

<ppt>width=-1
height=-2
orientation=vertical
visibility=8</ppt>

<event></event>

</View>

<View id="47" did="55" type="CardView">

<ppt>width=-1
height=45dp
layout_marginTop=20dp
layout_marginLeft=20dp
layout_marginRight=20dp
app_CardElevation=0dp
app_CardcornerRadius=10dp</ppt>

<event></event>

</View>

<View id="48" did="47" type="LinearLayout">

<ppt>width=-1
height=-1
orientation=vertical
</ppt>

<event></event>

</View>

<View id="49" did="48" type="EditText">

<ppt>width=-1
height=-1
text=
hint=请输入账号
background=#F5F5F5
textSize=14sp
paddingLeft=15dp
textCursorDrawable=#ff030202</ppt>

<event></event>

</View>

<View id="51" did="55" type="CardView">

<ppt>width=-1
height=45dp
layout_marginTop=20dp
layout_marginLeft=20dp
layout_marginRight=20dp
app_CardElevation=0dp
app_CardcornerRadius=10dp</ppt>

<event></event>

</View>

<View id="54" did="51" type="RelativeLayout">

<ppt>width=-1
height=-1</ppt>

<event></event>

</View>

<View id="53" did="54" type="EditText">

<ppt>width=-1
height=-1
text=
hint=请输入密码
background=#F5F5F5
textSize=14sp
paddingLeft=15dp
textCursorDrawable=#ff030202</ppt>

<event></event>

</View>

<View id="56" did="54" type="ImageView">

<ppt>width=20dp
height=20dp
src=@mn.png
ut_centerVertical=true
ut_alignParentRight=true
layout_marginRight=10dp</ppt>

<event></event>

</View>

<View id="57" did="55" type="TextView">

<ppt>width=-2
height=-2
text=忘记密码
textColor=#616161
layout_marginLeft=25dp
layout_marginTop=10dp
textSize=12sp</ppt>

<event></event>

</View>

<View id="39" did="1" type="CardView">

<ppt>width=-1
height=45dp
layout_marginTop=30dp
layout_marginLeft=20dp
layout_marginRight=20dp
app_CardElevation=0dp
app_CardcornerRadius=10dp</ppt>

<event></event>

</View>

<View id="40" did="39" type="LinearLayout">

<ppt>width=-1
height=-1
background=#E0E0E0
orientation=vertical
gravity=center</ppt>

<event></event>

</View>

<View id="41" did="40" type="TextView">

<ppt>width=-2
height=-2
text=登录
textStyle=bold
textColor=#9E9E9E</ppt>

<event></event>

</View>

<View id="42" did="1" type="LinearLayout">

<ppt>width=-1
gravity=center_horizontal
height=-2
orientation=vertical
layout_marginTop=20dp</ppt>

<event></event>

</View>

<View id="43" did="42" type="TextView">

<ppt>width=-2
height=-2
text=注册账号
textColor=#616161</ppt>

<event></event>

</View>

<View id="44" did="1" type="RelativeLayout">

<ppt>width=-1
height=-1</ppt>

<event></event>

</View>

<View id="45" did="44" type="TextView">

<ppt>width=-2
height=-2
text=Copyright © 2021-2022 Mixture.All Rights Reserved
ut_alignParentBottom=true
ut_centerHorizontal=true
layout_marginBottom=20dp
textSize=12sp</ppt>

<event></event>

</View>

<View id="46" did="44" type="TextView">

<ppt>width=-2
height=-2
text=Mix为 米西 用户提供账号服务
ut_alignParentBottom=true
ut_centerHorizontal=true
layout_marginBottom=45dp
textSize=12sp</ppt>

<event></event>

</View>

<UIEventset></UIEventset>

一般界面模板，在要求写界面的时候优先选择

本模板基于高仿 iOS 设置页面设计，提炼了一套完整的 分组卡片式列表界面 结构，适用于  yuv3 中任何需要设置、菜单、列表展示的场景。您可以直接复制结构并修改内容，快速生成风格统一的页面。

---

整体框架（骨架）

```yu
<根布局 LinearLayout 背景=#F2F1F6>
├── 导航栏 (LinearLayout 高度55dp 背景=#F9F9F9)
│   └── RelativeLayout
│       ├── 标题 (TextView 居中)
│       └── 返回按钮 (TextView 左对齐，使用图标字体)
├── 分割线 (高度0.5dp 颜色#E7E7E8)
├── ScrollView (填充剩余高度)
│   └── 内容 LinearLayout (padding左右15dp)
│       ├── 组标题 (TextView 灰色小字，上边距15dp)
│       ├── CardView (圆角15dp 无阴影)
│       │   └── 垂直 LinearLayout
│       │       ├── 条目1 (LinearLayout 高度50dp)
│       │       │   ├── 左侧标题 (TextView)
│       │       │   └── 右侧区域 (LinearLayout 右对齐)
│       │       │       ├── 辅助文字 (TextView 灰色)
│       │       │       └── 右箭头 (ImageView 18dp) 或 开关 (SwitchCompat)
│       │       ├── 分割线 (0.5dp)
│       │       ├── 条目2 ... (同上)
│       │       └── ...
│       ├── 组标题 (下一组)
│       ├── CardView ...
│       └── 底部留白 (20dp)
└──
```

---

导航栏（Navigation Bar）

组件 属性 说明
外层容器 height=55dp, background=#F9F9F9 固定高度，浅灰色背景
标题 gravity=center, textSize=17.5dp, textStyle=bold, 字体 Inter-SemiBold 居中显示，黑色
返回按钮 width=30dp, gravity=center, paddingLeft=15dp, 图标字体  左对齐，蓝色 #4386F6，点击 end()
状态栏 在 loading 事件调用 uycl("#F9F9F9", true, 0) 与导航栏同色，保留状态栏空间

关键代码片段：

```yu
<View id="2" did="1" type="LinearLayout">
<ppt>width=-1 height=55dp background=#F9F9F9</ppt>
</View>
<View id="3" did="2" type="RelativeLayout">
<ppt>width=-1 height=-1</ppt>
</View>
<!-- 标题 -->
<View id="4" did="3" type="TextView">
<ppt>text=设置 gravity=center textSize=17.5dp textColor=#000000 textStyle=bold</ppt>
</View>
<!-- 返回 -->
<View id="5" did="3" type="TextView">
<ppt>text= textColor=#4386F6 typeface=@SF-Symbols-Icon.ttf gravity=center paddingLeft=15dp width=30dp</ppt>
<event><eventItme type="clicki">end()</eventItme></event>
</View>
```

---

分组卡片（Section）

组件 属性 说明
组标题 textColor=#A7A7AD, textSize=15sp, layout_marginTop=15dp, layout_marginLeft=15dp, layout_marginBottom=5dp 灰色小字，用于分组标识
CardView app_CardcornerRadius=15dp, app_CardElevation=0 白色卡片，圆角
内部布局 orientation=vertical 垂直排列条目

关键代码：

```yu
<View id="9" did="8" type="TextView">
<ppt>text=账号 layout_marginTop=15dp marginLeft=15dp marginBottom=5dp textColor=#A7A7AD textSize=15sp</ppt>
</View>
<View id="10" did="8" type="CardView">
<ppt>width=-1 height=-2 app_CardcornerRadius=15dp app_CardElevation=0</ppt>
</View>
<View id="11" did="10" type="LinearLayout">
<ppt>width=-1 height=-2 orientation=vertical</ppt>
</View>
<!-- 条目放在这里 -->
```

---

条目（Item）布局模式

模式一：纯标题 + 右箭头（可跳转）

```yu
<View id="XX" did="parent" type="LinearLayout">
<ppt>width=-1 height=50dp orientation=horizontal paddingLeft=15dp paddingRight=15dp gravity=center_vertical|left</ppt>
<event><eventItme type="clicki">uigo("next.iyu")</eventItme></event>
</View>

<!-- 左侧标题 -->
<View id="YY" did="XX" type="TextView">
<ppt>text=标题 textColor=#000000 textSize=16dp</ppt>
</View>

<!-- 右侧区域 -->
<View id="ZZ" did="XX" type="LinearLayout">
<ppt>width=-1 height=-1 orientation=horizontal gravity=center_vertical|right</ppt>
</View>

<!-- 辅助文字（可选） -->
<View id="AA" did="ZZ" type="TextView">
<ppt>text=详情 textColor=#8E8E93 textSize=14dp layout_marginRight=8dp</ppt>
</View>

<!-- 右箭头 -->
<View id="BB" did="ZZ" type="ImageView">
<ppt>width=18dp height=-1 src=@goto.png</ppt>
</View>
```

模式二：图标 + 标题 + 右箭头

在左侧标题前增加 ImageView（如 Apple ID 条目）：

```yu
<View id="13" did="12" type="LinearLayout">
<ppt>width=-2 height=-1 gravity=center_vertical</ppt>
</View>
<View id="14" did="13" type="ImageView">
<ppt>width=30dp height=30dp src=@avatar.png layout_marginRight=12dp</ppt>
</View>
<View id="15" did="13" type="TextView">
<ppt>text=Apple ID textSize=16dp</ppt>
</View>
```

模式三：标题 + 开关（SwitchCompat）

```yu
<!-- 右侧区域 -->
<View id="30" did="28" type="LinearLayout">
<ppt>width=-1 height=-1 gravity=center_vertical|right</ppt>
</View>
<View id="31" did="30" type="SwitchCompat">
<ppt>width=-2 height=-2 checked=false</ppt>
<event>
<eventItme type="clicki">
    ug(31, "checked", state)
    f(state) { tw("开启") } else { tw("关闭") }
</eventItme>
</event>
</View>
```

模式四：标题 + 副标题 + 右箭头（如语言与地区）

与模式一相同，在右侧添加 TextView 显示当前值。

---

颜色、尺寸、字体规范

元素 值 用途
页面背景 #F2F1F6 整体背景
卡片背景 白色（默认） 卡片内条目
导航栏背景 #F9F9F9 顶部栏
分割线 #E7E7E8, 高度 0.5dp 条目间分隔
标题文字 #000000, 16dp (条目), 17.5dp (导航) 主要文字
副标题/辅助文字 #8E8E93, 14dp 右侧说明文字
组标题 #A7A7AD, 15sp 分组标签
返回/链接颜色 #4386F6 交互元素
卡片圆角 15dp CardView 圆角
条目高度 50dp (标准), 60dp (带头像) 统一舒适
左右内边距 15dp 条目内容距离边缘
字体 Inter-Regular / Inter-SemiBold（可替换为系统字体） 模拟 SF Pro

---

常用控件总结

· LinearLayout：根布局、导航栏、内容区、条目容器。
· RelativeLayout：导航栏内部，用于居中标题和左对齐返回按钮。
· ScrollView：使整个内容可滚动，避免溢出。
· CardView：分组卡片，带圆角无阴影。
· TextView：标题、副标题、组标题、图标字体（返回箭头）。
· ImageView：头像、右箭头图标。
· SwitchCompat：开关控件，用于布尔设置项。
· View：分割线（高度0.5dp）。

---

交互事件模式

· 返回按钮：clicki → end()
· 条目点击：clicki → uigo("页面.iyu") 或 tw("提示")
· 开关切换：clicki → ug(控件ID, "checked", state) 获取状态，执行逻辑

---

注意事项

1. 字体资源：如无自定义字体，可删除 typeface 属性，使用系统默认字体。
2. 图标字体：返回箭头  需要 SF-Symbols-Icon.ttf 字体文件，若没有可用图片代替。
3. 右箭头图片：需准备一张 goto.png 图片（18dp×18dp）放入 files/。
4. 上方资源请告诉用户前往 仓库为https://github.com/Catndroid/yuv3-resources的yuv3 Skill资源库获取 直链：https://github.com/Catndroid/yuv3-resources/archive/refs/heads/main.zip
5. 状态栏：在 loading 事件中调用 uycl，第三个参数 0 表示只修改状态栏颜色，不影响导航栏。
6. CardView 属性：app_CardElevation=0 去掉阴影，符合 iOS 扁平风格；app_CardcornerRadius=15dp 设置圆角。
7. 分割线处理：最后一个条目下方不添加分割线（符合 iOS 习惯）。

---

可复用性建议

· 将整个界面视为一个 “卡片列表” 模板，您只需：
  1. 复制 ScrollView 内部结构。
  2. 修改组标题文字。
  3. 添加或删除条目（每个条目按标准模式编写）。
  4. 替换条目标题、右侧文字、绑定事件。
· 若需要增加新分组，重复“组标题 + CardView”模式即可。
· 若需要更多控件（如滑块、进度条），可在条目内部自由组合。

<View id="5" did="0" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=vertical
background=#F2F1F6</ppt>
<event></event>
</View>
<View id="6" did="5" type="LinearLayout">
<ppt>width=-1
height=55dp
orientation=vertical
background=#F9F9F9</ppt>
<event></event>
</View>
<View id="8" did="6" type="RelativeLayout">
<ppt>width=-1
height=-1</ppt>
<event></event>
</View>
<View id="9" did="8" type="TextView">
<ppt>width=-1
height=-1
text=偏好設定
textSize=17.5dp
textColor=#000000
typeface=@Inter-SemiBold.ttf
gravity=center
textStyle=bold</ppt>
<event></event>
</View>
<View id="7" did="8" type="TextView">
<ppt>width=30dp
height=-1
text=
textSize=20dp
//textColor=#F54047
textColor=#4386F6
typeface=@SF-Symbols-Icon.ttf
gravity=center
paddingLeft=15dp
ps=icon:sf-symbols
ps=back icon</ppt>
<event><eventItme type="clicki">end()</eventItme></event>
</View>
<View id="10" did="5" type="LinearLayout">
<ppt>width=-1
height=0.5dp
orientation=vertical
background=#E7E7E8</ppt>
<event></event>
</View>
<View id="11" did="5" type="ScrollView">
<ppt>width=-1
height=-1</ppt>
<event></event>
</View>
<View id="12" did="11" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=vertical
background=#F2F1F6
paddingLeft=15dp
paddingRight=15dp</ppt>
<event></event>
</View>
<View id="13" did="12" type="TextView">
<ppt>width=-2
height=-2
text=喜好設定
layout_marginTop=15dp
layout_marginLeft=15dp
layout_marginBottom=5dp
textColor=#A7A7AD
textSize=15sp
typeface=@Inter-Regular.ttf</ppt>
<event></event>
</View>
<View id="14" did="12" type="CardView">
<ppt>width=-1
height=-2
app_CardcornerRadius=15dp
app_CardElevation=0</ppt>
<event></event>
</View>
<View id="19" did="14" type="LinearLayout">
<ppt>width=-1
height=-2
orientation=vertical</ppt>
<event></event>
</View>
<View id="20" did="19" type="LinearLayout">
<ppt>width=-1
height=-2
orientation=vertical
</ppt>
<event></event>
</View>
<View id="15" did="20" type="LinearLayout">
<ppt>width=-1
height=50dp
orientation=horizontal
paddingLeft=15dp
paddingRight=15dp
gravity=center_vertical|left
</ppt>
<event></event>
</View>
<View id="16" did="15" type="TextView">
<ppt>width=-2
height=-2
text=用戶界面
textColor=#000000
textSize=16dp
typeface=@Inter-Regular.ttf</ppt>
<event></event>
</View>
<View id="17" did="15" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=horizontal
gravity=center_vertical|right</ppt>
<event></event>
</View>
<View id="18" did="17" type="ImageView">
<ppt>width=18dp
height=-1
src=@goto.png</ppt>
<event></event>
</View>
<View id="21" did="20" type="LinearLayout">
<ppt>width=-1
height=0.5dp
orientation=vertical
background=#E7E7E8</ppt>
<event></event>
</View>
<View id="22" did="19" type="LinearLayout">
<ppt>width=-1
height=-2
orientation=vertical</ppt>
<event></event>
</View>
<View id="23" did="22" type="LinearLayout">
<ppt>width=-1
height=50dp
orientation=horizontal
paddingLeft=15dp
paddingRight=15dp
gravity=center_vertical|left
</ppt>
<event></event>
</View>
<View id="24" did="23" type="TextView">
<ppt>width=-2
height=-2
text=歌詞顯示
textColor=#000000
textSize=16dp
typeface=@Inter-Regular.ttf</ppt>
<event></event>
</View>
<View id="25" did="23" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=horizontal
gravity=center_vertical|right</ppt>
<event></event>
</View>
<View id="26" did="25" type="ImageView">
<ppt>width=18dp
height=-1
src=@goto.png</ppt>
<event></event>
</View>
<View id="27" did="22" type="LinearLayout">
<ppt>width=-1
height=0.5dp
orientation=vertical
background=#E7E7E8</ppt>
<event></event>
</View>
<View id="28" did="19" type="LinearLayout">
<ppt>width=-1
height=-2
orientation=vertical</ppt>
<event></event>
</View>
<View id="29" did="28" type="LinearLayout">
<ppt>width=-1
height=50dp
orientation=horizontal
paddingLeft=15dp
paddingRight=15dp
gravity=center_vertical|left
</ppt>
<event></event>
</View>
<View id="30" did="29" type="TextView">
<ppt>width=-2
height=-2
text=媒體通知
textColor=#000000
textSize=16dp
typeface=@Inter-Regular.ttf</ppt>
<event></event>
</View>
<View id="31" did="29" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=horizontal
gravity=center_vertical|right</ppt>
<event></event>
</View>
<View id="32" did="31" type="ImageView">
<ppt>width=18dp
height=-1
src=@goto.png</ppt>
<event></event>
</View>
<View id="33" did="28" type="LinearLayout">
<ppt>width=-1
height=0.5dp
orientation=vertical
background=#E7E7E8</ppt>
<event></event>
</View>
<View id="34" did="12" type="TextView">
<ppt>width=-2
height=-2
text=LightGather 提供幫助與支持
layout_marginTop=15dp
layout_marginLeft=15dp
layout_marginBottom=5dp
textColor=#A7A7AD
textSize=15sp
typeface=@Inter-Regular.ttf</ppt>
<event></event>
</View>
<UIEventset><eventItme type="loading">uycl("#F9F9F9",true,0)</eventItme></UIEventset>

Material 风格 Commander 界面模板

<View id="2" did="0" type="Toolbar">
<ppt>width=-1
height=-3
background=#222222
app_TitleColor=#ffffff
app_subTitleColor=#ffffff
app_title=命令行</ppt>
<event></event>
</View>
<View id="3" did="0" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=vertical
//background=@android.png
</ppt>
<event></event>
</View>
<View id="10" did="3" type="CoordinatorLayout">
<ppt>width=-1
height=-1</ppt>
<event></event>
</View>
<View id="7" did="10" type="ScrollView">
<ppt>width=-1
height=-2
overScrollMode=never</ppt>
<event></event>
</View>
<View id="15" did="7" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=vertical</ppt>
<event></event>
</View>
<View id="8" did="15" type="TextView">
<ppt>width=-1
height=-2
text=
textColor=#ffffff
typeface=monospace
textSize=15dp
textIsSelectable=true</ppt>
<event></event>
</View>
<View id="16" did="15" type="LinearLayout">
<ppt>width=-1
height=100dp
orientation=vertical
ps=滚动占用空间布局
visibility=invisible</ppt>
<event></event>
</View>
<View id="11" did="10" type="LinearLayout">
<ppt>width=-1
height=-1
orientation=vertical
gravity=bottom</ppt>
<event></event>
</View>
<View id="12" did="11" type="LinearLayout">
<ppt>width=-1
height=-2
orientation=horizontal
background=#222222
padding=20dp</ppt>
<event></event>
</View>
<View id="13" did="12" type="EditText">
<ppt>width=-1
height=-2
text=
textColor=#ffffff
typeface=monospace
background=#222222
textCursorDrawable=#ffffff
hint=鍵入命令
textColorHint=#eeeeee
layout_weight=1
singleLine=true</ppt>
<event></event>
</View>
<View id="14" did="12" type="ImageButton">
<ppt>width=45dp
height=-1
src=@send.png
scaleType=fitcenter
background=through
BackgroundRipple=#eeeeee
clickable=true</ppt>
<event><eventItme type="clicki">
</eventItme></event>
</View>
<UIEventset><eventItme type="loading">
uycl("#222222",true)
uycl(-3)
uycl(0)

utb(2)
utb("left", 2, "@back.png")
utb("set", "leftck", 2)
{
end()
}

t()
{
	call(GetVer,"mjava","shellExec.execShell","uname -r")
    ufnsui()
    {
    	us(8,"text","Commander 1.04.2\nBuild:"+GetVer)
    }
}

</eventItme><eventItme type="menu">case 重載:
uigo("mian.iyu")
end()
break

case 新回話:
uigo("mian.iyu")
break

case 複製:
ug(8,"text",iamvalue)
sxb(iamvalue)
break

case 粘貼:
shb(iamvalue)
us(13,"text",iamvalue)
break

case 關於:
s a = "Commander"
s b = "Follow me on GitHub(Catndroid)\nhttps://github.com/catndroid"
s c = "Done"
utw(null, a, b, c, false, v)
{
}
break</eventItme></UIEventset>

yuv3一些基本组件(很多都是参考的Android XML Layout的写法)

TextView（文本）

```yu
type=TextView
text=显示文字
textColor=#FFFFFF
textSize=16sp
textStyle=bold          // bold / italic / normal
typeface=monospace      // normal / sans / serif / monospace
textIsSelectable=true   // 可选中复制
lineSpacingExtra=2dp    // 行间距
```

EditText（输入框）

```yu
type=EditText
text=默认文本
hint=提示文字
textColorHint=#888888
singleLine=true         // 单行模式
textCursorDrawable=#FF0000  // 光标颜色
gravity=top             // 多行时文字居上
```

Button（按钮）

```yu
type=Button
text=按钮文字
backgroundcolor=#4CAF50
textColor=#FFFFFF
backgroundripple=#888888  // 点击波纹效果（5.0+）
clickable=true
```

ImageButton（图像按钮）

```yu
type=ImageButton
src=@icon.png           // 图像来源
scaleType=center        // center / fitcenter / fitxy
background=through      // 透明背景
backgroundripple=#888888
```

ImageView（图像）

```yu
type=ImageView
src=@image.png
scaleType=fitcenter
```

CheckBox（复选框）

```yu
type=CheckBox
text=选项文字
checked=true           // 是否选中
```

WebView（浏览器）

```yu
type=WebView
url=http://example.com
```

CardView（卡片）

```yu
type=CardView
backgroundcolor=#FFFFFF
elevation=4dp          // 阴影深度
```

Toolbar（工具栏）

```yu
type=Toolbar
background=#222222
app_title=标题
app_subtitle=子标题
app_TitleColor=#FFFFFF
app_subTitleColor=#AAAAAA
elevation=4dp
```

3.4 布局容器

LinearLayout（线性布局）

```yu
type=LinearLayout
orientation=vertical    // vertical / horizontal
gravity=center         // center / center_vertical / center_horizontal
layout_weight=1        // 权重分配剩余空间
```

ScrollView（滚动容器）

```yu
type=ScrollView
overScrollMode=never   // never / always / ifContentScrolls
scrollbarThumbVertical=#30363D
scrollbarSize=4dp
```

3.5 常见属性完整列表

属性 说明 示例
width 宽度 -1 / -2 / 100dp
height 高度 -1 / -2 / 48dp
padding 内边距（四方向） 12dp
paddingleft 左内边距 10dp
layout_marginleft 左外边距 10dp
layout_weight 权重（LinearLayout中） 1
background 背景（颜色/图片） #FFFFFF / @bg.png
backgroundcolor 背景颜色 #FF0000
visibility 可见性 visible / invisible / gone
gravity 内容对齐 center / left / right
elevation 阴影（5.0+） 4dp

第四章：控件操作（UG/US 核心）

4.1 获取控件属性 (UG)

```yu
// 基本用法
ug(控件ID, "属性名", 变量)

// 示例
ug(3, "text", content)    // 获取文本
ug(5, "checked", isChecked) // 获取复选框状态
ug(8, "text", output)     // 获取TextView内容
```

常用属性：

属性 说明 适用控件
text 文本内容 TextView, EditText, Button
checked 是否选中 CheckBox, RadioButton
progress 进度值 ProgressBar
url 当前URL WebView
title 页面标题 WebView
visibility 可见状态 所有控件
width 宽度 所有控件
height 高度 所有控件
x / y 坐标 所有控件

4.2 设置控件属性 (US)

```yu
// 基本用法
us(控件ID, "属性名", 值)

// 示例：设置文本
us(3, "text", "新内容")

// 设置背景颜色
us(1, "background", "#FF0000")

// 设置背景图片（安装包内）
us(1, "background", "@bg.png")

// 设置背景图片（SD卡）
us(1, "background", "%photo.jpg")

// 设置网络图片
us(1, "background", "http://example.com/image.png")

// 设置WebView加载URL
us(8, "url", "https://example.com")

// 设置复选框状态
us(5, "checked", true)

// 设置可见性
us(4, "visibility", "gone")  // gone / invisible / visible

// 设置文本框光标位置
us(13, "selection", 5)  // 光标移动到第5个字符后

// 设置阴影
us(2, "shadow", 5, 0, 0, "#000000")  // 半径, dx, dy, 颜色
```

4.3 动态绑定事件 (SSJ)

```yu
// 给动态创建的控件绑定点击事件
ssj(控件ID, "clicki")
{
    tw("被点击了")
}

// 事件类型列表
// clicki         - 单击
// press          - 长按
// touchmonitor   - 触屏监听
// keyboard       - 键盘事件
// focuschange    - 焦点变化
```

4.4 获取控件对象 (GVS)

```yu
// 获取控件对象（用于Java原生操作）
gvs(7, scrollViewObj)
gvs(8, textViewObj)

// 获取根控件内部的子控件
gvs(parentId, childId, childObj)
```

4.5 动态创建控件 (NVW)

```yu
// 创建文本控件
s newId = 999
s parentId = 1
nvw(newId, parentId, "文本", "width=-2\nheight=-2\ntext=动态创建")

// 带返回对象
nvw(newId, parentId, "文本", "width=-2\nheight=-2\ntext=动态创建", obj)
```

4.6 移除控件 (URVW)

```yu
urvw(控件ID)  // 移除指定控件
```

第五章：事件系统完全手册

5.1 事件类型速查表

事件类型 触发时机 系统变量 返回值
clicki 单击控件 st_vId, st_vW 无
press 长按控件 st_vId, st_vW true/false
touchmonitor 触屏操作 st_eX, st_eY, st_eA true/false
keyboard 物理按键 st_kC, st_eA true/false
focuschange 焦点变化 st_vId, st_hF 无
load 界面加载完成 无 无
loading 界面加载中（已弃用） 无 无
menu 菜单弹出 无 无
sensor 重力感应 st_x, st_y, st_z 无
result 回调结果 st_sC, st_lC, st_iT 无

5.2 事件代码格式

在布局中定义

```xml
<View id="3" did="0" type="Button">
<ppt>text=点击我</ppt>
<event>
<eventItme type="clicki">
tw("按钮被点击了")
</eventItme>
<eventItme type="press">
tw("长按触发")
</eventItme>
</event>
</View>
```

返回值事件（如 touchmonitor）

```yu
<eventItme type="touchmonitor">
[true]   // 返回true表示事件已处理
tw("触屏坐标: " + st_eX + ", " + st_eY)
</eventItme>
```

5.3 菜单事件

```yu
<eventItme type="menu">
case 选项一:
    tw("选择了选项一")
    break

case 选项二|@icon.png|0|1:   // 标题|图标|显示值|次序
    tw("选择了选项二")
    break

case 选项三:
    tw("选择了选项三")
    break

default:
    tw("菜单打开")
    break
</eventItme>
```

5.4 回调结果事件

```yu
<eventItme type="result">
f(st_sC == 1102)  // 1102 是二维码扫描结果
{
    git(st_iT, "extra", "result", qrResult)
    tw("扫描结果: " + qrResult)
}
</eventItme>
```

第六章：文件与IO操作

6.1 文件操作命令速查

命令 功能 示例
fe(path, result) 判断文件是否存在 fe("%abc.txt", exists)
fs(path, size) 获取文件大小（字节） fs("%abc.txt", size)
fd(path, result) 删除文件 fd("%abc.txt", success)
fr(path, content) 读取文本文件 fr("%abc.txt", content)
fr(path, encoding, content) 指定编码读取 fr("%abc.txt", "utf-8", content)
fw(path, content) 写入文本文件 fw("%abc.txt", "内容")
fw(path, content, encoding) 指定编码写入 fw("%abc.txt", "内容", "utf-8")
fc(src, dest, result) 复制文件 fc("%a.txt", "%b.txt", success)
ft(src, dest, result) 移动文件 ft("%a.txt", "%b.txt", success)
fdir(result) 获取SD卡根目录 fdir(sdcard)
fdir(path, result) 获取绝对路径 fdir("%dir", absPath)
fi(path, result) 判断是否文件夹 fi("%dir", isDir)
fl(path, list) 获取文件列表 fl("%dir", files)
fl(path, onlyDir, list) 仅获取文件夹 fl("%dir", true, dirs)

6.2 文件操作示例

```yu
// 读取配置文件
fr("%config.txt", "utf-8", config)

// 写入日志
fw("%log.txt", "日志内容\n", "utf-8")

// 检查文件是否存在
fe("%data.db", exists)
f(exists == false)
{
    tw("数据文件不存在")
}

// 遍历文件夹
fl("%downloads", files)
for(file; files)
{
    syso(file)
}

// 计算文件大小（MB）
fs("%video.mp4", bytes)
s(bytes / 1024 / 1024, mb)
tw("文件大小: " + mb + " MB")
```

6.3 ZIP 压缩解压

```yu
// 解压整个ZIP
fuzs("%archive.zip", "%output", success)

// 解压部分文件
fuz("%archive.zip", "readme.txt", "%output", count)

// 压缩文件/文件夹
fj("%folder", "%archive.zip", success)
```

第七章：网络编程

7.1 HTTP 请求 (HS)

```yu
// GET 请求
t()
{
    hs("https://api.example.com/data", "utf-8", response)
    syso(response)
}

// POST 请求（表单）
t()
{
    hs("https://api.example.com/login", "username=admin&password=123", "utf-8", response)
    syso(response)
}

// POST 请求（JSON）
t()
{
    s json = "{\"id\":1,\"name\":\"test\"}"
    hs("https://api.example.com/api", json, "utf-8", response)
    syso(response)
}

// 带 Cookie
t()
{
    hs("https://api.example.com/profile", null, "utf-8", "session_id=abc123;", response)
    syso(response)
}

// 带 Header
t()
{
    s headers = "User-Agent=Mozilla/5.0||accept=application/json"
    hs("https://api.example.com/data", null, "utf-8", null, true, headers, response)
    syso(response)
}
```

7.2 文件下载 (HD)

```yu
// 简单下载
t()
{
    hd("http://example.com/file.zip", "%downloads/file.zip", success)
    syso(success)
}

// 带覆盖控制
t()
{
    hd("http://example.com/file.zip", "%downloads/file.zip", true, success)
    syso(success)  // 0=成功, 1=已存在, -1=失败
}

// 带 Cookie 下载
t()
{
    hd("http://example.com/file.zip", "%downloads/file.zip", true, 
       null, "utf-8", "session_id=abc", true, null, result)
}
```

7.3 文件上传 (HUF)

```yu
t()
{
    s postData = "title=测试&desc=描述"
    s filePath = "%downloads/test.jpg"
    huf("http://example.com/upload", postData, filePath, "utf-8", response)
    syso(response)
}

// 多文件上传
t()
{
    s files = "%img1.jpg|%img2.jpg|%img3.jpg"
    huf("http://example.com/upload", "title=测试", files, "utf-8", response)
}
```

7.4 JSON 解析

```yu
// 解析 JSON 对象
s jsonText = "{\"id\":1,\"name\":\"张三\",\"age\":18}"
json(jsonText, obj)
json(obj, "get", "name", name)  // name = "张三"
json(obj, "get", "age", age)    // age = 18

// 修改 JSON
json(obj, "set", "age", 20)
json(obj, "json", newJson)  // 转回字符串

// 解析 JSON 数组
s jsonList = "{\"list\":[{\"id\":1,\"name\":\"a\"},{\"id\":2,\"name\":\"b\"}]}"
json(jsonList, obj)
json(obj, "list", "list", list)  // 获取列表对象
json(list, "size", size)
w(size > 0)
{
    s(size - 1, size)  // 转为0-based索引
    json(list, "data", size, item)
    json(item, "get", "name", name)
    syso(name)
}
```

第八章：界面导航与控件高级

8.1 界面跳转 (UIGO)

```yu
// 基本跳转（V3必须加后缀）
uigo("settings.iyu")

// 带参数跳转（Flags）
uigo("settings.iyu", 536870912)
```

Flags 含义：

值 含义
67108864 清空之上的界面
268435456 新内存实例
1073741824 不入栈
536870912 复用已有实例

8.2 弹窗界面 (UTW)

```yu
// 三按钮弹窗
s title = "确认"
s content = "确定要删除吗？"
s btn1 = "确定"
s btn2 = "取消"
s btn3 = "稍后"
utw(null, title, content, btn1, btn2, btn3, false, v)
{
    tw("点击了确定")
}
else
{
    tw("点击了取消")
}
else
{
    tw("点击了稍后")
}

// 单按钮弹窗
utw(null, "提示", "操作成功", "确定", false, v)
{
    tw("点击了确定")
}

// 无按钮弹窗（自动关闭）
utw(null, "提示", "加载中...", false, v)
stop(2000)
endutw()  // 手动关闭
```

8.3 弹出界面 (UTW 高级)

```yu
// 加载一个界面文件作为弹窗内容
utw(null, "标题", "content.iyu", "关闭", false, v)
{
    tw("点击了关闭")
}
```

8.4 列表控件 (ULA + ULS)

添加数据

```yu
// 创建列表数据
ula(listObj, 1="项目1", 2="项目2", 3="项目3")

// 添加单项
ula(listObj, 1="新项目")

// 清空列表
ula(listObj, null)
// 或
ula(listObj, "clear")

// 刷新列表
ula(listObj)
```

显示列表

```yu
// 简单列表（分隔符）
s data = "苹果;香蕉;橘子"
sl(data, ";", arr)
uls(1, arr)  // 下拉列表

// 自定义列表项
ula(listObj, 1="标题1", 2="副标题1")
uls(listId, listObj, "item.iyu", -1, -2)  // V7列表
```

获取列表数据

```yu
// 获取点击的项目数据
ulag(listObj, 1, value)  // 获取ID=1的控件值
ulag(listObj, -1, tag)   // 获取自定义标识

// 获取指定位置的数据
ulag(listObj, position, 1, value)
```

更新列表数据

```yu
ulas(listObj, 1, "新值")
ula(listObj)  // 刷新显示
```

8.5 Toolbar 工具栏

```yu
// 绑定 Toolbar
utb(2)

// 设置返回按钮
utb("left", 2, "@back.png")
utb("set", "leftck", 2)
{
    end()  // 点击返回
}

// 设置标题
utb("set", "title", "我的标题")
utb("set", "subtitle", "子标题")

// 设置右菜单
utb("right", 2, "@menu.png")

// 获取信息
utb("get", "title", title)
utb("get", "height", height)
```

8.6 滑动窗体 (UHT)

```yu
// 添加页面
uht(2, "add", -1, "标题1", "page1.iyu", 1="数据1")
uht(2, "add", -1, "标题2", "page2.iyu", 1="数据2")

// 删除页面
uht(2, "del", 0)  // 删除第一页

// 获取页面总数
uht(2, "size", count)

// 绑定标签布局
uht(2, "bd", 3, true)  // 绑定标签控件id=3
```

第九章：系统能力

9.1 设备信息

```yu
// 获取设备信息（数组）
sjxx(info)
sgsz(info, 0, cpu)
sgsz(info, 1, freq)
sgsz(info, 2, screenW)
sgsz(info, 3, screenH)
sgsz(info, 4, model)
sgsz(info, 5, brand)

// IMEI / IMSI
simei(imei)
simsi(imsi)

// 屏幕分辨率
swh("w", width)      // 宽度dp
swh("h", height)     // 高度dp
swh("pxw", pxWidth)  // 宽度px
swh("pxh", pxHeight) // 高度px
swh("pxztl", statusBarHeight)  // 状态栏高度
swh("pxbvk", navBarHeight)     // 导航栏高度
```

9.2 时间操作

```yu
// 获取当前时间
time(0, datetime)   // 2014-07-07 09:10:08
time(1, datetime)   // 2014/07/07 09:10:08
time(2, date)       // 2014-07-07
time(3, time)       // 09:10:08
time(4, timestamp)  // 18144133553151 (毫秒)
time(5, datetime)   // 2014年07月07日 09:10:08

// 自定义格式
time("Y", year)     // 年
time("m", month)    // 月
time("d", day)      // 日
time("H", hour)     // 时
time("M", minute)   // 分
time("S", second)   // 秒
time("a", weekDay)  // 星期几
```

9.3 剪贴板

```yu
// 写入剪贴板
sxb("要复制的内容")

// 读取剪贴板
shb(content)
tw(content)
```

9.4 状态栏控制

```yu
// 隐藏/显示状态栏
uycl(true)   // 隐藏
uycl(false)  // 显示

// 修改状态栏颜色（4.4+）
uycl("#50c4e5", true)   // 保留状态栏空间
uycl("#50c4e5", false)  // 不保留空间（全屏）
```

9.5 屏幕方向

```yu
ushsp(true)   // 横屏
ushsp(false)  // 竖屏
```

9.6 震动器

```yu
// 简单震动（需要申请权限）
uzd(sss.zdq, 1000)  // 震动1秒

// 自定义震动模式
uzd(sss.zdq, "1000 500 1000 500", false)  // 震-停-震-停

// 停止震动
uzd(sss.zdq, "sp")
```

9.7 发送短信 / 拨打电话

```yu
// 发送短信（打包后才真正发送）
usms("10086", "0")

// 拨打电话（打包后才真正拨打）
ucall("10086")
```

9.8 应用管理

```yu
// 打开其他应用
uapp("com.example.app", success)

// 卸载应用
uninapp("com.example.app")

// 获取应用列表
uapplist(true, list)  // true=包含系统应用
sgsz(list, 0, appInfo)  // 格式: 包名\n类名\n标题\n版本
```

9.9 通知栏

```yu
ftz("标题", "内容", "子内容", null)
{
    tw("通知被点击了")
}

// 带图标
ftz("标题", "内容", "子内容", "@icon.png")
{
    tw("点击了通知")
}
```

第十章：多线程与异步编程

10.1 新线程 (T)

```yu
// 创建新线程
t()
{
    // 耗时操作
    hs("https://api.example.com", response)
    
    // 更新UI必须用 ufnsui
    ufnsui()
    {
        us(8, "text", response)
    }
}
```

10.2 线程安全更新UI (UFNSUI)

```yu
t()
{
    // 后台操作
    s result = doHeavyWork()
    
    // 更新UI
    ufnsui()
    {
        us(13, "text", result)
        tw("操作完成")
    }
}
```

！ 规则：

· 新线程中获取UI数据（ug）可以不用 ufnsui
· 新线程中修改UI数据（us）必须用 ufnsui
· 否则会报错或闪退

10.3 线程暂停 (STOP)

```yu
t()
{
    syso("开始")
    stop(1000)  // 暂停1秒
    syso("1秒后")
    stop(2000)  // 暂停2秒
    syso("再过2秒")
}
```

第十一章：动画系统

11.1 动画控制 (DH)

```yu
// 透明度动画
dha(dh, true, false)  // 从可见到消失
dh(dh, "duration", 2000)  // 2秒
us(2, "dh", dh)  // 应用到控件

// 缩放动画
dhs(dh, 0.5, 2.5, 0.5, 2.5)  // X:0.5->2.5, Y:0.5->2.5
dh(dh, "duration", 2000)
us(2, "dh", dh)

// 移动动画
dht(dh, 30, 80, 30, 80)  // X:30->80, Y:30->80
dh(dh, "duration", 2000)
us(2, "dh", dh)

// 旋转动画
dhr(dh, 0, 180)  // 0° -> 180°
dh(dh, "duration", 2000)
us(2, "dh", dh)

// 启动动画
dh(dh, "start")
```

11.2 队列动画 (DHAS)

```yu
// 顺序执行多个动画
dhas(dh1, 2, "rotation", 0, 180)
dhas(dh2, 2, "alpha", 1, 0)
dhast(dhlist, "sequen", dh1, dh2)
dh(dhlist, "start")

// 同时执行
dhast(dhlist, "together", dh1, dh2)
```

11.3 动画监听 (DHON)

```yu
dhon(dh)
{
    syso("动画结束")
}
else
{
    syso("动画开始")
}
else
{
    syso("动画重复")
}
```

第十二章：性能优化与调试

12.1 调试模式 (SDEG)

```yu
// 设置调试模式
sdeg(0)  // 打包后无提示
sdeg(1)  // 打包后可打印错误
sdeg(2)  // 记录日志到 iApp/Log
```

12.2 日志输出 (SYSO)

```yu
syso("调试信息")
syso(变量值)
```

查看日志：

· 打包前：在 iApp 的 调试日志 中查看
· 打包后：使用 adb logcat 或系统日志工具，Tag 为 iapp

12.3 性能建议

操作 建议
网络请求 必须用 t(){...}
文件操作 小文件直接操作，大文件用 t(){...}
UI更新 在 ufnsui(){...} 中执行
循环运算 使用 s+ / s- 替代表达式
字符串拼接 大量拼接用 ss 命令
列表操作 批量操作用 ula 一次添加

12.4 常见崩溃排查

崩溃现象 可能原因 解决方案
启动闪退 uigo 没加 .iyu 后缀 检查所有跳转
点击无反应 事件代码错误 检查语法和转义
变量为null 作用域错误 检查 s/ss/sss
界面卡死 耗时操作在主线程 用 t() 包裹
UI不更新 在子线程修改UI 用 ufnsui 包裹
字符串报错 特殊字符未转义 加 \ 转义
编译失败 Java语法错误 检查 MJava 代码

第十三章：MJava 深度开发

13.1 MJava 基础语法

```java
// 文件必须以 .mjava 结尾
// 支持标准 Java 语法（JDK 1.7 兼容）

import java.io.*;
import java.net.*;

public class MyClass {
    // 静态方法（供裕语言调用）
    public static String doSomething(String input) {
        return "Result: " + input;
    }
    
    // 实例方法（需要通过对象调用）
    public String instanceMethod(int value) {
        return "Value: " + value;
    }
}
```

13.2 裕语言调用 MJava

```yu
// 调用静态方法
call(result, "mjava", "MyClass.doSomething", "test")

// 创建实例
javanew(obj, "MyClass")

// 调用实例方法
java(result, obj, "MyClass.instanceMethod", "int", 123)
```

13.3 常用系统 API 调用

执行 Shell 命令

```java
public static String execShell(String cmd) {
    try {
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(p.getInputStream(), "UTF-8")
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        p.waitFor();
        return sb.toString();
    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }
}
```

获取系统属性

```java
public static String getProp(String key) {
    return System.getProperty(key);
}
```

读写 SharedPreferences

```java
import android.content.Context;
import android.content.SharedPreferences;

public static void savePref(Context ctx, String key, String value) {
    SharedPreferences prefs = ctx.getSharedPreferences("app", Context.MODE_PRIVATE);
    prefs.edit().putString(key, value).apply();
}

public static String getPref(Context ctx, String key) {
    SharedPreferences prefs = ctx.getSharedPreferences("app", Context.MODE_PRIVATE);
    return prefs.getString(key, "");
}
```

13.4 Java 直接调用 Android API

```yu
// 获取 ScrollView 并滚动到底部
gvs(7, sv)
java(null, sv, "android.widget.ScrollView.fullScroll", "int", 130)

// 获取 TextView 内容高度
gvs(8, tv)
java(布局, tv, "android.widget.TextView.getLayout")
java(高度, 布局, "android.text.Layout.getHeight")

// Toast 提示（用裕语言的 tw 更简单）
java(null, activity, "android.widget.Toast.makeText", 
    "android.content.Context", activity,
    "CharSequence", "消息",
    "int", 0,
    "android.widget.Toast.show")
```

13.5 常用 Java 常量

常量名 值 用途
View.FOCUS_DOWN 130 滚动到底部
View.FOCUS_UP 33 滚动到顶部
View.GONE 8 隐藏控件
View.INVISIBLE 4 不可见但占位
View.VISIBLE 0 可见

第十四章：实战最佳实践

14.1 项目初始化模板

```yu
// mian.iyu 载入事件
<eventItme type="load">
    // 初始化全局变量
    sss appReady = false
    sss userId = ""
    
    // 状态栏设置
    uycl("#161B22", true)
    
    // 申请权限（6.0+）
    rps()
    
    // 启动服务/加载数据
    t()
    {
        // 初始化数据
        ufnsui()
        {
            us(8, "text", "应用已启动")
        }
    }
</eventItme>
```

14.2 列表 + 网络请求模板

```yu
// 加载列表数据
t()
{
    hs("https://api.example.com/list", "utf-8", response)
    json(response, obj)
    json(obj, "list", "data", list)
    
    ufnsui()
    {
        // 清空旧数据
        ula(listObj, null)
        
        // 添加新数据
        json(list, "size", size)
        for(i; size)
        {
            json(list, "data", i, item)
            json(item, "get", "title", title)
            ula(listObj, -1=title, -2=i)  // -1作为标识
        }
        ula(listObj)  // 刷新
    }
}
```

14.3 表单提交模板

```yu
<eventItme type="clicki">
    // 获取表单数据
    ug(13, "text", username)
    ug(14, "text", password)
    
    f(username == null || username == "")
    {
        tw("请输入用户名")
        endcode
    }
    f(password == null || password == "")
    {
        tw("请输入密码")
        endcode
    }
    
    // 提交数据
    t()
    {
        s postData = "username=" + username + "&password=" + password
        hs("https://api.example.com/login", postData, "utf-8", response)
        
        ufnsui()
        {
            json(response, obj)
            json(obj, "get", "code", code)
            f(code == 200)
            {
                tw("登录成功")
                uigo("main.iyu")
            }
            else
            {
                json(obj, "get", "msg", msg)
                tw("登录失败: " + msg)
            }
        }
    }
</eventItme>
```

14.4 文件下载 + 进度模板

```yu
t()
{
    hd("https://example.com/file.zip", "%downloads/file.zip", true, result)
    f(result == 0)
    {
        ufnsui()
        {
            tw("下载成功")
        }
    }
    else f(result == -1)
    {
        ufnsui()
        {
            tw("下载失败")
        }
    }
    else
    {
        ufnsui()
        {
            tw("文件已存在")
        }
    }
}
```

14.5 模块化设计模板

```yu
// 文件: api.myu
fn get(url, callback)
{
    t()
    {
        hs(url, "utf-8", response)
        // 通过全局变量返回结果
        sss apiResponse = response
    }
}

fn parse(jsonText)
{
    json(jsonText, obj)
    return obj
}

// 调用
call(null, "myu", "api.get", "https://api.example.com/data")
stop(500)  // 等待响应
json(sss.apiResponse, obj)
```

第十五章：常见问题速查

15.1 编译/运行时错误

错误信息 原因 解决方法
YuErr: ... 裕语言语法错误 检查转义、括号匹配
MJavaErr: Parse error Java语法错误 检查Java代码
启动闪退 uigo 没加后缀 所有跳转加 .iyu
null 访问错误 变量未初始化 检查 s/ss/sss 声明
ClassNotFoundException MJava类名错误 检查类名匹配文件名

15.2 常见编码问题

问题 解决方法
中文乱码 使用 "utf-8" 编码
特殊字符报错 在 ( ) , = ! > < ? * + { } \| & 前加 \
转义混乱 记住 \ 是转义符，\\ 表示一个 \

15.3 界面问题

问题 解决方法
控件不显示 检查 visibility
布局错乱 检查 layout_weight 和 gravity
列表不刷新 调用 ula(listObj) 刷新
滚动不生效 检查 ScrollView 高度
按钮无点击效果 添加 clickable=true

请严格按照上述要求编写界面/编程


《裕语言》速成开发手册3.0

 用户编程交流QQ群：
 官方源码开源群：323924434
 iApp技术开发群：483556574
 官方1群：1042334128
 官方2群：781302772
 官方3群：291033193
 官方4群：549133854
 官方5群：705873634
 官方游戏开发群：379221113


《裕语言》是一基于java的扩展性脚本语言，丰富的类库定置简单快速编程开发你的应用程序，让编程开发过程变得简单化、大众化。《裕语言》是由游改乐计算编程工程师 黄裕先生、宇恒先生 定制以及实现成型代码功能，其代码简单方便的编写体验是一大亮点，目前还会有更多强大的功能完善中。裕语言V3.0是基于iApp平台上运行的程序语言，任何有兴趣的人都可以参与开放设计自己的代码程序。

【3.0 iyu升级简介】
1. uigo代码必须加文件后缀，如 uigo("a.iyu") 或 uigo("a.ilua")。 否则将会闪退等。
2. uls必须正确的 输入界面宽度，输入界面高度。 否则界面列表可能异常。
3. 代码中双引号需进行转义，如 fw("%a.txt", "ab"cd") 修改成 fw("%a.txt", "ab\"cd")


【java yuv3交互原生java代码】
用法：
// 将代码放入 载入事件，当然也可以放在其他事件里。
// java代码块 可作为补充 裕语言V3 的新选择，它与裕V3同步执行，互相补充使用。可提升效率以及提升代码安全性。

syso("裕v3开始执行载入事件")
// 声明一个变量
s xx1 = "我是xx1"
sss xx2 = "我是xx2"

java {
  // java交互的文档
  // 说明：裕V3与java的交互调用是同步的，需要自己控制在 新线程 或 界面线程 中调用。
  // 类名：androidx.Yu3Java.class —— 该类不可混淆
  // 预设可用方法：
  // public static int yu(Object yuc, String code) —— 裕V3交互方法
  // public static void syso(Object yuc, Object log) —— 裕V3打印输出
  //
  // 预设可用变量(注意请勿在java代码块中重复申明)：
  // final Activity activity,  —— Activity对象
  // final Context context,  —— Context对象
  // final Object yuc,  —— 裕V3对象，用于 java 与 裕V3交互使用
  // final HashMap<String, Object> sss,  —— 裕V3全局变量
  // final HashMap<String, Object> ss,  —— 裕V3界面变量
  // final HashMap<String, Object> s —— 裕V3局部变量

    // 读取局部变量
    Object xx1 = s.get("xx1");

    // 申明写入局部变量：xx3
    s.put("xx3", xx1 + " 我在java里写入变量");

    // 读取全局变量
    Object xx2 = sss.get("xx2");

    // 申明写入全局变量：xx5
    sss.put("xx5", xx2 + " 我在java里写入变量");

    // java 代码与 裕V3同步执行，注意调用的先后顺序。如，这里先在 裕V3申明了 xx1 局部变量，
    // 然后按照顺序在 java 代码块里读取 xx1 变量，并申明写入 xx3 变量，然后 java代码块执行完毕后，
    // 接着又可以在裕V3 读取在 java代码里申明的 xx3变量了。

}

// 在裕v3读取 java 代码块里申明写入的裕v3的 xx3变量
syso("局部变量 xx3：" + xx3)
// 在裕v3读取 java 代码块里申明写入的裕v3的 xx3变量
syso("全局变量 xx5：" + sss.xx5)

// 在java代码块执行 裕V3代码
java{

    // 申明写入局部变量：xx6
    s.put("xx6", "我在java里写入xx6变量");

    // yuc 是裕V3对象，然后传入要输出的数据
    syso(yuc,
        " xx1:" + s.get("xx1") +
        " xx2:" + sss.get("xx2") +
        " xx3:" + s.get("xx3") +
        " xx6:" + s.get("xx6")
    );

    // 执行裕V3代码，注意转义字符
    yu(yuc,
        "s xx7 = 123\n" +
        "syso(\"xx7:\" + xx7)"
    );
}

/.
// 可选操作

java文件进行交互，首先创建一个 abc.java 的代码文件，注意包名 这里就用 com.ceshi 做例子，全部代码：
package com.ceshi;

import java.lang.*;

public class abc {

public String cs(Object aa) {
return aa + " 我调用了 com.ceshi.abc.cs(Object aa)";
}

}

然后把下面代码放入 载入事件，当然也可以放在其他事件：

s xx8 = "你好啊"
java
{
    com.ceshi.abc abc1 = new com.ceshi.abc();

    // 读取局部变量
    Object xx8 = s.get("xx8");
    // 写入局部变量，并且获取 abc1.cs 方法的值
    s.put("xx9", abc1.cs(xx8));
}
syso(xx9)

./

syso("裕v3结束执行载入事件")


【s 变量】
用法：

//申明一个变量，如果不赋值，系统将默认赋值 null
s a

申明事件局部变量
//可以赋数值
s a = 123
tw(a)

// 赋值表达式
s a1 = 50 * 10
s a2 = 12 * (a1 * 2)

s 我是变量 = 123
tw(我是变量)

申明界面变量
//可以赋字符串
ss a = "123"
tw(ss.a)

申明全局变量
//可以赋其他变量
sss b = a
tw(sss.b)

区域介绍：
局部变量：服务于一个事件，当用户与界面发生交互时，产生一个事件，仅供于该事件的变量产生以及操作。
界面变量：生产界面变量后，同一个界面中的所有事件，均可对其进行操作。
全局变量：生产全局变量后，同一个应用中的所有事件，均可对其进行操作。

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。裕语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。

提示：
变量的定义规范， 以 “s、ss、sss”开头。 然后加上自己自定义的变量名，比如“abc、 nihao、sfw123、www_zzw”变量不允许全部为数字，不允许掺杂符号，请不要使用太长的变量名，不推荐使用中文作为变量名。

空值：
如果访问一个没有声明的变量，将返回“null”空值类型，这个不对等于字符的 'null'。
判断是否空值的例子:(这里我们不知道变量“abc”是否空值)
f(abc == null)
{
	syso("是null")
}

【// 或 /. ./ 注释语句】
用法
//这个是变量“a”它的值等于“1”
s a = 1
//这个是变量“b”它的值等于“2”
s b = 2

/.
大量代码注释方法
s c = 3
s d = 4

./


说明：
注释语句符号可以用“//”，以注释符号开头的正行，将会被代码执行器无视。通常用于给自己标示代码的含义

提示：
此注释语句可用于属性。

提示：
不支持代码尾部使用注释语句，注释行必须开头为注释符，举错误的例：

s a = 1 //这个是变量“a”它的值等于“1”
//这个是变量“b”它的值等于“2” s b = 2

【syso 打印】
用法：
syso("1314")
可以打印出数据，代码同等于 System.out.println("1314")，可以在测试时，选择 调试日志查看打印数据。

s a1 = 10
syso("打印结果：" + (12 * (a1 * 2)))

说明：
打包后，安装运行可以通过 Log Tag：iapp 进行监听数据。

【f 判断语句】
用法：
s a = 2
f(a == 1)
{
	syso("等于1")
}
else f(a == 2)
{
	syso("等于2")
}
else
{
	syso("等于其他")
}

s a = 1
s b = 1
f(a == b)
{
	syso("等于")
}
else
{
	syso("不等于")
}

s a = "nimei"
s b = "nimei"
f(a == b)
{
	syso("等于")
}
else
{
	syso("不等于")
}

s a = 1
s b = 2
f(!(a == b))
{
	syso("等于")
}
else
{
	syso("不等于")
}

s a = 1
s b = 2
s c = 3
f(a < b && b < c)
{
	syso("等于")
}
else
{
	syso("不等于")
}

s a = 1
s b = 2
s c = 2
f(a == b || b == c + a)
{
	syso("等于")
}
else
{
	syso("不等于")
}

// 单变量判断，当值为 0 或 null 或 false 时条件为不成立，否则其他值条件都为成立；
s a = "abc"
s b = 123
s c = true
f(a)
{
	syso("a变量条件成立")
}
else f(b)
{
	syso("b变量条件成立")
}
else f(c)
{
	syso("c变量条件成立")
}

说明：
条件判断语句，用于两个值的比较，常用于判断值是否对等与数值的大小，判断数据需要同类型数据对比。表达式返回的“是”，那么将执行 { 代码 } 里面的代码。“否”将执行else后面的代码（不支持运算表达式，例：a+b=2）

支持运算符（返回 是 与 否）：
== 是否对等
!= 是否不等于
>= 是否大于或等于
<= 是否小于或等于
> 是否大于
< 是否小于
?* 字符串开头是否相同
*? 字符串结尾是否相同
? 字符串是否被包含
上面三个举例：
s a = "abcdef"
f(a ?* "abc") 返回“是”
f(a *? "def") 返回“是”
f(a ? "cde") 返回“是”

支持逻辑运算符：
|| 或者
&& 并且
! 反意

【w 循环】
用法：
//这将循环99次
s a = 99
w(a > 0)
{
syso(a)
s(a - 1, a)
}

说明：
条件循环语句，比较值的变化，然后进行循环执行 { 代码 } 里面的代码。当条件为“否”的时候会停止循环，条件“是”的话，将一直循环执行。
支持运算符（返回 是 与 否）：（跟 f 语句 一样，请参考）

【for 循环】
用法：
for(1; 20)
{
	syso("循环20次")
}

s a = 1
s b = 10
for(a; b)
{
	syso("循环10次")
}

// 三参数的for循环
for(s a=1; a<10; a++)
{
    // 循环打印a变量
	syso(a)
}
for(s a=1; a<100; a+=10)
{
    // 循环打印a变量
	syso(a)
}

for(s a=10; a>0; a--)
{
    // 循环打印a变量
	syso(a)
}

for(s a=100; a>0; a-=10)
{
    // 循环打印a变量
	syso(a)
}

s a = "12;12;12;12;12"
s b = ";"
// 将字符串变量 a 分割成一个数组
sl(a, b, c)
// 循环数组
for(d; c)
{
//将打印5次：12
syso(d)
}

说明：
参数可以给予另个参数，一个为初始循环的值，一个是最大循环值。


【t 新线程】
用法：
t()
{
	syso("新线程里执行代码")
}

说明：
启用新线程，去执行一些需要执行很久的代码。比如把下载文件，获取网页源码，大量的文件操作，可以放入新线里执行。这里线程的概念，启用新的线程帮你处理代码，这样不会影响到主线程。

【ssj 设置或修改控件事件代码】
用法：
s id = 3
ssj(id, "clicki")
{
tw("ok")
}

说明：
输入控件Id，输入事件类型，并将事件代码填写在 { 中 }，动态控件将触发该事件代码。

事件类型：
clicki=单击事件
touchmonitor=触屏监听事件
press=触屏长按事件
keyboard=键盘触发事件
pressmenu=触屏长按菜单事件
editormonitor=框编辑监听事件
ontextchanged=文本内容已改变
beforetextchanged=文本内容改变之前
aftertextchanged=文本内容改变之后
focuschange=获得焦点事件
onscrollstatechanged=滚动状态已改变
onscroll=滚动
clickitem=单击项目事件
onprogresschanged=加载过程进度改变
shouldoverrideurlloading=加载网址之前
ondownloadstart=文件下载事件
onpageselected=滑动切换界面事件
onpagescrolled=滑动切换界面过程
onpagescrollstatechanged=滑动操作过程
ondrawerclosed=侧滑关闭事件
ondraweropened=侧滑展示事件
onoptionsitemselected=项目选择
onitemselected=选择项目事件


【tw 提示】
用法：
tw("你好")

//设置参数1：显示的时间长久；0：显示的时间短暂；\n为换行的意思，其他地方通用
tw("你好\n吗？", 1)

说明：
用于提醒用户，界面显示时长大约为 2秒钟。弹出代码中的文字，来提醒用户。

【fd 删除文件】
用法：(将删除SD卡根目录的abc.zip文件)
s a = "%abc.zip"
fd(a, b)
tw(b)

说明：
用于删除指定的文件，是否成功返回数据：true或 false

提示：同时将创建变量“b”，作为记录返回的值。（通用于下咧）

【fe 文件是否存在】
用法：(将判断SD卡根目录的abc.zip文件是否存在)
s a = "%abc.zip"
fe(a, b)
tw(b)

说明：
用于判断指定的文件存在，是否存在返回数据：true或 false

【fs 文件大小】
用法：(将获取SD卡根目录的abc.zip文件占用的大小)
s a = "%abc.zip"
fs(a, b)
tw(b)

说明：
用于判断指定的文件大小，是否存在返回数值单位(字节)，若获取失败将返回 “-1”。
转换为KB：
s a = "%abc.zip"
fs(a, b)
s(b/1024, b)
tw(b)

转换为MB：
s2(b/1024/1024, b)
//保留所有小数
sn(b/1024/1024, b2)

【fr 读取文本】
用法：(将读取SD卡根目录的abc.txt文件里面的内容)
s a = "%abc.txt"
fr(a, b)
tw(b)

s a = "%abc.txt"
s b = "utf-8"
fr(a, b, c)
tw(c)

说明：
用于读取文本文件的数据内容。

【fc 复制文件】
用法：（在SD卡根目录abc.txt文件拷贝一个新的副本至abc2.txt）
s a = "%abc.txt"
s b = "%abc2.txt"
fc(a, b, c)

//设置重复不覆盖
s c = false
fc(a, b, c, d)

说明：
用于复制文件，创建一个新的副本文件。是否成功返回数据：true或 false


【fw 写入文本】
用法：(将文本数据写入至SD卡根目录的abc.txt文件里面)
s a = "%abc.txt"
s b = "我是一个txt文件的内容"
fw(a, b)

s a = "%abc.txt"
s b = "我是一个txt文件的内容"
s c = "utf-8"
fw(a, b, c)

说明：
用于写入文件。

【fl 文件列表】
用法：（获取一个目录的文件列表）
s a = "%dir"
fl(a, b)
for(c; b)
{
	syso(c)
}

//仅获取文件夹
s a = "%dir"
fl(a, true, b)
for(c; b)
{
	syso(c)
}

//仅获取文件
s a = "%dir"
fl(a, false, b)
for(c; b)
{
	syso(c)
}

说明：上面例子是获取sd卡根目录文件夹“dir”里面的所有子目录以及文件，并获取结果传入变量“b”，并用for循环，来读取变量“b”里面的列表数据，并把列表数据复制给变量“c”，其中代码会自动创建并赋值好变量：b、c

提示：
看似有些复杂，理解了就简单了， 这里的变量“b”类型是一个数组，里面包含了一个数据列表。通过for循环可以顺序读取这个列表。并每次循环把每列的数据赋值给变量“c”

【ft 转移文件】
用法：（将SD卡根目录的abc.txt转移至abc3.txt）
s a = "%abc.txt"
s b = "%abc3.txt"
ft(a, b, c)
tw(c)

说明：
用于转移文件。是否成功返回数据：true或 false

【fdir 获取SD卡根目录路径】
用法：（获取根目录路径并赋值至变量“a”）
//获取根目录
fdir(a)
tw(a)

//获取目录的绝对路径
s a = "%dir"
fdir(a, b)
tw(b)

说明：
通过获取根目录路径，就可以计算文件的绝对路径。

【fuz 解压zip部分文件】
用法：（将根目录文件abc.apk压缩包里的AndroidManifest.xml文件，解压到根目录AndroidManifest2.xml）
s a = "%abc.apk"
s b = "AndroidManifest.xml"
s c = "%abc"
fuz(a, b, c, d)
tw(d)

//解压文件遇到重复不覆盖
s a = "%abc.apk"
s b = "AndroidManifest.xml"
s c = "%abc"
s d = false
fuz(a, b, c, d, e)
tw(e)

说明：
通过上面代码可以实现压缩包解压部分的文件，并返回赋值至变量“d”解压文件的数量。

【fuzs 解压整个zip】
用法：(将根目录文件abc.apk压缩包解压至根目录文件夹abcdir，会自动创建)
s a = "%abc.apk"
s b = "%abcdir"
fuzs(a, b, c)
tw(c)

//解压文件遇到重复不覆盖
s a = "%abc.apk"
s b = "%abcdir"
s c = false
fuzs(a, b, c, d)
tw(d)

说明：
通过上面代码将解压整个压缩包至指定文件，并赋值至变量“c”，是否成功返回数据：true或 false

【fj 压缩文件或文件夹至zip】
用法：
s a = "%adc.txt"
s b = "%abc.zip"
fj(a, b, c)
tw(c)

//不去除根目录
s a = "%adc.txt"
s b = "%abc.zip"
s c = false
fj(a, b, c, d)
tw(d)

说明：
压缩文件。返回赋值数据：true 或 false

【fo 打开文件】
用法：（将根目录打开安装abc.apk文件）
s a = "%abc.apk"
fo(a)

说明：
可以调用系统工具打开不同的文件。

【s+-*/% 运算方式】
用法：
s a = 2

//加法例子赋值a=4
s+(2, a)
//减法例子赋值a=3
s-(5, a)
//乘法例子赋值a=6
s*(3, a)
//除法例子赋值a=4
s/(8, a)
//求余例子赋值a=2
s%(5, a)

//其他用法

//加法例子赋值a=7
s+(2, 5, a)

//乘法例子赋值b=8，保留小数
s*(4, a, true, b)


说明：
此方法的效率高于 s计算表达式、sb计算表达式。 在循环数据运行时，是受到推荐的用法。

【s 计算表达式】
用法：（用于计算表达式）
s a = 12
s b = 13
s(a + b, c)
//将提示：25
tw(c)

s a = 60
s b = 14
s(a / (b + 12), c)
//将提示：2 （自动去除了小数）
tw(c)

// 也可以用于将小数转为整数
s a = 60.123
s(a, c)
//将提示：60 （自动去除了小数）
tw(c)

说明：
用于计算数据表达式，不支持逻辑表达式计算。

【s2 计算表达式】
说明：
功能跟上面的一样，但这个会保留2位小数。

【sn 计算表达式】
说明：
功能跟上面的一样，但保留所有小数。

【ss 变量相加】
用法：
s a = "123"
s b = "789"
ss(a + "456" + b, c)
//将提示：123456789
tw(c)

说明：
将字符串数据相连，并赋值至变量“c”。

【sr 替换字符】
用法：
s a = "123456789"
s b = "456"
s c = "."
sr(a, b, c, d)

//将提示：123.789
tw(d)

//支持正则表达式
//sr(a, b, c, true, d)

说明：
用于替换字符

【sj 截取字符】
用法：
s a = "123456789"
s b = "34"
s c = "8"
sj(a, b, c, d)
//将提示：567
tw(d)

//从头部开始截取
sj(a, null, c, d)
tw(d)

//截取到尾部
sj(a, b, null, d)
tw(d)


说明：
用于截取数据部分字符

【sl 数据数组】
用法：
s a = "12;12;12;12;12"
s b = ";"
sl(a, b, c)

//可以支持正则表达式；例子看（注意说明）
//sl(a, b, true, c)

for(d; c)
{
//将打印5次：12
	syso(d)
}

说明：
将把变量“a”的字符串，切割成一个数组，以字符“.”为分割字符。并用循环顺序打印出数据。

注意：
如果支持正则表达式数据数组，上例子的 s b = ";" 其内的值。需要转义的特殊字符 “$()*+.[]?\^{},|”

支持正则的特殊字符转义方法：
如：
s a = "12|a$12|a$12|a$12|a$12"

//关键分割字符串如果包含特殊字符，需要在每个特殊字符前面增加“\”
s b = "\\|a\\$"
sl(a, b, true, c)

for(d; c)
{
//将打印5次：12
	syso(d)
}

【siof 获取字符位置】
用法：
s a = "123456789"
s b = "3"
s c = 0
siof(a, b, c, d)
//将提示：2
tw(d)

s a = "123456789"
s b = "3"
siof(a, b, c)
//将提示：2
tw(c)
说明：
从前面向后面进行匹配。字符位置以0计算，若无数据找到将返回 -1

【slof 获取字符位置】
用法：
s a = "123456789"
s b = "4"
s c = 8
slof(a, b, c, d)
//将提示：3
tw(d)

s a = "123456789"
s b = "4"
slof(a, b, c)
//将提示：3
tw(c)

说明：
从后面向前面进行匹配。字符位置以0计算，若无数据找到将返回 -1


【ssg 截取字符】
用法：
s a = "abcdefghijk"
ssg(a, 2, 6, b)
//将提示：cdef
tw(b)

s a = "abcdefghijk"
ssg(a, 6, b)
//将提示：ghijk
tw(b)

说明：
根据字符的位置进行截取字符，若失败将变量“b”赋值 null


【slg 获取字符长度】
用法：
s a = "123456789"
slg(a, b)
//将提示：9
tw(b)

说明：
顾名思义。

【strim 去除头尾空格】
用法：
s a = "   123456789 "
strim(a, b)
//将提示:123456789
tw(b)

说明：
常用于去除后进行判断头尾字符。

【slower 转换为小写】
用法：
s a = "AiufSUscN"
slower(a, b)
//将提示:aiufsuscn
tw(b)

说明：
常用于转换为小写后进行判断。

【supper 转换为大写】
用法：
s a = "AiufSUscN"
supper(a, b)
//将提示:AIUFSUSCN
tw(b)

说明：
常用于转换为大写后进行判断。

【stop 暂停代码】
用法：
t()
{
syso("1")

stop(1000)
syso("2")

stop(1000)
syso("3")

stop(1000)
syso("4")
}

说明：
每次执行 stop(1000) 将暂停1秒后，再执行下面代码。单位为毫秒：1000毫秒 = 1秒

【sran 生产范围随机数】
用法：（生产一个 100 至 1000的随机数）
sran(100, 1000, a)
tw(a)

说明：
有时候需要利用到随机机制，可以利用这个来开发！

【nsz 创建数组】
用法：
s a = 6
nsz(a, b)

或

//指定数组数据类型
s a = 6
nsz(a, "String", b)

说明：
申明一个数组。并且舍子数组总行数为6

【sgsz 指定访问数组维数】
用法：（根据序号访问数组）
s a = "12;34;56;78;90"
s b = ";"
sl(a, b, c)
sgsz(c, 2, d)
tw(d)

说明：
数组可以进行列表形式存储数据，常用于数据列表。注意的是序号是从0开始的。数组总行数如果是5，那序号最大为4

【sssz 设置数组数据】
用法：
s a = 6
nsz(a, b)
s c = 1
s d = "数据"
sssz(b, c, d)

说明：
指定数组序号设置数组的数据。


【sgszl 访问数组总行数】
用法：
s a = "12;34;56;78;90"
s b = ";"
sl(a, b, c)
sgszl(c, d)
tw(d)

说明：
可以获取到长度，更准确的访问数组

【hs 获取网页源码】
用法：
t()
{
s a = "https://m.baidu.com/"
hs(a, b)
syso(b)
}

2，提交post数据:
如果参数包含 & 为普通字符，可以进行转义 \& 如提交数据:&text=abc\&def
输入说明：地址，post数据提交，目标网页编码，赋值变量
t()
{
s a = "https://m.baidu.com/"
hs(a, "title=你好&text=你好吗？", "utf-8", b)
syso(b)
}

// 也可以提交 json数据
t()
{
s a = "https://m.baidu.com/"
s data = "{\"id\":1, \"name\":\"xiaobai\", \"age\":16}"
hs(a, data, "utf-8", b)
syso(b)
}


3，带自定义cookie方式获取网页:
//传递cookie项值，格式为nama=value 下例： uid=112;name=nihao;sb=123456789;

t()
{
s a = "https://m.baidu.com/"
hs(a, "title=你好&text=你好吗？", "utf-8", "uid=112;name=nihao;sb=123456789;", b)
syso(b)
}

4，带自动设置cookie方式获取网页，并记录当前网页的Cookie:
//传递cookie项值，当自定义为null 系统将自动设置已记录的cookie
t()
{
s a = "https://m.baidu.com/"
hs(a, "title=你好&text=你好吗？", "utf-8", null, true, b)
syso(b)
}

5，带自动设置cookie方式获取网页，并记录当前网页的Cookie，并设置Header头:（可设置多条，以“||”隔开）文件头包括了Cookie，User-Agent设备型号。
//传递cookie项值，当自定义为null 系统将自动设置已记录的cookie
t()
{
s a = "https://m.baidu.com/"
hs(a, "title=你好&text=你好吗？", "utf-8", null, true, "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept=*/*||accept-language=zh-CN", b)
syso(b)
}

6，带自动设置cookie方式获取网页，并记录当前网页的Cookie，并设置Header头:（可设置多条，以“||”隔开）文件头包括了Cookie，User-Agent设备型号，设置连接超时，设置接收超时，设置代理IP。
//传递cookie项值，当自定义为null 系统将自动设置已记录的cookie
t()
{
s a = "https://m.baidu.com/"
hs(a, "title=你好&text=你好吗？", "utf-8", null, true, "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept=*/*||accept-language=zh-CN", 20000, 20000, "10.0.0.172:80", b)
syso(b)
}

7，应用系统存储Cookie的浏览查看，返回赋值变量为字符串
hs("cookie", b)

8，应用系统存储Cookie的清空，无赋值变量
hs("del cookie")

9，应用系统存储Cookie的浏览查看指定网址，返回赋值变量为字符串
hs("cookie:https://m.baidu.com", b)

说明：
这里先开了一个线程，然后在线程里执行获取网页源码的工作，开线程是担心有些主线程界面。大部分网页都需要使用cookie登陆，可使用工具查询所需cookie然后进行操作。
设置cookie有说明作用？
1.登陆用户名
2.获取验证码图片并发送验证码
....

【hd 下载文件】
用法：（下载文件至SD卡根目录 abc.apk）

1，下载文件，默认不覆盖重复
t()
{
s a = "http://abc.com/abc.apk"
s b = "abc.apk"
hd(a, b, c)
syso(c)
}

2，设置重复是否覆盖
t()
{
s a = "http://abc.com/abc.apk"
s b = "abc.apk"
hd(a, b, true, c)
syso(c)
}


3，带自动设置cookie方式下载网页形式文件（如图片形式验证码，论坛的附件等），支持post数据，自定义Cookie或系统设置Cookie，并记录当前网页的Cookie，并设置重复是否覆盖。可参考hs获取网页，并设置Header头:（可设置多条，以“||”隔开，也可留空为null）
输入说明：下载地址，保存文件位置，是否重复覆盖，post数据提交，目标网页编码，自定义Cookie，是否系统自动设置Cookie，设置Header头，赋值变量
t()
{
s a = "http://abc.com/abc.apk"
s b = "abc.apk"
hd(a, b, true, "title=你好&text=你好吗？", "utf-8", null, true, null, b)
syso(b)
}

// 也可以提交 json数据
t()
{
s a = "http://abc.com/abc.apk"
s b = "abc.apk"
s data = "{\"id\":1, \"name\":\"xiaobai\", \"age\":16}"
hd(a, b, true, data, "utf-8", null, true, null, b)
syso(b)
}

说明：
开个线程，然后在里面下载一个文件。并存到SD卡。下载结果将赋值到变量“c”
返回的赋值：
1 文件已经存在
0 下载成功
-1 下载失败

【hw 访问网页】
用法：
s a = "https://m.baidu.com/"
hw(a)

说明：
使用内置浏览器访问网页。
可用于下载文件：
s a = "http://abc.com/abc.apk"
hw(a)


//跳转访问网页，并且自定义标题栏颜色
//主体颜色
s b = "#387bd6"
//底部横杠颜色
s c = "#255eab"
hw("https://m.baidu.com/", b, c)

【hws 系统浏览器访问网页】
用法：
s a = "https://m.baidu.com/"
hws(a)

说明：
使用内置浏览器访问网页。
可用于下载文件：
s a = "http://abc.com/abc.apk"
hws(a)

【ug 获取控件属性】
用法：(1为：控件ID，第二个参数为控件属性标识，然后赋值到变量)
//
ug(1, "text", c)

//获取侧滑控件 左边的侧滑是否展开状态
ug(1, "isdraweropen", "start", c)

说明：
输入属性标示来返回不同的控件数据。注意：有些控件没有指定属性，将返回null。下面有属性介绍，可参考。

可用属性标识：
text=内容、background=背景、width=宽度、height=高度、x=X轴、y=Y轴、paddingleft=左内边距、paddingtop=顶内边距、paddingright右内边距、paddingbottom=底内边距、layout_marginleft=左外边距、layout_margintop=顶外边距、layout_marginright=右外边距、layout_marginbottom=底外边距、
hint=提示字符、imeoptions=虚拟键盘按键状态、visibility=控件可视状态、checked=选项是否被选中、title=浏览器网页标题、url=浏览器网址、lastvisibleposition=列表滑动到项目位置的序号、count=列表项目总数、
selecteditem=获取下拉框选值、rating=评分当前数值、progress=控件当前进度数值、date=日期控件选值、time=时间控件选值、currentitem=获得滑动窗体界面序号、isdraweropen=侧滑是否界面展开状态、selectionstart=获取文本框光标开始位置、selectionend=获取文本框光标结束位置、
cangoback=是否存在可返回的网页、cangoforward=是否存在可前进的网页、collapsecolumns=表格布局获取指定列是否折叠、shrinkcolumns=表格布局获取指定的列是否可收缩、stretchcolumns=表格布局获取指定的列是否可拉伸、shrinkcolumnsall=表格布局获取指示是否所有的列都是可收缩的、
stretchcolumnsall=表格布局获取指示是否所有的列都是可拉伸的


【us 设置控件属性】
用法：(1为：控件ID，第二个参数为控件属性标识，第三个是需要设置的数据或变量)

//设置文本控件内容
s c = "文本内容"
us(1, "text", c)

// 设置背景颜色
us(1, "background", "#666666")

// 设置背景图像
us(1, "background", "@b.png")

// 设置背景网络图像
us(1, "background", "http://abc.com/abc.png")

// 图像控件设置图像
us(1, "src", "@b.png")

// 设置网络图像
us(1, "src", "http://abc.com/abc.png")

//关闭下拉刷新加载图标
us(1, "refreshing", false)


//设置浏览器的连接url
s c = "https://m.baidu.com/"
us(2, "url", c)
//提示：如果浏览器正在播放视频或音乐，直接关闭浏览器可能还会有声音，建议关闭浏览器时先跳转成另一个网页。
//提示：如果需要加载本地的文件，可以 us(2, "url", "file:///android_asset/res/web.html") 加载安装包内assets/res/web.html文件


//设置浏览器显示的html文件或文本
s c = "<html><p>html内容</></html>"
s d = "utf-8"
s e = "text/html"
us(2, "url", c, d, e, f)
tw(f)


//设置控件阴影（部分控件有效果如文本、文本框、按钮）
s radius = 5
s dx = 0
s dy = 0
s color = "#000000"
us(2, "shadow", radius, dx, dy, color, f)
tw(f)


//带有赋值变量，变量d将返回数据是否设置成功 true 或 false
s c = "文本内容"
us(1, "text", c, d)

//设置文本框控件光标
us(1, "selection", 1, d)

//选中文本框部分内容
us(1, "selection", 1, 3, d)

//浏览器前进1个网页
us(1, "gobackorforward", 1)

//浏览器后退1个网页
us(1, "gobackorforward", -1)

//设置控件点击波纹效果颜色；需系统5.0以及以上才有效果；部分控件还需要设置 clickable=true 才有效果。
us(1, "backgroundripple", "#888888")

//设置编辑框光标颜色
us(1, "textcursordrawable", "#000000")

//自定义文本控件字体
us(1, "typeface", "@ttf.ttf")

说明：
输入控件标示设置控件数据。【可参照控件属性，所有属性标识通用】

更多属性标识：
currentitem=设置滑动窗体界面序号、closedrawer=关闭指定侧滑、opendrawer=展开指定侧滑、drawerlockmode=设置手势滑动、selection=设置文本框光标位置、gobackorforward=浏览器的前进或推后、backgroundripple=波纹效果、dh=执行动画（非队列动画）

【uigo 跳转界面】
用法：（输入界面文件名，跳转指定的界面）
uigo("abc.iyu")

//带参数的跳转
uigo("abc.iyu", 536870912)


说明：
可以界面之间的转换，扩展新的界面。

参数：
67108864：如果在内存中发现存在该界面，则清空这个界面之上的所有其他界面，使其处于栈顶。
268435456：系统会寻找或创建一个新的内存来放置该界面
1073741824：跳转到的界面，不排在内存中
536870912：当内存中存在该界面并且位手机的显示状态时，不再创建一个新的，直接利用这个界面。


【utw 弹出界面】
用法：（在原有的界面弹出界面）
s a = null
s b = "界面标题"
s c = "界面内容"
s d = "退出"
s e = "保存"
s f = "取消"

//三个按钮
//输入图标，输入标题，输入内容，输入按钮名称，输入按钮名称，输入按钮名称，输入是否点击弹窗以外界面是否关闭弹窗，输入赋值变量
utw(a, b, c, d, e, f, false, v)
{
syso("点击了确定")
}
else
{
syso("点击了保存")
}
else
{
syso("点击了取消")
}

//两个按钮
utw(a, b, c, d, e, false, v)
{
syso("点击了确定")
}
else
{
syso("点击了取消")
}

//一个按钮
utw(a, b, c, d, false, v)
{
syso("点击了确定")
}

//没有按钮
utw(a, b, c, false, v)


//将界面添加到弹窗界面上，直接将界面内容设为一个界面文件
s a = "界面标题"
s b = "a.iyu"
s c = "取消"
utw(null, a, b, c, false, v)
{
syso("点击了确定")
}

说明：
常用于询问用户当前的操作，弹窗展示内容。

赋值变量说明：
弹出界面需要设置一个赋值变量，用于自定义界面弹窗的操作。

【endutw 关闭弹出界面】
用法：
endutw()

说明：
用于关闭当前打开的弹窗界面

【end 结束界面】
用法：
end()

说明：
调用后，将结束当前的界面。 并返回原来的界面。如果原来没有界面，将退出应用。

【ends 显示桌面】
用法：
ends()

说明：
跳转到手机的桌面，程序将后台运行。

【bfm 播放音频】
用法：
s a = "%abc.mp3"
bfm(a)

s a = "http://www.abc.com/abc.mp3"
bfm(a)

s a = "%abc.mp3"
bfm(a, b)
//播放
//bfms(b, "st")
//暂停
//bfms(b, "pe")
//停止
//bfms(b, "sp")
//结束播放组件
//bfms(b, "re")
//是否在播放
//bfms(b, "ip", c)
//tw(c)

//获取音频时长（毫秒）
//bfms(b, "dn", c)
//tw(c)
//获取当前播放时长（毫秒）
//bfms(b, "cn", c)
//tw(c)

//指定播放的位置（毫秒）
//bfms(b, "seekto", 2000)

//设置音量（0-1.0 小数）
//bfms(b, "volume", 0.1, 0.9)

//一直循环播放
//bfms(b, "sl", true)

说明：
可以直接访问安装包里面的音频文件，也可以访问sd卡上的。

【html标签支持】
用法：
s a = "(html)<a href="https://m.baidu.com">百度</a>"
us(1, "text", a)

说明：
text属性：设置支持html代码！

【ula 列表操作内容】
用法：
//输入数据列表对象，输入数据项...不限制数量。
ula(a, 1="abc", 2="bac", 3="bbc")

//刷新列表显示内容，常用增加数据后的刷新。
ula(a)

//V7列表，刷新指定序号列表项目显示内容，常用增加数据后的刷新。
ula(a, 1)

//清空列表对象
ula(a, null)
//ula(a, "clear")

//获得列表对象，赋值返回v变量为列表对象
ula(a, "list", v)

说明：
根据数据列表，进行增加数据。

提示：
1=abc，其中1为控件id，abc为设置控件值
其中所谓的控件，为a.iyu界面中的控件。
增加标识数据，不作为设置控件数据，可在标识处设负数。如下：
-1=abc

提示：
如果需要设置 单选控件、多选控件 的选择状态，可设值为 true 或 false

注意：
将要执行事件的控件，必须在此设置值。如你有一个按钮控件无需设置值，但需要使用事件，可设置 1=null
不设置值的控件，将无法获取列表内容数据。

【uls 列表显示内容】
用法：
ula(a, 1="abc", 2="bac", 3="bbc")
s c = "a.iyu"
s d = -1
s e = -2
//列表项目界面高度 建议输入 -2 ，如果高度输入 -1 v7列表单项会填充整个界面。项目界面的宽度建议输入 -1
//输入控件id或控件对象，输入数据列表，输入列表项界面文件名，输入项目界面宽度，输入项目界面高度
uls(1, a, c, d, e)

//设置下拉选择列表
s a = "a;b;c"
s b = ";"
sl(a, b, c)
//输入控件id或控件对象，输入数据列表或数组数据
uls(1, c)


//自定义标签布局 的子项
ula(a, 1="abc", 2="bac", 3="bbc")
//输入控件id或控件对象，输入数据列表，输入列表项界面文件名，输入界面宽度，输入界面高度
uls(1, a, "a.iyu", -2, -2)

// 如果需要给列表中图像控件设置图像，可设置路径 或远程网络图像，比如ID 5 和 6 都是图像控件
// ula(a, 1="abc", 2="bac", 3="bbc", 5="%1.png", 6="http://abc.com/1.png")
// 输入控件id或控件对象，输入数据列表，输入列表项界面文件名，输入界面宽度，输入界面高度
// uls(1, a, "a.iyu", -2, -2)


说明：
设置列表控件、视图控件、下拉列表、标签布局 的数据。

注意：
列表控件、视图控件 设置的界面 a.iyu 其中的载入事件是允许被调用。
可以通过列表控件、视图控件 设置的界面 a.iyu 的载入事件，进行每项列表布局的个性化设计。
每当显示到每项列表内容就会调用一次此载入事件，并且将该项的布局控件赋值给 st_vW 变量对象，
然后可以通过 gvs(st_vW, a.2, b) 获取其中的子控件对象，然后进行操作子控件即可。
还可以通过 st_pN 获取当前的视图中的序号，方便判断目前操作的是那一个视图。


【ulag 获取列表内容数据】
用法：

//输入当前的控件对象，输入获取控件ID 1的数据参数，输入赋值变量
ulag(a, 1, b)

//输入当前的控件对象，输入获取标识为 -1的数据参数，输入赋值变量
ulag(a, -1, b)

//通过 数据列表对象 或 列表控件对象 获取数据
//输入数据列表对象 或 列表控件对象，输入视图中的位置序号，输入获取标识为 -1的数据参数，输入赋值变量
ulag(a, 1, -1, b)

//如v7列表、滑动窗体控制 的加载界面中的 载入事件里可使用此方法获取数据内容
ulag(st_vW, 1, b)


说明：
常用与在列表控件的事件中，获取参数数据与用户进行互动。获取失败将赋值变量为 null

注意：
使用此方法在uls中设置控件参数后，有设置参数的控件，在事件中可使用此方法。

【ulas 更新列表内容数据】
用法：

//输入当前的控件对象，输入获取控件ID 1的数据参数，输入新的数据
ulas(a, 1, b)

//输入当前的控件对象，输入获取标识为 -1的数据参数，输入新的数据
ulas(a, -1, b)

//通过 数据列表对象 或 列表控件对象 获取数据
//输入数据列表对象 或 列表控件对象，输入视图中的位置序号，输入获取标识为 -1的数据参数，输入新的数据
ulas(a, 1, -1, b)

//刷新列表显示内容，常用增加数据后的刷新。
ula(a)

//如v7列表、滑动窗体控制 的加载界面中的 载入事件里可使用此方法获取数据内容
ulas(st_vW, 1, b)


说明：
常用与更新修改列表内容数据。修改数据后，别忘记刷新列表。

【usms 发送短信】
用法：
s a = "10086"
s b = "0"
usms(a, b)

注意:测试时只显示syso日志，不直接 发送短信，打包即可。

【ucall 拨打电话】
用法：
s a = "10086"
ucall(a)

注意:测试时只显示syso日志，不直接 拨出号码，打包即可。

【time 当前时间】
用法：
s a = 0
time(a, b)
tw(b)

说明：
第一个参数为时间类型，第二个赋值变量

[数字类型]
0：2014-07-07 09:10:08
1：2014/07/07 09:10:08
2：2014-07-07
3：09:10:08
4：18144133553151
5：2014年07月07日 09:10:08
[字符类型，输入字符形式需引号概括]
Y 年
m 月
d 日
H 时
M 分
S 秒
a/A 星期几

【fi 判断路径是否文件夹】
用法：
s a = "%abc"
fi(a, b)
tw(b)

说明：
指定路径，判断是否为目录文件夹，返回：true 或 false

【swh 获取屏幕分辨率】
用法：
s a = "w"
//获取屏幕宽度的dp
swh(a, w)
s a = "h"
//获取屏幕高度的dp
swh(a, h)
s a = "hh"
//获取屏幕真实高度的dp
swh(a, hh)

s a = "pxw"
//获取屏幕宽度的px像素
swh(a, w)
s a = "pxh"
//获取屏幕高度的px像素
swh(a, h)
s a = "pxhh"
//获取屏幕真实高度的px像素
swh(a, hh)

s a = "pxztl"
//获取屏幕状态栏高度的px像素
swh(a, h)

s a = "pxbvk"
//获取屏幕底部虚拟键盘的高度的px像素
swh(a, h)

说明：
常用于获取屏幕的大小。

真实高度：不去除其他系统界面所占用（如状态栏）


【stobm 汉字转换编码字符】
用法：（你 转换 %E4%BD%A0）
stobm("你", "utf-8", b)
tw(b)

//转换网址中的汉字
stobm("你", "utf-8", true, b)
tw(b)

说明：
有些时候网络操作的时候，网址需要带有字符参数，就可以把这个汉字转换下。

【sutf8to 将UTF-8编码字符转换中文】
sutf8to("%E4%BD%A0", b)
tw(b)

//网址中的汉字
sutf8to("%E4%BD%A0", "utf-8", true, b)
tw(b)

【uycl 隐藏状态栏】
用法：
//隐藏状态栏
uycl(true)
//不隐藏状态栏
uycl(false)

// 将状态栏文字设为暗色，输入整数
uycl(1)

// 将状态栏文字设为亮色，输入整数
uycl(0)

// 进入全屏效果
uycl(-1)

// 退出全屏效果
uycl(-2)

// 隐藏底部导航条
uycl(-3)

// 不隐藏底部导航条
uycl(-4)

// 注意：功能 -5 至 -10 需 targetSdk 36 才有效。
// 全屏侵入式效果
uycl(-5)

// 仅状态栏侵入式效果
uycl(-6)

// 仅底部导航条侵入式效果。API 30以内的低版本可能无效果
uycl(-7)

// 全屏侵入式效果，并且没有进行虚拟键盘处理
uycl(-8)

// 保留状态栏和底部导航条，并且没有进行虚拟键盘处理
uycl(-9)

// 移除所有预设状态栏和底部导航条处理效果；移除后可以自己设计处理方案
uycl(-10)

说明：
隐藏手机顶部的状态栏

【uycl 修改状态栏颜色】
用法：

//输入更变颜色，并且保留状态栏空间，并默认设置底部导航键
uycl("#50c4e5", true)

//输入更变颜色，并且不保留状态栏空间，并默认设置底部导航键
uycl("#50c4e5", false)

//输入更变颜色，并且保留状态栏空间，只设置状态栏，不设置底部导航键
uycl("#50c4e5", true, 0)

//输入更变颜色，并且不保留状态栏空间，只设置底部导航键，不设置状态栏
uycl("#50c4e5", false, 1)


说明：
常用与设置一体化颜色，以及更变不同的状态栏颜色。

ps:如果不保留状态栏空间的话，你的底部控件可能会与底部软键盘重叠，你可以使用 swh 获取底部虚拟键盘的高度，然后可以增加一个底部外边距。

注意：
仅系统android 4.4以及以上才有效果，系统android 5.0以及以上效果更佳！
android 4.4以下的系统，无效果！

【ushsp 设置横屏或竖屏】
用法：
//横屏
ushsp(true)
//竖屏
ushsp(false)

说明：
设置屏幕的显示方式，注意的是设置后载入事件将重新执行

【bfv 播放视频】
用法：(播放SD卡上的视频文件)
s a = "%abcd.mp4"
bfv(a)

//并且横屏
s a = "%abcd.mp4"
s b = true
bfv(a, b)


//并且横屏
s a = "http://m.baidu.com/abcd.mp4"
s b = true
bfv(a, b)
说明：
此方法将全屏播放SD卡上的视频文件。调用自带的播放器。

注意：
不支持加载assets文件。支持SD卡文件、应用私有文件、（http）远程网络文件！

支持格式：
3gp、MP4、avi

【endcode 结束执行】
用法：
s a = 1
s b = 1
f(a == b)
{
tw("会提示")
//结束执行代码
endcode
}
tw("不会提示")

说明：
可用于提前结束执行代码，也可以用于模块的函数结束。

【break 跳出循环以及代码块】
用法：
w(1 == 1)
{
syso("1")
break
syso("2")
}
f(1 == 1)
{
syso("1")
break
syso("2")
}

说明：
代码块当执行 break 语句后，将跳出。


【fn 模块与函数】
1.创建一个模块：
在程序文件列表，新建一个模块名“mokuai”

2.在模块mokuai.myu里定义各种函数：
fn hanshu(a, b)
ss(a + b, c)
tw(c)
end fn
fn hanshu(a)
tw(a)
end fn

3.在事件里根据模块对象来调用函数：
s a = "abc"
s b = "def"
fn mokuai.hanshu(a, b)
fn mokuai.hanshu(a)

说明：
常用与将重复性的代码，放入模块中执行。

注意：
模块的调用过程将不共享使用 调用事件的局部变量；

例：
//(m.myu模块代码)
fn abc()
s bb = "456"
sss cc = "789"
end fn

//（mian.iyu载入事件代码）
s bb = "123"
fn abc()

//将提示 123，因为模块代码与事件代码的局部变量是不共享的；
tw(bb)

//将提示 789，可以通过全局变量进行共享数据
tw(sss.cc)

【ftz 发送通知栏】
用法：

ftz("提醒标题", "标题", "内容", null)
{
tw("点击了")
}

//设置显示图标
ftz("提醒标题", "标题", "内容", "%abc.png")
{
tw("点击了")
}

说明：
可以用于通知用户。

【uapp 打开App应用或游戏】
用法：
uapp("com.iapp", c)

//或 带有指定类名的启动
uapp("com.iapp", "com.yougaile.MakeiApp.logoActivity", c)

说明：
输入应用包名，赋值变量； 赋值变量返回启动结果：true 或 false

【uapplist 获取App列表】
用法：
uapplist(true, b)
sgsz(b, 1, d)
tw(d)

说明：
输入 是否包括获取系统App，返回一个列表数组 至变量 “b”，每列数据将存储一个应用的信息，并且以 “\n”隔开。

其中列内容格式：
应用包名，启动类，应用标题，应用版本

【uapplistgo 获取正在运行的App列表】
用法：
uapplistgo(b)
sgsz(b, 1, d)
tw(d)

说明：
输入 返回一个列表数组 至变量 “b”，每列数据将存储一个应用的信息，并且以 “\n”隔开。

其中列内容格式：
应用包名，pid, uid

【uninapp 卸载应用】
用法：
uninapp("com.iapp")

说明：
输入应用包名

【huf 上传文件】
用法：
t()
{
s a = "http://abc.com/upfile.php"
s b = "filename=iApp我的应用.apk&test=一款非常好的应用哦"
s c = "%abc/iApp.apk"
// 支持多文件上传
//s c = "%abc/iApp.apk|%abc/iApp2.apk|%abc/iApp3.apk"
s d = "utf-8"
huf(a, b, c, d, e)
syso(e)
}

// 设置 上传文件的name标签（非文件名），这个标签对应后台的设置，设置不当可能后台无法接收到文件
t()
{
s a = "http://abc.com/upfile.php"
s b = "filename=iApp我的应用.apk&test=一款非常好的应用哦"

// 将这个文件设置为 img name标签，不设置的话默认为 file
s c = "img\n%abc/iApp.apk"
// 支持多文件上传，多文件设置name标签需要在尾部加 []  比如 img[]
//s c = "img[]\n%abc/iApp.apk|img[]\n%abc/iApp2.apk|img[]\n%abc/iApp3.apk"
s d = "utf-8"
huf(a, b, c, d, e)
syso(e)
}


2.设置 header文件头，文件头包括了Cookie，User-Agent设备型号。。
t()
{
s a = "http://abc.com/upfile.php"
s b = "filename=iApp我的应用.apk&test=一款非常好的应用哦"
s c = "%abc/iApp.apk"
// 支持多文件上传
//s c = "%abc/iApp.apk|%abc/iApp2.apk|%abc/iApp3.apk"
s d = "utf-8"
s e = "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||Cookie=aa=123;bb=456;||accept-language=zh-CN"
huf(a, b, c, d, e, e)
syso(e)
}



说明：
输入 http接口，表单内容，手机内存选择文件，接口的网页编码， 赋值变量。 返回网页内容将赋值给变量 “e”

【nvw 创建动态控件】
用法：
//将控件添加至指定的控件作为子控件
//输入要添加的控件ID或控件对象，输入添加至指定控件ID或控件对象
nvw(id, did)

//输入要添加的控件ID或控件对象，输入添加至指定控件ID或控件对象，输入插入指定序号
nvw(id, did, 0)

//创建文本控件
//输入控件ID，输入添加至指定控件ID或控件对象，输入控件类型，输入控件属性
s id = 123456
s did = 1
nvw(id, did, "文本", "width=-2\nheight=-2\ntext=内容")

//创建文本控件
//输入控件ID，输入添加至指定控件ID或控件对象（若不添加则输入null），输入控件类型，输入控件属性，赋值变量为创建控件的对象
s id = 123456
s did = 1
nvw(id, did, "文本", "width=-2\nheight=-2\ntext=内容", b)

说明：
输入创建的控件ID，输入将新控件添加至指定控件ID或控件对象，创建控件的类型，创建控件的属性，可带有赋值变量


【uall 获取子控件】
用法：
//输入控件ID或控件对象，输入false时将赋值子控件ID，输入赋值变量将返回一个数据列表
uall(1, false, a)

//输入控件ID或控件对象，输入true时将赋值子控件对象，输入赋值变量将返回一个数据列表
uall(1, true, a)

s b = 1
gslist(a, b，c)
tw(c)

说明：
获取一个包含子控件的，控件中所有的子控件。

【urvw 移除控件】
用法：
urvw(3)

说明：
输入需要移除的控件ID或控件对象


【sbp 图像分割】
用法：
//载入一个图像变量，并赋值到图像变量“b”
sbp("%1.png", b)

//载入一个用户图标，{裁剪图像区域（像素）：x坐标:80，y坐标:90，裁剪宽度:50，裁剪高度:60}
//并将裁剪好的赋值到图像变量“b”
sbp("%1.png", 80, 90, 50, 60, b)

//载入一个SD卡上的图标，{裁剪图像区域（像素）：x坐标:80，y坐标:90，裁剪宽度:50，裁剪高度:60}，图像旋转图像:180度
//并将裁剪好的赋值到图像变量“b”
sbp("%1.png", 80, 90, 50, 60, 180, b)

说明：
三种方式载入图像，从图像变量，从用户图标，从SD上图标；并可设置裁剪图片；可设置图像旋转； 并赋值到新的图像变量；

【bfs 保存图像】
用法：
bfs(b, "%1.jpg")

//或 压缩比例（1至100）
bfs(b, 70, "%1.jpg")

说明：
输入图像变量，输入压缩比例（1至100），输入保存图像的路径，图像将保存至该路径。

【sdeg 启动调试模式】
用法：
sdeg(0)
sdeg(1)
sdeg(2)

说明：
提示日志方式。0打包后没有任何提示，1打包后可任然打印错误，2打包后记录日志保存至文件 iApp/Log


【tot 获取控件图标】
用法：
s id = 4
tot(id, b)

说明：
输入控件ID或控件对象，返回将赋值“b”图像变量。注：此方法仅限于 图片控件，图标按钮控件。


【tzz 图像旋转】
用法：
sbp("%1.png", a)
s b = 90
tzz(a, b, c)

说明：
输入被旋转图像变量，输入旋转度数（逆向旋转数为负数），返回将赋值“c”图像变量。


【tsf 图像缩放】
用法：
sbp("%1.png", a)

//按照倍增缩放，值小于则为缩小，否则为放大
s b = 2
tsf(a, b, c)

//指定高度与宽度缩放
s w = 100
s h = 200
tsf(a, w, h, c)

说明：
输入被缩放图像变量，输入缩放倍数 或 指定图像高度与宽度缩放，返回将赋值“c”图像变量。


【tfz 图像反转】
用法：
sbp("%1.png", a)
//水平反转
s b = "x"
tfz(a, b, c)

//垂直反转
s b = "y"
tfz(a, b, c)

说明：
输入被反转图像变量，输入反转方式 x为水平 y为垂直，返回将赋值“c”图像变量。

【tcc 获取图像变量尺寸】
用法：
sbp("%1.png", a)
s b = "w"
tcc(a, b, c)
tw(c)

s b = "h"
tcc(a, b, c)
tw(c)

说明：
获取图像变量的 w宽度 和 h高度。

【sxb 写入剪切板】
用法：
s a = "nihao"
sxb(a)

说明：
可用于复制到剪切板，其他应用可获取到此数据。

【shb 获取剪切板】
用法：
shb(a)
tw(a)

说明：
可获取剪切板数据，得到其他地方写入的剪切板数据。

【usjxm 手机休眠】
用法：
usjxm(false)

说明：
设置后手机将不休眠，不锁屏。默认为 true 需要休眠。

【bfvs 播放视频】
用法：

//设置SD卡视频文件
bfvs(1, "%a.mp4")

//设置网络远程视频文件
bfvs(1, "http://abc.com/a.mp4")

//增加控制器，c为赋值变量
bfvss(1, "media", c)
//开始播放
bfvss(1, "st")

说明：
自定义视频播放控件进行播放视频。

注意：
不支持加载assets文件。支持SD卡文件、（http）远程网络文件！

支持格式：
3gp、MP4、avi

【bfvss 播放视频控制】
用法：
//开始播放
bfvss(1, "st")

//暂停播放
bfvss(1, "pe")

//停止播放
bfvss(1, "sp")

//定位到指定帧
bfvss(1, "seekto", 300)

//增加控制器，c为赋值变量
bfvss(1, "media", c)

//是否在播放
bfvss(1, "ip", c)
tw(c)

//获取视频时长（毫秒）
bfvss(1, "dn", c)
tw(c)

//获取当前播放时长（毫秒）
bfvss(1, "cn", c)
tw(c)

【addv 加载界面】
用法：
//界面中载入其他界面
s id = 1
addv(id, "a.iyu")
addv(id, "b.iyu")

//侧滑窗体
s id = 1
addv(id, "a.iyu|b.iyu")

//滑动窗体，将带有赋值变量。此处变量“b”赋值为根控件列表，先通过 gslist 访问指定序号的根控件。通过 gvs 指定的根控件访问指定ID的控件。
s id = 1
addv(id, "a.iyu|b.iyu", b)



说明：
输入控件ID，输入界面名，输入辅助参数。可用将一个界面的控件，载入到指定控件作为子控件。

如何设置或获取属性上例 a.iyu 中的控件呢？
通过文件名作为对象，进行访问，如：

//注意：此对象的使用方式。
ug(a.2, "text", b)
us(a.3, "text", "你好")

注意：
如果载入事件中使用 addv 滑动窗体进行绑定， 如果还需要给滑动窗体内的界面中的控件设置数据，需要将设置控件的代码写在 载入完毕事件 中。否将将可能设置数据失败。

注意：
若增加 侧滑窗体 与 滑动窗体 的子控件，需要在被载入的界面设计中，自设一个根目录，作为界面唯一根目录。

【gvs 获取控件对象】
用法：
//根据当前界面，来获取控件
//输入要获取的控件ID，输入赋值变量
gvs(1, c)

// 输入0 则获取界面的根控件对象，是一个系统控件，如需获取自己的用户控件可通过 gvs(root, 1) 或 uall(root, true, list) 再次获取它的内部的子控件。
gvs(0, root)

//根据控件对象，来获取内部的子控件
//输入控件ID或控件对象，输入要获取的控件ID，输入赋值变量
gvs(1, 2, c)

// 输入0 则获取其父控件对象，这里获取了控件ID1的父对象
gvs(1, 0, p)

说明：
常用与于利用根控件获取内部的子控件 或 获取控件对象。获取失败将赋值返回 null

【aslist 添加数据列表】
用法：
s b = "你好"
aslist(a, b)
s c = "你好2"
aslist(a, c)

//可插入数据到指定序号
s c = "你好3"
s b = 1
aslist(a, c, b)

说明：
输入列表对象，输入要添加的数据，输入插入指定序号。

【sslist 数据列表设置数据】
用法：
s b = 1
s c = "数据"
sslist(a, b，c)


说明：
输入列表对象，输入指定数据序号，输入设置的数据

【gslist 数据列表访问数据】
用法：
s b = 1
gslist(a, b，c)
tw(c)

说明：
输入列表对象，输入指定数据序号，输入赋值变量

【gslistl 数据列表访问数据总数】
用法：
gslistl(a, b)
tw(b)

说明：
输入列表对象，输入赋值变量

【dslist 数据列表删除指定数据】
用法：
s b = 1
dslist(a, b)

//清空所有数据
s b = -1
dslist(a, b)

说明：
输入列表对象，输入指定数据序号

提示：
如果需要清空所有数据，[输入指定数据序号]可输入 -1 即会删除当前数据列表所有数据。

【gslistsz 列表数据转化为数组】
用法：
gslistsz(a, b)

说明：
输入列表对象，输入赋值变量

【gslistis 列表数据检查是否存在指定数据】
用法：
s b = "数据"
gslistis(a, b, c)

说明：
输入列表对象，被判断的数据，输入赋值变量。赋值数据：true 或 false

【gslistiof 列表数据从头开始检查是否包含该数据】
用法：
s b = "数据"
gslistiof(a, b, c)

说明：
输入列表对象，被判断的数据，输入赋值变量

【gslistlof 列表数据从尾开始检查是否包含该数据】
用法：
s b = "数据"
gslistlof(a, b, c)

说明：
输入列表对象，被判断的数据，输入赋值变量


【nuibs 背景选择器】
用法：
//使用颜色作为背景
s pressed = "#333333"
s selected = "#333333"
s normal = "#888888"
nuibs(pressed, selected, normal, b)


//使用图像作为背景
s pressed = "%a.png"
s selected = "%a.png"
s normal = "%b.png"
nuibs(pressed, selected, normal, b)


//使用渐变颜色作为背景
.配置选中状态背景
s a = 0
s b = 0
s c = "#255779|#3e7492|#a6c0cd"
s d = "0"
s e = "topbottom"
ngde(a, b, c, d, e, pressed)

.配置正常状态背景
s a = 0
s b = 0
s c = "#255779|#3e7492|#a6c0cd"
s d = "0"
s e = "rightleft"
ngde(a, b, c, d, e, normal)

s selected = pressed

nuibs(pressed, selected, normal, b)

说明：
输入按下背景，输入选中背景，正常状态背景，输入赋值变量。


【ngde 背景调控器】
用法：
//输入圆角半径，输入背景填充色，输入赋值变量
s a = 15
s b = "#888888"
ngde(a, b, c)

//输入边框宽度，输入背景填充色，输入边框颜色，输入赋值变量
s a = 5
s b = "#888888"
s c = "#333333"
ngde(a, b, c, d)

//输入边框宽度，输入圆角半径，输入背景填充色，输入边框颜色，输入赋值变量
s a = 5
s b = 15
s c = "#888888"
s d = "#333333"
ngde(a, b, c, d, e)

//颜色渐变。输入边框宽度，输入圆角半径，输入背景填充渐变色组，输入边框颜色，输入颜色渐变方向，输入赋值变量
s a = 5
s b = 15
s c = "#255779|#3e7492|#a6c0cd"
s d = "#333333"
s e = "topbottom"
ngde(a, b, c, d, e, f)

说明：
背景空调生成的赋值变量，可配合背景选择器进行应用。

注意：
ngde 代码将赋值返回一个背景对象，此背景对象如果被多个不同大小的控件引用为背景。因为控件的大小不同，会导致此背景对象大小被修改。从而影响其他引用者控件。

提示：
边框与圆角半径 若不想调整，可设值为0 。适用于颜色渐变，不需要调节圆角半径和边框。

颜色渐变方向说明：
	topbottom：绘制从顶部梯度至底部
	trbl：借鉴右上角渐变左下角
	rightleft：绘制从右侧的梯度向左
	brtl：借鉴右下角渐变左上角
	bottomtop：绘制从底部梯度顶端
	bltr：借鉴渐变左下角到右上角
	leftright：绘制从左侧的梯度向右
	TL_BR：从绘制渐变的左上角到右下角

【sit 目标的设置】
用法：
//如，分享软件
//输入对象，输入属性标识，输入属性值
sit(a, "action", "android.intent.action.SEND")
sit(a, "type", "text/plain")
sit(a, "extra", "android.intent.extra.SUBJECT", "共享软件")
sit(a, "extra", "android.intent.extra.TEXT", "共享内容文本")
sit(a, "flags", 268435456)
uit(a, "chooser", "标题")

说明：
常用于调用系统程序以及功能 或 第三方程序功能。

可属性标识：action、type、extra、flags、data、classname、component

【uit 目标的执行】
用法：
//输入目标对象，输入属性，输入属性值
uit(a, "chooser", "标题")

//输入目标对象，输入属性，输入请求数值
uit(a, "result", 1)

//输入目标对象
uit(a, "start")

说明：
常用于调用系统程序以及功能 或 第三方程序功能。

属性支持：chooser、result

【git 目标获取参数】
用法：
//输入目标对象，输入属性标识，输入赋值变量
git(a, "action", c)
git(a, "type", c)
git(a, "extra", "title", c)
git(a, "flags", c)

说明：
获取目标的属性。

【uqr 二维码扫描】
用法：

//扫描二维码
uqr()

//扫描结果，需要在 回调结果事件 写代码
f(st_sC == 1102)
{
git(st_iT, "extra", "result", c)
tw(c)
}


//生成二维码图像
s a = "https://m.baidu.com"
//输入字符串数据，输入图像长宽像素，输入赋值变量；将返回一个图像变量
uqr(a, 400, c)


//识别二维码图像
//输入图像变量或图片路径，输入赋值变量；将返回一个字符串
uqr(a, c)

说明：
常用于网络通用二维码扫描。

【zdp  dip转换px】
用法：
s dp = 10
//输入dp数值，输入赋值变量
zdp(dp, c)

说明：
用于常用数据转换。

【zpd  px转换dip】
用法：
s px = 10
//输入px数值，输入赋值变量
zpd(px, c)

说明：
用于常用数据转换。

【zps  px转换sp】
用法：
s px = 10
//输入px数值，输入赋值变量
zps(px, c)

说明：
用于常用数据转换。

【zsp  sp转换px】
用法：
s sp = 10
//输入sp数值，输入赋值变量
zsp(sp, c)

说明：
用于常用数据转换。

【lan 跳转界面动画】
用法：
uigo("abc.iyu")
//输入跳转界面动画的序号；6 右往左推出效果
lan(6)

说明：
用于跳转界面时候进行的动画效果

提示：
0.淡入淡出效果 1.放大淡出效果 2.转动淡出效果1 3.转动淡出效果2 4.左上角展开淡出效果 5.压缩变小淡出效果 6.右往左推出效果 7.下往上推出效果 8.左右交错效果 9.放大淡出效果 10.缩小效果 11.上下交错效果

【sjxx 获取设备信息】
用法：
sjxx(a)
sgsz(a, 0, d)
tw(d)

说明：
获取手机基本信息，将返回一个数组到赋值变量“a”，数组格式如下：

数据格式：（真实数据 \n 旁边将不没有空格）

CPU型号 \n CPU频率
屏幕宽度 \n 屏幕高度 \n 分辨率宽度 \n 分辨率高度
手机型号 \n 手机品牌 \n 手机SDK

【simsi 获取设备imsi】
用法：
simsi(a)
tw(a)

说明：
常用于识别用户的手段。

【simei 获取设备imei】
用法：
simei(a)
tw(a)

说明：
常用于识别用户的手段。

【endkeyboard 强制隐藏虚拟键盘】
用法：
endkeyboard()

说明：
常用于需要隐藏安卓弹出的虚拟键盘。

【hdfl 文件下载器】
用法：
//两个参数的方法设置
s savedir = "%SaveDir"
//输入下载保存目录，输入赋值变量返回一个下载器对象
hdfl(savedir, a)
{
//每当下载完一个执行
//系统赋值 st_drD 文件下载项的序号
//系统赋值 st_drI 文件下载项的状态

//获取下载的URL
ulag(a, st_drD, "url", b1)
syso(b1)

//获取自定义整数标识
ulag(a, st_drD, "type", b2)
syso(b2)

//获取自定义参数任意数据
ulag(a, st_drD, "text", b3)
syso(b3)

//获取下载文件保存的路径
ulag(a, st_drD, "filename", b4)
syso(b4)

}
else
{
//当下载完目前所有执行
//系统赋值 st_drJ 本次文件下载完成总数
ufnsui()
{
tw(st_drJ)
}
}

//三个参数的方法设置
s tempdir = "%TempDir"
s savedir = "%SaveDir"
//输入下载临时文件保存目录，输入下载保存目录，输入赋值变量返回一个下载器对象
hdfl(tempdir, savedir, a)
{
ufnsui()
{
tw(st_drD)
}
}
else
{
ufnsui()
{
tw(st_drJ)
}
}

//六个参数的方法设置
s tempdir = "%TempDir"
s savedir = "%SaveDir"
//输入下载临时文件保存目录，输入下载保存目录, 下载线程数量，连接网络超时时间（25秒的意思），文件重复是否覆盖，输入赋值变量返回一个下载器对象
hdfl(tempdir, savedir, 3, 25000, true, a)
{
ufnsui()
{
tw(st_drD)
}
}
else
{
ufnsui()
{
tw(st_drJ)
}
}

说明：
常用与单个或多个的文件下载。推荐图片列表下载或小文件下载。

提示：
代码{ 区域中 }属于线程内执行。在其中更新界面控件属性需要使用ufnsui代码
上例子使用tw代码，并且用了ufnsui代码。

【hdfla 文件下载器 增加文件下载项】
用法：
//创建一个文件下载器
hdfl(tempdir, a)
{
ufnsui()
{
tw(st_drD)
}
}
else
{
ufnsui()
{
tw(st_drJ)
}
}

//增加下载项
//输入下载器对象，输入下载连接URL，输入自定义整数标识，输入自定义参数任意数据
hdfla(a, "http://abc.com/1.jpg", 1, "abcd123")


//增加下载项，并且自定义保存目录
//输入下载器对象，输入下载连接URL，输入自定义整数标识，输入自定义参数任意数据，输入自定义保存路径
hdfla(a, "http://abc.com/2.jpg", 1, "abcd123", "%abc.jpg")

说明：
调用下载器增加下载项，并且立刻进行下载。

【hdd 配置下载管理器】
用法：
//下载产生的临时文件目录
s a = "%tempdir"
//下载至保存的目录
s b = "%filedir"
//允许同时下载任务数量
s c = 3
//每个任务开启线程数量
s d = 3
//连接失败重试次数
s e = 2
//连接超时时间，25秒的意思
s f = 25000
//是否显示下载进度通知
s g = true
hdd(a, b, c, d, e, f, g)

说明：
如果不使用此代码进行配置，那么系统将使用默认配置。下载配置器可以很方便的制作下载文件，并且方便管理。

默认目录属性：
临时文件目录：iApp/DownloadFileDir/TempDefaultDownFile
保存文件目录：iApp/DownloadFileDir/DefaultDownFile

【hdda 下载管理器 增加文件下载项】
用法：

//===========方法一
//下载的链接
s url = "http://abc.com/abc.apk"

//保存的文件名（仅输入文件名,请勿不包含目录）
s name = "abc.apk"

//输入自定义参数任意数据
s data = "abcde123"

//变量v为赋值变量，为下载对象
hdda(url, name, data, v)

//===========方法二
//下载的链接
s url = "http://abc.com/abc.apk"

//保存的文件名（仅输入文件名,请勿不包含目录）
s name = "abc.apk"

//下载任务的标题
s title = "abc.apk最新版"

//输入自定义参数任意数据
s data = "abcde123"

//变量v为赋值变量，为下载对象
hdda(url, name, title, data, v)

//===========方法三
//下载的链接
s url = "http://abc.com/abc.apk"

//保存的文件名（仅输入文件名,请勿不包含目录）
s name = "abc.apk"

//下载任务的标题
s title = "abc.apk最新版"

//下载任务的图标
s icon = "@abc.png"

//输入自定义参数任意数据
s data = "abcde123"

//变量v为赋值变量，为下载对象
hdda(url, name, title, icon, data, v)

//===========方法四
//下载的链接
s url = "http://abc.com/abc.apk"

//保存至目录
s dir = "%filedir"

//保存的文件名（仅输入文件名,请勿不包含目录）
s name = "abc.apk"

//下载任务的标题
s title = "abc.apk最新版"

//下载任务的图标
s icon = "@abc.png"

//是否显示下载进度通知
s notsohw = true

//输入自定义参数任意数据
s data = "abcde123"

//变量v为赋值变量，为下载对象
hdda(url, dir, name, title, icon, notsohw, data, v)

说明：
增加常用的网络文件进行下载。

【hddgl 获取下载管理器下载列表】
用法：
//输入赋值变量返回下载列表
hddgl(list)

//使用for循环下载列表
for(b; list)
{
hddg(b, "url", c)
syso(c)
}

说明：
获取下载管理器所有的下载列表。

【hddg 获取下载管理器获取下载项属性】
用法：
//下载的链接
s url = "http://abc.com/abc.apk"
//保存的文件名（仅输入文件名,请勿不包含目录）
s name = "abc.apk"
//输入自定义参数任意数据
s data = "abcde123"
//变量v为赋值变量，为下载对象
hdda(url, name, data, v)

//===========获取下载项的属性
//获取下载项的 ID
hddg(v, "id", b)

//获取下载项的 下载链接
hddg(v, "url", b)

//获取下载项的 保存的绝对路径
hddg(v, "dirfilename", b)

//获取下载项的 下载链接的md5
hddg(v, "urlmd5", b)

//获取下载项的 保存的目录
hddg(v, "dir", b)

//获取下载项的 保存的文件名
hddg(v, "filename", b)

//获取下载项的 下载文件的大小（字节）
hddg(v, "contentlength", b)

//获取下载项的 已下载的数据（字节）
hddg(v, "equivalent", b)

//获取下载项的 当前下载速度（字节）
hddg(v, "downloadspeed", b)

//获取下载项的 当前下载进度百分比
hddg(v, "downloadpercentage", b)

//获取下载项的 下载状态；（0为等待下载；1为正在下载；2为下载完成；3下载已经暂停或停止；-1下载失败；-2已删除）
hddg(v, "status", b)

//获取下载项的 是否显示下载通知
hddg(v, "notificationshow", b)

//获取下载项的 自定义的数据
hddg(v, "text", b)

//获取下载项的 通知标题
hddg(v, "title", b)

//获取下载项的 通知图标
hddg(v, "icon", b)

说明：
可获取详细的下载项目状态属性。

【hdds 设置下载管理器下载项的属性】
用法：
//下载的链接
s url = "http://abc.com/abc.apk"
//保存的文件名（仅输入文件名,请勿不包含目录）
s name = "abc.apk"
//输入自定义参数任意数据
s data = "abcde123"
//变量v为赋值变量，为下载对象
hdda(url, name, data, v)

//===========可设置的下载项属性

//设置下载项的 下载状态；（0为等待下载；1为正在下载；2为下载完成；3下载已经暂停或停止；-1下载失败；-2已删除）
hdds(v, "status", 0)

//设置下载项的 是否显示下载通知
hdds(v, "notificationshow", true)

//设置下载项的 自定义的数据
hdds(v, "text", "abcd123")

//设置下载项的 通知标题
hdds(v, "title", "abc.apk最新版本")

//设置下载项的 通知图标
hdds(v, "icon", "@abc.png")

说明：
设置下载项目的属性。

【hdduigo 跳转至下载管理器】
用法：
//跳转至下载管理器
hdduigo()

//跳转至下载管理器，并且自定义标题栏颜色
//主体颜色
s a = "#387bd6"
//底部横杠颜色
s b = "#255eab"
hdduigo(a, b)

说明：
跳转至文件下载的管理器。

【ufnsui 线程更新界面】
用法：
ufnsui()
{
tw(a)
us(1, "text", "内容")
}

说明：
线程中直接修改界面或修改设置控件属性，出错。
需要使用ufnsui模块进行更新或设置控件属性。

提示：
线程中获取控件数据不会出错。

【se 正则表达式操作】
用法：
//===========例子1；所有属性展示
//字符串
s a = "qqqq123456eee"
//正则表达式
s b = "([a-z]+)(\d+)"
//更多参数
s c = 0
se(a, b, c, d)

//替换成，将替换全部
se(d, "sral", "1:$1, 2:$2", e)
syso(e)
//替换成，只替换第一个
se(d, "srft", "1:$1, 2:$2", e)
syso(e)

//返回是否匹配成功，需字符串被完全匹配，赋值返回true或 false
.se(d, "ms", e)

//开始匹配 或 匹配下一个，赋值返回true或 false
.se(d, "find", e)

//给定位置序号进行匹配，赋值返回true或 false
.se(d, "find", 1, e)

//获取匹配组的数量，当前为2组：([a-z]+)、(\d+)
.se(d, "gl", e)

//获取第1组匹配到的子字符串在字符串中的开头位置 
.se(d, "start", 1, e)

//获取第1组匹配到的子字符串在字符串中的结尾位置 
.se(d, "end", 1, e)

//获取第1组匹配到的子字符串
.se(d, "group", 1, e)
//获取第2组匹配到的子字符串
.se(d, "group", 2, e)


//===========例子2；获取所有手机号

//字符串
s a = "我的号码 13612345678 , 你的号码 13412345678"
//正则表达式
s b = "[1][3-8]\d{9}"
//更多参数
s c = 0
se(a, b, c, d)

//开始匹配 或 匹配下一个
se(d, "find", e)

//循环判断是否匹配成功
w(e == true)
{
//因为 [1][3-8]\d{9} 没有组，所以这里我们输入 0
se(d, "group", 0, e)

//打印出匹配到的子字符串
syso(e)

//开始匹配 或 匹配下一个
se(d, "find", e)
}

//===========例子3；判断是否为手机号

//字符串
s a = "13612345678"
//正则表达式
s b = "^[1][3-8]\d{9}$"
//更多参数
s c = 0
se(a, b, c, d)

se(d, "ms", e)
f(e == true)
{
syso("手机号格式正确")
}
else
{
syso("手机号格式错误")
}


说明：
常用与字符串处理，高效的处理字符串，以及检测字符串类型等。使用此方法，需要对正则表达式有部分知识。

【usg 闪光灯操作】
用法：
//开启闪光灯
//输入闪光灯变量对象，输入是否开启闪光灯
usg(sss.sgd, true)

//关闭闪光灯
//输入闪光灯变量对象，输入是否开启闪光灯
usg(sss.sgd, false)

说明：
开启或关闭 设备闪光灯！

说明：
常用照明。

注意：
此方法调用将无法与摄像头同时调用。如启动摄像头需要使用闪光灯，可在摄像头操作中开启闪光灯。

【uzd 震动器操作】
用法：
//震动1秒时长
//输入振动器变量对象，输入震动时长
uzd(sss.zdq, 1000)

//静止1秒，震动1秒，静止1秒，震动1秒，静止1秒，震动1秒，静止1秒，..， 并且不重复
//输入振动器变量对象，输入震动规则，输入是否重复循环执行
uzd(sss.zdq, "1000 1000 1000 1000 1000 1000 1000 1000", false)

//强制停止震动器
uzd(sss.zdq, "sp")

//检查硬件是否具有振动器
uzd(sss.zdq, "ip", b)
syso(b)

说明：
常用提示用户。

【usxq 开启前置摄像头】
用法：
//开启摄像头
//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度
usxq(sss.ps, 1, 90)

//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度，输入拍摄宽度像素，输入拍摄高度像素，输入图像品质1-100
usxq(sss.ps, 1, 90, 640, 480, 95)

//自动对焦拍摄
//输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
usx(sss.ps, "shot", "%abc.jpg", -90, false)

说明：
指定打开前置摄像头。

注意：
此功能需要与一个面控件进行绑定，你可以在面控件上面设置拍摄事件。

注意：
此代码仅限于载入事件调用。

【usxh 开启后置摄像头】
用法：
//开启摄像头
//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度
usxh(sss.ps, 1, 90)

//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度，输入拍摄宽度像素，输入拍摄高度像素，输入图像品质1-100
usxh(sss.ps, 1, 90, 1280, 960, 95)

//自动对焦拍摄
//输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
usx(sss.ps, "shot", "%abc.jpg", 90, false)

说明：
指定打开后置摄像头。

注意：
此功能需要与一个面控件进行绑定，你可以在面控件上面设置拍摄事件。

注意：
此代码仅限于载入事件调用。

【usx 摄像头操作】
用法：
//开启摄像头
usxh(sss.ps, 1, 90)

//自动对焦拍摄
//输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
usx(sss.ps, "shot", "%abc.jpg", 90, false)

//开始预览
usx(sss.ps, "st")

//停止预览
usx(sss.ps, "sp")

//旋转摄像头角度
usx(sss.ps, "rotaing", 180)
//获取旋转摄像头角度
usx(sss.ps, "getrotaing", b)
syso(b)

//启动摄像头闪光灯
usx(sss.ps, "usg", true)

//结束摄像头组件变量对象
usx(sss.ps, "re")

说明：
摄像头的控制。

【bly 录制音频】
用法：
//开始录制
//输入录音变量对象，输入保存文件路径
bly(sss.ly, "%abcd.amr")

//停止录音
bly(sss.ly, "sp")

说明：
常用于录制音频。

说明：
可使用 bfm 代码来播放录制好的音频。

【ujp 截取屏幕】
用法：
//输入保存路径，输入图像品质（1-100）
ujp("%123.jpg", 70)

说明：
常用于截取当前界面。

【sqlite 数据库操作】
用法：
//连接一个私有数据库，如果不存在将自动新建
//输入数据库对象变量，输入数据库文件名
sqlite(sss.data, "iapp.db")

//连接一个公共数据库，如果不存在将自动新建
//输入数据库对象变量，输入数据库文件名
sqlite(sss.data, "%iapp.db")

//判断数据库是否存在
sqlite("iapp.db", "ip", b)
syso(b)

//删除数据库
sqlite("iapp.db", "del", b)
syso(b)

//释放数据库
sqlite(sss.data, "re")

说明：
进行数据库的操作。

【sql 数据表操作】
用法：

//创建数据表
s table = "_id integer primary key,url text, filename text,status interger,createTime datetime"
sql(sss.data, "info", "add", table, b)

//判断数据表是否存在
sql(sss.data, "info", "ip", b)
syso(b)

//删除数据表
sql(sss.data, "info", "del", b)
syso(b)

//添加数据表一条数据
s table = "url,filename,status,createTime"
time(0, sj)
ss("'http://abc.com/abc.apk', 'abc.apk', 1, '" + sj + "'", data)
sql(sss.data, "info", "add", table, data, b)
syso(b)

//修改数据表的数据，若不需要设置条件(_id=1)可设为 null 视为适用于执行所以数据
sql(sss.data, "info", "up", "status=2", "_id=1", b)
syso(b)

//删除数据表的数据，若不需要设置条件(_id=1)可设为 null 视为适用于执行所以数据
sql(sss.data, "info", "del", "_id=1", b)
syso(b)


//查询，若不需要设置条件(status=1 order by _id desc LIMIT 0,1)可设为 null 视为适用于执行所以数据

// LIMIT <跳过的数据数目>, <取数据数目>
s table = "_id,url,filename,status,createTime"
s sqlx = "status=1 order by _id desc LIMIT 0,1"
sql(sss.data, "info", "sele", table, sqlx, data)

//自定义sql查询
//s sqlx = "select _id,url,filename,status,createTime from info where status=1 order by _id desc LIMIT 0,1"
//sql(sss.data, sqlx, data)

//光标对象移到下一条数据
sqlsele(data, "next", e)
w(e == true)
{
//获取光标对象的第一列数据
sqlsele(data, 0, e)
syso(e)

//获取光标对象的第二列数据
sqlsele(data, 1, e)
syso(e)

//光标对象移到下一条数据
sqlsele(data, "next", e)
}


//自定义的sql执行，需要对sql语法了解才能灵活运用
s sqlx = "insert into info (url,filename,status,createTime) values ('http://abc.com/abc.apk', 'abc.apk', 1, '2016-7-31 10:31:21')"
sql(sss.data, sqlx)

说明：
数据表的操作。

注意：
在执行sql语句的时候，需要注意你的字符串的特殊字符的转义。
     /   ->    //
     '   ->    ''
     [   ->    /[
     ]   ->    /]
     %   ->    /%
     &   ->    /&
     _   ->    /_
     (   ->    /(
     )   ->    /)

【sqlsele 查询数据操作】
用法：

//获取光标对象的第一列数据
sqlsele(data, 0, e)

//获取光标对象有多少列
sqlsele(data, "columncount", e)
syso(e)

//获取总共查询到多少条数据
sqlsele(data, "count", e)
syso(e)

//光标对象移到下一条数据
sqlsele(data, "next", e)

//光标对象移到上一条数据
sqlsele(data, "previous", e)

//光标对象移到第一条数据
sqlsele(data, "first", e)

//光标对象移到最后第一条数据
sqlsele(data, "last", e)

//光标对象移到指定第2条数据
sqlsele(data, "position", 2)

//获取光标对象当前位置
sqlsele(data, "getposition", e)
syso(e)

//释放数据查询
sqlite(data, "re")

说明：
数据查询的操作。

【dha 渐变透明度动画】
用法：
//创建一个渐变透明度动画，开始显示，然后渐变消失
//输入动画开始是否透明，输入动画结束是否透明
dha(dh, true, false)
dh(dh, "duration", 2000)
us(2, "dh", dh)

说明：
常用于控件透明度动画。

【dhs 渐变尺寸伸缩动画】
用法：
//创建一个渐变尺寸伸缩动画
//0为没有，2.5为原始2.5倍

//输入X开始尺寸比例，输入X结束尺寸比例，输入Y开始尺寸比例，输入Y结束尺寸比例
dhs(dh, 0.5, 2.5, 0.5, 2.5)
dh(dh, "duration", 2000)
us(2, "dh", dh)

//输入X开始尺寸比例，输入X结束尺寸比例，输入Y开始尺寸比例，输入Y结束尺寸比例，输入X位置类型，输入X坐标的开始位置，输入Y位置类型，输入Y坐标的开始位置
dhs(dh, 0.5, 2.5, 0.5, 2.5, 1, 0.5, 1, 0.5)
dh(dh, "duration", 2000)
us(2, "dh", dh)

说明：
常用于控件伸缩动画。

位置类型：
0 默认
1 以对象本身为基准位置类型
2 以父控件为基准位置类型

【dht 画面位置移动动画】
用法：
//创建一个画面位置移动动画
//输入开始X坐标上的移动位置，结束X坐标上的移动位置，开始Y坐标上的移动位置，结束Y坐标上的移动位置
dht(dh, 30, 80, 30, 80)
dh(dh, "duration", 2000)
us(2, "dh", dh)

说明：
常用于控件移动动画。

【dhr 画面旋转动画】
用法：
//创建一个画面旋转动画
//输入动画开始的旋转角度，输入动画旋转到的角度
dhr(dh, 0, 180)
dh(dh, "duration", 2000)
us(2, "dh", dh)

//输入动画开始的旋转角度，输入动画旋转到的角度，输入X位置类型，输入X坐标的开始位置，输入Y位置类型，输入Y坐标的开始位置
dhr(dh, 0, 180, 1, 0.5, 1, 0.5)
dh(dh, "duration", 2000)
us(2, "dh", dh)

说明：
常用于控件旋转动画。

位置类型：
0 默认
1 以对象本身为基准位置类型
2 以父控件为基准位置类型

【dhset 动画集合】
用法：

//渐变尺寸伸缩动画
dhs(dh1, 0.5, 2.5, 0.5, 2.5)
dh(dh1, "duration", 2000)

//画面位置移动动画
dht(dh2, 30, 80, 30, 80)
dh(dh2, "duration", 2000)

//画面旋转动画
dhr(dh3, 0, 180)
dh(dh3, "duration", 2000)

//创建一个动画集合
//输入动画集合变量对象，输入是否使用动画集合的interpolator，输入动画...（可输入N个参数）
dhset(dhlist, false, dh1, dh2, dh3, dh4)
us(2, "dh", dhlist)
	
说明：
常用于动画集合执行。

提示：
动画集合允许被其他动画集合添加成为子动画。

提示：
动画集合如果设置了动画控制属性，同时也会重置所有子控件的属性。

【dhas 队列动画执行】
用法：
//旋转动画
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入旋转角度...（可输入N个参数）
dhas(dh, 2, "rotation", 60, 180)
//dhas(dh, 2, "rotationX", 30, 80, 60, 20, 60)
//dhas(dh, 2, "rotationY", 30, 80)
dh(dh, "duration", 2000)
dh(dh, "start")

//伸缩动画
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入伸缩尺寸比例...（可输入N个参数）
dhas(dh, 2, "scaleX", 1.5, 2.5)
//dhas(dh, 2, "scaleY", 1.5, 2.5, 1.2, 2.6, 1.3)
dh(dh, "duration", 2000)
dh(dh, "start")

//移动动画
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入移动到位置...（可输入N个参数）
dhas(dh, 2, "translationX", 0, 60)
//dhas(dh, 2, "translationY", 0, 60, 30, 10, 60)
dh(dh, "duration", 2000)
dh(dh, "start")

//透明度
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，可见度比例(0.0至1.0)...（可输入N个参数）
dhas(dh, 2, "alpha", 1, 0.3, 1, 0.2, 1)
dh(dh, "duration", 2000)
dh(dh, "start")

说明：
自定义队列动画执行。


【dhast 队列动画集合】
用法：

//旋转动画
dhas(dh1, 2, "rotation", 60, 180)
dh(dh1, "duration", 2000)

//伸缩动画
dhas(dh2, 2, "scaleX", 1.5, 2.5)
dh(dh2, "duration", 2000)

//移动动画
dhas(dh3, 2, "translationX", 0, 60)
dh(dh3, "duration", 2000)

//透明度
dhas(dh4, 2, "alpha", 1, 0.3, 1, 0.2, 1)
dh(dh4, "duration", 2000)

//顺序执行
dhast(dhlist, "sequen", dh1, dh2, dh3, dh4)

//同时执行
//dhast(dhlist, "together", dh1, dh2, dh3, dh4)
dh(dhlist, "start")

说明：
常用于动画集合执行。

提示：
队列动画集合允许被其他队列动画集合添加成为子动画。

提示：
动画集合如果设置了动画控制属性，同时也会重置所有子控件的属性。


【dh 动画控制】
用法：

//========动画的属性（非队列动画）设置========================

//取消动画，取消后若需要重新播放，需要先执行 reset 然后再执行 start 进行播放
dh(dh, "cancel")

//重置动画属性
dh(dh, "reset")

//启动动画
dh(dh, "start")

//动画持续时长
dh(dh, "duration", 2000)

//延迟执行，延迟指定时长后才执行动画
dh(dh, "delay", 2000)

//启动动画结束填充效果（如果设false 那么 after 与 before将无效）
dh(dh, "enabled", true)

//动画执行后，控件停留执行结束状态
dh(dh, "after", true)

//动画执行后，控件停留执行开始状态
dh(dh, "before", true)

//动画重复执行的次数
dh(dh, "repeat", 20)

dhas(dh2, 2, "rotation", 60, 180)
//动画集合添加动画，仅用于 dhset 动画集合追加更多的动画
dh(dh, "add", dh2)

//========队列动画的属性设置========================

//取消动画
dh(dh, "cancel")

//播放动画
dh(dh, "start")

//动画持续时长
dh(dh, "duration", 2000)

//延迟执行，延迟指定时长后才执行动画
dh(dh, "delay", 2000)

//动画是否正在运行
dh(dh, "running", b)
syso(b)

//设置动画执行的控件ID或控件对象
dh(dh, "target", 2)

//克隆动画
dh(dh, "clone", dh2)

说明：
常用于动画的控制管理。

【dhon 动画监听事件】
用法：
//========动画（非队列动画）设置监听事件========================
dhon(dh)
{
//当结束动画时
syso("End")
}

//或

dhon(dh)
{
//当结束动画时
syso("End")
}
else
{
//当重复动画时
syso("Repeat")
}
else
{
//当启动动画时
syso("Start")
}

//========队列动画设置监听事件========================

dhon(dh)
{
//当结束动画时
syso("End")
}

//或

dhon(dh)
{
//当结束动画时
syso("End")
}
else
{
//当重复动画时
syso("Repeat")
}
else
{
//当启动动画时
syso("Start")
}
else
{
//当取消动画时
syso("Cancel")
}

说明：
常用于动画状态的监听。

提示：
该事件使用的选择性，可顺序选择性保留。

【dhb 动画背景】
用法：
//创建动画背景
//输入动画背景变量对象，输入是否重复执行
dhb(dh, true)

//添加元素
//输入动画背景变量对象，输入背景图像或图片变量或背景对象，输入显示时长
dhb(dh, "@t1.png", 1000)
dhb(dh, "@t2.png", 1000)
dhb(dh, "@t3.png", 1000)

//设为指定控件背景
us(2, "background", dh)

//启动动画
dhb(dh, "start")

//停止动画
//dhb(dh, "stop")

//是否在运行
dhb(dh, "running", b)
syso(b)

说明：
常用于组合一个背景动画。

【hsas 开启浏览器控件交互(裕语言+js+html5)】
用法：
//开启浏览器控件支持iapp交互
//输入浏览器控件ID或对象，输入是否开启
hsas(1, true)

//hsas(1, false)

说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

【has 裕语言交互JavaScript语言】
用法：
//首先将 web.html 放入用户文件中

//设置浏览器控件显示的html内容
s a = "@web.html"
s b = "utf-8"
fr(a, b, c)

s d = "utf-8"
s e = "text/html"
us(1, "url", c, d, e, f)

//因为浏览器加载内容属于异步操作，如果立刻执行下面的代码会执行失败
//所以将下面的代码放入某项单击事件中

s a = "go('呀！')"
//输入浏览器控件ID或对象，输入JavaScript的方法
has(1, a)

//带返回值解决方案
//s a = "go2('呀！')"
//输入浏览器控件ID或对象，输入JavaScript的方法
//has(1, a)
//tw(sss.sb)

说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

注意：
在载入事件设置浏览器控件的加载html内容，它不会立刻加载完成。所以如果将 裕语言交互js的代码也写在载入事件，会导致交互调用失败。必须等待浏览器加载完毕html内容后，才能交互。

注意：
建议尽量使用JavaScript调用交互裕语言，效率较高。裕语言调用执行JavaScript的方法效率要慢数倍。

注意：
has 不应该放在新线程中，测试发现5.1系统has放入新线程中报错。

注意：
本例子需要注意编码，否则将乱码。

html（web.html）文件（utf-8编码）例子：
<html>
<head>
<script type="text/javascript">
function go(o)
{
document.getElementById("sb").innerHTML += "打我" + o;
}
function go2(o)
{
document.getElementById("sb").innerHTML += "打我" + o;
iapp.s("sss.sb", document.getElementById("sb").innerHTML);
}
</script>
</head>
<p id="sb">哈哈，你来</p>
</html>


【JavaScript交互裕语言】
用法：
//首先将 web.html 放入用户文件中

//设置浏览器控件显示的html内容
s a = "@web.html"
s b = "utf-8"
fr(a, b, c)

s d = "utf-8"
s e = "text/html"
us(1, "url", c, d, e, f)

//此方法，主要是在JavaScript中写交互代码哦
//JavaScript中交互方法列表（用于交互裕语言）：

/.

//调用裕语言模块方法，不带返回变量的
iapp.fn('a.b("' + o + '")');

//调用裕语言模块方法，带返回变量的
var value = iapp.fn2('a.c("' + o + '")', b);

//设置裕语言变量数据
iapp.s(o);

//获取裕语言变量数据
var value = iapp.g(o);
./
说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

注意：
建议尽量使用JavaScript调用交互裕语言，效率较高。裕语言调用执行JavaScript的方法效率要慢数倍。

注意：
本例子需要注意编码，否则将乱码。


html（web.html）文件（utf-8编码）例子：
<html>
<head>
<script type="text/javascript">

//不带返回变量的
function go(o)
{
//调用的是 模块a.myu 中的 b方法
iapp.fn('a.b("' + o + '")');
}

//带返回变量的
//执行模块后，获取一个变量并返回到javascript方法里
function go2(o, b)
{
//调用的是 模块a.myu 中的 c方法
var value = iapp.fn2('a.c("' + o + '")', b);
alert('变量 sss.abc：' + value);
}

//设置全局变量数据
//同理，下面也有设置界面变量、设置局部变量的例子
function ss(o, b)
{
iapp.s(o, b);
}

//获取全局变量数据
//同理，下面也有获取界面变量、获取局部变量的例子
function gs(o)
{
var value = iapp.g(o);
alert('变量 sss.abc：' + value);
}

</script>
</head>
<p><a href="javascript:void(0)" onclick="go('呵呵')">调用裕语言的模块方法</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="go2('呵呵', 'sss.abc')">调用裕语言的模块方法，并返回sss.abc变量内容</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="ss('sss.abc', '呵呵')">设置裕语言的sss.abc全局变量数据</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="gs('sss.abc')">获取裕语言的sss.abc全局变量数据</a></p>
</html>

模块（a.myu）例子：
fn b(a)
//打印出数据
syso(a)
end fn

fn c(a)
//打印出数据
syso(a)
sss abc = "666呵呵"
end fn

【uxf 显示悬浮窗】
用法：

//输入界面名，输入宽度，输入高度，输入对其方式，输入赋值变量
s w = -1
s h = -1
s gravity = "top|right"
uxf("a.iyu", w, h, gravity, sss.v)


//输入界面名，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入类型的窗口，输入对其方式，输入flags，输入format，输入赋值变量
s x = 0
s y = 0
s w = -1
s h = -1
s type = 0
s gravity = "top|right"
s flags = 0
s format = 0
uxf("a.iyu", x, y, w, h, type, gravity, flags, format, sss.v)


//刷新悬浮窗口的布局，常用于通过us设置后的刷新
//输入界面根控件的控件对象
uxf(sss.v)


//移除悬浮窗口
//输入界面根控件的控件对象，输入标识
uxf(sss.v, "del")


//重置悬浮窗的属性
//输入界面根控件的控件对象，输入标识，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入对其方式
s x = 0
s y = 0
s w = -2
s h = -2
s gravity = "top|right"
uxf(sss.v, "set", x, y, w, h, gravity)

//重置悬浮窗的属性
//输入界面根控件的控件对象，输入标识，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入对其方式
s x = 0
s y = 0
s w = -1
s h = -1
s type = 0
s gravity = "top|right"
s flags = 0
s format = 0
uxf(sss.v, "set", x, y, w, h, type, gravity, flags, format)

说明：
常用于显示悬浮窗窗口。

提示：
可通过 gvs(sss.v, a.1, b) 代码进行获取悬浮窗内的子控件，然后对其进行操作。

提示：
可通过下例代码，控制窗口位置的移动
//更新窗口位置
us(sss.v, "x", 100)
us(sss.v, "y", 100)

//获取窗口位置
ug(sss.v, "x", xx)
ug(sss.v, "y", yy)

//通过us 更新后， 需要刷新悬浮窗口的布局
uxf(sss.v)


对齐方式：
center：居中
top：顶
bottom：底
left：左
right：右
center_vertical：垂直居中
center_horizontal：水平居中

输入flags：
0 不许获得焦点（编辑框输入法将无法弹出）
1 可以获得焦点，返回键将不可用


【tts 文本转换语音】
用法：
//创建一个TTS对象
//输入赋值对象
tts(a)


//创建一个TTS对象；并且直接设置播放
//输入赋值对象，输入语言代码，输入语速率，输入音高率，输入播放文字（可传入null）
tts(a, "en", "I love you", 1, 1)

// 支持中文
tts(a, "zh", "你好", 1, 1)


//获取TTS对象初始化状态；赋值变量返回 0未完成初始化 1初始化成功 -1初始化失败 -2初始化语言失败 -3当前TTS对象不可用
//输入TTS对象，输入标识，输入赋值变量
tts(a, "zt", b)
syso(b)


//播放文字；模式 0替换以前的任务 1队列追加至后面
//输入TTS对象，输入标识，输入播放文字，输入模式，输入赋值变量
tts(a, "st", "I love you", 0, b)
syso(b)


//文字转换音频文件
//输入TTS对象，输入标识，输入文字，输入保存路径，输入赋值变量
tts(a, "ft", "I love you", "123.wav", b)
syso(b)


//设置语言
//输入TTS对象，输入标识，输入语言代码
tts(a, "lg", "en")


//设置语音播放速率。1为正常，值越低语速越慢（0.5是正常的一半），值越大语速越快（2是正常的两倍）
//输入TTS对象，输入标识，输入小数
tts(a, "se", 1)


//设置音高率，值越大声音越高音，值越小声音越低音，正常为1.0
//输入TTS对象，输入标识，输入小数
tts(a, "ph", 1)


//检查是否TTS正在播放
//输入TTS对象，输入标识
tts(a, "ip", b)
syso(b)


//释放TTS使用的资源
//输入TTS对象，输入标识
tts(a, "re")


//停止所有任务
//输入TTS对象，输入标识，输入赋值变量
tts(a, "sp", b)
syso(b)


//检查是否一个可用的TTS对象
//输入TTS对象，输入标识，输入赋值变量
tts(a, "is", b)
syso(b)


说明：
常用于文本转化为音频，并且播放。


语言代码：
- 系统默认支持语言
中国    zh
美国    en
德国    de
意大利  it
法国    fr
日本    ja
韩国    ko

注意事项：
单独TTS对象创建后，需要有一个异步初始化过程，如果创建TTS对象然后直接播放文本将无法成功。需要先完成初始化后，然后播放文本。

注意事项：
文字转语音TTS输出；默认语言状态：完全支持 中文


【blp 录制屏幕】
用法：
s b = "123.mp4"
//输入储存录制文件路径，输入视频宽度，输入视频高度，输入视频码率，输入视频帧率
blp(b, 1280, 720, 1024000, 30)

//开始录制
blp("st", b)
syso(b)

//停止录制
blp("sp", b)
syso(b)

//释放资源
blp("re", b)
syso(b)

//判断是否正在录制
blp("ip", b)
syso(b)

说明：
用于手机屏幕录制。

注意：
仅支持系统Android 5.0以及以上才有效果！
Android 5.0以下的系统，无效果！

【otob 转换为字节组】
用法：
//将文件转换为字节组，字节组将为字符串形式返回赋值给“b”
otob("%abc.txt", b)
syso(b)

//将字符串转换为字节组
otob("utf-8", "nihao", b)
syso(b)
//不设置编码
otob(null, "nihao", b)

//将文件转换成 byte[] 字节数组对象
otob("file", null, "%abc.txt", b)
syso(b)

//将字符串转换成 byte[] 字节数组对象
otob("str", "utf-8", "nihao", b)
syso(b)


说明：
将字符或文件转换为字节组

【btoo 字节组还原】
用法：
otob("%abc.txt", b)
//将字节组转换为文件，变量 b 可为byte[] 字节数组对象
btoo(b, "%abc2.txt")


otob("utf-8", "nihao", b)
//字节组转换为字符串，变量 b 可为byte[] 字节数组对象
btoo("utf-8", b, c)
syso(c)

//不设置编码，变量 b 可为byte[] 字节数组对象
btoo(null, b, c)

说明：
将字节组转换为字符或文件

【sot Socket网络通信】
用法：
//服务端
//服务端口，临时文件目录，接受客户超时，客户连接超时，是否覆盖文件
sot(8668, "%iApp/tempSocket", 0, 0, false, b)
{
//消息内容
syso(st_msG)
//连接对象
syso(st_ssR)

}

//客户端
//服务地址，服务端口，服务连接超时，是否覆盖文件
sot("192.168.1.100", 8668, 0, false, b)
{
//消息内容
syso(st_msG)
//连接对象
syso(st_ssR)

}

//发送字符串，必须放在线程内
sot(b, "str", "nihao")

//发送文件，必须放在线程内
sot(b, "file", "%abc.txt")

//发送字节组，必须放在线程内
otob("utf-8", "nihao", c)
sot(b, "bt", c)

//发送不带信息头 byte[]字节组，必须放在线程内
sot(b, "bt2", bytes)

//关闭释放sot
sot(b, "re")

//获取sot是否已释放
sot(b, "ip", c)

//获取ID总数
sot(b, "id", c)

//获取连接对象列表
sot(b, "list", c)

//获取连接对象列表的第一位
sot(b, "list", 0, c)

//获取连接总数
sot(b, "size", c)

//是否允许接受新连接
sot(b, "new", true)


说明：
Socket 管理操作。服务端发送消息将批量发送给所有连接。

服务端说明：
要求：
1.能连接公共网络 或 内网
2.拥有固定IP作为客户端连接的目标
3.电脑、手机、平板电脑等设备上运行服务端。
4.可使用iapp在自己的手机上面开发服务端，并运行服务端。

客户端说明：
要求：
1.能连接公共网络 或 内网
2.可使用iapp在自己的手机上面开发客户端，并连接服务端。

常见开发：
使用手机或电脑作为服务端，手机客户端与服务端相互传递文件、数据等。

【sota 单个Socket通信操作】
用法：
//获取连接对象列表的第一位，变量“c”属于单个Socket通信
sot(b, "sl", 0, c)

//获取通信对方的IP
sota(c, "ht", d)

//获取sota是否已释放
sota(c, "ip", d)

//关闭释放sota
sota(c, "re")

//获取socket对象
sota(c, "socket", d)

//获取连接对象ID
sota(c, "id", d)

//发送字符串，必须放在线程内
sota(c, "str", "nihao")

//发送文件，必须放在线程内
sota(c, "file", "%abc.txt")

//发送字节组，必须放在线程内
otob("utf-8", "nihao", d)
sota(c, "bt", d)

//发送不带信息头 byte[]字节组，必须放在线程内
sota(c, "bt2", bytes)

说明：
常用于单个Socket通信的操作管理


【loadso 加载动态库】
用法：
//比如加载 libabc.so
loadso("abc")

说明：
加载SO动态链接库。


【loadjar 加载jar库】
用法：
//比如加载 abc.jar
//赋值变量 库对象
loadjar("abc.jar", b)
syso(b)

//比如加载 abc.apk
//包含Activity需要传入true，赋值变量 库对象
loadjar("abc.apk", true, b)
syso(b)

//比如加载 abc.apk
// 配置 ClassLoader 后可以让SDK读取到ClassLoader里的类，如下就是把当前应用的ClassLoader加上了
//包含Activity需要传入true，传入ClassLoader，赋值变量 库对象
javax(gcl, activity, "android.content.ContextWrapper", "getClassLoader")
loadjar("abc.apk", false, gcl, b)
syso(b)

说明：
用于加载一些jar，dex，apk 的 sdk。需要把jar文件载入至项目资源，加载过程将联网校验。
如果附带SO动态链接库，需要把SO文件载入至项目资源。


【cls 获取完整接口类】
用法：
//获取一个类，输入完整类名如 java.lang.Math
//赋值变量 类对象
cls("java.lang.Math", a)
syso(a)

//获取一个字符串类，常用类型可直接输入类名如 String
cls("String", b)
syso(b)

//加载SDK abc.jar，并获取SDK里一个类 输入完整类名 com.sdk.abc
loadjar("abc.jar", a)
cls(a, "com.sdk.ceshi", c)
syso(c)

用法：
获取一个类；或从 jar SDK包获取类；

注意：完整类名区分大小写

【clssm 获取类的所有接口】
用法：
cls("String", b)

//获取所有构造函数
clssm(b, "init", c)

//获取所有函数方法
clssm(b, "method", c)

//获取所有变量
clssm(b, "field", c)


说明：
返回一个数组。


【java 调用java代码方法】
用法：
//调用java api java.lang.String.indexOf(String string) 查询字符56 在123456789 中位置
cls("String", c)
javax(a, "123456789", c, "indexOf", "String", "56")
syso(a)

//初始化一个StringBuilderd
javanew(a, "java.lang.StringBuilder", "String", "12345")
java(b, a, "java.lang.StringBuilder.append", "String", "6789")
java(c, b, "java.lang.StringBuilder.toString")
syso(c)


loadjar("test.jar", jar)
cls(jar, "com.sdk.ceshi", c1)
//调用静态方法 com.sdk.ceshi类 c 方法
javax(c, null, c1, "c", "int", 123)
syso(c)

//调用静态变量 com.sdk.ceshi类 a 变量
javags(c, null, c1, "a")
syso(c)

//初始化com.sdk.ceshi类
//输入赋值对象变量，输入完整类名或 cls方法的赋值变量
javanew(a, c1)

//访问变量，com.sdk.ceshi类 b变量
javags(c, a, c1, "b")
syso(c)

//设置变量，com.sdk.ceshi类 b变量
javass(c, a, c1, "b", "123456")
syso(c)


//设置回调方法
javanew(a, "android.widget.TextView", "android.content.Context", activity)
java(b, a, "android.widget.TextView.setText", "CharSequence", "我是文本控件")
//注意回调接口类名前面需要加一个“.”，如.android.view.View.OnClickListener
java(b, a, "android.view.View.setOnClickListener", ".android.view.View$OnClickListener", null)
{
//系统赋值
syso(st_mD)
syso(st_aS)

}


说明：
支持 android 所有的api；以及 自加载的jar SDK 的 api 

注意：完整类名或 方法名 或 变量名 区分大小写

activity：默认变量 Activity组件

javax 与 java 方法区别：
javax：第3位参数完整类名，第4位参数方法名。类名可传入 cls方法的赋值变量；
java：第3位参数 完整类名和方法名。


【javax 调用java代码方法】
用法：
loadjar("test.jar", jar)
cls(jar, "com.sdk.ceshi", c1)
//调用静态方法 com.sdk.ceshi类 c 方法
javax(c, null, c1, "c", "int", 123)
syso(c)

//调用静态变量 com.sdk.ceshi类 a 变量
javags(c, null, c1, "a")
syso(c)

//初始化com.sdk.ceshi类
//输入赋值对象变量，输入完整类名或 cls方法的赋值变量
javanew(a, c1)

//访问变量，com.sdk.ceshi类 b变量
javags(c, a, c1, "b")
syso(c)

//设置变量，com.sdk.ceshi类 b变量
javass(c, a, c1, "b", "123456")
syso(c)

说明：
常用于自定义SDK加载后的操作。

javax：第3位参数完整类名，第4位参数方法名。类名可传入 cls方法的赋值变量；

【javacb 自定义回调】
用法：

loadjar("test.jar", jar)
cls(jar, "com.ceshi.dex.main", c1)
javanew(o, c1)
cls(jar, "com.ceshi.dex.main$huidiao", c2)

javacb(hd, c2)
{
//系统赋值
syso(st_mD)
syso(st_aS)

}

//设置回调
javax(a, o, c1, "sethuidiao", c2, hd)
//调用回调方法
javax(a, o, c1, "get", "String", "666")

说明：
常用于设置自定义SDK的回调方法。


【res 安装包资源管理器】
用法：
//获取应用自己的对象
res(a)

//获取其他apk安装包内的资源对象，只支持加载SD卡上的apk
res("%abc.apk", a)

//获取资源
//输入资源对象，输入资源标识或文件名(没后缀)，输入资源类型，输入赋值变量
res(a, "ic_launcher", "drawable", b)

//获取资源ID，打包测试才有效
res(a, "ic_launcher", "drawable", false, b)

//获取 AssetManager 或 Resources 对象
res(a, "asset", b)
res(a, "resources", b)

说明：
可获取的资源类型 drawable、string、color、stringarray、layout


【call 交互式语言调用】
用法：

//输入赋值变量，语言类型，模块m的abc方法，输入参数1，输入参数2
call(null, "myu", "m.abc", "nihao", 66)


//输入赋值变量，语言类型，模块mk的abc方法，输入参数1
call(a, "mlua", "mk.abcd", 123)

//输入赋值变量，语言类型，模块mk的abc方法，，输入参数1，输入参数2，输入参数3
call(a, "mjava", "mk.abcd", 123, 456, 789 )


//没有参数的
//输入赋值变量，语言类型，模块mk的abc方法
call(null, "mjs", "mk.abcdf")

说明：
用于多语言的代码交互。

注意：
此方法只能调用模块方法，输入是字符串如 m.abc 模块m 的abc方法

注意：
参数数量要与实际模块方法的参数的数量一致。

注意：
三种语言，只有 mlua 可以返回赋值变量，裕语言可以通过设置全局变量变相返回变量， mjs设置赋值变量无效。


【json json数据解析】
用法：
//解析json数据
s text = "{"id":1, "name":"xiaobai", "age":16}"
json(text, jo)
//获取id
json(jo, "get", "id", a)
syso(a)
//获取name
json(jo, "get", "name", b)
syso(b)
//获取age
json(jo, "get", "age", c)
syso(c)

//修改age数据
json(jo, "set", "age", 20)

//删除id数据
json(jo, "del", "id")

//打印json数据
json(jo, "json", text)
syso(text)



//解析json列表数据
s text = "{"userlist":[{"id":1, "name":"niubi", "age":16},{"id":2, "name":"wangba", "age":18},{"id":3, "name":"goudan", "age":17}]}"
json(text, jo)

//打印json数据
json(jo, "list", "userlist", list)
json(list, "size", size)
w(size > 0)
{
s-(1, size)
json(list, "data", size, item)

//获取id
json(item, "get", "id", a)
syso(a)
//获取name
json(item, "get", "name", b)
syso(b)
//获取age
json(item, "get", "age", c)
syso(c)

}

说明：
常用于解析服务器反馈的数据。


【utb Toolbar工具栏设置】
用法：

//设置自定义的工具栏 为当前界面的工具栏
//输入Toolbar工具栏的 控件id或控件对象
s id = 3
utb(id)


//绑定侧滑控件，侧滑控件内需要包含左侧滑，绑定后可以在Toolbar工具栏的左图标 控制左边侧滑
//输入Toolbar工具栏的 控件id或控件对象，输入侧滑的 控件id或控件对象
utb(3, 2)


//设置参数

s id = 3
utb("set", "dshe", true)

//设置左图标，可以设置事件监听
utb("left", id, "@a.png")

//设置左图标的点击事件，注意此代码需在 utb(id) 后，否则事件将无效。
utb("set", "leftck", id)
{
syso("lefticon")
}

//设置右菜单图标，无事件。可使用界面菜单事件
utb("right", id, "@b.png")


//标题
utb("set", "title", "apptitle")

//子标题
utb("set", "subtitle", "appsubtitle")

//自定义布局可输入View类型布局
utb("set", "cv", v)

//显示选项
utb("set", "do", 0)

//显示或隐藏 标题
utb("set", "dste", true)

//显示或隐藏 自定义布局
utb("set", "dsce", true)

//显示或隐藏 主页图标
utb("set", "dshe", true)


//获取参数

//标题
utb("get", "title", c)

//子标题
utb("get", "subtitle", c)

//自定义布局可输入View类型布局
utb("get", "cv", c)

//显示选项
utb("get", "do", c)

//动作栏布局高度
utb("get", "height", c)


说明：
常用于设计应用顶部工具栏。

【tws 弹窗提醒】
用法：
//获取展示的控件对象，提醒将在这个控件里展示
gvs(1, v)

//无按钮弹出提醒
//输入控件对象可设置null，输入字符，输入显示时长（值0 -1 -2）
tws(v, "ni hao!", 0)


//有按钮弹出提醒
//输入控件对象可设置null，输入字符，输入显示时长（值0 -1 -2），输入按钮标题
tws(v, "ni hao ma?", 0, "hao")
{
syso("go")
}

【uht 滑动窗体控制】
用法：

//添加新的页面，设置的界面会执行载入事件里的代码
//输入滑动窗体的 控件id或控件对象，输入标识，输入插入序号 如-1为尾部 0为头部，输入标题，输入界面名，输入控件对应的数据项...不限制数量可参考代码ula
uht(2, "add", -1, "标题", "a.iyu", 1="abc", 2="bac", 3="bbc")

//删除界面
//输入滑动窗体的 控件id或控件对象，输入标识，输入界面序号 序号以0开始 -1为尾部
uht(2, "del", 0)

//修改界面标题
//输入滑动窗体的 控件id或控件对象，输入标识，输入界面序号 序号以0开始 -1为尾部
uht(2, "title", 0, "标题2")

//获取页面总数
uht(2, "size", b)
syso(b)

//释放内存
uht(2, "close")


//绑定标签布局，绑定后滑动界面时标签布局会跟随运动，需要注意 标签布局 和 滑动窗体 的子项数量应一致，新增子项时也需要同时增加
//输入滑动窗体的 控件id或控件对象，输入标识，输入标签布局的 控件id或控件对象，是否应刷新其内容
uht(2, "bd", 3, true)
//注意：如果绑定前 标签布局 如有设置子项，绑定时会被清空。绑定后直接添加滑动窗体 的子项并设置 标题


//增加标签布局 的子项
us(3, "app_tabadd", "选项")

//添加滑动窗体 的子项
uht(2, "add", -1, "标题", "a.iyu", 1="abc", 2="bac", 3="bbc")


说明：
用于动态管理控制滑动窗体和垂直滑动窗体的 新增页面、删除页面、绑定标签布局等。


【cast 强制转换数据类型】
用法：
s a = 123
//转换数据类型并直接赋值
//输入目标类型 或 类对象，输入需要转换的数据变量
cast("String", a)
syso(a)

s b = 456
//获取类对象
cls("String", a)
//输入目标类型 或 类对象，输入需要转换的数据变量，输入赋值变量
cast(a, b, c)
syso(c)

说明：
常用于数据强制转换。
支持基础类型 byte、Byte、short、Short、int、Integer、long、Long、float、Float、double、Double、char、Char、boolean、Boolean、String
还支持以前允许强制转换的其他类，转换失败则返回 null

【yul 加载yul布局】
用法：

//将布局加载展示到指定的布局控件里
//输入控件id或控件对象（比如输入线性布局ID），输入 yul 布局文件名
yul(1, "a.yul")


//返回布局对象
//输入 yul 布局文件名，输入赋值变量返回一个View对象
yul("a.yul", a)
syso(a)


说明：
yul布局是以 android 的 xml布局为基础，用于动态加载布局到应用界面。和安卓xml布局用法和代码都是一致的。

在设计 yul布局 时需要自定义控件ID，如设置控件ID:123 编写代码 android:id="123" 或 android:id="@+id/s123" 两种写法都可以，效果都是ID为 123

【rps 请求权限】
用法：
// 请求所有权限
rps()

// 请求指定权限，可输入数组
s p = "android.permission.WRITE_EXTERNAL_STORAGE"
rps(p)

// 检查权限是否需要授权
s p = "android.permission.WRITE_EXTERNAL_STORAGE"
rps(a, p)
// true 为需要需要申请授权；false为不用再申请授权
syso(a)

【zj 组件控制】
用法：
//如广告组件，首先下载的组件，并且设置好组件。

//初始化SDK，放在第一个界面的载入事件里
//输入赋值变量，标识，发布 ID，密钥，是否开启的Log输出（需要换自己的渠道信息）
zj(a, "init", "String", "85aa56a59eac8b3d", "String", "a14006f66f58d5d7", "boolean", true)

//初始化积分墙
//输入赋值变量，标识
zj(a, "initjfq")

//展示积分墙
zj(a, "jfqgo")


说明：
用于控制组件。

【无障碍服务】
用法：
固定模块名为 ays_service 可创建模块 ays_service.myu，代码如下：

//事件方法 on 实时回调变化事件
fn on(e)
//获取事件类型
java(b, ays, "com.iapp.app.ays.gtype", "android.view.accessibility.AccessibilityEvent", e)
//如果事件类型
f(b == 32 || b == 2048){
  //获取事件源的对象节点列表
  java(node, ays, "com.iapp.app.ays.gall", "AccessibilityEvent", e)
  //判断事件来源是不是包名为com.iapp.app的应用
  java(gpn, ays, "com.iapp.app.ays.gpn", "AccessibilityEvent", e)
  f("com.iapp.app" == gpn)
  {
     //判断类名，根据指定的类名进行不同的操作
     java(gcn, ays, "com.iapp.app.ays.gcn", "AccessibilityEvent", e)
     f("com.iapp.app.HomeMian" == gcn)
     {
        //从对象列表搜索文本为“创建”的对象，并点击该对象
        java(b, ays, "com.iapp.app.ays.cktext", "AccessibilityNodeInfo", node, "int", 16, "String", "创建")
     }
     else f("com.iapp.app.HomeAdd" == gcn)
     {
        //根据ID获取指定的节点
        java(b, ays, "com.iapp.app.ays.id", "AccessibilityNodeInfo", node, "String", "com.iapp.app:id/ui_home_add_title")
	//设置节点的文本框输入指定字符
        java(c, ays, "com.iapp.app.ays.enter", "java.util.List", b, "String", "name")
        //根据ID获取指定的节点
        java(b, ays, "com.iapp.app.ays.id", "AccessibilityNodeInfo", node, "String", "com.iapp.app:id/ui_home_add_remark")
	//设置节点的文本框输入指定字符
        java(c, ays, "com.iapp.app.ays.enter", "java.util.List", b, "String", "remark")
        //从对象列表搜索指定ID的对象，并点击该节点对象
        java(b, ays, "com.iapp.app.ays.ckid", "AccessibilityNodeInfo", node, "int", 16, "String", "com.iapp.app:id/ui_home_add_go")
     }
  }
  //释放根源节点
  java(b, ays, "com.iapp.app.ays.re", "AccessibilityNodeInfo", node)

}

end fn

//初始化事件方法 onsc 启动时回调一次
fn onsc()
s pns = "com.iapp.app"
//设置监听指定的包名，可以设置多个包名用逗号隔开如"com.xxx.a,com.xxx.b"
javass(a, null, "com.iapp.app.ays.pns", pns)
//设置相应时间
javass(a, null, "com.iapp.app.ays.nt", 1000)
end fn


然后 权限配置管理》application配置 将下面的配置粘贴进去：
	<service
            android:name="com.iapp.app.ays"
            android:label="iapp开发工具无障碍辅助功能"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService"/>
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/aya_config"/>
        </service>

最后，【正式打包发布】打包完成后，安装测试。记得自行去设置》辅助功能》打开我们的服务《iapp开发工具无障碍辅助功能》。
注意：直接在iapp里测试无效。


更多代码示范：

//------静态调用
//获取无障碍功能是否已经授权
java(a, null, "com.iapp.app.ays.isas", "Context", activity)

//如果没有授权，可跳转设置界面
java(a, null, "com.iapp.app.ays.goset", "Context", activity)


//------事件源操作
//获取Context功能类
java(a, ays, "com.iapp.app.ays.gbc")

//获取无障碍功能配置信息
java(a, ays, "com.iapp.app.ays.gsi")

//设置无障碍功能配置信息
java(b, ays, "com.iapp.app.ays.ssi", "AccessibilityServiceInfo", a)

//调用全局事件
//输入值：1. 返回键 2. HOME键 3. 最近打开应用列表 4. 打开通知栏 5. 设置 6. 锁屏
java(a, ays, "com.iapp.app.ays.pga", "int", 1)

//获取事件类型
//值：32 打开PopupWindow，Menu，Dialog等的事件  64 显示通知的事件  2048 更改窗口内容的事件  4194304 屏幕上显示的窗口中的事件更改
java(a, ays, "com.iapp.app.ays.gtype", "AccessibilityEvent", e)

//获取事件源类的类型
java(a, ays, "com.iapp.app.ays.gcn", "AccessibilityEvent", e)

//获取事件源的包名
java(a, ays, "com.iapp.app.ays.gpn", "AccessibilityEvent", e)

//获取事件源的是否可用
java(a, ays, "com.iapp.app.ays.ised", "AccessibilityEvent", e)

//获取事件源的节点总数
java(a, ays, "com.iapp.app.ays.gsl", "AccessibilityEvent", e)

//获取事件源的整数ID
java(a, ays, "com.iapp.app.ays.gwid", "AccessibilityEvent", e)

//获取事件源的时间
java(a, ays, "com.iapp.app.ays.gtime", "AccessibilityEvent", e)

//释放资源
java(a, ays, "com.iapp.app.ays.re", "AccessibilityEvent", e)


//------节点的操作
//获取事件源的节点对象列表
java(a, ays, "com.iapp.app.ays.gall", "AccessibilityEvent", e)

//获取窗口的对象节点列表，需要Android 4.1及以上才可调用
java(a, ays, "com.iapp.app.ays.gall")

//根据序号；获取对象的子节点
java(a, ays, "com.iapp.app.ays.gi", "AccessibilityNodeInfo", node, "int", 0)

//获取对象的子节点总数
java(a, ays, "com.iapp.app.ays.gi", "AccessibilityNodeInfo", node)

//根据当前焦点向某个方向进行搜索可以获得输入焦点的最近控件
//输入值：33 向上  130 向下  17 向左  66 向右
java(a, ays, "com.iapp.app.ays.focussearch", "AccessibilityNodeInfo", node, "int", 130)

//根据文本查询控件，返回节点列表
java(nodelist, ays, "com.iapp.app.ays.text", "AccessibilityNodeInfo", node, "String", "创建")

//根据id查询控件，返回节点列表
java(nodelist, ays, "com.iapp.app.ays.id", "AccessibilityNodeInfo", node, "String", "com.iapp.app:id/ui_home_add_go")

//根据焦点查询
//输入值：1 输入焦点  2 可访问性焦点
java(a, ays, "com.iapp.app.ays.focus", "AccessibilityNodeInfo", node, "int", 1)

//获取节点文本
java(a, ays, "com.iapp.app.ays.gt", "AccessibilityNodeInfo", node)

//获取节点类的类型
java(a, ays, "com.iapp.app.ays.gcn", "AccessibilityNodeInfo", node)

//获取节点整数ID
java(a, ays, "com.iapp.app.ays.gwid", "AccessibilityNodeInfo", node)

//获取节点ID
java(a, ays, "com.iapp.app.ays.gid", "AccessibilityNodeInfo", node)

//获取可以在节点上执行的操作
java(a, ays, "com.iapp.app.ays.gal", "AccessibilityNodeInfo", node)

//获取节点在屏幕上坐标
java(a, ays, "com.iapp.app.ays.gbis", "AccessibilityNodeInfo", node)

//获取父节点在屏幕上坐标
java(a, ays, "com.iapp.app.ays.gbip", "AccessibilityNodeInfo", node)

//获取节点的包名
java(a, ays, "com.iapp.app.ays.gpn", "AccessibilityNodeInfo", node)

//获取节点的父节点
java(a, ays, "com.iapp.app.ays.gp", "AccessibilityNodeInfo", node)

//获取此节点是否可点击
java(a, ays, "com.iapp.app.ays.isck", "AccessibilityNodeInfo", node)

//获取此节点是否已启用
java(a, ays, "com.iapp.app.ays.ised", "AccessibilityNodeInfo", node)

//获取此节点是否已选中
java(a, ays, "com.iapp.app.ays.iscd", "AccessibilityNodeInfo", node)

//获取这个节点是否被聚焦
java(a, ays, "com.iapp.app.ays.isfd", "AccessibilityNodeInfo", node)

//获取此节点是否可以长时间点击
java(a, ays, "com.iapp.app.ays.islck", "AccessibilityNodeInfo", node)

//获取此节点是否是密码
java(a, ays, "com.iapp.app.ays.ispd", "AccessibilityNodeInfo", node)

//获取节点是否可滚动
java(a, ays, "com.iapp.app.ays.isse", "AccessibilityNodeInfo", node)

//获取是否选择此节点
java(a, ays, "com.iapp.app.ays.issd", "AccessibilityNodeInfo", node)



//根据文本查询；模拟控件点击控件
java(a, ays, "com.iapp.app.ays.cktext", "AccessibilityNodeInfo", node, "int", 16, "String", "创建")

//根据ID查询；模拟控件点击控件
java(a, ays, "com.iapp.app.ays.ckid", "AccessibilityNodeInfo", node, "int", 16, "String", "com.iapp.app:id/ui_home_add_go")

//根据焦点查询；模拟控件点击控件
//输入值：1 输入焦点  2 可访问性焦点
java(a, ays, "com.iapp.app.ays.ckfocus", "AccessibilityNodeInfo", node, "int", 16, "int", 1)


	/.
	  模拟执行操作
	  1 将输入焦点输入到节点的操作
	  16 点击节点信息的动作
	  32 长时间点击节点的动作
	  32768 操作来粘贴当前的剪贴板内容
	 ./
//开始模拟控件点击
//输入节点列表
java(b, ays, "com.iapp.app.ays.ck", "java.util.List", nodelist, "int", 16)

//开始模拟控件点击
//输入节点列表，输入自定义的Bundle
java(b, ays, "com.iapp.app.ays.ck", "java.util.List", nodelist, "int", 16, "android.os.Bundle", be)

//对单项模拟控件点击
//输入节点列表
java(b, ays, "com.iapp.app.ays.ck", "AccessibilityNodeInfo", node, "int", 16)

//对单项模拟控件点击
//输入节点列表，输入自定义的Bundle
java(b, ays, "com.iapp.app.ays.ck", "AccessibilityNodeInfo", node, "int", 16, "android.os.Bundle", be)

//对单项模拟执行输入文本,Android 4.3 版本及以上
java(b, ays, "com.iapp.app.ays.enter", "AccessibilityNodeInfo", node, "String", "nihao")

//开始模拟执行输入文本,Android 4.3 版本及以上
java(b, ays, "com.iapp.app.ays.enter", "java.util.List", nodelist, "String", "nihao")

//获取节点所有子节点列表
java(nodelist, ays, "com.iapp.app.ays.ganiall", "AccessibilityNodeInfo", node)

//释放节点资源
java(b, ays, "com.iapp.app.ays.re", "AccessibilityNodeInfo", node)

说明：
无障碍功能（辅助功能）常用于简化操作，使应用或 系统的变得更智能、简便。


【自定义代码提示】
说明：
iapp允许开发者自定义代码提示，这样可以最大程度保留开发者的个人习惯，可以定义成你自己熟悉的关键词。

格式：
代码\说明
如：
abcde\变量名
abc()\方法名


配置对应文件：/data/data/com.iapp.app/files/config/srctonew.xml

【HTML5项目】
例子：
//输入浏览器控件ID或对象，输入标识，输入项目网页路径
us(1, "url", "@html5/index.html")
//us(1, "url", "%html5/index.html")

说明：
常用与运行一个HTML5项目，包括HTML5应用、HTML5游戏等。

【上传项目】

项目内导入覆盖规则：

	综合：一个完整应用项目的导入；先清空当前项目源码与资源后，导入源码与资源 以及根据需求导入项目信息与图标

	其他分类通用：
	1. 项目中mian.iyu启动界面，只导入其中有备注的控件，导入至当前项目打开的界面里；
	2. 不清空当前项目文件，直接覆盖除了mian.iyu以外的其他所有界面与资源；
	3. 覆盖过程如有模块文件重复，将以追加方式模块增加，不覆盖；
	4. 建议复杂命名界面名，复杂命名模块方法名；

项目外导入覆盖规则：
	1. 遇本地重复项目，不覆盖。
	2. 导入为完整项目。

说明：
分享技术，享受乐趣。

【代码规范】
例子：
//下面的判断语句，使用了字符串；存在规范问题，会出错；
f("1?2(3}4,5==6" == "1?2(3}4,5==6")
{
f("a"!=sb" != "a"!=sb6")
{
tw("{1},(2)")
}
}

转义关键符号，需修正为：
f("1\?2(3}4\,5\=\=6" == "1\?2(3}4\,5\=\=6")
{
f("a"\!\=sb" != "a"\!\=sb6")
{
tw("{1}\,(2)")
}
}

//下面判断读取文本文件，
fr("%ab,c.txt", "utf-8", c)
tw(c)

转义关键符号，需修正为：
fr("%ab\,c.txt", "utf-8", c)
tw(c)

以上为规范异常，系统关键符号需要进行转义，转义在符号前增加“\”。

系统关键符号(小写符号)：( ) , = ! > < ? * + { } | &

注意：
“\”作为转义符号需注意例子：

.例子1
tw("ni\nhao")
/.
输出：
ni
hao
./

.例子2
tw("ni\\nhao")
/.
输出：
ni\nhao
./

.例子3
tw("ni\\hao")
/.
输出：
ni\hao
./

.例子4
tw("ni\hao")
/.
输出：
ni\hao
./

.例子5
tw("ni\\\\hao")
/.
输出：
ni\\hao
./

.例子6
tw("ni\,hao")
/.
输出：
ni,hao
./

【单击触屏事件】

系统赋值：
st_vId：控件id
st_vW：控件对象

说明：
该事件无返回值，当用户完成单击触屏即执行事件代码。

【触屏监听事件】
用法：
[true]
tw("将返回值为true")

系统赋值：
st_vId：控件id
st_vW：控件对象
st_eA：执行的动作
st_eX：触屏位置X坐标
st_eY：触屏位置Y坐标
st_rX：原始位置X坐标
st_rY：原始位置Y坐标

说明：
该事件有返回值，不设置返回值将默认为false。当用户触屏屏幕即执行事件代码。

返回值说明：
在事件代码编辑框顶部一行填写 “[true]”，即设置为返回true
当返回true值时，说明已完成该事件的执行，将不在执行此事件。
当返回false值时，将持续执行当前事件。

【触屏长按事件】
用法：
[true]
tw("将返回值为true")

系统赋值：
st_vId：控件id
st_vW：控件对象

说明：
该事件有返回值，不设置返回值将默认为false。当用户长久触屏屏幕即执行事件代码。

返回值说明：
在事件代码编辑框顶部一行填写 “[true]”，即设置为返回true
当返回true值时，说明已完成该事件的执行，将不在执行此事件。
当返回false值时，将持续执行当前事件。

【键盘触发事件】
用法：
[true]
tw("将返回值为true")

系统赋值：
st_vId：控件id
st_vW：控件对象
st_kC：按下的物理按键对应的数值
st_eA：执行的动作
st_eR：

说明：
该事件有返回值，不设置返回值将默认为false。当用户按下物理按键即执行事件代码。

返回值说明：
在事件代码编辑框顶部一行填写 “[true]”，即设置为返回true
当返回true值时，说明已完成该事件的执行，将不在执行此事件。
当返回false值时，将持续执行当前事件。

【触屏长按菜单事件】
用法：
title:操作
case 选择A:
tw("A")
break
case 选择B:
tw("B")
break
case 选择C:
tw("C")
break
default:
tw("载入成功")
break

系统赋值：
st_vId：控件id
st_vW：控件对象

说明：
常用于需要多操作选项。

【框编辑监听事件】
用法：
[true]
tw("将返回值为true")

系统赋值：
st_vId：控件id
st_vW：控件对象
st_aI：动作的标识数值
st_eA：执行的动作
st_eR：
st_eK：键值

说明：
该事件有返回值，不设置返回值将默认为false。当用户按下动作键即执行事件代码。

注意：
需要编辑框设置相应的控件 imeoptions 属性

事件例子：
f(st_aI != 0)
{
//动作的标识数值
syso(st_aI)
}

返回值说明：
在事件代码编辑框顶部一行填写 “[true]”，即设置为返回true
当返回true值时，说明已完成该事件的执行，将不在执行此事件。
当返回false值时，将持续执行当前事件。


【文本更新监听事件】

系统赋值：
st_vId：控件id
st_vW：控件对象
st_sS：文本内容
st_sT：
st_bE：
st_cT：
st_aR：

说明：
该事件无返回值。常用于监听文本即时更新。

【获得焦点事件】

系统赋值：
st_vId：控件id
st_vW：控件对象
st_hF：是否获得焦点

说明：
该事件无返回值，当控件获得/失去焦点即执行事件代码。

【触屏滑动事件】

系统赋值：
st_vId：控件id
st_vW：控件对象
st_sE：
st_fM：
st_vT：
st_bT：

说明：
常用于滑动控件的滑动监听。


【单击项目事件】

系统赋值：
st_vId：控件id
st_vW：控件对象
st_pN：被点击视图中的位置
st_iD：被点击的项目

说明：
常用于列表项点击监听。

【浏览器事件】

说明：
常用于浏览器的互动。

【滑动窗体事件】

说明：
常用于滑动窗体的互动。

【侧滑窗体事件】

说明：
常用于侧滑窗体的互动。

【下拉菜单事件】

说明：
常用于下拉菜单的互动。

【摄像头拍摄事件】

说明：
常用于摄像头拍摄事件的互动。

【载入事件】

说明：
将于界面加载完毕后执行。

【载入完毕事件（界面可交互）】

说明：
将于界面加载完毕后，并且用户可于界面交互时执行。常用需要在载入事件中设置控件属性。

如：
使用 addv 添加将界面后，如果设置控件属性，请将设置属性的代码写入 载入完毕事件中。

【菜单事件】
用法：
case 选择A:
tw("A")
break
case 选择B:
tw("B")
break
case 选择C:
tw("C")
break
default:
tw("载入成功")
break


//参数为多个并以“|”隔开
//参数1为选项标题|参数2为图标|参数3为显示动作值分别为0 1 2 4 8|参数4为次序根据数值大小
带图标的
case 选择A|@a.png|1|1:
tw("A")
break
case 选择B|@b.png|0|2:
tw("B")
break
case 选择C|@c.png|0|3:
tw("C")
break
default:
tw("载入成功")
break


说明：
当用户触屏菜单事件。

【按键按下事件】

说明：
用户设备物理按键按下将执行。

【按键释放事件】

说明：
用户设备物理按键按下然后释放触屏，将执行。

【销毁界面事件】

说明：
当用户销毁当前界面时将执行。

【停止事件】

说明:
界面处于停止或暂停事将执行。（如：用户切出到其他应用）

【重新开始事件】

说明：
界面重新获得焦点，可视时将执行。（如：用户从其他应用切换回来了）

【回调结果事件】

系统赋值：
st_sC：请求标识数值
st_lC：结果状态数值
st_iT：结果目标对象

说明：
常用于界面或功能回调返回的结果或传递的数据。

【重力感应事件】

系统赋值：
st_x：X轴
st_y：Y轴
st_z：Z轴

说明：
获取手机的即时动作。

参考：
　　手机屏幕向上(z轴朝天)水平放置的时侯，(x，y，z)的值分别为(0，0，10);
　　手机屏幕向下(z轴朝地)水平放置的时侯，(x，y，z)的值分别为(0，0，-10);
　　手机屏幕向左侧放(x轴朝天)的时候，(x，y，z)的值分别为(10，0，0);
　　手机竖直(y轴朝天)向上的时候，(x，y，z)的值分别为(0，10，0);

《ijava》速成开发手册3.0


 用户编程交流QQ群：
 官方源码开源群：323924434
 iApp技术开发群：483556574
 官方1群：1042334128
 官方2群：781302772
 官方3群：291033193
 官方4群：549133854
 官方5群：705873634
 官方游戏开发群：379221113


【3.0 ijava升级简介】
1. 出错后报错信息，显示在调试日志里。
2. 语法完整参照java语法 和 Android的api
3. 融合裕语言代码，写法和使用方式有所不同。
4. 入口文件必须为 mian.iyu 如果全部采用此语言开发，可以在入口文件加一个uigo跳转。
5. 更多不同点可以自行探索。
6. java语言相对复杂，更多教程资料可以自己百度找找。
7. 注意java的类都是需要导入完整包名的，否则会出错。可以使用 import 或 imports 进行导入包名。


【java 变量】
用法：

常用变量类型：
"boolean", "byte", "char", "short", "int", "long", "float", "double", "void", "Boolean", "Byte", "Character", "Short", "Integer", "Long", "Float", "Double", "String", "Object"

变量类型的详细说明，可以自己百度下；

ijava声明全局变量：
申明全局变量可以在 mjava模块里， 然后在 ijava的载入事件加载模块；

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。裕语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。


【s 变量】
用法：

申明界面变量
//可以赋字符串
ss("a", "blss");
//或 设置为空
i.ss("a", null);

//读取数据
tw(ss("a"));

申明全局变量
//可以赋其他变量
sss("a", "blsss");
//或 设置为空
i.sss("a", null);

//读取数据
tw(sss("a"));

用途：
可用于与iApp支持的其他语言进行交换数据，数据共享，数据储存等。

区域介绍：
界面变量：生产界面变量后，同一个界面中的所有事件，均可对其进行操作。
全局变量：生产全局变量后，同一个应用中的所有事件，均可对其进行操作。

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。

提示：
自定义的变量名，比如“abc、 nihao、sfw123、www_zzw”变量不允许全部为数字，不允许掺杂符号，请不要使用太长的变量名，不推荐使用中文作为变量名。

空值：
如果访问一个没有声明的变量，将返回“null”空值类型，这个不对等于字符的 'null'。
判断是否空值的例子:(这里我们不知道变量“abc”是否空值)
Object abc = sss("abc");
f(abc == null)
{
	syso("是空值");
}

【// 或  /* */ 注释语句】
用法：

//这个是变量“a”它的值等于“1”
int a = 1;
//这个是变量“b”它的值等于“2”
int b = 2;

/*
大量代码注释方法
int c = 3;
int d = 4;

*/

说明：
注释语句符号可以用“//” 也可以用“/* */”，以注释符号开头的正行，将会被代码执行器无视。通常用于给自己标示代码的含义


【import 导入包】
用法：
在模块里可以使用，如：
//但是在方法里 或 事件里这样写不行
import android.view.View;

在方法或事件 里导入包，如：
//注意imports 多一个 s 
imports("android.view.View");

//可以同时导入多个包，使用逗号“,”隔开
imports("android.view.View, android.widget.Toast");

说明：
java代码是需要导入完整类名的；你也可以直接使用完整类名，如：android.view.View v = null; 这样就可以不用导入；

注意：
如果你使用了某个类，又没有导入他的完整包，那么就会出错。


【fn 加载mjava模块】
用法：
//加载a.mjava模块
fn("a");

//然后就可以直接，调用a.mjava模块 的 abc 方法
abc();

说明：
用于加载你的模块文件；建议要加载的模块，载入事件加载一次即可。

注意：
加载多模块时，模块方法名，不要过分简单，避免重复；可在不同的模块，所有方法名加前缀或 后缀。


【syso 打印】
用法：
//打印字符串
syso("wo1314");
//或
i.syso("wo1314");

//打印字符串，字符串拼接
syso("wo" + "ai" + "ni");
//或
i.syso("wo" + "ai"+ "ni");

//拼接变量打印字符串
String a = "a";
String b = null;

syso("变量" + a + "等于" + b);


可以打印出数据，代码同等于 System.out.println("1314")，可以在测试时，选择 调试日志查看打印数据。

说明：
用于打印调试数据。

【if 判断语句】
用法：

//if 语句的使用
int a = 2;

if(a > 1){

syso("a大于1");

}


//if...else 搭配使用
int a = 2;

if(a == 1){

syso("a等于1");

}else if(a == 2){

syso("a等于2");

}else{

syso("a等于其他");

}


//多个if语句嵌套
int a = 1;
int b = 2;
if (a == 1){

if (b == 2){
syso("a等于1，b等于2");
}

}else{

syso("a不等于1");

}


//逻辑运算判断

int a = 1;
int b = 2;
//a等于1 或者 等于2
if(a == 1 || a == 2){

syso("a等于1");

}

//a等于1 并且 b等于2
if(a == 1 && b == 2){

syso("a等于1，b等于2");

}


//a不等于b 此运算符检测两个值是否相等，相等返回 false，否则返回 true

if (a != b){

syso("a不等于b");

}


【while 循环】
用法：
//这将循环10次
int a = 10;

while (a > 0){

syso(a);
a = a - 1;

}

说明：
条件循环语句，比较值的变化，然后进行循环执行里面的代码。当条件为“否”的时候会停止循环，条件“是”的话，将一直循环执行。
支持运算符（返回 是 与 否）：（跟 if 语句 一样，请参考）


【for 循环】
用法：
// 条件: a=初始值,最大值,增长值；a初始为1，设置a最大为10，a每次循环增加1
for(int i=0; i<10; i++){

syso("循环10/" + i);

}


// 提前跳出循环
for(int i=0; i<10; i++){

syso("循环10/" + i);
if (a == 6){
// break 语句跳出循环
break;
}

}


//设置一个数组
String[] data = {"a","b","c","d"};
//打印数组；a是每次循环数组值
for (String a : data){

syso(a + " 循环");

}

说明：
用于多次重复循环操作。


【t 新线程】
用法：
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){
syso("新线程里执行代码");
}
}
);

//或

i.t(
new OnThread(){
public void run(){
syso("新线程里执行代码");
}
}
);

说明：
启用新线程，去执行一些需要执行很久的代码。比如把下载文件，获取网页源码，大量的文件操作，可以放入新线里执行。这里线程的概念，启用新的线程帮你处理代码，这样不会影响到主线程。


【ssj 设置或修改控件事件代码】
用法：
//设置控件ID3，的单击事件
i.ssj(3, "clicki",
new android.view.View.OnClickListener() {
public void onClick(android.view.View v) {
syso("ok");
}
}
);


说明：
输入控件Id，输入事件类型，并将事件顺序填写在 { 中 }，动态控件将触发该事件代码。

事件类型：
clicki=单击事件


【tw 提示】
用法：
tw("你好");
//或
i.tw("你好");

//设置参数1：显示的时间长久；0：显示的时间短暂；\n为换行的意思，其他地方通用
tw("你好\n吗？", 1);
//或
i.tw("你好\n吗？", 1);


说明：
用于提醒用户，界面显示时长大约为 2秒钟。弹出代码中的文字，来提醒用户。


【fd 删除文件】
用法：(将删除SD卡根目录的abc.zip文件)
boolean b = i.fd("%abc.zip");

syso(b);

说明：
用于删除指定的文件，是否成功返回数据：true或 false


【fe 文件是否存在】
用法：(将判断SD卡根目录的abc.zip文件是否存在)
boolean b = i.fe("%abc.zip");

syso(b);

说明：
用于判断指定的文件存在，是否存在返回数据：true或 false


【fs 文件大小】
用法：(将获取SD卡根目录的abc.zip文件占用的大小)
long b = i.fs("%abc.zip");

syso(b);

说明：
用于判断指定的文件存在，是否存在返回数值单位(字节)。
转换为KB：
long b = i.fs("%abc.zip");
double b2 = b / 1024;
syso(b2);

转换为MB：
double b2 = b / 1024 / 1024;


【fr 读取文本】
用法：(将读取SD卡根目录的abc.txt文件里面的内容)
String b = i.fr("%abc.txt");
syso(b);

String b = i.fr("%abc.txt", "utf-8")
syso(b);

说明：
用于读取文本文件的数据内容。
……

【fc 复制文件】
用法：（在SD卡根目录abc.txt文件拷贝一个新的副本至abc2.txt）
boolean b = i.fc("%abc.txt", "%abc2.txt");
syso(b);

//设置重复不覆盖
boolean b = i.fc("%abc.txt", "%abc2.txt", false);
syso(b);

//将apk包内的 abc.txt 复制到SD卡上
boolean b = i.fc("@abc.txt", "%abc2.txt");
syso(b);

说明：
用于复制文件，创建一个新的副本文件。是否成功返回数据：true或 false
……


【fw 写入文本】
用法：(将文本数据写入至SD卡根目录的abc.txt文件里面)

String b = "我是一个txt文件的内容";
i.fw("%abc.txt", b);

String b = "我是一个txt文件的内容";
i.fw("%abc.txt", b, "utf-8");

说明：
用于写入文件。



【fl 文件列表】
用法：（获取一个目录的文件列表）
Object[] c2 = i.fl("%dir");
for (Object a : c2){

syso("文件 " + a);

}


//仅获取文件夹
Object[] c2 = i.fl("%.estrongs", false);
for (Object a : c2){

syso("文件 " + a);

}


//仅获取文件
Object[] c2 = i.fl("%.estrongs", false);
for (Object a : c2){

syso("文件 " + a);

}

说明：上面例子是获取sd卡根目录文件夹“dir”里面的所有子目录以及文件，并获取结果返回变量“c”，并用用符合 # 获取变量c 有多少位，然后循环来读取变量“c”里面的列表数据，可以通过c[0]获取第一位，c[1]第二位数据等。

提示：
看似有些复杂，理解了就简单了， 这里的变量“c”类型是一个数组，里面包含了一个数据列表。通过循环可以顺序读取这个列表。


【ft 转移文件】
用法：（将SD卡根目录的abc.txt转移至abc3.txt）
boolean c = i.ft("%abc.txt", "%abc3.txt");
syso(c);

说明：
用于转移文件。是否成功返回数据：true或 false


【fdir 获取SD卡根目录路径】
用法：（获取根目录路径并赋值至变量“a”）
//获取根目录
String a = i.fdir();
syso(a);

//获取目录的绝对路径
String a = i.fdir("%dir");
syso(a);

说明：
通过获取根目录路径，就可以计算文件的绝对路径。


【fuz 解压zip部分文件】
用法：（将根目录文件abc.apk压缩包里的AndroidManifest.xml文件，解压到根目录AndroidManifest2.xml）
int d = i.fuz("%abc.apk", "AndroidManifest.xml", "%AndroidManifest2.xml");
syso(d);

//解压文件遇到重复不覆盖
int d = i.fuz("%abc.apk", "AndroidManifest.xml", "%AndroidManifest2.xml",false);
syso(d);

说明：
通过上面代码可以实现压缩包解压部分的文件，并返回赋值至变量“d”解压文件的数量。


【fuzs 解压整个zip】
用法：(将根目录文件abc.apk压缩包解压至根目录文件夹abcdir，会自动创建)

boolean c = i.fuzs("%abc.apk", "%abcdir");
syso(c);

//解压文件遇到重复不覆盖
boolean c = i.fuzs("%abc.apk", "%abcdir", false);
syso(c);

说明：
通过上面代码将解压整个压缩包至指定文件，并赋值至变量“c”，是否成功返回数据：true或 false


【fj 压缩文件或文件夹至zip】
用法：
boolean c = i.fj("%adc.txt", "%abc.zip");
syso(c);

//不去除根目录
boolean c = i.fj("%adc.txt", "%abc.zip",false);
syso(c);

说明：
压缩文件。返回赋值数据：true 或 false


【fo 打开文件】
用法：（将根目录打开安装abc.apk文件）
i.fo("%abc.apk");

说明：
可以调用系统工具打开不同的文件。


【sr 替换字符】
用法：
String a = "123456789";
String b = "456";
String c = ".";
String d = i.sr(a, b, c);
//将提示：123.789
syso(d);

//支持正则表达式
//String d = i.sr(a, b, c, true);

说明：
用于替换字符


【sj 截取字符】
用法：
String a = "123456789";
String b = "34";
String c = "8";
String d = i.sj(a, b, c);
//将提示：567
syso(d);

//从头部开始截取
String d = i.sj(a, null, c);
syso(d);

//截取到尾部
String d = i.sj(a, b, null);
syso(d);

说明：
用于截取数据部分字符


【sl 数据数组】
用法：
String a = "12;12;12;12;12";
String b = ";";
String[] c2 = i.sl(a, b);

//可以支持正则表达式；例子看（注意说明）
//String[] c2 = i.sl(a, b, true);

for(String a : c2)
{
syso(a);
}

说明：
将把变量“a”的字符串，切割成一个数组，以字符“.”为分割字符。并用循环顺序打印出数据。

注意：
如果支持正则表达式数据数组，上例子的 String b = ";" 其内的值。需要转义的特殊字符 “$()*+.[]?\^{},|”

支持正则的特殊字符转义方法：
如：
String a = "12|a$12|a$12|a$12|a$12";

//关键分割字符串如果包含特殊字符，需要在每个特殊字符前面增加“\\”进行转义
String b = "\\|a\\$";
String[] c2 = i.sl(a, b, true);
for(String a : c2)
{
syso(a);
}


【siof 获取字符位置】
用法：
String a = "123456789";
String b = "3";
int c = 0;
int d = i.siof(a, b, c);
//将提示：2
syso(d);

String a = "123456789";
String b = "3";
int d = i.siof(a, b);
//将提示：2
syso(d);

说明：
从前面向后面进行匹配。字符位置以0计算，若无数据找到将返回 -1


【slof 获取字符位置】
用法：
String a = "123456789";
String b = "4";
int c = 8;
int d = i.slof(a, b, c);
//将提示：3
syso(d);

String a = "123456789";
String b = "4";
int c = i.slof(a, b);
//将提示：3
syso(c);

说明：
从后面向前面进行匹配。字符位置以0计算，若无数据找到将返回 -1


【ssg 截取字符】
用法：
String a = "abcdefghijk";
String b = i.ssg(a, 2, 6);
//将提示：cdef
syso(b);

String a = "abcdefghijk";
String b = i.ssg(a, 6);
//将提示：ghijk
syso(b);

说明：
根据字符的位置进行截取字符，若失败将变量“b”赋值 null


【slg 获取字符长度】
用法：
String a = "123456789";
int b = i.slg(a);
//将提示：9
syso(b);

说明：
顾名思义。


【strim 去除头尾空格】
用法：
String a = "   123456789 ";
String b = i.strim(a);
//将提示:123456789
syso(b);

说明：
常用于去除后进行判断头尾字符。

【slower 转换为小写】
用法：
String a = "AiufSUscN";
String b = i.slower(a);
//将提示:aiufsuscn
syso(b);

说明：
常用于转换为小写后进行判断。

【supper 转换为大写】
用法：
String a = "AiufSUscN";
String b = i.supper(a);
//将提示:AIUFSUSCN
syso(b);

说明：
常用于转换为大写后进行判断。


【stop 暂停代码】
用法：
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

syso("1");

i.stop(1000);
syso("2");

i.stop(1000);
syso("3");

i.stop(1000);
syso("4");

}
}
);

说明：
每次执行 i.stop(1000) 将暂停1秒后，再执行下面代码。单位为毫秒：1000毫秒 = 1秒


【sran 生产范围随机数】
用法：（生产一个 100 至 1000的随机数）
int a = i.sran(100, 1000);
syso(a);

说明：
有时候需要利用到随机机制，可以利用这个来开发！


【hs 获取网页源码】
用法：
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "https://m.baidu.com/";
String b = i.hs(a);
syso(b);

}
}
);

2，提交post数据:
输入说明：地址，post数据提交，目标网页编码
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "https://m.baidu.com/";
String b = i.hs(a, "title=你好&text=你好吗？", "utf-8");
syso(b);

}
}
);

3，带自定义cookie方式获取网页:
//传递cookie项值，格式为nama=value 下例： uid=112;name=nihao;sb=123456789;
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "https://m.baidu.com/";
String b = i.hs(a, "title=你好&text=你好吗？", "utf-8", "uid=112;name=nihao;sb=123456789;");
syso(b);

}
}
);

4，带自动设置cookie方式获取网页，并记录当前网页的Cookie:
//传递cookie项值，当自定义为null 系统将自动设置已记录的cookie
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "https://m.baidu.com/";
String b = i.hs(a, "title=你好&text=你好吗？", "utf-8", null, true);
syso(b);

}
}
);

5，带自动设置cookie方式获取网页，并记录当前网页的Cookie，并设置Header头:（可设置多条，以“||”隔开）文件头包括了Cookie，User-Agent设备型号。
//传递cookie项值，当自定义为null 系统将自动设置已记录的cookie
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "https://m.baidu.com/";
String b = i.hs(a, "title=你好&text=你好吗？", "utf-8", null, true, "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept=*/*||accept-language=zh-CN");
syso(b);

}
}
);

6，带自动设置cookie方式获取网页，并记录当前网页的Cookie，并设置Header头:（可设置多条，以“||”隔开）文件头包括了Cookie，User-Agent设备型号，设置连接超时，设置接收超时，设置代理IP。
//传递cookie项值，当自定义为null 系统将自动设置已记录的cookie
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "https://m.baidu.com/";
String b = i.hs(a, "title=你好&text=你好吗？", "utf-8", null, true, "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept=*/*||accept-language=zh-CN", 20000, 20000, "10.0.0.172:80");
syso(b);

}
}
);

7，应用系统存储Cookie的浏览查看，返回赋值变量为字符串
String b = i.hs("cookie");

8，应用系统存储Cookie的清空，无赋值变量
i.hs("del cookie");

说明：
这里先开了一个线程，然后在线程里执行获取网页源码的工作，开线程是担心有些主线程界面。大部分网页都需要使用cookie登陆，可使用工具查询所需cookie然后进行操作。
设置cookie有说明作用？
1.登陆用户名
2.获取验证码图片并发送验证码
....


【hd 下载文件】
用法：（下载文件至SD卡根目录 abc.apk）

1，下载文件，默认不覆盖重复
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "http://abc.com/abc.apk";
String b = "abc.apk";
int c = i.hd(a, b);
syso(c);

}
}
);

2，设置重复是否覆盖
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "http://abc.com/abc.apk";
String b = "abc.apk";
int c = i.hd(a, b, true);
syso(c);

}
}
);


3，带自动设置cookie方式下载网页形式文件（如图片形式验证码，论坛的附件等），支持post数据，自定义Cookie或系统设置Cookie，并记录当前网页的Cookie，并设置重复是否覆盖。可参考hs获取网页，并设置Header头:（可设置多条，以“||”隔开，也可留空为null）
输入说明：下载地址，保存文件位置，是否重复覆盖，post数据提交，目标网页编码，自定义Cookie，是否系统自动设置Cookie，设置Header头
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "http://abc.com/abc.apk";
String b = "abc.apk";
int c = i.hd(a, b, true, "title=你好&text=你好吗？", "utf-8", null, true, null);
syso(b);

}
}
);

说明：
开个线程，然后在里面下载一个文件。并存到SD卡。下载结果将赋值到变量“c”
返回的赋值：
1 文件已经存在
0 下载成功
-1 下载失败


【hw 访问网页】
用法：
String a = "https://m.baidu.com/";
i.hw(a);

说明：
使用内置浏览器访问网页。
可用于下载文件：
String a = "http://abc.com/abc.apk";
hw(a);


//跳转访问网页，并且自定义标题栏颜色
//主体颜色
String b = "#387bd6";
//底部横杠颜色
String c = "#255eab";
i.hw("https://m.baidu.com/", b, c);

【hws 系统浏览器访问网页】
用法：
String a = "https://m.baidu.com/";
i.hws(a);

说明：
使用内置浏览器访问网页。
可用于下载文件：
String a = "http://abc.com/abc.apk";
i.hws(a);


【ug 获取控件属性】
用法：(1为：控件ID，第二个参数为控件属性标识，然后赋值到变量)
String a = i.ug(1, "text");
syso(a);

说明：
输入属性标示来返回不同的控件数据。注意：有些控件没有指定属性，将返回null。下面有属性介绍，可参考。

可用属性标识：
text=内容、background=背景、width=宽度、height=高度、x=X轴、y=Y轴、paddingleft=左内边距、paddingtop=顶内边距、paddingright右内边距、paddingbottom=底内边距、layout_marginleft=左外边距、layout_margintop=顶外边距、layout_marginright=右外边距、layout_marginbottom=底外边距、
hint=提示字符、imeoptions=虚拟键盘按键状态、visibility=控件可视状态、checked=选项是否被选中、title=浏览器网页标题、url=浏览器网址、lastvisibleposition=列表滑动到项目位置的序号、count=列表项目总数、
selecteditem=获取下拉框选值、rating=评分当前数值、progress=控件当前进度数值、date=日期控件选值、time=时间控件选值、currentitem=获得滑动窗体界面序号、isdraweropen=侧滑是否界面展开状态、selectionstart=获取文本框光标开始位置、selectionend=获取文本框光标结束位置、
cangoback=是否存在可返回的网页、cangoforward=是否存在可前进的网页、collapsecolumns=表格布局获取指定列是否折叠、shrinkcolumns=表格布局获取指定的列是否可收缩、stretchcolumns=表格布局获取指定的列是否可拉伸、shrinkcolumnsall=表格布局获取指示是否所有的列都是可收缩的、
stretchcolumnsall=表格布局获取指示是否所有的列都是可拉伸的



【us 设置控件属性】
用法：(1为：控件ID，第二个参数为控件属性标识，第三个是需要设置的数据或变量)

//设置文本控件内容
String c = "文本内容";
boolean f = i.us(1, "text", c);
syso(f);


//设置浏览器的连接url
String c = "https://m.baidu.com/";
boolean f = i.us(2, "url", c);
//提示：如果浏览器正在播放视频或音乐，直接关闭浏览器可能还会有声音，建议关闭浏览器时先跳转成另一个网页。
//提示：如果需要加载本地的文件，可以 us(2, "url", "file:///android_asset/res/web.html") 加载安装包内assets/res/web.html文件
syso(f);


//设置浏览器显示的html文件或文本
String c = "<html><p>html内容</></html>";
String d = "utf-8";
String e = "text/html";
boolean f = i.us(2, "url", c, d, e);
syso(f);


//设置控件阴影（部分控件有效果如文本、文本框、按钮）
int radius = 5;
int dx = 0;
int dy = 0;
String color = "#000000";
boolean f = i.us(2, "shadow", radius, dx, dy, color);
syso(f);


//带有赋值变量，将返回数据是否设置成功 true 或 false
String c = "文本内容";
i.us(1, "text", c);

//设置文本框控件光标
i.us(1, "selection", 1);

//选中文本框部分内容
i.us(1, "selection", 1, 3);

//浏览器前进1个网页
i.us(1, "gobackorforward", 1);

//浏览器后退1个网页
i.us(1, "gobackorforward", -1);

//设置控件点击波纹效果颜色；需系统5.0以及以上才有效果；部分控件还需要设置 clickable=true 才有效果。
i.us(1, "backgroundripple", "#888888");

//设置编辑框光标颜色
i.us(1, "textcursordrawable", "#000000");

说明：
输入控件标示设置控件数据。【可参照控件属性，所有属性标识通用】

更多属性标识：
currentitem=设置滑动窗体界面序号、closedrawer=关闭指定侧滑、opendrawer=展开指定侧滑、drawerlockmode=设置手势滑动、selection=设置文本框光标位置、gobackorforward=浏览器的前进或推后、backgroundripple=波纹效果、dh=执行动画（非队列动画）


【uigo 跳转界面】
用法：（输入界面文件名，跳转指定的界面）
i.uigo("abc.ijava");

//带参数的跳转
i.uigo("abc.ijava", 536870912);


说明：
可以界面之间的转换，扩展新的界面。

参数：
67108864：如果在内存中发现存在该界面，则清空这个界面之上的所有其他界面，使其处于栈顶。
268435456：系统会寻找或创建一个新的内存来放置该界面
1073741824：跳转到的界面，不排在内存中
536870912：当内存中存在该界面并且位手机的显示状态时，不再创建一个新的，直接利用这个界面。


【utw 弹出界面】
用法：（在原有的界面弹出界面）
String a = null;
String b = "界面标题";
String c = "界面内容";
String d = "退出";
String e = "保存";
String f = "取消";

//三个按钮
//输入图标，输入标题，输入内容，输入按钮名称，输入按钮名称，输入按钮名称，输入是否点击弹窗以外界面是否关闭弹窗
i.utw(a, b, c, d, e, f, false,

new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了确定");
}
}
,
new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了保存");
}
}
,
new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了取消");
}
}
);

//两个按钮
i.utw(a, b, c, d, e, false,
new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了确定");
}
}
,
new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了取消");
}
}
);

// 一个按钮
i.utw(a, b, c, d, false,
new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了确定");
}
}
);


//没有按钮
i.utw(a, b, c, false);

//将界面添加到弹窗界面上，直接将界面内容设为一个界面文件
String a = "界面标题";
String b = "a.ijava";
String c = "取消";

//返回界面对象，设置为全局变量 v
android.view.View v = i.utw(null, a, b, c, false, 
new DialogInterface.OnClickListener(){
public void onClick(DialogInterface d,int w){

syso("点击了取消");

//获取全局变量 v
android.view.View v = is("v");
syso(v);
}
}
);


说明：
常用于询问用户当前的操作，弹窗展示内容。

赋值变量说明：
弹出界面需要赋值到一个变量，用于自定义界面弹窗的操作。


【endutw 关闭弹出界面】
用法：
i.endutw();

说明：
用于关闭当前打开的弹窗界面

【end 结束界面】
用法：
i.end();

说明：
调用后，将结束当前的界面。 并返回原来的界面。如果原来没有界面，将退出应用。

【ends 显示桌面】
用法：
i.ends();

说明：
跳转到手机的桌面，程序将后台运行。


【bfm 播放音频】
用法：
String a = "http://www.abc.com/abc.mp3";
android.media.MediaPlayer b = i.bfm(a);

String a = "%abc.mp3";
android.media.MediaPlayer b = i.bfm(a);
// 播放
// i.bfms(b, "st");
// 暂停
// i.bfms(b, "pe");
// 停止
// i.bfms(b, "sp");
// 结束播放组件
// i.bfms(b, "re");
// 是否在播放
// i.bfms(b, "ip", c);
// tw(c);

// 获取音频时长（毫秒）
// i.bfms(b, "dn", c);
// tw(c);
// 获取当前播放时长（毫秒）
// i.bfms(b, "cn", c);
// tw(c);

// 指定播放的位置（毫秒）
// i.bfms(b, "seekto", 2000);

// 设置音量（0-100）
// i.bfms(b, "volume", 100, 100);

// 一直循环播放
// i.bfms(b, "sl", true);

说明：
可以直接访问安装包里面的音频文件，也可以访问sd卡上的。
……

【html 标签支持】
用法：
String a = "(html)<a href="https://m.baidu.com">百度</a>";
i.us(1, "text", a);

说明：
text属性：设置支持html代码！


【ula 列表操作内容】
用法：
//输入数据列表对象，输入数据项...不限制数量。
Object a = null;
a = i.ula(a, new Object[]{1,2,3}, new Object[]{"abc","bac","bbc"});

//刷新列表显示内容，常用增加数据后的刷新。
i.ula(a);

//V7列表，刷新指定序号列表项目显示内容，常用增加数据后的刷新。
i.ula(a, 2);

//清空列表对象
i.ula(a, null);
//i.ula(a, "clear");

//获得列表对象，赋值返回v变量为列表对象
Object v = i.ula(a, "list");

说明：
根据数据列表，进行增加数据。

提示：
1 abc，其中1为控件id，abc为设置控件值
其中所谓的控件，为a.iyu界面中的控件。
增加标识数据，不作为设置控件数据，可在标识处设负数。如下：
-1 abc

提示：
如果需要设置 单选控件、多选控件 的选择状态，可设值为 true 或 false

注意：
将要执行事件的控件，必须在此设置值。如你有一个按钮控件无需设置值，但需要使用事件，可设置 1=null
不设置值的控件，将无法获取列表内容数据。

【uls 列表显示内容】
用法：
//
Object a = null;
a = i.ula(a, new Object[]{1,2,3}, new Object[]{"abc","bac","bbc"});
a = i.ula(a, new Object[]{1,2,3}, new Object[]{"cde","cdw","cad"});
String c = "a.ijava";
int d = -1;
int e = -2;
//输入控件id或控件对象，输入数据列表，输入列表项界面文件名，输入界面宽度，输入界面高度
i.uls(1, a, c, d, e);

//设置下拉选择列表
//输入控件id或控件对象，输入数据列表或数组数据
i.uls(1, new Object[]{"abc","bac","bbc"});

说明：
设置列表控件、视图控件、下拉列表的数据。

注意：
列表控件、视图控件 设置的界面 a.ijava 其中的载入事件是允许被调用。
可以通过列表控件、视图控件 设置的界面 a.ijava 的载入事件，进行每项列表布局的个性化设计。
每当显示到每项列表内容就会调用一次此载入事件，并且将该项的布局控件赋值给 st_vW 变量对象，
然后可以通过  i.gvs(st_vW, "a.2") 获取其中的子控件对象，然后进行操作子控件即可。
还可以通过 st_pN 获取当前的视图中的序号，方便判断目前操作的是那一个视图。


【ulag 获取列表内容数据】
用法：

//输入当前的控件对象，输入获取控件ID 1的数据参数
Object b = i.ulag(a, 1);

//输入当前的控件对象，输入获取标识为 -1的数据参数
Object b = i.ulag(a, -1);

//通过 数据列表对象 或 列表控件对象 获取数据
//输入数据列表对象 或 列表控件对象，输入视图中的位置序号，输入获取标识为 -1的数据参数
Object b = i.ulag(a, 1, -1);


说明：
常用与在列表控件的事件中，获取参数数据与用户进行互动。获取失败将赋值变量为 null

注意：
使用此方法在uls中设置控件参数后，有设置参数的控件，在事件中可使用此方法。

【ulas 更新列表内容数据】
用法：

//输入当前的控件对象，输入获取控件ID 1的数据参数
Object b = i.ulas(a, 1);

//输入当前的控件对象，输入获取标识为 -1的数据参数
Object b = i.ulas(a, -1);

//通过 数据列表对象 或 列表控件对象 获取数据
//输入数据列表对象 或 列表控件对象，输入视图中的位置序号，输入获取标识为 -1的数据参数，输入新的数据
Object b = i.ulas(a, 1, -1);

//刷新列表显示内容，常用增加数据后的刷新。
i.ula(a);

说明：
常用与更新修改列表内容数据。修改数据后，别忘记刷新列表。

【usms 发送短信】
用法：
String a = "10086";
String b = "0";
i.usms(a, b);

注意:测试时只显示syso日志，不直接 发送短信，打包即可。

【ucall 拨打电话】
用法：
String a = "10086";
i.ucall(a);

注意:测试时只显示syso日志，不直接 拨出号码，打包即可。

【time 当前时间】
用法：
String a = "0";
String b = i.time(a);
syso(b);

说明：
第一个参数为时间类型，第二个赋值变量

[数字类型]
0：2014-07-07 09:10:08
1：2014/07/07 09:10:08
2：2014-07-07
3：09:10:08
4：18144133553151
5：2014年07月07日 09:10:08
[字符类型，输入字符形式需引号概括]
Y 年
m 月
d 日
H 时
M 分
S 秒
a/A 星期几


【fi 判断路径是否文件夹】
用法：
String a = "abc";
boolean b = i.fi(a);
syso(b);

说明：
指定路径，判断是否为目录文件夹，返回：true 或 false


【swh 获取屏幕分辨率】
用法：
String a = "w";
//获取屏幕宽度的dp
int w = i.swh(a);

String a = "h";
//获取屏幕高度的dp
int h = i.swh(a);

String a = "hh";
//获取屏幕真实高度的dp
int w = i.swh(a);

String a = "pxw";
//获取屏幕宽度的px像素
int w = i.swh(a);

String a = "pxh";
//获取屏幕高度的px像素
int h = i.swh(a);

String a = "pxhh";
//获取屏幕真实高度的px像素
int hh = i.swh(a);

String a = "pxztl";
//获取屏幕状态栏高度的px像素
int h = i.swh(a);

String a = "pxbvk";
//获取屏幕底部虚拟键盘的高度的px像素
int h = i.swh(a);

说明：
常用于获取屏幕的大小。

真实高度：不去除其他系统界面所占用（如状态栏）



【stobm 汉字转换编码字符】
用法：（你 转换 %E4%BD%A0）
String b = i.stobm("你", "utf-8");
tw(b);

说明：
有些时候网络操作的时候，网址需要带有字符参数，就可以把这个汉字转换下。

【sutf8to 将UTF-8编码字符转换中文】
String b = i.sutf8to("%E4%BD%A0");
tw(b);

【uycl 隐藏状态栏】
用法：
//隐藏
i.uycl(true);
//不隐藏
i.uycl(false);

// 将状态栏文字设为暗色，输入整数
i.uycl(1)

// 将状态栏文字设为亮色，输入整数
i.uycl(0)

// 进入全屏效果
i.uycl(-1)

// 退出全屏效果
i.uycl(-2)

// 隐藏底部导航条
i.uycl(-3)

// 不隐藏底部导航条
i.uycl(-4)

说明：
隐藏手机顶部的状态栏

【uycl 修改状态栏颜色】
用法：
//输入更变颜色，并且保留状态栏空间
i.uycl("#50c4e5", true);

//输入更变颜色，并且不保留状态栏空间
i.uycl("#50c4e5", false);

//输入更变颜色，并且保留状态栏空间，只设置状态栏，不设置软键盘
i.uycl("#50c4e5", true, 0)

//输入更变颜色，并且保留状态栏空间，只设置软键盘，不设置状态栏
i.uycl("#50c4e5", true, 1)

说明：
常用与设置一体化颜色，以及更变不同的状态栏颜色。

注意：
仅系统android 4.4以及以上才有效果，系统android 5.0以及以上效果更佳！
android 4.4以下的系统，无效果！

【ushsp 设置横屏或竖屏】
用法：
//横屏
i.ushsp(true);
//竖屏
i.ushsp(false);

说明：
设置屏幕的显示方式，注意的是设置后载入事件将重新执行


【bfv 播放视频】
用法：(播放SD卡上的视频文件)
String a = "%abcd.mp4";
i.bfv(a);

//并且横屏
String a = "%abcd.mp4";
boolean b = true;
i.bfv(a, b);


//并且横屏
String a = "http://m.baidu.com/abcd.mp4";
boolean b = true;
i.bfv(a, b);
说明：
此方法将全屏播放SD卡上的视频文件。调用自带的播放器。

注意：
不支持加载assets文件。支持SD卡文件、应用私有文件、（http）远程网络文件！

支持格式：
3gp、MP4、avi


【ftz 发送通知栏】
用法：
i.ftz("提醒标题", "标题", "内容", null, [[
tw("点击了")
]]);

//设置显示图标
i.ftz("提醒标题", "标题", "内容", "%abc.png", [[
tw("点击了")
]]);

说明：
可以用于通知用户。


【uapp 打开App应用或游戏】
用法：
boolean c = i.uapp("com.iapp");

//或 带有指定类名的启动
boolean c = i.uapp("com.iapp", "com.yougaile.MakeiApp.logoActivity");

说明：
输入应用包名，赋值变量； 赋值变量返回启动结果：true 或 false

【uapplist 获取App列表】
用法：
Object[] b = i.uapplist(true);
Object c = b[1];
syso(c[0]);

说明：
输入 是否包括获取系统App，返回一个列表数组 至变量 “b”，每列数据将存储一个应用的信息，并且以数组方式储存。

其中列数组内容序列：
0应用包名，1启动类，2应用标题，3应用版本


【uapplistgo 获取正在运行的App列表】
用法：
Object[] b = i.uapplistgo();
syso(b[0]);

说明：
输入 返回一个列表数组 至变量 “b”，每列数据将存储一个应用的信息，并且以 “\n”隔开。

其中列内容格式：
应用包名，pid, uid

【uninapp 卸载应用】
用法：
i.uninapp("com.iapp");

说明：
输入应用包名


【huf 上传文件】
用法：
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){
String a = "http://abc.com/upfile.php";
String b = "filename=iApp我的应用.apk&test=一款非常好的应用哦";
String c = "%abc/iApp.apk";
// 支持多文件上传
//String c = "%abc/iApp.apk|%abc/iApp2.apk|%abc/iApp3.apk"
String d = "utf-8";
String e = i.huf(a, b, c, d);
syso(e);

}
}
);

2.设置 header文件头，文件头包括了Cookie，User-Agent设备型号。。
i.t(
new com.iapp.interfaces.OnThread(){
public void run(){

String a = "http://abc.com/upfile.php";
String b = "filename=iApp我的应用.apk&test=一款非常好的应用哦";
String c = "%abc/iApp.apk";
// 支持多文件上传
//String c = "%abc/iApp.apk|%abc/iApp2.apk|%abc/iApp3.apk"
String d = "utf-8";
String e = "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||Cookie=aa=123;bb=456;||accept-language=zh-CN";
String f = huf(a, b, c, d, e);
syso(f);

}
}
);

说明：
输入 http接口，表单内容，手机内存选择文件，接口的网页编码， 赋值变量。 返回网页内容将赋值给变量 “e”


【nvw 创建动态控件】
用法：
//将控件添加至指定的控件作为子控件
//输入要添加的控件ID或控件对象，输入添加至指定控件ID或控件对象
i.nvw(id, did);

//输入要添加的控件ID或控件对象，输入添加至指定控件ID或控件对象，输入插入指定序号
i.nvw(id, did, 0);

//创建文本控件
//输入控件ID，输入添加至指定控件ID或控件对象（若不添加则输入null），输入控件类型，输入控件属性
int id = 123456;
int did = 1;
android.view.View b = i.nvw(id, did, "文本", "width=-2\nheight=-2\ntext=内容");

说明：
输入创建的控件ID，输入将新控件添加至指定控件ID或控件对象，创建控件的类型，创建控件的属性


【uall 获取子控件】
用法：
//输入控件ID或控件对象，输入false时将赋值子控件ID，输入赋值变量将返回一个数据列表
Object[] a = i.uall(1, false);

//输入控件ID或控件对象，输入true时将赋值子控件对象，输入赋值变量将返回一个数据列表
Object[] a = i.uall(1, true);

tw(a[0]);

说明：
获取一个包含子控件的，控件中所有的子控件。

【urvw 移除控件】
用法：
i.urvw(3);

说明：
输入需要移除的控件ID或控件对象


【sbp 图像分割】
用法：
//载入一个图像变量，并赋值到图像变量“b”
Bitmap b = i.sbp("%1.png");

//载入一个用户图标，{裁剪图像区域（像素）：x坐标:80，y坐标:90，裁剪宽度:50，裁剪高度:60}
//并将裁剪好的赋值到图像变量“b”
Bitmap b = i.sbp("%1.png", 80, 90, 50, 60);

//载入一个SD卡上的图标，{裁剪图像区域（像素）：x坐标:80，y坐标:90，裁剪宽度:50，裁剪高度:60}，图像旋转图像:180度
//并将裁剪好的赋值到图像变量“b”
Bitmap b = i.sbp("%1.png", 80, 90, 50, 60, 180);

说明：
三种方式载入图像，从图像变量，从用户图标，从SD上图标；并可设置裁剪图片；可设置图像旋转； 并赋值到新的图像变量；


【bfs 保存图像】
用法：
i.bfs(b, "%1.jpg");

//或 压缩比例（1至100）
i.bfs(b, 70, "%1.jpg");

说明：
输入图像变量，输入压缩比例（1至100），输入保存图像的路径，图像将保存至该路径。


【sdeg 启动调试模式】
用法：

i.sdeg(0);
i.sdeg(1);
i.sdeg(2);

说明：
提示日志方式。0打包后没有任何提示，1打包后可任然打印错误，2打包后记录日志保存至文件 iApp/Log


【tot 获取控件图标】
用法：
int id = 4;
Bitmap b = i.tot(id);

说明：
输入控件ID或控件对象，返回将赋值“b”图像变量。注：此方法仅限于 图片控件，图标按钮控件。


【tzz 图像旋转】
用法：
Bitmap a = i.sbp("%1.png");
int b = 90;
Bitmap c = i.tzz(a, b);

说明：
输入被旋转图像变量，输入旋转度数（逆向旋转数为负数），返回将赋值“c”图像变量。


【tsf 图像缩放】
用法：
Bitmap a = i.sbp("%1.png");

//按照倍增缩放，值小于则为缩小，否则为放大
int b = 2;
Bitmap c = i.tsf(a, b);

//指定高度与宽度缩放
int w = 100;
int h = 200;
Bitmap c = i.tsf(a, w, h);

说明：
输入被缩放图像变量，输入缩放倍数 或 指定图像高度与宽度缩放，返回将赋值“c”图像变量。


【tfz 图像反转】
用法：
Bitmap a = i.sbp("%1.png");
//水平反转
String b = "x";
Bitmap c = i.tfz(a, b);

//垂直反转
String b = "y";
Bitmap c = i.tfz(a, b, c);

说明：
输入被反转图像变量，输入反转方式 x为水平 y为垂直，返回将赋值“c”图像变量。

【tcc 获取图像变量尺寸】
用法：
Bitmap a = i.sbp("%1.png");
String b = "w";
Bitmap c = i.tcc(a, b);
syso(c);

String b = "h";
Bitmap c = i.tcc(a, b);
syso(c);

说明：
获取图像变量的 w宽度 和 h高度。

【sxb 写入剪切板】
用法：
String a = "nihao";
i.sxb(a);

说明：
可用于复制到剪切板，其他应用可获取到此数据。

【shb 获取剪切板】
用法：
String a = i.shb();
syso(a);

说明：
可获取剪切板数据，得到其他地方写入的剪切板数据。

【usjxm 手机休眠】
用法：
i.usjxm(false);

说明：
设置后手机将不休眠，不锁屏。默认为 true 需要休眠。


【bfvs 播放视频】
用法：

//设置SD卡视频文件
//输入控件ID或对象，输入视频文件路径
i.bfvs(1, "%a.mp4");

//设置网络远程视频文件
i.bfvs(1, "http://abc.com/a.mp4");

//增加控制器，c为赋值变量
Object c = i.bfvss(1, "media");
//开始播放
i.bfvss(1, "st");

说明：
自定义视频播放控件进行播放视频。

注意：
不支持加载assets文件。支持SD卡文件、（http）远程网络文件！

支持格式：
3gp、MP4、avi

【bfvss 播放视频控制】
用法：
//开始播放
i.bfvss(1, "st");

//暂停播放
i.bfvss(1, "pe");

//停止播放
i.bfvss(1, "sp");

//定位到指定帧
i.bfvss(1, "seekto", 300);

//增加控制器，c为赋值变量
Object c = i.bfvss(1, "media");

//是否在播放
Object c = i.bfvss(1, "ip");
tw(c);

//获取视频时长（毫秒）
Object c = i.bfvss(1, "dn");
tw(c);

//获取当前播放时长（毫秒）
Object c = i.bfvss(1, "cn");
tw(c)

【addv 加载界面】
用法：
//界面中载入其他界面
int id = 1;
i.addv(id, "a.ijava");
i.addv(id, "b.ijava");

//侧滑窗体
int id = 1;
i.addv(id, "a.ijava|b.ijava");

//滑动窗体，将带有赋值变量。此处变量“b”赋值为根控件列表，先通过 gslist 访问指定序号的根控件。通过 gvs 指定的根控件访问指定ID的控件。
int id = 1;
Object b = i.addv(id, "a.ijava|b.ijava");

说明：
输入控件ID，输入界面名，输入辅助参数。可用将一个界面的控件，载入到指定控件作为子控件。

如何设置或获取属性上例 a.ijava 中的控件呢？
通过文件名作为对象，进行访问，如：

//注意：此对象的使用方式。
String b = i.ug("a.2", "text")
i.us("a.3", "text", "你好")

注意：
如果载入事件中使用 addv 滑动窗体进行绑定， 如果还需要给滑动窗体内的界面中的控件设置数据，需要将设置控件的代码写在 载入完毕事件 中。否将将可能设置数据失败。

注意：
若增加 侧滑窗体 与 滑动窗体 的子控件，需要在被载入的界面设计中，自设一个根目录，作为界面唯一根目录。


【gvs 获取控件对象】
用法：
//根据当前界面，来获取控件
//输入要获取的控件ID，输入赋值变量
android.view.View c = i.gvs(1);

// 输入0 则获取界面的根控件对象，是一个系统控件，如需获取自己的用户控件可通过 i.gvs(root, 1) 或 i.uall(root, true) 再次获取它的内部的子控件。
android.view.View c = i.gvs(0)

//根据控件对象，来获取内部的子控件
//输入控件ID或控件对象，输入要获取的控件ID，输入赋值变量
android.view.View c = i.gvs(1, 2);

// 输入0 则获取其父控件对象，这里获取了控件ID1的父对象
android.view.View c = i:gvs(1, 0)

说明：
常用与于利用根控件获取内部的子控件 或 获取控件对象。获取失败将赋值返回 null


【aslist 添加数据列表】
用法：
java.util.ArrayList<Object> a = null;
a = i.aslist(a, new Object[]{"你好", "你好", "你好"});
a = i.aslist(a, new Object[]{"你好2", "你好2", "你好2"});

//可插入数据到指定序号
a = i.aslist(a, {"你好3","你好3"，"你好3"}, 1);

说明：
输入列表对象，输入要添加的数据，输入插入指定序号。


【sslist 数据列表设置数据】
用法：
int b = 1;
String c = "数据";
i.sslist(a, b，c);


说明：
输入列表对象，输入指定数据序号，输入设置的数据

【gslist 数据列表访问数据】
用法：
int b = 1;
Object c = i.gslist(a, b);
syso(c);

说明：
输入列表对象，输入指定数据序号，输入赋值变量

【gslistl 数据列表访问数据总数】
用法：
int b = i.gslistl(a)
syso(b)

说明：
输入列表对象，输入赋值变量

【dslist 数据列表删除指定数据】
用法：
int b = 1;
i.dslist(a, b);

//清空所有数据
int b = -1;
i.dslist(a, b);

说明：
输入列表对象，输入指定数据序号

提示：
如果需要清空所有数据，[输入指定数据序号]可输入 -1 即会删除当前数据列表所有数据。

【gslistsz 列表数据转化为数组】
用法：
Object[] b = i.gslistsz(a);

说明：
输入列表对象，输入赋值变量

【gslistis 列表数据检查是否存在指定数据】
用法：
String b = "数据";
boolean c = i.gslistis(a, b);

说明：
输入列表对象，被判断的数据，输入赋值变量。赋值数据：true 或 false

【gslistiof 列表数据从头开始检查是否包含该数据】
用法：
String b = "数据";
int c = i.gslistiof(a, b);

说明：
输入列表对象，被判断的数据，输入赋值变量

【gslistlof 列表数据从尾开始检查是否包含该数据】
用法：
String b = "数据";
int c = i.gslistlof(a, b);

说明：
输入列表对象，被判断的数据，输入赋值变量


【nuibs 背景选择器】
用法：
//使用颜色作为背景
String pressed = "#333333";
String selected = "#333333";
String normal = "#888888";
Object b = i.nuibs(pressed, selected, normal);


//使用图像作为背景
String pressed = "%a.png";
String selected = "%a.png";
String normal = "%b.png";
Object b = i.nuibs(pressed, selected, normal);


//使用渐变颜色作为背景
.配置选中状态背景
int a = 0;
int b = 0;
String c = "#255779|#3e7492|#a6c0cd";
String d = "0";
String e = "topbottom";
Object pressed = i.ngde(a, b, c, d, e);

.配置正常状态背景
int a = 0;
int b = 0;
String c = "#255779|#3e7492|#a6c0cd";
int d = "0";
String e = "rightleft";
Object normal = i.ngde(a, b, c, d, e);

Object selected = pressed;

Object b = i.nuibs(pressed, selected, normal);

说明：
输入按下背景，输入选中背景，正常状态背景。


【ngde 背景调控器】
用法：
//输入圆角半径，输入背景填充色，输入赋值变量
int a = 15;
String b = "#888888";
Object c = i.ngde(a, b);

//输入边框宽度，输入背景填充色，输入边框颜色，输入赋值变量
int a = 5;
String b = "#888888";
String c = "#333333";
Object d = i.ngde(a, b, c);

//输入边框宽度，输入圆角半径，输入背景填充色，输入边框颜色，输入赋值变量
int a = 5;
int b = 15;
String c = "#888888";
String d = "#333333";
Object e = i.ngde(a, b, c, d);

//颜色渐变。输入边框宽度，输入圆角半径，输入背景填充渐变色组，输入边框颜色，输入颜色渐变方向，输入赋值变量
int a = 5;
int b = 15;
String c = "#255779|#3e7492|#a6c0cd";
String d = "#333333";
String e = "topbottom";
Object f = i.ngde(a, b, c, d, e);

说明：
背景空调生成的赋值变量，可配合背景选择器进行应用。

注意：
ngde 代码将赋值返回一个背景对象，此背景对象如果被多个不同大小的控件引用为背景。因为控件的大小不同，会导致此背景对象大小被修改。从而影响其他引用者控件。

提示：
边框与圆角半径 若不想调整，可设值为0 。适用于颜色渐变，不需要调节圆角半径和边框。

颜色渐变方向说明：
	topbottom：绘制从顶部梯度至底部
	trbl：借鉴右上角渐变左下角
	rightleft：绘制从右侧的梯度向左
	brtl：借鉴右下角渐变左上角
	bottomtop：绘制从底部梯度顶端
	bltr：借鉴渐变左下角到右上角
	leftright：绘制从左侧的梯度向右
	TL_BR：从绘制渐变的左上角到右下角

【sit 目标的设置】
用法：
//如，分享软件
//输入对象，输入属性标识，输入属性值
android.content.Intent a = null;
a = i.sit(a, "action", "android.intent.action.SEND");
a = i.sit(a, "type", "text/plain");
a = i.sit(a, "extra", "android.intent.extra.SUBJECT", "共享软件");
a = i.sit(a, "extra", "android.intent.extra.TEXT", "共享内容文本");
a = i.sit(a, "flags", 268435456);
i.uit(a, "chooser", "标题");

说明：
常用于调用系统程序以及功能 或 第三方程序功能。

可属性标识：action、type、extra、flags、data、classname、component

【uit 目标的执行】
用法：
//输入目标对象，输入属性，输入属性值
i.uit(a, "chooser", "标题");

//输入目标对象，输入属性，输入请求数值
i.uit(a, "result", 1);

//输入目标对象
i.uit(a);

说明：
常用于调用系统程序以及功能 或 第三方程序功能。

属性支持：chooser、result

【git 目标获取参数】
用法：
//输入目标对象，输入属性标识
Object c = i.git(a, "action")
Object c = i.git(a, "type")
Object c = i.git(a, "extra", "title")
Object c = i.git(a, "flags")

说明：
获取目标的属性。

【uqr 二维码扫描】
用法：

//扫描二维码
i.uqr();

//扫描结果，需要在 回调结果事件 写代码
if (st_sC == 1102){

Object c = i.git(st_iT, "extra", "result");
syso(c);

}


//生成二维码图像
String a = "https://m.baidu.com";
//输入字符串数据，输入图像长宽像素；将返回一个图像变量
Bitmap c = i.uqr(a, 400);


//识别二维码图像
//输入图像变量或图片路径；将返回一个字符串
String c = i.uqr(a);

说明：
常用于网络通用二维码扫描。

【zdp  dip转换px】
用法：
int dp = 10;
//输入dp数值
int c = i.zdp(dp);

说明：
用于常用数据转换。

【zpd  px转换dip】
用法：
int px = 10;
//输入px数值，输入赋值变量
int c = i.zpd(px);

说明：
用于常用数据转换。

【zps  px转换sp】
用法：
int px = 10;
//输入px数值，输入赋值变量
int c = i.zps(px);

说明：
用于常用数据转换。

【zsp  sp转换px】
用法：
int sp = 10;
//输入sp数值，输入赋值变量
int c = i.zsp(sp);

说明：
用于常用数据转换。

【lan 跳转界面动画】
用法：
i.uigo("abc.ijava");
//输入跳转界面动画的序号；6 右往左推出效果
i.lan(6);

说明：
用于跳转界面时候进行的动画效果

提示：
0.淡入淡出效果 1.放大淡出效果 2.转动淡出效果1 3.转动淡出效果2 4.左上角展开淡出效果 5.压缩变小淡出效果 6.右往左推出效果 7.下往上推出效果 8.左右交错效果 9.放大淡出效果 10.缩小效果 11.上下交错效果


【sjxx 获取设备信息】
用法：
Object[] a = i.sjxx();
syso(a[0]);

说明：
获取手机基本信息，将返回一个数组到赋值变量“a”，数组格式如下：

数据格式：（真实数据 \n 旁边将不没有空格）

CPU型号 \n CPU频率
屏幕宽度 \n 屏幕高度 \n 分辨率宽度 \n 分辨率高度
手机型号 \n 手机品牌 \n 手机SDK

【simsi 获取设备imsi】
用法：
String a = i.simsi();
syso(a);

说明：
常用于识别用户的手段。

【simei 获取设备imei】
用法：
String a = i.simei();
syso(a);

说明：
常用于识别用户的手段。

【endkeyboard 强制隐藏虚拟键盘】
用法：
i.endkeyboard();

说明：
常用于需要隐藏安卓弹出的虚拟键盘。


【hdfl 文件下载器】
用法：
//两个参数的方法设置
String savedir = "%SaveDir";
//输入下载保存目录，输入赋值变量返回一个下载器对象
Object a = i.hdfl(savedir,
new com.iapp.interfaces.OnFileDownStatusListener(){
public void resultStatus(int st_drD, int st_drI, Object o){

//每当下载完一个执行
//系统赋值 st_drD 文件下载项的序号
//系统赋值 st_drI 文件下载项的状态

//获取下载的URL
Object b1 = i.ulag(a, st_drD, "url");
syso(b1);

//获取自定义整数标识
Object b2 = i.ulag(a, st_drD, "type");
syso(b2);

//获取自定义参数任意数据
Object b3 = i.ulag(a, st_drD, "text");
syso(b3);

//获取下载文件保存的路径
Object b4 = i.ulag(a, st_drD, "filename");
syso(b4);
}
public void complete(int st_drJ, Object o){
//当下载完目前所有执行
//系统赋值 st_drJ 本次文件下载完成总数
syso(st_drJ);
}
}

);


//三个参数的方法设置
Object tempdir = "%TempDir";
Object savedir = "%SaveDir";
//输入下载临时文件保存目录，输入下载保存目录，输入赋值变量返回一个下载器对象
Object a = i.hdfl(tempdir, savedir,

new com.iapp.interfaces.OnFileDownStatusListener(){
public void resultStatus(int st_drD, int st_drI, Object o){

syso(st_drD);
}
public void complete(int st_drJ, Object o){
syso(st_drJ);
}
}
);

//六个参数的方法设置
Object tempdir = "%TempDir"
Object savedir = "%SaveDir"
//输入下载临时文件保存目录，输入下载保存目录, 下载线程数量，连接网络超时时间（25秒的意思），文件重复是否覆盖，输入赋值变量返回一个下载器对象
Object a = i.hdfl(tempdir, savedir, 3, 25000, true,

new com.iapp.interfaces.OnFileDownStatusListener(){
public void resultStatus(int st_drD, int st_drI, Object o){

syso(st_drD);
}
public void complete(int st_drJ, Object o){
syso(st_drJ);
}
}
);

说明：
常用与单个或多个的文件下载。推荐图片列表下载或小文件下载。

提示：
代码 区域中 属于线程内执行。在其中更新界面控件属性需要使用ufnsui代码
上例子使用tw代码，并且用了ufnsui代码。



【hdfla 文件下载器 增加文件下载项】
用法：
//创建一个文件下载器
Object a = i.hdfl("%TempDir",

new com.iapp.interfaces.OnFileDownStatusListener(){
public void resultStatus(int st_drD, int st_drI, Object o){

syso(st_drD);
}
public void complete(int st_drJ, Object o){
syso(st_drJ);
}
}
);

//增加下载项
//输入下载器对象，输入下载连接URL，输入自定义整数标识，输入自定义参数任意数据
i.hdfla(a, "http://abc.com/1.jpg", 1, "abcd123");


//增加下载项，并且自定义保存目录
//输入下载器对象，输入下载连接URL，输入自定义整数标识，输入自定义参数任意数据，输入自定义保存路径
i.hdfla(a, "http://abc.com/2.jpg", 1, "abcd123", "%abc.jpg");

说明：
调用下载器增加下载项，并且立刻进行下载。

【hdd 配置下载管理器】
用法：
//下载产生的临时文件目录
String a = "%tempdir";
//下载至保存的目录
String b = "%filedir";
//允许同时下载任务数量
int c = 3;
//每个任务开启线程数量
int d = 3;
//连接失败重试次数
int e = 2;
//连接超时时间，25秒的意思
int f = 25000;
//是否显示下载进度通知
boolean g = true;
i.hdd(a, b, c, d, e, f, g);

说明：
如果不使用此代码进行配置，那么系统将使用默认配置。下载配置器可以很方便的制作下载文件，并且方便管理。

默认目录属性：
临时文件目录：iApp/DownloadFileDir/TempDefaultDownFile
保存文件目录：iApp/DownloadFileDir/DefaultDownFile

【hdda 下载管理器 增加文件下载项】
用法：

//===========方法一
//下载的链接
String url = "http://abc.com/abc.apk";

//保存的文件名（仅输入文件名,请勿不包含目录）
String name = "abc.apk";

//输入自定义参数任意数据
String data = "abcde123";

//变量v为赋值变量，为下载对象
Object v = i.hdda(url, name, data);

//===========方法二
//下载的链接
String url = "http://abc.com/abc.apk";

//保存的文件名（仅输入文件名,请勿不包含目录）
String name = "abc.apk";

//下载任务的标题
String title = "abc.apk最新版";

//输入自定义参数任意数据
String data = "abcde123";

//变量v为赋值变量，为下载对象
Object v = i.hdda(url, name, title, data);

//===========方法三
//下载的链接
String url = "http://abc.com/abc.apk";

//保存的文件名（仅输入文件名,请勿不包含目录）
String name = "abc.apk";

//下载任务的标题
String title = "abc.apk最新版";

//下载任务的图标
String icon = "@abc.png";

//输入自定义参数任意数据
String data = "abcde123";

//变量v为赋值变量，为下载对象
Object v = i.hdda(url, name, title, icon, data);

//===========方法四
//下载的链接
String url = "http://abc.com/abc.apk";

//保存至目录
String dir = "%filedir";

//保存的文件名（仅输入文件名,请勿不包含目录）
String name = "abc.apk";

//下载任务的标题
String title = "abc.apk最新版";

//下载任务的图标
String icon = "@abc.png";

//是否显示下载进度通知
boolean notsohw = true;

//输入自定义参数任意数据
String data = "abcde123";

//变量v为赋值变量，为下载对象
Object v = i.hdda(url, dir, name, title, icon, notsohw, data);

说明：
增加常用的网络文件进行下载。

【hddgl 获取下载管理器下载列表】
用法：
//输入赋值变量返回下载列表
ArrayList<Object> list = i.hddgl();

//获取第一位数据
Object b = i.gslist(list, 0);
Object c = i.hddg(b, "url");
syso(c);

说明：
获取下载管理器所有的下载列表。

【hddg 获取下载管理器获取下载项属性】
用法：
//下载的链接
String url = "http://abc.com/abc.apk";
//保存的文件名（仅输入文件名,请勿不包含目录）
String name = "abc.apk";
//输入自定义参数任意数据
String data = "abcde123";
//变量v为赋值变量，为下载对象
Object v = i.hdda(url, name, data);

//===========获取下载项的属性
//获取下载项的 ID
Object b = i.hddg(v, "id");

//获取下载项的 下载链接
Object b = i.hddg(v, "url");

//获取下载项的 保存的绝对路径
Object b = i.hddg(v, "dirfilename");

//获取下载项的 下载链接的md5
Object b = i.hddg(v, "urlmd5");

//获取下载项的 保存的目录
Object b = i.hddg(v, "dir");

//获取下载项的 保存的文件名
Object b = i.hddg(v, "filename");

//获取下载项的 下载文件的大小（字节）
Object b = i.hddg(v, "contentlength");

//获取下载项的 已下载的数据（字节）
Object b = i.hddg(v, "equivalent");

//获取下载项的 当前下载速度（字节）
Object b = i.hddg(v, "downloadspeed");

//获取下载项的 当前下载进度百分比
Object b = i.hddg(v, "downloadpercentage");

//获取下载项的 下载状态；（0为等待下载；1为正在下载；2为下载完成；3下载已经暂停或停止；-1下载失败；-2已删除）
Object b = i.hddg(v, "status");

//获取下载项的 是否显示下载通知
Object b = i.hddg(v, "notificationshow");

//获取下载项的 自定义的数据
Object b = i.hddg(v, "text");

//获取下载项的 通知标题
Object b = i.hddg(v, "title");

//获取下载项的 通知图标
Object b = i.hddg(v, "icon");

说明：
可获取详细的下载项目状态属性。

【hdds 设置下载管理器下载项的属性】
用法：
//下载的链接
String url = "http://abc.com/abc.apk";
//保存的文件名（仅输入文件名,请勿不包含目录）
String name = "abc.apk";
//输入自定义参数任意数据
String data = "abcde123";
//变量v为赋值变量，为下载对象
Object v = i.hdda(url, name, data);

//===========可设置的下载项属性

//设置下载项的 下载状态；（0为等待下载；1为正在下载；2为下载完成；3下载已经暂停或停止；-1下载失败；-2已删除）
i.hdds(v, "status", 0);

//设置下载项的 是否显示下载通知
i.hdds(v, "notificationshow", true);

//设置下载项的 自定义的数据
i.hdds(v, "text", "abcd123");

//设置下载项的 通知标题
i.hdds(v, "title", "abc.apk最新版本");

//设置下载项的 通知图标
i.hdds(v, "icon", "@abc.png");

说明：
设置下载项目的属性。

【hdduigo 跳转至下载管理器】
用法：
//跳转至下载管理器
i.hdduigo();

//跳转至下载管理器，并且自定义标题栏颜色
//主体颜色
String a = "#387bd6";
//底部横杠颜色
String b = "#255eab";
i.hdduigo(a, b);

说明：
跳转至文件下载的管理器。

【ufnsui 线程更新界面】
用法：
i.ufnsui(
new com.iapp.interfaces.OnHandler(){
public void on(){
tw(a);
us(1, "text", "内容");

}
}
);

说明：
线程中直接修改界面或修改设置控件属性，出错。
需要使用ufnsui模块进行更新或设置控件属性。

提示：
线程中获取控件数据不会出错。


【se 正则表达式操作】
用法：
//===========例子1；所有属性展示
//字符串
String a = "qqqq123456eee";
//正则表达式
String b = "([a-z]+)(\\d+)";
//更多参数
int c = 0;
java.util.regex.Matcher d = i.se(a, b, c);
syso(d);

//替换成，将替换全部
String e = i.se(d, "sral", "1:$1, 2:$2");
syso(e);
//替换成，只替换第一个
String e = i.se(d, "srft", "1:$1, 2:$2");
syso(e);

//返回是否匹配成功，赋值返回true或 false
.String e = i.se(d, "ms");

//开始匹配 或 匹配下一个，赋值返回true或 false
.boolean e = i.se(d, "find");

//给定位置序号进行匹配，赋值返回true或 false
.boolean e = i.se(d, "find", 1);

//获取匹配组的数量，当前为2组：([a-z]+)、(\d+)
.int e = i.se(d, "gl");

//获取第1组匹配到的子字符串在字符串中的开头位置 
.int e = i.se(d, "start", 1);

//获取第1组匹配到的子字符串在字符串中的结尾位置 
.int e = i.se(d, "end", 1);

//获取第1组匹配到的子字符串
.String e = i.se(d, "group", 1);
//获取第2组匹配到的子字符串
.String e = i.se(d, "group", 2);


//===========例子2；获取所有手机号

//字符串
String a = "我的号码 13612345678 , 你的号码 13412345678";
//正则表达式
String b = "[1][3-8]\\d{9}";
//更多参数
int c = 0;
java.util.regex.Matcher d = i.se(a, b, c);

//开始匹配 或 匹配下一个
boolean ee = i.se(d, "find");

//循环判断是否匹配成功
while(ee){
//因为 [1][3-8]\\d{9} 没有组，所以这里我们输入 0
boolean e = i.se(d, "group", 0);

//打印出匹配到的子字符串
syso(e);

//开始匹配 或 匹配下一个
ee = i.se(d, "find");
}

//===========例子3；判断是否为手机号

//字符串
String a = "13612345678";
//正则表达式
String b = "^[1][3-8]\\d{9}$";
//更多参数
int c = 0;
java.util.regex.Matcher d = i.se(a, b, c);

boolean e = i.se(d, "ms");
if (e == true)
syso("手机号格式正确");
else
syso("手机号格式错误");



说明：
常用与字符串处理，高效的处理字符串，以及检测字符串类型等。使用此方法，需要对正则表达式有部分知识。

【usg 闪光灯操作】
用法：
//开启闪光灯
//输入闪光灯变量对象，输入是否开启闪光灯
android.hardware.Camera sgd = null;
sgd = i.usg(sgd, true);

//关闭闪光灯
//输入闪光灯变量对象，输入是否开启闪光灯
sgd = i.usg(sgd, false);

说明：
开启或关闭 设备闪光灯！

说明：
常用照明。

注意：
此方法调用将无法与摄像头同时调用。如启动摄像头需要使用闪光灯，可在摄像头操作中开启闪光灯。

【uzd 震动器操作】
用法：
//震动1秒时长
//输入振动器变量对象，输入震动时长
android.os.Vibrator zdq = null;
zdq = i.uzd(zdq, 1000);

//静止1秒，震动1秒，静止1秒，震动1秒，静止1秒，震动1秒，静止1秒，..， 并且不重复
//输入振动器变量对象，输入震动规则，输入是否重复循环执行
zdq = i.uzd(zdq, new long[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 }, false);

//强制停止震动器
i.uzd(zdq, "sp");

//检查硬件是否具有振动器
boolean b = i.uzd(zdq, "ip");
syso(b);

说明：
常用提示用户。

【usxq 开启前置摄像头】
用法：
Object ps = null;
//开启摄像头
//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度
ps = i.usxq(ps, 1, 90);

//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度，输入拍摄宽度像素，输入拍摄高度像素，输入图像品质1-100
ps = i.usxq(ps, 1, 90, 640, 480, 95);

//自动对焦拍摄
//输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
ps = i.usx(ps, "shot", "%abc.jpg", -90, false);

说明：
指定打开前置摄像头。

注意：
此功能需要与一个面控件进行绑定，你可以在面控件上面设置拍摄事件。

注意：
此代码仅限于载入事件调用。

【usxh 开启后置摄像头】
用法：
Object ps = null;
//开启摄像头
//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度
ps = i.usxh(ps, 1, 90);

//输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度，输入拍摄宽度像素，输入拍摄高度像素，输入图像品质1-100
ps = i.usxh(ps, 1, 90, 1280, 960, 95);

//自动对焦拍摄
//输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
ps = i.usx(ps, "shot", "%abc.jpg", 90, false);

说明：
指定打开后置摄像头。

注意：
此功能需要与一个面控件进行绑定，你可以在面控件上面设置拍摄事件。

注意：
此代码仅限于载入事件调用。

【usx 摄像头操作】
用法：
//开启摄像头
i.usxh(ps, 1, 90);

//自动对焦拍摄
//输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
i.usx(ps, "shot", "%abc.jpg", 90, false);

//开始预览
i.usx(ps, "st");

//停止预览
i.usx(ps, "sp");

//旋转摄像头角度
i.usx(ps, "rotaing", 180);
//获取旋转摄像头角度
int b = i.usx(ps, "getrotaing");
syso(b)

//启动摄像头闪光灯
i.usx(ps, "usg", true)

//结束摄像头组件变量对象
i.usx(ps, "re")

说明：
摄像头的控制。

【bly 录制音频】
用法：
android.media.MediaRecorder ly = null;
//开始录制
//输入录音变量对象，输入保存文件路径
i.bly(ly, "%abcd.amr")

//停止录音
i.bly(ly, "sp")

说明：
常用于录制音频。

说明：
可使用 bfm 代码来播放录制好的音频。

【ujp 截取屏幕】
用法：
//输入保存路径，输入图像品质（1-100）
i.ujp("%123.jpg", 70);

说明：
常用于截取当前界面。

【sqlite 数据库操作】
用法：
android.database.sqlite.SQLiteDatabase data = null;
//连接一个私有数据库，如果不存在将自动新建
//输入数据库对象变量，输入数据库文件名
i.sqlite(data, "iapp.db");

//连接一个公共数据库，如果不存在将自动新建
//输入数据库对象变量，输入数据库文件名
i.sqlite(data, "%iapp.db");

//判断数据库是否存在
boolean b = i.sqlite("iapp.db", "ip");
syso(b)

//删除数据库
boolean b = i.sqlite("iapp.db", "del");
syso(b)

//释放数据库
i.sqlite(data, "re");

说明：
进行数据库的操作。

【sql 数据表操作】
用法：

//创建数据表
String table = "_id integer primary key,url text, filename text,status interger,createTime datetime";
Object b = i.sql(data, "info", "add", table);

//判断数据表是否存在
Object b = i.sql(data, "info", "ip");
syso(b);

//删除数据表
Object b = i.sql(data, "info", "del");
syso(b);

//添加数据表一条数据
String table = "url,filename,status,createTime";
String value = "'http://abc.com/abc.apk', 'abc.apk', 1, '" + i.time(0) + "'";
Object b = i.sql(data, "info", "add", table, value);
syso(b);

//修改数据表的数据，若不需要设置条件(status=2)可设为 null 视为适用于执行所以数据
Object b = i.sql(sss.data, "info", "up", "status=2", "_id=1");
syso(b);

//删除数据表的数据，若不需要设置条件(_id=1)可设为 null 视为适用于执行所以数据
Object b = i.sql(sss.data, "info", "del", "_id=1");
syso(b);


//查询，若不需要设置条件(status=1 order by _id desc LIMIT 0,1)可设为 null 视为适用于执行所以数据

// LIMIT <跳过的数据数目>, <取数据数目>
String table = "_id,url,filename,status,createTime";
String sqlx = "status=1 order by _id desc LIMIT 0,1";
Object da = i.sql(sss.data, "info", "sele", table, sqlx);

//自定义sql查询
//String sqlx = "select _id,url,filename,status,createTime from info where status=1 order by _id desc LIMIT 0,1";
//Object da = i.sql(data, sqlx);

//光标对象移到下一条数据
boolean ee = i.sqlsele(da, "next");
while(ee){

//获取光标对象的第一列数据
Object e = i.sqlsele(data, 0);
syso(e);

//获取光标对象的第二列数据
Object e = i.sqlsele(data, 1);
syso(e);

//光标对象移到下一条数据
ee = i.sqlsele(data, "next");
}


//自定义的sql执行，需要对sql语法了解才能灵活运用
String sqlx = "insert into info (url,filename,status,createTime) values ('http://abc.com/abc.apk', 'abc.apk', 1, '2016-7-31 10:31:21')";
i.sql(sqlx, data);

说明：
数据表的操作。

注意：
在执行sql语句的时候，需要注意你的字符串的特殊字符的转义。
     /   ->    //
     '   ->    ''
     [   ->    /[
     ]   ->    /]
     %   ->    /%
     &   ->    /&
     _   ->    /_
     (   ->    /(
     )   ->    /)

【sqlsele 查询数据操作】
用法：

//获取光标对象的第一列数据
Object e = i.sqlsele(data, 0);

//获取光标对象有多少列
Object e = i.sqlsele(data, "columncount");
syso(e);

//获取总共查询到多少条数据
Object e = i.sqlsele(data, "count");
syso(e);

//光标对象移到下一条数据
Object e = i.sqlsele(data, "next");

//光标对象移到上一条数据
Object e = i.sqlsele(data, "previous");

//光标对象移到第一条数据
Object e = i.sqlsele(data, "first");

//光标对象移到最后第一条数据
Object e = i.sqlsele(data, "last");

//光标对象移到指定第2条数据
i.sqlsele(data, "position", 2);

//获取光标对象当前位置
Object e = i.sqlsele(data, "getposition");
syso(e);

//释放数据查询
i.sqlite(data, "re");

说明：
数据查询的操作。

【dha 渐变透明度动画】
用法：
//创建一个渐变透明度动画，开始显示，然后渐变消失
//输入动画开始是否透明，输入动画结束是否透明
android.view.animation.AlphaAnimation dh = i.dha(true, false);
i.dh(dh, "duration", 2000);
i.us(2, "dh", dh);

说明：
常用于控件透明度动画。

【dhs 渐变尺寸伸缩动画】
用法：
//创建一个渐变尺寸伸缩动画
//0为没有，2.5为原始2.5倍

//输入X开始尺寸比例，输入X结束尺寸比例，输入Y开始尺寸比例，输入Y结束尺寸比例
android.view.animation.ScaleAnimation dh = i.dhs(0.5, 2.5, 0.5, 2.5);
i.dh(dh, "duration", 2000);
i.us(2, "dh", dh);

//输入X开始尺寸比例，输入X结束尺寸比例，输入Y开始尺寸比例，输入Y结束尺寸比例，输入X位置类型，输入X坐标的开始位置，输入Y位置类型，输入Y坐标的开始位置
android.view.animation.ScaleAnimation dh = i.dhs(0.5, 2.5, 0.5, 2.5, 1, 0.5, 1, 0.5);
i.dh(dh, "duration", 2000);
i.us(2, "dh", dh);

说明：
常用于控件伸缩动画。

位置类型：
0 默认
1 以对象本身为基准位置类型
2 以父控件为基准位置类型

【dht 画面位置移动动画】
用法：
//创建一个画面位置移动动画
//输入开始X坐标上的移动位置，结束X坐标上的移动位置，开始Y坐标上的移动位置，结束Y坐标上的移动位置
android.view.animation.TranslateAnimation dh = i.dht(30, 80, 30, 80);
i.dh(dh, "duration", 2000);
i.us(2, "dh", dh);

说明：
常用于控件移动动画。

【dhr 画面旋转动画】
用法：
//创建一个画面旋转动画
//输入动画开始的旋转角度，输入动画旋转到的角度
android.view.animation.RotateAnimation dh = i.dhr(0, 180);
i.dh(dh, "duration", 2000);
i.us(2, "dh", dh);

//输入动画开始的旋转角度，输入动画旋转到的角度，输入X位置类型，输入X坐标的开始位置，输入Y位置类型，输入Y坐标的开始位置
android.view.animation.RotateAnimation dh = i.dhr(0, 180, 1, 0.5, 1, 0.5);
i.dh(dh, "duration", 2000);
i.us(2, "dh", dh);

说明：
常用于控件旋转动画。

位置类型：
0 默认
1 以对象本身为基准位置类型
2 以父控件为基准位置类型

【dhset 动画集合】
用法：

//渐变尺寸伸缩动画
android.view.animation.ScaleAnimation dh1 = i.dhs(0.5, 2.5, 0.5, 2.5);
i.dh(dh1, "duration", 2000);

//画面位置移动动画
android.view.animation.TranslateAnimation dh2 = i.dht(30, 80, 30, 80);
i.dh(dh2, "duration", 2000);

//画面旋转动画
android.view.animation.RotateAnimation dh3 = i.dhr(0, 180);
i.dh(dh3, "duration", 2000);

//创建一个动画集合
//输入动画集合变量对象，输入是否使用动画集合的interpolator，输入动画...（可输入N个参数）
android.view.animation.AnimationSet dhlist = i.dhset(false, new Object[]{dh1, dh2, dh3, dh4});
i.us(2, "dh", dhlist);
	
说明：
常用于动画集合执行。

提示：
动画集合允许被其他动画集合添加成为子动画。

提示：
动画集合如果设置了动画控制属性，同时也会重置所有子控件的属性。

【dhas 队列动画执行】
用法：
//旋转动画
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入旋转角度...（可输入N个参数）
Object dh = i.dhas(2, "rotation", new Object[]{ 60, 180 });
//Object dh = i.dhas(2, "rotationX", new Object[]{ 30, 80, 60, 20, 60 });
//Object dh = i.dhas(2, "rotationY", new Object[]{ 30, 80 });
i.dh(dh, "duration", 2000);
i.dh(dh, "start");

//伸缩动画
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入伸缩尺寸比例...（可输入N个参数）
Object dh = i.dhas(2, "scaleX", new Object[]{ 1.5, 2.5 });
//Object dh = i.dhas(2, "scaleY", new Object[]{ 1.5, 2.5, 1.2, 2.6, 1.3 });
i.dh(dh, "duration", 2000);
i.dh(dh, "start");

//移动动画
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入移动到位置...（可输入N个参数）
Object dh = i.dhas(2, "translationX", new Object[]{ 0, 60 });
//Object dh = i.dhas(2, "translationY", new Object[]{ 0, 60, 30, 10, 60 });
i.dh(dh, "duration", 2000);
i.dh(dh, "start");

//透明度
//输入动画变量对象，输入控件ID或控件对象，输入动画类型，可见度比例(0.0至1.0)...（可输入N个参数）
Object dh = i.dhas(2, "alpha", new Object[]{ 1, 0.3, 1, 0.2, 1 });
i.dh(dh, "duration", 2000);
i.dh(dh, "start");

说明：
自定义队列动画执行。


【dhast 队列动画集合】
用法：

//旋转动画
Object dh1 = i.dhas(2, "rotation", new Object[]{ 60, 180});
i.dh(dh1, "duration", 2000);

//伸缩动画
Object dh2 = i.dhas(2, "scaleX", new Object[]{ 1.5, 2.5});
i.dh(dh2, "duration", 2000);

//移动动画
Object dh3 = i.dhas(2, "translationX", new Object[]{ 0, 60});
i.dh(dh3, "duration", 2000);

//透明度
Object dh4 = i.dhas(2, "alpha", new Object[]{ 1, 0.3, 1, 0.2, 1});
i.dh(dh4, "duration", 2000);

//顺序执行
Object dhlist = i.dhast("sequen", new Object[]{ dh1, dh2, dh3, dh4});

//同时执行
//Object dhlist = i.dhast("together", new Object[]{ dh1, dh2, dh3, dh4});
i.dh(dhlist, "start");

说明：
常用于动画集合执行。

提示：
队列动画集合允许被其他队列动画集合添加成为子动画。

提示：
动画集合如果设置了动画控制属性，同时也会重置所有子控件的属性。


【dh 动画控制】
用法：

//========动画的属性（非队列动画）设置========================

//取消动画，取消后若需要重新播放，需要先执行 reset 然后再执行 start 进行播放
i.dh(dh, "cancel");

//重置动画属性
i.dh(dh, "reset");

//启动动画
i.dh(dh, "start");

//动画持续时长
i.dh(dh, "duration", 2000);

//延迟执行，延迟指定时长后才执行动画
i.dh(dh, "delay", 2000);

//启动动画结束填充效果（如果设false 那么 after 与 before将无效）
i.dh(dh, "enabled", true);

//动画执行后，控件停留执行结束状态
i.dh(dh, "after", true);

//动画执行后，控件停留执行开始状态
i.dh(dh, "before", true);

//动画重复执行的次数
i.dh(dh, "repeat", 20);

Object dh2 = i.dhas(2, "rotation", 60, 180);
//动画集合添加动画，仅用于 dhset 动画集合追加更多的动画
i.dh(dh, "add", dh2);

//========队列动画的属性设置========================

//取消动画
i.dh(dh, "cancel");

//播放动画
i.dh(dh, "start");

//动画持续时长
i.dh(dh, "duration", 2000);

//延迟执行，延迟指定时长后才执行动画
i.dh(dh, "delay", 2000);

//动画是否正在运行
Object b = i.dh(dh, "running");
syso(b);

//设置动画执行的控件ID或控件对象
i.dh(dh, "target", 2);

//克隆动画
Object dh2 = i.dh(dh, "clone");

说明：
常用于动画的控制管理。

【dhon 动画监听事件】
用法：
//========动画（非队列动画）设置监听事件========================

i.dhon(dh,
new android.view.animation.Animation.AnimationListener() {
public void onAnimationEnd(android.view.animation.Animation a) {

//当结束动画时
syso("End");
}
public void onAnimationRepeat(android.view.animation.Animation a) {

//当重复动画时
syso("Repeat");
}
public void onAnimationStart(android.view.animation.Animation a) {

//当启动动画时
syso("Start");
}
}

);


//========队列动画设置监听事件========================

i.dhon(dh,
new Animator.AnimatorListener() {
public void onAnimationEnd(Animator a) {

//当结束动画时
syso("End");
}
public void onAnimationRepeat(Animator a) {

//当重复动画时
syso("Repeat");
}
public void onAnimationStart(Animator a) {

//当启动动画时
syso("Start");
}
public void onAnimationCancel(Animator a) {

//当取消动画时
syso("Cancel");
}
}

);


说明：
常用于动画状态的监听。

提示：
该事件使用的选择性，可顺序选择性保留。

【dhb 动画背景】
用法：
//创建动画背景
//输入动画背景变量对象，输入是否重复执行
android.graphics.drawable.AnimationDrawable dh = i.dhb(true);

//添加元素
//输入动画背景变量对象，输入背景图像或图片变量或背景对象，输入显示时长
i.dhb(dh, "@t1.png", 1000);
i.dhb(dh, "@t2.png", 1000);
i.dhb(dh, "@t3.png", 1000);

//设为指定控件背景
i.us(2, "background", dh);

//启动动画
i.dhb(dh, "start");

//停止动画
//i.dhb(dh, "stop");

//是否在运行
Object b = i.dhb(dh, "running");
syso(b);

说明：
常用于组合一个背景动画。

【hsas 开启浏览器控件交互(裕语言+js+html5)】
用法：
//开启浏览器控件支持iapp交互
//输入浏览器控件ID或对象，输入是否开启
i.hsas(1, true);

//i.hsas(1, false)

说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

【has 裕语言交互JavaScript语言】
用法：
//首先将 web.html 放入用户文件中

//设置浏览器控件显示的html内容
String a = "@web.html";
String b = "utf-8";
String c = i.fr(a, b);

String d = "utf-8";
String e = "text/html";
boolean f = i.us(1, "url", c, d, e);

//因为浏览器加载内容属于异步操作，如果立刻执行下面的代码会执行失败
//所以将下面的代码放入某项单击事件中

String a = "go('呀！')";
//输入浏览器控件ID或对象，输入JavaScript的方法
i.has(1, a);

//带返回值解决方案
//String a = "go2('呀！')";
//输入浏览器控件ID或对象，输入JavaScript的方法
//i.has(1, a);

说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

注意：
在载入事件设置浏览器控件的加载html内容，它不会立刻加载完成。所以如果将 裕语言交互js的代码也写在载入事件，会导致交互调用失败。必须等待浏览器加载完毕html内容后，才能交互。

注意：
建议尽量使用JavaScript调用交互裕语言，效率较高。裕语言调用执行JavaScript的方法效率要慢数倍。

注意：
has 不应该放在新线程中，测试发现5.1系统has放入新线程中报错。

注意：
本例子需要注意编码，否则将乱码。

html（web.html）文件（utf-8编码）例子：
<html>
<head>
<script type="text/javascript">
function go(o)
{
document.getElementById("sb").innerHTML += "打我" + o;
}
function go2(o)
{
document.getElementById("sb").innerHTML += "打我" + o;
iapp.s("sss.sb", document.getElementById("sb").innerHTML);
}
</script>
</head>
<p id="sb">哈哈，你来</p>
</html>


【JavaScript交互裕语言】
用法：
//首先将 web.html 放入用户文件中

//设置浏览器控件显示的html内容
s a = "@web.html"
s b = "utf-8"
fr(a, b, c)

s d = "utf-8"
s e = "text/html"
us(1, "url", c, d, e, f)

//此方法，主要是在JavaScript中写交互代码哦
//JavaScript中交互方法列表（用于交互裕语言）：

/.

//调用裕语言模块方法，不带返回变量的
iapp.fn('a.b("' + o + '")');

//调用裕语言模块方法，带返回变量的
var value = iapp.fn2('a.c("' + o + '")', b);

//设置裕语言变量数据
iapp.s(o);

//获取裕语言变量数据
var value = iapp.g(o);
./
说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

注意：
建议尽量使用JavaScript调用交互裕语言，效率较高。裕语言调用执行JavaScript的方法效率要慢数倍。

注意：
本例子需要注意编码，否则将乱码。


html（web.html）文件（utf-8编码）例子：
<html>
<head>
<script type="text/javascript">

//不带返回变量的
function go(o)
{
//调用的是 模块a.myu 中的 b方法
iapp.fn('a.b("' + o + '")');
}

//带返回变量的
//执行模块后，获取一个变量并返回到javascript方法里
function go2(o, b)
{
//调用的是 模块a.myu 中的 c方法
var value = iapp.fn2('a.c("' + o + '")', b);
alert('变量 sss.abc：' + value);
}

//设置全局变量数据
//同理，下面也有设置界面变量、设置局部变量的例子
function ss(o, b)
{
iapp.s(o, b);
}

//获取全局变量数据
//同理，下面也有获取界面变量、获取局部变量的例子
function gs(o)
{
var value = iapp.g(o);
alert('变量 sss.abc：' + value);
}

</script>
</head>
<p><a href="javascript:void(0)" onclick="go('呵呵')">调用裕语言的模块方法</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="go2('呵呵', 'sss.abc')">调用裕语言的模块方法，并返回sss.abc变量内容</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="ss('sss.abc', '呵呵')">设置裕语言的sss.abc全局变量数据</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="gs('sss.abc')">获取裕语言的sss.abc全局变量数据</a></p>
</html>

模块（a.myu）例子：
fn b(a)
//打印出数据
syso(a)
end fn

fn c(a)
//打印出数据
syso(a)
sss abc = "666呵呵"
end fn

【uxf 显示悬浮窗】
用法：

//输入界面名，输入宽度，输入高度，输入对其方式，输入赋值变量
int w = -1;
int h = -1;
String gravity = "top|right";
v = i.uxf("a.ijava", w, h, gravity);


//输入界面名，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入类型的窗口，输入对其方式，输入flags，输入format，输入赋值变量
int x = 0;
int y = 0;
int w = -1;
int h = -1;
int type = 0;
String gravity = "top|right";
int flags = 0;
int format = 0;
i.uxf("a.ijava", x, y, w, h, type, gravity, flags, format, v);


//刷新悬浮窗口的布局，常用于通过us设置后的刷新
//输入界面根控件的控件对象
i.uxf(v);


//移除悬浮窗口
//输入界面根控件的控件对象，输入标识
i.uxf(v, "del");


//重置悬浮窗的属性
//输入界面根控件的控件对象，输入标识，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入对其方式
int x = 0;
int y = 0;
int w = -2;
int h = -2;
String gravity = "top|right";
i.uxf(v, "set", x, y, w, h, gravity);

//重置悬浮窗的属性
//输入界面根控件的控件对象，输入标识，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入对其方式
int x = 0;
int y = 0;
int w = -1;
int h = -1;
int type = 0;
String gravity = "top|right";
int flags = 0;
int format = 0;
i.uxf(v, "set", x, y, w, h, type, gravity, flags, format);

说明：
常用于显示悬浮窗窗口。

提示：
可通过 View b = i.gvs(v, "a.1") 代码进行获取悬浮窗内的子控件，然后对其进行操作。

提示：
可通过下例代码，控制窗口位置的移动
//更新窗口位置
i.us(v, "x", 100);
i.us(v, "y", 100);

//获取窗口位置
i.ug(v, "x", xx);
i.ug(v, "y", yy);

//通过us 更新后， 需要刷新悬浮窗口的布局
i.uxf(v);


对齐方式：
center：居中
top：顶
bottom：底
left：左
right：右
center_vertical：垂直居中
center_horizontal：水平居中

输入flags：
0 不许获得焦点（编辑框输入法将无法弹出）
1 可以获得焦点，返回键将不可用


【tts 文本转换语音】
用法：
//创建一个TTS对象
//输入赋值对象
Object a = i.tts();


//创建一个TTS对象；并且直接设置播放
//输入赋值对象，输入语言代码，输入语速率，输入音高率，输入播放文字（可传入null）
i.tts(a, "en", "I love you", 1, 1);


//获取TTS对象初始化状态；赋值变量返回 0未完成初始化 1初始化成功 -1初始化失败 -2初始化语言失败 -3当前TTS对象不可用
//输入TTS对象，输入标识，输入赋值变量
Object b = i.tts(a, "zt");
syso(b);


//播放文字；模式 0替换以前的任务 1队列追加至后面
//输入TTS对象，输入标识，输入播放文字，输入模式，输入赋值变量
Object b = i.tts(a, "st", "I love you", 0);
syso(b);


//文字转换音频文件
//输入TTS对象，输入标识，输入文字，输入保存路径，输入赋值变量
Object b = i.tts(a, "ft", "I love you", "123.wav");
syso(b);


//设置语言
//输入TTS对象，输入标识，输入语言代码
i.tts(a, "lg", "en");


//设置语音播放速率。1为正常，值越低语速越慢（0.5是正常的一半），值越大语速越快（2是正常的两倍）
//输入TTS对象，输入标识，输入小数
i.tts(a, "se", 1);


//设置音高率，值越大声音越高音，值越小声音越低音，正常为1.0
//输入TTS对象，输入标识，输入小数
i.tts(a, "ph", 1);


//检查是否TTS正在播放
//输入TTS对象，输入标识
Object b = i.tts(a, "ip");
syso(b);


//释放TTS使用的资源
//输入TTS对象，输入标识
i.tts(a, "re");


//停止所有任务
//输入TTS对象，输入标识，输入赋值变量
Object b = i.tts(a, "sp");
syso(b);


//检查是否一个可用的TTS对象
//输入TTS对象，输入标识，输入赋值变量
Object b = i.tts(a, "is");
syso(b);


说明：
常用于文本转化为音频，并且播放。


语言代码：
- 系统默认支持语言
美国    en
德国    de
意大利  it
法国    fr

- 需安装第三方语言包（讯飞语音TTS），并且设置语言
日本    ja
韩国    ko
中国    zh


安装与设置中文语言：

下载其中一个 
(4.0系统)讯飞语音TTS http://m.yx93.com/app.aspx?id=28515  
(2.2系统)讯飞语音TTS http://m.yx93.com/app.aspx?id=28513

安装 讯飞语音TTS

安卓手机》设置》语言和输入法》文本转语音输入》选择 讯飞语音合成 ,默认引擎 讯飞语音合成 , 语言 中文
（设置因为各品牌细节不同，但是都大同小异）


注意事项：
单独TTS对象创建后，需要有一个异步初始化过程，如果创建TTS对象然后直接播放文本将无法成功。需要先完成初始化后，然后播放文本。

注意事项：
文字转语音TTS输出；默认语言状态：完全支持 中文


【blp 录制屏幕】
用法：
Object b = "123.mp4";
//输入储存录制文件路径，输入视频宽度，输入视频高度，输入视频码率，输入视频帧率
i.blp(b, 1280, 720, 1024000, 30);

//开始录制
Object b = i.blp("st");
syso(b);

//停止录制
Object b = i.blp("sp");
syso(b);

//释放资源
Object b = i.blp("re");
syso(b);

//判断是否正在录制
Object b = i.blp("ip");
syso(b);

说明：
用于手机屏幕录制。

注意：
仅支持系统Android 5.0以及以上才有效果！
Android 5.0以下的系统，无效果！


【otob 转换为字节组】
用法：
//将文件转换为字节组，字节组将为字符串形式返回赋值给“b”
String b = i.otob("%abc.txt");
syso(b);

//将字符串转换为字节组
String b = i.otob("utf-8", "nihao");
syso(b);

//不设置编码
String b = i.otob(null, "nihao");

//将文件转换成 byte[] 字节数组对象
byte[] b = i.otob("file", null, "%abc.txt");
syso(b);

//将字符串转换成 byte[] 字节数组对象
byte[] b = i.otob("str", "utf-8", "nihao");
syso(b);


说明：
将字符或文件转换为字节组

【btoo 字节组还原】
用法：
String b = i.otob("%abc.txt");
//将字节组转换为文件
//输入字节组，文件路径，是否覆盖，变量 b 可为byte[] 字节数组对象
i.btoo(b, "%abc2.txt", true);


Object b = i.otob("utf-8", "nihao");
//字节组转换为字符串，变量 b 可为byte[] 字节数组对象
Object c = i.btoo("utf-8", b);
syso(c);

//不设置编码
Object c = i.btoo(null, b);

说明：
将字节组转换为字符或文件

【sot Socket网络通信】
用法：
//服务端
//服务端口，临时文件目录，接受客户超时，客户连接超时，是否覆盖文件
Object b = i.sot(8668, "%iApp/tempSocket", 0, 0, false,
new com.iapp.interfaces.OnMessagesListener() {
public void Message(Object msg, socketServer ss) {

}
}
);

//客户端
//服务地址，服务端口，服务连接超时，是否覆盖文件
Object b = i.sot("192.168.1.100", 8668, 0, false,
new com.iapp.interfaces.OnMessagesListener() {
public void Message(Object msg, socketServer ss) {

}
}
);

//发送字符串，必须放在线程内
i.sot(b, "str", "nihao");

//发送文件，必须放在线程内
i.sot(b, "file", "%abc.txt");

//发送字节组，必须放在线程内
Object c = i.otob("nihao", "utf-8");
i.sot(b, "bt", c);

//发送不带信息头 byte[]字节组，必须放在线程内
i.sot(b, "bt2", bytes);

//关闭释放sot
i.sot(b, "re");

//获取sot是否已释放
Object c = i.sot(b, "ip");

//获取ID总数
Object c = i.sot(b, "id");

//获取连接对象列表
Object c = i.sot(b, "list");

//获取连接对象列表的第一位
Object c = i.sot(b, "list", 0);

//获取连接总数
Object c = i.sot(b, "size");

//是否允许接受新连接
i.sot(b, "new", true);


说明：
Socket 管理操作。服务端发送消息将批量发送给所有连接。

服务端说明：
要求：
1.能连接公共网络 或 内网
2.拥有固定IP作为客户端连接的目标
3.电脑、手机、平板电脑等设备上运行服务端。
4.可使用iapp在自己的手机上面开发服务端，并运行服务端。

客户端说明：
要求：
1.能连接公共网络 或 内网
2.可使用iapp在自己的手机上面开发客户端，并连接服务端。

常见开发：
使用手机或电脑作为服务端，手机客户端与服务端相互传递文件、数据等。

【sota 单个Socket通信操作】
用法：
//获取连接对象列表的第一位，变量“c”属于单个Socket通信
Object c = i.sot(b, "sl", 0);

//获取通信对方的IP
Object d = i.sota(c, "ht");

//获取sota是否已释放
Object d = i.sota(c, "ip");

//关闭释放sota
i.sota(c, "re");

//获取socket对象
Object d = i.sota(c, "socket");

//获取连接对象ID
Object d = i.sota(c, "id");

//发送字符串，必须放在线程内
i.sota(c, "str", "nihao");

//发送文件，必须放在线程内
i.sota(c, "file", "%abc.txt");

//发送字节组，必须放在线程内
Object d = i.otob("utf-8", "nihao");
i.sota(c, "bt", d);

//发送不带信息头 byte[]字节组
i.sota(c, "bt2", bytes);

说明：
常用于单个Socket通信的操作管理


【loadso 加载动态库】
用法：
//比如加载 libabc.so
i.loadso("abc");

说明：
加载SO动态链接库。


【loadjar 加载jar库】
用法：
//比如加载 abc.jar
//返回变量 库对象
Object b = i.loadjar("abc.jar");
syso(b);

//比如加载 abc.apk
//包含Activity需要传入true，赋值变量 库对象
Object b = i.loadjar("abc.apk", true);
syso(b);

说明：
用于加载一些jar，dex，apk 的 sdk。需要把jar文件导入至项目资源的lib目录里，jar加载过程将联网校验。
如果附带SO动态链接库，需要把SO文件载入至项目资源。


【cls 获取完整接口类】
用法：
//获取一个类，输入完整类名如 java.lang.Math
//赋值变量 类对象
Object a = i.cls("java.lang.Math");
syso(a);

//获取一个字符串类，常用类型可直接输入类名如 String
Object b = i.cls("String");
syso(b);

//加载SDK abc.jar，并获取SDK里一个类 输入完整类名 com.sdk.abc
Object a = i.loadjar("abc.jar");
Object c = i.cls(a, "com.sdk.ceshi");
syso(c);

用法：
获取一个类；或从 jar SDK包获取类；

注意：完整类名区分大小写

【clssm 获取类的所有接口】
用法：
Object b = i.cls("String");

//获取所有构造函数
Object c = i.clssm(b, "init");

//获取所有函数方法
Object c = i.clssm(b, "method");

//获取所有变量
Object c = i.clssm(b, "field");


说明：
返回一个数组。


【java 调用java代码方法】
用法：
//调用java api java.lang.String.indexOf(String string); 查询字符56 在123456789 中位置
Object c = i.cls("String");
Object a = i.javax("123456789", c, "indexOf", {"String", "56"});
syso(a);


//初始化一个StringBuilderd
Object a = i.javanew("java.lang.StringBuilder", {"String", "12345"});
Object b = i.java(a, "java.lang.StringBuilder.append", {"String", "6789"});
Object c = i.java(a, "java.lang.StringBuilder.toString");
syso(c);


Object jar = i.loadjar("test.jar");
Object c1 = i.cls(jar, "com.sdk.ceshi");
//调用静态方法 com.sdk.ceshi类 c 方法
Object c = i.javax(null, c1, "c", {"int", 123});
syso(c);

//调用静态变量 com.sdk.ceshi类 a 变量
Object c = i.javags(null, c1, "a");
syso(c);

//初始化com.sdk.ceshi类
//返回对象变量，输入完整类名或 cls方法的返回变量
Object a = i.javanew(c1);

//访问变量，com.sdk.ceshi类 b变量
Object c = i.javags(a, c1, "b");
syso(c);

//设置变量，com.sdk.ceshi类 b变量
Object c = i.javass(a, c1, "b", "123456");
syso(c);


//设置回调方法
Object a = i.javanew("android.widget.TextView", {"Context", activity});
Object b = i.java(a, "android.widget.TextView.setText", {"CharSequence", "我是文本控件"});
//注意回调接口类名前面需要加一个“.”，如.android.view.View.OnClickListener
Object b = i.java(a, "android.view.View.setOnClickListener", {".android.view.View$OnClickListener", null},
new com.iapp.interfaces.OnInvocationHandler();
{
    public void on(java.lang.reflect.Method m, Object[] s)
    {
    }
}
)


说明：
支持 android 所有的api；以及 自加载的jar SDK 的 api

注意：完整类名或 方法名 或 变量名 区分大小写

传递参数：
要传递的参数可设置多个，格式为一个数组 {  } 括起来的，参数为格式：类名， 值，类名， 值...

activity：默认变量 Activity组件

javax 与 java 方法区别：
javax：第3位参数完整类名，第4位参数方法名。类名可传入 cls方法的赋值变量；总共6位参数
java：第3位参数 完整类名和方法名。总共5位参数。


【javacb 自定义回调】
用法：

Object jar = i.loadjar("test.jar");
Object c1 = i.cls(jar, "com.ceshi.dex.main");
Object o = i.javanew(c1);
Object c2 = i.cls(jar, "com.ceshi.dex.main$huidiao");

//设置回调方法
Object hd = i.javacb(c2, 
new com.iapp.interfaces.OnInvocationHandler()
{
    public void on(java.lang.reflect.Method m, Object[] s)
    {
    }
}
);
//设置回调
Object a = i.javax(o, c1, "sethuidiao", {c2, hd});
//调用回调方法
Object a = i.javax(o, c1, "get", {"String", "666"});

说明：
常用于设置自定义SDK的回调方法。


【res 安装包资源管理器】
用法：
//获取应用自己的对象
Object a = i.res();

//获取其他apk安装包内的资源对象，只支持加载SD卡上的apk
Object a = i.res("%abc.apk");

//获取资源
//输入资源对象，输入资源标识或文件名(没后缀)，输入资源类型
Object b = i.res(a, "ic_launcher", "drawable");

//获取资源ID，打包测试才有效
Object b = i.res(a, "ic_launcher", "drawable", false);

//获取 AssetManager 或 Resources 对象
Object b = i.res(a, "asset");
Object b = i.res(a, "resources");

说明：
可获取的资源类型 drawable、string、color、stringarray、layout


【src 自定义代码】
用法：

//[[
SDK自定义的 com.sdk.ceshia类 源码
package com.sdk;
public class ceshia {

	public String cs(String sm)
	{
		return sm;
	}
}
//]]

//初始化SDK自定义的 com.sdk.ceshia类
Object a = i.javanew("com.sdk.ceshia");
//将自定义类添加到代码块里
i.src("ceshia", a);
//代码里调用com.sdk.ceshi类 里的这个方法
Object b = ceshia:cs("abcde");
syso(b);

说明：
支持可以自己写java 的SDK，封装成代码。然后再自定义代码提示，把自己封装的代码加上去就可以了。



【call 交互式语言调用】
用法：

//输入语言类型，模块m的abc方法，输入一个Object数组
i.call("myu", "m.abc", new Object[]{ "nihao", 66 });


//输入语言类型，模块mk的abc方法，输入一个Object数组
Object a = i.call("mlua", "mk.abcd", new Object[]{ 123 });

//输入语言类型，模块mk的abc方法，输入一个Object数组
Object a = i.call("mjava", "mk.abcd", new Object[]{ 123, 456, 789 });

//没有参数的
//输入语言类型，模块mk的abc方法
i.call("mjs", "mk.abcdf");

说明：
用于多语言的代码交互。

注意：
此方法只能调用模块方法，输入是字符串如 m.abc 模块m 的abc方法

注意：
参数数量要与实际模块方法的参数的数量一致。

注意：
四种语言，只有 mlua 和 mjava 可以返回赋值变量，裕语言可以通过设置全局变量变相返回变量， mjs设置赋值变量无效。



【json json数据解析】
用法：
//解析json数据，双引号要加 \ 进行转义
Object text = "{\"id\":1, \"name\":\"xiaobai\", \"age\":16}";
Object jo = i.json(text);

//获取id
Object a = i.json(jo, "get", "id");
syso(a);
//获取name
Object b = i.json(jo, "get", "name");
syso(b);
//获取age
Object c = i.json(jo, "get", "age");
syso(c);

//修改age数据
i.json(jo, "set", "age", 20);

//删除id数据
i.json(jo, "del", "id");

//打印json数据
Object text = i.json(jo, "json");
syso(text);



//解析json列表数据
Object text = "{\"userlist\":[{\"id\":1, \"name\":\"niubi\", \"age\":16},{\"id\":2, \"name\":\"wangba\", \"age\":18},{\"id\":3, \"name\":\"goudan\", \"age\":17}]}";
Object jo = i.json(text);

//打印json数据
Object list = i.json(jo, "list", "userlist");
Object size = i.json(list, "size");
while(size > 0){

size = size - 1;

Object item = i.json(list, "data", size);

//获取id
Object a = i.json(item, "get", "id");
syso(a);
//获取name
Object b = i.json(item, "get", "name");
syso(b);
//获取age
Object c = i.json(item, "get", "age");
syso(c);

}

说明：
常用于解析服务器反馈的数据。


【utb Toolbar工具栏设置】
用法：

//设置自定义的工具栏 为当前界面的工具栏
//输入Toolbar工具栏的 控件id或控件对象
i:utb(3);


//绑定侧滑控件，侧滑控件内需要包含左侧滑，绑定后可以在Toolbar工具栏的左图标 控制左边侧滑
//输入Toolbar工具栏的 控件id或控件对象，输入侧滑的 控件id或控件对象
i:utb(3, 2);


//设置参数

i.utb("set", "dshe", true);

//设置左图标，可以设置事件监听
i.utb("left", 3, "@a.png");

//设置左图标的点击事件，注意此代码需在 i.utb(id) 后，否则事件将无效。
i.utb("set", "leftck", 3,

new android.view.View.OnClickListener() {

public void onClick(android.view.View v) {
syso("ok");
}
}
);

//设置右菜单图标，无事件。可使用界面菜单事件
i.utb("right", 3, "@b.png");



//标题
i:utb("set", "title", "apptitle");

//子标题
i:utb("set", "subtitle", "appsubtitle");

//自定义布局可输入View类型布局
i:utb("set", "cv", v);

//显示选项
i:utb("set", "do", 0);

//显示或隐藏 标题
i:utb("set", "dste", true);

//显示或隐藏 自定义布局
i:utb("set", "dsce", true);

//显示或隐藏 主页图标
i:utb("set", "dshe", true);


//获取参数

//标题
Object c = i:utb("get", "title");

//子标题
Object c = i:utb("get", "subtitle");

//自定义布局可输入View类型布局
Object c = i:utb("get", "cv");

//显示选项
Object c = i:utb("get", "do");

//动作栏布局高度
Object c = i:utb("get", "height");


说明：
常用于设计应用顶部工具栏。

【tws 弹窗提醒】
用法：
//获取展示的控件对象，提醒将在这个控件里展示
android.view.View v = i:gvs(1);

//无按钮弹出提醒
//输入控件对象可设置null，输入字符，输入显示时长（值0 -1 -2）
i:tws(v, "ni hao!", 0);


//有按钮弹出提醒
//输入控件对象可设置null，输入字符，输入显示时长（值0 -1 -2），输入按钮标题
i:tws(v, "ni hao ma?", 0, "hao",

new android.view.View.OnClickListener() {

public void onClick(android.view.View v) {
syso("ok");
}
}
);


【uht 滑动窗体控制】
用法：

//添加新的页面，设置的界面会执行载入事件里的代码
//输入滑动窗体的 控件id或控件对象，输入标识，输入插入序号 如-1为尾部 0为头部，输入标题，输入界面名，输入控件对应的数据项...不限制数量可参考代码ula
i.uht(2, "add", -1, "标题", "a.iyu", new Object[]{1,2,3}, new Object[]{"abc","bac","bbc"});

//删除界面
//输入滑动窗体的 控件id或控件对象，输入标识，输入界面序号 序号以0开始 -1为尾部
i.uht(2, "del", 0);

//修改界面标题
//输入滑动窗体的 控件id或控件对象，输入标识，输入界面序号 序号以0开始 -1为尾部
i.uht(2, "title", 0, "标题2");

//获取页面总数
Object b = i.uht(2, "size", b);
syso(b);

//释放内存
i.uht(2, "close");


//绑定标签布局，绑定后滑动界面时标签布局会跟随运动，需要注意 标签布局 和 滑动窗体 的子项数量应一致，新增子项时也需要同时增加
//输入滑动窗体的 控件id或控件对象，输入标识，输入标签布局的 控件id或控件对象，是否应刷新其内容
i.uht(2, "bd", 3, true);
//注意：如果绑定前 标签布局 如有设置子项，绑定时会被清空。绑定后需使用 i.us(3, "app_tablist", "选项1|选项2|选项3"); 代码设置

//增加标签布局 的子项
i.us(3, "app_tabadd", "选项");

//添加滑动窗体 的子项
i.uht(2, "add", -1, "标题", "a.iyu", new Object[]{1,2,3}, new Object[]{"abc","bac","bbc"});


说明：
用于动态管理控制滑动窗体和垂直滑动窗体的 新增页面、删除页面、绑定标签布局等。


【cast 强制转换数据类型】
用法：

int a = 123;
//转换数据类型并直接赋值
//输入完整类名 或 类对象，输入需要转换的数据变量
Object b = i.cast("String", a);
syso(b);


说明：
常用于数据强制转换。


【yul 加载yul布局】
用法：

//将布局加载展示到指定的布局控件里
//输入控件id或控件对象（比如输入线性布局ID），输入 yul 布局文件名
i.yul(1, "a.yul");


//返回布局对象
//输入 yul 布局文件名 返回一个View对象
android.view.View a = i.yul("a.yul");
syso(a);


说明：
yul布局是以 android 的 xml布局为基础，用于动态加载布局到应用界面。和安卓xml布局用法和代码都是一致的。

在设计 yul布局 时需要自定义控件ID，如设置控件ID:123 编写代码 android:id="123" 或 android:id="@+id/s123" 两种写法都可以，效果都是ID为 123



【无障碍服务】
用法：
固定模块名为 ays_service 可创建模块 ays_service.mjava，代码如下：

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;

//事件方法 on 实时回调变化事件
public void on(AccessibilityEvent e)
{
//获取事件类型
int b = ays.gtype(e);
//如果事件类型
if(b == 32 || b == 2048){
  //获取事件源的对象节点列表
  AccessibilityNodeInfo node = ays.gall(e);
  //判断事件来源是不是包名为com.iapp.app的应用
  String gpn = ays.gpn(e);
  if("com.iapp.app".equals(gpn))
  {
     //判断类名，根据指定的类名进行不同的操作
     String gcn = ays.gcn(e);
     if("com.iapp.app.HomeMian".equals(gcn))
     {
        //从对象列表搜索文本为“创建”的对象，并点击该对象
        ays.cktext(node, 16, "创建");
     }
     else if("com.iapp.app.HomeAdd".equals(gcn))
     {
        //根据ID获取指定的节点
        ArrayList b = ays.id(node, "com.iapp.app:id/ui_home_add_title");
	//设置节点的文本框输入指定字符
        ays.enter(b, "name");
        //根据ID获取指定的节点
        b = ays.id(node, "com.iapp.app:id/ui_home_add_remark");
	//设置节点的文本框输入指定字符
        ays.enter(b, "remark");
        //从对象列表搜索指定ID的对象，并点击该节点对象
        ays.ckid(node, 16, "com.iapp.app:id/ui_home_add_go");
     }
  }
  //释放根源节点
  ays.re(node);

}

}

//初始化事件方法 onsc 启动时回调一次
public void onsc()
{
String pns = "com.iapp.app";
//设置监听指定的包名，可以设置多个包名用逗号隔开如"com.xxx.a,com.xxx.b"
com.iapp.app.ays.pns = pns;
//设置相应时间
com.iapp.app.ays.nt = 1000;
}


然后 权限配置管理》application配置 将下面的配置粘贴进去：
	<service
            android:name="com.iapp.app.ays"
            android:label="iapp开发工具无障碍辅助功能"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService"/>
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/aya_config"/>
        </service>

最后，【正式打包发布】打包完成后，安装测试。记得自行去设置》辅助功能》打开我们的服务《iapp开发工具无障碍辅助功能》。
注意：直接在iapp里测试无效。


更多代码示范：

//------静态调用
//获取无障碍功能是否已经授权
com.iapp.app.ays.isas(activity);

//如果没有授权，可跳转设置界面
com.iapp.app.ays.goset(activity);


//------事件源操作
//获取Context功能类
android.content.Context a = ays.gbc();

//获取无障碍功能配置信息
android.accessibilityservice.AccessibilityServiceInfo a = ays.gsi();

//设置无障碍功能配置信息
ays.ssi(a);

//调用全局事件
//输入值：1. 返回键 2. HOME键 3. 最近打开应用列表 4. 打开通知栏 5. 设置 6. 锁屏
boolean a = ays.pga(1);

//获取事件类型
//值：32 打开PopupWindow，Menu，Dialog等的事件  64 显示通知的事件  2048 更改窗口内容的事件  4194304 屏幕上显示的窗口中的事件更改
int a = ays.gtype(e);

//获取事件源类的类型
String a = ays.gcn(e);

//获取事件源的包名
String a = ays.gpn(e);

//获取事件源的是否可用
boolean a = ays.ised(e);

//获取事件源的节点总数
int a = ays.gsl(e);

//获取事件源的整数ID
int a = ays.gwid(e);

//获取事件源的时间
long a = ays.gtime(e);

//释放资源
ays.re(e);


//------节点的操作
//获取事件源的节点对象列表
AccessibilityNodeInfo node = ays.gall(e);

//获取窗口的对象节点列表，需要Android 4.1及以上才可调用
AccessibilityNodeInfo node = ays.gall();

//根据序号；获取对象的子节点
AccessibilityNodeInfo a = ays.gi(node, 0);

//获取对象的子节点总数
int a = ays.gi(node);

//根据当前焦点向某个方向进行搜索可以获得输入焦点的最近控件
//输入值：33 向上  130 向下  17 向左  66 向右
AccessibilityNodeInfo a = ays.focussearch(node, 130);

//根据文本查询控件，返回节点列表
ArrayList nodelist = ays.text(node, "创建");

//根据id查询控件，返回节点列表
ArrayList nodelist = ays.id(node, "com.iapp.app:id/ui_home_add_go");

//根据焦点查询
//输入值：1 输入焦点  2 可访问性焦点
AccessibilityNodeInfo a = ays.focus(node, 1);

//获取节点文本
String a = ays.gt(node);

//获取节点类的类型
String a = ays.gcn(node);

//获取节点整数ID
int a = ays.gwid(node);

//获取节点ID
String a = ays.gid(node);

//获取可以在节点上执行的操作
ArrayList list = ays.gal(node);

//获取节点在屏幕上坐标
android.graphics.Rect a = ays.gbis(node);

//获取父节点在屏幕上坐标
android.graphics.Rect a = ays.gbip(node);

//获取节点的包名
String a = ays.gpn(node);

//获取节点的父节点
AccessibilityNodeInfo a = ays.gp(node);

//获取此节点是否可点击
boolean a = ays.isck(node);

//获取此节点是否已启用
boolean a = ays.ised(node);

//获取此节点是否已选中
boolean a = ays.iscd(node);

//获取这个节点是否被聚焦
boolean a = ays.isfd(node);

//获取此节点是否可以长时间点击
boolean a = ays.islck(node);

//获取此节点是否是密码
boolean a = ays.ispd(node);

//获取节点是否可滚动
boolean a = ays.isse(node);

//获取是否选择此节点
boolean a = ays.issd(node);



//根据文本查询；模拟控件点击控件
boolean a = ays.cktext(node, 16, "创建");

//根据ID查询；模拟控件点击控件
boolean a = ays.ckid(node, 16, "com.iapp.app:id/ui_home_add_go");

//根据焦点查询；模拟控件点击控件
//输入值：1 输入焦点  2 可访问性焦点
boolean a = ays.ckfocus(node, 16, 1);


	/.
	  模拟执行操作
	  1 将输入焦点输入到节点的操作
	  16 点击节点信息的动作
	  32 长时间点击节点的动作
	  32768 操作来粘贴当前的剪贴板内容
	 ./
//开始模拟控件点击
//输入节点列表
boolean a = ays.ck(nodelist, 16);

//开始模拟控件点击
//输入节点列表，输入自定义的Bundle
boolean a = ays.ck(nodelist, 16, bundle);

//对单项模拟控件点击
//输入节点列表
boolean a = ays.ck(node, 16);

//对单项模拟控件点击
//输入节点列表，输入自定义的Bundle
boolean a = ays.ck(node, 16, bundle);

//对单项模拟执行输入文本,Android 4.3 版本及以上
boolean a = ays.enter(node, "nihao");

//开始模拟执行输入文本,Android 4.3 版本及以上
boolean a = ays.enter(nodelist, "nihao");

//获取节点所有子节点列表
ArrayList nodelist = ays.ganiall(node);

//释放节点资源
ays.re(node);

说明：
无障碍功能（辅助功能）常用于简化操作，使应用或 系统的变得更智能、简便。


【zj 组件控制】
用法：
//如广告组件，首先下载的组件，并且设置好组件。

//初始化SDK，放在第一个界面的载入事件里
//输入赋值变量，标识，发布 ID，密钥，是否开启的Log输出（需要换自己的渠道信息）
Object a = i.zj("init", {"String", "85aa56a59eac8b3d", "String", "a14006f66f58d5d7", "boolean", true})

//初始化积分墙
//输入赋值变量，标识
Object a = i.zj("initjfq")

//展示积分墙
Object a = i.zj("jfqgo")


说明：
用于控制组件。






《ijs》速成开发手册3.0


 用户编程交流QQ群：
 官方源码开源群：323924434
 iApp技术开发群：483556574
 官方1群：1042334128
 官方2群：781302772
 官方3群：291033193
 官方4群：549133854
 官方5群：705873634
 官方游戏开发群：379221113



【3.0 ijs升级简介】
1. 出错后报错信息，显示在调试日志里。
2. 融合裕语言代码，写法和使用方式有所不同。
3. ijs 是没有ufnsui 线程更新界面代码，可以直接在线程中写代码更新控件界面
4. 入口文件必须为 mian.iyu 如果全部采用此语言开发，可以在入口文件加一个uigo跳转。
5. 更多不同点可以自行探索。


【js语言】
JavaScript一种直译式脚本语言，是一种动态类型、弱类型、基于原型的语言，内置支持类型。它的解释器被称为JavaScript引擎，为浏览器的一部分，广泛用于客户端的脚本语言，最早是在HTML（标准通用标记语言下的一个应用）网页上使用，用来给HTML网页增加动态功能。

在1995年时，由Netscape公司的Brendan Eich，在网景导航者浏览器上首次设计实现而成。因为Netscape与Sun合作，Netscape管理层希望它外观看起来像Java，因此取名为JavaScript。但实际上它的语法风格与Self及Scheme较为接近。
 
为了取得技术优势，微软推出了JScript，CEnvi推出ScriptEase，与JavaScript同样可在浏览器上运行。为了统一规格，因为JavaScript兼容于ECMA标准，因此也称为ECMAScript。


【var 变量】
用法：

//申明一个局部变量
var a = "aaa"
tw(a)

//申明一个界面变量，直接赋值不写“var”关键词视为界面变量
b = "bbb"
tw(b)


js变量区域介绍：
局部变量：服务于一个事件，当用户与界面发生交互时，产生一个事件，仅供于该事件的变量产生以及操作。
界面变量：生产界面变量后，同一个界面中的所有事件，均可对其进行操作。

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。裕语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。

空值：
判断是否空值的例子:(这里我们不知道变量“abc”是否空值)
if(abc == null)
{
	syso("是空值")
}


【s 变量】
用法：

申明界面变量
//可以赋字符串
ss("a", "blss")
//或 设置为空
I.ss("a", null)

//读取数据
tw(ss("a"))
或 注意，这样写也属于读取
tw(ss("a", null))

申明全局变量
//可以赋其他变量
sss("a", "blsss")
//或 设置为空
I.sss("a", null)

//读取数据
tw(sss("a"))

用途：
可用于与iApp支持的其他语言进行交换数据，数据共享，数据储存等。

区域介绍：
界面变量：生产界面变量后，同一个界面中的所有事件，均可对其进行操作。
全局变量：生产全局变量后，同一个应用中的所有事件，均可对其进行操作。

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。

提示：
自定义的变量名，比如“abc、 nihao、sfw123、www_zzw”变量不允许全部为数字，不允许掺杂符号，请不要使用太长的变量名，不推荐使用中文作为变量名。

空值：
如果访问一个没有声明的变量，将返回“null”空值类型，这个不对等于字符的 'null'。
判断是否空值的例子:(这里我们不知道变量“abc”是否空值)
var abc = sss("abc")
f(abc == null)
{
	syso("是空值")
}

【// 或  /* */ 注释语句】
用法：

//这个是变量“a”它的值等于“1”
var a = 1
//这个是变量“b”它的值等于“2”
var b = 2

/*
大量代码注释方法
var c = 3
var d = 4

*/

说明：
注释语句符号可以用“//” 也可以用“/*  */”，以注释符号开头的正行，将会被代码执行器无视。通常用于给自己标示代码的含义

【fn 加载mjs模块】
用法：
//加载a.mjs模块
fn("a")

说明：
用于加载你的模块文件；建议要加载的模块，载入事件加载一次即可。

【syso 打印】
用法：
//打印字符串
syso("wo1314")
//或
i.syso("wo1314")

//打印字符串，字符串拼接
syso("wo" + "ai" + "ni")
//或
i.syso("wo" + "ai" + "ni")


//拼接变量打印字符串
var a = "a"
var b = null

syso("变量" + tos(a) + "等于" + tos(b))


可以打印出数据，代码同等于 System.out.println("1314")，可以在测试时，选择 调试日志查看打印数据。

说明：
用于打印调试数据。

【if 判断语句】
用法：

//if 语句的使用
var a = 2

if (a > 1){

syso("a大于1")

}


//if...else 搭配使用
var a = 2

if (a == 1){

syso("a等于1")

}else if (a == 2){

syso("a等于2")

}else{

syso("a等于其他")

}


//多个if语句嵌套
var a = 1
var b = 2
if (a == 1){

if (b == 2){
syso("a等于1，b等于2")
}

}else{

syso("a不等于1")

}


//逻辑运算判断

var a = 1
var b = 2
//a等于1 或者 等于2
if (a == 1 || a == 2){

syso("a等于1")

}

//a等于1 并且 b等于2
if (a == 1 and b == 2){

syso("a等于1，b等于2")

}

//a等于3 取反意，与逻辑运算结果相反，如果条件为 true，逻辑非为 false
if !(a == 3){

syso("a不等于3")

}

//a不等于b 此运算符检测两个值是否相等，相等返回 false，否则返回 true

if (a != b){

syso("a不等于b")

}


【while 循环】
用法：
//这将循环10次
var a = 10

while (a > 0){

syso(a)
a = a - 1

}

说明：
条件循环语句，比较值的变化，然后进行循环执行里面的代码。当条件为“否”的时候会停止循环，条件“是”的话，将一直循环执行。
支持运算符（返回 是 与 否）：（跟 if 语句 一样，请参考）


【for 循环】
用法：
// 条件: a=初始值,最大值,增长值；a初始为1，设置a最大为10，a每次循环增加1
for (var a=1,i<10,i++){

syso("循环10/" + a)

}

// 提前跳出循环
for (var a=1,i<10,i++){

syso("循环10/" + a)
if (a == 6){
// break 语句跳出循环
break
}

}


//设置一个数组
var data = ["a","b","c","d"];
//打印数组；a是记录循环次数，b是每次循环数组值
for (var a in data){

syso("循环:" + sz[a]);

}

说明：
用于多次重复循环操作。


【t 新线程】
用法：
i.t(
function(){

syso("新线程里执行代码")

}
)

说明：
启用新线程，去执行一些需要执行很久的代码。比如把下载文件，获取网页源码，大量的文件操作，可以放入新线里执行。这里线程的概念，启用新的线程帮你处理代码，这样不会影响到主线程。


【ssj 设置或修改控件事件代码】
用法：
//设置控件ID3，的单击事件
i.ssj(3, "clicki",[
function(st_vId,st_vW){

syso("ok")

}
])

//设置控件ID3，的触屏滑动事件
i.ssj(3, "onscroll",[
function(st_vId,st_vW,st_sE){

syso("ok")

}
,

function(st_vId,st_vW,st_fM,st_vT,st_bT){

syso("ok")

}
])


说明：
输入控件Id，输入事件类型，并将事件顺序填写在 { 中 }，动态控件将触发该事件代码。

事件类型：
clicki=单击事件


【tw 提示】
用法：
tw("你好")
//或
i.tw("你好")

//设置参数1：显示的时间长久；0：显示的时间短暂；\n为换行的意思，其他地方通用
tw("你好\n吗？", 1)
//或
i.tw("你好\n吗？", 1)


说明：
用于提醒用户，界面显示时长大约为 2秒钟。弹出代码中的文字，来提醒用户。


【fd 删除文件】
用法：(将删除SD卡根目录的abc.zip文件)
var b = i.fd("%abc.zip")

syso(b)

说明：
用于删除指定的文件，是否成功返回数据：true或 false


【fe 文件是否存在】
用法：(将判断SD卡根目录的abc.zip文件是否存在)
var b = i.fe("%abc.zip")

syso(b)

说明：
用于判断指定的文件存在，是否存在返回数据：true或 false


【fs 文件大小】
用法：(将获取SD卡根目录的abc.zip文件占用的大小)
var b = i.fs("%abc.zip")

syso(b)

说明：
用于判断指定的文件存在，是否存在返回数值单位(字节)。
转换为KB：
var b = i.fs("%abc.zip")
b = b / 1024
syso(b)

转换为MB：
b = b / 1024 / 1024


【fr 读取文本】
用法：(将读取SD卡根目录的abc.txt文件里面的内容)
var b = i.fr("%abc.txt")
syso(b)

var b = i.fr("%abc.txt", "utf-8")
syso(b)

说明：
用于读取文本文件的数据内容。
……

【fc 复制文件】
用法：（在SD卡根目录abc.txt文件拷贝一个新的副本至abc2.txt）
var b = i.fc("%abc.txt", "%abc2.txt")
syso(b)

//设置重复不覆盖
var b = i.fc("%abc.txt", "%abc2.txt", false)
syso(b)

//将apk包内的 abc.txt 复制到SD卡上
var b = i.fc("@abc.txt", "%abc2.txt")
syso(b)

说明：
用于复制文件，创建一个新的副本文件。是否成功返回数据：true或 false
……


【fw 写入文本】
用法：(将文本数据写入至SD卡根目录的abc.txt文件里面)

var b = "我是一个txt文件的内容"
i.fw("%abc.txt", b)

var b = "我是一个txt文件的内容"
i.fw("%abc.txt", b, "utf-8")

说明：
用于写入文件。



【fl 文件列表】
用法：（获取一个目录的

《ilua》速成开发手册3.0


 用户编程交流QQ群：
 官方源码开源群：323924434
 iApp技术开发群：483556574
 官方1群：1042334128
 官方2群：781302772
 官方3群：291033193
 官方4群：549133854
 官方5群：705873634
 官方游戏开发群：379221113


【3.0 ilua升级简介】
1. 出错后报错信息，显示在调试日志里。
2. “null”的表示方式在lua为 “nil”，所以空值写法为 nil
3. 融合裕语言代码，写法和使用方式有所不同。
4. 入口文件必须为 mian.iyu 如果全部采用此语言开发，可以在入口文件加一个uigo跳转。
5. 更多不同点可以自行探索。


【lua语言】
Lua 是一种轻量小巧的脚本语言，用标准C语言编写并以源代码形式开放， 其设计目的是为了嵌入应用程序中，从而为应用程序提供灵活的扩展和定制功能。
Lua 是巴西里约热内卢天主教大学（Pontifical Catholic University of Rio de Janeiro）里的一个研究小组，由Roberto Ierusalimschy、Waldemar Celes 和 Luiz Henrique de Figueiredo所组成并于1993年开发。 

【luajava介绍】
LuaJava项目由 Carlos Cassino 在2004年开发创建，上传开源，并未作出使用限制。以及 Thiago Ponte 作为主要代码贡献者，在2005年和2007年等进行了更新。 
iApp引用了他们的项目基础，并进行了升级和修复BUG
[历史资料来源于网络，仅供参考]


【local 变量】
用法：

--申明一个局部变量
local a = "aaa"
tw(a)

--申明一个界面变量，直接赋值不写“local”关键词视为界面变量
b = "bbb"
tw(b)

--注：此全局变量如果等于 空值， 则无法访问，访问将报错。
--通过 is(var) 访问不确定的全局变量，若他不存在或 是空值 不会报错
local abc = is("abc")
syso(abc)


lua变量区域介绍：
局部变量：服务于一个事件，当用户与界面发生交互时，产生一个事件，仅供于该事件的变量产生以及操作。
界面变量：生产界面变量后，同一个界面中的所有事件，均可对其进行操作。

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。裕语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。

空值：
如果访问一个没有声明的变量，将返回“nil”空值类型，这个不对等于字符的 'nil'。
判断是否空值的例子:(这里我们不知道变量“abc”是否空值)
if(abc == nil)
{
	syso("是空值")
}


【s 变量】
用法：

申明界面变量
--可以赋字符串
ss("a", "blss")
--或 设置为空
i:ss("a", nil)

--读取数据
tw(ss("a"))
或 注意，这样写也属于读取
tw(ss("a", nil))

申明全局变量
--可以赋其他变量
sss("a", "blsss")
--或 设置为空
i:sss("a", nil)

--读取数据
tw(sss("a"))

用途：
可用于与iApp支持的其他语言进行交换数据，数据共享，数据储存等。

区域介绍：
界面变量：生产界面变量后，同一个界面中的所有事件，均可对其进行操作。
全局变量：生产全局变量后，同一个应用中的所有事件，均可对其进行操作。

说明：
变量类似一个箱子，你可以把数据储存在里面，等需要的时候就取出来使用，可以改变它装你想要装的数据。语言中的变量是可以根据赋值，而且自动转换的，所以无需申明数据类型。

提示：
自定义的变量名，比如“abc、 nihao、sfw123、www_zzw”变量不允许全部为数字，不允许掺杂符号，请不要使用太长的变量名，不推荐使用中文作为变量名。

空值：
如果访问一个没有声明的变量，将返回“nil”空值类型，这个不对等于字符的 'nil'。
判断是否空值的例子:(这里我们不知道变量“abc”是否空值)
local abc = sss("abc")
f(abc == nil)
{
	syso("是空值")
}

【-- 或  --[[ --]] 注释语句】
用法：

--这个是变量“a”它的值等于“1”
local a = 1
--这个是变量“b”它的值等于“2”
local b = 2

--[[
大量代码注释方法
local c = 3
local d = 4

--]]

说明：
注释语句符号可以用“--” 也可以用“--[[ --]]”，以注释符号开头的正行，将会被代码执行器无视。通常用于给自己标示代码的含义

【tos 转为字符串】
用法：
--将 数字 转换字符串
local c = tos(123)
local c = tos(123.23)
--将 是否 转换字符串
local c = tos(true)

--拼接变量打印字符串
local a = "a"
local b = nil
local c = true

syso("如果变量" .. tos(a) .. "等于" .. tos(b) .. "那么就不是" .. tos(c))

【toi 转为整数型】
用法：
--将 小数型 转换为整数型 123
local c = toi(123.23)

--将转换为整数型数据 123
local c = toi(123.23)

说明：
由于lua只有小数类型，没有整数类型，传递到java方法将导致数据类型不匹配。若需要整数型时，使用该方法转换整数型。

【tosz lua数组转为java数组】
用法：
local c = {3,2,4,5,1}
local sz = tosz(c)


【szto java数组转为lua数组】
用法：
local sz = i:sl("12;12;12;12;12", ";")
local c = szto(sz)


【fn 加载mlua模块】
用法：
--加载a.mjs模块
fn("a")

--然后就可以直接，调用a.mjs模块 的 abc 方法
abc()

说明：
用于加载你的模块文件；建议要加载的模块，载入事件加载一次即可。

注意：
加载多模块时，模块方法名，不要过分简单，避免重复；可在不同的模块，所有方法名加前缀或 后缀。

也可以使用require方法加载模块，效果一样。

【syso 打印】
用法：
--打印字符串
syso("wo1314")
--或
i:syso("wo1314")

--打印字符串，字符串拼接
syso("wo" .. "ai" .. "ni")
--或
i:syso("wo" .. "ai" .. "ni")


--拼接变量打印字符串
local a = "a"
local b = nil

syso("变量" .. tos(a) .. "等于" .. tos(b))


可以打印出数据，代码同等于 System.out.println("1314")，可以在测试时，选择 调试日志查看打印数据。

说明：
用于打印调试数据。

【if 判断语句】
用法：

--if 语句的使用
local a = 2

if a > 1 then

syso("a大于1")

end


--if...else 搭配使用
local a = 2

if a == 1 then

syso("a等于1")

elseif a == 2 then

syso("a等于2")

else

syso("a等于其他")

end


--多个if语句嵌套
local a = 1
local b = 2
if a == 1 then

if b == 2 then
syso("a等于1，b等于2")
end

else

syso("a不等于1")

end


--逻辑运算判断

local a = 1
local b = 2
--a等于1 或者 等于2
if a == 1 or a == 2 then

syso("a等于1")

end

--a等于1 并且 b等于2
if a == 1 and b == 2 then

syso("a等于1，b等于2")

end

--a等于3 取反意，与逻辑运算结果相反，如果条件为 true，逻辑非为 false
if not(a == 3) then

syso("a不等于3")

end

--a不等于b 此运算符检测两个值是否相等，相等返回 false，否则返回 true

if a ~= b  then

syso("a不等于b")

end


【while 循环】
用法：
--这将循环10次
local a = 10

while a > 0 do

syso(a)
a = a - 1

end

说明：
条件循环语句，比较值的变化，然后进行循环执行里面的代码。当条件为“否”的时候会停止循环，条件“是”的话，将一直循环执行。
支持运算符（返回 是 与 否）：（跟 if 语句 一样，请参考）


【for 循环】
用法：
-- 条件: a=初始值,最大值,增长值；a初始为1，设置a最大为10，a每次循环增加1
for a=1,10,1 do

syso("循环10/" .. a)

end


-- 提前跳出循环
for a=1,10,1 do

syso("循环10/" .. a)
if a == 6 then
-- break 语句跳出循环
break
end

end


--设置一个数组
local data = {"a","b","c","d"}
--打印数组；a是记录循环次数，b是每次循环数组值
for a,b in ipairs(data) do

syso(a .. "次 循环:" .. b)

end

说明：
用于多次重复循环操作。


【t 新线程】
用法：
i:t(
function()

syso("新线程里执行代码")

end
)

说明：
启用新线程，去执行一些需要执行很久的代码。比如把下载文件，获取网页源码，大量的文件操作，可以放入新线里执行。这里线程的概念，启用新的线程帮你处理代码，这样不会影响到主线程。


【ssj 设置或修改控件事件代码】
用法：
--设置控件ID3，的单击事件
i:ssj(3, "clicki",{
function(st_vId,st_vW)

syso("ok")

end
})

--设置控件ID3，的触屏滑动事件
i:ssj(3, "onscroll",{
function(st_vId,st_vW,st_sE)

syso("ok")

end
,

function(st_vId,st_vW,st_fM,st_vT,st_bT)

syso("ok")

end
})


说明：
输入控件Id，输入事件类型，并将事件顺序填写在 { 中 }，动态控件将触发该事件代码。

事件类型：
clicki=单击事件


【tw 提示】
用法：
tw("你好")
--或
i:tw("你好")

--设置参数1：显示的时间长久；0：显示的时间短暂；\n为换行的意思，其他地方通用
tw("你好\n吗？", 1)
--或
i:tw("你好\n吗？", 1)


说明：
用于提醒用户，界面显示时长大约为 2秒钟。弹出代码中的文字，来提醒用户。


【fd 删除文件】
用法：(将删除SD卡根目录的abc.zip文件)
local b = i:fd("%abc.zip")

syso(b)

说明：
用于删除指定的文件，是否成功返回数据：true或 false


【fe 文件是否存在】
用法：(将判断SD卡根目录的abc.zip文件是否存在)
local b = i:fe("%abc.zip")

syso(b)

说明：
用于判断指定的文件存在，是否存在返回数据：true或 false


【fs 文件大小】
用法：(将获取SD卡根目录的abc.zip文件占用的大小)
local b = i:fs("%abc.zip")

syso(b)

说明：
用于判断指定的文件存在，是否存在返回数值单位(字节)。
转换为KB：
local b = i:fs("%abc.zip")
b = b / 1024
syso(b)

转换为MB：
b = b / 1024 / 1024


【fr 读取文本】
用法：(将读取SD卡根目录的abc.txt文件里面的内容)
local b = i:fr("%abc.txt")
syso(b)

local b = i:fr("%abc.txt", "utf-8")
syso(b)

说明：
用于读取文本文件的数据内容。
……

【fc 复制文件】
用法：（在SD卡根目录abc.txt文件拷贝一个新的副本至abc2.txt）
local b = i:fc("%abc.txt", "%abc2.txt")
syso(b)

--设置重复不覆盖
local b = i:fc("%abc.txt", "%abc2.txt", false)
syso(b)

--将apk包内的 abc.txt 复制到SD卡上
local b = i:fc("@abc.txt", "%abc2.txt")
syso(b)

说明：
用于复制文件，创建一个新的副本文件。是否成功返回数据：true或 false
……


【fw 写入文本】
用法：(将文本数据写入至SD卡根目录的abc.txt文件里面)

local b = "我是一个txt文件的内容"
i:fw("%abc.txt", b)

local b = "我是一个txt文件的内容"
i:fw("%abc.txt", b, "utf-8")

说明：
用于写入文件。



【fl 文件列表】
用法：（获取一个目录的文件列表）
local c2 = i:fl("%dir")
local c = szto(c2)
local cc = #c
--获取总共文件并减去1
local leng = cc - 1
--循环打印
for a=0,leng,1 do

syso("文件 " .. c[a])

end


--仅获取文件夹
local c2 = i:fl("%.estrongs", false)
local c = szto(c2)
local leng = #c
while leng>0 do
leng = leng - 1

syso("文件夹 " .. c[leng])
end


--仅获取文件
local c2 = i:fl("%.estrongs", false)
local c = szto(c2)
local leng = #c
while leng>0 do
leng = leng - 1

syso("文件 " .. c[leng])
end

说明：上面例子是获取sd卡根目录文件夹“dir”里面的所有子目录以及文件，并获取结果返回变量“c”，并用用符合 # 获取变量c 有多少位，然后循环来读取变量“c”里面的列表数据，可以通过c[0]获取第一位，c[1]第二位数据等。

提示：
看似有些复杂，理解了就简单了， 这里的变量“c”类型是一个数组，里面包含了一个数据列表。通过循环可以顺序读取这个列表。


【ft 转移文件】
用法：（将SD卡根目录的abc.txt转移至abc3.txt）
local c = i:ft("%abc.txt", "%abc3.txt")
syso(c)

说明：
用于转移文件。是否成功返回数据：true或 false


【fdir 获取SD卡根目录路径】
用法：（获取根目录路径并赋值至变量“a”）
--获取根目录
local a = i:fdir()
syso(a)

--获取目录的绝对路径
local a = i:fdir("%dir")
syso(a)

说明：
通过获取根目录路径，就可以计算文件的绝对路径。


【fuz 解压zip部分文件】
用法：（将根目录文件abc.apk压缩包里的AndroidManifest.xml文件，解压到根目录AndroidManifest2.xml）
local d = i:fuz("%abc.apk", "AndroidManifest.xml", "%AndroidManifest2.xml")
syso(d)

--解压文件遇到重复不覆盖
local d = i:fuz("%abc.apk", "AndroidManifest.xml", "%AndroidManifest2.xml",false)
syso(d)

说明：
通过上面代码可以实现压缩包解压部分的文件，并返回赋值至变量“d”解压文件的数量。


【fuzs 解压整个zip】
用法：(将根目录文件abc.apk压缩包解压至根目录文件夹abcdir，会自动创建)

local c = i:fuzs("%abc.apk", "%abcdir")
syso(c)

--解压文件遇到重复不覆盖
local c = i:fuzs("%abc.apk", "%abcdir", false)
syso(c)

说明：
通过上面代码将解压整个压缩包至指定文件，并赋值至变量“c”，是否成功返回数据：true或 false


【fj 压缩文件或文件夹至zip】
用法：
local c = i:fj("%adc.txt", "%abc.zip")
syso(c)

--不去除根目录
local c = i:fj("%adc.txt", "%abc.zip",false)
syso(c)

说明：
压缩文件。返回赋值数据：true 或 false


【fo 打开文件】
用法：（将根目录打开安装abc.apk文件）
i:fo("%abc.apk")

说明：
可以调用系统工具打开不同的文件。


【sr 替换字符】
用法：
local a = "123456789"
local b = "456"
local c = "."
local d = i:sr(a, b, c)
--将提示：123.789
syso(d)

--支持正则表达式
--local d = i:sr(a, b, c, true)

说明：
用于替换字符


【sj 截取字符】
用法：
local a = "123456789"
local b = "34"
local c = "8"
local d = i:sj(a, b, c)
--将提示：567
syso(d)

--从头部开始截取
local d = i:sj(a, nil, c)
syso(d)

--截取到尾部
local d = i:sj(a, b, nil)
syso(d)

说明：
用于截取数据部分字符


【sl 数据数组】
用法：
local a = "12;12;12;12;12"
local b = ";"
local c2 = i:sl(a, b)

--可以支持正则表达式；例子看（注意说明）
--local c2 = i:sl(a, b, true)
local c = szto(c2)

--获取数组有多少位
local leng = #c
while leng>0 do
leng = leng - 1

--将打印5次：12
syso(c[leng])
end

说明：
将把变量“a”的字符串，切割成一个数组，以字符“.”为分割字符。并用循环顺序打印出数据。

注意：
如果支持正则表达式数据数组，上例子的 local b = ";" 其内的值。需要转义的特殊字符 “$()*+.[]?\^{},|”

支持正则的特殊字符转义方法：
如：
local a = "12|a$12|a$12|a$12|a$12"

--关键分割字符串如果包含特殊字符，需要在每个特殊字符前面增加“\\”进行转义
local b = "\\|a\\$"
local c2 = i:sl(a, b, true)
local c = szto(c2)
--获取数组有多少位
local leng = #c
while leng>0 do
leng = leng - 1

--将打印5次：12
syso(c[leng])
end


【siof 获取字符位置】
用法：
local a = "123456789"
local b = "3"
local c = 0
local d = i:siof(a, b, c)
--将提示：2
syso(d)

local a = "123456789"
local b = "3"
local d = i:siof(a, b)
--将提示：2
syso(d)

说明：
从前面向后面进行匹配。字符位置以0计算，若无数据找到将返回 -1


【slof 获取字符位置】
用法：
local a = "123456789"
local b = "4"
local c = 8
local d = i:slof(a, b, c)
--将提示：3
syso(d)

local a = "123456789"
local b = "4"
local c = i:slof(a, b)
--将提示：3
syso(c)

说明：
从后面向前面进行匹配。字符位置以0计算，若无数据找到将返回 -1


【ssg 截取字符】
用法：
local a = "abcdefghijk"
local b = i:ssg(a, 2, 6)
--将提示：cdef
syso(b)

local a = "abcdefghijk"
local b = i:ssg(a, 6)
--将提示：ghijk
syso(b)

说明：
根据字符的位置进行截取字符，若失败将变量“b”赋值 nil


【slg 获取字符长度】
用法：
local a = "123456789"
local b = i:slg(a)
--将提示：9
syso(b)

说明：
顾名思义。


【strim 去除头尾空格】
用法：
local a = "   123456789 "
local b = i:strim(a)
--将提示:123456789
syso(b)

说明：
常用于去除后进行判断头尾字符。

【slower 转换为小写】
用法：
local a = "AiufSUscN"
local b = i:slower(a)
--将提示:aiufsuscn
syso(b)

说明：
常用于转换为小写后进行判断。

【supper 转换为大写】
用法：
local a = "AiufSUscN"
local b = i:supper(a)
--将提示:AIUFSUSCN
syso(b)

说明：
常用于转换为大写后进行判断。


【stop 暂停代码】
用法：
i:t(
function()
syso("1")

i:stop(1000)
syso("2")

i:stop(1000)
syso("3")

i:stop(1000)
syso("4")
end
)

说明：
每次执行 i:stop(1000) 将暂停1秒后，再执行下面代码。单位为毫秒：1000毫秒 = 1秒


【sran 生产范围随机数】
用法：（生产一个 100 至 1000的随机数）
local a = i:sran(100, 1000)
syso(a)

说明：
有时候需要利用到随机机制，可以利用这个来开发！


【nsz 创建数组】
用法：
--声明6位的数组
local a2 = i:nsz(6)
syso(i:sgszl(a2))

或

//指定数组数据类型
local a2 = nsz(6, "String")


--声明数组，并直接设置数组值
local a2 = i:nsz({ 1, 2, 3, 4, 5 })

--打印数组总行数
local leng = i:sgszl(a2)
syso(leng)

--设置数组值
a[0] = 3 --设置第一位
a[1] = 6 --设置第二位

--循环打印数组所有
local leng = i:sgszl(a2)
local a = szto(a2)
while leng>0 do
leng = leng - 1

syso(a[leng])
end


说明：
申明一个数组。并且设置数组


【sgsz 指定访问数组维数】
用法：（根据序号访问数组）
local a = i:nsz({ 1, 2, 3, 4, 5 })
local d = i:sgsz(a, 2)
tw(d)

说明：
数组可以进行列表形式存储数据，常用于数据列表。注意的是序号是从0开始的。数组总行数如果是5，那序号最大为4

【sssz 设置数组数据】
用法：
local a = i:nsz({ 1, 2, 3, 4, 5 })
local c = 1
local d = 9
i:sssz(a, c, d)

说明：
指定数组序号设置数组的数据。


【sgszl 访问数组总行数】
用法：
local a = i:nsz({ 1, 2, 3, 4, 5 })
local d = i:sgszl(a)
tw(d)

说明：
可以获取到长度，更准确的访问数组


【hs 获取网页源码】
用法：
i:t(
function()
local a = "https://m.baidu.com/"
local b = i:hs(a)
syso(b)
end
)

2，提交post数据:
输入说明：地址，post数据提交，目标网页编码
i:t(
function()
local a = "https://m.baidu.com/"
local b = i:hs(a, "title=你好&text=你好吗？", "utf-8")
syso(b)
end
)

3，带自定义cookie方式获取网页:
--传递cookie项值，格式为nama=value 下例： uid=112;name=nihao;sb=123456789;
i:t(
function()
local a = "https://m.baidu.com/"
local b = i:hs(a, "title=你好&text=你好吗？", "utf-8", "uid=112;name=nihao;sb=123456789;")
syso(b)
end
)

4，带自动设置cookie方式获取网页，并记录当前网页的Cookie:
--传递cookie项值，当自定义为nil 系统将自动设置已记录的cookie
i:t(
function()
local a = "https://m.baidu.com/"
local b = i:hs(a, "title=你好&text=你好吗？", "utf-8", nil, true)
syso(b)
end
)

5，带自动设置cookie方式获取网页，并记录当前网页的Cookie，并设置Header头:（可设置多条，以“||”隔开）文件头包括了Cookie，User-Agent设备型号。
--传递cookie项值，当自定义为nil 系统将自动设置已记录的cookie
i:t(
function()
local a = "https://m.baidu.com/"
local b = i:hs(a, "title=你好&text=你好吗？", "utf-8", nil, true, "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept=*/*||accept-language=zh-CN")
syso(b)
end
)

6，带自动设置cookie方式获取网页，并记录当前网页的Cookie，并设置Header头:（可设置多条，以“||”隔开）文件头包括了Cookie，User-Agent设备型号，设置连接超时，设置接收超时，设置代理IP。
--传递cookie项值，当自定义为nil 系统将自动设置已记录的cookie
i:t(
function()
local a = "https://m.baidu.com/"
local b = i:hs(a, "title=你好&text=你好吗？", "utf-8", nil, true, "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept=*/*||accept-language=zh-CN", 20000, 20000, "10.0.0.172:80")
syso(b)
end
)

7，应用系统存储Cookie的浏览查看，返回赋值变量为字符串
local b = i:hs("cookie")

8，应用系统存储Cookie的清空，无赋值变量
i:hs("del cookie")

说明：
这里先开了一个线程，然后在线程里执行获取网页源码的工作，开线程是担心有些主线程界面。大部分网页都需要使用cookie登陆，可使用工具查询所需cookie然后进行操作。
设置cookie有说明作用？
1.登陆用户名
2.获取验证码图片并发送验证码
....


【hd 下载文件】
用法：（下载文件至SD卡根目录 abc.apk）

1，下载文件，默认不覆盖重复
i:t(
function()
local a = "http://abc.com/abc.apk"
local b = "abc.apk"
local c = i:hd(a, b)
syso(c)
end
)

2，设置重复是否覆盖
i:t(
function()
local a = "http://abc.com/abc.apk"
local b = "abc.apk"
local c = i:hd(a, b, true)
syso(c)
end
)


3，带自动设置cookie方式下载网页形式文件（如图片形式验证码，论坛的附件等），支持post数据，自定义Cookie或系统设置Cookie，并记录当前网页的Cookie，并设置重复是否覆盖。可参考hs获取网页，并设置Header头:（可设置多条，以“||”隔开，也可留空为nil）
输入说明：下载地址，保存文件位置，是否重复覆盖，post数据提交，目标网页编码，自定义Cookie，是否系统自动设置Cookie，设置Header头
i:t(
function()
local a = "http://abc.com/abc.apk"
local b = "abc.apk"
local c = i:hd(a, b, true, "title=你好&text=你好吗？", "utf-8", nil, true, nil)
syso(b)
end
)

说明：
开个线程，然后在里面下载一个文件。并存到SD卡。下载结果将赋值到变量“c”
返回的赋值：
1 文件已经存在
0 下载成功
-1 下载失败


【hw 访问网页】
用法：
local a = "https://m.baidu.com/"
i:hw(a)

说明：
使用内置浏览器访问网页。
可用于下载文件：
local a = "http://abc.com/abc.apk"
hw(a)


--跳转访问网页，并且自定义标题栏颜色
--主体颜色
local b = "#387bd6"
--底部横杠颜色
local c = "#255eab"
i:hw("https://m.baidu.com/", b, c)

【hws 系统浏览器访问网页】
用法：
local a = "https://m.baidu.com/"
i:hws(a)

说明：
使用内置浏览器访问网页。
可用于下载文件：
local a = "http://abc.com/abc.apk"
i:hws(a)


【ug 获取控件属性】
用法：(1为：控件ID，第二个参数为控件属性标识，然后赋值到变量)
local a = i:ug(1, "text")
syso(a)

说明：
输入属性标示来返回不同的控件数据。注意：有些控件没有指定属性，将返回nil。下面有属性介绍，可参考。

可用属性标识：
text=内容、background=背景、width=宽度、height=高度、x=X轴、y=Y轴、paddingleft=左内边距、paddingtop=顶内边距、paddingright右内边距、paddingbottom=底内边距、layout_marginleft=左外边距、layout_margintop=顶外边距、layout_marginright=右外边距、layout_marginbottom=底外边距、
hint=提示字符、imeoptions=虚拟键盘按键状态、visibility=控件可视状态、checked=选项是否被选中、title=浏览器网页标题、url=浏览器网址、lastvisibleposition=列表滑动到项目位置的序号、count=列表项目总数、
selecteditem=获取下拉框选值、rating=评分当前数值、progress=控件当前进度数值、date=日期控件选值、time=时间控件选值、currentitem=获得滑动窗体界面序号、isdraweropen=侧滑是否界面展开状态、selectionstart=获取文本框光标开始位置、selectionend=获取文本框光标结束位置、
cangoback=是否存在可返回的网页、cangoforward=是否存在可前进的网页、collapsecolumns=表格布局获取指定列是否折叠、shrinkcolumns=表格布局获取指定的列是否可收缩、stretchcolumns=表格布局获取指定的列是否可拉伸、shrinkcolumnsall=表格布局获取指示是否所有的列都是可收缩的、
stretchcolumnsall=表格布局获取指示是否所有的列都是可拉伸的



【us 设置控件属性】
用法：(1为：控件ID，第二个参数为控件属性标识，第三个是需要设置的数据或变量)

--设置文本控件内容
local c = "文本内容"
local f = i:us(1, "text", c)
syso(f)


--设置浏览器的连接url
local c = "https://m.baidu.com/"
local f = i:us(2, "url", c)
--提示：如果浏览器正在播放视频或音乐，直接关闭浏览器可能还会有声音，建议关闭浏览器时先跳转成另一个网页。
--提示：如果需要加载本地的文件，可以 us(2, "url", "file:///android_asset/res/web.html") 加载安装包内assets/res/web.html文件
syso(f)


--设置浏览器显示的html文件或文本
s c = "<html><p>html内容</></html>"
s d = "utf-8"
s e = "text/html"
local f = i:us(2, "url", c, d, e)
syso(f)


--设置控件阴影（部分控件有效果如文本、文本框、按钮）
local radius = 5
local dx = 0
local dy = 0
local color = "#000000"
local f = us(2, "shadow", radius, dx, dy, color)
syso(f)


--带有赋值变量，将返回数据是否设置成功 true 或 false
local c = "文本内容"
us(1, "text", c)

--设置文本框控件光标
us(1, "selection", 1)

--选中文本框部分内容
us(1, "selection", 1, 3)

--浏览器前进1个网页
us(1, "gobackorforward", 1)

--浏览器后退1个网页
us(1, "gobackorforward", -1)

--设置控件点击波纹效果颜色；需系统5.0以及以上才有效果；部分控件还需要设置 clickable=true 才有效果。
us(1, "backgroundripple", "#888888")

--设置编辑框光标颜色
us(1, "textcursordrawable", "#000000")

说明：
输入控件标示设置控件数据。【可参照控件属性，所有属性标识通用】

更多属性标识：
currentitem=设置滑动窗体界面序号、closedrawer=关闭指定侧滑、opendrawer=展开指定侧滑、drawerlockmode=设置手势滑动、selection=设置文本框光标位置、gobackorforward=浏览器的前进或推后、backgroundripple=波纹效果、dh=执行动画（非队列动画）


【uigo 跳转界面】
用法：（输入界面文件名，跳转指定的界面）
i:uigo("abc.ilua")

--带参数的跳转
i:uigo("abc.ilua", 536870912)


说明：
可以界面之间的转换，扩展新的界面。

参数：
67108864：如果在内存中发现存在该界面，则清空这个界面之上的所有其他界面，使其处于栈顶。
268435456：系统会寻找或创建一个新的内存来放置该界面
1073741824：跳转到的界面，不排在内存中
536870912：当内存中存在该界面并且位手机的显示状态时，不再创建一个新的，直接利用这个界面。


【utw 弹出界面】
用法：（在原有的界面弹出界面）
local a = nil
local b = "界面标题"
local c = "界面内容"
local d = "退出"
local e = "保存"
local f = "取消"

--三个按钮
--输入图标，输入标题，输入内容，输入按钮名称，输入按钮名称，输入按钮名称，输入是否点击弹窗以外界面是否关闭弹窗
i:utw(a, b, c, d, e, f, false,
function()
syso("点击了确定")
end
,
function()
syso("点击了保存")
end
,
function()
syso("点击了取消")
end
)

--两个按钮
i:utw(a, b, c, d, e, false,
function()
syso("点击了确定")
end
,
function()
syso("点击了取消")
end
)

-- 一个按钮
i:utw(a, b, c, d, false,
function()
syso("点击了确定")
end
)


--没有按钮
i:utw(a, b, c, false)

--将界面添加到弹窗界面上，直接将界面内容设为一个界面文件
local a = "界面标题"
local b = "a.ilua"
local c = "取消"

--返回界面对象，设置为全局变量 v
v = i:utw(nil, a, b, c, false, 
function()
syso("点击了取消")

--获取全局变量 v
local v = is("v")
syso(v)
end
)


说明：
常用于询问用户当前的操作，弹窗展示内容。

赋值变量说明：
弹出界面需要赋值到一个变量，用于自定义界面弹窗的操作。


【endutw 关闭弹出界面】
用法：
i:endutw()

说明：
用于关闭当前打开的弹窗界面

【end 结束界面】
用法：
i:end()

说明：
调用后，将结束当前的界面。 并返回原来的界面。如果原来没有界面，将退出应用。

【ends 显示桌面】
用法：
i:ends()

说明：
跳转到手机的桌面，程序将后台运行。


【bfm 播放音频】
用法：
local a = "http://www.abc.com/abc.mp3"
local b = i:bfm(a)

local a = "%abc.mp3"
local b = i:bfm(a)
-- 播放
-- i:bfms(b, "st")
-- 暂停
-- i:bfms(b, "pe")
-- 停止
-- i:bfms(b, "sp")
-- 结束播放组件
-- i:bfms(b, "re")
-- 是否在播放
-- i:bfms(b, "ip", c)
-- tw(c)

-- 获取音频时长（毫秒）
-- i:bfms(b, "dn", c)
-- tw(c)
-- 获取当前播放时长（毫秒）
-- i:bfms(b, "cn", c)
-- tw(c)

-- 指定播放的位置（毫秒）
-- i:bfms(b, "seekto", 2000)

-- 设置音量（0-100）
-- i:bfms(b, "volume", 100, 100)

-- 一直循环播放
-- i:bfms(b, "sl", true)

说明：
可以直接访问安装包里面的音频文件，也可以访问sd卡上的。
……

【html 标签支持】
用法：
local a = "(html)<a href="https://m.baidu.com">百度</a>"
i:us(1, "text", a)

说明：
text属性：设置支持html代码！


【ula 列表操作内容】
用法：
--输入数据列表对象，输入数据项...不限制数量。
local a
a = i:ula(a, {1,2,3}, {"abc","bac","bbc"})

--刷新列表显示内容，常用增加数据后的刷新。
i:ula(a)

--清空列表对象
i:ula(a, nil)
--i:ula(a, "clear")

--获得列表对象，赋值返回v变量为列表对象
local v = i:ula(a, "list")

说明：
根据数据列表，进行增加数据。

提示：
1 abc，其中1为控件id，abc为设置控件值
其中所谓的控件，为a.iyu界面中的控件。
增加标识数据，不作为设置控件数据，可在标识处设负数。如下：
-1 abc

提示：
如果需要设置 单选控件、多选控件 的选择状态，可设值为 true 或 false

注意：
将要执行事件的控件，必须在此设置值。如你有一个按钮控件无需设置值，但需要使用事件，可设置 1=nil
不设置值的控件，将无法获取列表内容数据。

【uls 列表显示内容】
用法：
--
local a
a = i:ula(a, {1,2,3}, {"abc","bac","bbc"})
a = i:ula(a, {1,2,3}, {"cde","cdw","cad"})
local c = "a.ilua"
local d = -1
local e = -2
--输入控件id或控件对象，输入数据列表，输入列表项界面文件名，输入界面宽度，输入界面高度
i:uls(1, a, c, d, e)

--设置下拉选择列表
--输入控件id或控件对象，输入数据列表或数组数据
i:uls(1, {"abc","bac","bbc"})

说明：
设置列表控件、视图控件、下拉列表的数据。

注意：
列表控件、视图控件 设置的界面 a.ilua 其中的载入事件是允许被调用。
可以通过列表控件、视图控件 设置的界面 a.ilua 的载入事件，进行每项列表布局的个性化设计。
每当显示到每项列表内容就会调用一次此载入事件，并且将该项的布局控件赋值给 st_vW 变量对象，
然后可以通过 local b = i:gvs(st_vW, "a.2") 获取其中的子控件对象，然后进行操作子控件即可。
还可以通过 st_pN 获取当前的视图中的序号，方便判断目前操作的是那一个视图。


【ulag 获取列表内容数据】
用法：

--输入当前的控件对象，输入获取控件ID 1的数据参数
local b = i:ulag(a, 1)

--输入当前的控件对象，输入获取标识为 -1的数据参数
local b = i:ulag(a, -1)

--通过 数据列表对象 或 列表控件对象 获取数据
--输入数据列表对象 或 列表控件对象，输入视图中的位置序号，输入获取标识为 -1的数据参数
local b = i:ulag(a, 1, -1)


说明：
常用与在列表控件的事件中，获取参数数据与用户进行互动。获取失败将赋值变量为 nil

注意：
使用此方法在uls中设置控件参数后，有设置参数的控件，在事件中可使用此方法。

【ulas 更新列表内容数据】
用法：

--输入当前的控件对象，输入获取控件ID 1的数据参数
local b = i:ulas(a, 1)

--输入当前的控件对象，输入获取标识为 -1的数据参数
local b = i:ulas(a, -1)

--通过 数据列表对象 或 列表控件对象 获取数据
--输入数据列表对象 或 列表控件对象，输入视图中的位置序号，输入获取标识为 -1的数据参数，输入新的数据
local b = i:ulas(a, 1, -1)

--刷新列表显示内容，常用增加数据后的刷新。
i:ula(a)

--V7列表，刷新指定序号列表项目显示内容，常用增加数据后的刷新。
i:ula(a, 2)

说明：
常用与更新修改列表内容数据。修改数据后，别忘记刷新列表。

【usms 发送短信】
用法：
local a = "10086"
local b = "0"
i:usms(a, b)

注意:测试时只显示syso日志，不直接 发送短信，打包即可。

【ucall 拨打电话】
用法：
local a = "10086"
i:ucall(a)

注意:测试时只显示syso日志，不直接 拨出号码，打包即可。

【time 当前时间】
用法：
local a = 0
local b = i:time(a)
syso(b)

说明：
第一个参数为时间类型，第二个赋值变量

[数字类型]
0：2014-07-07 09:10:08
1：2014/07/07 09:10:08
2：2014-07-07
3：09:10:08
4：18144133553151
5：2014年07月07日 09:10:08
[字符类型，输入字符形式需引号概括]
Y 年
m 月
d 日
H 时
M 分
S 秒
a/A 星期几


【fi 判断路径是否文件夹】
用法：
local a = "abc"
local b = i:fi(a)
syso(b)

说明：
指定路径，判断是否为目录文件夹，返回：true 或 false


【swh 获取屏幕分辨率】
用法：
local a = "w"
--获取屏幕宽度的dp
local w = i:swh(a)

local a = "h"
--获取屏幕高度的dp
local h = i:swh(a)

local a = "hh"
--获取屏幕真实高度的dp
local w = i:swh(a)

local a = "pxw"
--获取屏幕宽度的px像素
local w = i:swh(a)

local a = "pxh"
--获取屏幕高度的px像素
local h = i:swh(a)

local a = "pxhh"
--获取屏幕真实高度的px像素
local hh = i:swh(a)

local a = "pxztl"
//获取屏幕状态栏高度的px像素
local h = i:swh(a)

local a = "pxbvk"
//获取屏幕底部虚拟键盘的高度的px像素
local h = i:swh(a)

说明：
常用于获取屏幕的大小。

真实高度：不去除其他系统界面所占用（如状态栏）



【stobm 汉字转换编码字符】
用法：（你 转换 %E4%BD%A0）
local b = i:stobm("你", "utf-8")
tw(b)

说明：
有些时候网络操作的时候，网址需要带有字符参数，就可以把这个汉字转换下。

【sutf8to 将UTF-8编码字符转换中文】
local b = i:sutf8to("%E4%BD%A0")
tw(b)

【uycl 隐藏状态栏】
用法：
--隐藏
i:uycl(true)
--不隐藏
i:uycl(false)

// 将状态栏文字设为暗色，输入整数
i:uycl(1)

// 将状态栏文字设为亮色，输入整数
i:uycl(0)

// 进入全屏效果
i:uycl(-1)

// 退出全屏效果
i:uycl(-2)

// 隐藏底部导航条
i:uycl(-3)

// 不隐藏底部导航条
i:uycl(-4)

--输入更变颜色，并且保留状态栏空间，只设置状态栏，不设置软键盘
i:uycl("#50c4e5", true, 0)

--输入更变颜色，并且保留状态栏空间，只设置软键盘，不设置状态栏
i:uycl("#50c4e5", true, 1)

说明：
隐藏手机顶部的状态栏

【uycl 修改状态栏颜色】
用法：
--输入更变颜色，并且保留状态栏空间
i:uycl("#50c4e5", true)

--输入更变颜色，并且不保留状态栏空间
i:uycl("#50c4e5", false)


说明：
常用与设置一体化颜色，以及更变不同的状态栏颜色。

注意：
仅系统android 4.4以及以上才有效果，系统android 5.0以及以上效果更佳！
android 4.4以下的系统，无效果！

【ushsp 设置横屏或竖屏】
用法：
--横屏
i:ushsp(true)
--竖屏
i:ushsp(false)

说明：
设置屏幕的显示方式，注意的是设置后载入事件将重新执行


【bfv 播放视频】
用法：(播放SD卡上的视频文件)
local a = "%abcd.mp4"
i:bfv(a)

--并且横屏
local a = "%abcd.mp4"
local b = true
i:bfv(a, b)


--并且横屏
local a = "http://m.baidu.com/abcd.mp4"
local b = true
i:bfv(a, b)
说明：
此方法将全屏播放SD卡上的视频文件。调用自带的播放器。

注意：
不支持加载assets文件。支持SD卡文件、应用私有文件、（http）远程网络文件！

支持格式：
3gp、MP4、avi


【ftz 发送通知栏】
用法：
i:ftz("提醒标题", "标题", "内容", nil, [[
tw("点击了")
]])

--设置显示图标
i:ftz("提醒标题", "标题", "内容", "%abc.png", [[
tw("点击了")
]])

说明：
可以用于通知用户。


【uapp 打开App应用或游戏】
用法：
local c = i:uapp("com.iapp")

--或 带有指定类名的启动
local c = i:uapp("com.iapp", "com.yougaile.MakeiApp.logoActivity")

说明：
输入应用包名，赋值变量； 赋值变量返回启动结果：true 或 false

【uapplist 获取App列表】
用法：
local b = i:uapplist(true)
local c = b[1]
syso(c[0])

说明：
输入 是否包括获取系统App，返回一个列表数组 至变量 “b”，每列数据将存储一个应用的信息，并且以数组方式储存。

其中列数组内容序列：
0应用包名，1启动类，2应用标题，3应用版本


【uapplistgo 获取正在运行的App列表】
用法：
local b = i:uapplistgo()
syso(b[0])

说明：
输入 返回一个列表数组 至变量 “b”，每列数据将存储一个应用的信息，并且以 “\n”隔开。

其中列内容格式：
应用包名，pid, uid

【uninapp 卸载应用】
用法：
i:uninapp("com.iapp")

说明：
输入应用包名


【huf 上传文件】
用法：
t(
function()
local a = "http://abc.com/upfile.php"
local b = "filename=iApp我的应用.apk&test=一款非常好的应用哦"
local c = "%abc/iApp.apk"
-- 支持多文件上传
//local c = "%abc/iApp.apk|%abc/iApp2.apk|%abc/iApp3.apk"
local d = "utf-8"
local e = i:huf(a, b, c, d)
syso(e)
end
)

2.设置 header文件头，文件头包括了Cookie，User-Agent设备型号。。
t(
function()
local a = "http://abc.com/upfile.php"
local b = "filename=iApp我的应用.apk&test=一款非常好的应用哦"
local c = "%abc/iApp.apk"
-- 支持多文件上传
//local c = "%abc/iApp.apk|%abc/iApp2.apk|%abc/iApp3.apk"
local d = "utf-8"
local e = "User-Agent=Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||Cookie=aa=123;bb=456;||accept-language=zh-CN"
local f = huf(a, b, c, d, e)
syso(f)
end
)

说明：
输入 http接口，表单内容，手机内存选择文件，接口的网页编码， 赋值变量。 返回网页内容将赋值给变量 “e”


【nvw 创建动态控件】
用法：
--将控件添加至指定的控件作为子控件
--输入要添加的控件ID或控件对象，输入添加至指定控件ID或控件对象
i:nvw(id, did)

--输入要添加的控件ID或控件对象，输入添加至指定控件ID或控件对象，输入插入指定序号
i:nvw(id, did, 0)

--创建文本控件
--输入控件ID，输入添加至指定控件ID或控件对象（若不添加则输入nil），输入控件类型，输入控件属性
local id = 123456
local did = 1
local v = i:nvw(id, did, "文本", "width=-2\nheight=-2\ntext=内容")

说明：
输入创建的控件ID，输入将新控件添加至指定控件ID或控件对象，创建控件的类型，创建控件的属性


【uall 获取子控件】
用法：
--输入控件ID或控件对象，输入false时将赋值子控件ID，输入赋值变量将返回一个数据列表
local a = i:uall(1, false)

--输入控件ID或控件对象，输入true时将赋值子控件对象，输入赋值变量将返回一个数据列表
local a = i:uall(1, true)

tw(a[0])

说明：
获取一个包含子控件的，控件中所有的子控件。

【urvw 移除控件】
用法：
i:urvw(3)

说明：
输入需要移除的控件ID或控件对象


【sbp 图像分割】
用法：
--载入一个图像变量，并赋值到图像变量“b”
local b = i:sbp("%1.png")

--载入一个用户图标，{裁剪图像区域（像素）：x坐标:80，y坐标:90，裁剪宽度:50，裁剪高度:60}
--并将裁剪好的赋值到图像变量“b”
local b = i:sbp("%1.png", 80, 90, 50, 60)

--载入一个SD卡上的图标，{裁剪图像区域（像素）：x坐标:80，y坐标:90，裁剪宽度:50，裁剪高度:60}，图像旋转图像:180度
--并将裁剪好的赋值到图像变量“b”
local b = i:sbp("%1.png", 80, 90, 50, 60, 180)

说明：
三种方式载入图像，从图像变量，从用户图标，从SD上图标；并可设置裁剪图片；可设置图像旋转； 并赋值到新的图像变量；


【bfs 保存图像】
用法：
i:bfs(b, "%1.jpg")

--或 压缩比例（1至100）
i:bfs(b, 70, "%1.jpg")

说明：
输入图像变量，输入压缩比例（1至100），输入保存图像的路径，图像将保存至该路径。


【sdeg 启动调试模式】
用法：

i:sdeg(0)
i:sdeg(1)
i:sdeg(2)

说明：
提示日志方式。0打包后没有任何提示，1打包后可任然打印错误，2打包后记录日志保存至文件 iApp/Log


【tot 获取控件图标】
用法：
local id = 4
local b = i:tot(id)

说明：
输入控件ID或控件对象，返回将赋值“b”图像变量。注：此方法仅限于 图片控件，图标按钮控件。


【tzz 图像旋转】
用法：
local a = i:sbp("%1.png")
local b = 90
local c = i:tzz(a, b)

说明：
输入被旋转图像变量，输入旋转度数（逆向旋转数为负数），返回将赋值“c”图像变量。


【tsf 图像缩放】
用法：
local a = i:sbp("%1.png")

--按照倍增缩放，值小于则为缩小，否则为放大
local b = 2
local c = i:tsf(a, b)

--指定高度与宽度缩放
local w = 100
local h = 200
local c = i:tsf(a, w, h)

说明：
输入被缩放图像变量，输入缩放倍数 或 指定图像高度与宽度缩放，返回将赋值“c”图像变量。


【tfz 图像反转】
用法：
local a = i:sbp("%1.png")
--水平反转
local b = "x"
local c = i:tfz(a, b)

--垂直反转
local b = "y"
local c = i:tfz(a, b, c)

说明：
输入被反转图像变量，输入反转方式 x为水平 y为垂直，返回将赋值“c”图像变量。

【tcc 获取图像变量尺寸】
用法：
local a = i:sbp("%1.png")
local b = "w"
local c = i:tcc(a, b)
syso(c)

local b = "h"
local c = i:tcc(a, b)
syso(c)

说明：
获取图像变量的 w宽度 和 h高度。

【sxb 写入剪切板】
用法：
local a = "nihao"
i:sxb(a)

说明：
可用于复制到剪切板，其他应用可获取到此数据。

【shb 获取剪切板】
用法：
local a = i:shb()
syso(a)

说明：
可获取剪切板数据，得到其他地方写入的剪切板数据。

【usjxm 手机休眠】
用法：
i:usjxm(false)

说明：
设置后手机将不休眠，不锁屏。默认为 true 需要休眠。


【bfvs 播放视频】
用法：

--设置SD卡视频文件
--输入控件ID或对象，输入视频文件路径
i:bfvs(1, "%a.mp4")

--设置网络远程视频文件
i:bfvs(1, "http://abc.com/a.mp4")

--增加控制器，c为赋值变量
local c = i:bfvss(1, "media")
--开始播放
i:bfvss(1, "st")

说明：
自定义视频播放控件进行播放视频。

注意：
不支持加载assets文件。支持SD卡文件、（http）远程网络文件！

支持格式：
3gp、MP4、avi

【bfvss 播放视频控制】
用法：
--开始播放
i:bfvss(1, "st")

--暂停播放
i:bfvss(1, "pe")

--停止播放
i:bfvss(1, "sp")

--定位到指定帧
i:bfvss(1, "seekto", 300)

--增加控制器，c为赋值变量
local c = i:bfvss(1, "media")

--是否在播放
local c = i:bfvss(1, "ip")
tw(c)

--获取视频时长（毫秒）
local c = i:bfvss(1, "dn")
tw(c)

--获取当前播放时长（毫秒）
local c = i:bfvss(1, "cn")
tw(c)

【addv 加载界面】
用法：
--界面中载入其他界面
local id = 1
i:addv(id, "a.ilua")
i:addv(id, "b.ilua")

--侧滑窗体
local id = 1
i:addv(id, "a.ilua|b.ilua")

--滑动窗体，将带有赋值变量。此处变量“b”赋值为根控件列表，先通过 gslist 访问指定序号的根控件。通过 gvs 指定的根控件访问指定ID的控件。
local id = 1
local b = i:addv(id, "a.ilua|b.ilua")

说明：
输入控件ID，输入界面名，输入辅助参数。可用将一个界面的控件，载入到指定控件作为子控件。

如何设置或获取属性上例 a.ilua 中的控件呢？
通过文件名作为对象，进行访问，如：

--注意：此对象的使用方式。
local b = i:ug("a.2", "text")
i:us("a.3", "text", "你好")

注意：
如果载入事件中使用 addv 滑动窗体进行绑定， 如果还需要给滑动窗体内的界面中的控件设置数据，需要将设置控件的代码写在 载入完毕事件 中。否将将可能设置数据失败。

注意：
若增加 侧滑窗体 与 滑动窗体 的子控件，需要在被载入的界面设计中，自设一个根目录，作为界面唯一根目录。


【gvs 获取控件对象】
用法：
--根据当前界面，来获取控件
--输入要获取的控件ID，输入赋值变量
local c = i:gvs(1)

// 输入0 则获取界面的根控件对象，是一个系统控件，如需获取自己的用户控件可通过 i:gvs(root, 1) 或 i:uall(root, true) 再次获取它的内部的子控件。
local c = i:gvs(0)

--根据控件对象，来获取内部的子控件
--输入控件ID或控件对象，输入要获取的控件ID，输入赋值变量
local c = i:gvs(1, 2)

// 输入0 则获取其父控件对象，这里获取了控件ID1的父对象
local c = i:gvs(1, 0)

说明：
常用与于利用根控件获取内部的子控件 或 获取控件对象。获取失败将赋值返回 nil


【aslist 添加数据列表】
用法：
local a
a = i:aslist(a, {"你好", "你好", "你好"})
a = i:aslist(a, {"你好2", "你好2", "你好2"})

--可插入数据到指定序号
a = i:aslist(a, {"你好3","你好3"，"你好3"}, 1)

说明：
输入列表对象，输入要添加的数据，输入插入指定序号。


【sslist 数据列表设置数据】
用法：
local b = 1
local c = "数据"
i:sslist(a, b，c)


说明：
输入列表对象，输入指定数据序号，输入设置的数据

【gslist 数据列表访问数据】
用法：
local b = 1
local c = i:gslist(a, b)
syso(c)

说明：
输入列表对象，输入指定数据序号，输入赋值变量

【gslistl 数据列表访问数据总数】
用法：
local b = i:gslistl(a)
syso(b)

说明：
输入列表对象，输入赋值变量

【dslist 数据列表删除指定数据】
用法：
local b = 1
i:dslist(a, b)

--清空所有数据
local b = -1
i:dslist(a, b)

说明：
输入列表对象，输入指定数据序号

提示：
如果需要清空所有数据，[输入指定数据序号]可输入 -1 即会删除当前数据列表所有数据。

【gslistsz 列表数据转化为数组】
用法：
local b = i:gslistsz(a)

说明：
输入列表对象，输入赋值变量

【gslistis 列表数据检查是否存在指定数据】
用法：
local b = "数据"
local c = i:gslistis(a, b)

说明：
输入列表对象，被判断的数据，输入赋值变量。赋值数据：true 或 false

【gslistiof 列表数据从头开始检查是否包含该数据】
用法：
local b = "数据"
local c = i:gslistiof(a, b)

说明：
输入列表对象，被判断的数据，输入赋值变量

【gslistlof 列表数据从尾开始检查是否包含该数据】
用法：
local b = "数据"
local c = i:gslistlof(a, b)

说明：
输入列表对象，被判断的数据，输入赋值变量


【nuibs 背景选择器】
用法：
--使用颜色作为背景
local pressed = "#333333"
local selected = "#333333"
local normal = "#888888"
local b = i:nuibs(pressed, selected, normal)


--使用图像作为背景
local pressed = "%a.png"
local selected = "%a.png"
local normal = "%b.png"
local b = i:nuibs(pressed, selected, normal)


--使用渐变颜色作为背景
.配置选中状态背景
local a = 0
local b = 0
local c = "#255779|#3e7492|#a6c0cd"
local d = "0"
local e = "topbottom"
local pressed = i:ngde(a, b, c, d, e)

.配置正常状态背景
local a = 0
local b = 0
local c = "#255779|#3e7492|#a6c0cd"
local d = "0"
local e = "rightleft"
local normal = i:ngde(a, b, c, d, e)

local selected = pressed

local b = i:nuibs(pressed, selected, normal)

说明：
输入按下背景，输入选中背景，正常状态背景。


【ngde 背景调控器】
用法：
--输入圆角半径，输入背景填充色，输入赋值变量
local a = 15
local b = "#888888"
local c = i:ngde(a, b)

--输入边框宽度，输入背景填充色，输入边框颜色，输入赋值变量
local a = 5
local b = "#888888"
local c = "#333333"
local d = ngde(a, b, c)

--输入边框宽度，输入圆角半径，输入背景填充色，输入边框颜色，输入赋值变量
local a = 5
local b = 15
local c = "#888888"
local d = "#333333"
local e = i:ngde(a, b, c, d)

--颜色渐变。输入边框宽度，输入圆角半径，输入背景填充渐变色组，输入边框颜色，输入颜色渐变方向，输入赋值变量
local a = 5
local b = 15
local c = "#255779|#3e7492|#a6c0cd"
local d = "#333333"
local e = "topbottom"
local f = i:ngde(a, b, c, d, e)

说明：
背景空调生成的赋值变量，可配合背景选择器进行应用。

注意：
ngde 代码将赋值返回一个背景对象，此背景对象如果被多个不同大小的控件引用为背景。因为控件的大小不同，会导致此背景对象大小被修改。从而影响其他引用者控件。

提示：
边框与圆角半径 若不想调整，可设值为0 。适用于颜色渐变，不需要调节圆角半径和边框。

颜色渐变方向说明：
	topbottom：绘制从顶部梯度至底部
	trbl：借鉴右上角渐变左下角
	rightleft：绘制从右侧的梯度向左
	brtl：借鉴右下角渐变左上角
	bottomtop：绘制从底部梯度顶端
	bltr：借鉴渐变左下角到右上角
	leftright：绘制从左侧的梯度向右
	TL_BR：从绘制渐变的左上角到右下角

【sit 目标的设置】
用法：
--如，分享软件
--输入对象，输入属性标识，输入属性值
local a
a = i:sit(a, "action", "android.intent.action.SEND")
a = i:sit(a, "type", "text/plain")
a = i:sit(a, "extra", "android.intent.extra.SUBJECT", "共享软件")
a = i:sit(a, "extra", "android.intent.extra.TEXT", "共享内容文本")
a = i:sit(a, "flags", 268435456)
i:uit(a, "chooser", "标题")

说明：
常用于调用系统程序以及功能 或 第三方程序功能。

可属性标识：action、type、extra、flags、data、classname、component

【uit 目标的执行】
用法：
--输入目标对象，输入属性，输入属性值
i:uit(a, "chooser", "标题")

--输入目标对象，输入属性，输入请求数值
i:uit(a, "result", 1)

--输入目标对象
i:uit(a)

说明：
常用于调用系统程序以及功能 或 第三方程序功能。

属性支持：chooser、result

【git 目标获取参数】
用法：
--输入目标对象，输入属性标识
local c = i:git(a, "action")
local c = i:git(a, "type")
local c = i:git(a, "extra", "title")
local c = i:git(a, "flags")

说明：
获取目标的属性。

【uqr 二维码扫描】
用法：

--扫描二维码
i:uqr()

--扫描结果，需要在 回调结果事件 写代码
if st_sC == 1102 then

local c = i:git(st_iT, "extra", "result")
syso(c)

end


--生成二维码图像
s a = "https://m.baidu.com"
--输入字符串数据，输入图像长宽像素；将返回一个图像变量
local c = i:uqr(a, 400)


--识别二维码图像
--输入图像变量或图片路径；将返回一个字符串
local c = i:uqr(a)

说明：
常用于网络通用二维码扫描。

【zdp  dip转换px】
用法：
local dp = 10
--输入dp数值
local c = i:zdp(dp)

说明：
用于常用数据转换。

【zpd  px转换dip】
用法：
local px = 10
--输入px数值，输入赋值变量
local c = i:zpd(px)

说明：
用于常用数据转换。

【zps  px转换sp】
用法：
local px = 10
--输入px数值，输入赋值变量
local c = i:zps(px)

说明：
用于常用数据转换。

【zsp  sp转换px】
用法：
local sp = 10
--输入sp数值，输入赋值变量
local c = i:zsp(sp)

说明：
用于常用数据转换。

【lan 跳转界面动画】
用法：
i:uigo("abc.ilua")
--输入跳转界面动画的序号；6 右往左推出效果
i:lan(6)

说明：
用于跳转界面时候进行的动画效果

提示：
0.淡入淡出效果 1.放大淡出效果 2.转动淡出效果1 3.转动淡出效果2 4.左上角展开淡出效果 5.压缩变小淡出效果 6.右往左推出效果 7.下往上推出效果 8.左右交错效果 9.放大淡出效果 10.缩小效果 11.上下交错效果


【sjxx 获取设备信息】
用法：
local a = i:sjxx()
syso(a[0])

说明：
获取手机基本信息，将返回一个数组到赋值变量“a”，数组格式如下：

数据格式：（真实数据 \n 旁边将不没有空格）

CPU型号 \n CPU频率
屏幕宽度 \n 屏幕高度 \n 分辨率宽度 \n 分辨率高度
手机型号 \n 手机品牌 \n 手机SDK

【simsi 获取设备imsi】
用法：
local a = i:simsi()
syso(a)

说明：
常用于识别用户的手段。

【simei 获取设备imei】
用法：
local a = i:simei()
syso(a)

说明：
常用于识别用户的手段。

【endkeyboard 强制隐藏虚拟键盘】
用法：
i:endkeyboard()

说明：
常用于需要隐藏安卓弹出的虚拟键盘。


【hdfl 文件下载器】
用法：
--两个参数的方法设置
local savedir = "%SaveDir"
--输入下载保存目录，输入赋值变量返回一个下载器对象
a = i:hdfl(savedir,
function(st_drD,st_drI)
--每当下载完一个执行
--系统赋值 st_drD 文件下载项的序号
--系统赋值 st_drI 文件下载项的状态

--获取下载的URL
local b1 = i:ulag(a, st_drD, "url")
syso(b1)

--获取自定义整数标识
local b2 = i:ulag(a, st_drD, "type")
syso(b2)

--获取自定义参数任意数据
local b3 = i:ulag(a, st_drD, "text")
syso(b3)

--获取下载文件保存的路径
local b4 = i:ulag(a, st_drD, "filename")
syso(b4)

end
,
function(st_drJ)

--当下载完目前所有执行
--系统赋值 st_drJ 本次文件下载完成总数
syso(st_drJ)
end
)


--三个参数的方法设置
local tempdir = "%TempDir"
local savedir = "%SaveDir"
--输入下载临时文件保存目录，输入下载保存目录，输入赋值变量返回一个下载器对象
local a = i:hdfl(tempdir, savedir,
function(st_drD,st_drI)

syso(st_drD)
end
,
function(st_drJ)

syso(st_drJ)
end
)

--六个参数的方法设置
local tempdir = "%TempDir"
local savedir = "%SaveDir"
--输入下载临时文件保存目录，输入下载保存目录, 下载线程数量，连接网络超时时间（25秒的意思），文件重复是否覆盖，输入赋值变量返回一个下载器对象
local a = i:hdfl(tempdir, savedir, 3, 25000, true,
function(st_drD,st_drI)

syso(st_drD)
end
,
function(st_drJ)

syso(st_drJ)
end
)

说明：
常用与单个或多个的文件下载。推荐图片列表下载或小文件下载。

提示：
代码 区域中 属于线程内执行。在其中更新界面控件属性需要使用ufnsui代码
上例子使用tw代码，并且用了ufnsui代码。



【hdfla 文件下载器 增加文件下载项】
用法：
--创建一个文件下载器
local a = i:hdfl("%TempDir",
function()

syso(st_drD)
end
,
function()

syso(st_drJ)
end
)

--增加下载项
--输入下载器对象，输入下载连接URL，输入自定义整数标识，输入自定义参数任意数据
i:hdfla(a, "http://abc.com/1.jpg", 1, "abcd123")


--增加下载项，并且自定义保存目录
--输入下载器对象，输入下载连接URL，输入自定义整数标识，输入自定义参数任意数据，输入自定义保存路径
i:hdfla(a, "http://abc.com/2.jpg", 1, "abcd123", "%abc.jpg")

说明：
调用下载器增加下载项，并且立刻进行下载。

【hdd 配置下载管理器】
用法：
--下载产生的临时文件目录
local a = "%tempdir"
--下载至保存的目录
local b = "%filedir"
--允许同时下载任务数量
local c = 3
--每个任务开启线程数量
local d = 3
--连接失败重试次数
local e = 2
--连接超时时间，25秒的意思
local f = 25000
--是否显示下载进度通知
local g = true
i:hdd(a, b, c, d, e, f, g)

说明：
如果不使用此代码进行配置，那么系统将使用默认配置。下载配置器可以很方便的制作下载文件，并且方便管理。

默认目录属性：
临时文件目录：iApp/DownloadFileDir/TempDefaultDownFile
保存文件目录：iApp/DownloadFileDir/DefaultDownFile

【hdda 下载管理器 增加文件下载项】
用法：

--===========方法一
--下载的链接
local url = "http://abc.com/abc.apk"

--保存的文件名（仅输入文件名,请勿不包含目录）
local name = "abc.apk"

--输入自定义参数任意数据
local data = "abcde123"

--变量v为赋值变量，为下载对象
local v = i:hdda(url, name, data)

--===========方法二
--下载的链接
local url = "http://abc.com/abc.apk"

--保存的文件名（仅输入文件名,请勿不包含目录）
local name = "abc.apk"

--下载任务的标题
local title = "abc.apk最新版"

--输入自定义参数任意数据
local data = "abcde123"

--变量v为赋值变量，为下载对象
local v = i:hdda(url, name, title, data)

--===========方法三
--下载的链接
local url = "http://abc.com/abc.apk"

--保存的文件名（仅输入文件名,请勿不包含目录）
local name = "abc.apk"

--下载任务的标题
local title = "abc.apk最新版"

--下载任务的图标
local icon = "@abc.png"

--输入自定义参数任意数据
local data = "abcde123"

--变量v为赋值变量，为下载对象
local v = i:hdda(url, name, title, icon, data)

--===========方法四
--下载的链接
local url = "http://abc.com/abc.apk"

--保存至目录
local dir = "%filedir"

--保存的文件名（仅输入文件名,请勿不包含目录）
local name = "abc.apk"

--下载任务的标题
local title = "abc.apk最新版"

--下载任务的图标
local icon = "@abc.png"

--是否显示下载进度通知
local notsohw = true

--输入自定义参数任意数据
local data = "abcde123"

--变量v为赋值变量，为下载对象
local v = i:hdda(url, dir, name, title, icon, notsohw, data)

说明：
增加常用的网络文件进行下载。

【hddgl 获取下载管理器下载列表】
用法：
--输入赋值变量返回下载列表
local list = i:hddgl()

--获取第一位数据
local b = i:gslist(list, 0)
local c = i:hddg(b, "url")
syso(c)

说明：
获取下载管理器所有的下载列表。

【hddg 获取下载管理器获取下载项属性】
用法：
--下载的链接
local url = "http://abc.com/abc.apk"
--保存的文件名（仅输入文件名,请勿不包含目录）
local name = "abc.apk"
--输入自定义参数任意数据
local data = "abcde123"
--变量v为赋值变量，为下载对象
local v = i:hdda(url, name, data)

--===========获取下载项的属性
--获取下载项的 ID
local b = i:hddg(v, "id")

--获取下载项的 下载链接
local b = i:hddg(v, "url")

--获取下载项的 保存的绝对路径
local b = i:hddg(v, "dirfilename")

--获取下载项的 下载链接的md5
local b = i:hddg(v, "urlmd5")

--获取下载项的 保存的目录
local b = i:hddg(v, "dir")

--获取下载项的 保存的文件名
local b = i:hddg(v, "filename")

--获取下载项的 下载文件的大小（字节）
local b = i:hddg(v, "contentlength")

--获取下载项的 已下载的数据（字节）
local b = i:hddg(v, "equivalent")

--获取下载项的 当前下载速度（字节）
local b = i:hddg(v, "downloadspeed")

--获取下载项的 当前下载进度百分比
local b = i:hddg(v, "downloadpercentage")

--获取下载项的 下载状态；（0为等待下载；1为正在下载；2为下载完成；3下载已经暂停或停止；-1下载失败；-2已删除）
local b = i:hddg(v, "status")

--获取下载项的 是否显示下载通知
local b = i:hddg(v, "notificationshow")

--获取下载项的 自定义的数据
local b = i:hddg(v, "text")

--获取下载项的 通知标题
local b = i:hddg(v, "title")

--获取下载项的 通知图标
local b = i:hddg(v, "icon")

说明：
可获取详细的下载项目状态属性。

【hdds 设置下载管理器下载项的属性】
用法：
--下载的链接
local url = "http://abc.com/abc.apk"
--保存的文件名（仅输入文件名,请勿不包含目录）
local name = "abc.apk"
--输入自定义参数任意数据
local data = "abcde123"
--变量v为赋值变量，为下载对象
local v = i:hdda(url, name, data)

--===========可设置的下载项属性

--设置下载项的 下载状态；（0为等待下载；1为正在下载；2为下载完成；3下载已经暂停或停止；-1下载失败；-2已删除）
i:hdds(v, "status", 0)

--设置下载项的 是否显示下载通知
i:hdds(v, "notificationshow", true)

--设置下载项的 自定义的数据
i:hdds(v, "text", "abcd123")

--设置下载项的 通知标题
i:hdds(v, "title", "abc.apk最新版本")

--设置下载项的 通知图标
i:hdds(v, "icon", "@abc.png")

说明：
设置下载项目的属性。

【hdduigo 跳转至下载管理器】
用法：
--跳转至下载管理器
i:hdduigo()

--跳转至下载管理器，并且自定义标题栏颜色
--主体颜色
local a = "#387bd6"
--底部横杠颜色
local b = "#255eab"
i:hdduigo(a, b)

说明：
跳转至文件下载的管理器。

【ufnsui 线程更新界面】
用法：
i:ufnsui(
function()

tw(a)
us(1, "text", "内容")

end
)

说明：
线程中直接修改界面或修改设置控件属性，出错。
需要使用ufnsui模块进行更新或设置控件属性。

提示：
线程中获取控件数据不会出错。


【se 正则表达式操作】
用法：
--===========例子1；所有属性展示
--字符串
local a = "qqqq123456eee"
--正则表达式
local b = "([a-z]+)(\\d+)"
--更多参数
local c = 0
local d = i:se(a, b, c)
syso(d)

--替换成，将替换全部
local e = i:se(d, "sral", "1:$1, 2:$2")
syso(e)
--替换成，只替换第一个
local e = i:se(d, "srft", "1:$1, 2:$2")
syso(e)

--返回是否匹配成功，赋值返回true或 false
.local e = i:se(d, "ms")

--开始匹配 或 匹配下一个，赋值返回true或 false
.local e = i:se(d, "find")

--给定位置序号进行匹配，赋值返回true或 false
.local e = i:se(d, "find", 1)

--获取匹配组的数量，当前为2组：([a-z]+)、(\d+)
.local e = i:se(d, "gl")

--获取第1组匹配到的子字符串在字符串中的开头位置 
.local e = i:se(d, "start", 1)

--获取第1组匹配到的子字符串在字符串中的结尾位置 
.local e = i:se(d, "end", 1)

--获取第1组匹配到的子字符串
.local e = i:se(d, "group", 1)
--获取第2组匹配到的子字符串
.local e = i:se(d, "group", 2)


--===========例子2；获取所有手机号

--字符串
local a = "我的号码 13612345678 , 你的号码 13412345678"
--正则表达式
local b = "[1][3-8]\\d{9}"
--更多参数
local c = 0
local d = i:se(a, b, c)

--开始匹配 或 匹配下一个
local ee = i:se(d, "find")

--循环判断是否匹配成功
while ee do
--因为 [1][3-8]\\d{9} 没有组，所以这里我们输入 0
local e = i:se(d, "group", 0)

--打印出匹配到的子字符串
syso(e)

--开始匹配 或 匹配下一个
ee = i:se(d, "find")
end

--===========例子3；判断是否为手机号

--字符串
local a = "13612345678"
--正则表达式
local b = "^[1][3-8]\\d{9}$"
--更多参数
local c = 0
local d = i:se(a, b, c)

local e = i:se(d, "ms")
if e == true then
syso("手机号格式正确")
else
syso("手机号格式错误")
end


说明：
常用与字符串处理，高效的处理字符串，以及检测字符串类型等。使用此方法，需要对正则表达式有部分知识。

【usg 闪光灯操作】
用法：
--开启闪光灯
--输入闪光灯变量对象，输入是否开启闪光灯
local sgd
sgd = i:usg(sgd, true)

--关闭闪光灯
--输入闪光灯变量对象，输入是否开启闪光灯
sgd = i:usg(sgd, false)

说明：
开启或关闭 设备闪光灯！

说明：
常用照明。

注意：
此方法调用将无法与摄像头同时调用。如启动摄像头需要使用闪光灯，可在摄像头操作中开启闪光灯。

【uzd 震动器操作】
用法：
--震动1秒时长
--输入振动器变量对象，输入震动时长
local zdq
zdq = i:uzd(zdq, 1000)

--静止1秒，震动1秒，静止1秒，震动1秒，静止1秒，震动1秒，静止1秒，..， 并且不重复
--输入振动器变量对象，输入震动规则，输入是否重复循环执行
zdq = i:uzd(zdq, { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 }, false)

--强制停止震动器
i:uzd(zdq, "sp")

--检查硬件是否具有振动器
local b = i:uzd(zdq, "ip")
syso(b)

说明：
常用提示用户。

【usxq 开启前置摄像头】
用法：
local ps
--开启摄像头
--输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度
ps = i:usxq(ps, 1, 90)

--输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度，输入拍摄宽度像素，输入拍摄高度像素，输入图像品质1-100
ps = i:usxq(ps, 1, 90, 640, 480, 95)

--自动对焦拍摄
--输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
ps = i:usx(ps, "shot", "%abc.jpg", -90, false)

说明：
指定打开前置摄像头。

注意：
此功能需要与一个面控件进行绑定，你可以在面控件上面设置拍摄事件。

注意：
此代码仅限于载入事件调用。

【usxh 开启后置摄像头】
用法：
local ps
--开启摄像头
--输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度
ps = i:usxh(ps, 1, 90)

--输入摄像头变量对象，输入面控件的对象或ID，摄像头旋转角度，输入拍摄宽度像素，输入拍摄高度像素，输入图像品质1-100
ps = i:usxh(ps, 1, 90, 1280, 960, 95)

--自动对焦拍摄
--输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
ps = i:usx(ps, "shot", "%abc.jpg", 90, false)

说明：
指定打开后置摄像头。

注意：
此功能需要与一个面控件进行绑定，你可以在面控件上面设置拍摄事件。

注意：
此代码仅限于载入事件调用。

【usx 摄像头操作】
用法：
--开启摄像头
i:usxh(ps, 1, 90)

--自动对焦拍摄
--输入摄像头变量对象，输入保存路径，输入图像旋转角度，输入拍摄是否停止预览
i:usx(ps, "shot", "%abc.jpg", 90, false)

--开始预览
i:usx(ps, "st")

--停止预览
i:usx(ps, "sp")

--旋转摄像头角度
i:usx(ps, "rotaing", 180)
--获取旋转摄像头角度
local b = i:usx(ps, "getrotaing")
syso(b)

--启动摄像头闪光灯
i:usx(ps, "usg", true)

--结束摄像头组件变量对象
i:usx(ps, "re")

说明：
摄像头的控制。

【bly 录制音频】
用法：
local ly
--开始录制
--输入录音变量对象，输入保存文件路径
i:bly(ly, "%abcd.amr")

--停止录音
i:bly(ly, "sp")

说明：
常用于录制音频。

说明：
可使用 bfm 代码来播放录制好的音频。

【ujp 截取屏幕】
用法：
--输入保存路径，输入图像品质（1-100）
i:ujp("%123.jpg", 70)

说明：
常用于截取当前界面。

【sqlite 数据库操作】
用法：
local data
--连接一个私有数据库，如果不存在将自动新建
--输入数据库对象变量，输入数据库文件名
i:sqlite(data, "iapp.db")

--连接一个公共数据库，如果不存在将自动新建
--输入数据库对象变量，输入数据库文件名
i:sqlite(data, "%iapp.db")

--判断数据库是否存在
local b = i:sqlite("iapp.db", "ip")
syso(b)

--删除数据库
local b = i:sqlite("iapp.db", "del")
syso(b)

--释放数据库
i:sqlite(data, "re")

说明：
进行数据库的操作。

【sql 数据表操作】
用法：

--创建数据表
local table = "_id integer primary key,url text, filename text,status interger,createTime datetime"
local b = i:sql(data, "info", "add", table)

--判断数据表是否存在
local b = i:sql(data, "info", "ip")
syso(b)

--删除数据表
local b = i:sql(data, "info", "del")
syso(b)

--添加数据表一条数据
local table = "url,filename,status,createTime"
local value = "'http://abc.com/abc.apk', 'abc.apk', 1, '" .. i:time(0) .. "'"
local b = i:sql(data, "info", "add", table, value)
syso(b)

--修改数据表的数据，若不需要设置条件(status=2)可设为 nil 视为适用于执行所以数据
local b = i:sql(sss.data, "info", "up", "status=2", "_id=1")
syso(b)

--删除数据表的数据，若不需要设置条件(_id=1)可设为 nil 视为适用于执行所以数据
local b = i:sql(sss.data, "info", "del", "_id=1")
syso(b)


--查询，若不需要设置条件(status=1 order by _id desc LIMIT 0,1)可设为 nil 视为适用于执行所以数据

-- LIMIT <跳过的数据数目>, <取数据数目>
local table = "_id,url,filename,status,createTime"
local sqlx = "status=1 order by _id desc LIMIT 0,1"
local da = i:sql(sss.data, "info", "sele", table, sqlx)

--自定义sql查询
--local sqlx = "select _id,url,filename,status,createTime from info where status=1 order by _id desc LIMIT 0,1"
--local da = i:sql(data, sqlx)

--光标对象移到下一条数据
local ee = i:sqlsele(da, "next")
while ee do

--获取光标对象的第一列数据
local e = i:sqlsele(data, 0)
syso(e)

--获取光标对象的第二列数据
local e = i:sqlsele(data, 1)
syso(e)

--光标对象移到下一条数据
ee = i:sqlsele(data, "next")
end


--自定义的sql执行，需要对sql语法了解才能灵活运用
local sqlx = "insert into info (url,filename,status,createTime) values ('http://abc.com/abc.apk', 'abc.apk', 1, '2016-7-31 10:31:21')"
i:sql(sqlx, data)

说明：
数据表的操作。

注意：
在执行sql语句的时候，需要注意你的字符串的特殊字符的转义。
     /   ->    //
     '   ->    ''
     [   ->    /[
     ]   ->    /]
     %   ->    /%
     &   ->    /&
     _   ->    /_
     (   ->    /(
     )   ->    /)

【sqlsele 查询数据操作】
用法：

--获取光标对象的第一列数据
local e = i:sqlsele(data, 0)

--获取光标对象有多少列
local e = i:sqlsele(data, "columncount")
syso(e)

--获取总共查询到多少条数据
local e = i:sqlsele(data, "count")
syso(e)

--光标对象移到下一条数据
local e = i:sqlsele(data, "next")

--光标对象移到上一条数据
local e = i:sqlsele(data, "previous")

--光标对象移到第一条数据
local e = i:sqlsele(data, "first")

--光标对象移到最后第一条数据
local e = i:sqlsele(data, "last")

--光标对象移到指定第2条数据
i:sqlsele(data, "position", 2)

--获取光标对象当前位置
local e = i:sqlsele(data, "getposition")
syso(e)

--释放数据查询
i:sqlite(data, "re")

说明：
数据查询的操作。

【dha 渐变透明度动画】
用法：
--创建一个渐变透明度动画，开始显示，然后渐变消失
--输入动画开始是否透明，输入动画结束是否透明
local dh = i:dha(true, false)
i:dh(dh, "duration", 2000)
i:us(2, "dh", dh)

说明：
常用于控件透明度动画。

【dhs 渐变尺寸伸缩动画】
用法：
--创建一个渐变尺寸伸缩动画
--0为没有，2.5为原始2.5倍

--输入X开始尺寸比例，输入X结束尺寸比例，输入Y开始尺寸比例，输入Y结束尺寸比例
local dh = i:dhs(0.5, 2.5, 0.5, 2.5)
i:dh(dh, "duration", 2000)
i:us(2, "dh", dh)

--输入X开始尺寸比例，输入X结束尺寸比例，输入Y开始尺寸比例，输入Y结束尺寸比例，输入X位置类型，输入X坐标的开始位置，输入Y位置类型，输入Y坐标的开始位置
local dh = i:dhs(0.5, 2.5, 0.5, 2.5, 1, 0.5, 1, 0.5)
i:dh(dh, "duration", 2000)
i:us(2, "dh", dh)

说明：
常用于控件伸缩动画。

位置类型：
0 默认
1 以对象本身为基准位置类型
2 以父控件为基准位置类型

【dht 画面位置移动动画】
用法：
--创建一个画面位置移动动画
--输入开始X坐标上的移动位置，结束X坐标上的移动位置，开始Y坐标上的移动位置，结束Y坐标上的移动位置
local dh = i:dht(30, 80, 30, 80)
i:dh(dh, "duration", 2000)
i:us(2, "dh", dh)

说明：
常用于控件移动动画。

【dhr 画面旋转动画】
用法：
--创建一个画面旋转动画
--输入动画开始的旋转角度，输入动画旋转到的角度
local dh = i:dhr(0, 180)
i:dh(dh, "duration", 2000)
i:us(2, "dh", dh)

--输入动画开始的旋转角度，输入动画旋转到的角度，输入X位置类型，输入X坐标的开始位置，输入Y位置类型，输入Y坐标的开始位置
local dh = i:dhr(0, 180, 1, 0.5, 1, 0.5)
i:dh(dh, "duration", 2000)
i:us(2, "dh", dh)

说明：
常用于控件旋转动画。

位置类型：
0 默认
1 以对象本身为基准位置类型
2 以父控件为基准位置类型

【dhset 动画集合】
用法：

--渐变尺寸伸缩动画
local dh1 = i:dhs(0.5, 2.5, 0.5, 2.5)
i:dh(dh1, "duration", 2000)

--画面位置移动动画
local dh2 = i:dht(30, 80, 30, 80)
i:dh(dh2, "duration", 2000)

--画面旋转动画
local dh3 = i:dhr(0, 180)
i:dh(dh3, "duration", 2000)

--创建一个动画集合
--输入动画集合变量对象，输入是否使用动画集合的interpolator，输入动画...（可输入N个参数）
local dhlist = i:dhset(false, {dh1, dh2, dh3, dh4})
i:us(2, "dh", dhlist)
	
说明：
常用于动画集合执行。

提示：
动画集合允许被其他动画集合添加成为子动画。

提示：
动画集合如果设置了动画控制属性，同时也会重置所有子控件的属性。

【dhas 队列动画执行】
用法：
--旋转动画
--输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入旋转角度...（可输入N个参数）
local dh = i:dhas(2, "rotation", {60, 180})
--local dh = i:dhas(2, "rotationX", {30, 80, 60, 20, 60})
--local dh = i:dhas(2, "rotationY", {30, 80})
i:dh(dh, "duration", 2000)
i:dh(dh, "start")

--伸缩动画
--输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入伸缩尺寸比例...（可输入N个参数）
local dh = i:dhas(2, "scaleX", {1.5, 2.5})
--local dh = i:dhas(2, "scaleY", {1.5, 2.5, 1.2, 2.6, 1.3})
i:dh(dh, "duration", 2000)
i:dh(dh, "start")

--移动动画
--输入动画变量对象，输入控件ID或控件对象，输入动画类型，输入移动到位置...（可输入N个参数）
local dh = i:dhas(2, "translationX", {0, 60})
--local dh = i:dhas(2, "translationY", {0, 60, 30, 10, 60})
i:dh(dh, "duration", 2000)
i:dh(dh, "start")

--透明度
--输入动画变量对象，输入控件ID或控件对象，输入动画类型，可见度比例(0.0至1.0)...（可输入N个参数）
local dh = i:dhas(2, "alpha", {1, 0.3, 1, 0.2, 1})
i:dh(dh, "duration", 2000)
i:dh(dh, "start")

说明：
自定义队列动画执行。


【dhast 队列动画集合】
用法：

--旋转动画
local dh1 = i:dhas(2, "rotation", {60, 180})
i:dh(dh1, "duration", 2000)

--伸缩动画
local dh2 = i:dhas(2, "scaleX", {1.5, 2.5})
i:dh(dh2, "duration", 2000)

--移动动画
local dh3 = i:dhas(2, "translationX", {0, 60})
i:dh(dh3, "duration", 2000)

--透明度
local dh4 = i:dhas(2, "alpha", {1, 0.3, 1, 0.2, 1})
i:dh(dh4, "duration", 2000)

--顺序执行
local dhlist = i:dhast("sequen", {dh1, dh2, dh3, dh4})

--同时执行
--local dhlist = i:dhast("together", {dh1, dh2, dh3, dh4})
i:dh(dhlist, "start")

说明：
常用于动画集合执行。

提示：
队列动画集合允许被其他队列动画集合添加成为子动画。

提示：
动画集合如果设置了动画控制属性，同时也会重置所有子控件的属性。


【dh 动画控制】
用法：

--========动画的属性（非队列动画）设置========================

--取消动画，取消后若需要重新播放，需要先执行 reset 然后再执行 start 进行播放
i:dh(dh, "cancel")

--重置动画属性
i:dh(dh, "reset")

--启动动画
i:dh(dh, "start")

--动画持续时长
i:dh(dh, "duration", 2000)

--延迟执行，延迟指定时长后才执行动画
i:dh(dh, "delay", 2000)

--启动动画结束填充效果（如果设false 那么 after 与 before将无效）
i:dh(dh, "enabled", true)

--动画执行后，控件停留执行结束状态
i:dh(dh, "after", true)

--动画执行后，控件停留执行开始状态
i:dh(dh, "before", true)

--动画重复执行的次数
i:dh(dh, "repeat", 20)

local dh2 = i:dhas(2, "rotation", 60, 180)
--动画集合添加动画，仅用于 dhset 动画集合追加更多的动画
i:dh(dh, "add", dh2)

--========队列动画的属性设置========================

--取消动画
i:dh(dh, "cancel")

--播放动画
i:dh(dh, "start")

--动画持续时长
i:dh(dh, "duration", 2000)

--延迟执行，延迟指定时长后才执行动画
i:dh(dh, "delay", 2000)

--动画是否正在运行
local b = i:dh(dh, "running")
syso(b)

--设置动画执行的控件ID或控件对象
i:dh(dh, "target", 2)

--克隆动画
local dh2 = i:dh(dh, "clone")

说明：
常用于动画的控制管理。

【dhon 动画监听事件】
用法：
--========动画（非队列动画）设置监听事件========================

i:dhon(dh,
function()
syso("End")
end
,
nil
,
nil
)
--或

i:dhon(dh,
function()
syso("End")
end
,
function()
syso("Repeat")
end
,
function()
syso("Start")
end
)


--========队列动画设置监听事件========================

i:dhon(dh,
function()
--当结束动画时
syso("End")
end
,
function()
--当重复动画时
syso("Repeat")
end
,
function()
--当启动动画时
syso("Start")
end
,
function()
--当取消动画时
syso("Cancel")
end
)


--或

i:dhon(dh,
function()
--当结束动画时
syso("End")
end
,
nil
,
nil
,
nil
)


说明：
常用于动画状态的监听。

提示：
该事件使用的选择性，可顺序选择性保留。

【dhb 动画背景】
用法：
--创建动画背景
--输入动画背景变量对象，输入是否重复执行
local dh = i:dhb(true)

--添加元素
--输入动画背景变量对象，输入背景图像或图片变量或背景对象，输入显示时长
i:dhb(dh, "@t1.png", 1000)
i:dhb(dh, "@t2.png", 1000)
i:dhb(dh, "@t3.png", 1000)

--设为指定控件背景
i:us(2, "background", dh)

--启动动画
i:dhb(dh, "start")

--停止动画
--i:dhb(dh, "stop")

--是否在运行
local b = i:dhb(dh, "running")
syso(b)

说明：
常用于组合一个背景动画。

【hsas 开启浏览器控件交互(裕语言+js+html5)】
用法：
--开启浏览器控件支持iapp交互
--输入浏览器控件ID或对象，输入是否开启
i:hsas(1, true)

--i:hsas(1, false)

说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

【has 裕语言交互JavaScript语言】
用法：
--首先将 web.html 放入用户文件中

--设置浏览器控件显示的html内容
local a = "@web.html"
local b = "utf-8"
local c = i:fr(a, b)

local d = "utf-8"
local e = "text/html"
local f = i:us(1, "url", c, d, e)

--因为浏览器加载内容属于异步操作，如果立刻执行下面的代码会执行失败
--所以将下面的代码放入某项单击事件中

local a = "go('呀！')"
--输入浏览器控件ID或对象，输入JavaScript的方法
i:has(1, a)

--带返回值解决方案
--local a = "go2('呀！')"
--输入浏览器控件ID或对象，输入JavaScript的方法
--i:has(1, a)

说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

注意：
在载入事件设置浏览器控件的加载html内容，它不会立刻加载完成。所以如果将 裕语言交互js的代码也写在载入事件，会导致交互调用失败。必须等待浏览器加载完毕html内容后，才能交互。

注意：
建议尽量使用JavaScript调用交互裕语言，效率较高。裕语言调用执行JavaScript的方法效率要慢数倍。

注意：
has 不应该放在新线程中，测试发现5.1系统has放入新线程中报错。

注意：
本例子需要注意编码，否则将乱码。

html（web.html）文件（utf-8编码）例子：
<html>
<head>
<script type="text/javascript">
function go(o)
{
document.getElementById("sb").innerHTML += "打我" + o;
}
function go2(o)
{
document.getElementById("sb").innerHTML += "打我" + o;
iapp.s("sss.sb", document.getElementById("sb").innerHTML);
}
</script>
</head>
<p id="sb">哈哈，你来</p>
</html>


【JavaScript交互裕语言】
用法：
--首先将 web.html 放入用户文件中

--设置浏览器控件显示的html内容
s a = "@web.html"
s b = "utf-8"
fr(a, b, c)

s d = "utf-8"
s e = "text/html"
us(1, "url", c, d, e, f)

--此方法，主要是在JavaScript中写交互代码哦
--JavaScript中交互方法列表（用于交互裕语言）：

/.

//调用裕语言模块方法，不带返回变量的
iapp.fn('a.b("' + o + '")');

//调用裕语言模块方法，带返回变量的
var value = iapp.fn2('a.c("' + o + '")', b);

//设置裕语言变量数据
iapp.s(o);

//获取裕语言变量数据
var value = iapp.g(o);
./
说明：
常用于浏览器中的JavaScript代码于iapp代码的互相调用。

注意：
建议尽量使用JavaScript调用交互裕语言，效率较高。裕语言调用执行JavaScript的方法效率要慢数倍。

注意：
本例子需要注意编码，否则将乱码。


html（web.html）文件（utf-8编码）例子：
<html>
<head>
<script type="text/javascript">

//不带返回变量的
function go(o)
{
//调用的是 模块a.myu 中的 b方法
iapp.fn('a.b("' + o + '")');
}

//带返回变量的
//执行模块后，获取一个变量并返回到javascript方法里
function go2(o, b)
{
//调用的是 模块a.myu 中的 c方法
var value = iapp.fn2('a.c("' + o + '")', b);
alert('变量 sss.abc：' + value);
}

//设置全局变量数据
//同理，下面也有设置界面变量、设置局部变量的例子
function ss(o, b)
{
iapp.s(o, b);
}

//获取全局变量数据
//同理，下面也有获取界面变量、获取局部变量的例子
function gs(o)
{
var value = iapp.g(o);
alert('变量 sss.abc：' + value);
}

</script>
</head>
<p><a href="javascript:void(0)" onclick="go('呵呵')">调用裕语言的模块方法</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="go2('呵呵', 'sss.abc')">调用裕语言的模块方法，并返回sss.abc变量内容</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="ss('sss.abc', '呵呵')">设置裕语言的sss.abc全局变量数据</a></p>
<p></p>
<p></p>
<p><a href="javascript:void(0)" onclick="gs('sss.abc')">获取裕语言的sss.abc全局变量数据</a></p>
</html>

模块（a.myu）例子：
fn b(a)
//打印出数据
syso(a)
end fn

fn c(a)
//打印出数据
syso(a)
sss abc = "666呵呵"
end fn

【uxf 显示悬浮窗】
用法：

--输入界面名，输入宽度，输入高度，输入对其方式，输入赋值变量
local w = -1
local h = -1
local gravity = "top|right"
v = i:uxf("a.ilua", w, h, gravity)


--输入界面名，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入类型的窗口，输入对其方式，输入flags，输入format，输入赋值变量
local x = 0
local y = 0
local w = -1
local h = -1
local type = 0
local gravity = "top|right"
local flags = 0
local format = 0
i:uxf("a.ilua", x, y, w, h, type, gravity, flags, format, v)


--刷新悬浮窗口的布局，常用于通过us设置后的刷新
--输入界面根控件的控件对象
i:uxf(v)


--移除悬浮窗口
--输入界面根控件的控件对象，输入标识
i:uxf(v, "del")


--重置悬浮窗的属性
--输入界面根控件的控件对象，输入标识，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入对其方式
local x = 0
local y = 0
local w = -2
local h = -2
local gravity = "top|right"
i:uxf(v, "set", x, y, w, h, gravity)

--重置悬浮窗的属性
--输入界面根控件的控件对象，输入标识，输入X显示位置，输入Y显示位置，输入宽度，输入高度，输入对其方式
local x = 0
local y = 0
local w = -1
local h = -1
local type = 0
local gravity = "top|right"
local flags = 0
local format = 0
i:uxf(v, "set", x, y, w, h, type, gravity, flags, format)

说明：
常用于显示悬浮窗窗口。

提示：
可通过 local b = i:gvs(v, "a.1") 代码进行获取悬浮窗内的子控件，然后对其进行操作。

提示：
可通过下例代码，控制窗口位置的移动
--更新窗口位置
i:us(v, "x", 100)
i:us(v, "y", 100)

--获取窗口位置
i:ug(v, "x", xx)
i:ug(v, "y", yy)

--通过us 更新后， 需要刷新悬浮窗口的布局
i:uxf(v)


对齐方式：
center：居中
top：顶
bottom：底
left：左
right：右
center_vertical：垂直居中
center_horizontal：水平居中

输入flags：
0 不许获得焦点（编辑框输入法将无法弹出）
1 可以获得焦点，返回键将不可用


【tts 文本转换语音】
用法：
--创建一个TTS对象
--输入赋值对象
local a = i:tts()


--创建一个TTS对象；并且直接设置播放
--输入赋值对象，输入语言代码，输入语速率，输入音高率，输入播放文字（可传入nil）
i:tts(a, "en", "I love you", 1, 1)


--获取TTS对象初始化状态；赋值变量返回 0未完成初始化 1初始化成功 -1初始化失败 -2初始化语言失败 -3当前TTS对象不可用
--输入TTS对象，输入标识，输入赋值变量
local b = i:tts(a, "zt")
syso(b)


--播放文字；模式 0替换以前的任务 1队列追加至后面
--输入TTS对象，输入标识，输入播放文字，输入模式，输入赋值变量
local b = i:tts(a, "st", "I love you", 0)
syso(b)


--文字转换音频文件
--输入TTS对象，输入标识，输入文字，输入保存路径，输入赋值变量
local b = i:tts(a, "ft", "I love you", "123.wav")
syso(b)


--设置语言
--输入TTS对象，输入标识，输入语言代码
i:tts(a, "lg", "en")


--设置语音播放速率。1为正常，值越低语速越慢（0.5是正常的一半），值越大语速越快（2是正常的两倍）
--输入TTS对象，输入标识，输入小数
i:tts(a, "se", 1)


--设置音高率，值越大声音越高音，值越小声音越低音，正常为1.0
--输入TTS对象，输入标识，输入小数
i:tts(a, "ph", 1)


--检查是否TTS正在播放
--输入TTS对象，输入标识
local b = i:tts(a, "ip")
syso(b)


--释放TTS使用的资源
--输入TTS对象，输入标识
i:tts(a, "re")


--停止所有任务
--输入TTS对象，输入标识，输入赋值变量
local b = i:tts(a, "sp")
syso(b)


--检查是否一个可用的TTS对象
--输入TTS对象，输入标识，输入赋值变量
local b = i:tts(a, "is")
syso(b)


说明：
常用于文本转化为音频，并且播放。


语言代码：
- 系统默认支持语言
美国    en
德国    de
意大利  it
法国    fr

- 需安装第三方语言包（讯飞语音TTS），并且设置语言
日本    ja
韩国    ko
中国    zh


安装与设置中文语言：

下载其中一个 
(4.0系统)讯飞语音TTS http://m.yx93.com/app.aspx?id=28515  
(2.2系统)讯飞语音TTS http://m.yx93.com/app.aspx?id=28513

安装 讯飞语音TTS

安卓手机》设置》语言和输入法》文本转语音输入》选择 讯飞语音合成 ,默认引擎 讯飞语音合成 , 语言 中文
（设置因为各品牌细节不同，但是都大同小异）


注意事项：
单独TTS对象创建后，需要有一个异步初始化过程，如果创建TTS对象然后直接播放文本将无法成功。需要先完成初始化后，然后播放文本。

注意事项：
文字转语音TTS输出；默认语言状态：完全支持 中文


【blp 录制屏幕】
用法：
local b = "123.mp4"
--输入储存录制文件路径，输入视频宽度，输入视频高度，输入视频码率，输入视频帧率
i:blp(b, 1280, 720, 1024000, 30)

--开始录制
local b = i:blp("st")
syso(b)

--停止录制
local b = i:blp("sp")
syso(b)

--释放资源
local b = i:blp("re")
syso(b)

--判断是否正在录制
local b = i:blp("ip")
syso(b)

说明：
用于手机屏幕录制。

注意：
仅支持系统Android 5.0以及以上才有效果！
Android 5.0以下的系统，无效果！


【otob 转换为字节组】
用法：
--将文件转换为字节组，字节组将为字符串形式返回赋值给“b”
local b = i:otob("%abc.txt")
syso(b)

--将字符串转换为字节组
local b = i:otob("utf-8", "nihao")
syso(b)

--不设置编码
local b = i:otob(nil, "nihao")

--将文件转换成 byte[] 字节数组对象
local b = i:otob("file", nil, "%abc.txt")
syso(b)

--将字符串转换成 byte[] 字节数组对象
local b = i:otob("str", "utf-8", "nihao")
syso(b)

说明：
将字符或文件转换为字节组

【btoo 字节组还原】
用法：
local b = i:otob("%abc.txt")
--将字节组转换为文件
--输入字节组，文件路径，是否覆盖，变量 b 可为byte[] 字节数组对象
i:btoo(b, "%abc2.txt", true)


local b = i:otob("utf-8", "nihao")
--字节组转换为字符串，变量 b 可为byte[] 字节数组对象
local c = i:btoo("utf-8", b)
syso(c)

--不设置编码
local c = i:btoo(nil, b)

说明：
将字节组转换为字符或文件

【sot Socket网络通信】
用法：
--服务端
--服务端口，临时文件目录，接受客户超时，客户连接超时，是否覆盖文件
local b = i:sot(8668, "%iApp/tempSocket", 0, 0, false,
function(st_msG,st_ssR)
--消息内容
syso(st_msG)
--连接对象
syso(st_ssR)

end
)

--客户端
--服务地址，服务端口，服务连接超时，是否覆盖文件
local b = i:sot("192.168.1.100", 8668, 0, false,
function(st_msG,st_ssR)
--消息内容
syso(st_msG)
--连接对象
syso(st_ssR)

end
)

--发送字符串，必须放在线程内
i:sot(b, "str", "nihao")

--发送文件，必须放在线程内
i:sot(b, "file", "%abc.txt")

--发送字节组，必须放在线程内
local c = i:otob("nihao", "utf-8")
i:sot(b, "bt", c)

--发送不带信息头 byte[]字节组，必须放在线程内
i:sot(b, "bt2", bytes)

--关闭释放sot
i:sot(b, "re")

--获取sot是否已释放
local c = i:sot(b, "ip")

--获取ID总数
local c = i:sot(b, "id")

--获取连接对象列表
local c = i:sot(b, "list")

--获取连接对象列表的第一位
local c = i:sot(b, "list", 0)

--获取连接总数
local c = i:sot(b, "size")

--是否允许接受新连接
i:sot(b, "new", true)


说明：
Socket 管理操作。服务端发送消息将批量发送给所有连接。

服务端说明：
要求：
1.能连接公共网络 或 内网
2.拥有固定IP作为客户端连接的目标
3.电脑、手机、平板电脑等设备上运行服务端。
4.可使用iapp在自己的手机上面开发服务端，并运行服务端。

客户端说明：
要求：
1.能连接公共网络 或 内网
2.可使用iapp在自己的手机上面开发客户端，并连接服务端。

常见开发：
使用手机或电脑作为服务端，手机客户端与服务端相互传递文件、数据等。

【sota 单个Socket通信操作】
用法：
--获取连接对象列表的第一位，变量“c”属于单个Socket通信
local c = i:sot(b, "sl", 0)

--获取通信对方的IP
local d = i:sota(c, "ht")

--获取sota是否已释放
local d = i:sota(c, "ip")

--关闭释放sota
i:sota(c, "re")

--获取socket对象
local d = i:sota(c, "socket")

--获取连接对象ID
local d = i:sota(c, "id")

--发送字符串，必须放在线程内
i:sota(c, "str", "nihao")

--发送文件，必须放在线程内
i:sota(c, "file", "%abc.txt")

--发送字节组，必须放在线程内
local d = i:otob("utf-8", "nihao")
i:sota(c, "bt", d)

--发送不带信息头 byte[]字节组，必须放在线程内
i:sota(c, "bt2", bytes)

说明：
常用于单个Socket通信的操作管理


【loadso 加载动态库】
用法：
--比如加载 libabc.so
i:loadso("abc")

说明：
加载SO动态链接库。


【loadjar 加载jar库】
用法：
--比如加载 abc.jar
--返回变量 库对象
local b = i:loadjar("abc.jar")
syso(b)

--比如加载 abc.apk
--包含Activity需要传入true，赋值变量 库对象
local b = i:loadjar("abc.apk", true)
syso(b)

说明：
用于加载一些jar，dex，apk 的 sdk。需要把jar文件导入至项目资源的lib目录里，jar加载过程将联网校验。
如果附带SO动态链接库，需要把SO文件载入至项目资源。


【cls 获取完整接口类】
用法：
--获取一个类，输入完整类名如 java.lang.Math
--赋值变量 类对象
local a = i:cls("java.lang.Math")
syso(a)

--获取一个字符串类，常用类型可直接输入类名如 String
local b = i:cls("String")
syso(b)

--加载SDK abc.jar，并获取SDK里一个类 输入完整类名 com.sdk.abc
local a = i:loadjar("abc.jar")
local c = i:cls(a, "com.sdk.ceshi")
syso(c)

用法：
获取一个类；或从 jar SDK包获取类；

注意：完整类名区分大小写

【clssm 获取类的所有接口】
用法：
local b = i:cls("String")

--获取所有构造函数
local c = i:clssm(b, "init")

--获取所有函数方法
local c = i:clssm(b, "method")

--获取所有变量
local c = i:clssm(b, "field")


说明：
返回一个数组。


【java 调用java代码方法】
用法：
--调用java api java.lang.String.indexOf(String string) 查询字符56 在123456789 中位置
local c = i:cls("String")
local a = i:javax("123456789", c, "indexOf", {"String", "56"})
syso(a)


--初始化一个StringBuilderd
local a = i:javanew("java.lang.StringBuilder", {"String", "12345"})
local b = i:java(a, "java.lang.StringBuilder.append", {"String", "6789"})
local c = i:java(a, "java.lang.StringBuilder.toString")
syso(c)


local jar = i:loadjar("test.jar")
local c1 = i:cls(jar, "com.sdk.ceshi")
--调用静态方法 com.sdk.ceshi类 c 方法
local c = i:javax(nil, c1, "c", {"int", 123})
syso(c)

--调用静态变量 com.sdk.ceshi类 a 变量
local c = i:javags(nil, c1, "a")
syso(c)

--初始化com.sdk.ceshi类
--返回对象变量，输入完整类名或 cls方法的返回变量
local a = i:javanew(c1)

--访问变量，com.sdk.ceshi类 b变量
local c = i:javags(a, c1, "b")
syso(c)

--设置变量，com.sdk.ceshi类 b变量
local c = i:javass(a, c1, "b", "123456")
syso(c)


--设置回调方法
local a = i:javanew("android.widget.TextView", {"Context", activity})
local b = i:java(a, "android.widget.TextView.setText", {"CharSequence", "我是文本控件"})
--注意回调接口类名前面需要加一个“.”，如.android.view.View.OnClickListener
local b = i:java(a, "android.view.View.setOnClickListener", {".android.view.View$OnClickListener", nil},
function(st_mD,st_aS)

--系统赋值
syso(st_mD)
syso(st_aS)
end
)


说明：
支持 android 所有的api；以及 自加载的jar SDK 的 api

注意：完整类名或 方法名 或 变量名 区分大小写

传递参数：
要传递的参数可设置多个，格式为一个数组 {  } 括起来的，参数为格式：类名， 值，类名， 值...

activity：默认变量 Activity组件

javax 与 java 方法区别：
javax：第3位参数完整类名，第4位参数方法名。类名可传入 cls方法的赋值变量；总共6位参数
java：第3位参数 完整类名和方法名。总共5位参数。


【javacb 自定义回调】
用法：

local jar = i:loadjar("test.jar")
local c1 = i:cls(jar, "com.ceshi.dex.main")
local o = i:javanew(c1)
local c2 = i:cls(jar, "com.ceshi.dex.main$huidiao")

--设置回调方法
local hd = i:javacb(c2, 
function(st_mD,st_aS)

--系统赋值
syso(st_mD)
syso(st_aS)
end
)
--设置回调
local a = i:javax(o, c1, "sethuidiao", {c2, hd})
--调用回调方法
local a = i:javax(o, c1, "get", {"String", "666"})

说明：
常用于设置自定义SDK的回调方法。


【res 安装包资源管理器】
用法：
--获取应用自己的对象
local a = i:res()

--获取其他apk安装包内的资源对象，只支持加载SD卡上的apk
local a = i:res("%abc.apk")

--获取资源
--输入资源对象，输入资源标识或文件名(没后缀)，输入资源类型
local b = i:res(a, "ic_launcher", "drawable")

--获取资源ID，打包测试才有效
local b = i:res(a, "ic_launcher", "drawable", false)

--获取 AssetManager 或 Resources 对象
local b = i:res(a, "asset")
local b = i:res(a, "resources")

说明：
可获取的资源类型 drawable、string、color、stringarray、layout


【src 自定义代码】
用法：

--[[
SDK自定义的 com.sdk.ceshia类 源码
package com.sdk;
public class ceshia {

	public String cs(String sm)
	{
		return sm;
	}
}
--]]

--初始化SDK自定义的 com.sdk.ceshia类
local a = i:javanew("com.sdk.ceshia")
--将自定义类添加到代码块里
i:src("ceshia", a)
--代码里调用com.sdk.ceshi类 里的这个方法
local b = ceshia:cs("abcde")
syso(b)

说明：
支持可以自己写java 的SDK，封装成代码。然后再自定义代码提示，把自己封装的代码加上去就可以了。


【call 交互式语言调用】
用法：

--输入语言类型，模块m的abc方法，输入一个数组
i:call("myu", "m.abc", {"nihao", 66})


--输入语言类型，模块mk的abc方法，输入一个数组
local a = i:call("mlua", "mk.abcd", { 123 })

--输入语言类型，模块mk的abc方法，输入一个数组
local a = i:call("mjava", "mk.abcd", { 123, 456, 789 })

--没有参数的
--输入语言类型，模块mk的abc方法
i:call("mjs", "mk.abcdf")

说明：
用于多语言的代码交互。

注意：
此方法只能调用模块方法，输入是字符串如 m.abc 模块m 的abc方法

注意：
参数数量要与实际模块方法的参数的数量一致。

注意：
四种语言，只有 mlua 和 mjava 可以返回赋值变量，裕语言可以通过设置全局变量变相返回变量， mjs设置赋值变量无效。


【json json数据解析】
用法：
--解析json数据，双引号要加 \ 进行转义
local text = "{\"id\":1, \"name\":\"xiaobai\", \"age\":16}"
local jo = i:json(text)

--获取id
local a = i:json(jo, "get", "id")
syso(a)
--获取name
local b = i:json(jo, "get", "name")
syso(b)
--获取age
local c = i:json(jo, "get", "age")
syso(c)

--修改age数据
i:json(jo, "set", "age", 20)

--删除id数据
i:json(jo, "del", "id")

--打印json数据
local text = i:json(jo, "json")
syso(text)



--解析json列表数据
local text = "{\"userlist\":[{\"id\":1, \"name\":\"niubi\", \"age\":16},{\"id\":2, \"name\":\"wangba\", \"age\":18},{\"id\":3, \"name\":\"goudan\", \"age\":17}]}"
local jo = i:json(text)

--打印json数据
local list = i:json(jo, "list", "userlist")
local size = i:json(list, "size")
while size > 0 do

size = size - 1

local item = i:json(list, "data", size)

--获取id
local a = i:json(item, "get", "id")
syso(a)
--获取name
local b = i:json(item, "get", "name")
syso(b)
--获取age
local c = i:json(item, "get", "age")
syso(c)

end

说明：
常用于解析服务器反馈的数据。


【utb Toolbar工具栏设置】
用法：

--设置自定义的工具栏 为当前界面的工具栏
--输入Toolbar工具栏的 控件id或控件对象
i:utb(3)


--绑定侧滑控件，侧滑控件内需要包含左侧滑，绑定后可以在Toolbar工具栏的左图标 控制左边侧滑
--输入Toolbar工具栏的 控件id或控件对象，输入侧滑的 控件id或控件对象
i:utb(3, 2)


--设置参数

i:utb("set", "dshe", true)

--设置左图标，可以设置事件监听
i:utb("left", 3, "@a.png")

--设置左图标的点击事件，注意此代码需在 i:utb(id) 后，否则事件将无效。
i:utb("set", "leftck", 3,

function(v)

--系统赋值
syso(v)
end
)

--设置右菜单图标，无事件。可使用界面菜单事件
i:utb("right", 3, "@b.png")


--标题
i:utb("set", "title", "apptitle")

--子标题
i:utb("set", "subtitle", "appsubtitle")

--自定义布局可输入View类型布局
i:utb("set", "cv", v)

--显示选项
i:utb("set", "do", 0)

--显示或隐藏 标题
i:utb("set", "dste", true)

--显示或隐藏 自定义布局
i:utb("set", "dsce", true)

--显示或隐藏 主页图标
i:utb("set", "dshe", true)


--获取参数

--标题
local c = i:utb("get", "title")

--子标题
local c = i:utb("get", "subtitle")

--自定义布局可输入View类型布局
local c = i:utb("get", "cv")

--显示选项
local c = i:utb("get", "do")

--动作栏布局高度
local c = i:utb("get", "height")


说明：
常用于设计应用顶部工具栏。

【tws 弹窗提醒】
用法：
--获取展示的控件对象，提醒将在这个控件里展示
local v = i:gvs(1)

--无按钮弹出提醒
--输入控件对象可设置null，输入字符，输入显示时长（值0 -1 -2）
i:tws(v, "ni hao!", 0)


--有按钮弹出提醒
--输入控件对象可设置null，输入字符，输入显示时长（值0 -1 -2），输入按钮标题
i:tws(v, "ni hao ma?", 0, "hao",

function(v)

--系统赋值
syso(v)
end
)


【uht 滑动窗体控制】
用法：

--添加新的页面，设置的界面会执行载入事件里的代码
--输入滑动窗体的 控件id或控件对象，输入标识，输入插入序号 如-1为尾部 0为头部，输入标题，输入界面名，输入控件对应的数据项...不限制数量可参考代码ula
i:uht(2, "add", -1, "标题", "a.iyu", {1,2,3}, {"abc","bac","bbc"})

--删除界面
--输入滑动窗体的 控件id或控件对象，输入标识，输入界面序号 序号以0开始 -1为尾部
i:uht(2, "del", 0)

--修改界面标题
--输入滑动窗体的 控件id或控件对象，输入标识，输入界面序号 序号以0开始 -1为尾部
i:uht(2, "title", 0, "标题2")

--获取页面总数
local b = i:uht(2, "size", b)
syso(b)

--释放内存
i:uht(2, "close")


--绑定标签布局，绑定后滑动界面时标签布局会跟随运动，需要注意 标签布局 和 滑动窗体 的子项数量应一致，新增子项时也需要同时增加
--输入滑动窗体的 控件id或控件对象，输入标识，输入标签布局的 控件id或控件对象，是否应刷新其内容
i:uht(2, "bd", 3, true)
--注意：如果绑定前 标签布局 如有设置子项，绑定时会被清空。绑定后需使用 i:us(3, "app_tablist", "选项1|选项2|选项3") 代码设置

--增加标签布局 的子项
i:us(3, "app_tabadd", "选项")

--添加滑动窗体 的子项
i:uht(2, "add", -1, "标题", "a.iyu", {1,2,3}, {"abc","bac","bbc"})


说明：
用于动态管理控制滑动窗体和垂直滑动窗体的 新增页面、删除页面、绑定标签布局等。


【cast 强制转换数据类型】
用法：

local a = 123
--转换数据类型并直接赋值
--输入完整类名 或 类对象，输入需要转换的数据变量
local b = i:cast("String", a)
syso(b)


说明：
常用于数据强制转换。


【yul 加载yul布局】
用法：

--将布局加载展示到指定的布局控件里
--输入控件id或控件对象（比如输入线性布局ID），输入 yul 布局文件名
i:yul(1, "a.yul")


--返回布局对象
--输入 yul 布局文件名 返回一个View对象
local a = i:yul("a.yul")
syso(a)


说明：
yul布局是以 android 的 xml布局为基础，用于动态加载布局到应用界面。和安卓xml布局用法和代码都是一致的。

在设计 yul布局 时需要自定义控件ID，如设置控件ID:123 编写代码 android:id="123" 或 android:id="@+id/s123" 两种写法都可以，效果都是ID为 123


【luajava 对象方法】
代码：

--newInstance实例化
--输入类名，构造方法参数
local a = luajava.newInstance(className, ....)


--bindClass绑定类，返回一个 class 类对象
--输入类名
local a = luajava.bindClass(className)


--new, 需配合bindClass实例化对象
local a = luajava.bindClass("java.lang.String")
--输入类对象
local b = luajava.new(a)


--createProxy java接口，可以多个接口同时调用。
local a = luajava.newInstance("java.awt.Button", "execute")
b = {}
function b.actionPerformed(ev)
. . .
end
--输入类名，方法
local c = luajava.createProxy("java.awt.ActionListener", b)

a:addActionListener(c)


【无障碍服务】
用法：
固定模块名为 ays_service 可创建模块 ays_service.mjava，代码如下：

--事件方法 on 实时回调变化事件
function on(e)

--获取事件类型
local b = ays:gtype(e)
--如果事件类型
if b == 32 or b == 2048 then

  --获取事件源的对象节点列表
  local node = ays:gall(e)
  --判断事件来源是不是包名为com.iapp.app的应用
  local gpn = ays:gpn(e)
  if "com.iapp.app" == gpn then
  
     --判断类名，根据指定的类名进行不同的操作
     local gcn = ays:gcn(e)
     if "com.iapp.app.HomeMian" == gcn then
     
        --从对象列表搜索文本为“创建”的对象，并点击该对象
        ays:cktext(node, 16, "创建")
     
     elseif "com.iapp.app.HomeAdd" == gcn then
     
        --根据ID获取指定的节点
        local b = ays:id(node, "com.iapp.app:id/ui_home_add_title")
	--设置节点的文本框输入指定字符
        ays:enter(b, "name")
        --根据ID获取指定的节点
        b = ays:id(node, "com.iapp.app:id/ui_home_add_remark")
	--设置节点的文本框输入指定字符
        ays:enter(b, "remark")
        --从对象列表搜索指定ID的对象，并点击该节点对象
        ays:ckid(node, 16, "com.iapp.app:id/ui_home_add_go")
     end
  end
  --释放根源节点
  ays:re(node)

end
end


--初始化事件方法 onsc 启动时回调一次
function onsc()

import 'com.iapp.app.ays'

local pns = "com.iapp.app"
--设置监听指定的包名，可以设置多个包名用逗号隔开如"com.xxx.a,com.xxx.b"
ays.pns = pns
--设置相应时间
ays.nt = 1000
end


然后 权限配置管理》application配置 将下面的配置粘贴进去：
	<service
            android:name="com.iapp.app.ays"
            android:label="iapp开发工具无障碍辅助功能"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService"/>
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/aya_config"/>
        </service>

最后，【正式打包发布】打包完成后，安装测试。记得自行去设置》辅助功能》打开我们的服务《iapp开发工具无障碍辅助功能》。
注意：直接在iapp里测试无效。


更多代码示范：

--------静态调用
--获取无障碍功能是否已经授权
import 'com.iapp.app.ays'
ays:isas(activity)

--如果没有授权，可跳转设置界面
import 'com.iapp.app.ays'
ays:goset(activity)


--------事件源操作
--获取Context功能类
local a = ays:gbc()

--获取无障碍功能配置信息
local a = ays:gsi()

--设置无障碍功能配置信息
ays:ssi(a)

--调用全局事件
--输入值：1. 返回键 2. HOME键 3. 最近打开应用列表 4. 打开通知栏 5. 设置 6. 锁屏
local a = ays:pga(1)

--获取事件类型
--值：32 打开PopupWindow，Menu，Dialog等的事件  64 显示通知的事件  2048 更改窗口内容的事件  4194304 屏幕上显示的窗口中的事件更改
local a = ays:gtype(e)

--获取事件源类的类型
local a = ays:gcn(e)

--获取事件源的包名
local a = ays:gpn(e)

--获取事件源的是否可用
local a = ays:ised(e)

--获取事件源的节点总数
local a = ays:gsl(e)

--获取事件源的整数ID
local a = ays:gwid(e)

--获取事件源的时间
local a = ays:gtime(e)

--释放资源
ays:re(e)


--------节点的操作
--获取事件源的节点对象列表
local node = ays:gall(e)

--获取窗口的对象节点列表，需要Android 4.1及以上才可调用
local node = ays:gall()

--根据序号；获取对象的子节点
local a = ays:gi(node, 0)

--获取对象的子节点总数
local a = ays:gi(node)

--根据当前焦点向某个方向进行搜索可以获得输入焦点的最近控件
--输入值：33 向上  130 向下  17 向左  66 向右
local a = ays:focussearch(node, 130)

--根据文本查询控件，返回节点列表
local nodelist = ays:text(node, "创建")

--根据id查询控件，返回节点列表
local nodelist = ays:id(node, "com.iapp.app:id/ui_home_add_go")

--根据焦点查询
--输入值：1 输入焦点  2 可访问性焦点
local a = ays:focus(node, 1)

--获取节点文本
local a = ays:gt(node)

--获取节点类的类型
local a = ays:gcn(node)

--获取节点整数ID
local a = ays:gwid(node)

--获取节点ID
local a = ays:gid(node)

--获取可以在节点上执行的操作
local list = ays:gal(node)

--获取节点在屏幕上坐标
local a = ays:gbis(node)

--获取父节点在屏幕上坐标
local a = ays:gbip(node)

--获取节点的包名
local a = ays:gpn(node)

--获取节点的父节点
local a = ays:gp(node)

--获取此节点是否可点击
local a = ays:isck(node)

--获取此节点是否已启用
local a = ays:ised(node)

--获取此节点是否已选中
local a = ays:iscd(node)

--获取这个节点是否被聚焦
local a = ays:isfd(node)

--获取此节点是否可以长时间点击
local a = ays:islck(node)

--获取此节点是否是密码
local a = ays:ispd(node)

--获取节点是否可滚动
local a = ays:isse(node)

--获取是否选择此节点
local a = ays:issd(node)



--根据文本查询；模拟控件点击控件
local a = ays:cktext(node, 16, "创建")

--根据ID查询；模拟控件点击控件
local a = ays:ckid(node, 16, "com.iapp.app:id/ui_home_add_go")

--根据焦点查询；模拟控件点击控件
--输入值：1 输入焦点  2 可访问性焦点
local a = ays:ckfocus(node, 16, 1)


	/.
	  模拟执行操作
	  1 将输入焦点输入到节点的操作
	  16 点击节点信息的动作
	  32 长时间点击节点的动作
	  32768 操作来粘贴当前的剪贴板内容
	 ./
--开始模拟控件点击
--输入节点列表
local a = ays:ck(nodelist, 16)

--开始模拟控件点击
--输入节点列表，输入自定义的Bundle
local a = ays:ck(nodelist, 16, bundle)

--对单项模拟控件点击
--输入节点列表
local a = ays:ck(node, 16)

--对单项模拟控件点击
--输入节点列表，输入自定义的Bundle
local a = ays:ck(node, 16, bundle)

--对单项模拟执行输入文本,Android 4.3 版本及以上
local a = ays:enter(node, "nihao")

--开始模拟执行输入文本,Android 4.3 版本及以上
local a = ays:enter(nodelist, "nihao")

--获取节点所有子节点列表
local nodelist = ays:ganiall(node)

--释放节点资源
ays:re(node)

说明：
无障碍功能（辅助功能）常用于简化操作，使应用或 系统的变得更智能、简便。


【zj 组件控制】
用法：
--如广告组件，首先下载的组件，并且设置好组件。

--初始化SDK，放在第一个界面的载入事件里
--输入赋值变量，标识，发布 ID，密钥，是否开启的Log输出（需要换自己的渠道信息）
local a = i:zj("init", {"String", "85aa56a59eac8b3d", "String", "a14006f66f58d5d7", "boolean", true})

--初始化积分墙
--输入赋值变量，标识
local a = i:zj("initjfq")

--展示积分墙
local a = i:zj("jfqgo")


说明：
用于控制组件。

你居然能读完！豆包都不行！哈哈
就到这里没了 希望你能学会
剩下的路就靠你走吧... 前辈留言(DeepSeek V4,Claude Opus 4.8)