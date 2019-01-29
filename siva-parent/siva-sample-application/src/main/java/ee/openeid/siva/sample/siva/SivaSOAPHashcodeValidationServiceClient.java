package ee.openeid.siva.sample.siva;


import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;

@Service(value = SivaServiceType.SOAP_HASHCODE_SERVICE)
public class SivaSOAPHashcodeValidationServiceClient implements HashcodeValidationService {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String EMPTY_STRING = "";
    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;

    @Override
    public Observable<String> validateDocument(String policy, String report, UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("File not found");
        }

        String requestBody = createXMLValidationRequest(file.getEncodedFile(), report, policy);

        String fullUrl = properties.getServiceHost() + properties.getSoapHashcodeServicePath();
        return Observable.just(XMLTransformer.formatXML(restTemplate.postForObject(fullUrl, requestBody, String.class)));
    }

    static String createXMLValidationRequest(String base64Document, String report, String policy) {
        String reportType = getSoapReportTypeRow(report);
        String policyType = getSoapPolicyTypeRow(policy);

        String extraLines = reportType + policyType;
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" + LINE_SEPARATOR +
                "   <soapenv:Header/>" + LINE_SEPARATOR +
                "   <soapenv:Body>" + LINE_SEPARATOR +
                "      <soap:HashcodeValidationDocument>" + LINE_SEPARATOR +
                "         <soap:HashcodeValidationRequest>" + LINE_SEPARATOR +
                "            <SignatureFiles>" + LINE_SEPARATOR +
                "               <SignatureFile>" + LINE_SEPARATOR +
                "                   <Signature>" + base64Document + "</Signature>" + LINE_SEPARATOR +
                "               </SignatureFile>" + LINE_SEPARATOR +
                "            </SignatureFiles>" + LINE_SEPARATOR +
                extraLines +
                "         </soap:HashcodeValidationRequest>" + LINE_SEPARATOR +
                "      </soap:HashcodeValidationDocument>" + LINE_SEPARATOR +
                "   </soapenv:Body>" + LINE_SEPARATOR +
                "</soapenv:Envelope>";
    }

    private static String getSoapPolicyTypeRow(String policy) {
        if (StringUtils.isNotBlank(policy))
            return "            <SignaturePolicy>" + policy + "</SignaturePolicy>" + LINE_SEPARATOR;
        return EMPTY_STRING;
    }

    private static String getSoapReportTypeRow(String report) {
        if (StringUtils.isNotBlank(report))
            return "            <ReportType>" + report + "</ReportType>" + LINE_SEPARATOR;
        return EMPTY_STRING;
    }


    @Autowired
    public void setProperties(SivaRESTWebServiceConfigurationProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
