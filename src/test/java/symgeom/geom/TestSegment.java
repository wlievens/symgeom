package symgeom.geom;

import org.junit.Assert;
import org.junit.Test;
import symgeom.value.Value;

import java.util.Optional;

import static org.junit.Assert.*;

public class TestSegment
{
    @Test
    public void testGetLength()
    {
        Segment segment = new Segment(new Point(20, 30), new Point(50, 80));
        Assert.assertEquals(Value.number(10).multiply(Value.number(34).sqrt()), segment.getLength());
    }

    @Test
    public void testGetMidpoint()
    {
        Segment segment = new Segment(new Point(20, 30), new Point(50, 80));
        assertEquals(new Point(35, 55), segment.getMidpoint());
    }

    @Test
    public void testIntersection001()
    {
        Segment segmentA = new Segment(new Point(20, 30), new Point(60, 80));
        Segment segmentB = new Segment(new Point(40, 10), new Point(50, 120));
        Optional<Point> intersection = segmentA.intersection(segmentB);
        assertTrue(intersection.isPresent());
        assertEquals(new Point(Value.number(580).divide(Value.number(13)), Value.number(790).divide(Value.number(13))), intersection.get());
    }

    @Test
    public void testIntersection002()
    {
        Segment segmentA = new Segment(new Point(120, 30), new Point(60, 80));
        Segment segmentB = new Segment(new Point(40, 10), new Point(50, 120));
        Optional<Point> intersection = segmentA.intersection(segmentB);
        assertFalse(intersection.isPresent());
    }
}
