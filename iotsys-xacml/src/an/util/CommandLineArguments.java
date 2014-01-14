package an.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

/**
 * A command line arguments parsing tool. Can set required and optional arguments. To use it, first construct it by 
 * calling constructors, then call parse method to parsing command line. Argument's token is case-insensitive, but the
 * value is case-sensitive.
 */
public class CommandLineArguments {
    private Collection<String> reqTokens;
    private Collection<String> optTokens;
    /**
     * Whether or not allow undefined arguments exist in command line. True is allow, false is disallow. Default is 
     * true.
     */
    private boolean allowInvalidArgs;
    private Map<String, Argument> arguments = new HashMap<String, Argument>();
    /**
     * All tokens should start with "-". For example "java someclass -config c:\work\config.xml"
     */
    public static final String TOKEN_PREFIX_PATTERN = "(\\s-)|(^-)";

    /**
     * Construct with array type required and optional tokens.
     * @param requiredTokens
     * @param optionalTokens
     */
    public CommandLineArguments(String[] requiredTokens, String[] optionalTokens) {
        this(requiredTokens, optionalTokens, true);
    }

    public CommandLineArguments(String[] requiredTokens, String[] optionalTokens, boolean allowInvalid) {
        reqTokens = new HashSet<String>();
        if (requiredTokens != null) {
            for (int i = 0; i < requiredTokens.length; i ++) {
                // For case-insensitive
                reqTokens.add(requiredTokens[i].toLowerCase());
            }
        }

        optTokens = new HashSet<String>();
        if (optionalTokens != null) {
            for (int i = 0; i < optionalTokens.length; i ++) {
                // For case-insensitive
                optTokens.add(optionalTokens[i].toLowerCase());
            }
        }
        allowInvalidArgs = allowInvalid;
    }

    /**
     * Construct with Collection type required and optional tokens.
     * @param requiredTokens
     * @param optionalTokens
     */
    public CommandLineArguments(Collection<String> requiredTokens, Collection<String> optionalTokens) {
        this(requiredTokens, optionalTokens, true);
    }

    public CommandLineArguments(
            Collection<String> requiredTokens, Collection<String> optionalTokens, boolean allowInvalid) {
        reqTokens = new HashSet<String>();
        if (requiredTokens != null) {
            Iterator<String> iReq = requiredTokens.iterator();
            while (iReq.hasNext()) {
                // For case-insensitive
                reqTokens.add(iReq.next().toLowerCase());
            }
        }

        optTokens = new HashSet<String>();
        if (optionalTokens != null) {
            Iterator<String> iOpt = optionalTokens.iterator();
            while (iOpt.hasNext()) {
                // For case-insensitive
                optTokens.add(iOpt.next().toLowerCase());
            }
        }

        allowInvalidArgs = allowInvalid;
    }

    /**
     * Parsing the command line, and add the parsed arguments to a Map. If there are invalid arguments and we don't 
     * allow them exist, an InvalidCommandLineArgumentException will be thrown. If not all required arguments are 
     * present, a MissingRequiredArgumentsException will be thrown.
     * @param commandLine
     * @throws InvalidCommandLineArgumentException
     * @throws MissingRequiredArgumentsException
     */
    public void parse(String commandLine) throws InvalidCommandLineArgumentException, MissingRequiredArgumentsException {
        String[] argsByToken = trimStringArray(commandLine.split(TOKEN_PREFIX_PATTERN));

        for (int i = 0; i < argsByToken.length; i ++) {
            String tokenLine = argsByToken[i].trim();
            String[] tokenWithValue = trimStringArray(tokenLine.split("[ \t\n\r\f]"));

            String token = tokenWithValue[0].toLowerCase();
            if (!reqTokens.contains(token) && !optTokens.contains(token)) {
                if (!allowInvalidArgs) {
                    throw new InvalidCommandLineArgumentException("Invalid command line argument : " + token);
                }
                else {
                    continue;
                }
            }

            if (!allowInvalidArgs && tokenWithValue.length > 2) {
                throw new InvalidCommandLineArgumentException("Invalid argument value, token : " + token);
            }

            String value = (tokenWithValue.length > 1 ? tokenWithValue[1] : null);
            boolean isRequired = reqTokens.contains(token);
            arguments.put(token, new Argument(token, value, isRequired));
        }

        // Check if all required arguments exist.
        if (!arguments.keySet().containsAll(reqTokens)) {
            throw new MissingRequiredArgumentsException("Required arguments are absent.");
        }
    }

    public void parse(String[] args) throws InvalidCommandLineArgumentException, MissingRequiredArgumentsException {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < args.length; i ++) {
            strBuf.append(args[i]);
            strBuf.append(" ");
        }
        parse(strBuf.toString());
    }

    public Argument getArgumentByToken(String token) {
        return arguments.get(token.toLowerCase());
    }

    public String getArgumentValueByToken(String token) {
        Argument arg = arguments.get(token.toLowerCase());
        return arg == null ? null : arg.getValue();
    }

    public Iterator<Argument> getArguments() {
        return arguments.values().iterator();
    }

    private String[] trimStringArray(String[] array) {
        Vector<String> result = new Vector<String>();
        for (int i = 0; i < array.length; i ++) {
            if (!array[i].equals("")) {
                result.add(array[i]);
            }
        }
        return result.toArray(new String[0]);
    }
}