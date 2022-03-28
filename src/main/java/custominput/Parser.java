package custominput;

import com.google.gson.Gson;
import custominput.mdb.Delimiter;
import custominput.mdb.commands.MDBCommandPattern;
import custominput.mdb.commands.MDBCommands;
import custominput.mdb.parameters.MDBParameters;
import custominput.mdb.parameters.MDBParametersPattern;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private String rawCommand;
    private int currPos;
    private Gson gson = new Gson();

    public String parseCommand() {
        consumeWhitespaces();
        int wordStartPos = currPos;
        String lastWord = null;
        String currWord = null;
        MDBCommandPattern lastKeyword = null;
        MDBCommandPattern currKeyword = null;
        boolean currCharIsWordStart = true;
        Delimiter usedDelimiter = null;
        MDBParameters mdbParameters = new MDBParameters();
        while (currPos < rawCommand.length()) {
            if (Character.isJavaIdentifierPart(rawCommand.charAt(currPos))) {
                if (currCharIsWordStart && !Character.isJavaIdentifierStart(rawCommand.charAt(currPos))) {
                    return "Identifiers cannot start with such symbol: " + rawCommand.charAt(currPos) + ".";
                }
                currCharIsWordStart = false;
                currPos++;
                continue;
            }
            lastWord = currWord;
            lastKeyword = currKeyword;
            currWord = rawCommand.substring(wordStartPos, currPos);
            // are we processing a first word in a raw command?
            if (lastKeyword == null) {
                // yes so have to check every available command
                // todo: optimisation: cache commands-roots
                for (MDBCommandPattern mdbCommandPattern : MDBCommands.AVAILABLE_MDB_COMMANDS) {
                    if (currWord.equals(mdbCommandPattern.getCommandRaw())) {
                        currKeyword = mdbCommandPattern;
                        break;
                    }
                }
            } else {
                // no, not a first word, but what if we used a wrong delimiter? So we assume we typed a parameter
                if (lastKeyword.getChildCommandsAccessDelimiter() != usedDelimiter) {
                    // does a previous keyword even have params?
                    if (lastKeyword.getParameters() == MDBParametersPattern.NO_PARAMS) {
                        return lastWord + " cannot have parameters. Met parameter: " + currWord + ".";
                    }
                    // have a user typed a wrong delimiter even for params?
                    if (usedDelimiter == null
                            || lastKeyword.getParameters().getParameterSearchPlace().parametersShouldStartWith
                            != usedDelimiter.delimiterChar
                    ) {
                        return lastWord + "'s parameters list should start with \""
                                + lastKeyword.getParameters().getParameterSearchPlace().parametersShouldStartWith + "\"";
                    }
                    // aight, we have params and they start with a correct delimiter.
                    // Let parseParams() handle our currPos
                    currPos -= currWord.length();
                    String status = parseParams(lastKeyword.getParameters(), mdbParameters);
                    if (!status.isEmpty()) {
                        return status;
                    }
                    continue;
                }
                // ok we used a correct delimiter and trying to find a matching child command
                boolean foundMatchingChild = false;
                for (MDBCommandPattern mdbCommandPattern : lastKeyword.getChildCommands()) {
                    if (currWord.equals(mdbCommandPattern.getCommandRaw())) {
                        currKeyword = mdbCommandPattern;
                        foundMatchingChild = true;
                        break;
                    }
                }
                if (!foundMatchingChild) {
                    if (lastKeyword.getChildCommandAsParameter() == null) {
                        return "Couldn't find such child identifier or function for: " + currWord + ".";
                    }
                    currKeyword = lastKeyword.getChildCommandAsParameter();
                    mdbParameters.addParameter(currWord);
                }
            }
            shortenWhitespaces();
            usedDelimiter = Delimiter.getByChar(rawCommand.charAt(currPos));
            currPos++;
            wordStartPos = currPos;
        }
        if (currKeyword == null) {
            return "Couldn't parse such command.";
        }
        return currKeyword.apply(mdbParameters).toString();
    }

    private void consumeWhitespaces() {
        while (currPos < rawCommand.length() && Character.isWhitespace(rawCommand.charAt(currPos))) {
            currPos++;
        }
    }

    /**
     * Guarantees that a next character in a raw program won't be whitespace, skips any whitespace before it.
     */
    private void shortenWhitespaces() {
        boolean foundAtLeastOneWhitespace = false;
        while (currPos < rawCommand.length() && Character.isWhitespace(rawCommand.charAt(currPos))) {
            currPos++;
            foundAtLeastOneWhitespace = true;
        }
        if (foundAtLeastOneWhitespace) {
            currPos--;
        }
    }

    public String parseParams(MDBParametersPattern mdbParametersPattern, MDBParameters mdbParameters) {
        List<String> rawParameters = new ArrayList<>();
        consumeWhitespaces();
        int wordStartPos = currPos;
        boolean endedWithParamEndChar = false;
        while (currPos < rawCommand.length()) {
            if (rawCommand.charAt(currPos) == mdbParametersPattern.getParameterSearchPlace().parametersShouldEndWith) {
                rawParameters.add(StringUtils.trim(rawCommand.substring(wordStartPos, currPos)));
                currPos++;
                endedWithParamEndChar = true;
                break;
            }
            if (rawCommand.charAt(currPos) == mdbParametersPattern.getDelimiter().delimiterChar) {
                rawParameters.add(StringUtils.trim(rawCommand.substring(wordStartPos, currPos)));
                wordStartPos = currPos;
                continue;
            }
            currPos++;
        }

        if (!endedWithParamEndChar) {
            rawParameters.add(StringUtils.trim(rawCommand.substring(wordStartPos, currPos)));
        }

        int paramPatternIndex = 0;
        for (String rawParameter : rawParameters) {
            Class<?> contentClass = mdbParametersPattern.getParameters()[paramPatternIndex].getContentClass();
            Object param = gson.fromJson(rawParameter, contentClass);
            paramPatternIndex++;
            if (param == null) {
                return "Wrong argument at position: " + paramPatternIndex
                        + ". Expected type: " + contentClass.getSimpleName() + ".";
            }
            mdbParameters.addParameter(param);
        }

        paramPatternIndex++;
        if (paramPatternIndex < mdbParametersPattern.getParameters().length
                && !mdbParametersPattern.getParameters()[paramPatternIndex].isOptional()
        ) {
            Class<?> contentClass = mdbParametersPattern.getParameters()[paramPatternIndex].getContentClass();
            return "Missing required argument. Expected next argument type: " + contentClass.getSimpleName() + ".";
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
