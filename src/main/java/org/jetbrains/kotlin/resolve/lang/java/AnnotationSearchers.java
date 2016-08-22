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
package org.jetbrains.kotlin.resolve.lang.java;

import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.resolve.lang.java.newstructure.NetBeansJavaAnnotationArgument;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;

/**
 *
 * @author Alexander.Baratynski
 */
public class AnnotationSearchers {

    public static class ArgumentSearcher implements Task<CompilationController> {

        private final ElementHandle from;
        private final Name name;
        private final String mirrorName;
        private JavaAnnotationArgument argument;
        private final Project project;
        
        public ArgumentSearcher(ElementHandle from, String mirrorName, Name name, Project project) {
            this.from = from;
            this.mirrorName = mirrorName;
            this.name = name;
            this.project = project;
        }

        @Override
        public void run(CompilationController info) throws Exception {
            info.toPhase(Phase.RESOLVED);
            
            Element fromElement = from.resolve(info);
            List<? extends AnnotationMirror> mirrors = info.getElements().getAllAnnotationMirrors(fromElement);
            AnnotationMirror mirror = null;
            for (AnnotationMirror mir : mirrors) {
                if (mirrorName.equals(mir.toString())) {
                    mirror = mir;
                    break;
                }
            }
            if (mirror == null) {
                return;
            }

            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                    : mirror.getElementValues().entrySet()) {
                if (name.asString().equals(entry.getKey().getSimpleName().toString())) {
                    argument = NetBeansJavaAnnotationArgument.create(entry.getValue().getValue(),
                            name,project, ElementHandle.create(fromElement));
                }
            }

        }

        public JavaAnnotationArgument getArgument() {
            return argument;
        }
        
    }

}
