package org.eduscript.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eduscript.datastructures.ExecutionPlan;
import org.eduscript.datastructures.Pipeline;
import org.eduscript.datastructures.Stage;
import org.eduscript.datastructures.StageConfigs;
import org.eduscript.datastructures.Trigger;
import org.eduscript.datastructures.TriggerType;

import main.antlr4.EduScriptBaseVisitor;
import main.antlr4.EduScriptParser;
import main.antlr4.EduScriptParser.PipelineContext;
import main.antlr4.EduScriptParser.TrgBlockContext;

public class SemanticAnalyzer extends EduScriptBaseVisitor<Void> {

    private SemanticErrorHandler errorHandler;

    private String pplNm;
    private Map<String, String> envs;
    private Trigger trg;
    private ExecutionPlan plan;

    public SemanticAnalyzer(SemanticErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public SemanticErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public Pipeline exportPipelineObj() {
        return new Pipeline(
                pplNm,
                envs,
                trg,
                plan.build().getExecutionPlan());
    }

    @Override
    public Void visitPipeline(PipelineContext ctx) {
        pplNm = extractValue(ctx.value());
        return visit(ctx.pipelineBody());
    }

    @Override
    public Void visitEnvEntry(EduScriptParser.EnvEntryContext ctx) {
        String k = ctx.ID().getText();
        String v = extractValue(ctx.value());

        if (envs.containsKey(k)) {
            // TODO: error env key already exists
        }

        envs.put(k, v);
        return null;
    }

    @Override
    public Void visitTrgBlock(TrgBlockContext ctx) {
        if (trg != null) {
            // TODO: error can have only one trigger per pipeline
        }

        trg = new Trigger();

        try {
            TriggerType tp = TriggerType.valueOf(ctx.trgType().getText());
            trg.setType(tp);
        } catch (IllegalArgumentException ex) {
            // TODO: error event type doesnt exist
        }

        String tv = extractValue(ctx.trgValue().value());
        trg.setValue(tv);

        return null;
    }

    @Override
    public Void visitStageBlock(EduScriptParser.StageBlockContext ctx) {
        Stage stage = new Stage(extractValue(ctx.value()));

        for (var item : ctx.stageBody().children) {
            if (item instanceof EduScriptParser.ImageBlockContext imageCtx) {
                stage.setImage(extractImage(imageCtx));
            } else if (item instanceof EduScriptParser.RunBlockContext runCtx) {
                stage.addRunCommand(extractRun(runCtx));
            } else if (item instanceof EduScriptParser.NeedsBlockContext needsCtx) {
                if (stage.hasDeps()) {
                    // TODO: error only one need block per stage
                }

                List<String> deps = extractValueList(needsCtx.stringList());
                stage.getDeps().addAll(deps);
            } else if (item instanceof EduScriptParser.ConfigBlockContext configCtx) {
                if (stage.getConfig() != null) {
                    // TODO: error only one config block per stage
                }
                stage.setConfig(extractStageConfigs(configCtx));
            }
        }

        if (!stage.hasAtLeastOneRunCommand()) {
            // TODO: error must have at least one run
        }

        plan.addStage(stage);

        return null;
    }

    private String extractImage(EduScriptParser.ImageBlockContext ctx) {
        if (ctx == null) {
            // TODO: error must have at least one image
        }

        return extractValue(ctx.value());
    }

    private String extractRun(EduScriptParser.RunBlockContext ctx) {
        String runcmd;

        if (ctx.value() != null) {
            runcmd = extractValue(ctx.value());
        } else {
            runcmd = ctx.multiLineString().getText();
        }

        if (runcmd.isBlank()) {
            // TODO: error command must not be blank
        }

        return runcmd;
    }

    private StageConfigs extractStageConfigs(EduScriptParser.ConfigBlockContext ctx) {
        StageConfigs cfgs = new StageConfigs();

        for (var cfg : ctx.configEntry()) {
            // For future use, validate here predefined configs.
            // Custom args will be added in block
            if (cfgs.customArgAlreadyDefined(cfg.ID().getText())) {
                // TODO: error config key already defined
            }

            cfgs.addCustomArg(cfg.ID().getText(), extractValue(cfg.value()));
        }

        return cfgs;
    }

    // TODO: implement variable substitution eg ${variableValue}
    private String extractValue(EduScriptParser.ValueContext parsedval) {
        String envVal;
        if (parsedval.TEXT() != null) {
            envVal = parsedval.TEXT().getText();
        } else {
            envVal = parsedval.ID().getText();
        }
        return envVal;
    }

    private List<String> extractValueList(EduScriptParser.StringListContext parsedlis) {
        List<String> values = new ArrayList<>();
        for (var val : parsedlis.value()) {
            values.add(extractValue(val));
        }

        return values;
    }
}
