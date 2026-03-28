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
        currentContainer.push(ast.root);
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
    public void enterVariable(ICSSParser.VariableContext ctx) {
        System.out.println("Enter Variable");
        VariableAssignment variable = new VariableAssignment();
        variable.addChild(new VariableReference(ctx.CAPITAL_IDENT().getText()));
        currentContainer.peek().addChild(variable);
        currentContainer.push(variable);
    }

    @Override
    public void exitVariable(ICSSParser.VariableContext ctx) {
        System.out.println("Exit Variable");
        currentContainer.pop();
    }

    @Override
    public void enterRuleset(ICSSParser.RulesetContext ctx) {
        System.out.println("Enter Ruleset");
        Stylerule stylerule = new Stylerule();
        currentContainer.peek().addChild(stylerule);
        currentContainer.push(stylerule);
    }

    @Override
    public void exitRuleset(ICSSParser.RulesetContext ctx) {
        System.out.println("Exit Ruleset");
        currentContainer.pop();
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        System.out.println("Enter Selector");
        Selector selector = createSelector(ctx);
        currentContainer.peek().addChild(selector);
        currentContainer.push(selector);
    }

    @Override
    public void exitSelector(ICSSParser.SelectorContext ctx) {
        System.out.println("Exit Selector");
        currentContainer.pop();
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        System.out.println("Enter Declaration");
        Declaration declaration = new Declaration(ctx.LOWER_IDENT().getText());
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        System.out.println("Exit Declaration");
        currentContainer.pop();
    }

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        System.out.println("Enter IfClause");
        IfClause ifClause = new IfClause();
        currentContainer.peek().addChild(ifClause);
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        System.out.println("Exit IfClause");
        currentContainer.pop();
    }

    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        System.out.println("Enter ElseClause");
        ElseClause elseClause = new ElseClause();
        currentContainer.peek().addChild(elseClause);
        currentContainer.push(elseClause);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        System.out.println("Exit IfClause");
        currentContainer.pop();
    }

    @Override
    public void enterAssignmentValue(ICSSParser.AssignmentValueContext ctx) {
        System.out.println("Enter AssignmentValue");

        if (ctx.CAPITAL_IDENT() != null) {
            VariableReference ref = new VariableReference(ctx.CAPITAL_IDENT().getText());
            currentContainer.peek().addChild(ref);
        }
    }

    @Override
    public void exitAssignmentValue(ICSSParser.AssignmentValueContext ctx) {
        System.out.println("Exit AssignmentValue");
    }

    @Override
    public void enterAdditionExpr(ICSSParser.AdditionExprContext ctx) {
        System.out.println("Enter AdditionExpr");
    }

    @Override
    public void exitAdditionExpr(ICSSParser.AdditionExprContext ctx) {
        System.out.println("Exit AdditionExpr");
        if (currentContainer.peek() instanceof Operation) {
            currentContainer.pop();
        }
    }

    @Override
    public void enterMultiplicationExpr(ICSSParser.MultiplicationExprContext ctx) {
        System.out.println("Enter MultiplicationExpr");
    }

    @Override
    public void exitMultiplicationExpr(ICSSParser.MultiplicationExprContext ctx) {
        System.out.println("Exit MultiplicationExpr");
        if (currentContainer.peek() instanceof Operation) {
            currentContainer.pop();
        }
    }

    @Override
    public void enterCalculationValue(ICSSParser.CalculationValueContext ctx) {
        System.out.println("Enter CalculationValue");
        ASTNode calculationValue = createCalculationValue(ctx);
        if(currentContainer.peek() instanceof Operation){
            currentContainer.peek().addChild(calculationValue);
        } else {
            currentContainer.push(calculationValue);
        }
    }

    @Override
    public void exitCalculationValue(ICSSParser.CalculationValueContext ctx) {
        System.out.println("Exit CalculationValue");
    }

    @Override
    public void enterMultiplicationOperator(ICSSParser.MultiplicationOperatorContext ctx) {
        System.out.println("Enter MultiplicationOperator");
        MultiplyOperation operation = new MultiplyOperation();
        ASTNode firstValue;
        if(currentContainer.peek() instanceof AddOperation){
            firstValue = ((Operation) currentContainer.peek()).rhs;
            ((Operation) currentContainer.peek()).rhs = operation;
        } else {
            firstValue =currentContainer.pop();
        }
        operation.addChild(firstValue);
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    @Override
    public void exitMultiplicationOperator(ICSSParser.MultiplicationOperatorContext ctx) {
        System.out.println("Exit MultiplicationOperator");
    }

    @Override
    public void enterAdditionOperator(ICSSParser.AdditionOperatorContext ctx) {
        System.out.println("Enter AdditionOperator");
        Operation operation;
        if (ctx.getStart().getType() == ICSSParser.PLUS) {
            operation = new AddOperation();
        } else {
            operation = new SubtractOperation();
        }
        ASTNode firstValue = currentContainer.pop();
        operation.addChild(firstValue);
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    @Override
    public void exitAdditionOperator(ICSSParser.AdditionOperatorContext ctx) {
        System.out.println("Exit AdditionOperator");
    }

    @Override
    public void enterBooleanType(ICSSParser.BooleanTypeContext ctx) {
        System.out.println("Exit BooleanType");
        if(ctx.getStart().getType() == ICSSParser.CAPITAL_IDENT) {
            currentContainer.peek().addChild(new VariableReference(ctx.CAPITAL_IDENT().getText()));
        }
    }

    @Override
    public void exitBooleanType(ICSSParser.BooleanTypeContext ctx) {
        System.out.println("Exit BooleanType");
    }


    @Override
    public void enterValue(ICSSParser.ValueContext ctx) {
        System.out.println("Enter Value");
        Literal value = createLiteral(ctx);
        currentContainer.peek().addChild(value);
    }

    @Override
    public void exitValue(ICSSParser.ValueContext ctx) {
        System.out.println("Exit Value");
    }

    private Selector createSelector(ICSSParser.SelectorContext ctx) {
        switch (ctx.getStart().getType()) {
            case ICSSParser.ID_IDENT:
                return new IdSelector(ctx.ID_IDENT().getText());
            case ICSSParser.CLASS_IDENT:
                return new ClassSelector(ctx.CLASS_IDENT().getText());
            case ICSSParser.LOWER_IDENT:
                return new TagSelector(ctx.LOWER_IDENT().getText());
            default:
                throw new IllegalStateException("Unexpected token type: " + ctx.getStart().getType());
        }
    }

    private Literal createLiteral(ICSSParser.ValueContext ctx) {
        switch (ctx.getStart().getType()) {
            case ICSSParser.COLOR:
                return new ColorLiteral(ctx.COLOR().getText());
            case ICSSParser.PIXELSIZE:
                return new PixelLiteral(ctx.PIXELSIZE().getText());
            case ICSSParser.TRUE:
                return new BoolLiteral(ctx.TRUE().getText());
            case ICSSParser.FALSE:
                return new BoolLiteral(ctx.FALSE().getText());
            default:
                throw new IllegalStateException("Unexpected token type: " + ctx.getStart().getType());
        }
    }

    private ASTNode createCalculationValue(ICSSParser.CalculationValueContext ctx) {
        switch (ctx.getStart().getType()) {
            case ICSSParser.CAPITAL_IDENT:
                return new VariableReference(ctx.CAPITAL_IDENT().getText());
            case ICSSParser.PIXELSIZE:
                return new PixelLiteral(ctx.PIXELSIZE().getText());
            case ICSSParser.SCALAR:
                return new ScalarLiteral(ctx.SCALAR().getText());
            default:
                throw new IllegalStateException("Unexpected token type: " + ctx.getStart().getType());
        }
    }
}