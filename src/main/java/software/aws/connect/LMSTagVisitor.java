package software.aws.connect;

import com.amazonaws.kinesisvideo.parser.ebml.MkvTypeInfos;
import com.amazonaws.kinesisvideo.parser.mkv.MkvDataElement;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitor;
import com.amazonaws.kinesisvideo.parser.mkv.MkvEndMasterElement;
import com.amazonaws.kinesisvideo.parser.mkv.MkvStartMasterElement;

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

public class LMSTagVisitor extends MkvElementVisitor {
    private String tagName;
    private String tagValue;
    private String prevContactId;
    private boolean isDone = false;

    private LMSTagVisitor () {};

    public static LMSTagVisitor create() {
        return new LMSTagVisitor();
    }

    @Override
    public void visit(MkvStartMasterElement startMasterElement) throws MkvElementVisitException {
        if (MkvTypeInfos.EBML.equals(startMasterElement.getElementMetaData().getTypeInfo())) {
            System.out.println("Start of segment");
        }
        // System.out.println(startMasterElement.getElementMetaData().toString());
    }

    @Override
    public void visit(MkvEndMasterElement endMasterElement) throws MkvElementVisitException {
        if (MkvTypeInfos.SEGMENT.equals(endMasterElement.getElementMetaData().getTypeInfo())) {
            System.out.println("End of segment");
        }
        // System.out.println(endMasterElement.getElementMetaData().toString());
    }

    @Override
    public void visit(MkvDataElement dataElement) throws MkvElementVisitException {
        if (MkvTypeInfos.TAGNAME.equals(dataElement.getElementMetaData().getTypeInfo())) {
            tagName = (String) dataElement.getValueCopy().getVal();
        } else if (MkvTypeInfos.TAGSTRING.equals(dataElement.getElementMetaData().getTypeInfo())) {
            tagValue = (String) dataElement.getValueCopy().getVal();
        }
        if (tagName != null && tagValue != null) {
            System.out.println(String.format("%s=%s", tagName, tagValue));

            // Stop processing if we encounter a different contactId
            if (tagName == "ContactId") {
                if (prevContactId == null) {
                    prevContactId = tagValue;
                } else if (!tagValue.equals(prevContactId)) {
                    isDone = true;
                }
            }

            // Empty the values for new tag
            tagName = null;
            tagValue = null;
        }
        
    }

    public boolean isDone() {
        return isDone;
    }
}
