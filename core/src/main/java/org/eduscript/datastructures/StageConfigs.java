package org.eduscript.datastructures;

import java.util.HashMap;
import java.util.Map;

public class StageConfigs {
    private Map<String, String> customArgs = new HashMap<>();

    public StageConfigs() {
    }

    public Map<String, String> getCustomArgs() {
        return customArgs;
    }

    public void setCustomArgs(Map<String, String> customArgs) {
        this.customArgs = customArgs;
    }

    public void addCustomArg(String k, String v) {
        customArgs.put(k, v);
    }

    public Boolean customArgAlreadyDefined(String k) {
        return customArgs.containsKey(k);
    }
}
