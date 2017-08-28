package com.github.anno4j.model.impl.multiplicity;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Precedes;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

@Partial
@Precedes(ResourceObjectSupport.class)
public abstract class ChoiceSupport extends ResourceObjectSupport implements Choice {

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
            StringBuilder sb = new StringBuilder();

            sb.append(super.getTriples(RDFFormat.NTRIPLES));

            if (this.getItems() != null) {
                for (RDFObject object : this.getItems()) {
                    ResourceObject objectCast = (ResourceObject) object;

                    sb.append(objectCast.getTriples(RDFFormat.NTRIPLES));
                }
            }

            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch (IOException | RDFHandlerException | RDFParseException e) {
            e.printStackTrace();
        }

        return out.toString();
    }
    
}
