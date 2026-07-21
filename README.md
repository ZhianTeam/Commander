<p align="center">
  <img src="apk/res/mipmap-hdpi-v4/i.png" width="96" height="96" alt="Commander Icon" style="border-radius: 24%; box-shadow: 0 4px 12px rgba(0,0,0,0.15);"/>
</p>

<h1 align="center">Commander</h1>

<p align="center">
  <strong>Run command like chatting.</strong>
</p>

<p align="center">
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack_Compose-M3E-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose"></a>
  <a href="https://developer.android.com/about/versions/9"><img src="https://img.shields.io/badge/minSDK-28_(Android_9)-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="minSDK"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-orange.svg?style=for-the-badge" alt="License: MIT"></a>
  <a href="https://github.com/Catndroid/Commander"><img src="https://img.shields.io/github/stars/Catndroid/Commander?style=for-the-badge&color=yellow" alt="GitHub Repo stars"></a>
</p>

---

[TOC]

---

**Commander** is an Android terminal app that lets you run all kinds of shell commands as naturally as chatting with a friend.

This is a completely refactored version, migrated from iApp to Kotlin (with Jetpack Compose).

## Features
- IM-like terminal experience
- and **No More**! It even cannot run any command.

<details>
    <summary>Refactor TODOs</summary>
    - UI
      - [ ] Material 3 Expressive UI
      - [ ] New UI Icons
      - [ ] Springy component click animation
      - [ ] Monet
        - [ ] Follow System Monet
        - [ ] Built-in color scheme Preset (for Android 11- bros)
        - [ ] ColorSpec 2025 (M3E's standard)
        - [ ] Custom Monet palette (HEX/RGB/HSL/OKLCH, override default P/S/T value)
      - [ ] Optional Light Mode
    - UX
      - [ ] D-Pad input panel
      - [ ] Escape sequence input panel (Ctrl, Alt, etc.)
      - [ ] Quick input toggle (whether long-press button to input continuously or not)
      - [ ] Ctrl/Alt/Shift lock
    - Terminal itself
      - [ ] Color Scheme Toggle
        - [ ] Extract & Generate terminal color scheme from Monet
        - [ ] Built-in popular color scheme
          - [ ] Catppuccin
          - [ ] Solarized
          - [ ] Gruvbox
          - [ ] Tokyo Night
          - [ ] Nord
        - [ ] Terminal color follows App (Light/Dark)
      - [ ] Ligatures (for supported fonts)
      - [ ] Request real PTY instead of `Runtime.getRuntime().exec()`
      - [ ] Alternative Screen (for full-screen programs like `less`, `nvim`)
      - [ ] Shell Changing (hook `chsh`)
      - [ ] 24-bit True Color
      - [ ] Built-in `rish` (Rikka's Shell, works with Shizuku/Sui)
      - [ ] More Moe Easter eggs! (⁠｡⁠•̀⁠ᴗ⁠-⁠)⁠✧
    - Miscs
      - [ ] eat Catndroid
      - [ ] Clean up old spaghetti codes / iApp bloats
</details>

## Quick Start
<details>
    <summary>😎 Claude Code user?</summary>
    This repository is Understand-Anything ready.
    Follow the 3-step guide to get started.
    
    ## I. Clone the Repo
    ```bash
    $ git clone https://github.com/ZhianTeam/Commander.git # or via ssh
    $ cd Commander/
    ```
    
    ## II. Launch your Claude Code and Install Understand-Anything
    ```bash
    > /plugin marketplace add Lum1104/Understand-Anything
    > /plugin install understand-anything@understand-anything
    ```
    
    ## III. Fire in the Hole!
    ```bash
    > /understand-onboard
    ```
    Transform your codebase into an interactive knowledge graph: modules, call hierarchies, and workflows, all fully visualized.
    Also tells you what you need, how to build, etc.
    Fast as Superman, stop spending time on browsing repo.
</details>
### Requirements
- Gradle 9+
- OpenJDK 21
- A "global network" environment (Need to connect Google Maven Central)
- Build tools & NDK at least API 28 (Android 9+)

### Compilation 
#### I. Get Down
```bash
$ git clone https://github.com/ZhianTeam/Commander.git # or via ssh
$ cd Commander/
```
#### II. Run Compilation
> [!important]
> Make sure **you have setup everything**!

```bash
$ chmod +x ./gradlew
$ ./gradlew assembleDebug --no-daemon --warning-mode=all
```

## Acknowledgement
Best thanks to the following people! *(shorted by alphabet)*
- [Catndroid](@Catndroid)
- Lunox
- XiaoMozi
- 肖珞茜 (呆毛)
- Yanshan 燕山

## License
[MIT License](/LICENSE).
