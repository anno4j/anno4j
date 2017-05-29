package org.openrdf.repository.object;

import info.aduna.io.FileUtil;

import java.io.File;

import junit.framework.TestCase;

import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;

public class RepositoryProviderTest extends TestCase {

	public void testFileURI() throws Exception {
		File dir = FileUtil.createTempDir(RepositoryProviderTest.class.getSimpleName());
		try {
			RepositoryManager byfile = RepositoryProvider.getRepositoryManager(dir);
			RepositoryManager byurl = RepositoryProvider.getRepositoryManager(dir.toURI().getRawPath());
			try {
				assertEquals(byfile, byurl);
			} finally {
				byfile.shutDown();
				byurl.shutDown();
			}
		} finally {
			FileUtil.deleteDir(dir);
		}
	}
}
