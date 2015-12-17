package org.black.kotlin.highlighter;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.black.kotlin.highlighter.netbeans.KotlinToken;
import org.black.kotlin.highlighter.netbeans.KotlinTokenId;
import org.black.kotlin.model.KotlinEnvironment;
import org.black.kotlin.model.KotlinLightVirtualFile;
import org.black.kotlin.resolve.KotlinAnalyzer;
import org.black.kotlin.resolve.NBAnalyzerFacadeForJVM;
import org.black.kotlin.utils.HintsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.psi.KtFile;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.lexer.LexerInput;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
/**
 *
 * @author Александр
 */
public class KotlinTokenScanner {

    private final KotlinTokensFactory kotlinTokensFactory;

    private KtFile ktFile = null;
    private File syntaxFile = null;
    private int offset = 0;
    private int rangeEnd = 0;
    private PsiElement lastElement = null;
    private List<KotlinToken> kotlinTokens = null;
    private int tokensNumber = 0;
    private LexerInput input;

    
    public KotlinTokenScanner(LexerInput input) {
        kotlinTokensFactory = new KotlinTokensFactory();
        this.input = input;
             
        TopComponent active = TopComponent.getRegistry().getActivated();
        DataObject dataLookup = active.getLookup().lookup(DataObject.class);
        
        createSyntaxFile();
        try {
            
           FileObject currFileObject = dataLookup.getPrimaryFile();
           ktFile = parseFile(syntaxFile);
//           System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +currFileObject.getPath());
           KotlinEnvironment ke = KotlinEnvironment.getEnvironment(OpenProjects.getDefault().getOpenProjects()[0]);
//            //System.out.println("---******----" + ke.getProject().getName());
//            //System.out.println("$£%££%         "+OpenProjects.getDefault().getMainProject().toString());
//            Map<FileObject, List<ErrorDescription>> annotations = 
//                    HintsUtil.parseAnalysisResult(NBAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(ke.getProject(), 
//                            OpenProjects.getDefault().getOpenProjects()[0], 
//                            Lists.newArrayList(ktFile)));
           File currentFile = FileUtil.toFile(currFileObject);
           KtFile currKTF = parseFile(currentFile);
           Map<FileObject, List<ErrorDescription>> annotations = 
                    HintsUtil.parseAnalysisResult(KotlinAnalyzer.analyzeFile(ke.getProject(), 
                            OpenProjects.getDefault().getOpenProjects()[0], 
                            currKTF));
            HintsController.setErrors(currFileObject, 
                    "test_1", 
                    annotations.get(currFileObject));
//            HintsController.setErrors(currFileObject, 
//            "test", 
//            HintsUtil.createTestErrDesc(currFileObject));
            deleteSyntaxFile();
            this.rangeEnd = (int) syntaxFile.length();
            createListOfKotlinTokens();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        catch (NullPointerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void createSyntaxFile(){
        syntaxFile = new File("syntax");
        StringBuilder builder = new StringBuilder("");
        int num = -1000;
        while (num != LexerInput.EOF){
            num = input.read();
            builder.append((char) num);
        }
        
        CharSequence readText = builder.toString();//input.readText();
        input.backup(input.readLengthEOF());
        
        try {
            PrintWriter writer = new PrintWriter(syntaxFile,"UTF-8");
            writer.print(readText);
//            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.
//                            Message(builder.toString()));
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void readSyntaxFile(){
        try {
            StringBuilder builder = new StringBuilder("");
            String curLine;
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(syntaxFile));
            while((curLine = br.readLine()) != null){
                builder.append(curLine);
            }
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.
                            Message(builder.toString()));
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void deleteSyntaxFile(){
        if (syntaxFile != null)
            syntaxFile.deleteOnExit();
    }
    
    private void createListOfKotlinTokens() {
        kotlinTokens = new ArrayList();
        for (;;) {

            lastElement = ktFile.findElementAt(offset);
            
            if (ktFile == null) {
                kotlinTokens.add(new KotlinToken(
                        new KotlinTokenId(TokenType.EOF.name(), TokenType.EOF.name(), 7), "",
                        0, TokenType.EOF));
                tokensNumber = kotlinTokens.size();
                break;
            }

            if (lastElement != null) {

                offset = lastElement.getTextRange().getEndOffset();
                TokenType tokenType = kotlinTokensFactory.getToken(lastElement);

                kotlinTokens.add(new KotlinToken(
                        new KotlinTokenId(tokenType.name(), tokenType.name(), tokenType.getId()),
                        lastElement.getText(), lastElement.getTextOffset(),
                        tokenType));
                tokensNumber = kotlinTokens.size();
            } else {
                tokensNumber = kotlinTokens.size();
                break;
            }

        }

    }

    @Nullable
    private KtFile parseFile(@NotNull File file) throws IOException {
        return parseText(FileUtil.loadFile(file, null, true), file);
    }

    @Nullable
    private KtFile parseText(@NotNull String text, @NotNull File file) {
        StringUtil.assertValidSeparators(text);

        Project project = KotlinEnvironment.getEnvironment(
                OpenProjects.getDefault().getOpenProjects()[0]).getProject();

        LightVirtualFile virtualFile = new KotlinLightVirtualFile(file, text);
        virtualFile.setCharset(CharsetToolkit.UTF8_CHARSET);

        PsiFileFactoryImpl psiFileFactory = (PsiFileFactoryImpl) PsiFileFactory.getInstance(project);

        return (KtFile) psiFileFactory.trySetupPsiForFile(virtualFile, KotlinLanguage.INSTANCE, true, false);
    }


    public KotlinToken<KotlinTokenId> getNextToken() {

        KotlinToken ktToken = null;

        if (tokensNumber > 0) {
            ktToken = kotlinTokens.get(kotlinTokens.size() - tokensNumber);
            tokensNumber--;
            if (ktToken != null) {
                int num = ktToken.length();
                
                while (num > 0) {
                    input.read();
                    num--;
                }
                
            }
            return ktToken;
        } else {
            input.read();
            return new KotlinToken(
                    new KotlinTokenId(TokenType.EOF.name(), TokenType.EOF.name(), 7), "",
                    0, TokenType.EOF);
        }

    }

    
}

