/*
 * Copyright (c) 2009, James Leigh All rights reserved.
 * Copyright (c) 2011 Talis Inc., Some rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution. 
 * - Neither the name of the openrdf.org nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.openrdf.repository.object.managers.helpers;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Matching;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.managers.RoleMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

/**
 * Loads the annotations, concepts and behaviours into memory.
 *
 * @author James Leigh
 */
public class RoleClassLoader {
    private static final String CONCEPTS = "META-INF/org.openrdf.concepts";
    private static final String BEHAVIOURS = "META-INF/org.openrdf.behaviours";
    private static final String ANNOTATIONS = "META-INF/org.openrdf.annotations";

    private final Logger logger = LoggerFactory.getLogger(DirectMapper.class);

    private RoleMapper roleMapper;

    public RoleClassLoader(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * Loads and registers roles listed in resource.
     *
     * @throws ObjectStoreConfigException
     */
    public void loadRoles(ClassLoader cl) throws ObjectStoreConfigException {
        try {
            ClassLoader first = RoleClassLoader.class.getClassLoader();
            Set<URL> loaded;
            loaded = load(new CheckForAnnotation(first), first, "annotations", ANNOTATIONS, true, new HashSet<URL>());
            loaded = load(new CheckForAnnotation(cl), cl, "annotations", ANNOTATIONS, true, loaded);
            loaded = load(new CheckForConcept(first), first, "concepts", CONCEPTS, true, new HashSet<URL>());
            loaded = load(new CheckForConcept(cl), cl, "concepts", CONCEPTS, true, loaded);
            loaded = load(new CheckForBehaviour(first), first, "behaviours", BEHAVIOURS, false, new HashSet<URL>());
            loaded = load(new CheckForBehaviour(cl), cl, "behaviours", BEHAVIOURS, false, loaded);

            scanConceptsWithReflections();

            Collection<Class<?>> concepts = roleMapper.getConceptClasses();
            for (Class<?> conceptClass : concepts) {
                logger.debug("Registered concept class " + conceptClass.getCanonicalName());
            }

        } catch (ObjectStoreConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new ObjectStoreConfigException(e);
        }
    }

    public void scan(URL jar, ClassLoader cl) throws ObjectStoreConfigException {
        scan(jar, new CheckForAnnotation(cl), ANNOTATIONS, cl);
        scan(jar, new CheckForConcept(cl), CONCEPTS, cl);
        scan(jar, new CheckForBehaviour(cl), BEHAVIOURS, cl);
    }

    private void scan(URL url, CheckForConcept checker, String role, ClassLoader cl)
            throws ObjectStoreConfigException {
        try {
            Scanner scanner = new Scanner(checker);
            load(scanner.scan(url, checker.getName(), role), cl, false);
        } catch (Exception e) {
            throw new ObjectStoreConfigException(e);
        }
    }

    private void scanConceptsWithReflections() throws ObjectStoreConfigException, InterruptedException {
        logger.debug("Search for concepts with reflections");
        Set<URL> classpath = new HashSet<>();
        classpath.addAll(ClasspathHelper.forClassLoader());
        classpath.addAll(ClasspathHelper.forJavaClassPath());
        classpath.addAll(ClasspathHelper.forManifest());
        classpath.addAll(ClasspathHelper.forPackage(""));
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(classpath)
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Iri.class, true);
        logger.debug("Search for concepts with reflections resulted in " + annotated.size() + " classes");

        // For multiple inheritance reasons, the Annotation class always needs to be BEHIND the PartMMM class
        Class annotation = null;

		List<Class> sortedAnnotated = new LinkedList<>();
		sortedAnnotated.addAll(annotated);

//		sortedAnnotated.sort(new Comparator<Class>() {
//			@Override
//			public int compare(Class o1, Class o2) {
//				if (o1.isAssignableFrom(o2)) {
//					return 1;
//				} else {
//					return 0;
//				}
//
//			}
//		});

        boolean done;

        while (!done) {
            Integer indexC1 = null;
            Integer indexC2 = null;
            done = true;
            for (int i = 0; i < sortedAnnotated.size(); i++) {
                Class c1 = sortedAnnotated.get(i);
                indexC1 = i;
                for (int j = 0; j < sortedAnnotated.size(); j++) {
                    Class c2 = sortedAnnotated.get(j);
                    if (c2.isAssignableFrom(c1)) {
                        indexC2 = j;
                    }
                }
                if (!(indexC1 == indexC2)) {
                    done = false;
                    break;
                }
            }


        }

//        for(int i = 0; i < sortedAnnotated.size(); ++i) {
//            for(int j = i+1; j < sortedAnnotated.size(); ++j) {
//                Class clazz1 = sortedAnnotated.get(i);
//                Class clazz2 = sortedAnnotated.get(j);
//
//                if(clazz1.isAssignableFrom(clazz2)) {
//
//                }
//                sortedAnnotated.
//            }
//        }

        for (Class clazz : sortedAnnotated) {

//            if (annotation == null && clazz.toString().equals("interface com.github.anno4j.model.Annotation")) {
//                annotation = clazz;
//            } else {
                logger.debug("Found concept class: " + clazz.getCanonicalName());
                roleMapper.addConcept(clazz);

//            }
        }
//        roleMapper.addConcept(annotation);
    }

    private Set<URL> load(CheckForConcept checker, ClassLoader cl, String forType, String roles, boolean concept, Set<URL> exclude)
            throws IOException, ClassNotFoundException, ObjectStoreConfigException {
        if (cl == null)
            return exclude;
        Scanner scanner = new Scanner(checker, roles);
        Enumeration<URL> resources = cl.getResources(roles);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if (!exclude.contains(url)) {
                exclude.add(url);
                logger.debug("Reading roles from {}", url);
                try {
                    Properties p = new Properties();
                    p.load(url.openStream());


                    if (p.isEmpty()) {
                        // exclude concept scanning because of external reflection scanning
                        if (!CONCEPTS.equals(roles)) {
                            load(scanner.scan(url, forType, null), cl, concept);
                        }
                    } else {
                        load(p, cl, concept);
                    }
                } catch (IOException e) {
                    String msg = e.getMessage() + " in: " + url;
                    throw new ObjectStoreConfigException(msg, e);
                } catch (IllegalArgumentException e) {
                    String msg = e.getMessage() + " in: " + url;
                    throw new ObjectStoreConfigException(msg, e);
                }
            }
        }
        return exclude;
    }

    private void load(List<String> roles, ClassLoader cl, boolean concept)
            throws IOException, ObjectStoreConfigException {
        for (String role : roles) {
            try {
                Class<?> clazz = forName(role, true, cl);
                recordRole(clazz, null, concept);
            } catch (ClassNotFoundException exc) {
                logger.error(exc.toString());
            }
        }
    }

    private void load(Properties p, ClassLoader cl, boolean concept)
            throws ClassNotFoundException, IOException, ObjectStoreConfigException {
        for (Map.Entry<Object, Object> e : p.entrySet()) {
            String role = (String) e.getKey();
            String types = (String) e.getValue();
            try {
                int idx = role.indexOf('#');
                if (idx >= 0) {
                    role = role.substring(0, idx);
                }
                Class<?> clazz = forName(role, true, cl);
                for (String rdf : types.split("\\s+")) {
                    if (idx < 0) {
                        recordRole(clazz, rdf, concept);
                    } else {
                        String mname = ((String) e.getKey()).substring(idx + 1);
                        if (mname.endsWith("()")) {
                            mname = mname.substring(0, mname.length() - 2);
                        }
                        if (rdf.length() > 0) {
                            roleMapper.addAnnotation(clazz.getMethod(mname), new URIImpl(rdf));
                        } else {
                            roleMapper.addAnnotation(clazz.getMethod(mname));
                        }
                    }
                }
            } catch (ClassNotFoundException exc) {
                logger.error(exc.toString());
            } catch (NoSuchMethodException exc) {
                logger.error(exc.toString());
            }
        }
    }

    private Class<?> forName(String name, boolean init, ClassLoader cl)
            throws ClassNotFoundException {
        synchronized (cl) {
            return Class.forName(name, init, cl);
        }
    }

    private void recordRole(Class<?> clazz, String uri, boolean concept)
            throws ObjectStoreConfigException {
        if (uri == null || uri.length() == 0) {
            if (clazz.isAnnotation()) {
                roleMapper.addAnnotation(clazz);
            } else if (isAnnotationPresent(clazz) || concept) {
                roleMapper.addConcept(clazz);
            } else {
                roleMapper.addBehaviour(clazz);
            }
        } else {
            if (clazz.isAnnotation()) {
                roleMapper.addAnnotation(clazz, new URIImpl(uri));
            } else if (isAnnotationPresent(clazz) || concept) {
                roleMapper.addConcept(clazz, new URIImpl(uri));
            } else {
                roleMapper.addBehaviour(clazz, new URIImpl(uri));
            }
        }
    }

    private boolean isAnnotationPresent(Class<?> clazz) {
        for (Annotation ann : clazz.getAnnotations()) {
            String name = ann.annotationType().getName();
            if (Iri.class.getName().equals(name))
                return true;
            if (Matching.class.getName().equals(name))
                return true;
        }
        return false;
    }
}
