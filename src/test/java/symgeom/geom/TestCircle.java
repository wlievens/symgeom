package symgeom.geom;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.w3c.dom.Document;
import symgeom.value.Value;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestCircle
{
    @Rule
    public TestName name = new TestName();

    @Test
    public void testContains()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        assertTrue(circle.contains(new Point(35, 56)).isTrue());
        assertFalse(circle.contains(new Point(15, 36)).isTrue());
    }

    @Test
    public void testInside()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        assertTrue(circle.inside(new Point(11, 50)).isTrue());
        assertFalse(circle.inside(new Point(10, 50)).isTrue());
    }

    @Test
    public void testGetArea()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        assertEquals(Value.number(400).multiply(Value.PI), circle.getArea());
    }

    @Test
    public void testGetCircumference()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        assertEquals(Value.number(40).multiply(Value.PI), circle.getCircumference());
    }

    @Test
    public void testIntersection001()
    {
        // No intersection
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 20), new Point(80, 10));
        List<Point> intersections = circle.intersections(segment);
        assertEquals(0, intersections.size());
    }

    @Test
    public void testIntersection002()
    {
        // Horizontal segment that touches the circle, only one intersection point (tangent)
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 30), new Point(80, 30));
        List<Point> intersections = circle.intersections(segment);
        System.out.println(intersections);
        debug(circle, segment, intersections);
        assertEquals(1, intersections.size());
        assertEquals(new Point(30, 30), intersections.get(0));
    }

    @Test
    public void testIntersection003()
    {
        // Intersection by segment with a descending slope
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 20), new Point(80, 60));
        List<Point> intersections = circle.intersections(segment);
        debug(circle, segment, intersections);
        assertEquals(2, intersections.size());
        System.out.println(intersections);
        assertEquals("Point(x=38 + (-14 / 13) * sqrt(91), y=36 + (-8 / 13) * sqrt(91))", intersections.get(0).toString());
        assertEquals("Point(x=38 + (14 / 13) * sqrt(91), y=36 + (8 / 13) * sqrt(91))", intersections.get(1).toString());
    }

    @Test
    public void testIntersection004()
    {
        // Intersection by segment with an ascending slope
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 60), new Point(80, 10));
        debug(circle, segment, Collections.emptyList());
        List<Point> intersections = circle.intersections(segment);
        debug(circle, segment, intersections);
        assertEquals(2, intersections.size());
        assertEquals("Point(x=1035 / 37 + (35 / 37) * sqrt(287), y=1745 / 37 + (-25 / 37) * sqrt(287))", intersections.get(0).toString());
        assertEquals("Point(x=1035 / 37 + (-35 / 37) * sqrt(287), y=1745 / 37 + (25 / 37) * sqrt(287))", intersections.get(1).toString());
    }

    @Test
    public void testIntersection005()
    {
        // Intersection by segment that starts inside the circle
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(40, 40), new Point(80, 60));
        List<Point> intersections = circle.intersections(segment);
        debug(circle, segment, intersections);
        assertEquals(1, intersections.size());
        assertEquals("Point(x=36 + 4 * sqrt(11), y=38 + 2 * sqrt(11))", intersections.get(0).toString());
    }

    private void debug(Circle circle, Segment segment, List<Point> points)
    {
        try
        {
            Document document = GeomTestUtil.createSvgDocument(800, 800);
            double scale = 8.0;
            GeomTestUtil.createSvgCircle(document, circle, scale);
            GeomTestUtil.createSvgSegment(document, segment, scale);
            points.forEach(point -> GeomTestUtil.createSvgPoint(document, point, scale));
            File file = new File(getTestOutputPath(), "debug.svg");
            GeomTestUtil.saveDocument(document, file);
        }
        catch (TransformerException | ParserConfigurationException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private File getTestOutputPath()
    {
        return GeomTestUtil.getTestOutputPath(getClass().getSimpleName(), name.getMethodName());
    }
}
