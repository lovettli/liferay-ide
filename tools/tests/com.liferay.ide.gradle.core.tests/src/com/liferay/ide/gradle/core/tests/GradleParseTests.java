package com.liferay.ide.gradle.core.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.groovy.antlr.AntlrASTProcessor;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.treewalker.SourceCodeTraversal;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDocAssembler;
import org.junit.Before;
import org.junit.Test;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;

public class GradleParseTests
{
    ArrayList<GroovySourceAST> unvisitedNodes = new ArrayList<GroovySourceAST>();
    File file = new File( "build.gradle" );
    FileReader reader = null;
  
    @Before
    public void initGradleFile() throws IOException
    {
        if( !file.exists() )
        {
            file.createNewFile();
        }

        reader = new FileReader( "build.gradle" );
    }

    @Test
    public void parseWithGroovyLexerAndRecognizer() throws RecognitionException, TokenStreamException, IOException
    {

        SourceBuffer sourceBuffer = new SourceBuffer();
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader( reader, sourceBuffer );
        GroovyLexer lexer = new GroovyLexer( unicodeReader );
        unicodeReader.setLexer( lexer );
        GroovyRecognizer parser = GroovyRecognizer.make( lexer );
        parser.setSourceBuffer( sourceBuffer );
        parser.compilationUnit();

        AST ast = parser.getAST();

        traverse(ast);

        for( GroovySourceAST gast : unvisitedNodes )
        {
            if(gast.getText().equals( "dependencies" ) ){
                System.out.println( "Line: " + gast.getLine() );
                System.out.println( "StartOffset: " + gast.getColumn() );
                System.out.println( "EndOffset: " + gast.getColumnLast() );
            }
        }

        /*GroovyMethodDoc only for groovy source code */
        SimpleGroovyClassDocAssembler visitor = new SimpleGroovyClassDocAssembler("", "build.gradle", sourceBuffer, null, new Properties(), true);
        AntlrASTProcessor traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
        SimpleGroovyClassDoc doc = (SimpleGroovyClassDoc) (visitor.getGroovyClassDocs().values().toArray())[0];

        GroovyMethodDoc[] methods = doc.methods();
        
        for(GroovyMethodDoc m : methods){
            System.out.println( m.name() );
        }

    }

    public void traverse(AST ast) {
        if (ast == null) { return; }
        if (unvisitedNodes != null) {
           unvisitedNodes.add((GroovySourceAST)ast);
        }
        GroovySourceAST child = (GroovySourceAST)ast.getFirstChild();
        if (child != null) {
            traverse(child);
        }
        GroovySourceAST sibling = (GroovySourceAST)ast.getNextSibling();
        if (sibling != null) {
            traverse(sibling);
        }
    }

    @Test
    public void gradleScriptParse() throws MultipleCompilationErrorsException, FileNotFoundException, IOException{
        GradleScriptASTParser gradleScriptASTParser = new GradleScriptASTParser(new FileInputStream( file ));

        List<ASTNode> astNode = gradleScriptASTParser.getASTNode();
        
        gradleScriptASTParser.walkScript();
        
    }
}
