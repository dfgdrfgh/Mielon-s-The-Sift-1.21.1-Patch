#!/usr/bin/env python3
"""Verify Ancient City pool IDs and *placed* Sonorous Deepslate NBT blocks."""
import gzip
import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / "src" / "main" / "resources" / "data"

class NBT:
    def __init__(self, payload):
        self.data = memoryview(payload)
        self.offset = 0

    def take(self, length):
        value = self.data[self.offset:self.offset + length]
        if len(value) != length:
            raise ValueError("Truncated NBT")
        self.offset += length
        return value

    def unpack(self, fmt):
        return struct.unpack(">" + fmt, self.take(struct.calcsize(">" + fmt)))[0]

    def string(self):
        return bytes(self.take(self.unpack("H"))).decode("utf-8")

    def payload(self, tag):
        if 1 <= tag <= 6:
            return self.unpack({1: "b", 2: "h", 3: "i", 4: "q", 5: "f", 6: "d"}[tag])
        if tag == 7:
            length = self.unpack("i")
            assert length >= 0
            return bytes(self.take(length))
        if tag == 8:
            return self.string()
        if tag == 9:
            inner = self.unpack("B")
            length = self.unpack("i")
            assert length >= 0
            return [self.payload(inner) for _ in range(length)]
        if tag == 10:
            contents = {}
            while True:
                inner = self.unpack("B")
                if inner == 0:
                    return contents
                contents[self.string()] = self.payload(inner)
        if tag in (11, 12):
            length = self.unpack("i")
            assert length >= 0
            return [self.unpack("i" if tag == 11 else "q") for _ in range(length)]
        raise ValueError(f"Unexpected NBT tag {tag}")

    def parse(self):
        assert self.unpack("B") == 10
        self.string()
        result = self.payload(10)
        assert self.offset == len(self.data), "Trailing NBT data"
        return result

pool_path = BASE / "minecraft/worldgen/template_pool/ancient_city/city_center.json"
pool = json.loads(pool_path.read_text(encoding="utf-8"))
assert pool["fallback"] == "minecraft:empty"
assert len(pool["elements"]) == 3, "Expected all three Ancient City starting center pieces"
for i, entry in enumerate(pool["elements"], start=1):
    assert entry["weight"] == 1
    element = entry["element"]
    assert element["element_type"] == "minecraft:single_pool_element"
    assert element["location"] == f"the_sift:ancient_city/city_center/city_center_{i}"
    assert element["processors"] == "minecraft:ancient_city_start_degradation"
    assert element["projection"] == "rigid"

total = 0
for i in (1, 2, 3):
    relative = Path("ancient_city") / "city_center" / f"city_center_{i}.nbt"
    original = BASE / "minecraft/structure" / relative
    namespaced = BASE / "the_sift/structure" / relative
    assert original.is_file(), f"Missing original: {original}"
    assert namespaced.is_file(), f"Missing namespaced template: {namespaced}"
    assert original.read_bytes() == namespaced.read_bytes(), f"Template {i} has drifted"
    nbt = NBT(gzip.decompress(namespaced.read_bytes())).parse()
    palette = nbt["palette"] if "palette" in nbt else nbt["palettes"][0]
    sonorous_indices = {
        idx for idx, state in enumerate(palette)
        if state["Name"] == "the_sift:sonorous_deepslate"
    }
    assert sonorous_indices, f"Template {i}: block name absent from NBT palette"
    positions = [tuple(block["pos"]) for block in nbt["blocks"] if block["state"] in sonorous_indices]
    assert positions, f"Template {i}: Sonorous Deepslate appears only in palette, never placed"
    total += len(positions)
    print(f"Ancient City center_{i}: {len(positions)} Sonorous Deepslate placements; first: {positions[:8]}")

print(f"SIFT_ANCIENT_CITY_TEMPLATES_PASS: 3 pools, {total} actual Sonorous Deepslate placements")
