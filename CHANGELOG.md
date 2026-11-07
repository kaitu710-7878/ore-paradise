# CHANGELOG / 変更履歴

All notable changes to this project will be documented in this file.
このプロジェクトのすべての重要な変更はこのファイルに記録されます。

---

## [0.1.3-alpha] - 2026-11-07

### Fixed / 修正
- **Complete Eradication of Return Ghost Blocks (帰還時のゴーストブロック完全消滅)**
  - Identified the root cause of the phantom extractor appearing below trees at y=64: the return extractor placed in the Ore Dimension at y=64 was lingering in client-side chunk cache when teleporting back to Overworld.
  - 樹上設置時に木の下（y=64）にゴースト生成機が発生していた根本原因を特定（鉱石dimのy=64に生成された帰還用抽出機がクライアントのチャンクメモリからアンロードされずオーバーワールドの同じ座標に重なって見えていた）。
  - Immediately destroys and unlinks the Ore Dimension return extractor upon use, and sends an explicit block removal packet before dimension traversal so no phantom blocks can ever cross over.
  - 帰還時に鉱石dim側の帰還用抽出機を即座に破棄（AIR化およびBlockEntity削除）し、クライアントへ消滅パケットを送信してからテレポートすることで、ディメンションをまたぐ残像・ゴーストブロックを物理的に完全根絶。
  - Enhanced safe landing detection to properly support tree canopies (leaves) and other sturdy platforms.
  - 樹上（葉ブロック上）などの足場にも安全に着地できるよう足場判定を強化。

---

## [0.1.2-alpha] - 2026-11-07

### Fixed / 修正
- **Eliminated Ghost Blocks on Return (帰還時のゴーストブロック増殖・右クリック消滅現象の根絶)**
  - Removed duplicate packet calls (`player.connection.teleport`) immediately after dimension change to prevent client-side desync.
  - Restricted interaction handling strictly to `InteractionHand.MAIN_HAND` to avoid double-execution and unintentional placement predictions.
  - Added proactive surrounding block synchronization (`syncSurroundingBlocks`).

---

## [0.1.1-alpha] - 2026-11-07

### Added / 追加
- **Almost Unified Integration (Almost Unified 連携)**
  - Added seamless soft-dependency compatibility with Almost Unified.
  - Automatically unifies duplicate ores into the single preferred/dominant variant.

### Fixed / 修正
- **Invisible Collision & Jittering on Return (帰還時の透明当たり判定・ガクガク現象の解消)**
  - Migrated `MultiPhaseOreExtractorBlock` from `BaseEntityBlock` to `Block implements EntityBlock`.
  - Added network sync packets to block entity.
  - Improved return teleportation logic with safe adjacent landing detection.

---

## [0.1.0-alpha] - 2026-11-07

### Added / 追加
- Initial implementation of Multi-phase transition type ore extractor and Ore Dimension.
