package org.eduscript.exec;

public abstract class Runner {
    protected abstract String compile(String dir);

    protected abstract void run(String executable);

    public void invoke(String dir) {
        String executable = compile(dir);
        run(executable);
    }
}
