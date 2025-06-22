package org.eduscript.datastructures;

import java.util.ArrayList;
import java.util.List;

public class Stage {
    private String name;
    private String image;
    private List<String> runCommands = new ArrayList<>();
    private StageConfigs config;
    private List<String> deps = new ArrayList<>();

    public Stage(String name) {
        this.name = name;
        this.config = new StageConfigs();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getRunCommands() {
        return runCommands;
    }

    public void setRunCommands(List<String> runCommands) {
        this.runCommands = runCommands;
    }

    public void addRunCommand(String runCommand) {
        this.runCommands.add(runCommand);
    }

    public Boolean hasAtLeastOneRunCommand() {
        return this.runCommands.size() > 0;
    }

    public StageConfigs getConfig() {
        return config;
    }

    public void setConfig(StageConfigs config) {
        this.config = config;
    }

    public List<String> getDeps() {
        return deps;
    }

    public void setDeps(List<String> needs) {
        this.deps = needs;
    }

    public Boolean hasDeps() {
        return this.deps.size() > 0;
    }
}
