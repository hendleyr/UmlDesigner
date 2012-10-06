package ui.UmlClass;

import java.awt.Font;
import java.awt.Insets;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;

import domain.UmlClass.UmlClassModel;


@SuppressWarnings("serial")
public class UmlClassFigure extends GraphicalCompositeFigure {
	private UmlClassModel model;
	
    public UmlClassModel getModellerClass() {
        return model;
    }
    protected void setModellerClass(UmlClassModel newModel) {
    	model = newModel;
    }
    
    private Font attributeFont;
    private Font methodFont;

    /**
     * Direct reference to a composite figure which stores text figures for attribute names.
     * This figure is also part of this composite container.
     */
    private GraphicalCompositeFigure myAttributesFigure;

    /**
     * Direct reference to a composite figure which stores text figures for method names.
     * This figure is also part of this composite container.
     */
    private GraphicalCompositeFigure myMethodsFigure;

    /**
     * TextFigure for editing the class name
     */
    private TextFigure myClassNameFigure;
    
    /**
     * Create a new instance of ClassFigure with a RectangleFigure as presentation figure
     */    
    public UmlClassFigure() {
        this(new RectangleFigure());
    }

    /**
     * Create a new instance of ClassFigure with a given presentation figure
     *
     * @param newPresentationFigure presentation figure
     */    
    public UmlClassFigure(Figure newPresentationFigure) {
        super(newPresentationFigure);
    }
    
    /**
     * Hook method called to initizialize a ClassFigure.
     * It is called from the constructors and the clone() method.
     */
    protected void initialize() {
        // start with an empty Composite
    	this.removeAllChildren();

        // set the fonts used to print attributes and methods
        attributeFont = new Font("Helvetica", Font.PLAIN, 12);
        methodFont = new Font("Helvetica", Font.PLAIN, 12);

        // create a new Model object associated with this View figure
       setModellerClass(new UmlClassModel());

        // create a TextFigure responsible for the class name
        setClassNameFigure(new TextFigure() {
            public void setText(String newText) {
                super.setText(newText);
                getModellerClass().setName(newText);
                //update();
            }
        });
        //getClassNameFigure().setFont(new Font("Helvetica", Font.BOLD, 12));
        getClassNameFigure().setText(getModellerClass().getName());
        
        // add the TextFigure to the Composite
        GraphicalCompositeFigure nameFigure = new GraphicalCompositeFigure(new SeparatorFigure());
        nameFigure.add(getClassNameFigure());
        //nameFigure.getLayouter().setInsets(new Insets(0, 4, 0, 0));
        add(nameFigure);

        // create a figure responsible for maintaining attributes
        setAttributesFigure(new GraphicalCompositeFigure(new SeparatorFigure()));
        //getAttributesFigure().getLayouter().setInsets(new Insets(4, 4, 4, 0));
        // add the figure to the Composite
        add(getAttributesFigure());

        // create a figure responsible for maintaining methods
        setMethodsFigure(new GraphicalCompositeFigure(new SeparatorFigure()));
        //getMethodsFigure().getLayouter().setInsets(new Insets(4, 4, 4, 0));
        // add the figure to the Composite
        add(getMethodsFigure());

        //setAttribute(Figure.POPUP_MENU, createPopupMenu());
        //super.initialize();
    }
    
    /**
     * Set the figure which containes all figures representing attribute names.
     *
     * @param newAttributesFigure container for other figures
     */
    protected void setAttributesFigure(GraphicalCompositeFigure newAttributesFigure) {
        myAttributesFigure = newAttributesFigure;
    }

    /**
     * Return the figure which containes all figures representing attribute names.
     *
     * @return container for other figures
     */
    public GraphicalCompositeFigure getAttributesFigure() {
        return myAttributesFigure;
    }

    /**
     * Set the figure which containes all figures representing methods names.
     *
     * @param newMethodsFigure container for other figures
     */
    protected void setMethodsFigure(GraphicalCompositeFigure newMethodsFigure) {
        myMethodsFigure = newMethodsFigure;
    }

    /**
     * Return the figure which containes all figures representing method names.
     *
     * @return container for other figures
     */
    public GraphicalCompositeFigure getMethodsFigure() {
        return myMethodsFigure;
    }

    /**
     * Set the class name text figure responsible for handling user input
     *
     * @param newClassNameFigure text figure for the class name
     */
    protected void setClassNameFigure(TextFigure newClassNameFigure) {
        myClassNameFigure = newClassNameFigure;
    }
    
    /**
     * Return the text figure for the class name
     *
     * @return text figure for the class name
     */
    public TextFigure getClassNameFigure() {
        return myClassNameFigure;
    }
}
