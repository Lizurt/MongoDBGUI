package custominput;

import custominput.mdb.parameters.MDBParameterPattern;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static String parseCommand(String rawCommand) {
        for (int i = 0; i < rawCommand.length(); i++) {

        }
        return "SAMPLE TEXT";
    }

    public static List<Object> parseParams(String rawParams, MDBParameterPattern[] parametersPatternGuide) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Object> result = new ArrayList<>();
        StringBuilder sbCurrParam = new StringBuilder();
        for (int rawParCharPos = 0, parPatternGuideIndex = 0; rawParCharPos < rawParams.length(); rawParCharPos++) {
            switch (rawParams.charAt(rawParCharPos)) {
                case ',':
                    result.add(parametersPatternGuide[parPatternGuideIndex]
                            .getContentClass()
                            .getConstructor()
                            .newInstance());
                    break;
                case ' ':
                case '\n':
                case '\r':
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
