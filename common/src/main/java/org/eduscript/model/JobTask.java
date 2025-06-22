package org.eduscript.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobTask {
    private String name;
    private String image;
    private List<String> runCommands = new ArrayList<>();
    private Map<String, String> args = new HashMap<>();

    public JobTask() {
    }

    public JobTask(String name, String image, List<String> runCommands, Map<String, String> args) {
        this.name = name;
        this.image = image;
        this.runCommands = runCommands;
        this.args = args;
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

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
