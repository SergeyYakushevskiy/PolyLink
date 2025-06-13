# PolyLink

**PolyLink** is an educational simulator that helps students and enthusiasts explore topics in *coding theory, data compression, and information recovery*. The project provides hands-on experience with **Galois fields** and **Reed-Solomon codes**, enabling users to study encoding, error injection, and decoding processes.

## Features

- Select a Galois field from a list of supported fields.
- Initialize and configure Reed-Solomon codes based on the chosen field.
- Encode and decode text messages using Reed-Solomon codes.
- Manually inject errors into encoded messages and analyze the decoding result.
- Visual highlighting of decoding errors (errors are shown in red in the polynomial form).
- Support for **UTF-8** (original text representation) and **Base64** (encoded data transmission).

If encoding is used incorrectly (e.g., improper decoding attempt), the program will raise clear exceptions.

## Technologies

- **Java 17**
- **JavaFX** for graphical user interface
- **Maven** for project build and dependency management
- **Git** for version control

Core logic is implemented using two custom libraries:

- `galois` — Galois field initialization and operations
- `tiecar` — Reed-Solomon code initialization and operations

## Installation

Clone the repository:

```bash
git clone https://github.com/SergeyYakushevskiy/PolyLink.git
cd PolyLink
```
Build the project using Maven:

```bash
mvn clean package
```
Run the generated JAR:

```bash
java -jar target/polylink.jar
```
