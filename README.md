# 🧩 MatrixLogic: Binary Puzzle Console Game

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![Terminal](https://img.shields.io/badge/Interface-CLI-black?style=for-the-badge)
  ![Data Structures](https://img.shields.io/badge/Algorithms-Data_Structures-success?style=for-the-badge)
  ![Academic](https://img.shields.io/badge/Academic_Project-1st_Year-purple?style=for-the-badge)

</div>

<br>

## 📖 About the Project

**MatrixLogic** is a terminal-based logic puzzle game (widely known as Takuzu or Binary Puzzle). This project was developed as an academic practice during the 1st Year of the Computer Engineering degree.

The game challenges the player to fill a dynamic grid using only `'X'` and `'O'` tokens, adhering to a strict set of logical constraints. It features multiple randomized boards parsed from a local text database and a robust *Undo* system.

### 🎮 Gameplay Rules
To successfully solve a board, the following conditions must be met:
1. **Immutable Seed:** Initial uppercase tokens (`X` and `O`) cannot be modified.
2. **Equilibrium:** Each row and each column must contain the exact same number of `X`s and `O`s.
3. **No Triples:** No more than two identical elements can be placed consecutively in any row or column.
4. **Uniqueness:** No two rows can be identical. No two columns can be identical.

---

## 🛠️ Tech Stack & Technical Features

- **Language:** Java 8+
- **Interface:** Command-Line Interface (CLI)

### ✨ Key Implementation Details
* **Stack-Based History (Undo System):** Leverages `java.util.Stack` (LIFO) to store deep copies of board states, allowing the user to seamlessly revert their moves dynamically step-by-step to the very beginning.
* **File Parsing & I/O Optimization:** Uses `BufferedReader` wrapped in a `try-with-resources` block to safely and efficiently read level layouts dynamically from a `.txt` database.
* **Matrix Validation Algorithms:** Implements 2D array traversal algorithms to validate the grid against complex geometric rules (row/column equivalence and contiguous character counting).
* **Defensive Input Parsing:** Robust console input validation with dynamic coordinate boundaries parsing (e.g., handling boundaries for inputs like `1A` or `12D`).

---

## ⚙️ How to Run Locally

*Note: The game requires the `tableros.txt` file to be located in the root directory alongside the executable to load the levels properly.*

**1. Clone the repository:**
```bash
git clone [https://github.com/Ivanlaguna511/MatrixLogic.git](https://github.com/Ivanlaguna511/MatrixLogic.git)
cd MatrixLogic
