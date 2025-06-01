# EduScript Code Generator Implementation

## Overview
I've implemented a complete code generation system for the EduScript programming language that translates EduScript programs into C code. The implementation includes:

1. **Stack-based block management** for handling nested scopes
2. **Command pattern** for representing different code constructs
3. **Two-pass compilation**: semantic analysis followed by code generation

## Architecture

### Core Components

#### 1. Command Classes (in `org.eduscript.codegen`)
- **BaseCommand**: Abstract base class for all commands
- **ProgramCommand**: Represents the entire program structure
- **BlockCommand**: Manages code blocks with proper indentation
- **AssignmentCommand**: Handles variable assignments
- **WriteCommand**: Generates printf statements
- **ReadCommand**: Generates scanf statements
- **ConditionalCommand**: Handles if-else statements
- **WhileCommand**: Generates while loops
- **ForCommand**: Generates for loops

#### 2. Enhanced Symbol Classes
- Modified `Symbol` to be abstract with `generateDeclaration()` method
- Updated `VariableSymbol`, `ArraySymbol`, and `FunctionSymbol` to generate C declarations

#### 3. CodeGenerator Class
- Extends `SemanticAnalyzer` to leverage existing AST traversal
- Uses a **stack** (`blockStack`) to manage nested blocks
- Tracks indentation levels for proper code formatting
- Implements visitor methods for each language construct

### Key Features

#### Stack Logic for Blocks
```java
private Stack<BlockCommand> blockStack;
```
- Push new `BlockCommand` when entering a block (main, if, while, for)
- Add statements to the current block (top of stack)
- Pop when exiting a block
- Maintains proper nesting and scope

#### Type Conversion
EduScript types are mapped to C types:
- `inteiro` → `int`
- `real` → `float`
- `logico` → `bool`
- `caractere` → `char`
- `cadeia` → `char*`

#### Expression Generation
The `generateExpression()` method recursively builds C expressions:
- Handles operators (arithmetic, logical, relational)
- Converts EduScript operators (`e`, `ou`, `nao`) to C operators (`&&`, `||`, `!`)
- Supports array access and function calls

## Usage

### Command Line
```bash
# Compile and run with input file
mvn exec:java -Dexec.mainClass="org.eduscript.App" -Dexec.args="input.edu output.c"

# Or use the test script
./test_codegen.sh
```

### Example Translation

EduScript:
```eduscript
programa Example;
var x: inteiro;
inicio
    x = 10;
    escrever("Value: ", x);
fimprograma
```

Generated C:
```c
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

int x;

int main() {
    x = 10;
    printf("Value: %d\n", x);
    return 0;
}
```

## Implementation Details

### Block Stack Management
1. When entering a new block (e.g., `visitMainBlock`, `visitConditional`):
   - Create new `BlockCommand`
   - Push onto stack
   - Increment indent level

2. When processing statements:
   - Add commands to current block (`blockStack.peek()`)

3. When exiting a block:
   - Pop from stack
   - Decrement indent level
   - Attach block to parent structure

### Two-Pass Compilation
1. **First Pass**: Semantic analysis builds symbol table
2. **Second Pass**: Code generation traverses AST and generates C code

## Limitations and Future Improvements

1. **Type Information**: Currently uses simplified type handling for printf/scanf
2. **String Handling**: Basic string support, could be enhanced
3. **Function Definitions**: Framework is in place but needs full implementation
4. **Error Recovery**: Could be improved for better error messages
5. **Optimization**: No optimization passes currently implemented

## Testing

Test programs are provided in the `examples/` directory:
- `simple_test.edu`: Basic variable operations and output
- `test_program.edu`: Comprehensive test with loops, conditionals, and arrays

The generated C code includes all necessary headers and follows standard C conventions.
