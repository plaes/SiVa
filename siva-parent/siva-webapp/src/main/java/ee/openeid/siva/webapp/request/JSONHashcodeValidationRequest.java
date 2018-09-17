/*
 * Copyright 2018 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.webapp.request;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import ee.openeid.siva.webapp.request.validation.annotations.ValidReportType;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignatureFilename;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicy;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class JSONHashcodeValidationRequest implements HashcodeValidationRequest {

    @ValidBase64String(message = "{validation.error.message.signatureFile.invalidBase64}")
    private String signatureFile;

    @ValidSignatureFilename
    private String filename;

    @ValidSignaturePolicy
    private String signaturePolicy;

    @ValidReportType
    private String reportType;

    @Valid
    @NotNull
    @NotEmpty
    private List<Datafile> datafiles;
}
