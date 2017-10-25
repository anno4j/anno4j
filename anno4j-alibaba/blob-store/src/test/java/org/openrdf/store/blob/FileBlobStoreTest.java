package org.openrdf.store.blob;

import java.io.File;
import java.io.IOException;

import org.openrdf.store.blob.file.FileBlobStore;

public class FileBlobStoreTest extends BlobStoreTestCase {

	@Override
	public BlobStore createBlobStore(File dir) throws IOException {
		return new FileBlobStore(dir);
	}

}
