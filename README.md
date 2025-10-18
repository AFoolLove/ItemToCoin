# Item To Coin

将物品转换为货币(sdm)

单击右键换取一次  
潜行+右键全部换取

### 配置

路径：`./config/itemtocoin/toCoins.json`

1. 1个弓 换取 5个itc货币
2. 3个石头 换取 2个itc货币

```json
{
  "minecraft:bow": {
    "amount": 1,
    "type": "itc",
    "rate": 5
  },
  "minecraft:stone": {
    "amount": 3,
    "type": "itc",
    "rate": 2
  }
}
```

## 音效

转换成功后播放一个block类型的音效  
配置同样在：`./config/itemtocoin/toCoins.json` 文件中

1. `itemtocoin:sound` 单个转换音效
2. `itemtocoin:sounds` 批量转换音效

```json
{
  "itemtocoin:sound": {
    "type": "minecraft:entity.experience_orb.pickup"
  },
  "itemtocoin:sounds": {
    "type": "minecraft:entity.player.levelup"
  }
}
```

## 完整配置内容示例

`./config/itemtocoin/toCoins.json`

```json
 {
  "itemtocoin:sound": {
    "type": "minecraft:entity.experience_orb.pickup"
  },
  "itemtocoin:sounds": {
    "type": "minecraft:entity.player.levelup"
  },
  "minecraft:bow": {
    "amount": 1,
    "type": "itc",
    "rate": 5
  },
  "minecraft:stone": {
    "amount": 3,
    "type": "itc",
    "rate": 2
  }
}
```