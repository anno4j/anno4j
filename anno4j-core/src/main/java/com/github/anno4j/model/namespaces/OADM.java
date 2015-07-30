package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the Open Annotation Data Model (oa:).
 * See <a href="http://www.w3.org/ns/oa#">http://www.w3.org/ns/oa#</a>
 */
public class OADM {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/ns/oa#";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "oa";

    /**
     * ---------- Classes ----------
     */

    /**
     * Refers to http://www.w3.org/ns/oa#Annotation
     * Typically an Annotation has a single Body (oa:hasBody), which is the comment or other descriptive resource, and a single Target (oa:hasTarget) that the Body is somehow "about". The Body provides the information which is annotating the Target.
     * This "aboutness" may be further clarified or extended to notions such as classifying or identifying with oa:motivatedBy.
     */
    public final static String ANNOTATION = NS + "Annotation";

    /**
     * Refers to http://www.w3.org/ns/oa#Tag
     * A class assigned to the Body when it is a tag, such as a embedded text string with cnt:chars.
     * Tags are typically keywords or labels, and used for organization, description or discovery of the resource being tagged. In the Semantic Web, URIs are used instead of strings to avoid the issue of polysemy where one word has multiple meanings, such usage MUST be indicated using the subclass oa:SemanticTag.
     * Annotations that tag resources, either with text or semantic tags, SHOULD also have the oa:tagging motivation to make the reason for the Annotation more clear to applications, and MAY have other motivations as well.
     */
    public final static String TAG = NS + "Tag";

    /**
     * Refers to http://www.w3.org/ns/oa#SemanticTag
     * A class assigned to the Body when it is a semantic tagging resource; a URI that identifies a concept, rather than an embedded string, frequently a term from a controlled vocabulary.
     * It is NOT RECOMMENDED to use the URI of a document as a Semantic Tag, as it might also be used as a regular Body in other Annotations which would inherit the oa:SemanticTag class assignment. Instead it is more appropriate to create a new URI and link it to the document, using the foaf:page predicate.
     */
    public final static String SEMANTIC_TAG = NS + "SemanticTag";

    /**
     * Refers to http://www.w3.org/ns/oa#SpecificResource
     * A resource identifies part of another Source resource, a particular representation of a resource, a resource with styling hints for renders, or any combination of these.
     * The Specific Resource takes the role of oa:hasBody or oa:hasTarget in an oa:Annotation instead of the Source resource.
     * There MUST be exactly 1 oa:hasSource relationship associated with a Specific Resource.
     * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource.
     * There MAY be 0 or 1 oa:hasState relationship for each Specific Resource.
     * If the Specific Resource has an HTTP URI, then the exact segment of the Source resource that it identifies, and only the segment, MUST be returned when the URI is dereferenced. For example, if the segment of interest is a region of an image and the Specific Resource has an HTTP URI, then dereferencing it MUST return the selected region of the image as it was at the time when the annotation was created. Typically this would be a burden to support, and thus the Specific Resource SHOULD be identified by a globally unique URI, such as a UUID URN. If it is not considered important to allow other Annotations or systems to refer to the Specific Resource, then a blank node MAY be used instead.
     */
    public final static String SPECIFIC_RESOURCE = NS + "SpecificResource";

    /**
     * Refers to http://www.w3.org/ns/oa#Choice
     * A multiplicity construct that conveys to a consuming application that it should select one of the constituent resources to display to the user, and not render/use all of them.
     * oa:Choice can be used as the object of the object of the oa:hasBody, oa:hasTarget, oa:hasSelector, oa:hasState, oa:styledBy and oa:hasScope relationships,
     * There MUST be 1 or more oa:item relationships for each oa:Choice.
     * There SHOULD be exactly 1 default relationship for each Choice.
     */
    public final static String CHOICE = NS + "Choice";

    /**
     * http://www.w3.org/ns/oa#Composite
     * A multiplicity construct that conveys to a consuming application that all of the constituent resources are required for the Annotation to be correctly interpreted.
     * oa:Composite can be used as the object of the object of the oa:hasBody, oa:hasTarget, oa:hasSelector, oa:hasState, oa:styledBy and oa:hasScope relationships,
     * There MUST be 2 or more oa:item relationships for each oa:Composite.
     */
    public final static String COMPOSITE = NS + "Composite";

    /**
     * Refers to http://www.w3.org/ns/oa#List
     * A multiplicity construct that conveys to a consuming application that all of the constituent resources are required for the Annotation to be correctly interpreted, and in a particular order.
     * oa:List can be used as the object of the object of the oa:hasBody, oa:hasTarget, oa:hasSelector, oa:hasState, oa:styledBy and oa:hasScope relationships,
     * There MUST be 2 or more oa:item relationships for each oa:List, with their order defined using the rdf:List construct of rdf:first/rdf:rest/rdf:nil.
     * All the elements of the list should also be declared using oa:item, and each of the oa:items should appear at least once in the list.
     */
    public final static String LIST = NS + "List";

    // ---------- Motivation ----------

    /**
     * Refers to http://www.w3.org/ns/oa#Motivation
     * The Motivation for creating an Annotation, indicated with oa:motivatedBy, is a reason for its creation, and might include things like oa:replying to another annotation, oa:commenting on a resource, or oa:linking to a related resource.
     * Each Annotation SHOULD have at least one oa:motivatedBy relationship to an instance of oa:Motivation, which is a subClass of skos:Concept.
     */
    public final static String MOTIVATION = NS + "Motivation";

    /**
     * Refers to http://www.w3.org/ns/oa#bookmarking
     * The motivation that represents the creation of a bookmark to the target resources or recorded point or points within one or more resources. For example, an Annotation that bookmarks the point in a text where the reader finished reading. Bookmark Annotations may or may not have a Body resource.
     */
    public final static String MOTIVATION_BOOKMARKING = NS + "bookmarking";

    /**
     * Refers to http://www.w3.org/ns/oa#classifying
     * The motivation that represents the assignment of a classification type, typically from a controlled vocabulary, to the target resource(s). For example to classify an Image resource as a Portrait.
     */
    public final static String MOTIVATION_CLASSIFYING = NS + "classifying";

    /**
     * Refers to http://www.w3.org/ns/oa#commenting
     * The motivation that represents a commentary about or review of the target resource(s). For example to provide a commentary about a particular PDF.
     */
    public final static String MOTIVATION_COMMENTING = NS + "commenting";

    /**
     * Refers to http://www.w3.org/ns/oa#describing
     * The motivation that represents a description of the target resource(s), as opposed to a comment about them. For example describing the above PDF's contents, rather than commenting on their accuracy.
     */
    public final static String MOTIVATION_DESCRIBING = NS + "describing";

    /**
     * Refers to http://www.w3.org/ns/oa#editing
     * The motivation that represents a request for a modification or edit to the target resource. For example, an Annotation that requests a typo to be corrected.
     */
    public final static String MOTIVATION_EDITING = NS + "editing";

    /**
     * Refers to http://www.w3.org/ns/oa#highlighting
     * The motivation that represents a highlighted section of the target resource or segment. For example to draw attention to the selected text that the annotator disagrees with. A Highlight may or may not have a Body resource.
     */
    public final static String MOTIVATION_HIGHLIGHTING = NS + "highlighting";

    /**
     * Refers to http://www.w3.org/ns/oa#identifying
     * The motivation that represents the assignment of an identity to the target resource(s). For example, annotating the name of a city in a string of text with the URI that identifies it.
     */
    public final static String MOTIVATION_IDENTIFYING = NS + "identifying";

    /**
     * Refers to http://www.w3.org/ns/oa#linking
     * The motivation that represents an untyped link to a resource related to the target.
     */
    public final static String MOTIVATION_LINKING = NS + "linking";

    /**
     * Refers to http://www.w3.org/ns/oa#moderating
     * The motivation that represents an assignment of value or quality to the target resource(s). For example annotating an Annotation to moderate it up in a trust network or threaded discussion.
     */
    public final static String MOTIVATION_MODERATING = NS + "moderating";

    /**
     * Refers to http://www.w3.org/ns/oa#questioning
     * The motivation that represents asking a question about the target resource(s). For example to ask for assistance with a particular section of text, or question its veracity.
     */
    public final static String MOTIVATION_QUESTIONING = NS + "questioning";

    /**
     * Refers to http://www.w3.org/ns/oa#replying
     * The motivation that represents a reply to a previous statement, either an Annotation or another resource. For example providing the assistance requested in the above.
     */
    public final static String MOTIVATION_REPLYING = NS + "replying";

    /**
     * Refers to http://www.w3.org/ns/oa#tagging
     * The motivation that represents adding a Tag on the target resource(s). One or more of the bodies of the annotation should be typed as a oa:Tag or oa:SemanticTag.
     */
    public final static String MOTIVATION_TAGGING = NS + "tagging";

    // ---------- Selector ----------

    /**
     * Refers to http://www.w3.org/ns/oa#Selector
     * A resource which describes the segment of interest in a representation of a Source resource, indicated with oa:hasSelector from the Specific Resource.
     * This class is not used directly in Annotations, only its subclasses are.
     * The nature of the Selector will be dependent on the type of the representation for which the segment is conveyed. The specific type of selector should be indicated using a subclass of oa:Selector.
     * The Specifier's description MAY be conveyed as an external or embedded resource (cnt:Content), or as RDF properties within the graph. The description SHOULD use existing standards whenever possible. If the Specifier has an HTTP URI, then its description, and only its description, MUST be returned when the URI is dereferenced.
     */
    public final static String SELECTOR = NS + "Selector";

    /**
     * Refers to http://www.w3.org/ns/oa#FragmentSelector
     * A Selector which describes the segment of interest in a representation, through the use of the fragment identifier component of a URI.
     * It is RECOMMENDED to use oa:FragmentSelector as the selector on a Specific Resource rather than annotating the fragment URI directly, in order to improve discoverability of annotation on the Source.
     * The oa:FragmentSelector MUST have exactly 1 rdf:value property, containing the fragment identifier component of a URI that describes the segment of interest in the resource, excluding the initial "#".
     * The Fragment Selector SHOULD have a dcterms:conformsTo relationship with the object being the specification that defines the syntax of the fragment, for instance <http://tools.ietf.org/rfc/rfc3236> for HTML fragments.
     */
    public final static String SELECTOR_FRAGMENT = NS + "FragmentSelector";

    /**
     * Refers to http://www.w3.org/ns/oa#SvgSelector
     * A Selector which selects an area specified as an SVG shape.
     * The SVG document should either be retrievable by resolving the URI of this resource, or be included as an Embedded Resource using cnt:Content.
     * It is RECOMMENDED that the document contain only a single shape element and that element SHOULD be one of: path, rect, circle, ellipse, polyline, polygon or g. The g element SHOULD ONLY be used to construct a multi-element group, for example to define a donut shape requiring an outer circle and a clipped inner circle.
     * The dimensions of both the shape and the SVG canvas MUST be relative to the dimensions of the Source resource. For example, given an image which is 600 pixels by 400 pixels, and the desired section is a circle of 100 pixel radius at the center of the image, then the SVG element would be: <circle cx="300" cy="200" r="100"/>
     * It is NOT RECOMMENDED to include style information within the SVG element, nor Javascript, animation, text or other non shape oriented information. Clients SHOULD ignore such information if present.
     */
    public final static String SELECTOR_SVG = NS + "SvgSelector";

    /**
     * Refers to http://www.w3.org/ns/oa#DataPositionSelector
     * A Selector which describes a range of data based on its start and end positions within the byte stream of the representation.
     * Each DataPositionSelector MUST have exactly 1 oa:start property.
     * Each TextPositionSelector MUST have exactly 1 oa:end property.
     * See oa:TextPositionSelector for selection at normalized character level rather than bytestream level.
     */
    public final static String SELECTOR_DATA_POSITION = NS + "DataPositionSelector";

    /**
     * Refers to http://www.w3.org/ns/oa#TextPositionSelector
     * An oa:Selector which describes a range of text based on its start and end positions.
     * The text MUST be normalized before counting characters. For a Selector that works from the bitstream rather than the rendered characters, see oa:DataPositionSelector.
     * Each oa:TextPositionSelector MUST have exactly 1 oa:start property.
     * Each oa:TextPositionSelector MUST have exactly 1 oa:end property.
     */
    public final static String SELECTOR_TEXT_POSITION = NS + "TextPositionSelector";

    /**
     * Refers to http://www.w3.org/ns/oa#TextQuoteSelector
     * A Selector that describes a textual segment by means of quoting it, plus passages before or after it.
     * For example, if the document were "abcdefghijklmnopqrstuvwxyz", one could select "efg" by a oa:prefix of "abcd", the quotation of oa:exact "efg" and a oa:suffix of "hijk".
     * The text MUST be normalized before recording.
     * Each TextQuoteSelector MUST have exactly 1 oa:exact property.
     * Each TextQuoteSelector SHOULD have exactly 1 oa:prefix property, and MUST NOT have more than 1.
     * Each TextQuoteSelector SHOULD have exactly 1 oa:suffix property, and MUST NOT have more than 1.
     */
    public final static String SELECTOR_TEXT_QUOTE = NS + "TextQuoteSelector";

    /**
     * ---------- Relationships ----------
     */

    /**
     * Refers to http://www.w3.org/ns/oa#hasBody
     * The relationship between oa:Annotation and body. The body is somehow "about" the oa:hasTarget of the annotation.
     * The Body may be of any media type, and contain any type of content. The Body SHOULD be identified by HTTP URIs unless they are embedded within the Annotation.
     * Embedded bodies SHOULD be instances of cnt:ContentAsText and embed their content with cnt:chars. They SHOULD declare their media type with dc:format, and MAY indicate their language using dc:language and a RFC-3066 language tag.
     * There is no OA class provided for "Body" as a body might be a target of a different annotation. However, there SHOULD be 1 or more content-based classes associated with the body resources of an Annotation, and the dctypes: vocabulary is recommended for this purpose, for instance dctypes:Text to declare textual content.
     */
    public final static String HAS_BODY = NS + "hasBody";

    /**
     * Refers to http://www.w3.org/ns/oa#hasTarget
     * The relationship between oa:Annotation and target. The target resource is what the oa:hasBody is somewhat "about".
     * The target may be of any media type, and contain any type of content. The target SHOULD be identified by HTTP URIs unless they are embedded within the Annotation.
     * Embedded targets SHOULD be instances of cnt:ContentAsText and embed their content with cnt:chars. They SHOULD declare their media type with dc:format, and MAY indicate their language using dc:language and a RFC-3066 language tag.
     * There is no OA class provided for "Target" as a target might be a body in a different annotation. However, there SHOULD be 1 or more content-based classes associated with the target resources of an Annotation, and the dctypes: vocabulary is recommended for this purpose, for instance dctypes:Text to declare textual content.
     */
    public final static String HAS_TARGET = NS + "hasTarget";

    /**
     * Refers to http://www.w3.org/ns/oa#annotatedBy
     * The object of the relationship is a resource that identifies the agent responsible for creating the Annotation. This may be either a human or software agent.
     * There SHOULD be exactly 1 oa:annotatedBy relationship per Annotation, but MAY be 0 or more than 1, as the Annotation may be anonymous, or multiple agents may have worked together on it.
     * It is RECOMMENDED to use these and other FOAF terms to describe agents: foaf:Person, prov:SoftwareAgent, foaf:Organization, foaf:name, foaf:mbox, foaf:openid, foaf:homepage
     */
    public final static String ANNOTATED_BY = NS + "annotatedBy";

    /**
     * Refers to http://www.w3.org/ns/oa#serializedBy
     * The object of the relationship is the agent, likely software, responsible for generating the serialization of the Annotation's serialization.
     * It is RECOMMENDED to use these and other FOAF terms to describe agents: foaf:Person, prov:SoftwareAgent, foaf:Organization, foaf:name, foaf:mbox, foaf:openid, foaf:homepage
     * There MAY be 0 or more oa:serializedBy relationships per Annotation.
     */
    public final static String SERIALIZED_BY = NS + "serializedBy";

    /**
     * Refers to http://www.w3.org/ns/oa#hasSelector
     * The relationship between a oa:SpecificResource and a oa:Selector.
     * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource.
     * If multiple Selectors are required, either to express a choice between different optional, equivalent selectors, or a chain of selectors that should all be processed, it is necessary to use oa:Choice, oa:Composite or oa:List as a selector.
     */
    public final static String HAS_SELECTOR = NS + "hasSelector";

    /**
     * Refers to http://www.w3.org/ns/oa#hasSource
     * The relationship between a oa:SpecificResource and the resource that it is a more specific representation of.
     * There MUST be exactly 1 oa:hasSource relationship associated with a Specific Resource.
     */
    public final static String HAS_SOURCE = NS + "hasSource";

    /**
     * Refers to http://www.w3.org/ns/oa#hasScope
     * The relationship between a Specific Resource and the resource that provides the scope or context for it in this Annotation.
     * There MAY be 0 or more hasScope relationships for each Specific Resource.
     */
    public final static String HAS_SCOPE = NS + "hasScope";

    /**
     * Refers to http://www.w3.org/ns/oa#motivatedBy
     * The relationship between an Annotation and a Motivation, indicating the reasons why the Annotation was created.
     * Each Annotation SHOULD have at least one oa:motivatedBy relationship, and MAY be more than 1.
     */
    public final static String MOTIVATED_BY = NS + "motivatedBy";

    /**
     * Refers to http://www.w3.org/ns/oa#item
     * The relationship between a multiplicity construct node and its constituent resources.
     * There MUST be 1 or more item relationships for each multiplicity construct oa:Choice, oa:Composite and oa:List.
     */
    public final static String ITEM = NS + "item";

    /**
     * The members of an oa:Choice element.
     */
    public final static String MEMBERS = NS + "members";

    /**
     * ---------- Data Properties ----------
     */

    /**
     * Refers to http://www.w3.org/ns/oa#serializedAt
     * The time at which the agent referenced by oa:serializedBy generated the first serialization of the Annotation, and any subsequent substantially different one. The annotation graph MUST have changed for this property to be updated, and as such represents the last modified datestamp for the Annotation. This might be used to determine if it should be re-imported into a triplestore when discovered.
     * There MAY be exactly 1 oa:serializedAt property per Annotation, and MUST NOT be more than 1. The datetime MUST be expressed in the xsd:dateTime format, and SHOULD have a timezone specified.
     */
    public final static String SERIALIZED_AT = NS + "serializedAt";

    /**
     * Refers to http://www.w3.org/ns/oa#annotatedAt
     * The time at which the Annotation was created.
     * There SHOULD be exactly 1 oa:annotatedAt property per Annotation, and MUST NOT be more than 1. The datetime MUST be expressed in the xsd:dateTime format, and SHOULD have a timezone specified.
     */
    public final static String ANNOTATED_AT = NS + "annotatedAt";

    /**
     * Refers to http://www.w3.org/ns/oa#end
     * The end position of the segment of text or bytes. The first character/byte in the full text/stream is position 0. The character/byte indicated at position oa:end is NOT included within the selected segment.
     * See oa:DataPositionSelector and oa:oa:TextPositionSelector.
     */
    public final static String END = NS + "end";

    /**
     * Refers to http://www.w3.org/ns/oa#exact
     * A copy of the text which is being selected, after normalization.
     * See oa:TextQuoteSelector.
     */
    public final static String EXACT = NS + "exact";

    /**
     * Refers to http://www.w3.org/ns/oa#start
     * The starting position of the segment of text or bytes. The first character/byte in the full text/stream is position 0. The character/byte indicated at position oa:start is included within the selected segment.
     * See oa:DataPositionSelector and oa:TextPositionSelector.
     */
    public final static String START = NS + "start";

    /**
     * Refers to http://www.w3.org/ns/oa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * See oa:TextQuoteSelector.
     */
    public final static String TEXT_PREFIX = NS + "prefix";

    /**
     * Refers to http://www.w3.org/ns/oa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * See oa:TextQuoteSelector.
     */
    public final static String SUFFIX = NS + "suffix";

    /**
     * Refers to http://www.w3.org/ns/oa#when
     * The timestamp at which the Source resource should be interpreted for the Annotation, typically the time that the Annotation was created.
     */
    public final static String WHEN = NS + "when";
}
