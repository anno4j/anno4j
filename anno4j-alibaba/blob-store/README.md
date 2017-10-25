Blob Store
==========
 
 The Blob Store is a concurrent optimistic
 isolation key/value store for binary streams.

 The Blob Store can be used independently of an RDF store. To get started use the BlobStoreFactory to open a BlobStore for a given directory. The binary streams will be stored within this directory. Once a BlobStore is created use the BlobStore#open(String) method to obtain a BlobObject. Each BlobObject includes openInputStream() and openOutputStream() methods to read and write to the stream.
 
Figure 1. Reading and writing Blob streams

    BlobStoreFactory factory = BlobStoreFactory.newInstance();
    BlobStore store = factory.openBlobStore(new File("."));
    
    String key = "http://example.com/store1/key1";
    BlobObject blob = store.open(key);
    
    OutputStream out = blob.openOutputStream();
    try {
        // write stream to out
    } finally {
        out.close();
    }
    
    InputStream in = blob.openInputStream();
    try {
        // read stream from in
    } finally {
        in.close();
    }

 Changes to a blob stream are not available for reading until the OutputStream is closed (committed). Any calls to openInputStream on a blob will read the complete committed version until an OutputStream is closed and committed.

 To commit multiple blob at once, that is a blob version is available to read iff another blob of the same version is available, use the BlobStore#newVersion() or BlobStore#newVersion(String) methods. Within the pending new version multiple BlobObjects can be opened for writing and only when all BlobObject streams are closed and the BlobVersion#commit() method is called will the new blob streams be available for reading.
 
 The committed version of the blob can be read from the method BlobObject#getComittedVersion(), the behaviour of this method is independent of any BlobVersion. To read a list of past versions of BlobObject use the BlobObject#getRecentVersions(). Previous blob versions can be opened using the BlobStore#openVersion(String) method to open a previous version. From there the BlobVersion#getModifications() can be used to list what other blob streams were changed at the same time. Naturally, the BlobVersion#open(String) method will provide access to the Blob stream of this version, if this blob was modified in this version.
 
