package custom_input;

public class MDBParametersPattern {
    private MDBParameterPattern[] parameters;
    private ParameterSearchPlace parameterSearchPlace;

    public static final MDBParametersPattern NO_PARAMS = new MDBParametersPattern(ParameterSearchPlace.NOWHERE);

    public MDBParametersPattern(ParameterSearchPlace parameterSearchPlace, MDBParameterPattern... parameters) {
        setParameterSearchPlace(parameterSearchPlace);
        setParameters(parameters);
    }

    public MDBParameterPattern[] getParameters() {
        return parameters;
    }

    public ParameterSearchPlace getParameterSearchPlace() {
        return parameterSearchPlace;
    }

    private void setParameters(MDBParameterPattern[] parameters) {
        this.parameters = parameters;
    }

    private void setParameterSearchPlace(ParameterSearchPlace parameterSearchPlace) {
        this.parameterSearchPlace = parameterSearchPlace;
    }
}
