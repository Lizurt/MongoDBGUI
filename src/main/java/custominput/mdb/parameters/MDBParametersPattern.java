package custominput.mdb.parameters;

import custominput.mdb.Delimiter;

public class MDBParametersPattern {
    private MDBParameterPattern[] parameters;
    private ParameterSearchPlace parameterSearchPlace;
    private Delimiter delimiter;

    public static final MDBParametersPattern NO_PARAMS = new MDBParametersPattern(
            ParameterSearchPlace.NOWHERE,
            Delimiter.IGNORED_DELIMITER
    );

    public MDBParametersPattern(
            ParameterSearchPlace parameterSearchPlace,
            Delimiter delimiter,
            MDBParameterPattern... parameters
    ) {
        setParameterSearchPlace(parameterSearchPlace);
        setParameters(parameters);
        setDelimiter(delimiter);

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

    public Delimiter getDelimiter() {
        return delimiter;
    }

    private void setDelimiter(Delimiter delimiter) {
        this.delimiter = delimiter;
    }
}
