package org.eduscript.datastructures;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineTest {
    
    private Pipeline pipeline;
    private String testName;
    private Map<String, String> testEnvs;
    private Trigger testTrigger;
    private List<Stage> testStages;
    
    @Before
    public void setUp() {
        testName = "test-pipeline";
        testEnvs = new HashMap<>();
        testEnvs.put("NODE_ENV", "production");
        testEnvs.put("API_URL", "https://api.example.com");
        
        testTrigger = new Trigger();
        testTrigger.setType(TriggerType.PUSH);
        testTrigger.setValue("main");
        
        Stage stage1 = new Stage("build");
        stage1.setImage("node:18");
        stage1.addRunCommand("npm install");
        stage1.addRunCommand("npm run build");
        
        Stage stage2 = new Stage("test");
        stage2.setImage("node:18");
        stage2.addRunCommand("npm test");
        
        testStages = Arrays.asList(stage1, stage2);
        
        pipeline = new Pipeline(testName, testEnvs, testTrigger, testStages);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(pipeline);
        assertEquals(testName, pipeline.getName());
        assertEquals(testEnvs, pipeline.getEnvs());
        assertEquals(testTrigger, pipeline.getTrg());
        assertEquals(testStages, pipeline.getPlan());
    }
    
    @Test
    public void testGettersAndSetters() {
        // Test name
        String newName = "updated-pipeline";
        pipeline.setName(newName);
        assertEquals(newName, pipeline.getName());
        
        // Test envs
        Map<String, String> newEnvs = new HashMap<>();
        newEnvs.put("DEBUG", "true");
        pipeline.setEnvs(newEnvs);
        assertEquals(newEnvs, pipeline.getEnvs());
        
        // Test trigger
        Trigger newTrigger = new Trigger();
        newTrigger.setType(TriggerType.TAG);
        newTrigger.setValue("v*");
        pipeline.setTrg(newTrigger);
        assertEquals(newTrigger, pipeline.getTrg());
        
        // Test plan
        Stage newStage = new Stage("deploy");
        newStage.setImage("alpine:latest");
        newStage.addRunCommand("echo 'deploying'");
        List<Stage> newPlan = Arrays.asList(newStage);
        pipeline.setPlan(newPlan);
        assertEquals(newPlan, pipeline.getPlan());
    }
    
    @Test
    public void testEmptyEnvironments() {
        Pipeline emptyEnvPipeline = new Pipeline("test", new HashMap<>(), testTrigger, testStages);
        assertTrue(emptyEnvPipeline.getEnvs().isEmpty());
    }
    
    @Test
    public void testNullValues() {
        Pipeline nullPipeline = new Pipeline(null, null, null, null);
        assertNull(nullPipeline.getName());
        assertNull(nullPipeline.getEnvs());
        assertNull(nullPipeline.getTrg());
        assertNull(nullPipeline.getPlan());
    }
}
