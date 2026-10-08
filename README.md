# Mielon's The Sift — Fabric 1.21.1 backport

Unofficial reconstructed source and Fabric port of Mielon's The Sift 1.1.2.

## Installation

Use Minecraft **1.21.1**, **Java 21**, Fabric Loader **0.19.5 or newer**, Fabric API **0.116.17+1.21.1**, and GeckoLib **4.9.3 for Fabric 1.21.1**. Put the compiled mod and those dependencies in your instance's `mods` folder. Replace the original The Sift JAR with this build.

VanillaBackport and NeoForge are **not dependencies**. The shelf and spear support is included inside this mod. Install the same mod/dependency versions on both client and server.

## Content and controls

This port includes the Sift dimension, world generation, mobs, Ichor, portal mechanics, equipment, particles, sounds, custom sky, and signs from the matching 1.1.2 content release. The newer overgrown willow shelf and Siftite spear are included. Shelves store three items and use the backported powered-chain/hotbar swapping behavior. Left-click with a spear to jab; hold right-click to charge it and hit targets through movement. Lunge is available as `the_sift:lunge`.

Only the Siftite spear is registered by this mod. Smith it with a trident, a Siftite upgrade template, and a Siftite ingot. If another mod supplies `minecraft:netherite_spear`, that spear is also accepted as the smithing base. VanillaBackport remains optional.

The inventory, dropped-item, and shelf icons use normal generated-item rendering. First- and third-person contexts use the longer held spear model. This applies each transform once and preserves vanilla flat GUI lighting.

## Build from source

Set `JAVA_HOME` to a JDK 21 installation, then run:

```sh
./gradlew build
```

On Windows use `gradlew.bat build`. The distributable JAR is in `build/libs/`; do not install the `sources` or `dev` JARs. The project uses Gradle 8.12, Fabric Loom 1.9.2, and Mojang mappings. A build-only copy of the unmodified GeckoLib 4.9.3 classes is included in `libs/` (its Loom manifest marker is removed for compatibility with this Loom release). GeckoLib remains an external runtime dependency.

## Provenance and licensing

The uploaded Fabric 26.3 JAR was decompiled for comparison and newer content/assets. The author's matching 1.1.2 NeoForge 1.21.1 release supplied the version-compatible gameplay and rendering baseline; its loader integration was replaced with Fabric APIs and mixins.

Relevant spear, movement, shelf, animation, codec, and asset code from the supplied VanillaBackport fork 1.2.0.2638.9 was isolated under `mielon.thesift.backport` and `the_sift:`. Unrelated VanillaBackport systems are excluded. Attribution and MIT notices are retained in `src/main/resources/LICENSE_the_sift` and `LICENSE_vanillabackport_internal`.

Original mod: https://modrinth.com/mod/mielons-the-sift (Mielon).
VanillaBackport upstream: https://github.com/ItsBlackGear/VanillaBackport (BlackGear and contributors).
The supplied MIT-licensed fork: https://github.com/dfgdrfgh/TrueVanillaBackport.

This is reconstructed Java source; local names, formatting, and comments can differ from the original developer's source. It is an unofficial compatibility build, not an upstream release.
