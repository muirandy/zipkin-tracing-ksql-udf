package com.github.muirandy.kafka.ksql.udf;

import brave.Span;
import brave.Tracing;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

@UdfDescription(name = "zipkinTracing", description = "Adding zipkin tracing support to KSQL CSAS")
public class ZipkinTracing {

    @Udf(description = "Get the current trace")
    public String zipkinTracing() {
        Span span = getTheCurrentSpan();
        return readTraceIdFromSpan(span);
    }

    @Udf(description = "Apply a tag to the current trace")
    public String zipkinTracing(@UdfParameter("tag name") String tagName, @UdfParameter("tag value") String tagValue) {
        Span span = getTheCurrentSpan();
        logTracing(tagName, tagValue, span);
        tagTheSpan(tagName, tagValue, span);
        return readTraceIdFromSpan(span);
    }

    private Span getTheCurrentSpan() {
        Span currentSpan = Tracing.currentTracer().currentSpan();
        return currentSpan;
    }

    private void logTracing(String tagName, String tagValue, Span span) {
        String traceIdString = readTraceIdFromSpan(span);
        System.out.println("Tagging traceId " + traceIdString + " with " + tagName + "=" + tagValue);
    }

    private String readTraceIdFromSpan(Span span) {
        return span.context().traceIdString();
    }

    private void tagTheSpan(String tagName, String tagValue, Span span) {
        span.tag(tagName, tagValue);
    }
}
