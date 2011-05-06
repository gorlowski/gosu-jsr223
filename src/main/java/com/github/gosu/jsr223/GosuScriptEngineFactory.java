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

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * A incomplete JSR 223 implementation of {@link ScriptEngineFactory} for the Gosu language. See the
 * comments in {@link GosuScriptEngine} for implementation gotchas.
 * 
 * @author Greg Orlowski
 */
public class GosuScriptEngineFactory implements ScriptEngineFactory {

    private static final String ENGINE_NAME = "Gosu";

    // This was the gosu version that I developed on
    private static final String GOSU_VERSION = "0.8.6.1-C";

    private static final String VERSION = "0.0.1";
    private static final List<String> EXTENSIONS = new ArrayList<String>();

    static {
        EXTENSIONS.add("gsp");
    }

    private static final List<String> MIME_TYPES = new ArrayList<String>();
    static {
        MIME_TYPES.add("application/gosu");
        MIME_TYPES.add("text/gosu");
    }

    private static final List<String> ENGINE_NAMES = new ArrayList<String>();
    static {
        ENGINE_NAMES.add(ENGINE_NAME);
        ENGINE_NAMES.add(ENGINE_NAME.toLowerCase());
    }

    @Override
    public String getEngineName() {
        return "Gosu";
    }

    @Override
    public String getEngineVersion() {
        return VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }

    @Override
    public List<String> getNames() {
        return ENGINE_NAMES;
    }

    @Override
    public String getLanguageName() {
        return ENGINE_NAME;
    }

    @Override
    public String getLanguageVersion() {
        return GOSU_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        return getScriptEngine().get(key).toString();
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        // TODO Auto-generated method stub
        return null;
    }

    // ???
    @Override
    public String getProgram(String... statements) {
        if (statements == null || statements.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < statements.length - 1; i++)
            sb.append(statements[i]).append(statements[i].trim().endsWith(";") ? "" : "; ");
        sb.append(statements[statements.length - 1]);
        return sb.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new GosuScriptEngine();
    }

}
