package org.black.kotlin.utils;

import javax.swing.ImageIcon;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.descriptors.VariableDescriptor;
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Александр
 */
public class KotlinImageProvider {

    public static final KotlinImageProvider INSTANCE = new KotlinImageProvider();
    
    private final String imagesLocation = "org/black/kotlin/completionIcons/";
    
    private KotlinImageProvider(){}
    
    public ImageIcon getImage(DeclarationDescriptor descriptor){
        if (descriptor instanceof ClassDescriptor){
            ClassDescriptor classDescriptor = (ClassDescriptor) descriptor;
            switch (classDescriptor.getKind()){
                case ANNOTATION_CLASS:
                    return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "annotation.png"));
                case ENUM_CLASS:
                    return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "enum.png"));
                case INTERFACE:
                    return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "interface.png"));
                case CLASS:
                default:
                    return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "class.png"));
            }
        } else if (descriptor instanceof FunctionDescriptor){
            return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "method.png"));
        } else if (descriptor instanceof VariableDescriptor){
            return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "field.png"));
        } else if (descriptor instanceof PackageFragmentDescriptor || 
                descriptor instanceof PackageViewDescriptor){
            return new ImageIcon(ImageUtilities.loadImage(imagesLocation + 
                            "package.png"));
        } else
            return null;
    } 
    
}
