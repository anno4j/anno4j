package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.annotations.Partial;
import org.apache.commons.io.IOUtils;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

@Partial
public abstract class AnnotationSupport extends ResourceObjectSupport implements Annotation {

    @Override
    public void addTarget(Target target) {
        if (this.getTarget() == null) {
            this.setTarget(new HashSet<Target>());
        }

        this.getTarget().add(target);
    }

    /**
     * Method returns a textual representation of the given Annotation, containing
     * its Body, Target and possible Selection, in a supported serialisation format.
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
            StringBuilder sb = new StringBuilder();
            sb.append(super.getTriples(RDFFormat.NTRIPLES));

            if (getBody() != null) {
                sb.append(getBody().getTriples(RDFFormat.NTRIPLES));
            }

            if (getTarget() != null) {
                for (Target target : getTarget()) {
                    sb.append(target.getTriples(RDFFormat.NTRIPLES));
                }
            }

            if(getMotivatedBy() != null) {
               sb.append(getMotivatedBy().getTriples(RDFFormat.NTRIPLES));
            }

            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds) {

        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(year)).append("-").
                append(Integer.toString(month)).append("-").
                append(Integer.toString(day)).append("T");

        if(hours < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(hours));

        builder.append(":");

        if(minutes < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(minutes));

        builder.append(":");

        if(seconds < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(seconds));

        builder.append("Z");

        this.setSerializedAt(builder.toString());
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setAnnotatedAt(int year, int month, int day, int hours, int minutes, int seconds) {

        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(year)).append("-").
                append(Integer.toString(month)).append("-").
                append(Integer.toString(day)).append("T");

        if(hours < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(hours));

        builder.append(":");

        if(minutes < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(minutes));

        builder.append(":");

        if(seconds < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(seconds));

        builder.append("Z");

        this.setAnnotatedAt(builder.toString());
    }
}
