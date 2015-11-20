/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.TokenId;
import lu.nowina.nexu.flow.operation.AdvancedCreationFeedbackOperation;
import lu.nowina.nexu.flow.operation.CreateTokenOperation;
import lu.nowina.nexu.flow.operation.GetTokenConnectionOperation;
import lu.nowina.nexu.flow.operation.OperationResult;
import lu.nowina.nexu.flow.operation.OperationStatus;
import lu.nowina.nexu.flow.operation.SelectPrivateKeyOperation;
import lu.nowina.nexu.flow.operation.TokenOperationResultKey;
import lu.nowina.nexu.view.core.UIDisplay;
import lu.nowina.nexu.view.core.UIOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.x509.CertificateToken;

class GetCertificateFlow extends Flow<GetCertificateRequest, GetCertificateResponse> {

	static final Logger logger = LoggerFactory.getLogger(GetCertificateFlow.class);

	public GetCertificateFlow(UIDisplay display) {
		super(display);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected GetCertificateResponse process(NexuAPI api, GetCertificateRequest req) {
		SignatureTokenConnection token = null;
		try {
			final OperationResult<Map<TokenOperationResultKey, Object>> createTokenOperationResult =
					getOperationFactory().getOperation(CreateTokenOperation.class, api).perform();
			if (createTokenOperationResult.getStatus().equals(OperationStatus.SUCCESS)) {
				final Map<TokenOperationResultKey, Object> map = createTokenOperationResult.getResult();
				final TokenId tokenId = (TokenId) map.get(TokenOperationResultKey.TOKEN_ID);
				
				final OperationResult<SignatureTokenConnection> getTokenConnectionOperationResult =
						getOperationFactory().getOperation(GetTokenConnectionOperation.class, api, tokenId).perform();
				if (getTokenConnectionOperationResult.getStatus().equals(OperationStatus.SUCCESS)) {
					token = getTokenConnectionOperationResult.getResult();

					final OperationResult<DSSPrivateKeyEntry> selectPrivateKeyOperationResult =
							getOperationFactory().getOperation(SelectPrivateKeyOperation.class, token).perform();
					if (selectPrivateKeyOperationResult.getStatus().equals(OperationStatus.SUCCESS)) {
						final DSSPrivateKeyEntry key = selectPrivateKeyOperationResult.getResult();

						if ((Boolean) map.get(TokenOperationResultKey.ADVANCED_CREATION)) {
							getOperationFactory().getOperation(AdvancedCreationFeedbackOperation.class,
									api, map).perform();
						}

						final GetCertificateResponse resp = new GetCertificateResponse();
						resp.setTokenId(tokenId);

						final CertificateToken certificate = key.getCertificate();
						resp.setCertificate(certificate.getBase64Encoded());
						resp.setKeyId(certificate.getDSSIdAsString());
						resp.setEncryptionAlgorithm(certificate.getEncryptionAlgorithm());

						final CertificateToken[] certificateChain = key.getCertificateChain();
						if (certificateChain != null) {
							final List<String> listCertificates = new ArrayList<String>();
							for (CertificateToken certificateToken : certificateChain) {
								listCertificates.add(certificateToken.getBase64Encoded());
							}
							resp.setCertificateChain(listCertificates);
						}

						return resp;
					}
				}
			}

			getOperationFactory().getOperation(UIOperation.class, getDisplay(), "/fxml/message.fxml",
					new Object[]{"Finished"}).perform();
		} catch (final Exception e) {
			logger.error("Flow error", e);
			final Feedback feedback = new Feedback(e);
			getOperationFactory().getOperation(
					UIOperation.class, getDisplay(), "/fxml/provide-feedback.fxml",
					new Object[]{feedback}).perform();
			getOperationFactory().getOperation(UIOperation.class, getDisplay(), "/fxml/message.fxml",
					new Object[]{"Failure"}).perform();
		} finally {
			if(token != null) {
				token.close();
			}
		}

		return null;
	}

}
