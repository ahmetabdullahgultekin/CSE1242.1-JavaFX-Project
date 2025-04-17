# Pipe Puzzle – JavaFX 4×4 Tile Game

A desktop game where you slide and rotate 4 × 4 tiles to build an unbroken **pipe** from the blue *Starter* to the red *End*. Twenty handcrafted levels ramp up in complexity; a built‑in level editor makes it easy to add your own.

> Course ID: **CSE 1242**  
> Authors: **Ahmet Abdullah Gültekin & Ömer Deliğöz**

---

## 🎮 Gameplay
* Click any **movable** tile to rotate it clockwise 90 °.
* Press **Space** to start water animation; if a continuous path exists, you win.
* Timer & move counter track your performance.
* Pause, Retry and Next‑Level buttons in the sidebar.

<table><tr><td width="290"><img src="src/Images/pipe-horizontal.jpg"></td><td>→</td><td width="290"><img src="src/Images/end-horizontal-left.jpg"></td></tr></table>

---

## 🏗 Project Structure
```
CSE1242-JavaFX-Project/
├── src/application/
│   ├── Main.java          # JavaFX launcher (sets Stage)
│   ├── Controller.java    # Scene switching & UI logic
│   ├── Pipe.java          # Movable tile class
│   ├── PipeStatic.java    # Fixed obstacles
│   ├── Starter.java / End.java / Empty.java
│   └── …
├── src/Images/            # Icons & sprite sheet (16×16 PNGs)
├── src/Levels/Level *.txt # CSV‑style definitions (see below)
└── build.fxbuild          # IntelliJ SceneBuilder descriptor
```

### Level File Format (`Level 7.txt`)
```
<index>,<type>,<orientation>
1,Pipe,11
2,Empty,Free
3,Starter,Vertical
…
16,Empty,none
```
* **index** 1‑16 row‑major on a 4×4 grid.
* **type** ∈ `{Pipe, PipeStatic, Starter, End, Empty}`.
* **orientation**
  * Pipes: `00` = ↖, `01` = ↗, `10` = ↙, `11` = ↘, `Horizontal`, `Vertical`.
  * Starter / End: `Horizontal` or `Vertical` indicating water direction.
  * Empty: `Free` (movable) or `none` (blocked).

---

## 🔧 Build & Run
### Prerequisites
* **JDK 17+**
* **JavaFX SDK 21** (download from openjfx.io)
* IDE: IntelliJ IDEA or Eclipse + e(fx)clipse (SceneBuilder optional)

### IntelliJ
1. *File ▷ Project Structure ▷ Libraries* → add JavaFX `lib` folder.
2. *Run ▷ Edit Configurations* → **VM options**:
   ```
   --module-path "<PATH_TO_FX_LIB>" --add-modules javafx.controls,javafx.fxml
   ```
3. Hit **Run** – main class is `application.Main`.

### Command Line
```bash
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml \
      -d out $(find src -name "*.java")
java  --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml \
      -cp out application.Main
```

---

## ✨ Features
* **20 built‑in levels** loaded from `/src/Levels`.
* **Water animation** travels pipe network each 200 ms.
* **Dark mode** toggle (CSS styles in `Icon.css`).
* **Statistics log** – completion time, moves, date written to `scores.csv`.
* **Credits dialog** with contributor avatars.

---

## 🧩 Extending the Game
* Add 5×5 or 6×6 board size – change grid loops & add assets.
* Implement *drag‑to‑swap* instead of fixed board.
* Load PNG sprite sheet dynamically instead of  individual images.
* Port logic to **Android** using Jetpack Compose Desktop ⇄ Android toolchain.



