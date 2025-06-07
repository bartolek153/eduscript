package org.eduscript;

import org.eduscript.datastructures.EduType;
import org.eduscript.datastructures.VariableSymbol;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataStructuresTest {
    @Test
    public void testVariableGen_unitialized() {
        VariableSymbol vrb = new VariableSymbol("x", EduType.INTEIRO);
        assertEquals(vrb.generateDeclaration(), "%x = alloca i32\n");
    }

    @Test
    public void testVariableGen_initialValue() {
        VariableSymbol vrb = new VariableSymbol("x", EduType.CARACTERE);
        vrb.setInitialValue("3");
        assertEquals(vrb.generateDeclaration(), "%x = alloca i32\nstore i32");
    }
}
