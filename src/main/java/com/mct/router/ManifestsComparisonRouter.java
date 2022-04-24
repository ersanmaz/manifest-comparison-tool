package com.mct.router;

import com.mct.aggregator.ManifestsAggregator;
import com.mct.processor.ManifestsComparisonProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ManifestsComparisonRouter extends RouteBuilder {

    private final String firstManifestFile;
    private final String secondManifestFile;
    private final ManifestsAggregator manifestsAggregator;
    private final ManifestsComparisonProcessor manifestsComparisonProcessor;

    public ManifestsComparisonRouter(@Value("${in1}") String firstManifestFile,
                                     @Value("${in2}") String secondManifestFile,
                                     ManifestsAggregator manifestsAggregator,
                                     ManifestsComparisonProcessor manifestsComparisonProcessor) {
        this.firstManifestFile = firstManifestFile;
        this.secondManifestFile = secondManifestFile;
        this.manifestsAggregator = manifestsAggregator;
        this.manifestsComparisonProcessor = manifestsComparisonProcessor;
    }

    @Override
    public void configure() throws Exception {

        from(createFileComponent(firstManifestFile))
                .pollEnrich(createFileComponent(secondManifestFile), manifestsAggregator)
                .log("${in.body}")
                .multicast()
                .to("direct:firstComparator", "direct:secondComparator");

        from("direct:firstComparator")
                .process(manifestsComparisonProcessor)
                .to("file:output?fileName=out1.MF");

        from("direct:secondComparator")
                .setBody(exchange -> {
                    List<?> files = exchange.getIn().getBody(List.class);
                    return Arrays.asList(files.get(1), files.get(0));
                })
                .process(manifestsComparisonProcessor)
                .to("file:output?fileName=out2.MF");

    }

    private String createFileComponent(String manifestFile) {
        if ("".equals(manifestFile)) throw new IllegalArgumentException("Manifest file not found!");
        String dir = manifestFile.substring(0, manifestFile.lastIndexOf("/"));
        String file = manifestFile.substring(manifestFile.lastIndexOf("/") + 1);
        return String.format("file:%s?fileName=%s&noop=true", dir, file);
    }
}
