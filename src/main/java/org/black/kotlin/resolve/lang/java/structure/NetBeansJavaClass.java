package org.black.kotlin.resolve.lang.java.structure;

import static org.black.kotlin.resolve.lang.java.structure.NetBeansJavaElementFactory.typeParameters;
import static org.black.kotlin.resolve.lang.java.structure.NetBeansJavaElementFactory.classifierTypes;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import org.jetbrains.kotlin.descriptors.Visibility;
import org.jetbrains.kotlin.load.java.structure.JavaClass;
import org.jetbrains.kotlin.load.java.structure.JavaClassifierType;
import org.jetbrains.kotlin.load.java.structure.JavaConstructor;
import org.jetbrains.kotlin.load.java.structure.JavaField;
import org.jetbrains.kotlin.load.java.structure.JavaMethod;
import org.jetbrains.kotlin.load.java.structure.JavaType;
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.name.SpecialNames;

/**
 *
 * @author Александр
 */
public class NetBeansJavaClass extends NetBeansJavaClassifier<TypeElement> implements JavaClass {
    
    public NetBeansJavaClass(TypeElement javaElement){
        super(javaElement);
    }

    @Override
    public Name getName() {
        return SpecialNames.safeIdentifier(getBinding().getSimpleName().toString());
    }

    @Override
    public Collection<JavaClass> getInnerClasses() {
        List<? extends Element> enclosedElements = getBinding().getEnclosedElements();
        List<JavaClass> innerClasses = Lists.newArrayList();
        for (Element element : enclosedElements){
            if (element.asType().getKind() == TypeKind.DECLARED && element instanceof TypeElement){
                innerClasses.add(new NetBeansJavaClass((TypeElement) element));
            }
        }
        return innerClasses;
    }

    @Override
    public FqName getFqName() {
        return new FqName(getBinding().getQualifiedName().toString());
    }

    @Override
    public boolean isInterface() {
        return getBinding().getKind().isInterface();
    }

    @Override
    public boolean isAnnotationType() {
        return getBinding().getKind() == ElementKind.ANNOTATION_TYPE;
    }

    @Override
    public boolean isEnum() {
        return getBinding().getKind() == ElementKind.ENUM;
    }

    @Override
    public JavaClass getOuterClass() {
        Element outerClass = getBinding().getEnclosingElement();
        if (outerClass == null || outerClass.asType().getKind() != TypeKind.DECLARED){
            return null;
        }
        return new NetBeansJavaClass((TypeElement) outerClass);
    }

    @Override
    public Collection<JavaClassifierType> getSupertypes() {
        return classifierTypes(NetBeansJavaElementUtil.getSuperTypesWithObject(getBinding()));
    }

    @Override
    public Collection<JavaMethod> getMethods() {
        List<? extends Element> declaredElements = getBinding().getEnclosedElements();
        List<JavaMethod> javaMethods = Lists.newArrayList();
        
        for (Element element : declaredElements){
            if (element.getKind() == ElementKind.METHOD){
                javaMethods.add(new NetBeansJavaMethod((ExecutableElement) element));
            }
        }
        
        return javaMethods;
    }

    @Override
    public Collection<JavaField> getFields() {
        List<? extends Element> declaredElements = getBinding().getEnclosedElements();
        List<JavaField> javaFields = Lists.newArrayList();
        
        for (Element element : declaredElements){
            if (element.getKind() == ElementKind.FIELD){
                String name = element.getSimpleName().toString();
                if (Name.isValidIdentifier(name)){
                    javaFields.add(new NetBeansJavaField((VariableElement) element));
                }
            }
        }
        
        return javaFields;
    }

    @Override
    public Collection<JavaConstructor> getConstructors() {
        List<? extends Element> declaredElements = getBinding().getEnclosedElements();
        List<JavaConstructor> javaConstructors = Lists.newArrayList();
        
        for (Element element : declaredElements){
            if (element.getKind().equals(ElementKind.CONSTRUCTOR)){
                javaConstructors.add(new NetBeansJavaConstructor((ExecutableElement) element));
            }
        }
        return javaConstructors;
    }

//    @Override
//    public JavaClassifierType getDefaultType() {
//        return new NetBeansJavaClassifierType(getBinding().asType());
//    }
//
//    @Override
//    public OriginKind getOriginKind() {
//        if (NetBeansJavaElementUtil.isKotlinLightClass(getBinding())){
//            return OriginKind.KOTLIN_LIGHT_CLASS;
//        } else // to add OriginKind.COMPILED
//            return OriginKind.SOURCE;
//    }
//
//    @Override
//    public JavaType createImmediateType(JavaTypeSubstitutor substitutor) {
//        return new NetBeansJavaImmediateClass(this, substitutor);
//    }

    @Override
    public List<JavaTypeParameter> getTypeParameters() {
        List<? extends TypeParameterElement> typeParameters = getBinding().getTypeParameters();
        return typeParameters(typeParameters.toArray(new TypeParameterElement[typeParameters.size()]));
    }

    @Override
    public boolean isAbstract() {
        return NetBeansJavaElementUtil.isAbstract(getBinding().getModifiers());
    }

    @Override
    public boolean isStatic() {
        return NetBeansJavaElementUtil.isStatic(getBinding().getModifiers());
    }

    @Override
    public boolean isFinal() {
        return NetBeansJavaElementUtil.isFinal(getBinding().getModifiers());
    }

    @Override
    public Visibility getVisibility() {
        return NetBeansJavaElementUtil.getVisibility(getBinding());
    }

    @Override
    public boolean isKotlinLightClass() {
        return NetBeansJavaElementUtil.isKotlinLightClass(getBinding().getEnclosingElement());
    }
    
}
