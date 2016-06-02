package com.liferay.ide.gradle.core.tests;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;

public class GradleScriptASTVisitor extends CodeVisitorSupport implements GradleMuleBuildModelProvider {

//    private List<ScriptMap> appliedPlugins = new LinkedList<ScriptMap>();
//    
//    private HashMap<GradleMulePlugin, ScriptMap> appliedMulePlugins = new HashMap<GradleMulePlugin, ScriptMap>();
//    
//    private List<ScriptDependency> dependencies = new LinkedList<ScriptDependency>();
    
    private HashMap<String, String> mulePluginProperties = new HashMap<String, String>();
    
    private ASTNode muleComponentsNode;
    
    private ASTNode dependenciesNode;
    
    public static enum STATE {
        apply, mule, components, connector, module, plugin, dependencies, buildscript,
        compile, runtime, providedCompile, providedRuntime, testCompile, testRuntime,
        providedTestCompile, providedTestRuntime, plugins, id, version
    }
    
    public static enum KNOWN_OBJECT {
        mule, mmc, cloudhub
    }
    
    private static LinkedList<STATE> currentContextStack = new LinkedList<STATE>();
    
    private static final LinkedList<STATE> pluginContextStack = new LinkedList<>(Arrays.asList(STATE.id, STATE.version, STATE.plugins));

    // utility methods

    private boolean applyCurrentContext(String contextName, ASTNode node) {
        try {
            currentContextStack.push(STATE.valueOf(contextName));
            trackContext(node);
            return true;
        } catch (IllegalArgumentException ex) {
            System.out.println("Ignoring irrelevant context call: " + contextName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    
    
    private boolean isKnownObject(String objectName) {
        try {
            return KNOWN_OBJECT.valueOf(objectName) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
    
    private void currentContextEnded(boolean contextApplied) {
        if (contextApplied) {
            currentContextStack.pop();
        }
    }
    
    //track the current context and save the ast node.
    private void trackContext(ASTNode node) {
        
        Iterator<STATE> stateReverseIterator = currentContextStack.descendingIterator();
        
        STATE lastState = stateReverseIterator.next();
        
        switch (lastState) {
        case components: 
            muleComponentsNode = node;
            break;
        case dependencies:
            if (stateReverseIterator.hasNext()) {
                if (stateReverseIterator.next() == STATE.buildscript) {
                    break;
                }
            }
            dependenciesNode = node;
            break;
        default:
            break;
        }
    }

    @Override
    public ASTNode getMuleComponentsNode() {
        return muleComponentsNode;
    }

    @Override
    public ASTNode getDependenciesNode() {
        return dependenciesNode;
    }
    
    
    @Override
    public void visitArgumentlistExpression(final ArgumentListExpression ale) {
        super.visitArgumentlistExpression( ale );
    }

    @Override
    public void visitBlockStatement( BlockStatement block )
    {
        super.visitBlockStatement( block );
    }

    @Override
    public void visitExpressionStatement( ExpressionStatement statement )
    {
        super.visitExpressionStatement( statement );
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        boolean contextApplied = applyCurrentContext(call.getMethodAsString(), call); 
        super.visitMethodCallExpression(call);
        currentContextEnded(contextApplied);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        String objectName = expression.getObjectExpression().getText();
        
        if (isKnownObject(objectName)) {
            String value = expression.getProperty().getText();
            mulePluginProperties.put(objectName, value);
        }
        
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        super.visitMapExpression( expression );
    }


    @Override
    public void visitConstantExpression( ConstantExpression expression )
    {
        super.visitConstantExpression( expression );
    }
    
    
}