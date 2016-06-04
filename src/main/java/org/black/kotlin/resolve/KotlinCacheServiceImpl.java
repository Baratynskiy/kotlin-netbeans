package org.black.kotlin.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import java.util.Collection;
import java.util.List;
import org.black.kotlin.model.KotlinAnalysisFileCache;
import org.black.kotlin.project.structure.KotlinProject;
import org.jetbrains.kotlin.analyzer.AnalysisResult;
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.ModuleDescriptor;
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache;
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode;
import org.netbeans.api.project.ui.OpenProjects;


/**
 *
 * @author Александр
 */
public class KotlinCacheServiceImpl implements KotlinCacheService {

    @Override
    public ResolutionFacade getResolutionFacade(List<? extends KtElement> list) {
        return new ResolutionFacade(){
            @Override
            public Project getProject() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public BindingContext analyze(KtElement element, BodyResolveMode bodyResolveMode) {
                KtFile ktFile = element.getContainingKtFile();
                KotlinProject kotlinProject = null;
                for (org.netbeans.api.project.Project project : OpenProjects.getDefault().getOpenProjects()){
                    if (ktFile.getVirtualFile().getUrl().contains(
                            project.getProjectDirectory().toURL().toString())){
                        kotlinProject = (KotlinProject) project;
                        break;
                    }
                }
                if (kotlinProject == null){
                    return BindingContext.EMPTY;
                }
                
                return KotlinAnalysisFileCache.INSTANCE.getAnalysisResult(ktFile, kotlinProject).
                        getAnalysisResult().getBindingContext();
            }

            @Override
            public AnalysisResult analyzeFullyAndGetResult(Collection<? extends KtElement> clctn) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public DeclarationDescriptor resolveToDescriptor(KtDeclaration kd) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ModuleDescriptor getModuleDescriptor() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T getFrontendService(Class<T> type) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T getIdeService(Class<T> type) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T getFrontendService(PsiElement pe, Class<T> type) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T getFrontendService(ModuleDescriptor md, Class<T> type) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        };
    }

    @Override
    public KotlinSuppressCache getSuppressionCache() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
