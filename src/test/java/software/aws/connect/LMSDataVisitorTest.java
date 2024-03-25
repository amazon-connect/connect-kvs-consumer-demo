package software.aws.connect;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.amazonaws.kinesisvideo.parser.ebml.EBMLElementMetaData;
import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.MkvDataElement;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException;
import com.amazonaws.kinesisvideo.parser.mkv.MkvValue;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import static com.amazonaws.kinesisvideo.parser.ebml.MkvTypeInfos.SIMPLEBLOCK;

/**
 * Unit test for simple App.
 */
public class LMSDataVisitorTest 
{
    private static final byte[] TEST_BYTES = "This is a test".getBytes();

    @Mock FragmentMetadataVisitor fragmentMetadataVisitor;
    @Mock LMSTagVisitor tagVisitor;
    @Mock OutputStream outputStreamFromCustomer;
    @Mock OutputStream outputStreamToCustomer;
    
    @Mock MkvDataElement mkvDataElement;
    @Mock EBMLElementMetaData ebmlElementMetaData;
    @Mock MkvValue<Frame> mkvValue;
    @Mock Frame frame;
    @Mock ByteBuffer byteBuffer;
    @Mock MkvTrackMetadata mkvTrackMetadata;

    LMSDataVisitor lmsDataVisitor;

    @Before
    public void setup() {
        openMocks(this);
        when(mkvDataElement.getElementMetaData()).thenReturn(ebmlElementMetaData);
        when(ebmlElementMetaData.getTypeInfo()).thenReturn(SIMPLEBLOCK);
        when(mkvDataElement.getValueCopy()).thenReturn(mkvValue);

        when(mkvValue.getVal()).thenReturn(frame);
        when(frame.getFrameData()).thenReturn(ByteBuffer.wrap(TEST_BYTES));

        when(fragmentMetadataVisitor.getMkvTrackMetadata(anyLong())).thenReturn(mkvTrackMetadata);

        lmsDataVisitor = LMSDataVisitor.create(fragmentMetadataVisitor, tagVisitor, outputStreamFromCustomer, outputStreamToCustomer);
    }

    @Test
    public void testWriteFromCustomerWhenTrackNameNull() throws IOException, MkvElementVisitException
    {
        lmsDataVisitor.visit(mkvDataElement);
        verify(outputStreamFromCustomer, times(1)).write(TEST_BYTES);
        verify(outputStreamToCustomer, times(0)).write(any());
    }

    @Test
    public void testNoWriteWhenTrackNameIncorrect() throws IOException, MkvElementVisitException
    {
        when(mkvTrackMetadata.getTrackName()).thenReturn("WRONG_TRACK");
        lmsDataVisitor.visit(mkvDataElement);
        verify(outputStreamFromCustomer, times(0)).write(any());
        verify(outputStreamToCustomer, times(0)).write(any());
    }

    @Test
    public void testWriteFromCustomer() throws IOException, MkvElementVisitException
    {
        when(mkvTrackMetadata.getTrackName()).thenReturn("AUDIO_FROM_CUSTOMER");
        lmsDataVisitor.visit(mkvDataElement);
        verify(outputStreamFromCustomer, times(1)).write(TEST_BYTES);
        verify(outputStreamToCustomer, times(0)).write(any());
    }

    @Test
    public void testWriteToCustomer() throws IOException, MkvElementVisitException
    {
        when(mkvTrackMetadata.getTrackName()).thenReturn("AUDIO_TO_CUSTOMER");
        lmsDataVisitor.visit(mkvDataElement);
        verify(outputStreamFromCustomer, times(0)).write(any());
        verify(outputStreamToCustomer, times(1)).write(TEST_BYTES);
    }

    @Test
    public void testIsDoneTrue() throws IOException, MkvElementVisitException
    {
        when(tagVisitor.isDone()).thenReturn(true);
        assertEquals(true, lmsDataVisitor.isDone());

        when(mkvTrackMetadata.getTrackName()).thenReturn("AUDIO_TO_CUSTOMER");
        lmsDataVisitor.visit(mkvDataElement);
        verify(outputStreamFromCustomer, times(0)).write(any());
        verify(outputStreamToCustomer, times(0)).write(any());
    }

    @Test
    public void testIsDoneFalse() throws IOException, MkvElementVisitException
    {
        when(tagVisitor.isDone()).thenReturn(false);
        assertEquals(false, lmsDataVisitor.isDone());

        when(mkvTrackMetadata.getTrackName()).thenReturn("AUDIO_FROM_CUSTOMER");
        lmsDataVisitor.visit(mkvDataElement);
        verify(outputStreamFromCustomer, times(1)).write(TEST_BYTES);
        verify(outputStreamToCustomer, times(0)).write(any());
    }
}
