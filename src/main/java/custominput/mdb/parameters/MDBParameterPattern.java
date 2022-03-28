package custominput.mdb.parameters;

public class MDBParameterPattern {
    private Class<?> contentClass;
    private boolean isOptional;

    public MDBParameterPattern(Class<?> contentClass, boolean isOptional) {
        this.contentClass = contentClass;
        this.isOptional = isOptional;
    }

    public Class<?> getContentClass() {
        return contentClass;
    }

    public boolean isOptional() {
        return isOptional;
    }
}
