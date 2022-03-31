package custominput;

import com.google.gson.Gson;
import custominput.mdb.ChildDelimiter;
import custominput.mdb.commands.MDBCommandPattern;
import custominput.mdb.commands.MDBCommands;
import custominput.mdb.parameters.MDBParameters;
import custominput.mdb.parameters.MDBParametersPattern;
import custominput.mdb.parameters.ParameterScanStartStopDelimiter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private String rawCommand;
    private int currPos = 0;
    private Gson gson = new Gson();

    int wordStartPos = currPos;
    String lastRawCommand = null;
    String currRawCommand = null;
    MDBCommandPattern lastCommand = null;
    MDBCommandPattern currCommand = null;
    boolean currCharIsWordStart = true;
    ChildDelimiter usedChildDelimiter = null;
    ParameterScanStartStopDelimiter usedParameterScanStartDelimiter = null;
    MDBParameters mdbParameters = new MDBParameters();

    public String parseCommand() {
        consumeWhitespaces();
        while (currPos < rawCommand.length()) {
            if (Character.isJavaIdentifierPart(rawCommand.charAt(currPos))) {
                if (currCharIsWordStart && !Character.isJavaIdentifierStart(rawCommand.charAt(currPos))) {
                    return "Identifiers cannot start with such symbol: " + rawCommand.charAt(currPos) + ".";
                }
                currCharIsWordStart = false;
                currPos++;
                continue;
            }
            lastRawCommand = currRawCommand;
            lastCommand = currCommand;
            currRawCommand = rawCommand.substring(wordStartPos, currPos);
            // are we processing a first word in a raw command?
            if (lastCommand == null) {
                // yes so have to check every available command
                // todo: optimisation: cache commands-roots
                for (MDBCommandPattern mdbCommandPattern : MDBCommands.AVAILABLE_MDB_COMMANDS) {
                    if (currRawCommand.equals(mdbCommandPattern.getCommandRaw())) {
                        currCommand = mdbCommandPattern;
                        break;
                    }
                }
            } else {
                // ok we used a correct delimiter and trying to find a matching child command
                String status = tryParseCurrRawCommandAsChild();
                if (!status.isEmpty()) {
                    return status;
                }
            }
            if (currCommand == null) {
                return "Couldn't find such identifier: \"" + currRawCommand + "\".";
            }
            String status = tryParseParams();
            if (!status.isEmpty()) {
                return status;
            }
            // params may end the program, so we gotta check it
            if (currPos >= rawCommand.length()) {
                break;
            }
            usedChildDelimiter = ChildDelimiter.getByChar(rawCommand.charAt(currPos));
            currPos++;
            wordStartPos = currPos;
        }
        if (currCommand == null) {
            return "Couldn't parse such command.";
        }
        mdbParameters.setStep(-1);
        mdbParameters.setCurrParamIndex(mdbParameters.getParameters().size() - 1);
        return currCommand.apply(mdbParameters).toString();
    }

    private String tryParseParams() {
        int shortenedWhitespaces = shortenWhitespaces();
        int consumedWhitespaces = 0;
        // We allow to use spaces before brackets, and at the same time we allow use spaces as parameters
        // scan start characters, so we gotta check if we can eat possible spaces
        if (currCommand.getParameters().getStartStopDelimiter() != ParameterScanStartStopDelimiter.SPACE) {
            consumeWhitespaces();
        }

        usedParameterScanStartDelimiter = ParameterScanStartStopDelimiter.getByStartChar(
                rawCommand.charAt(currPos)
        );
        if (usedParameterScanStartDelimiter == null) {
            // alright, seems we aren't trying to pass params to a command
            return "";
        }
        // have a user typed a wrong delimiter for params?
        if (
                currCommand.getParameters().getStartStopDelimiter().SHOULD_START_WITH
                        != usedParameterScanStartDelimiter.SHOULD_START_WITH
        ) {
            return "Wrong parameter access delimiter: \"" + rawCommand.charAt(currPos) + "\"." +
                    "Expected: \"" + currCommand.getParameters().getStartStopDelimiter().SHOULD_START_WITH
                    + "\".";
        }
        // aight, we have params and they start with a correct delimiter. Let's consume the start delimiter
        currPos++;
        String status = parseParams(currCommand.getParameters(), mdbParameters);
        if (!status.isEmpty()) {
            return status;
        }
        // and we should consume the end delimiter
        currPos++;
        return "";
    }

    private String tryParseCurrRawCommandAsChild() {
        boolean foundMatchingChild = false;
        for (MDBCommandPattern mdbCommandPattern : lastCommand.getChildCommands()) {
            if (currRawCommand.equals(mdbCommandPattern.getCommandRaw())) {
                currCommand = mdbCommandPattern;
                foundMatchingChild = true;
                break;
            }
        }
        if (!foundMatchingChild) {
            if (lastCommand.getChildCommandAsParameter() == null) {
                return "Couldn't find such child identifier or function for: " + currRawCommand + ".";
            }
            currCommand = lastCommand.getChildCommandAsParameter();
            mdbParameters.addParameter(currRawCommand);
        }
        return "";
    }


    private int consumeWhitespaces() {
        int consumed = 0;
        while (currPos < rawCommand.length() && Character.isWhitespace(rawCommand.charAt(currPos))) {
            consumed++;
            currPos++;
        }
        return consumed;
    }

    /**
     * Guarantees that a next character in a raw program won't be whitespace, skips any whitespace before it.
     */
    private int shortenWhitespaces() {
        int shortened = 0;
        boolean foundAtLeastOneWhitespace = false;
        while (currPos < rawCommand.length() && Character.isWhitespace(rawCommand.charAt(currPos))) {
            shortened++;
            currPos++;
            foundAtLeastOneWhitespace = true;
        }
        if (foundAtLeastOneWhitespace) {
            shortened--;
            currPos--;
        }
        return shortened;
    }

    public String parseParams(MDBParametersPattern mdbParametersPattern, MDBParameters mdbParameters) {
        List<String> rawParameters = new ArrayList<>();
        int wordStartPos = currPos;
        boolean endedWithParamEndChar = false;
        while (currPos < rawCommand.length()) {
            if (rawCommand.charAt(currPos) == mdbParametersPattern.getStartStopDelimiter().SHOULD_END_WITH) {
                String command = StringUtils.trim(rawCommand.substring(wordStartPos, currPos));
                if (!command.isEmpty()) {
                    rawParameters.add(command);
                }
                endedWithParamEndChar = true;
                break;
            }
            if (rawCommand.charAt(currPos) == mdbParametersPattern.getParameterDelimiter().DELIMITER) {
                rawParameters.add(StringUtils.trim(rawCommand.substring(wordStartPos, currPos)));
                wordStartPos = currPos;
            }
            currPos++;
        }

        if (!endedWithParamEndChar) {
            rawParameters.add(StringUtils.trim(rawCommand.substring(wordStartPos, currPos)));
        }

        int paramPatternIndex = 0;
        for (String rawParameter : rawParameters) {
            if (paramPatternIndex >= mdbParametersPattern.getParameters().length) {
                return "Too many parameters for a command. Expected: \""
                        + mdbParametersPattern.getParameters().length + "\"";
            }
            Class<?> contentClass = mdbParametersPattern.getParameters()[paramPatternIndex].getContentClass();
            Object param = gson.fromJson(rawParameter, contentClass);
            if (param == null) {
                return "Wrong argument at position: " + paramPatternIndex
                        + ". Expected type: " + contentClass.getSimpleName() + ".";
            }
            mdbParameters.addParameter(param);
            paramPatternIndex++;
        }

        if (paramPatternIndex < mdbParametersPattern.getParameters().length
                && !mdbParametersPattern.getParameters()[paramPatternIndex].isOptional()
        ) {
            Class<?> contentClass = mdbParametersPattern.getParameters()[paramPatternIndex].getContentClass();
            return "Missing required argument. Expected next argument type: " + contentClass.getSimpleName() + ".";
        }

        while (paramPatternIndex < mdbParametersPattern.getParameters().length) {
            paramPatternIndex++;
            mdbParameters.addParameter(null);
        }

        return "";
    }

    public String getRawCommand() {
        return rawCommand;
    }

    public void setRawCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    public int getCurrPos() {
        return currPos;
    }

    public void setCurrPos(int currPos) {
        this.currPos = currPos;
    }
}
