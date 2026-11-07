# Ore Paradise (鉱石パラダイス)

[English](#english) | [日本語](#日本語)

---

## English

### Overview
**Ore Paradise** is a high-yield resource utility mod for Minecraft 1.20.1 (Forge). It introduces the ultra-fast **Multi-phase transition type ore extractor** and the endless flat **Ore Dimension**.

### Features
1. **Multi-phase transition type ore extractor**
   - Automatically extracts 64 items per tick (1,280 items/sec) belonging to the `#forge:ores` item tag.
   - Instantly routes extracted items into adjacent storages (chests, barrels, drawers, etc.) across all 6 faces.
   - Sneak + Right-Click to warp seamlessly between your current dimension and the Ore Dimension.
   - Craftable in a standard 3x3 crafting grid by surrounding 1 Cobblestone with 8 Ender Pearls.

2. **Ore Dimension**
   - A completely flat superflat-style dimension designed for high-efficiency mining and observation.
   - **Bedrock Foundation:** The lowest layer (`y = -64`) is solid Bedrock.
   - **Ore Layer:** From `y = -63` up to `y = 63`, every single block is a randomized ore block tagged `#forge:ores`.
   - **Uniform Distribution:** No depth-dependent rarity or distribution variance; all registered ores appear uniformly.
   - **Zero Mob Spawns:** Mobs will never spawn in this dimension, ensuring a tranquil and safe environment.
   - **Safe Transit:** Transferring to the Ore Dimension automatically places a return extractor right beside you, pre-linked to your departure coordinates for an effortless journey back.

3. **Almost Unified Integration**
   - Built-in soft-dependency integration with **Almost Unified**.
   - When Almost Unified is installed, duplicate ores from different mods (such as multiple uranium ores) are automatically unified into the single dominant variant in both dimension generation and extractor output.

### Installation
1. Install **Minecraft Forge 1.20.1** (recommended version 47.3.0 or higher).
2. Place the built jar file (`ore_paradise-0.1.3-alpha.jar`) into your Minecraft `mods` folder.
3. Launch Minecraft and enjoy!

### License
This mod is licensed under the **GNU General Public License v3.0 (GPL-3.0-or-later)**.

---

## 日本語

### 概要
**Ore Paradise** は、Minecraft 1.20.1 (Forge) 向けの超高速資源ユーティリティMODです。超高速で鉱石を抽出し続ける**多相転移型鉱石抽出機 (Multi-phase transition type ore extractor)** と、果てしない鉱石層で満たされた**鉱石ディメンション (Ore Dimension)** を追加します。

### 主な特徴
1. **多相転移型鉱石抽出機 (Multi-phase transition type ore extractor)**
   - 1tickあたりランダムな `#forge:ores` タグ付きアイテム1種を64個（毎秒1,280個）のペースで抽出します。
   - 隣接する6方向のストレージ（チェスト、樽、ストレージMODのインベントリ等）へ直接自動格納します。
   - スニーク右クリックすることで、現在地と鉱石ディメンションの間を瞬時にテレポート移動できます。
   - 作業台で中央に丸石を置き、周囲8スロットをエンダーパールで囲むことでクラフト可能です。

2. **鉱石ディメンション (Ore Dimension)**
   - スーパーフラットのように平坦な専用ディメンションです。
   - **岩盤の底面:** 最低高度（`y = -64`）は全面が岩盤で保護されています。
   - **果てしない鉱石層:** `y = -63` から `y = 63` までの空間が、すべてランダムな `#forge:ores` タグ付きブロックで構成されています。
   - **均一な生成:** 高度による鉱石生成の偏りは一切なく、すべての鉱石が均一な確率で出現します。
   - **モブスポーン完全無効:** モブが一切スポーンしないため、安全かつ快適に作業や採掘が行えます。
   - **自動帰還ポイント生成:** 移動時にプレイヤーの目の前へ帰還用の多相転移型鉱石抽出機が自動配置され、元のディメンション・座標が記憶されているため、いつでも安全に元の場所へ戻ることができます。

3. **Almost Unified 連携対応**
   - **Almost Unified** との自動ソフト連携を標準搭載。
   - Almost Unified が導入されている場合、複数MODによる重複鉱石（例: 複数種類のウラン鉱石など）を、ディメンション生成・チェスト格納の両方で代表の1種類に自動統合します。

### 導入手順
1. **Minecraft Forge 1.20.1**（推奨バージョン: 47.3.0以上）を導入します。
2. ビルドされたJARファイル（`ore_paradise-0.1.3-alpha.jar`）を `.minecraft/mods` フォルダに配置します。
3. ゲームを起動してお楽しみください。

### ライセンス
本MODは **GNU General Public License v3.0 (GPL-3.0-or-later)** の下で公開されています。
