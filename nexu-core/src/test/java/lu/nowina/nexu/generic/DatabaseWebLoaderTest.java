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
package lu.nowina.nexu.generic;

import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import lu.nowina.nexu.NexuLauncher;
import lu.nowina.nexu.api.AppConfig;

public class DatabaseWebLoaderTest {

	@Test
	public void test1() throws Exception {

		HttpDataLoader httpLoader = Mockito.mock(HttpDataLoader.class);

		new NexuLauncher().loadAppConfig(new Properties());

		DatabaseWebLoader loader = new DatabaseWebLoader(NexuLauncher.getConfig(), httpLoader);
		loader.start();
		Thread.sleep(3000);
		loader.stop();

	}

}
