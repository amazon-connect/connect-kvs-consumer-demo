package software.aws.connect;

import java.nio.ByteBuffer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.amazonaws.kinesisvideo.parser.ebml.EBMLElementMetaData;
import com.amazonaws.kinesisvideo.parser.mkv.MkvDataElement;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException;
import com.amazonaws.kinesisvideo.parser.mkv.MkvValue;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import static com.amazonaws.kinesisvideo.parser.ebml.MkvTypeInfos.TAGNAME;
import static com.amazonaws.kinesisvideo.parser.ebml.MkvTypeInfos.TAGSTRING;

/**
 * Unit test for simple App.
 */
public class LMSTagVisitorTest 
{
    @Mock MkvDataElement mkvDataElement;
    @Mock EBMLElementMetaData ebmlElementMetaData;
    @Mock MkvValue mkvValue;
    @Mock ByteBuffer byteBuffer;
    @Mock MkvTrackMetadata mkvTrackMetadata;

    LMSTagVisitor lmsTagVisitor;

    @Before
    public void setup() {
        openMocks(this);
        when(mkvDataElement.getElementMetaData()).thenReturn(ebmlElementMetaData);
        when(mkvDataElement.getValueCopy()).thenReturn(mkvValue);

        lmsTagVisitor = LMSTagVisitor.create();
    }

    @Test
    public void testIsDoneTrue() throws MkvElementVisitException
    {
        when(ebmlElementMetaData.getTypeInfo()).thenReturn(TAGNAME).thenReturn(TAGSTRING);
        when(mkvValue.getVal()).thenReturn("ContactId").thenReturn("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        lmsTagVisitor.visit(mkvDataElement);
        lmsTagVisitor.visit(mkvDataElement);
        when(ebmlElementMetaData.getTypeInfo()).thenReturn(TAGNAME).thenReturn(TAGSTRING);
        when(mkvValue.getVal()).thenReturn("ContactId").thenReturn("yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy");
        lmsTagVisitor.visit(mkvDataElement);
        lmsTagVisitor.visit(mkvDataElement);
        assertEquals(true, lmsTagVisitor.isDone());
    }

    @Test
    public void testIsDoneFalse() throws MkvElementVisitException
    {
        when(ebmlElementMetaData.getTypeInfo()).thenReturn(TAGNAME).thenReturn(TAGSTRING);
        when(mkvValue.getVal()).thenReturn("ContactId").thenReturn("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        lmsTagVisitor.visit(mkvDataElement);
        lmsTagVisitor.visit(mkvDataElement);
        when(ebmlElementMetaData.getTypeInfo()).thenReturn(TAGNAME).thenReturn(TAGSTRING);
        when(mkvValue.getVal()).thenReturn("ContactId").thenReturn("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        lmsTagVisitor.visit(mkvDataElement);
        lmsTagVisitor.visit(mkvDataElement);
        assertEquals(false, lmsTagVisitor.isDone());
    }
}
