package software.aws.connect;

import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadata;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

public class LMSFrameProcessor implements FrameVisitor.FrameProcessor {

    private OutputStream outputStreamFromCustomer;
    private OutputStream outputStreamToCustomer;

    protected LMSFrameProcessor(OutputStream outputStreamFromCustomer, OutputStream outputStreamToCustomer) {
        this.outputStreamFromCustomer = outputStreamFromCustomer;
        this.outputStreamToCustomer = outputStreamToCustomer;
    }

    public static LMSFrameProcessor create(OutputStream outputStreamFromCustomer, OutputStream outputStreamToCustomer) {
        return new LMSFrameProcessor(outputStreamFromCustomer, outputStreamToCustomer);
    }

    @Override
    public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata) {
        saveToOutPutStream(frame, trackMetadata);
    }

    @Override
    public void close() {
        // No op close.
    }

    private void saveToOutPutStream(final Frame frame, final MkvTrackMetadata trackMetadata) {
        ByteBuffer frameBuffer = frame.getFrameData();
        String trackName = trackMetadata.getTrackName();

        try {
            byte[] frameBytes = new byte[frameBuffer.remaining()];
            frameBuffer.get(frameBytes);
            if (Strings.isNullOrEmpty(trackName) || "AUDIO_FROM_CUSTOMER".equals(trackName)) {
                outputStreamFromCustomer.write(frameBytes);
            } else if ("AUDIO_TO_CUSTOMER".equals(trackName)) {
                outputStreamToCustomer.write(frameBytes);
            } else {
              // Unknown track name. Not writing to output stream.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}