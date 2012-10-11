package ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.LinkedList;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jhotdraw.app.AbstractView;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.io.ImageOutputFormat;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.gui.PlacardScrollPaneLayout;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.undo.UndoRedoManager;

import domain.UmlDesignerFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@SuppressWarnings("serial")
public class UmlDesignerView extends AbstractView {
    private JScrollPane scrollPane;
    private DefaultDrawingView view;
    private UndoRedoManager undo;
    private DrawingEditor editor;
    
    public UmlDesignerView() {
    	initComponents();
    	scrollPane.setLayout(new PlacardScrollPaneLayout());
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setEditor(new DefaultDrawingEditor());
        undo = new UndoRedoManager();
        view.setDrawing(createDrawing());
        view.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setHasUnsavedChanges(undo.hasSignificantEdits());
            }
        });
    }
	
    private void initComponents() {
        scrollPane = new JScrollPane();
        view = new DefaultDrawingView();
        setLayout(new BorderLayout());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(view);
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }
    
    private void initActions() {
        getActionMap().put(UndoAction.ID, undo.getUndoAction());
        getActionMap().put(RedoAction.ID, undo.getRedoAction());
    }
    
    @Override
    protected void setHasUnsavedChanges(boolean newValue) {
        super.setHasUnsavedChanges(newValue);
        undo.setHasSignificantEdits(newValue);
    }
    
    /**
     * Creates a new Drawing for this view.
     */
    protected Drawing createDrawing() {
        DefaultDrawing drawing = new DefaultDrawing();
        DOMStorableInputOutputFormat ioFormat =
                new DOMStorableInputOutputFormat(new UmlDesignerFactory());
        LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
        inputFormats.add(ioFormat);
        drawing.setInputFormats(inputFormats);
        LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
        outputFormats.add(ioFormat);
        outputFormats.add(new ImageOutputFormat());
        drawing.setOutputFormats(outputFormats);
        return drawing;
    }
    
    /**
     * Writes the view to the specified uri.
     */
    @Override
    public void write(URI f, URIChooser chooser) throws IOException {
        Drawing drawing = view.getDrawing();
        OutputFormat outputFormat = drawing.getOutputFormats().get(0);
        outputFormat.write(f, drawing);
    }

    /**
     * Reads the view from the specified uri.
     */
    @Override
    public void read(URI f, URIChooser chooser) throws IOException {
        try {
            final Drawing drawing = createDrawing();
            InputFormat inputFormat = drawing.getInputFormats().get(0);
            inputFormat.read(f, drawing, true);
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(drawing);
                    view.getDrawing().addUndoableEditListener(undo);
                    undo.discardAllEdits();
                }
            });
        } catch (InterruptedException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        } catch (InvocationTargetException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        }
    }

    /**
     * Clears the view.
     */
    @Override
    public void clear() {
        final Drawing newDrawing = createDrawing();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(newDrawing);
                    view.getDrawing().addUndoableEditListener(undo);
                    undo.discardAllEdits();
                }
            });
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
	
    @Override
    public boolean canSaveTo(URI uri) {
        return uri.getPath().endsWith(".xml");
    }

    public DrawingEditor getEditor() {
        return editor;
    }
    
	public void setEditor(DefaultDrawingEditor newValue) {
        DrawingEditor oldValue = editor;
        if (oldValue != null) {
            oldValue.remove(view);
        }
        editor = newValue;
        if (newValue != null) {
            newValue.add(view);
        }
	}

}
