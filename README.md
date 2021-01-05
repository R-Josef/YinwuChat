# YinwuChat 说明文档

### 关于本fork
这个fork是为了将YinwuChat原版分支与Karlatemp的分支合并，将原作者Lintx和Karlatemp两者的后续更新包含在内，两个分支的后续修改都可参见 [ChangeLog](./CHANGELOG.md)。
- [原版](https://github.com/lintx/Minecraft-Plugin-YinwuChat)
- [Karlatemp版](https://github.com/Karlatemp/YinwuChat)
- [Karlatemp版（Fork）](https://github.com/Karlatemp/YinwuChat-Forked)

### 关于YinwuChat
YinwuChat同时是Bungeecord插件和Spigot插件，主要功能有。
- 跨服聊天同步
- 跨服私聊（`/msg <玩家名> 消息`）
- 跨服@（聊天内容中输入想@的玩家的名字，或名字的前面一部分，不区分大小写）
- 跨服物品展示（聊天内容中输入`[i]`即可将手中的物品发送到聊天栏，输入`[i:x]`可以展示背包中x对应的物品栏的物品，物品栏为0-8，然后从背包左上角从左至右从上至下为9-35，装备栏为36-39，副手为40，一条消息中可以展示多个物品）
- WebSocket，开启WebSocket后配合YinwuChat-Web（Web客户端）可以实现web、游戏内聊天同步
- 关键词屏蔽
- 使用酷Q和酷Q HTTP API来实现Q群聊天同步
- 同步死亡信息到Q群
- 同步玩家进服退服到Q群

注：你需要在你的Bungee服务端和这个Bungee接入的所有的Spigot服务端都安装这个插件

### Q群聊天同步
1. YinwuChat插件配置
    1. 需要开启openwsserver
    2. 将coolQGroup设置为你想同步的Q群的号码
    3. 将coolQAccessToken设置为一个足够复杂足够长的字符串（推荐32位左右的随机字符串）
2. 安装酷Q HTTP API插件
    1. 去 https://github.com/richardchien/coolq-http-api/releases/latest 下载最新版本的coolq-http-api，coolq-http-api具体的安装说明可以到 https://cqhttp.cc/docs/ 或 http://richardchien.gitee.io/coolq-http-api/docs/ 查看
    2. 将coolq-http-api放到酷Q目录下的app目录下
    3. 打开酷Q的应用管理界面，点击重载应用按钮
    4. 找到“[未启用]HTTP API”，点它，然后点右边的启用按钮
    5. 有提示的全部点“是”
    6. 到酷Q目录下的“data\app\io.github.richardchien.coolqhttpapi\config”目录，下，打开你登录的QQ号对应的json文件（比如你登录的QQ号是10000，那文件名就是10000.json）
    7. 将use_http修改为false（如果你没有其他应用需要使用的话）
    8. 将use_ws_reverse修改为true（必须！）
    9. 将ws_reverse_url修改为插件的websocket监听地址加端口（比如你端口是9000，酷Q和mc服务器在一台机器上就填 ws://127.0.0.1:9000/ws）
    10. post_message_format请务必保证是"string"
    11. 将enable_heartbeat设置为true
    12. 增加一行   "ws_reverse_use_universal_client": true,    或者如果你的json文件中有ws_reverse_use_universal_client的话将它改为true（必须！）
    13. 将access_token修改为和YinwuChat配置中的coolQAccessToken一致的内容
    14. 右键酷Q主界面，选择应用-HTTP API-重启应用

### 跨BungeeCord聊天
> 支持公屏聊天、私聊、at等所有功能，但是私聊和at等指定玩家的功能，被拒绝或忽略等情况下，本地提示可能不正确
1. 将新版本BungeeCord端配置文件的redisConfig.openRedis修改为true
2. redisConfig.ip修改为redis服务器的ip
3. redisConfig.port修改为redis服务器的端口
4. redisConfig.password修改为redis服务器的密码
5. redisConfig.selfName修改为每个BungeeCord端都不一样的一个字符串（插件内部标记消息来源及消息目的用，每个BungeeCord必须不一样，无其他要求）
6. 重新加载插件后，在一个BungeeCord端接入的玩家发送的消息可以在其他BungeeCord端接入的玩家处看到

### 配置文件
YinwuChat-Bungeecord的配置文件内容为：

```yaml
#是否开启WebSocket
openwsserver: false

#WebSocket监听端口
wsport: 8888

#WebSocket发送消息时间间隔（毫秒）
wsCooldown: 1000

#安装了BungeeAdminTools插件时，
#在Web端发送消息，使用哪个服务器作为禁言/ban的服务器
webBATserver: lobby

#@玩家时的冷却时间（秒）
atcooldown: 10

#@全体玩家的关键词
#默认为all，可以使用@all来@所有人
#如果你有一个服务器叫做lobby
#那么可以使用@lobbyall来@lobby服务器的所有人
#@lobbyall可以简写为@lall或@loball等（服务器名前面一部分）
atAllKey: all

#链接识别正则表达式，符合该正则的聊天内容会被替换，并且可以点击
linkRegex: ((https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])

#聊天屏蔽模式，目前1为将聊天内容替换为shieldedReplace的内容，其他为直接拦截
shieldedMode: 1

#多少秒内总共发送屏蔽关键词`shieldedKickCount`次就会被踢出服务器(包括web端)
shieldedKickTime: 60

#`shieldedKickTime`秒内发送屏蔽关键词多少次会被踢出服务器
shieldedKickCount: 3

#配置文件的版本，请勿修改
configVersion: 4

#从web页面发送消息到游戏中时禁用的样式代码
webDenyStyle: klmnor

#聊天内容屏蔽关键词，list格式，你需要自己添加这个设置
shieldeds:
#每个关键词一行
- keyword

tipsConfig:
  shieldedKickTip: 你因为发送屏蔽词语，被踢出服务器
  
  #聊天内容中含有屏蔽关键词时，整个消息会被替换为这个
  shieldedReplace: 富强、民主、文明、和谐、自由、平等、公正、法治、爱国、敬业、诚信、友善
  atyouselfTip: '&c你不能@你自己'
  atyouTip: '&e{player}&b@了你'
  cooldownTip: '&c每次使用@功能之间需要等待10秒'
  ignoreTip: '&c对方忽略了你，并向你仍了一个烤土豆'
  banatTip: '&c对方不想被@，只想安安静静的做一个美男子'
  toPlayerNoOnlineTip: '&c对方不在线，无法发送私聊'
  msgyouselfTip: '&c你不能私聊你自己'
  youismuteTip: '&c你正在禁言中，不能说话'
  youisbanTip: '&c你被ban了，不能说话'
  
  #发送的聊天消息中含有屏蔽的关键词时会收到的提醒
  shieldedTip: '&c发送的信息中有被屏蔽的词语，无法发送，继续发送将被踢出服务器'
  
  #聊天内容中的链接将被替换为这个文本
  linkText: '&7[&f&l链接&r&7]&r'
  
#各种消息的格式化设置
formatConfig:
  #WebSocket发送过来的消息格式化内容，
  #由list构成，每段内容都分message、hover、click 3项设置
  format:
  #直接显示在聊天栏的文字，
  #{displayName}将被替换为玩家名
  #hover和click字段中的{displayName}也会替换
  - message: '&b[Web]'
    #鼠标移动到这段消息上时显示的悬浮内容
    hover: 点击打开YinwuChat网页
    #点击这段消息时的动作，自动识别是否链接，如果是链接则打开链接
    #否则如果是以!开头就执行命令，否则就将内容填充到聊天框
    #比如让看到消息的人点击就直接给发消息的人发送tpa请求，
    #就可以写成!/tpa {displayName}（不写斜杠会按发送消息处理）
    click: https://chat.yinwurealm.org
  - message: '&e{displayName}'
    hover: 点击私聊
    click: /msg {displayName}
  - message: ' &6>>> '
  - message: '&r{message}'
  
  #QQ群群员发送的消息，游戏内展示的样式
  qqFormat:
  - message: '&b[QQ群]'
    hover: 点击加入QQ群xxxxx
    #这里可以替换为你QQ群的申请链接
    click: https://xxxxxx.xxxx.xxx
  - message: '&e{displayName}'
  - message: ' &6>>> '
  - message: '&r{message}'
  
  #私聊时，自己收到的消息的格式
  toFormat:
  - message: '&7我 &6-> '
  - message: '&e{displayName}'
    hover: 点击私聊
    click: /msg {displayName}
  - message: ' &6>>> '
  - message: '&r{message}'
  
  #私聊时，对方收到的消息的格式
  fromFormat:
  - message: '&b[Web]'
    hover: 点击打开YinwuChat网页
    click: https://xxxxxx.xxxx.xxx
  - message: '&e{displayName}'
    hover: 点击私聊
    click: /msg {displayName}
  - message: ' &6-> &7我'
  - message: ' &6>>> '
  - message: '&r{message}'
  
  #其他玩家私聊时，有权限的玩家看到的监听消息的样式
  monitorFormat:
  - message: '&7{formPlayer} &6-> '
  - message: '&e{toPlayer}'
  - message: ' &6>>> '
  - message: '&r{message}'
coolQConfig:
  #qq群有新消息时是否发送到游戏中
  coolQQQToGame: true
  
  #qq群有新消息时，只有开头跟这里一样才发送到游戏中
  coolqToGameStart: ''
  
  #游戏中有新消息时是否发送到QQ群中
  coolQGameToQQ: true
  
  #游戏中有新消息时，只有开头跟这里一样才发送到QQ群中
  gameToCoolqStart: ''
  
  #转发QQ群消息到游戏时禁用的样式代码
  qqDenyStyle: 0-9a-fklmnor
  
  #监听的QQ群的群号，酷Q接收到消息时，如果是QQ群，且群号和这里一致，就会转发到游戏中
  coolQGroup: 0
  
  #和酷Q HTTP API插件通信时使用的accesstoken，为空时不验证，强烈建议设置为一个复杂的字符串
  coolQAccessToken: ''
  
  #QQ群中群员发送的@信息将被替换为这个文本
  #{qq}将被替换为被@的人的QQ号
  qqAtText: '&7[@{qq}]&r'
  
  #QQ群中群员发送的图片将被替换为这个文本
  qqImageText: '&7[图片]&r'
  
  #QQ群中群员发送的语音将被替换为这个文本
  qqRecordText: '&7[语音]&r'

#利用redis做跨bc聊天同步的配置
redisConfig:
  #是否开启redis聊天同步
  openRedis: false
  
  #redis服务器的ip地址或域名
  ip: ''
  
  #redis的端口
  port: 0
  
  #一般不要修改
  maxConnection: 8
  
  #redis的密码
  password: ''
  
  #服务器标识，每个bc端的YinwuChat插件的标识请设置为不一样
  selfName: bc1
```
`webBATserver`可以实现WebSocket端的禁言（当你的服务器安装了BungeeAdminTools时，玩家在WebSocket发送信息，会以这个项目的内容作为玩家所在服务器，
去BungeeAdminTools查询该玩家是否被禁言或被ban，当他被禁言或被ban时无法说话，由于BungeeAdminTools禁言、ban人只能选择Bungee的配置文件中实际存在的服务器，
所以这里需要填一个实际存在的服务器的名字，建议使用大厅服的名字）

Bungee-Task配置文件(tasks.yml):
```yaml
tasks:
- enable: true    #是否开启这个任务
  interval: 30    #任务间隔时间
  list:           #格式和Bungee的配置文件中的消息格式一致
  - message: '&e[帮助]'
    hover: 服务器帮助文档
    click: ''
  - message: '&r 在聊天中输入'
  - message: '&b[i]'
    hover: 在聊天文本中包含这三个字符即可
    click: ''
  - message: '&r可以展示你手中的物品，输入'
  - message: '&b[i:x]'
    hover: |-
      &b:&r冒号不区分中英文
      &bx&r为背包格子编号
      物品栏为0-8，然后从背包左上角
      从左至右从上至下为9-35
      装备栏为36-39，副手为40
    click: ''
  - message: '&r可以展示背包中x位置对应的物品，一条消息中可以展示多个物品'
  server: all     #任务对应的服务器，不区分大小写，只有对应的服务器的玩家才会收到消息，为"all"时所有服务器都会广播，为"web"时只有web端才会收到通知
```

YinwuChat-Spigot的配置文件内容为：

```yaml
format:         #格式和Bungee的配置文件中的消息格式一致，但是这里的内容支持PlaceholderAPI变量
- message: '&b[%player_server%]'
  hover: 所在服务器：%player_server%
  click: /server %player_server%
- message: '&e{displayName}'
  hover: 点击私聊
  click: /msg {displayName}
- message: ' &6>>> '
- message: '&r{message}'
toFormat:
- message: '&b[%player_server%]'
  hover: 所在服务器：%player_server%
  click: /server %player_server%
- message: '&e{displayName}'
  hover: 点击私聊
  click: /msg {displayName}
- message: ' &6-> &7我'
- message: '&r{message}'
fromFormat:
- message: '&7我 &6-> '
- message: '&e{displayName}'
  hover: 点击私聊
  click: /msg {displayName}
- message: '&r{message}'
eventDelayTime: 50    #接收消息处理延时，单位为毫秒，用于处理部分需要使用聊天栏信息来交互的插件的运行（比如箱子商店等），延时时间就是等待其他插件处理的时间
messageHandles:       #自定义消息内容替换，比如下面默认的设置，发送消息时，消息中含有[p]的，[p]会被替换为位置
- placeholder: \[p\]    #消息中的哪些内容会被替换，写法是正则表达式，所以本来是替换[p]的，由于是正则表达式，两个方括号都需要加反斜杠转义
  format:               #替换成的消息样式，格式和前面的format格式一致，支持papi变量
  - message: '&7[位置]'
    hover: |-
      所在服务器：ServerName
      所在世界：%player_world%
      坐标：X:%player_x% Y:%player_y% Z:%player_z%
    click: ''
configVersion: 1  #配置文件的版本，请勿修改
```

### 接口

本插件所有信息均由WebSocket通信，格式均为JSON格式，具体数据如下：
#### 发往本插件的数据：
1. 检查token
```json
{
    "action": "check_token",
    "token": "待检查的token，token由服务器下发，初次连接时可以使用空字符串"
}
```
2. 发送消息
```json
{
    "action": "send_message",
    "message": "需要发送的消息，注意，格式代码必须使用§"
}
```

#### 发往Web客户端的数据：
1. 更新token（接收到客户端发送的check_token数据，然后检查token失败时下发，收到该数据应提醒玩家在游戏内输入/yinwuchat token title命令绑定token）
```json
{
    "action": "update_token",
    "token": "一个随机的token"
}
```
2. token校验结果（检查token成功后返回，或玩家在游戏内绑定成功后，token对应的WebSocket在线时主动发送，只有接收到了这个数据，且数据中的status为true，且数据中的isbind为true时才可以向服务器发送send_message数据）
```json5
{
    "action": "check_token",
    "status": true,        //表示该token是否有效
    "message": "成功时为success，失败时为原因，并同时发送一个更新token数据",
    "isbind": false         //表示该token是否被玩家绑定
}
```
3. 玩家在游戏内发送了消息
```json
{
    "action": "send_message",
    "message": "消息内容"
}
```
4. 游戏玩家列表
```json
{
    "action": "game_player_list",
    "player_list":[
        {
            "player_name": "玩家游戏名",
            "server_name": "玩家所在服务器"
        }
    ]
}
```
5. WebClient玩家列表
```json
{
    "action": "web_player_list",
    "player_list":[
        "玩家名1",
        "玩家名2"
    ]
}
```
6. 服务器提示消息（一般为和服务器发送数据包后的错误反馈信息）
```json5
{
    "action": "server_message",
    "message": "消息内容",
    "time": 1, //unix时间戳,
    "status": 1 //状态码，详情见下方表格(int)
}
```

#### 服务器消息状态码
状态码|具体含义
-:|-
0|一般成功或提示消息
1|一般错误消息
1001|获取历史聊天记录时，内容为空（不可继续获取历史消息）

### Bungeecord端命令
1. 控制台命令
    - `yinwuchat reload [config|ws]`：重新加载插件，或仅重新加载配置（在ws配置有变动时自动重启ws），或只重启ws
2. 游戏内命令
    - `/yinwuchat`：插件帮助（其他未识别的命令也都将显示帮助）
    - `/yinwuchat reload [config|ws]`：重新加载配置文件，执行这个命令需要你具有`yinwuchat.reload`权限
    - `/yinwuchat bind <token>`：绑定token，`token`是插件下发给web客户端的，玩家从web客户端获取token后到游戏内使用命令将玩家和token进行绑定
    - `/yinwuchat list`：列出玩家已绑定的token
    - `/yinwuchat unbind <token>`：解绑token，当你需要解绑某个token时使用（如在公共场合绑定了token，或者不想用这个token了等），`token`为使用`list`命令时查询到的`token`，可以只输入前面部分文字
    - `/msg <玩家名> <消息>`：向玩家发送私聊消息
    - `/yinwuchat vanish`：切换聊天系统隐身模式（无法被@，无法被私聊，web端无法看见在线，需要有`yinwuchat.vanish`权限）
    - `/yinwuchat ignore <玩家名>`：忽略/取消忽略玩家消息
    - `/yinwuchat noat`：禁止/允许自己被@（@全体除外）
    - `/yinwuchat muteat`：切换自己被@时有没有声音
    - `/yinwuchat monitor`：切换是否监听其他玩家的私聊消息
3. WebClient命令
    - `/msg <玩家名> <消息>`：向玩家发送私聊消息

### Bukkit端命令
`yinwuchat-bukkit reload`: 重新加载配置，需要权限`yinwuchat.reload

### Bungee端权限
- `yinwuchat.reload`玩家可以在游戏中使用`/yinwuchat reload`命令重新加载插件配置
- `yinwuchat.cooldown.bypass`@人没有冷却时间
- `yinwuchat.atall`允许@所有人
- `yinwuchat.vanish`允许进入聊天隐身模式
- `yinwuchat.badword`允许编辑聊天系统关键词列表
- `yinwuchat.monitor`允许玩家使用`/yinwuchat monitor`命令，并允许玩家监听其他玩家的私聊消息
* 权限需要在Bungeecord中设置，玩家可以在Bungeecord连接到的任何服务器使用这个命令

### Bukkit端权限
`yinwuchat.reload`玩家可以在游戏中使用`/yinwuchat-bukkit reload`命令重新加载bukkit端yinwuchat的配置，默认权限：仅OP可以使用
`yinwuchat.style.x`是否允许玩家使用对应的样式代码，`x`为具体样式代码，具体为`0-9`,`a-f`,`klmnor`共22个样式代码，默认设置时`0-9`,`a-f`,`r`为允许，其他为不允许

### @所有人
@所有人可以@整个服务器所有人（不包括WebSocket），或者分服务器@该服务器所有人（不包括WebSocket）
具体使用方法为：
假如配置文件中的`atAllKey`是默认的`all`，那么聊天内容中含有`@all`时即可@整个服务器的人（all后面不能紧接着英文或数字，可以是中文、空格等）
假如你有一个服务器名字为`lobby`，那么聊天内容中含有`@lall`或`@lobbyall`时，即可@lobby服务器的所有人（即服务器名只需要输入前面部分即可，该服务器名为BungeeCord配置文件中的名字）

### 错误信息
有些时候，玩家执行命令的时候可能会碰到一些错误（主要为数据库错误），具体含义为：

错误代码|具体含义
-:|-
001|根据UUID查找用户失败，且新增失败

### 其他信息
本插件由国内正版Minecraft服务器[YinwuRealm](https://www.yinwurealm.org/)玩家[LinTx](https://mine.ly/LinTx.1)为服务器开发