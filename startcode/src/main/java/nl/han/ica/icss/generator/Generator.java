package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Generator {

	public String generate(AST ast) {
		String stringBuilder = "";
		for(ASTNode child : ast.root.getChildren()){
			if(child instanceof VariableAssignment){
				stringBuilder += buildVariableAssignment((VariableAssignment) child);
			}
			if(child instanceof Stylerule){
				stringBuilder += buildStylerule((Stylerule)child);
			}
		}
        return stringBuilder;
	}

	private String buildStylerule(Stylerule node) {
		String stringBuilder = "";
		for(Selector selector : node.selectors){
			stringBuilder += selector + " {\n";
		}
		for(ASTNode child : node.getChildren()) {
			if (child instanceof VariableAssignment) {
				stringBuilder += "  " + buildVariableAssignment((VariableAssignment) child);
			}
			if (child instanceof Declaration) {
				stringBuilder += "  " + buildDeclaration((Declaration) child);
			}
		}
		stringBuilder += "}\n";
		return stringBuilder;
	}

	private String buildDeclaration(Declaration node) {
		return node.property.name + " : " + getStringFromExpression(node.expression) + ";\n";
	}

	private String buildVariableAssignment(VariableAssignment node) {
		return node.name.name + " := " + getStringFromExpression(node.expression) + ";\n";
	}

	private String getStringFromExpression(Expression expression){
		String value = "";
		if (expression instanceof PixelLiteral){
			value += ((PixelLiteral) expression).value + "px";
		}
		else if (expression instanceof ColorLiteral){
			value = ((ColorLiteral) expression).value;
		}
		else if (expression instanceof ScalarLiteral){
			value += ((ScalarLiteral) expression).value;
		}
		else if (expression instanceof BoolLiteral){
			if(((BoolLiteral) expression).value){
				value = "TRUE";
			} else {
				value = "FALSE";
			}
		}
		else if (expression instanceof PercentageLiteral){
			value += ((PercentageLiteral) expression).value + "%";
		}
		return value;
	}
}
