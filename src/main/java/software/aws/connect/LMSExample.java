package software.aws.connect;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.examples.KinesisVideoCommon;
import com.amazonaws.kinesisvideo.parser.examples.StreamOps;
import com.amazonaws.kinesisvideo.parser.examples.GetMediaWorker;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.model.StartSelector;
import com.amazonaws.services.kinesisvideo.model.StartSelectorType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

public class LMSExample extends KinesisVideoCommon {

    private final ExecutorService executorService;
    private final StreamOps streamOps;
    private final OutputStream outputStreamFromCustomer;
    private final OutputStream outputStreamToCustomer;
    private final String fragmentNumber;

    public LMSExample(Regions region,
                      String streamName,
                      String fragmentNumber,
                      AWSCredentialsProvider credentialsProvider,
                      OutputStream outputStreamFromCustomer,
                      OutputStream outputStreamToCustomer) throws IOException {
        super(region, credentialsProvider, streamName);
        this.executorService = Executors.newFixedThreadPool(2);
        this.streamOps = new StreamOps(region,  streamName, credentialsProvider);
        this.outputStreamFromCustomer = outputStreamFromCustomer;
        this.outputStreamToCustomer = outputStreamToCustomer;
        this.fragmentNumber = fragmentNumber;
    }

    public void execute() throws InterruptedException, IOException {
        try {
            FragmentMetadataVisitor fragmentMetadataVisitor = FragmentMetadataVisitor.create();
            LMSTagVisitor lmsTagVisitor = LMSTagVisitor.create();
            LMSDataVisitor lmsDataVisitor = LMSDataVisitor.create(fragmentMetadataVisitor, lmsTagVisitor, outputStreamFromCustomer, outputStreamToCustomer);
            
            //Start a GetMedia worker to read and process data from the Kinesis Video Stream.
            GetMediaWorker getMediaWorker = GetMediaWorker.create(
                getRegion(),
                getCredentialsProvider(),
                getStreamName(),
                new StartSelector()
                    .withStartSelectorType(StartSelectorType.FRAGMENT_NUMBER)
                    .withAfterFragmentNumber(fragmentNumber),
                streamOps.getAmazonKinesisVideo(),
                LMSCompositeMkvElementVisitor.create(fragmentMetadataVisitor, lmsTagVisitor, lmsDataVisitor)
            );
            
            executorService.submit(getMediaWorker);

            //Wait for the workers to finish.
            executorService.shutdown();
            executorService.awaitTermination(120, TimeUnit.SECONDS);
            if (!executorService.isTerminated()) {
                System.out.println("Shutting down executor service by force");
                executorService.shutdownNow();
            } else {
                System.out.println("Executor service is shutdown");
            }
        } finally {
            outputStreamFromCustomer.close();
            outputStreamToCustomer.close();
        }
    }
}