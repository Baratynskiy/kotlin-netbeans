package org.black.kotlin.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.black.kotlin.utils.KotlinImageProvider;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor;
import org.jetbrains.kotlin.descriptors.VariableDescriptor;
import org.jetbrains.kotlin.renderer.DescriptorRenderer;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Александр
 */
public class KotlinCompletionItem implements CompletionItem {

    private final String text, proposal;
    private final String type, name;
    private final ImageIcon FIELD_ICON; 
    private static final Color FIELD_COLOR = Color.decode("0x0000B2"); 
    private final int caretOffset, idenStartOffset; 
    private final DeclarationDescriptor descriptor;
    
    public KotlinCompletionItem(int idenStartOffset, int caretOffset, DeclarationDescriptor descriptor) { 
        this.text = descriptor.getName().getIdentifier(); 
        this.idenStartOffset = idenStartOffset;
        this.caretOffset = caretOffset; 
        this.proposal = DescriptorRenderer.ONLY_NAMES_WITH_SHORT_TYPES.render(descriptor);
        this.FIELD_ICON = KotlinImageProvider.INSTANCE.getImage(descriptor);
        this.descriptor = descriptor;
        String[] splitted = proposal.split(":");
        name = splitted[0];
        if (splitted.length > 1){
            type = splitted[1];
        } else {
            type = "";
        }
    }
    
    
    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            doc.remove(idenStartOffset, caretOffset - idenStartOffset);
            if (descriptor instanceof FunctionDescriptor){
                doc.insertString(idenStartOffset, text + "()", null);
            } else{
                doc.insertString(idenStartOffset, text, null);
            }
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent ke) {
    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(proposal, null, graphics, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(FIELD_ICON, name, type, g, defaultFont, 
                (selected ? Color.white : FIELD_COLOR), width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent jtc) {
        return false;
    }

    @Override
    public int getSortPriority() {
        if (descriptor instanceof VariableDescriptor){
            return 20;
        } else if (descriptor instanceof FunctionDescriptor){
            return 30;
        } else if (descriptor instanceof ClassDescriptor){
            return 40;
        } else if (descriptor instanceof PackageFragmentDescriptor 
                || descriptor instanceof PackageViewDescriptor){
            return 10;
        } else {
            return 150;
        }
    }

    @Override
    public CharSequence getSortText() {
        return name;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return proposal;
    }
    
}
