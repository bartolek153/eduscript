package org.eduscript.datastructures;

import org.junit.Test;
import org.eduscript.exceptions.NullStageException;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;

public class ExecutionPlanTest {
    
    private ExecutionPlan executionPlan;
    
    @Before
    public void setUp() {
        executionPlan = new ExecutionPlan();
    }
    
    @Test
    public void testExecutionPlan_Constructor() {
        assertNotNull(executionPlan);
        assertNotNull(executionPlan.getExecutionPlan());
        assertTrue(executionPlan.getExecutionPlan().isEmpty());
    }
    
    @Test
    public void testExecutionPlan_AddSingleStage() {
        Stage stage = new Stage("build");
        stage.setImage("node:18");
        stage.addRunCommand("npm install");
        
        executionPlan.addStage(stage);
        
        List<Stage> plan = executionPlan.build().getExecutionPlan();
        assertEquals(1, plan.size());
        assertEquals(stage, plan.get(0));
        assertEquals("build", plan.get(0).getName());
    }
    
    @Test
    public void testExecutionPlan_EmptyExecutionPlan() {
        ExecutionPlan builtPlan = executionPlan.build();
        
        assertNotNull(builtPlan);
        assertTrue(builtPlan.getExecutionPlan().isEmpty());
    }
    
    @Test
    public void testExecutionPlan_AddNullStage() {
        assertThrows(NullStageException.class, () -> {
            executionPlan.addStage(null);
        });
    }
    
    @Test
    public void testExecutionPlan_StageOrder() {
        // Test that stages are added in the correct order
        for (int i = 0; i < 5; i++) {
            Stage stage = new Stage("stage" + i);
            stage.setImage("alpine:latest");
            stage.addRunCommand("echo 'stage" + i + "'");
            executionPlan.addStage(stage);
        }
        
        List<Stage> plan = executionPlan.build().getExecutionPlan();
        assertEquals(5, plan.size());
        
        for (int i = 0; i < 5; i++) {
            assertEquals("stage" + i, plan.get(i).getName());
        }
    }
    
    @Test
    public void testStageWithDependencies() {
        Stage buildStage = new Stage("build");
        buildStage.setImage("node:18");
        buildStage.addRunCommand("npm install");
        
        Stage testStage = new Stage("test");
        testStage.setImage("node:18");
        testStage.addRunCommand("npm test");
        testStage.getDeps().add("build");
        
        Stage deployStage = new Stage("deploy");
        deployStage.setImage("alpine:latest");
        deployStage.addRunCommand("echo 'deploying'");
        deployStage.getDeps().add("build");
        deployStage.getDeps().add("test");
        
        executionPlan.addStage(buildStage);
        executionPlan.addStage(testStage);
        executionPlan.addStage(deployStage);
        
        List<Stage> plan = executionPlan.build().getExecutionPlan();
        assertEquals(3, plan.size());
        
        // Verify dependencies are preserved
        assertTrue(plan.get(1).hasDeps());
        assertEquals(1, plan.get(1).getDeps().size());
        assertEquals("build", plan.get(1).getDeps().get(0));
        
        assertTrue(plan.get(2).hasDeps());
        assertEquals(2, plan.get(2).getDeps().size());
        assertTrue(plan.get(2).getDeps().contains("build"));
        assertTrue(plan.get(2).getDeps().contains("test"));
    }
}
