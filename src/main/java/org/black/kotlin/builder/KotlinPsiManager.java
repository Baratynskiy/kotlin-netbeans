package org.black.kotlin.builder;

import com.google.common.collect.Sets;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.black.kotlin.model.KotlinEnvironment;
import org.black.kotlin.model.KotlinLightVirtualFile;
import org.black.kotlin.project.KotlinProject;
import org.black.kotlin.project.KotlinProjectConstants;
import org.black.kotlin.utils.ProjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.psi.KtFile;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class KotlinPsiManager {
    
    public static final KotlinPsiManager INSTANCE = new KotlinPsiManager();
    
    private final Map<FileObject, KtFile> cachedKtFiles = 
            new HashMap<FileObject, KtFile>();
    
    private KotlinPsiManager(){}
    
    @NotNull
    public Set<FileObject> getFilesByProject(KotlinProject project){
        Set<FileObject> ktFiles = Sets.newLinkedHashSet();
        
        for (SourceGroup srcGroup : project.getKotlinSources().
                getSourceGroups(KotlinProjectConstants.KOTLIN_SOURCE.toString())){
            for (FileObject file : srcGroup.getRootFolder().getChildren()){
                if (isKotlinFile(file)){
                    ktFiles.add(file);
                }
            }
        }
        
        return ktFiles;
    }
    
        /**
     * This method parses the input file. 
     * @param file syntaxFile that was created with createSyntaxFile method
     * @return the result of {@link #parseText(java.lang.String, java.io.File) parseText} method
     * @throws IOException 
     */
    @Nullable
    private KtFile parseFile(@NotNull FileObject file) throws IOException {
        return parseText(StringUtilRt.convertLineSeparators(file.asText()), file);
    }

    /**
     * This method parses text from the input file.
     * @param text Text of temporary file.
     * @param file syntaxFile that was created with createSyntaxFile method
     * @return {@link KtFile}
     */
    @Nullable
    public KtFile parseText(@NotNull String text, @NotNull FileObject file) {
        StringUtil.assertValidSeparators(text);

        KotlinProject kotlinProject = ProjectUtils.getKotlinProjectForFileObject(file);
        if (kotlinProject == null){
            return null;
        }
        
        com.intellij.openapi.project.Project project = KotlinEnvironment.getEnvironment(
                kotlinProject).getProject();

        LightVirtualFile virtualFile = new KotlinLightVirtualFile(file, text);
        virtualFile.setCharset(CharsetToolkit.UTF8_CHARSET);

        PsiFileFactoryImpl psiFileFactory = (PsiFileFactoryImpl) PsiFileFactory.getInstance(project);
        
        return (KtFile) psiFileFactory.trySetupPsiForFile(virtualFile, KotlinLanguage.INSTANCE, true, false);
    }
    
    public KtFile parseTextForDiagnostic(@NotNull String text, @NotNull FileObject file) {
        StringUtil.assertValidSeparators(text);

        if (cachedKtFiles.containsKey(file)) {
            updatePsiFile(text, file);
        } else {
            try {
                KtFile ktFile = parseFile(file);
                cachedKtFiles.put(file, ktFile);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return cachedKtFiles.get(file);
    }
    
    @NotNull
    public KtFile getParsedFile(@NotNull FileObject file) throws IOException {
        if (!cachedKtFiles.containsKey(file)) {
            KtFile ktFile = parseFile(file);
            cachedKtFiles.put(file, ktFile);
        } else {
            updatePsiFile(file);
        }
            
        return cachedKtFiles.get(file);
    }
    
    private void updatePsiFile(@NotNull FileObject file) throws IOException{
        String code = file.asText();
        String sourceCodeWithoutCR = StringUtilRt.convertLineSeparators(code);
        PsiFile currentParsedFile = cachedKtFiles.get(file);
        if (!currentParsedFile.getText().equals(sourceCodeWithoutCR)) {
            KtFile ktFile = parseText(sourceCodeWithoutCR, file);
            cachedKtFiles.put(file, ktFile);
        }
    }
    
    private void updatePsiFile(@NotNull String sourceCode, @NotNull FileObject file){
        String sourceCodeWithoutCR = StringUtilRt.convertLineSeparators(sourceCode);
        PsiFile currentParsedFile = cachedKtFiles.get(file);
        if (!currentParsedFile.getText().equals(sourceCodeWithoutCR)) {
            KtFile ktFile = parseText(sourceCodeWithoutCR, file);
            cachedKtFiles.put(file, ktFile);
        }
    }
    
    @Nullable
    public KtFile getParsedKtFileForSyntaxHighlighting(@NotNull String text){
        String sourceCode = StringUtilRt.convertLineSeparators(text);
        
        KotlinProject kotlinProject = null;
        
        for (Project proj : OpenProjects.getDefault().getOpenProjects()){
            if (proj instanceof KotlinProject){
                kotlinProject = (KotlinProject) proj;
                break;
            }
        }
        
        com.intellij.openapi.project.Project project = KotlinEnvironment.getEnvironment(kotlinProject).getProject();

        PsiFileFactoryImpl psiFileFactory = (PsiFileFactoryImpl) PsiFileFactory.getInstance(project);
        
        return (KtFile) psiFileFactory.createFileFromText(KotlinLanguage.INSTANCE, sourceCode);
    }
    
    public boolean isKotlinFile(@NotNull FileObject file){
        return KotlinFileType.INSTANCE.getDefaultExtension().equals(file.getExt());
    }
    
}
