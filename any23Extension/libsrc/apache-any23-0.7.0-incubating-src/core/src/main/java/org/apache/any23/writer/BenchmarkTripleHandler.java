/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.writer;

import org.apache.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * {@link TripleHandler} decorator useful to
 * perform benchmarking.
 */
public class BenchmarkTripleHandler implements TripleHandler {

    /**
     * Decorated.
     */
    private TripleHandler underlyingHandler;

    /**
     * Collected statistics. 
     */
    private final Map<String, StatObject> stats;

    /**
     * Constructor.
     */
    public BenchmarkTripleHandler(TripleHandler tripleHandler) {
        if(tripleHandler == null) {
            throw new NullPointerException("tripleHandler cannot be null.");
        }
        underlyingHandler = tripleHandler;
        stats = new HashMap<String, StatObject>();
        stats.put("SUM", new StatObject());
    }

    /**
     * Returns the report as a human readable string.
     *
     * @return a human readable report.
     */
    public String report() {
        StringBuilder sb = new StringBuilder();
        StatObject sum = stats.get("SUM");

        sb.append("\n>Summary: ");
        sb.append("\n   -total calls: ").append(sum.methodCalls);
        sb.append("\n   -total triples: ").append(sum.triples);
        sb.append("\n   -total runtime: ").append(sum.runtime).append(" ms!");
        if (sum.runtime != 0)
            sb.append("\n   -tripls/ms: ").append(sum.triples / sum.runtime);
        if (sum.methodCalls != 0)
            sb.append("\n   -ms/calls: ").append(sum.runtime / sum.methodCalls);

        stats.remove("SUM");

        for (Entry<String, StatObject> ent : stats.entrySet()) {
            sb.append("\n>Extractor: "       ).append(ent.getKey());
            sb.append("\n   -total calls: "  ).append(ent.getValue().methodCalls);
            sb.append("\n   -total triples: ").append(ent.getValue().triples);
            sb.append("\n   -total runtime: ").append(ent.getValue().runtime).append(" ms!");
            if (ent.getValue().runtime != 0)
                sb.append("\n   -tripls/ms: "  ).append(ent.getValue().triples / ent.getValue().runtime);
            if (ent.getValue().methodCalls != 0)
                sb.append("\n   -ms/calls: "   ).append(ent.getValue().runtime / ent.getValue().methodCalls);

        }

        return sb.toString();
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        underlyingHandler.startDocument(documentURI);
    }

    public void close() throws TripleHandlerException {
        underlyingHandler.close();
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        if (stats.containsKey(context.getExtractorName())) {
            stats.get(context.getExtractorName()).interimStop();
            stats.get("SUM").interimStop();
        }
        underlyingHandler.closeContext(context);
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        if (!stats.containsKey(context.getExtractorName())) {
            stats.put(context.getExtractorName(), new StatObject());
        }
        stats.get(context.getExtractorName()).methodCalls++;
        stats.get(context.getExtractorName()).interimStart();
        stats.get("SUM").methodCalls++;
        stats.get("SUM").interimStart();
        underlyingHandler.openContext(context);
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        if (!stats.containsKey(context.getExtractorName())) {
            stats.put(context.getExtractorName(), new StatObject());
        }
        stats.get(context.getExtractorName()).triples++;
        stats.get("SUM").triples++;
        underlyingHandler.receiveTriple(s, p, o, g, context);
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        underlyingHandler.receiveNamespace(prefix, uri, context);
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        underlyingHandler.endDocument(documentURI);
    }

    public void setContentLength(long contentLength) {
        underlyingHandler.setContentLength(contentLength);
    }

    /**
     * A single statistics.
     */
    private class StatObject {

        int methodCalls = 0;
        int triples     = 0;
        long runtime    = 0;
        long intStart   = 0;

        /**
         * Takes the start time.
         */
        public void interimStart() {
            intStart = System.currentTimeMillis();
        }

        /**
         * Takes the stop time.
         */
        public void interimStop() {
            runtime += (System.currentTimeMillis() - intStart);
            intStart = 0;
        }
    }

}

