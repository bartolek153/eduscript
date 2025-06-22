# Step-by-step plan to build your DSL semantic analyzer & executor

3. Build a semantic analyzer visitor

    Resolve uses references (if possible at this stage).

    Accumulate semantic errors or warnings (store them to report).

4. Implement variable scope & substitution logic
Design a scope resolver for variables: env (global) and config (stage-local).

Write code to substitute variables in run scripts, handling ${VAR} patterns.

Validate missing or undefined variables.

5. Implement basic executor skeleton
Define an executor class that takes the semantic model as input.

For each stage, execute steps in order (run, uses).

Stub execution logic (e.g., print commands, simulate execution).

Integrate with your backend services to actually launch jobs, containers, pods.

6. Add error handling & reporting
In your executor, fail fast on semantic errors.

Report detailed, user-friendly errors (line numbers, descriptions).

Support warnings for non-fatal issues.

7. Extend with advanced features
Support nested pipelines or reusable modules.

Add type checks for configs (e.g., integer vs string).

Implement conditional execution or loops (if needed).

Build caching and optimization features.
