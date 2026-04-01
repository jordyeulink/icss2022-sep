package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
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
        for(int index = 0 ; index < ast.root.getChildren().size(); index++){
            ASTNode child = ast.root.getChildren().get(index);
            if(child instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) child);
            }
            else if(child instanceof Stylerule){
                evaluateStylerule((Stylerule) child);
            }
        }
        removeVariableAssignments(ast.root.getChildren());
    }

    private void evaluateStylerule(Stylerule node) {

        for(int index = 0; index < node.body.size(); index ++){
            ASTNode child = node.body.get(index);
            if(child instanceof VariableAssignment){
                evaluateVariableAssignment((VariableAssignment) child);
            }
            else if(child instanceof Declaration){
                Declaration newDeclaration = evaluateDeclaration((Declaration) child);
                node.body.remove(index);
                node.body.add(index,newDeclaration);
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
        removeVariableAssignments(node.body);
    }

    private ArrayList<ASTNode> evaluateIfClause(IfClause node) {
        BoolLiteral conditionalExpression = null;
        if(node.conditionalExpression instanceof BoolLiteral) {
            conditionalExpression = (BoolLiteral) node.conditionalExpression;
        } else if (node.conditionalExpression instanceof VariableReference) {
            conditionalExpression = (BoolLiteral) variableValues.getFirst().get(((VariableReference) node.conditionalExpression).name);
        }
        if(conditionalExpression.value){
            for (int index = 0; index < node.body.size(); index++){
                ASTNode child = node.body.get(index);
                if(child instanceof VariableAssignment){
                    updateVariable((VariableAssignment) child);
                }
                if(child instanceof Declaration){
                    Declaration newDeclaration = evaluateDeclaration((Declaration) child);
                    node.body.remove(index);
                    node.body.add(index,newDeclaration);
                }
            }
            removeVariableAssignments(node.body);
            return node.body;
        }
        if (node.elseClause != null){
            for (int index = 0; index < node.elseClause.body.size(); index++){
                ASTNode child = node.elseClause.body.get(index);
                if(child instanceof VariableAssignment){
                    updateVariable((VariableAssignment) child);
                }
                if(child instanceof Declaration){
                    Declaration newDeclaration = evaluateDeclaration((Declaration) child);
                    node.body.remove(index);
                    node.body.add(index,newDeclaration);
                }
            }
            removeVariableAssignments(node.body);
            return node.elseClause.body;
        }
        return null;
    }

    private void updateVariable(VariableAssignment node) {
        Literal newValue = (Literal) evaluate(node.expression);
        if (variableValues.getFirst().get(node.name.name) != null){
            variableValues.getFirst().remove(node.name.name);
        }
        variableValues.getFirst().put(node.name.name, newValue);
    }

    private Declaration evaluateDeclaration(Declaration node) {
        if(node.expression instanceof Operation){
            node.expression = evaluateOperation((Operation) node.expression);
        }
        else if(node.expression instanceof VariableReference){
            node.expression = (Literal) evaluate(node.expression);
        }
        return node;
    }

    private Literal evaluateOperation(Operation expression) {
        Literal left = (Literal) evaluate(expression.lhs);
        Literal right = (Literal) evaluate(expression.rhs);

        ExpressionType resultType = getEffectiveType(expression);

        int lVal = getNumericValue(left);
        int rVal = getNumericValue(right);
        int resultValue = 0;
        if(expression instanceof MultiplyOperation) {
            resultValue = lVal * rVal;
        }
        else if(expression instanceof AddOperation) {
            resultValue = lVal + rVal;
        }
        else if(expression instanceof SubtractOperation) {
            resultValue = lVal - rVal;
        }

        switch (resultType) {
            case PIXEL:      return new PixelLiteral(resultValue);
            case PERCENTAGE: return new PercentageLiteral(resultValue);
            case SCALAR:     return new ScalarLiteral(resultValue);
            default:         return new ScalarLiteral(resultValue);
        }
    }

    private ExpressionType getEffectiveType(Expression node) {
        if (node instanceof MultiplyOperation) {
            Operation op = (Operation) node;
            ExpressionType left = getExpressionType(op.lhs);
            ExpressionType right = getExpressionType(op.rhs);

            return (left == ExpressionType.SCALAR) ? right : left;
        }
        if (node instanceof AddOperation) {
            return getExpressionType(((AddOperation) node).lhs);
        }
        return getExpressionType(node);
    }

    private int getNumericValue(Literal literal) {
        if (literal instanceof ScalarLiteral) return ((ScalarLiteral) literal).value;
        if (literal instanceof PixelLiteral) return ((PixelLiteral) literal).value;
        if (literal instanceof PercentageLiteral) return ((PercentageLiteral) literal).value;
        return 0;
    }

    private void evaluateVariableAssignment(VariableAssignment node) {
        Literal value = (Literal) evaluate(node.expression);
        variableValues.getFirst().put(node.name.name, value);
    }

    private Expression evaluate(Expression expression) {
        if (expression instanceof Literal) {
            return expression;
        }
        else if (expression instanceof VariableReference) {
            String varName = ((VariableReference) expression).name;
            return variableValues.getFirst().get(varName);
        }
        else if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }

        return expression;
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
            return getExpressionType(variableValues.getFirst().get(((VariableReference) expression).name));
        }
        System.out.println(expression.getNodeLabel());
        System.out.println("returned UNDEFINED");
        return ExpressionType.UNDEFINED;
    }

    private void removeVariableAssignments(ArrayList<ASTNode> nodes) {
        ArrayList<ASTNode> toRemove = new ArrayList<>();
        for (ASTNode child : nodes) {
            if (child instanceof VariableAssignment) {
                evaluateVariableAssignment((VariableAssignment) child);
                toRemove.add(child);
            }
        }
        nodes.removeAll(toRemove);
    }

}
