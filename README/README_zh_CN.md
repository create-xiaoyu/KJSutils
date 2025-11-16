---

# KJSutils（Kubejs集成）

此模组对kubejs进行了功能上的扩展，添加了4个新功能，共10个方法

---

### 1.从互联网中下载文件

支持自定义保存文件路径以及保存文件名
**仅能保存在 `.minecraft/` 路径下以保证安全性**

**使用方式：**

```javascript
KJSutils.Download("下载链接", "保存路径", "保存文件名（如果使用链接中的文件名则填入null）")
```

**示例：**

```javascript
KJSutils.Download("https://example.com/example.txt", "config/kjsutils", "null")
KJSutils.Download("https://example.com/example.txt", "config/kjsutils", "helloworld.txt")
```

---

### 2.使用语法对Json进行解析

**使用方式：**

```javascript
// 返回：第一个匹配值的字符串，无匹配返回 "null"
KJSutils.Analysis("文件路径（仅能读取在 .minecraft/ 路径下以保证安全性）", "Json路径")

// 返回：所有匹配值的字符串列表
KJSutils.AnalysisAll("文件路径（仅能读取在 .minecraft/ 路径下以保证安全性）", "Json路径")
```

---

语法列表：

| 语法类型   | 语法示例                                                         | 描述                                          | 返回值示例                                                                             |
|--------|--------------------------------------------------------------|---------------------------------------------|-----------------------------------------------------------------------------------|
| 根对象    | `$`                                                          | 引用 JSON 根对象                                 | 整个 JSON 内容                                                                        |
| 对象属性   | `$.property`<br>`$.nested.property`                          | 访问对象的属性<br>访问嵌套对象属性                         | `"value"`<br>`"nested_value"`                                                     |
| 数组索引   | `$[0]`<br>`$.items[1]`                                       | 访问数组的第一个元素<br>访问对象中数组的第二个元素                 | `"first_item"`<br>`{"id": 2, "name": "item2"}`                                    |
| 带引号的属性 | `$['property-name']`<br>`$['nested']['property']`            | 访问包含特殊字符的属性<br>嵌套带引号的属性                     | `"value"`<br>`"nested_value"`                                                     |
| 通配符    | `$.*`<br>`$.items.*`<br>`$.items.*.name`                     | 获取根对象的所有值<br>获取对象中数组的所有元素<br>获取数组中所有对象的指定属性 | `["value1", "value2", ...]`<br>`[elem1, elem2, ...]`<br>`["name1", "name2", ...]` |
| 混合路径   | `$.data[0].users[2].name`<br>`$['config']['items'][1].price` | 组合使用各种语法<br>复杂混合路径                          | `"user_name"`<br>`15.99`                                                          |

---

### 示例：

```JSON
{
  "version": "1.0.0",
  "settings": {
    "difficulty": "hard",
    "cheats": false
  },
  "players": [
    {
      "name": "Steve",
      "level": 42,
      "inventory": [
        {"id": "minecraft:diamond", "count": 5},
        {"id": "minecraft:stone", "count": 64}
      ]
    },
    {
      "name": "Alex", 
      "level": 38,
      "inventory": [
        {"id": "minecraft:wood", "count": 32}
      ]
    }
  ],
  "server-config": {
    "max-players": 20,
    "pvp": true
  }
}
```

| 语法查询路径                              | 描述              | 返回值                                                          |
|-------------------------------------|-----------------|--------------------------------------------------------------|
| `$.version`                         | 获取版本号           | `1.0.0`                                                      |
| `$.settings.difficulty`             | 获取难度设置          | `hard`                                                       |
| `$.players[0].name`                 | 获取第一个玩家的名字      | `Steve`                                                      |
| `$.players[1].inventory[0].id`      | 获取第二个玩家第一个物品的ID | `minecraft:wood`                                             |
| `$['server-config']['max-players']` | 获取服务器最大玩家数      | `20`                                                         |
| `$.players.*.name`                  | 获取所有玩家的名字       | `["Steve", "Alex"]`                                          |
| `$.players[0].inventory.*.count`    | 获取Steve所有物品的数量	 | `[5, 64]`                                                    |
| `$.players.*.inventory.*.id`        | 获取所有玩家的所有物品ID   | `["minecraft:diamond", "minecraft:stone", "minecraft:wood"]` |

---

### 3.使用语法（仅支持部分语法）对更改指定的Json对应值

**使用方式：**

```javascript
KJSutils.Analysis("文件路径（仅能读取在 .minecraft/ 路径下以保证安全性）", "Json路径", "要写入的值（注意值的类型）")
```

**示例：**

```javascript
// 修改根级别属性
KJSutils.ModifyJsonValue("config/test.json", "$.name", "新的名称");

// 修改嵌套属性
KJSutils.ModifyJsonValue("config/test.json", "$.player.level", 100);

// 修改为布尔值
KJSutils.ModifyJsonValue("config/test.json", "$.enabled", true);

// 修改为JSON对象
KJSutils.ModifyJsonValue("config/test.json", "$.data", '{"key": "value"}');
```

---

### 4.对FancyMenu的变量进行操作

**使用方式：**

```javascript
// 设置、添加变量
KJSutils.FMsetVariable("变量名称", "变量值")

// 删除变量
KJSutils.FMremoveVariable("变量名称")

// 获取变量值
KJSutils.FMgetVariable("变量名称") //返回 Variable 类型

// 检查变量是否存在
KJSutils.FMexistsVariable("变量名称") // 返回布尔值

// 触发变量初始化操作
KJSutils.FMinit()

// 删除所有变量
KJSutils.FMclearAllVariables()
```

---

**示例：**

```javascript
// 设置变量test值为1
KJSutils.FMsetVariable("test", "true")

// 删除test变量
KJSutils.FMremoveVariable("test")

// 获取test变量的值
let value = KJSutils.FMgetVariable("test")

// 检查test变量是否存在
if (KJSutils.FMexistsVariable("test")) {
    // 其它操作
}
```

---

### 谢鸣

* 此模组代码参考了[**FilesJS**](https://github.com/xiaoliziawa/Files-JS)
* Json解析方式参考了[**FancyMenu**](https://github.com/Keksuccino/FancyMenu)

---