/**
 * *****************************************************************************
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
 ******************************************************************************
 */
package org.jetbrains.kotlin.resolve.lang.java.newstructure;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument;
import org.jetbrains.kotlin.load.java.structure.JavaArrayAnnotationArgument;
import org.jetbrains.kotlin.name.Name;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.project.Project;

public class NetBeansJavaArrayAnnotationArgument implements JavaArrayAnnotationArgument {

    private final Name name;
    private final Project project;
    private final Collection<?> arguments;
    private final ElementHandle handle;
    
    public NetBeansJavaArrayAnnotationArgument(Collection<?> arguments, Name name, Project project, ElementHandle fromElement){
        this.name = name;
        this.project = project;
        this.arguments = arguments;
        this.handle = fromElement;
    }
    
    @Override
    public List<JavaAnnotationArgument> getElements() {
        List<JavaAnnotationArgument> argumentList = Lists.newArrayList();
        for (Object obj : arguments){
            argumentList.add(NetBeansJavaAnnotationArgument.create(obj, name, project, handle));
        }
        
        return argumentList;
        
    }

    @Override
    public Name getName() {
        return name;
    }
    
    
    
}
