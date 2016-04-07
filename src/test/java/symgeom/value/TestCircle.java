package symgeom.value;

import org.junit.Test;
import symgeom.geom.Circle;
import symgeom.geom.Point;
import symgeom.geom.Segment;

import java.util.List;

import static org.junit.Assert.*;

public class TestCircle
{
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
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 20), new Point(80, 10));
        List<Point> intersections = circle.intersections(segment);
        assertEquals(0, intersections.size());
    }

    @Test
    public void testIntersection002()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 30), new Point(80, 30));
        List<Point> intersections = circle.intersections(segment);
        assertEquals(1, intersections.size());
        assertEquals(new Point(30, 30), intersections.get(0));
    }

    @Test
    public void testIntersection003()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 20), new Point(80, 60));
        List<Point> intersections = circle.intersections(segment);
        assertEquals(2, intersections.size());
        assertEquals("", intersections.get(0));
        assertEquals("", intersections.get(1));
    }

    @Test
    public void testIntersection004()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(10, 60), new Point(80, 10));
        List<Point> intersections = circle.intersections(segment);
        assertEquals(2, intersections.size());
        assertEquals("", intersections.get(0));
        assertEquals("", intersections.get(1));
    }

    @Test
    public void testIntersection005()
    {
        Circle circle = new Circle(new Point(30, 50), Value.number(20));
        Segment segment = new Segment(new Point(40, 40), new Point(80, 60));
        List<Point> intersections = circle.intersections(segment);
        assertEquals(1, intersections.size());
        assertEquals("", intersections.get(0));
    }
}
