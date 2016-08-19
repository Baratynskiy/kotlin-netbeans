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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.jetbrains.kotlin.load.java.structure.JavaClass;
import org.jetbrains.kotlin.load.java.structure.JavaField;
import org.jetbrains.kotlin.load.java.structure.JavaType;

/**
 *
 * @author Александр
 */
public class NetBeansJavaField extends NetBeansJavaMember<VariableElement> implements JavaField {
    
    private final JavaClass containingClass;
    private final JavaType type;
    
    public NetBeansJavaField(VariableElement javaField){
        super(javaField);
        containingClass = new NetBeansJavaClass((TypeElement) javaField.getEnclosingElement());
        type = NetBeansJavaType.create(javaField.asType());
    }

    @Override
    public JavaClass getContainingClass() {
        return containingClass;
    }

    @Override
    public boolean isEnumEntry() {
        return getBinding().getKind() == ElementKind.ENUM_CONSTANT;
    }

    @Override
    public JavaType getType() {
        return type;
    }
}
