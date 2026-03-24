package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HanStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HanStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		System.out.println("Enter Stylesheet");
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		System.out.println("Exit Stylesheet");
	}

	@Override
	public void enterRuleset(ICSSParser.RulesetContext ctx) {
		System.out.println("Enter Ruleset");
	}

	@Override
	public void exitRuleset(ICSSParser.RulesetContext ctx) {
		System.out.println("Exit Ruleset");
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		System.out.println("Enter Selector");
	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		System.out.println("Exit Selector");
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		System.out.println("Enter Declaration");
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		System.out.println("Exit Declaration");
	}

	@Override
	public void enterValue(ICSSParser.ValueContext ctx) {
		System.out.println("Enter Value");
	}

	@Override
	public void exitValue(ICSSParser.ValueContext ctx) {
		System.out.println("Exit Value");
	}
}