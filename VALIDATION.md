# Verification — fix 2

Build: Java 21.0.2, Gradle 8.12, Fabric Loader 0.19.5, Fabric API 0.116.17+1.21.1, GeckoLib 4.9.3, Minecraft 1.21.1.

The distributable was tested in two disposable integrated-server/client instances:

- Standalone, without VanillaBackport or Sodium.
- With the supplied VanillaBackport fork 1.2.0.2638.9 and Sodium 0.8.13+mc1.21.1.

Both instances loaded the Sift dimension, completed runtime assertions, saved their worlds and exited normally. The user log lists fork 1.2.0.2639.1 and a larger modpack; those exact combinations were not reproduced.

## Corrections and checks

- Only the Siftite spear is registered by The Sift; all six unintended spear registrations and their resources are removed.
- The generated GUI icon uses flat lighting and applies its transform once. Its model is neither a custom renderer nor block-lit. Captures were visually inspected beside vanilla diamond and sword icons.
- First/third-person held contexts select the long spear model through ItemRenderer. Idle, jab and actively held charging poses rendered in first person and both third-person camera views. Charging captures assert that item use is active.
- A fresh-world third-person crash was reproduced: ItemInHandLayer passed a null swinging hand to getItemInHand before a swing had been recorded. The item and arm animation paths now guard that state. The same fresh-world camera sequence then completed without a crash.
- Embedded movement methods and fields use separate names. The compatibility test reports none of the computeSpeed/getKnownSpeed overwrite conflicts seen in the user's log.
- Runtime assertions cover component serialization, shelf save/load with an enchanted spear, both added recipes, five custom entity constructors, spear jab damage at 3.5 blocks and Lunge movement. The jab target position and immunity are reset immediately before the assertion to prevent ambient movement/fire from changing the fixture.
- Standalone smithing accepts a trident and does not conflict with the existing Siftite sword upgrade. The compatibility test also confirms acceptance of the fork's minecraft:netherite_spear.
- Earlier dedicated-server startup, Sift dimension generation and normal shutdown checks passed.
- Gradle build/remapping completed successfully. Every packaged JSON parses and all JAR entries pass CRC verification. Classes use Java 21 bytecode and contain no NeoForge or external VanillaBackport class references. No diagnostic mod is included in the distributable.

This cleaned source archive omits optional screenshots, assertion logs, and diagnostic Java sources from validation/. They were not part of the shipped mod and are not used by the Gradle build. The historical testing results below remain documentation only.

These tests do not cover the entire user modpack, shader packs, every gameplay interaction or old-world upgrades. Original entity data-fixer notices, unavailable optional fork integration classes and offline authentication/audio notices are outside these fixes.

## fix5 spear first-person renderer parity
- Added `ItemInHandRenderer.tick()` WrapOperation for `LocalPlayer#getAttackStrengthScale(float)`.
- The Sift now supplies its separate item-swap scale, matching modern vanilla's separation between attack cooldown and held-item swap height.
- Prevents the Siftite spear from inheriting the 1.21.1 sword-style downward hand dip after a stab.
- The stab transform itself remains the modern spear transform.


## fix14 startup verifier repair
- The fix13 diagnostic exposed the actual startup failure: a JVM `VerifyError` in `KineticWeapon.damageEntities`.
- The remapped StackMapTable merged the EnderDragonPart target branch as `java/lang/Object`, even though the source local is an `Entity`.
- The shipped class now performs an explicit `net.minecraft.world.entity.Entity` checkcast at the control-flow join before `MobSpearHandler` receives the target.
- This removes the bad operand-stack type that prevented `ModDataComponents` / `BackportContent` from initializing.
- Diagnostic logging/mixins are not included in fix14.
## Fix16 verifier correction
- Fix15 diagnostic exposed a second JVM VerifyError at the final `MobSpearHandler.theSift$stabAttack(...)` call in `KineticWeapon.damageEntities()`.
- The target entity local is widened to `Object` by the preserved StackMapTable across the condition-branch merge.
- Fix16 adds an explicit `Entity` cast at that final call site, matching the verifier-required method descriptor.
- Diagnostic wrappers are not included in the release JAR.

