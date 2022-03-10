package custom_input;

import java.util.ArrayList;
import java.util.List;

public class MDBParameters {
    private List<Object> parameters = new ArrayList<>();
    private int currParamIndex;
    private int step;

    public MDBParameters() {
        setStep(1);
        setCurrParamIndex(0);
    }

    public Object useAndGetParameter() {
        Object result = parameters.get(currParamIndex);
        currParamIndex += step;
        return result;
    }

    public void addParameter(Object param) {
        parameters.add(param);
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public int getCurrParamIndex() {
        return currParamIndex;
    }

    private void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public void setCurrParamIndex(int currParamIndex) {
        this.currParamIndex = currParamIndex;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
