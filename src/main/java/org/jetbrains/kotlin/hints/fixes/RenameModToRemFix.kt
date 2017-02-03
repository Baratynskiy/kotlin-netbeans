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
package org.jetbrains.kotlin.hints.fixes

import org.jetbrains.kotlin.diagnostics.netbeans.parser.KotlinParserResult
import org.jetbrains.kotlin.diagnostics.netbeans.parser.KotlinError
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.hints.KotlinRule
import org.netbeans.modules.csl.api.Hint
import org.netbeans.modules.csl.api.HintFix
import org.netbeans.modules.csl.api.HintSeverity
import org.netbeans.modules.csl.api.OffsetRange

class RenameModToRemFix(kotlinError: KotlinError,
                        parserResult: KotlinParserResult) : KotlinQuickFix(kotlinError, parserResult) {
    
    override val hintSeverity = HintSeverity.WARNING

    override fun isApplicable() = when (kotlinError.diagnostic.factory) {
        Errors.DEPRECATED_BINARY_MOD -> true
        else -> false
    }

    override fun createFixes() = listOf(this)

    override fun getDescription() = "Rename to 'rem'"

    override fun implement() {
        val doc = parserResult.snapshot.source.getDocument(false)
        
        doc.remove(kotlinError.startPosition, kotlinError.endPosition - kotlinError.startPosition)
        doc.insertString(kotlinError.startPosition, "rem", null)
    }
}