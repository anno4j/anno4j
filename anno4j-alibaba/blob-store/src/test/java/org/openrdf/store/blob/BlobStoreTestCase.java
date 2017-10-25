package org.openrdf.store.blob;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public abstract class BlobStoreTestCase extends TestCase {
	protected BlobStore store;
	protected File dir;
	protected AssertionFailedError error;

	public abstract BlobStore createBlobStore(File dir) throws IOException;

	public void setUp() throws Exception {
		String tmpDirStr = System.getProperty("java.io.tmpdir");
		if (tmpDirStr != null) {
			File tmpDir = new File(tmpDirStr);
			if (!tmpDir.exists()) {
				tmpDir.mkdirs();
			}
		}
		dir = File.createTempFile("store", "");
		dir.delete();
		dir.mkdirs();
		store = createBlobStore(dir);
		error = null;
	}

	public void tearDown() throws Exception {
		store.erase();
		dir.delete();
	}

	public void testEraseEmpty() throws Exception {
		store.erase();
		assertEmpty(dir);
	}

	public void testEraseSingleBlob() throws Exception {
		BlobVersion trx1 = store.newVersion("urn:test:trx1");
		Writer file = trx1.open("urn:test:file").openWriter();
		file.append("blob store test");
		file.close();
		trx1.commit();
		store.erase();
		assertEmpty(dir);
	}

	public void testEraseMultiVersionSingleBlob() throws Exception {
		Writer file = store.open("urn:test:file").openWriter();
		file.append("blob store test1");
		file.close();
		file = store.open("urn:test:file").openWriter();
		file.append("blob store test2");
		file.close();
		store.erase();
		assertEmpty(dir);
	}

	public void testRoundTripString() throws Exception {
		BlobVersion trx1 = store.newVersion("urn:test:trx1");
		Writer file = trx1.open("urn:test:file").openWriter();
		file.append("blob store test");
		file.close();
		trx1.commit();
		BlobVersion trx2 = store.newVersion("urn:test:trx2");
		CharSequence str = trx2.open("urn:test:file").getCharContent(true);
		assertEquals("blob store test", str.toString());
	}

	public void testReuseVersion() throws Exception {
		BlobVersion trx1 = store.newVersion("urn:test:trx1");
		Writer file1 = trx1.open("urn:test:file1").openWriter();
		file1.append("blob store test");
		file1.close();
		trx1.commit();
		BlobVersion trx2 = store.newVersion("urn:test:trx2");
		file1 = trx2.open("urn:test:file1").openWriter();
		file1.append("blob store test");
		file1.close();
		trx2.commit();
		Writer file2 = trx2.open("urn:test:file2").openWriter();
		file2.append("blob store test");
		file2.close();
		trx2.commit();
		trx2 = store.newVersion("urn:test:trx2");
		file2 = trx2.open("urn:test:file2").openWriter();
		file2.append("blob store test");
		file2.close();
		trx2.commit();
		BlobVersion trx3 = store.newVersion("urn:test:trx3");
		CharSequence str1 = trx3.open("urn:test:file1").getCharContent(true);
		assertEquals("blob store test", str1.toString());
		CharSequence str2 = trx3.open("urn:test:file2").getCharContent(true);
		assertEquals("blob store test", str2.toString());
	}

	public void testReuseVersionWithDelete() throws Exception {
		BlobVersion trx1 = store.newVersion("urn:test:trx1");
		Writer file1 = trx1.open("urn:test:file1").openWriter();
		file1.append("blob store test");
		file1.close();
		trx1.commit();
		BlobVersion trx2 = store.newVersion("urn:test:trx2");
		trx2.open("urn:test:file1").delete();
		trx2.commit();
		trx2 = store.newVersion("urn:test:trx2");
		Writer file2 = trx2.open("urn:test:file2").openWriter();
		file2.append("blob store test");
		file2.close();
		trx2.commit();
		trx2 = store.newVersion("urn:test:trx2");
		trx2.open("urn:test:file2").delete();
		trx2.commit();
		BlobVersion trx3 = store.newVersion("urn:test:trx3");
		CharSequence str1 = trx3.open("urn:test:file1").getCharContent(true);
		assertNull(str1);
		CharSequence str2 = trx3.open("urn:test:file2").getCharContent(true);
		assertNull(str2);
	}

	public void testAutocommit() throws Exception {
		Writer file = store.open("urn:test:file").openWriter();
		file.append("blob store test");
		file.close();
		CharSequence str = store.open("urn:test:file").getCharContent(true);
		assertEquals("blob store test", str.toString());
	}

	public void testConcurrency() throws Exception {
		Writer test1 = store.open("urn:test:file").openWriter();
		test1.append("test1");
		test1.close();
		Writer test2 = store.open("urn:test:file").openWriter();
		test2.append("test2");
		test2.flush();
		assertEquals("test1", store.open("urn:test:file").getCharContent(true).toString());
		test2.close();
		assertEquals("test2", store.open("urn:test:file").getCharContent(true).toString());
	}

	public void testReopenInvalid() throws Exception {
		try {
			store.openVersion("urn:test:nothing");
			fail();
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	public void testAtomicity() throws Exception {
		BlobVersion trx1 = store.newVersion("urn:test:trx1");
		Writer file1 = trx1.open("urn:test:file1").openWriter();
		file1.append("blob store test");
		file1.close();
		Writer file2 = trx1.open("urn:test:file2").openWriter();
		file2.append("blob store test");
		file2.close();
		BlobVersion trx2 = store.newVersion("urn:test:trx2");
		assertNull(trx2.open("urn:test:file1").getCharContent(true));
		assertNull(trx2.open("urn:test:file2").getCharContent(true));
		trx1.commit();
		BlobVersion trx3 = store.newVersion("urn:test:trx3");
		assertEquals("blob store test", trx3.open("urn:test:file1")
				.getCharContent(true).toString());
		assertEquals("blob store test", trx3.open("urn:test:file2")
				.getCharContent(true).toString());
	}

	public void testIsolation() throws Exception {
		BlobVersion trx1 = store.newVersion("urn:test:trx1");
		Writer file1 = trx1.open("urn:test:file1").openWriter();
		file1.append("blob store test");
		file1.close();
		final CountDownLatch latch1 = new CountDownLatch(1);
		new Thread(new Runnable() {
			public void run() {
				try {
					error = null;
					try {
						BlobVersion trx2 = store.newVersion("urn:test:trx2");
						BlobObject blob = trx2.open("urn:test:file1");
						assertNull(blob.getCharContent(true));
					} catch (Exception e) {
						e.printStackTrace();
						fail();
					} finally {
						latch1.countDown();
					}
				} catch (AssertionFailedError e) {
					error = e;
				}
			}
		}).start();
		latch1.await();
		if (error != null)
			throw error;
		trx1.prepare();
		final CountDownLatch latch2 = new CountDownLatch(1);
		final CountDownLatch latch3 = new CountDownLatch(1);
		new Thread(new Runnable() {
			public void run() {
				try {
					error = null;
					try {
						latch2.countDown();
						BlobVersion trx3 = store.newVersion("urn:test:trx3");
						BlobObject blob = trx3.open("urn:test:file1");
						CharSequence str = blob.getCharContent(true);
						assertNotNull(str);
						assertEquals("blob store test", str.toString());
					} catch (Exception e) {
						e.printStackTrace();
						fail();
					} finally {
						latch3.countDown();
					}
				} catch (AssertionFailedError e) {
					error = e;
				}
			}
		}).start();
		latch2.await();
		assertFalse(latch3.await(1, TimeUnit.SECONDS));
		trx1.commit();
		latch3.await();
		if (error != null)
			throw error;
	}

	protected void assertEmpty(File dir) {
		assertEquals(dir.getName() + "/", tree(dir, 0).toString());
	}

	private CharSequence tree(File dir, int depth) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			sb.append(" ");
		}
		sb.append(dir.getName());
		if (dir.isDirectory()) {
			sb.append("/");
			for (File file : dir.listFiles()) {
				sb.append("\n").append(tree(file, depth + 1));
			}
		}
		return sb;
	}

}
