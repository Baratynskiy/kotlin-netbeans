package org.jetbrains.kotlin.resolve.lang.java.newstructure;

import java.util.Collection;
import java.util.List;
import org.jetbrains.kotlin.descriptors.Visibility;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaClass;
import org.jetbrains.kotlin.load.java.structure.JavaMethod;
import org.jetbrains.kotlin.load.java.structure.JavaType;
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter;
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;

public class NetBeansJavaMethod implements JavaMethod {

    
    
    @Override
    public List<JavaValueParameter> getValueParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JavaType getReturnType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getHasAnnotationParameterDefaultValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JavaClass getContainingClass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<JavaAnnotation> getAnnotations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JavaAnnotation findAnnotation(FqName fqname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDeprecatedInJavaDoc() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAbstract() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isStatic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFinal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Visibility getVisibility() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Name getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<JavaTypeParameter> getTypeParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
