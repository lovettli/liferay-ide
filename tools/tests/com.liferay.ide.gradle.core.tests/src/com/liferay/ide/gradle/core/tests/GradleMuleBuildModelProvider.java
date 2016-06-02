package com.liferay.ide.gradle.core.tests;

import java.util.HashMap;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
//import org.mule.tooling.incubator.gradle.parser.ast.ScriptDependency;
//import org.mule.tooling.incubator.gradle.parser.ast.ScriptMap;

public interface GradleMuleBuildModelProvider {
    
//    public List<ScriptMap> getAppliedPlugins();
//
//    
//    public List<ScriptDependency> getDependencies();
//
//    
//    public HashMap<GradleMulePlugin, ScriptMap> getAppliedMulePlugins();
//    
//    
//    public HashMap<String, String> getMulePluginProperties();
//
//    
//    public boolean hasGradleMulePlugin(GradleMulePlugin plugin);
//    
    
    /**
     * Returns, if declared, the mule components node, otherwise null.
     * @return
     */
    public ASTNode getMuleComponentsNode();
    
    /**
     * Returns, if declared, the mule components node, otherwise null.
     * @return
     */
    public ASTNode getDependenciesNode();
}