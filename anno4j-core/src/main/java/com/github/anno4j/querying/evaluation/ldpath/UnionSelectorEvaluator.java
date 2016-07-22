package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.annotations.Evaluator;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.syntax.*;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.UnionSelector;

/**
 * Evaluates an UnionSelector. More precisely, it creates the UNION part of the SPARQL query.
 */
@Evaluator(UnionSelector.class)
public class UnionSelectorEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        UnionSelector unionSelector = (UnionSelector) nodeSelector;

        NodeSelector nodeSelectorLeft = unionSelector.getLeft();
        NodeSelector nodeSelectorRight = unionSelector.getRight();

        ElementGroup leftGroup = new ElementGroup();
        ElementGroup rightGroup = new ElementGroup();

        Var leftVar = LDPathEvaluator.evaluate(nodeSelectorLeft, leftGroup, var, evaluatorConfiguration);
        Var rightVar = LDPathEvaluator.evaluate(nodeSelectorRight, rightGroup, var, evaluatorConfiguration);

        Var subVar = Var.alloc(VarIDGenerator.createID());

        Query leftSubQuery = new Query();
        leftGroup.addElement(new ElementBind(subVar, new NodeValueNode(leftVar.asNode())));
        leftSubQuery.setQueryPattern(leftGroup);
        leftSubQuery.addResultVar(var);
        leftSubQuery.addResultVar(subVar);
        leftSubQuery.setQuerySelectType();
        ElementSubQuery leftESubQuery = new ElementSubQuery(leftSubQuery);

        Query rightSubQuery = new Query();
        rightGroup.addElement(new ElementBind(subVar, new NodeValueNode(rightVar.asNode())));
        rightSubQuery.setQueryPattern(rightGroup);
        rightSubQuery.addResultVar(var);
        rightSubQuery.addResultVar(subVar);
        rightSubQuery.setQuerySelectType();
        ElementSubQuery rightESubQuery = new ElementSubQuery(rightSubQuery);


        ElementUnion elementUnion = new ElementUnion();

        elementUnion.addElement(leftESubQuery);
        elementUnion.addElement(rightESubQuery);
        elementGroup.addElement(elementUnion);

        return subVar;
    }
}
