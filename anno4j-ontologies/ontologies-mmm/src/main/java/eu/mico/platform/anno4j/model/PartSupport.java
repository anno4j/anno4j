package eu.mico.platform.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.model.Target;
import org.openrdf.repository.object.RDFObject;

import java.util.HashSet;

/**
 * Support class for the Part.
 */
@Partial
public abstract class PartSupport extends AnnotationSupport implements Part {

    @Override
    public void addTarget(Target target) {
        if (this.getTarget() == null) {
            this.setTarget(new HashSet<Target>());
        }

        this.getTarget().add(target);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void addInput(RDFObject input) {
        if (this.getInputs() == null) {
            this.setInputs(new HashSet<RDFObject>());
        }

        this.getInputs().add(input);
    }
}
