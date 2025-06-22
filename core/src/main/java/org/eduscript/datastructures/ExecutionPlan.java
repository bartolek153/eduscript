package org.eduscript.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ExecutionPlan {
    private final Map<String, Stage> stages = new HashMap<>();
    private final Map<String, List<String>> edges = new HashMap<>();
    private final Map<String, Integer> inDegrees = new HashMap<>();

    public void addStage(Stage stage) {
        String name = stage.getName();
        if (stages.containsKey(name)) {
            // TODO: error duplicate stage
        }
        stages.put(name, stage);
    }

    public ExecutionPlan build() {
        for (String name : stages.keySet()) {
            edges.putIfAbsent(name, new ArrayList<>());
            inDegrees.putIfAbsent(name, 0);
        }

        for (Stage stage : stages.values()) {
            for (String dep : stage.getDeps()) {
                if (!stages.containsKey(dep)) {
                    // TODO: error unknown dep
                }
                edges.get(dep).add(stage.getName()); // dep → this stage
                inDegrees.put(stage.getName(), inDegrees.get(stage.getName()) + 1);
            }
        }

        return this;
    }

    public List<Stage> getExecutionPlan() {
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Stage> plan = new ArrayList<>();
        int processed = 0;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            plan.add(stages.get(current));
            processed++;

            for (String neighbor : edges.getOrDefault(current, List.of())) {
                inDegrees.put(neighbor, inDegrees.get(neighbor) - 1);
                if (inDegrees.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (processed != stages.size()) {
            // TODO: error cycle detected
        }

        return plan;
    }
}
