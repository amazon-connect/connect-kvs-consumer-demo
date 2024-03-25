package software.aws.connect;

import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitor;
import com.amazonaws.kinesisvideo.parser.mkv.visitors.CompositeMkvElementVisitor;

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

public class LMSCompositeMkvElementVisitor extends CompositeMkvElementVisitor {

    private LMSCompositeMkvElementVisitor(MkvElementVisitor... mkvElementVisitors) {
        super(mkvElementVisitors);
    }

    public static LMSCompositeMkvElementVisitor create(MkvElementVisitor... mkvElementVisitors) {
        return new LMSCompositeMkvElementVisitor(mkvElementVisitors);
    }
}
