package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        variableTypes.addFirst(new HashMap<>());
        for(ASTNode child : ast.root.getChildren()){
            if(child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child);
            }
            else if(child instanceof Stylerule){
                checkStylerule((Stylerule) child);
            }

        }
    }

    public void checkVariableAssignment(VariableAssignment node) {
        System.out.println(node.getNodeLabel());
        ExpressionType expressionType = getExpressionType(node.expression);
        variableTypes.getFirst().put(node.name.name, expressionType);
    }



    public void checkStylerule(Stylerule node){
        for(ASTNode child : node.getChildren()){
            if(child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
            else if(child instanceof IfClause){
                checkIfClause((IfClause) child);
            }

        }
    }

    public void checkDeclaration(Declaration node){
        
        for(ASTNode child : node.getChildren()){
            if(child instanceof VariableReference){
                checkVariableReference((VariableReference) child);
            }
            else if(child instanceof PropertyName){
                checkPropertyName((PropertyName) child);
            }
            else if(child instanceof Operation){
                checkOperation((Operation)child);
            }

        }
    }

    private void checkOperation(Operation node) {
        ExpressionType leftType = getExpressionType(node.lhs);
        ExpressionType rightType = getExpressionType(node.rhs);

        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
            node.setError("Color not allowed");
        }
        if (node instanceof MultiplyOperation) {
            if (leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) {
                node.setError("No scalar found in multiplication");
            }
        }
        else if (node instanceof AddOperation) {
            for (ASTNode child : node.getChildren()) {
                if (child instanceof Operation) {
                    checkOperation((Operation) child);
                }
            }

            ExpressionType effectiveLeft = getEffectiveType(node.lhs);
            ExpressionType effectiveRight = getEffectiveType(node.rhs);

            if (effectiveLeft != effectiveRight) {
                node.setError("Different types in Calculation: " + effectiveLeft + " vs " + effectiveRight);
            }
        }
    }

    private ExpressionType getEffectiveType(Expression node) {
        if (node instanceof MultiplyOperation) {
            Operation op = (Operation) node;
            ExpressionType left = getExpressionType(op.lhs);
            ExpressionType right = getExpressionType(op.rhs);

            return (left == ExpressionType.SCALAR) ? right : left;
        }
        return getExpressionType(node);
    }

    private void checkPropertyName(PropertyName node) {
    }

    private void checkVariableReference(VariableReference node) {
         ExpressionType expressionType = variableTypes.getFirst().get(node.name);
         if(expressionType == ExpressionType.UNDEFINED || expressionType == null){
            node.setError("Variable is not defined");
         }
    }

    public void checkIfClause(IfClause node){
        if((node.conditionalExpression instanceof VariableReference
                && variableTypes.getFirst().get(((VariableReference) node.conditionalExpression).name) != ExpressionType.BOOL)
                || (!(node.conditionalExpression instanceof VariableReference) && getExpressionType(node.conditionalExpression) != ExpressionType.BOOL)){
            node.setError("Conditional expression not of type bool");
        }
    }

    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof PixelLiteral){
            System.out.println("returned PIXEL");
            return ExpressionType.PIXEL;
        }
        if (expression instanceof ColorLiteral){
            System.out.println("returned COLOR");
            return ExpressionType.COLOR;
        }
        if (expression instanceof ScalarLiteral){
            System.out.println("returned SCALAR");
            return ExpressionType.SCALAR;
        }
        if (expression instanceof BoolLiteral){
            System.out.println("returned BOOL");
            return ExpressionType.BOOL;
        }
        if (expression instanceof PercentageLiteral){
            System.out.println("returned PERCENTAGE");
            return ExpressionType.PERCENTAGE;
        }
        if (expression instanceof VariableReference){
            return variableTypes.getFirst().get(((VariableReference) expression).name);
        }
        System.out.println("returned UNDEFINED");
        return ExpressionType.UNDEFINED;
    }
}
