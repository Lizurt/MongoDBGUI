package custominput.mdb.parameters;

import custominput.mdb.ChildDelimiter;

public class MDBParametersPattern {
    private MDBParameterPattern[] parameters;
    private ParameterSearchPlace parameterSearchPlace;
    private ParameterScanStartStopDelimiter startStopDelimiter;
    private ParameterDelimiter parameterDelimiter;

    public static final MDBParametersPattern NO_PARAMS = new MDBParametersPattern(
            ParameterSearchPlace.NOWHERE,
            ParameterScanStartStopDelimiter.NOTHING,
            ParameterDelimiter.NOTHING
    );

    public MDBParametersPattern(
            ParameterSearchPlace parameterSearchPlace,
            ParameterScanStartStopDelimiter startStopDelimiter,
            ParameterDelimiter parameterDelimiter,
            MDBParameterPattern... parameters
    ) {
        setParameterSearchPlace(parameterSearchPlace);
        setStartStopDelimiter(startStopDelimiter);
        setParameterDelimiter(parameterDelimiter);
        setParameters(parameters);

        boolean nonOptionalParamsEnded = false;
        for (MDBParameterPattern mdbParameterPattern : getParameters()) {
            if (mdbParameterPattern.isOptional()) {
                nonOptionalParamsEnded = true;
                continue;
            }
            if (nonOptionalParamsEnded && !mdbParameterPattern.isOptional()) {
                throw new IllegalArgumentException("MDBParameter non-optional parameters can't be after optional ones.");
            }
        }
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

    public ParameterDelimiter getParameterDelimiter() {
        return parameterDelimiter;
    }

    public void setParameterDelimiter(ParameterDelimiter parameterDelimiter) {
        this.parameterDelimiter = parameterDelimiter;
    }

    public ParameterScanStartStopDelimiter getStartStopDelimiter() {
        return startStopDelimiter;
    }

    public void setStartStopDelimiter(ParameterScanStartStopDelimiter startStopDelimiter) {
        this.startStopDelimiter = startStopDelimiter;
    }
}
