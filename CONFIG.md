# Configuration Specification / コンフィグ仕様書

This document describes the parameters and behavioral specifications of Ore Paradise.
本文書では Ore Paradise の各種仕様値および動作仕様を解説します。

---

## English

### Parameters & Default Behaviors
- **Extraction Rate:** 64 items / 1 tick (20 times per second = 1,280 items/sec)
- **Extraction Target:** Any item tagged under `#forge:ores` (registered via Forge tags, with vanilla ore fallback)
- **Storage Target:** Any block entity with `ForgeCapabilities.ITEM_HANDLER` adjacent to the extractor (all 6 directions)
- **Almost Unified Compatibility:** 
  - Enabled automatically when Almost Unified is present.
  - Replaces duplicate ore items and ore blocks with the dominant unified variant configured by Almost Unified.
- **Dimension Y Bounds:**
  - Min Y: `-64` (Bedrock layer)
  - Ore Layer Range: `y = -63` to `y = 63` (Uniform random generation across all depths)
  - Surface Air: `y = 64` to `y = 320`
- **Mob Spawning:** Disabled across the entire dimension.

---

## 日本語

### パラメータおよび基本動作仕様
- **抽出速度:** 1tickあたり64個（1秒あたり20回 = 1,280個/秒）
- **抽出対象アイテム:** `#forge:ores` タグが付与されている全アイテム（Forgeタグレジストリ動的取得＋バニラ鉱石フォールバック）
- **格納先ストレージ:** 抽出機に隣接する6方向の `ForgeCapabilities.ITEM_HANDLER` 対応ブロックエンティティ
- **Almost Unified 互換性:**
  - Almost Unified 導入環境下で自動的に有効化されます。
  - 重複する鉱石ブロックおよび鉱石アイテムを Almost Unified で設定された優先代表種へ自動置換・単一化します。
- **ディメンション高度構成:**
  - 最低高度: `y = -64`（岩盤層）
  - 鉱石層範囲: `y = -63` ～ `y = 63`（全高度均一な完全ランダム鉱石ブロック）
  - 地表上空: `y = 64` ～ `y = 320`（空気）
- **モブスポーン:** ディメンション全体で完全に無効化（敵対・友好ともに一切スポーンしません）。
