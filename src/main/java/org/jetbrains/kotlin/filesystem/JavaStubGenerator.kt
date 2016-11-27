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
package org.jetbrains.kotlin.filesystem

import kotlin.Pair
import org.jetbrains.kotlin.log.KotlinLogger
import org.jetbrains.org.objectweb.asm.ClassReader
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.ClassNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode
import org.jetbrains.org.objectweb.asm.tree.FieldNode
import org.jetbrains.org.objectweb.asm.ClassVisitor
import org.jetbrains.org.objectweb.asm.FieldVisitor
import org.jetbrains.org.objectweb.asm.signature.SignatureReader
import org.jetbrains.org.objectweb.asm.util.TraceSignatureVisitor

object JavaStubGenerator {

    fun generate(byteCode: ByteArray): Pair<ClassNode?, String> {
        val javaStub = StringBuilder()
        
        val classNode = ClassNode()
        try {
            ClassReader(byteCode).accept(classNode, 0)
        } catch(ex: Exception) {
            return Pair(null, "")
        }
        
        javaStub.append(classNode.packageString)
        javaStub.append(classNode.classDeclaration())
        javaStub.append(classNode.fields())
        javaStub.append(classNode.methods())
        
        javaStub.append("}")
        return Pair(classNode, javaStub.toString())
    }
    
    val ClassNode.packageString: String
        get() = "package ${name.substringBeforeLast("/").replace("/", ".")};\n"
    
    val ClassNode.className: String
        get() = name.substringAfterLast("/")
    
    private fun ClassNode.classDeclaration(): String {
        val declaration = StringBuilder()
        declaration.append(getAccess(access)).append(" ")
        declaration.append(getFinal(access)).append(" ")
        declaration.append(getStatic(access)).append(" ")
        declaration.append(getAbstract(access)).append(" ")
        
        val classType = getClassType(access)
        
        val superTypes = if (signature != null) {
            val signatureReader = SignatureReader(signature)
            val traceSigVisitor = TraceSignatureVisitor(access)
            signatureReader.accept(traceSigVisitor)
            traceSigVisitor.declaration
        } else ""
        
        declaration.append(classType).append(" ")
        declaration.append(className).append(" ")
        declaration.append(superTypes)
        
        declaration.append("{\n")
        
        return declaration.toString();
    }
    
    private fun ClassNode.fields(): String {
        val fieldsStub = StringBuilder()
        
        fields.forEach {
            fieldsStub.append(it.getString())
        }
        return fieldsStub.toString()
    }
    
    private fun FieldNode.getString(): String {
        val field = StringBuilder()
        
        val sig = if (signature != null) signature else desc

        val signatureReader = SignatureReader(sig)
        val traceSigVisitor = TraceSignatureVisitor(access)
        signatureReader.accept(traceSigVisitor)
        
        val type = traceSigVisitor.declaration.substringAfterLast(" ")
        
        field.append(getAccess(access)).append(" ")
        field.append(getFinal(access)).append(" ")
        field.append(getStatic(access)).append(" ")
        field.append(type).append(" ")
        field.append(name).append(";\n")
        
        return field.toString()
    }
    
    private fun ClassNode.methods(): String {
        val methodsStub = StringBuilder()
        
        for (it in methods) {
            methodsStub.append(it.getString(className))
        }
        
        return methodsStub.toString()
    }
    
    private fun MethodNode.getString(className: String): String {
        val method = StringBuilder()
        
        val sig = if (signature != null) signature else desc
        
        method.append(getAccess(access)).append(" ")
        method.append(getFinal(access)).append(" ")
        method.append(getStatic(access)).append(" ")
        method.append(getAbstract(access)).append(" ")
        
        val methodName = if (name == "<init>") className else name
        
        val traceSigVisitor = TraceSignatureVisitor(access)
        SignatureReader(sig).accept(traceSigVisitor)

        val returnType = if (name == "<init>") "" else traceSigVisitor.returnType
        
        method.append(returnType).append(" ").append(methodName)
                .append(traceSigVisitor.declaration).append("{}\n")
        
        return method.toString()
    }
    
    private fun getClassType(access: Int) = when {
        access.contains(Opcodes.ACC_INTERFACE) -> "interface"
//        access.contains(Opcodes.ACC_ENUM) -> "enum"
        else -> "class"
    }
    
    private fun getAccess(access: Int) = when {
        access.contains(Opcodes.ACC_PUBLIC) -> "public"
        access.contains(Opcodes.ACC_PRIVATE) -> "private"
        access.contains(Opcodes.ACC_PROTECTED) -> "protected"
        else -> ""
    }
   
    private fun getFinal(access: Int) = if (access.contains(Opcodes.ACC_FINAL)) "final" else ""
    
    private fun getStatic(access: Int) = if (access.contains(Opcodes.ACC_STATIC)) "static" else ""
    
    private fun getAbstract(access: Int) = if (access.contains(Opcodes.ACC_ABSTRACT)) "abstract" else ""
    
    private fun Int.contains(opcode: Int) = (this and opcode) != 0
    
    private fun String.toType() = when {
        startsWith("Z") -> "boolean"
        startsWith("V") -> "void"
        startsWith("B") -> "byte"
        startsWith("C") -> "char"
        startsWith("S") -> "short"
        startsWith("I") -> "int"
        startsWith("J") -> "long"
        startsWith("F") -> "float"
        startsWith("D") -> "double"
        startsWith("L") -> substring(1)
        startsWith("[") -> {
            val type = substring(1)
            if (type.startsWith("L")) type.substring(1) + "[]" else type + "[]"
        }
        else -> "void"
    }
    
}