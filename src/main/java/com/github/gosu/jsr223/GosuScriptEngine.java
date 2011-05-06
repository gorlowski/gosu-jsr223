/*
 * Copyright 2011 Greg Orlowski
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gosu.jsr223;

import gw.lang.parser.GosuParserFactory;
import gw.lang.parser.IGosuProgramParser;
import gw.lang.parser.IParseResult;
import gw.lang.parser.ISymbol;
import gw.lang.parser.ISymbolTable;
import gw.lang.parser.ParserOptions;
import gw.lang.parser.StandardSymbolTable;
import gw.lang.parser.ThreadSafeSymbolTable;
import gw.lang.parser.exceptions.ParseResultsException;
import gw.lang.reflect.gs.IGosuProgram;
import gw.lang.shell.Gosu;

import java.io.IOException;
import java.io.Reader;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * A partial implementation of a JSR 223 {@link ScriptEngine} for the Gosu language. This will work
 * if you do not need to execute Gosu scripts in a predefined ScriptContext. Passing the state of
 * {@link ScriptContext} to Gosu's {@link ISymbolTable} is not transparent because the Gosu parser
 * and {@link ISymbol} implementations are closed-source (gw.internal package).
 * 
 * Because complete implementation of a Gosu {@link ScriptEngine} runs into a licensing roadblock,
 * I'm not going to work out an optimal threading policy for this. I don't know how Gosu manages
 * state in its {@link StandardSymbolTable} implementation between invocations of a common
 * {@link IGosuProgramParser}. You can probably achieve a JSR 223 THREAD-ISOLATED policy if you
 * correctly extend and use {@link ThreadSafeSymbolTable} in this class.
 * 
 * @author Greg Orlowski
 */
public class GosuScriptEngine extends AbstractScriptEngine implements ScriptEngine {

    private boolean initialized = false;

    public GosuScriptEngine() {
        super();
    }

    private synchronized void init() {
        if (!initialized) {
            Gosu.init();
            initialized = true;
        }
    }

    /**
     * @param script
     *            A String representation of the script that you want to execute
     * @param scriptContext
     *            not used because manipulation of Gosu's symbol table is veiled behind some closed
     *            source code
     * @return the return value of the script
     */
    private Object parseAndExecute(String script, ScriptContext scriptContext) {
        init();
        Object ret = null;
        try {
            // splitting to multiple lines to ease stack inspection
            ParserOptions parserOptions = new ParserOptions();

            // TODO: this is where we could pass values from the ScriptContext to 
            // the Gosu symbol table (I think)
            ISymbolTable symbolTable = new StandardSymbolTable(true);
            IGosuProgramParser parser = getParser();// GosuParserFactory.createProgramParser();

            IParseResult parseResult = parser.parseExpressionOnly(script, symbolTable, parserOptions);

            IGosuProgram gosuProgram = parseResult.getProgram();

            ret = gosuProgram.getProgramInstance().evaluate(null); // evaluate it
        } catch (ParseResultsException e) {
            // TODO FIX
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        // TODO: FIX (should use context)
        return parseAndExecute(script, context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return eval(fromReader(reader), context);
    }

    private static final String fromReader(Reader reader) {
        StringBuilder sb = new StringBuilder();
        char[] cbuf = new char[1024];
        try {
            for (int numRead = 0; (numRead = reader.read(cbuf)) > 0;)
                sb.append(String.valueOf(cbuf, 0, numRead));
        } catch (IOException e) {
            // TODO temporary:
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        // TODO: should this be memoized? Should I keep a reference from the constructor?
        return new GosuScriptEngineFactory();
    }

    private static IGosuProgramParser getParser() {
        return ParserHolder.GOSU_PARSER;
    }

    // TODO: can we reuse a parser?
    private static class ParserHolder {
        private static final IGosuProgramParser GOSU_PARSER = GosuParserFactory.createProgramParser();
    }

}
