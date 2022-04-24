package com.mct.processor;

import com.mct.comparator.ManifestsComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManifestsComparisonProcessor implements Processor {

    private final ManifestsComparator manifestsComparator;

    @Override
    public void process(Exchange exchange) {
        List<?> files = exchange.getIn().getBody(List.class);
        File first = (File) files.get(0);
        File second = (File) files.get(1);
        String difference = manifestsComparator.getDifference(first, second);
        exchange.getIn().setBody(difference);
    }
}
