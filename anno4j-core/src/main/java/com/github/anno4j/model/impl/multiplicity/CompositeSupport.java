package com.github.anno4j.model.impl.multiplicity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

import com.github.anno4j.model.impl.ResourceObject;
import org.apache.commons.io.IOUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.rio.*;

@Partial
public abstract class CompositeSupport extends ResourceObjectSupport implements Composite {

    @Override
    public void addItem(RDFObject item) {
        if(item == null){
            return;
        }

        HashSet<RDFObject> items = new HashSet<>();

        if(this.getItems() != null) {
            items.addAll(this.getItems());
        }

        items.add(item);
        this.setItems(items);
    }

    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
            this.getObjectConnection().exportStatements(this.getResource(), null, null, true, writer);

            if(this.getItems() != null) {
                for(RDFObject item : this.getItems()) {
                    if(item instanceof ResourceObject) {
                        parser.parse(IOUtils.toInputStream( ((ResourceObject)item).getTriples(format)), "");
                    }
                }
            }
        } catch (IOException | RDFHandlerException | RDFParseException | RepositoryException e) {
            e.printStackTrace();
        }

        return out.toString();
    }
}
