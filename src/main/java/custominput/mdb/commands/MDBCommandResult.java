package custominput.mdb.commands;

public class MDBCommandResult {
    private Object result;
    private String humanReadableResult;

    public MDBCommandResult(Object result, String humanReadableResult) {
        this.result = result;
        this.humanReadableResult = humanReadableResult;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getHumanReadableResult() {
        return humanReadableResult;
    }

    public void setHumanReadableResult(String humanReadableResult) {
        this.humanReadableResult = humanReadableResult;
    }
}
