package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.SelectorSupport;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import com.github.anno4j.annotations.Partial;
import org.apache.commons.io.IOUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Partial
public abstract class SvgSelectorSupport extends SelectorSupport implements SvgSelector {
    @Override
    public String getConformsTo() {
        return FragmentSpecification.SVG.toString();
    }

    /**
     * Method returns a textual representation of the given ResourceObject in a supported serialisation format.
     *
     * @param format The format which should be printed.
     * @return A textual representation if this object in the format.
     */
    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
            this.getObjectConnection().exportStatements(this.getResource(), null, null, true, writer);

            if(this.getRefinedSelectors() != null && !this.getRefinedSelectors().isEmpty()) {
                for(Selector selector : this.getRefinedSelectors()) {
                    parser.parse(IOUtils.toInputStream(selector.getTriples(RDFFormat.NTRIPLES), "UTF-8"), "");
                }
            }

        } catch (IOException | RDFHandlerException | RDFParseException | RepositoryException e) {
            e.printStackTrace();
        }

        return out.toString();
    }
}
