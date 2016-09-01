<!--# Interface description-->

In this section the SiVa signature validation service's external interfaces that are provided for the service's clients are described. For information of internal components and their interfaces, please refer to [**Component diagram**](component_diagram). 

SiVa service provides **REST JSON** and **SOAP** interfaces that enable the service users to:

* request validation of signatures in a digitally signed document (i.e. signature container like BDOC/ASiC-E or PDF); 
* receive a response with the validation result of all the signatures in the document. 

In the following subsections, the SiVa validation request and response interfaces are described in detail. 

## Validation request interface


** REST JSON Endpoint **

```
POST https://<server url>/validate
```

** SOAP Endpoint ** 
```
POST https://<server url>/soap/validationWebService/validateDocument
```

### Validation request parameters 

Validation request parameters for JSON and SOAP interfaces are described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](appendix/wsdl).    

| JSON parameter | SOAP parameter | Mandatory | Description | JSON data type | 
|-----------------|-----------------|-----------|-------------|
| document | Document | + | Base64 encoded string of digitally signed document to be validated | String |
| filename | Filename | + | File name of the digitally signed document (i.e. sample.bdoc) | String |
| documentType | DocumentType | + | Format of the digitally signed document. This value determines the validation service type that will be used for validation. <br> **Possible values:** <br> * BDOC - for documents in [BDOC](http://id.ee/public/bdoc-spec212-eng.pdf) (ASiC-E/XAdES) format. Both time-stamp (TS) and time-mark (TM) profiles are supported here. <br> * DDOC - for documents in [DIGIDOC-XML](http://id.ee/public/DigiDoc_format_1.3.pdf) format, supported versions are DIGIDOC-XML 1.0 (also known as SK-XML 1.0) to DIGIDOC-XML 1.3. In case of DIGIDOC_XML documents, also the hashcode format is supported (see [DigiDocService Specification](http://sertkeskus.github.io/dds-documentation/api/api_docs/#ddoc-format-and-hashcode) for more information). <br> * PDF - for signed PDF document in [PAdES](http://www.etsi.org/deliver/etsi_en/319100_319199/31914201/01.01.01_60/en_31914201v010101p.pdf) format. <br>* XROAD - for documents created in the [X-Road](https://www.ria.ee/en/x-road.html) information system, see also [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf) of the signature format. | String |
| signaturePolicy | SignaturePolicy | - | Can be used to change the default signature validation policy that is used by the service. <br> See also [SiVa Validation Policy](appendix/validation_policy) for more information. <br> **Possible values:** <br> * POLv1 - the default policy. Signatures with all legal levels are accepted (i.e. QES, AdES and AdESqc, according to Regulation (EU) No 910/2014.) <br> * POLv2 - only signatures with QES legal level (according to Regulation (EU) No 910/2014) are accepted. | String |

### Sample JSON request

```json
{
  "filename":"sample.ddoc",
  "documentType":"DDOC",
  "document":"PD94bWwgdmVyc2lvbj0iMS4....",
  "signaturePolicy": "POLv1"
}
```


### Sample SOAP request

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocument xmlns:ns2="http://soap.webapp.siva.openeid.ee/">
      <ns2:ValidationRequest>
        <Document>PD94bWwgdmVyc2lvbj0iMS4....</Document>
        <Filename>sample.ddoc</Filename>
        <DocumentType>DDOC</DocumentType>
        <SignaturePolicy>POLv1</SignaturePolicy>
      </ns2:ValidationRequest>
    </ns2:ValidateDocument>
  </soap:Body>
</soap:Envelope>
```


## Validation response interface

### Validation response parameters (successful scenario)

The signature validation report (i.e. the validation response) for JSON and SOAP interfaces is described in the table below. Data types of SOAP parameters are defined in the [SiVa WSDL document](appendix/wsdl).    

| JSON parameter | SOAP parameter | Description | JSON data type |
|----------------------------------------------|--------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `policy` | `Policy` | Object containing information of the SiVa signature validation policy that was used for validation | Object |
| `policy.policyVersion` | `Policy.PolicyVersion` | Version of the validation policy | String |
| `policy.policyName` | `Policy.PolicyName` | Name of the validation policy | String |
| `policy. policyDescription` | `Policy. PolicyDescription` | Short description of the validation policy | String |
| `policy.policyUrl` | `Policy.PolicyUrl` | URL where the signature validation policy document can be downloaded. The validation policy document shall include information about validation of all the document formats, including the different validation policies that are used in case of different file formats and base libraries. | String |
| `signaturesCount` | `SignaturesCount` | Number of signatures found inside digitally signed file | Number |
| `validSignaturesCount` | `ValidSignaturesCount` | Signatures count that have validated to `TOTAL-PASSED`. See also `validSignaturesCount` field. | Number |
| `validationTime` | `ValidationTime` | Time of validating the signature by the service | Date |
| `documentName` | `DocumentName` | Digitally signed document's file name | String |
| `signatureForm` | `SignatureForm` | Format (and optionally version) of the digitally signed document container. <br> In case of documents in [DIGIDOC-XML](http://id.ee/public/DigiDoc_format_1.3.pdf) (DDOC) format, the "hashcode" suffix is used to denote that the container was validated in [hashcode mode](http://sertkeskus.github.io/dds-documentation/api/api_docs/#ddoc-format-and-hashcode), i.e. without original data files. <br> **Possible values:**  <br> * DIGIDOC_XML_1.0 <br> * DIGIDOC_XML_1.0_hashcode <br> * DIGIDOC_XML_1.1 <br> * DIGIDOC_XML_1.1_hashcode <br> * DIGIDOC_XML_1.2 <br> * DIGIDOC_XML_1.2_hashcode <br> * DIGIDOC_XML_1.3 <br> * DIGIDOC_XML_1.3_hashcode <br> * PAdES - used in case of signed PDF document <br> * ASiC_E - used in case of all [BDOC](http://id.ee/public/bdoc-spec212-eng.pdf) documents and X-Road simple containers that don't use batch time-stamping (see [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf))<br> * ASiC_E_batchsignature - used in case of X-Road containers with batch signature (see [specification document](https://cyber.ee/uploads/2013/05/T-4-23-Profile-for-High-Performance-Digital-Signatures1.pdf))| String |
| `signatures` | `Signatures` | Collection of signatures found in digitally signed document | Array |
| `signatures[0]` | `Signature` | Signature information object | Object |
| `signatures[0]. claimedSigningTime` | `Signature. ClaimedSigningTime` | Claimed signing time, i.e. signer's computer time during signature creation | Date |
| `signatures[0].id` | `Signature.Id` | Signature ID attribute  | String |
| `signatures[0].indication` | `Signature.Indication` | Overall result of the signature's validation process, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 5: Status indications of the signature validation process". <br> Note that the validation results of different signatures in one signed document (signature container) may vary. <br> See also `validSignaturesCount` and `SignaturesCount` fields. <br>**Possible values:** <br> * TOTAL-PASSED <br> * TOTAL-FAILED <br> * INDETERMINATE | String |
| `signatures[0]. subIndication` | `Signature. SubIndication` | Additional subindication in case of failed or indeterminate validation result, according to [ETSI EN 319 102-1](http://www.etsi.org/deliver/etsi_en/319100_319199/31910201/01.01.01_60/en_31910201v010101p.pdf) "Table 6: Validation Report Structure and Semantics" | String |
| `signatures[0].errors` | `Signature.Errors` | Information about validation error(s), array of error messages.  | Array |
| `signatures[0].errors[0].  content` | `Signature.Errors. Error.Content` | Error message, as returned by the base library that was used for signature validation. | String |
| `signatures[0].info` | `Signature.Info` | Object containing trusted signing time information.  | Object |
| `signatures[0].info. bestSignatureTime` | `Signature.Info. BestSignatureTime` | Time value that is regarded as trusted signing time, denoting the earliest time when it can be trusted by the validation application (because proven by some Proof-of-Existence present in the signature) that a signature has existed.<br>The source of the value depends on the signature profile (see also `SignatureFormat` parameter):<br>- Signature with time-mark (LT_TM level) - the producedAt value of the earliest valid time-mark (OCSP confirmation of the signer's certificate) in the signature.<br>- Signature with time-stamp (LT or LTA level) - the genTime value of the earliest valid signature time-stamp token in the signature. <br> - Signature with BES or EPES level - the value is empty, i.e. there is no trusted signing time value available. | Date |
| `signatures[0]. signatureFormat` | `Signature. SignatureFormat` | Format and profile (according to Baseline Profile) of the signature. See [XAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103171/02.01.01_60/ts_103171v020101p.pdf) and [PAdES Baseline Profile](http://www.etsi.org/deliver/etsi_ts/103100_103199/103172/02.02.02_60/ts_103172v020202p.pdf) for detailed description of the Baseline Profile levels. Levels that are accepted in SiVa validation policy are described in [SiVa signature validation policy](appendix/validation_policy) <br>**Possible values:**  <br> * XAdES_BASELINE_B_BES <br> * XAdES_BASELINE_B_EPES <br> * XAdES_BASELINE_T <br> * XAdES_BASELINE_LT - long-term level XAdES signature where time-stamp is used as a assertion of trusted signing time<br> * XAdES_BASELINE_LT_TM - long-term level XAdES signature where time-mark is used as a assertion of trusted signing time. Used in case of [BDOC](http://id.ee/public/bdoc-spec212-eng.pdf) signatures with time-mark profile and [DIGIDOC-XML](http://id.ee/public/DigiDoc_format_1.3.pdf) (DDOC) signatures.<br> * XAdES_BASELINE_LTA <br> * PAdES_BASELINE_B_BES <br> * PAdES_BASELINE_B_EPES <br> * PAdES_BASELINE_T <br> * PAdES_BASELINE_LT <br> * PAdES_BASELINE_LTA | String |
| `signatures[0]. signatureLevel` | `Signature. SignatureLevel` | Legal level of the signature, according to Regulation (EU) No 910/2014. <br> - In case of BDOC and PAdES formats: indication whether the signature is Advanced electronic Signature (AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES). <br> - In case of DIGIDOC-XML 1.0..1.3 formats, empty value is used as the signature level is not checked by the JDigiDoc base library that is used for validation. However, the signatures can be indirectly regarded as QES level signatures, see also [SiVa Validation Policy](appendix/validation_policy)| String |
| `signatures[0].signedBy` | `Signature.SignedBy` | Signers name and identification number, i.e. value of the CN field of the signer's certificate | String |
| `signatures[0]. signatureScopes` | `Signature. SignatureScopes` | Contains information of the original data that is covered by the signature. | Array |
| `signatures[0]. signatureScopes[0]. name` | `Signature. SignatureScopes.  SignatureScope.Name` | Name of the signature scope.  <br>- In case of XAdES signature: name of the data file that is signed. <br>- In case of PAdES signature: PDF version that is covered by the signature, e.g. 'PDF previous version #1' | String |
| `signatures[0]. signatureScopes[0]. scope` | `Signature. SignatureScopes.  SignatureScope. Scope` | Type of the signature scope.<br>- In case of XAdES signature: 'FullSignatureScope'<br>- In case of PAdES signature: 'PdfByteRangeSignatureScope' | String |
| `signatures[0]. signatureScopes[0]. content` | `Signature. SignatureScopes.  SignatureScope. Content` | - In case of XAdES signatures: 'Full document', indicating that the whole document is covered by the signature. <br>- In case of PAdES signatures: the byte range that is covered by the signature. | String |
| `signatures[0].warnings` | `Signature.Warnings` | Block of validation warnings that do not affect the overall validation result. Known warning situations (according to http://id.ee/public/SK-JDD-PRG-GUIDE.pdf, chap. 5.2.4.1): <br>- BDOC (ASiC-E) and PAdES: weaker digest method (SHA-1) has been used than recommended when calculating either Reference or Signature element’s digest value; <br> - DIGIDOC-XML 1.0-1.3: DataFile element’s xmlns attribute is missing;<br> - DIGIDOC-XML 1.0-1.3: IssuerSerial/X509IssuerName and/or IssuerSerial/X509IssuerSerial element’s xmlns attribute is missing. | Array |
| `signatures[0].warnings[0]. description` | `Signature.Warnings. Warning.Description` | Warning description, as retuned by the base library that was used for validation. | String |

### Sample JSON response (successful scenario)

```json
{
    "policy": {
        "policyVersion": "1.0",
        "policyName": "SiVa validation policy",
        "policyDescription": "SiVa validation policy version 1",
        "policyUrl": "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/"
    },
    "signaturesCount": 1,
    "validSignaturesCount": 1,
    "validationTime": "2016-07-28T14:41:45Z",
    "documentName": "sample.bdoc",
    "signatures": [
        {
            "claimedSigningTime": "2016-05-11T10:17:57Z",
            "errors": [],
            "id": "S0",
            "indication": "TOTAL-PASSED",
            "info": {
                "bestSignatureTime": "2016-05-11T10:18:06Z"
            },
            "signatureFormat": "XAdES_BASELINE_LT_TM",
            "signatureLevel": "QES",
            "signatureScopes": [],
            "signedBy": "NURM,AARE,38211015222",
            "subIndication": "",
            "warnings": []
        }
    ]
}
```

### Sample SOAP response (successful scenario)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:ValidateDocumentResponse xmlns:ns2="http://soap.webapp.siva.openeid.ee/">
      <ns2:ValidationReport>
        <Policy>
          <PolicyDescription>SiVa validation policy</PolicyDescription>
          <PolicyName>SiVa validation policy</PolicyName>
          <PolicyUrl>http://open-eid.github.io/SiVa/siva/appendix/validation_policy/</PolicyUrl>
          <PolicyVersion>1.0</PolicyVersion>
        </Policy>
        <ValidationTime>2016-08-31T16:26:20Z</ValidationTime>
        <DocumentName>default.bdoc</DocumentName>
        <SignatureForm>ASiC_E</SignatureForm>
        <Signatures>
          <Signature>
            <Id>S0</Id>
            <SignatureFormat>XAdES_BASELINE_LT_TM</SignatureFormat>
            <SignatureLevel>QES</SignatureLevel>
            <SignedBy>NURM,AARE,38211015222</SignedBy>
            <Indication>TOTAL_PASSED</Indication>
            <SubIndication/>
            <Errors/>
            <SignatureScopes>
              <SignatureScope>
                <Name>lama.jpg</Name>
                <Scope>FullSignatureScope</Scope>
                <Content>Full document</Content>
              </SignatureScope>
            </SignatureScopes>
            <ClaimedSigningTime>2013-11-25T13:16:42Z</ClaimedSigningTime>
            <Warnings/>
            <Info>
              <bestSignatureTime>2013-11-25T13:16:59Z</bestSignatureTime>
            </Info>
          </Signature>
        </Signatures>
        <ValidSignaturesCount>1</ValidSignaturesCount>
        <SignaturesCount>1</SignaturesCount>
      </ns2:ValidationReport>
    </ns2:ValidateDocumentResponse>
  </soap:Body>
</soap:Envelope>
```

### Sample JSON response (error situation)

```json
{"requestErrors": [{
    "message": "document malformed or not matching documentType",
    "key": "document"
}]}
```

### Sample SOAP response (error situation)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <soap:Fault>
      <faultcode>soap:Server</faultcode>
      <faultstring>document malformed or not matching documentType</faultstring>
    </soap:Fault>
  </soap:Body>
</soap:Envelope>
```