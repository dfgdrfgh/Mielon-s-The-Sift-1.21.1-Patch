#!/usr/bin/env python3
"""Ensure The Sift's Ancient City portal center cannot be lost to data-pack priority."""
from pathlib import Path
import gzip

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / "src" / "main" / "resources" / "data"
CHECKED = 0

for i in (1, 2, 3):
    relative = Path("ancient_city") / "city_center" / f"city_center_{i}.nbt"
    original = BASE / "minecraft" / "structure" / relative
    namespaced = BASE / "the_sift" / "structure" / relative
    assert original.is_file(), f"Missing original Ancient City template: {original}"
    assert namespaced.is_file(), f"Missing owned Ancient City template: {namespaced}"
    assert original.read_bytes() == namespaced.read_bytes(), (
        f"Ancient City center_{i} does not match its namespaced fallback"
    )
    data = gzip.decompress(namespaced.read_bytes())
    assert b"the_sift:sonorous_deepslate" in data, (
        f"Ancient City center_{i} lost Sonorous Deepslate from its NBT palette"
    )
    CHECKED += 1

print(f"SIFT_ANCIENT_CITY_TEMPLATES_PASS: {CHECKED} identical templates with Sonorous Deepslate")
