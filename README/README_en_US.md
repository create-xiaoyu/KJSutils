---

# KJSutils (KubeJS Integration)

This mod extends the functionality of KubeJS by adding **four new features** (for a total of ten methods).

---

### 1. Download Files from the Internet

Allows downloading files from the internet with customizable save paths and filenames.
**Files can only be saved inside the `.minecraft/` directory for security reasons.**

**Usage:**

```javascript
KJSutils.Download("download_url", "save_path", "save_filename (use null to keep the original filename from the URL)")
```

**Examples:**

```javascript
KJSutils.Download("https://example.com/example.txt", "config/kjsutils", "null")
KJSutils.Download("https://example.com/example.txt", "config/kjsutils", "helloworld.txt")
```

---

### 2. Parse JSON Files Using Syntax Expressions

**Usage:**

```javascript
// Returns: the first matched value as a string, or "null" if no match is found
KJSutils.Analysis("file_path (must be within .minecraft/ for safety)", "json_path")

// Returns: a list of all matched values as strings
KJSutils.AnalysisAll("file_path (must be within .minecraft/ for safety)", "json_path")
```

---

### JSON Path Syntax Reference

| Syntax Type       | Example                                                      | Description                                                                                         | Example Return Value                                                              |
|-------------------|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| Root Object       | `$`                                                          | Refers to the root of the JSON object                                                               | The entire JSON content                                                           |
| Force Root Object | `$$`                                                         | Force parse as root object name                                                                     | i18n lang key                                                                     |
| Object Property   | `$.property`<br>`$.nested.property`                          | Access an object’s property or nested property                                                      | `"value"`<br>`"nested_value"`                                                     |
| Array Index       | `$[0]`<br>`$.items[1]`                                       | Access the first array element or the second element within an object’s array                       | `"first_item"`<br>`{"id": 2, "name": "item2"}`                                    |
| Quoted Property   | `$['property-name']`<br>`$['nested']['property']`            | Access properties containing special characters, or nested quoted properties                        | `"value"`<br>`"nested_value"`                                                     |
| Wildcard          | `$.*`<br>`$.items.*`<br>`$.items.*.name`                     | Get all root values, all elements in an object array, or specific fields of all objects in an array | `["value1", "value2", ...]`<br>`[elem1, elem2, ...]`<br>`["name1", "name2", ...]` |
| Mixed Path        | `$.data[0].users[2].name`<br>`$['config']['items'][1].price` | Combine different syntax types for complex paths                                                    | `"user_name"`<br>`15.99`                                                          |

---

### Example JSON

```json
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

| JSON Path Query                     | Description                                                   | Return Value                                                 |
|-------------------------------------|---------------------------------------------------------------|--------------------------------------------------------------|
| `$.version`                         | Get the version                                               | `1.0.0`                                                      |
| `$.settings.difficulty`             | Get the difficulty setting                                    | `hard`                                                       |
| `$.players[0].name`                 | Get the first player's name                                   | `Steve`                                                      |
| `$.players[1].inventory[0].id`      | Get the ID of the first item in the second player's inventory | `minecraft:wood`                                             |
| `$['server-config']['max-players']` | Get the server's max player count                             | `20`                                                         |
| `$.players.*.name`                  | Get all player names                                          | `["Steve", "Alex"]`                                          |
| `$.players[0].inventory.*.count`    | Get all item counts for Steve                                 | `[5, 64]`                                                    |
| `$.players.*.inventory.*.id`        | Get all item IDs from all players                             | `["minecraft:diamond", "minecraft:stone", "minecraft:wood"]` |
| `$$.item.minecraft.wood`            | Force parse as root path                                      | `Wood`                                                       |

---

### 3. Use syntax (only partially supported) to modify specific JSON values

**Usage:**

```javascript
KJSutils.Analysis("File path (can only read files under the .minecraft/ directory for safety)", "JSON path", "Value to write (be mindful of the value type)")
```

**Examples:**

```javascript
// Modify a root-level property
KJSutils.ModifyJsonValue("config/test.json", "$.name", "New Name");

// Modify a nested property
KJSutils.ModifyJsonValue("config/test.json", "$.player.level", 100);

// Modify a boolean value
KJSutils.ModifyJsonValue("config/test.json", "$.enabled", true);

// Modify a JSON object
KJSutils.ModifyJsonValue("config/test.json", "$.data", '{"key": "value"}');
```

---

### 4. Operating FancyMenu Variables

**Usage:**

```javascript
// Set or add a variable
KJSutils.FMsetVariable("variableName", "variableValue", "resetOnLaunch (boolean)")

// Delete a variable
KJSutils.FMremoveVariable("variableName")

// Get a variable’s value
KJSutils.FMgetVariable("variableName") // returns a Variable type

// Check if a variable exists
KJSutils.FMexistsVariable("variableName") // returns a boolean

// Trigger variable initialization
KJSutils.FMinit()

// Delete all variables
KJSutils.FMclearAllVariables()
```

---

**Example:**

```javascript
// Set the variable "test" to value 1 and make it reset on launch
KJSutils.FMsetVariable("test", "1", true)

// Delete the variable "test"
KJSutils.FMremoveVariable("test")

// Get the value of the variable "test"
let value = KJSutils.FMgetVariable("test")

// Check if the variable "test" exists
if (KJSutils.FMexistsVariable("test")) {
    // Other operations
}
```

---

### 5. Get Client Language

**Usage:**

```javascript
// return String value. Example: en_us
KJSutils.GetClientLanguage()
```

---

### Credits

* Code references: [**FilesJS**](https://github.com/xiaoliziawa/Files-JS)
* JSON parsing approach inspired by: [**FancyMenu**](https://github.com/Keksuccino/FancyMenu)

---