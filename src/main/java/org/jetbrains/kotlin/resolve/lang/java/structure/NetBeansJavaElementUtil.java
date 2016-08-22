/*******************************************************************************
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************************/
package org.jetbrains.kotlin.resolve.lang.java.structure;

import com.google.common.collect.Lists;
import com.intellij.psi.CommonClassNames;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import org.jetbrains.kotlin.projectsextensions.KotlinProjectHelper;
import org.jetbrains.kotlin.resolve.lang.java.NBElementUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.Visibilities;
import org.jetbrains.kotlin.descriptors.Visibility;
import org.jetbrains.kotlin.load.java.JavaVisibilities;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter;
import org.jetbrains.kotlin.name.ClassId;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
/**
 *
 * @author Александр
 */
public class NetBeansJavaElementUtil {

    public static boolean isPublic(Set<Modifier> modifiers){
        return modifiers.contains(Modifier.PUBLIC);
    }
    
    public static boolean isPrivate(Set<Modifier> modifiers){
        return modifiers.contains(Modifier.PRIVATE);
    }
    
    public static boolean isProtected(Set<Modifier> modifiers){
        return modifiers.contains(Modifier.PROTECTED);
    }
    
    public static boolean isStatic(Set<Modifier> modifiers){
        return modifiers.contains(Modifier.STATIC);
    }
    
    public static boolean isAbstract(Set<Modifier> modifiers){
        return modifiers.contains(Modifier.ABSTRACT);
    }
    
    public static boolean isFinal(Set<Modifier> modifiers){
        return modifiers.contains(Modifier.FINAL);
    }
    
    @NotNull
    static Visibility getVisibility(@NotNull Element member){
        if (isPublic(member.getModifiers())){
            return Visibilities.PUBLIC;
        } else if (isPrivate(member.getModifiers())){
            return Visibilities.PRIVATE;
        } else if (isProtected(member.getModifiers())){
            return isStatic(member.getModifiers()) ? JavaVisibilities.PROTECTED_STATIC_VISIBILITY :
                    JavaVisibilities.PROTECTED_AND_PACKAGE;
        }
        
        return JavaVisibilities.PACKAGE_VISIBILITY;
    }
    
    @Nullable
    public static ClassId computeClassId(@NotNull TypeElement classBinding){
        Element container = classBinding.getEnclosingElement();
        
        if (container.getKind() != ElementKind.PACKAGE){
            ClassId parentClassId = computeClassId((TypeElement) container);
            return parentClassId == null ? null : parentClassId.createNestedClassId(
                    Name.identifier(classBinding.getSimpleName().toString()));
        }
        
        String fqName = classBinding.getQualifiedName().toString();
        return ClassId.topLevel(new FqName(fqName));
    }
    
    public static JavaAnnotation findAnnotation(@NotNull List<? extends AnnotationMirror> annotations, @NotNull FqName fqName){
        
        for (AnnotationMirror annotation : annotations){
            String annotationFQName = annotation.getAnnotationType().toString();
            if (fqName.asString().equals(annotationFQName)){
                return new NetBeansJavaAnnotation(annotation);
            }
        }
        
        return null;
    }
    
    private static List<TypeMirror> getSuperTypes(@NotNull TypeElement typeBinding){
        List<TypeMirror> superTypes = Lists.newArrayList();
        for (TypeMirror superInterface : typeBinding.getInterfaces()){
            superTypes.add(superInterface);
        }
        
        TypeMirror superclass = typeBinding.getSuperclass();
        if (!(superclass instanceof NoType)){
            superTypes.add(superclass);
        }
        
        return superTypes;
    }
    
    public static TypeMirror[] getSuperTypesWithObject(@NotNull TypeElement typeBinding){
        List<TypeMirror> allSuperTypes = Lists.newArrayList();
        
        boolean javaLangObjectInSuperTypes = false;
        for (TypeMirror superType : getSuperTypes(typeBinding)){
            
            if (superType.toString().equals(CommonClassNames.JAVA_LANG_OBJECT)){
                javaLangObjectInSuperTypes = true;
            }
            
            allSuperTypes.add(superType);
            
        }
        
        if (!javaLangObjectInSuperTypes && !typeBinding.toString().
                equals(CommonClassNames.JAVA_LANG_OBJECT)){
            allSuperTypes.add(getJavaLangObjectBinding());
        }
        
        return allSuperTypes.toArray(new TypeMirror[allSuperTypes.size()]);
    }
    
    @NotNull
    private static TypeMirror getJavaLangObjectBinding(){
        Project project = null;
        
        for (Project pr : OpenProjects.getDefault().getOpenProjects()){
            if (KotlinProjectHelper.INSTANCE.checkProject(pr)){
                project = pr;
                break;
            }
        }
        
        TypeMirror javaType = NBElementUtils.findTypeElement(
                project, CommonClassNames.JAVA_LANG_OBJECT).asType();
        return javaType;
    }
    
    @NotNull
    public static List<JavaValueParameter> getValueParameters(@NotNull ExecutableElement method){
        List<JavaValueParameter> parameters = new ArrayList<JavaValueParameter>();
        List<? extends VariableElement> valueParameters = method.getParameters();
        String[] parameterNames = getParametersNames(method);
        int parameterTypesCount = valueParameters.size();
        
        for (int i = 0; i < parameterTypesCount; i++){
            boolean isLastParameter = i == parameterTypesCount-1;
            parameters.add(new NetBeansJavaValueParameter(valueParameters.get(i), 
                    parameterNames[i], isLastParameter ? method.isVarArgs() : false));
            
        }
        
        return parameters;
    }
    
    
    @NotNull
    private static String[] getParametersNames(@NotNull ExecutableElement method){
        List<? extends VariableElement> valueParameters = method.getParameters();
        List<String> parameterNames = Lists.newArrayList();
        
        for (VariableElement elem : valueParameters){
            parameterNames.add(elem.getSimpleName().toString());
        }
        
        return parameterNames.toArray(new String[parameterNames.size()]);
        
    } 
    
    
    public static boolean isKotlinLightClass(@NotNull Element element ){
        return false;
    }
    
    
    
}
