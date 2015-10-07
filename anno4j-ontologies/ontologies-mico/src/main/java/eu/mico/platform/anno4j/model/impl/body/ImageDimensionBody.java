package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;import java.lang.Double;import java.lang.Long;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Iri(MICO.IMAGE_DIMENSION_BODY)
public interface ImageDimensionBody extends Body {

    @Iri(MICO.HAS_HEIGHT)
    public Long getHeight();

    @Iri(MICO.HAS_HEIGHT)
    public void setHeight(Long height);

    @Iri(MICO.HAS_CONFIDENCE)
    public Double getConfidence();

    @Iri(MICO.HAS_CONFIDENCE)
    public void setConfidence(Double confidence);

    @Iri(MICO.HAS_WIDTH)
    public Long getWidth();

    @Iri(MICO.HAS_WIDTH)
    public void setWidth(Long width);

}
