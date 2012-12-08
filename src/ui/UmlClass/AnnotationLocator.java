package ui.UmlClass;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

public class AnnotationLocator implements Locator, DOMStorable {
	private double _verticalOffset, _relativePosition;
	
	public AnnotationLocator(double verticalOffset, double relativePosition) {
		_verticalOffset = verticalOffset;
		_relativePosition = relativePosition;
	}
	
	/**
     * Returns the coordinates of the relative point on the path
     * of the specified bezier figure.
     */
    public Point2D.Double getRelativePoint(BezierFigure owner) {
        Point2D.Double point = owner.getPointOnPath((float) _relativePosition, 3);        
        Point2D.Double p = new Point2D.Double(point.x + 5, point.y + _verticalOffset);        
        if (java.lang.Double.isNaN(p.x)) p = point;        
        return p;
    }
    
    public Point2D.Double getRelativeLabelPoint(BezierFigure owner, Figure label) {
        // Get a point on the path an the next point on the path
        Point2D.Double point = owner.getPointOnPath((float) _relativePosition, 3);
        if (point == null) {
            return new Point2D.Double(0,0);
        }
        
        Point2D.Double p = new Point2D.Double(point.x + 5, point.y + _verticalOffset);
        if (java.lang.Double.isNaN(p.x)) p = point;
        
        Dimension2DDouble labelDim = label.getPreferredSize();
        if (_relativePosition == 0.5 && 
                p.x >= point.x - 5 / 2 && 
                p.x <= point.x + 5 / 2) {
            if (p.y >= point.y) {
                // South East
                return new Point2D.Double(p.x - labelDim.width / 2, p.y+_verticalOffset);
            } else {
                // North East
                return new Point2D.Double(p.x - labelDim.width / 2, p.y - labelDim.height+_verticalOffset);
            }
        } else {
            if (p.x >= point.x) {
                if (p.y >= point.y) {
                    // South East
                    return new Point2D.Double(p.x, p.y+_verticalOffset);
                } else {
                    // North East
                    return new Point2D.Double(p.x, p.y - labelDim.height+_verticalOffset);
                }
            } else {
                if (p.y >= point.y) {
                    // South West
                    return new Point2D.Double(p.x - labelDim.width,  p.y+_verticalOffset);
                } else {
                    // North West
                    return new Point2D.Double(p.x - labelDim.width, p.y - labelDim.height+_verticalOffset);
                }
            }
        }
    }

    @Override
    public void read(DOMInput in) {
        _relativePosition = in.getAttribute("relativePosition", 0d);
        _verticalOffset = in.getAttribute("verticalOffset", 0d);        
    }
    
    @Override
    public void write(DOMOutput out) {
        out.addAttribute("relativePosition", _relativePosition);
        out.addAttribute("verticalOffset", _verticalOffset);        
    }

	@Override
	public Double locate(Figure owner) {
		return getRelativePoint((BezierFigure) owner);
	}

	@Override
	public Double locate(Figure owner, Figure label) {
        Point2D.Double relativePoint = getRelativeLabelPoint((BezierFigure) owner, label);
        return relativePoint;
	}

}
