package symgeom.value;

import org.junit.Test;
import symgeom.geom.Point;

import static org.junit.Assert.assertEquals;

public class TestPoint
{
    @Test
    public void test001()
    {
        Point point = new Point(0, 0);
        assertEquals(Value.ZERO, point.getX());
        assertEquals(Value.ZERO, point.getY());
    }
}
