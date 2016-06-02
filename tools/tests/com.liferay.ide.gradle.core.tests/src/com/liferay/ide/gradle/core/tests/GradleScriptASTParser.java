package com.liferay.ide.gradle.core.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

public class GradleScriptASTParser
{
  private List<ASTNode> nodes;
    
    /**
     * Builds the AST parser and parses the script. If the privded document is not parseable
     * it will throw multiple compilation errors exception right away. 
     * @param document
     * @throws MultipleCompilationErrorsException
 
    public GradleScriptASTParser(IDocument document) throws MultipleCompilationErrorsException {
        this(document.get());
    }
        */
    
    /**
     * Builds the AST parser and parses the script.
     * @param is
     * @throws MultipleCompilationErrorsException
      */
    public GradleScriptASTParser(InputStream is) throws MultipleCompilationErrorsException, IOException {
        this(IOUtils.toString(is, "UTF-8"));
    }


    /**
     * Builds the AST parser with the given script.
     * @param script
     * @throws MultipleCompilationErrorsException
     */
    public GradleScriptASTParser(String script) throws MultipleCompilationErrorsException {
        AstBuilder builder = new AstBuilder();
        nodes = builder.buildFromString(script);
    }
    
    /**
     * Walks the script with the default visitor and returns the instance of this visitor.
     * @return
    */
    public GradleScriptASTVisitor walkScript() {
        GradleScriptASTVisitor visitor = new GradleScriptASTVisitor();
        walkScript(visitor);
        return visitor;
    }

        
    /**
     * Walks the script with the given visitor.
     * @param visitor
     */
    public void walkScript(GroovyCodeVisitor visitor) {
        for (ASTNode node : nodes) {
            node.visit(visitor);
        }
    }
    
    public List<ASTNode> getASTNode(){
        return nodes;
    }
}
