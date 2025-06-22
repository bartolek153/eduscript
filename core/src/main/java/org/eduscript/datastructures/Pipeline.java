package org.eduscript.datastructures;

import java.util.List;
import java.util.Map;

public class Pipeline {
    private String name;
    private Map<String, String> envs;
    private Trigger trg;
    private List<Stage> plan;

    public Pipeline(String name, Map<String, String> envs, Trigger trg, List<Stage> plan) {
        this.name = name;
        this.envs = envs;
        this.trg = trg;
        this.plan = plan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getEnvs() {
        return envs;
    }

    public void setEnvs(Map<String, String> envs) {
        this.envs = envs;
    }

    public Trigger getTrg() {
        return trg;
    }

    public void setTrg(Trigger trg) {
        this.trg = trg;
    }

    public List<Stage> getPlan() {
        return plan;
    }

    public void setPlan(List<Stage> plan) {
        this.plan = plan;
    }
}
