package ee.openeid.siva.proxy.transformer;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.siva.model.ValidationRequest;
import ee.openeid.siva.testutils.MockValidationRequestBuilder;
import eu.europa.esig.dss.MimeType;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ValidationRequestToJsonDocumentTransformerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_PDF_FILE = "test-files/sample.pdf";
    private ValidationRequestToJsonDocumentTransformer transformer = new ValidationRequestToJsonDocumentTransformer();
    private ValidationRequest validationRequest;

    @Before
    public void setUp() throws Exception {
        setValidPdfValidationRequest();
    }

    @Test
    public void fileNameRemainsUnchanged() {
        assertEquals(validationRequest.getFilename(), transformer.transform(validationRequest).getName());
    }

    @Test
    public void typeIsCorrectlyTransformedToMimeType() {
        assertEquals(MimeType.PDF, transformer.transform(validationRequest).getMimeType());
    }

    @Test
    public void contentIsCorrectlyTransformedToBytes() {
        JSONDocument jsonDocument = transformer.transform(validationRequest);
        assertEquals(validationRequest.getBase64Document(), Base64.encodeBase64String(jsonDocument.getBytes()));
    }

    @Test
    public void unsupportedTypeThrowsException() {
        validationRequest = MockValidationRequestBuilder.aValidationRequest().withType("unsupported").build();
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("type = unsupported is unsupported");
        transformer.transform(validationRequest);
    }

    private void setValidPdfValidationRequest() throws Exception {
        Path filepath = Paths.get(getClass().getClassLoader().getResource(VALID_PDF_FILE).toURI());
        validationRequest = MockValidationRequestBuilder
                .aValidationRequest()
                .withType("pdf")
                .withDocument(filepath)
                .build();
    }
}