package custom_input;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MDBCommandPattern implements Function<MDBParameters, Object> {
    private MDBCommandPattern parent;

    private Class<?> returnValueClass;

    private String commandRaw;

    private MDBParametersPattern parameters;
    public static final String COMMAND_AS_PARAMETER = "";

    private char childCommandsAccessDelimiter;
    public static final char IGNORED_CHILD_COMMANDS_ACCESS_DELIMITER = '\0';
    private List<MDBCommandPattern> childCommands = new ArrayList<>();

    public MDBCommandPattern(
            Class<?> returnValueClass,
            String commandRaw,
            MDBParametersPattern parameters,
            char childCommandsAccessDelimiter
    ) {
        setReturnValueClass(returnValueClass);
        setCommandRaw(commandRaw);
        setParameters(parameters);
        setChildCommandsAccessDelimiter(childCommandsAccessDelimiter);
    }

    public void addAvailableChildCommand(MDBCommandPattern childCmd) {
        if (childCmd.getParent() != null) {
            throw new IllegalArgumentException(MDBCommandPattern.class.getSimpleName()
                    + " cannot have more than one parent.");
        }
        childCmd.setParent(this);
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

    public char getChildCommandsAccessDelimiter() {
        return childCommandsAccessDelimiter;
    }

    public List<MDBCommandPattern> getChildCommands() {
        return childCommands;
    }

    private void setParent(MDBCommandPattern parent) {
        if (getParent() != null) {
            getParent().getChildCommands().removeIf(siblingCmd -> siblingCmd == this);
        }
        this.parent = parent;
        if (parent == null) {
            return;
        }
        parent.addAvailableChildCommand(this);
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

    private void setChildCommandsAccessDelimiter(char childCommandsAccessDelimiter) {
        this.childCommandsAccessDelimiter = childCommandsAccessDelimiter;
    }

    private void setChildCommands(List<MDBCommandPattern> childCommands) {
        this.childCommands = childCommands;
    }
}
