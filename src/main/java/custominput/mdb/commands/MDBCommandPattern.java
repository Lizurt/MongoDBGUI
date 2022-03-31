package custominput.mdb.commands;

import custominput.mdb.ChildDelimiter;
import custominput.mdb.parameters.MDBParametersPattern;
import custominput.mdb.parameters.MDBParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MDBCommandPattern implements Function<MDBParameters, Object> {
    private MDBCommandPattern parent;

    private Class<?> returnValueClass;

    private String commandRaw;

    private MDBParametersPattern parameters;
    public static final String COMMAND_AS_PARAMETER = "";

    private ChildDelimiter childCommandsAccessDelimiter;
    private List<MDBCommandPattern> childCommands = new ArrayList<>();

    // caching to decrease the search complexity
    private MDBCommandPattern childCommandAsParameter;

    public MDBCommandPattern(
            Class<?> returnValueClass,
            String commandRaw,
            MDBParametersPattern parameters,
            ChildDelimiter childCommandsAccessDelimiter
    ) {
        setReturnValueClass(returnValueClass);
        setCommandRaw(commandRaw);
        setParameters(parameters);
        setChildCommandsAccessDelimiter(childCommandsAccessDelimiter);

        if (parameters.getStartStopDelimiter().SHOULD_START_WITH == childCommandsAccessDelimiter.DELIMITER) {
            throw new IllegalArgumentException("Params and child delimiters clash.");
        }
    }

    public MDBCommandPattern getParent() {
        return parent;
    }

    public Class<?> getReturnValueClass() {
        return returnValueClass;
    }

    public String getCommandRaw() {
        return commandRaw;
    }

    public MDBParametersPattern getParameters() {
        return parameters;
    }

    public ChildDelimiter getChildCommandsAccessDelimiter() {
        return childCommandsAccessDelimiter;
    }

    public List<MDBCommandPattern> getChildCommands() {
        return childCommands;
    }

    public void setParent(MDBCommandPattern parent) {
        if (getParent() != null) {
            getParent().getChildCommands().removeIf(siblingCmd -> siblingCmd == this);
        }
        this.parent = parent;
        if (parent == null) {
            return;
        }
        if (getCommandRaw().equals(COMMAND_AS_PARAMETER)) {
            if (getChildCommandAsParameter() != null) {
                throw new IllegalArgumentException(MDBCommandPattern.class.getSimpleName()
                        + " cannot have more than one command-as-parameter child."
                );
            }
            parent.setChildCommandAsParameter(this);
        }
        parent.getChildCommands().add(this);
    }

    private void setReturnValueClass(Class<?> returnValueClass) {
        this.returnValueClass = returnValueClass;
    }

    private void setCommandRaw(String commandRaw) {
        this.commandRaw = commandRaw;
    }

    private void setParameters(MDBParametersPattern parameters) {
        this.parameters = parameters;
    }

    private void setChildCommandsAccessDelimiter(ChildDelimiter childCommandsAccessDelimiter) {
        this.childCommandsAccessDelimiter = childCommandsAccessDelimiter;
    }

    private void setChildCommands(List<MDBCommandPattern> childCommands) {
        this.childCommands = childCommands;
    }

    public MDBCommandPattern getChildCommandAsParameter() {
        return childCommandAsParameter;
    }

    private void setChildCommandAsParameter(MDBCommandPattern childCommandAsParameter) {
        this.childCommandAsParameter = childCommandAsParameter;
    }
}
