package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues.addFirst(new HashMap<>());
        for(ASTNode child : ast.root.getChildren()){
            if(child instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) child);
            }
            else if(child instanceof Stylerule){
                evaluateStylerule((Stylerule) child);
            }

        }

    }

    private void evaluateStylerule(Stylerule node) {

        for(int index = 0; index < node.body.size(); index ++){
            ASTNode child = node.body.get(index);
            if(child instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) child);
            }
            else if(child instanceof Declaration){
                evaluateDeclaration((Declaration) child);
            }
            else if (child instanceof IfClause) {
                ArrayList<ASTNode> newBody = evaluateIfClause((IfClause) child);
                node.body.remove(index);
                if (newBody != null && !newBody.isEmpty()) {
                    for (int i = 0; i < newBody.size(); i++) {
                        node.body.add(index + i, newBody.get(i));
                    }
                }
            }
        }
    }

    private ArrayList<ASTNode> evaluateIfClause(IfClause node) {
        BoolLiteral conditionalExpression = null;
        if(node.conditionalExpression instanceof BoolLiteral) {
           conditionalExpression = (BoolLiteral) node.conditionalExpression;
        } else if (node.conditionalExpression instanceof VariableReference) {
           conditionalExpression = (BoolLiteral) variableValues.getFirst().get(((VariableReference) node.conditionalExpression).name);
        }

        if(conditionalExpression.value){
            return node.body;
        }
        if (node.elseClause != null){
            return node.elseClause.body;
        }
        return null;
    }

    private void evaluateDeclaration(Declaration node) {

    }

    private void evaluateVariableAssignment(VariableAssignment node) {
        variableValues.getFirst().put(node.name.name,(Literal) node.expression);
    }



}
