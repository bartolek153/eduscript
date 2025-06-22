package org.eduscript.semantic;

import java.util.ArrayList;
import java.util.HashMap;
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
        this.envs = new HashMap<>();
        this.plan = new ExecutionPlan();
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
            errorHandler.reportError(
                "Environment variable '" + k + "' is already defined",
                "Environment variable redefinition",
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine()
            );
            return null;
        }

        envs.put(k, v);
        return null;
    }

    @Override
    public Void visitTrgBlock(TrgBlockContext ctx) {
        if (trg != null) {
            errorHandler.reportError(
                "Pipeline can have only one trigger block",
                "Multiple trigger blocks defined",
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine()
            );
            return null;
        }

        trg = new Trigger();

        try {
            TriggerType tp = TriggerType.valueOf(ctx.trgType().getText().toUpperCase());
            trg.setType(tp);
        } catch (IllegalArgumentException ex) {
            errorHandler.reportError(
                "Unknown trigger type '" + ctx.trgType().getText() + "'",
                "Invalid trigger type",
                ctx.trgType().getStart().getLine(),
                ctx.trgType().getStart().getCharPositionInLine()
            );
            return null;
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
                    errorHandler.reportError(
                        "Stage can have only one 'needs' block",
                        "Multiple needs blocks in stage '" + stage.getName() + "'",
                        needsCtx.getStart().getLine(),
                        needsCtx.getStart().getCharPositionInLine()
                    );
                    continue;
                }
                List<String> deps = extractValueList(needsCtx.stringList());
                stage.getDeps().addAll(deps);

            } else if (item instanceof EduScriptParser.ConfigBlockContext configCtx) {
                if (stage.getConfig() != null) {
                    errorHandler.reportError(
                        "Stage can have only one 'config' block",
                        "Multiple config blocks in stage '" + stage.getName() + "'",
                        configCtx.getStart().getLine(),
                        configCtx.getStart().getCharPositionInLine()
                    );
                    continue;
                }
                stage.setConfig(extractStageConfigs(configCtx));
            }
        }

        if (!stage.hasAtLeastOneRunCommand()) {
            errorHandler.reportError(
                "Stage must have at least one 'run' command",
                "Missing run commands in stage '" + stage.getName() + "'",
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine()
            );
        }

        plan.addStage(stage);

        return null;
    }

    private String extractImage(EduScriptParser.ImageBlockContext ctx) {
        if (ctx == null) {
            errorHandler.reportError(
                "Stage must have an image block",
                "Missing image specification"
            );
            return "";
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
            errorHandler.reportError(
                "Run command cannot be blank",
                "Empty run command",
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine()
            );
            return "";
        }

        return runcmd;
    }

    private StageConfigs extractStageConfigs(EduScriptParser.ConfigBlockContext ctx) {
        StageConfigs cfgs = new StageConfigs();

        for (var cfg : ctx.configEntry()) {
            // For future use, validate here predefined configs.
            // Custom args will be added in block
            if (cfgs.customArgAlreadyDefined(cfg.ID().getText())) {
                errorHandler.reportError(
                    "Configuration key '" + cfg.ID().getText() + "' is already defined",
                    "Duplicate configuration key",
                    cfg.getStart().getLine(),
                    cfg.getStart().getCharPositionInLine()
                );
                continue;
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
            envVal = unquote(envVal);
        } else {
            envVal = parsedval.ID().getText();
        }
        return envVal;
    }

    private String unquote(String inp) {
        return inp.replaceAll("^\"|\"$", "");
    }

    private List<String> extractValueList(EduScriptParser.StringListContext parsedlis) {
        List<String> values = new ArrayList<>();
        for (var val : parsedlis.value()) {
            values.add(extractValue(val));
        }

        return values;
    }
}
