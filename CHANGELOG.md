# CHANGELOG / 変更履歴

All notable changes to this project will be documented in this file.
このプロジェクトのすべての重要な変更はこのファイルに記録されます。

---

## [0.1.1-alpha] - 2026-11-07

### Added / 追加
- **Almost Unified Integration (Almost Unified 連携)**
  - Added seamless soft-dependency compatibility with Almost Unified.
  - Almost Unified とのソフト依存連携を実装。
  - Automatically unifies duplicate ores (e.g. multiple uranium ores from different mods) into the single preferred/dominant variant for both dimension generation and extractor output.
  - ディメンション生成および抽出機からの出力の両方において、重複する鉱石（複数MODのウラン鉱石など）を自動的に代表の1種類に統一。

### Fixed / 修正
- **Invisible Collision & Jittering on Return (帰還時の透明当たり判定・ガクガク現象の解消)**
  - Migrated `MultiPhaseOreExtractorBlock` from `BaseEntityBlock` to `Block implements EntityBlock` to guarantee standard baked model rendering across all optimization mods (Embeddium, Sodium, etc.).
  - `BaseEntityBlock` から `Block implements EntityBlock` 構成へ刷新し、描画最適化MOD環境下でも通常ブロックとして確実に描画されるよう修正。
  - Added network sync packets (`getUpdatePacket`, `getUpdateTag`, `onDataPacket`) to the block entity for proper client-server data synchronization.
  - ブロックエンティティにクライアント・サーバー間のデータ同期パケットを実装。
  - Improved return teleportation logic with safe adjacent landing detection, preloading destination chunks, and explicit client-side position sync packets (`connection.teleport`) to completely eliminate collision box overlap, slowdown, and jittering.
  - 帰還時のテレポート処理を改善し、隣接する安全な足場への着地判定、チャンク事前読み込み、クライアント位置同期パケットの発行により、ブロック埋まり・減速・ガクガク現象を完全に解消。

---

## [0.1.0-alpha] - 2026-11-07

### Added / 追加
- **Multi-phase transition type ore extractor (多相転移型鉱石抽出機)**
  - Extracts 64 items of a random ore type (`#forge:ores`) per tick into adjacent storages.
  - 1tickあたりランダムな鉱石1種（`#forge:ores`）を64個、隣接ストレージに格納。
  - Sneak + Right-Click teleports the player between the current dimension and the Ore Dimension.
  - スニーク右クリックで現在地と鉱石ディメンションの間をテレポート。
  - Recipe: 1 Cobblestone surrounded by 8 Ender Pearls.
  - レシピ: 丸石を中心とし、周囲8スロットをエンダーパールで囲む。
- **Ore Dimension (鉱石ディメンション)**
  - Superflat dimension without structure noise or height-based ore disparity.
  - 高度差による鉱石の違いのない、スーパーフラット形状の専用ディメンション。
  - Lowest layer (y = -64) consists entirely of Bedrock.
  - 最低高度（y = -64）はすべて岩盤。
  - Layers from y = -63 up to y = 63 consist entirely of random `#forge:ores` blocks.
  - y = -63 から y = 63 まではすべてランダムな `#forge:ores` ブロックで構成。
  - Height limits are identical to Overworld (y = -64 to 320).
  - 高度の下限・上限はオーバーワールドと同一（y = -64 ～ 320）。
  - Complete mob spawn suppression (no mobs spawn).
  - モブスポーン完全無効化。
  - Automatically generates a return extractor at the arrival location with linked return coordinates.
  - 転移時に到着地点へ帰還用の抽出機を自動生成（元の座標へ安全に帰還可能）。
