# Xmind解析与创建库

## 概要介绍

- 支持解析上传的xmind8与xmind zen两种格式的思维导图文件
- 支持构建并生成xmind思维导图文件(仅限xmind8格式的文档,为了更好的兼容性)

## 用法

### 解析

#### 方法1

直接调用 XmindParser.parseFile(String xmindFilePath) 方法,会获得Sheet列表,
每个Sheet对象里面有Children,Title等属性

#### 方法2

新建XmindParser对象并调用parse方法, new XmindParser().parse(filePath),返回值与上述方法一致

PS:
parseForJsonContent(String filePath)    // 成员方法
parseFileForJsonContent(String filePath) // 静态方法
可以获取解析完成后的Xmind节点Json数据结构,可以拿来实现自定义处理逻辑

### 创建

使用XmindBuilder方法,用法参考如下示例

```java
// 初始化builder
var builder = XmindBuilder.builder();
// 初始化画布
var sheet = builder.createSheet("哈哈哈");
// 在画布上面创建一个节点,该节点默认为根节点
// PS: 目前限制只能生成根节点
var rootTopic = sheet.createChildNode("哈哈Root");
// 在根节点后面创建第二个子节点,lambda函数里面可以基于子节点继续创建节点
rootTopic.createChildNode("哈哈Root2", secondTopic -> secondTopic.createChildNode("哈哈哈"));
// 在根节点后面再次创建一个子节点
rootTopic.createChildNode("囧囧4");
// xmind文件保存路径,注意使用xmind文件后缀名
var saveFilePath = "/Users/test/Downloads/" + RandomUtil.randomString(8) + ".xmind";
builder.build(saveFilePath);
// 构建完成后,文件就保存在 saveFilePath路径下了
```
