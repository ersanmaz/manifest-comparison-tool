package com.mct.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Component
public class ManifestsAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        File first = oldExchange.getIn().getBody(File.class);
        File second = newExchange.getIn().getBody(File.class);
        oldExchange.getIn().setBody(Arrays.asList(first, second));
        return oldExchange;
    }
}
