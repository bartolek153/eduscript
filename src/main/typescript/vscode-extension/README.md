# EduScript Syntax Highlighting Extension

**EduScript** is an educational programming language inspired by Python and Pascal. This Visual Studio Code extension provides syntax highlighting for `.edu` source files using a custom TextMate grammar.


---

## 🚀 Features

- Full syntax highlighting for EduScript:
  - Keywords (`programa`, `inicio`, `fim`, etc.)
  - Data types (`inteiro`, `real`, `cadeia`, etc.)
  - Control structures (`se`, `senao`, `para`, `enquanto`, etc.)
  - Literals, operators, punctuation
  - Comments, strings, and numbers

- File recognition for `.edu` files
- Theme-compatible token scopes (TextMate-based)

---

## 📦 Requirements

- Visual Studio Code or Code Server
- Node.js + `vsce` (for packaging locally)

---

## ⚙️ Extension Settings

This extension does not add custom settings yet. Future versions may include:

- Theme customizations
- Linting or diagnostics
- Formatter or snippets

---

## 🐛 Known Issues

- No semantic token support (only regex-based highlighting)
- Does not currently support IntelliSense, autocomplete, or diagnostics

---

## 📓 Release Notes

### 0.0.1

- Initial release
- Adds basic syntax highlighting for EduScript files

---

## 📁 File Support

| Extension | Description              |
|-----------|--------------------------|
| `.edu`    | EduScript source file    |

---

## 🔧 Development & Packaging

To build and package this extension:

```bash
npm install -g @vscode/vsce
vsce package
