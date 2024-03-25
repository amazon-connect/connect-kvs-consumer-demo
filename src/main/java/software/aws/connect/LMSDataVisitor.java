package software.aws.connect;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import com.google.common.base.Strings;
import org.apache.commons.lang3.Validate;

import com.amazonaws.kinesisvideo.parser.ebml.MkvTypeInfos;
import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitor;
import com.amazonaws.kinesisvideo.parser.mkv.MkvValue;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

public class LMSDataVisitor extends MkvElementVisitor {
    private final FragmentMetadataVisitor fragmentMetadataVisitor;
    private final LMSTagVisitor tagVisitor;
    private final OutputStream outputStreamFromCustomer;
    private final OutputStream outputStreamToCustomer;

    private LMSDataVisitor(FragmentMetadataVisitor fragmentMetadataVisitor, LMSTagVisitor tagVisitor, OutputStream outputStreamFromCustomer, OutputStream outputStreamToCustomer) {
        this.fragmentMetadataVisitor = fragmentMetadataVisitor;
        this.tagVisitor = tagVisitor;
        this.outputStreamFromCustomer = outputStreamFromCustomer;
        this.outputStreamToCustomer = outputStreamToCustomer;
    }

    public static LMSDataVisitor create(FragmentMetadataVisitor fragmentMetadataVisitor, LMSTagVisitor tagVisitor, OutputStream outputStreamFromCustomer, OutputStream outputStreamToCustomer) {
        return new LMSDataVisitor(fragmentMetadataVisitor, tagVisitor, outputStreamFromCustomer, outputStreamToCustomer);
    }

    @Override
    public void visit(final com.amazonaws.kinesisvideo.parser.mkv.MkvStartMasterElement startMasterElement) throws com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException {
    }

    @Override
    public void visit(final com.amazonaws.kinesisvideo.parser.mkv.MkvEndMasterElement endMasterElement) throws com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException {
    }

    @Override
    public void visit(final com.amazonaws.kinesisvideo.parser.mkv.MkvDataElement dataElement) throws com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException {
        if (isDone()) {
            return;
        }
        
        if (MkvTypeInfos.SIMPLEBLOCK.equals(dataElement.getElementMetaData().getTypeInfo())) {
            final MkvValue<Frame> frame = dataElement.getValueCopy();
            Validate.notNull(frame);

            final ByteBuffer frameBuffer = frame.getVal().getFrameData();
            final String trackName  = fragmentMetadataVisitor.getMkvTrackMetadata(frame.getVal().getTrackNumber()).getTrackName();
            
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

    @Override
    public boolean isDone() {
        return tagVisitor.isDone();
    }
}
