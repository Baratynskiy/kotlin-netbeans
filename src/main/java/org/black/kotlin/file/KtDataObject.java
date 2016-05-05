package org.black.kotlin.file;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.black.kotlin.filesystem.lightclasses.KotlinLightClassGeneration;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 *
 * @author Александр KtDataObject class represents .kt file in NetBeans IDE
 */
@Messages({
    "LBL_Kt_LOADER=Files of Kt"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Kt_LOADER",
        mimeType = "text/x-kt",
        extension = {"kt", "KT"}
)
@DataObject.Registration(
        mimeType = "text/x-kt",
        iconBase = "org/black/kotlin/kt.png",
        displayName = "#LBL_Kt_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    ),
    @ActionReference(
            path = "Loaders/text/x-kt/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class KtDataObject extends MultiDataObject {

    public KtDataObject(final FileObject file, MultiFileLoader loader) throws IOException {
        super(file, loader);
        registerEditor("text/x-kt", true);
        this.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DataObject.PROP_MODIFIED)) {
                    KotlinLightClassGeneration.INSTANCE.generate(file);
                } 
            }
        });
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_Kt_EDITOR",
            iconBase = "org/black/kotlin/kt.png",
            mimeType = "text/x-kt",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "Kt",
            position = 1000
    )
    @Messages("LBL_Kt_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

}
