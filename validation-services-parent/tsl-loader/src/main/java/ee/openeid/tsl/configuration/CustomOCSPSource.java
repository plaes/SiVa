package ee.openeid.tsl.configuration;


import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSRevocationUtils;
import eu.europa.esig.dss.client.http.DataLoader;
import eu.europa.esig.dss.client.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.client.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.RevocationToken;
import eu.europa.esig.dss.x509.ocsp.OCSPRespStatus;
import eu.europa.esig.dss.x509.ocsp.OCSPSource;
import eu.europa.esig.dss.x509.ocsp.OCSPToken;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Security;


public class CustomOCSPSource implements OCSPSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOCSPSource.class);

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private DataLoader dataLoader = new OCSPDataLoader();

    public CustomOCSPSource() {
    }

    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public OCSPToken getOCSPToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
        if (this.dataLoader == null) {
            throw new NullPointerException("DataLoader is not provided !");
        } else {
            try {
                String dssIdAsString = certificateToken.getDSSIdAsString();
                LOGGER.trace("--> OnlineOCSPSource queried for " + dssIdAsString);

                CertificateID certId = DSSRevocationUtils.getOCSPCertificateID(certificateToken, issuerCertificateToken);

                RevocationToken revocationToken = certificateToken.getRevocationTokens().stream().findFirst().orElse(null);
                if (revocationToken == null)
                    return new OnlineOCSPSource().getOCSPToken(certificateToken, issuerCertificateToken);
                byte[] ocspRespBytes = revocationToken.getEncoded();
                if (Utils.isArrayEmpty(ocspRespBytes)) {
                    return new OnlineOCSPSource().getOCSPToken(certificateToken, issuerCertificateToken);
                } else {
                    OCSPResp ocspResp;
                    try {
                        ocspResp = new OCSPResp(ocspRespBytes);
                    } catch (IOException e) {
                        return new OnlineOCSPSource().getOCSPToken(certificateToken, issuerCertificateToken);
                    }
                    OCSPRespStatus status = OCSPRespStatus.fromInt(ocspResp.getStatus());
                    if (OCSPRespStatus.SUCCESSFUL.equals(status)) {
                        OCSPToken ocspToken = new OCSPToken();
                        ocspToken.setResponseStatus(status);

                        ocspToken.setCertId(certId);
                        ocspToken.setAvailable(true);
                        BasicOCSPResp basicOCSPResp = (BasicOCSPResp) ocspResp.getResponseObject();
                        ocspToken.setBasicOCSPResp(basicOCSPResp);

                        return ocspToken;
                    } else {
                        certificateToken.extraInfo().infoOCSPException("OCSP Response status : " + status);
                        return null;
                    }
                }

            } catch (OCSPException ex) {
                throw new DSSException(ex);
            }
        }
    }
}