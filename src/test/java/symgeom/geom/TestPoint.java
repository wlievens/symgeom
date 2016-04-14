package symgeom.geom;

import org.junit.Assert;
import org.junit.Test;
import symgeom.geom.Point;
import symgeom.value.Value;

import static org.junit.Assert.assertEquals;

public class TestPoint
{
    @Test
    public void test001()
    {
        Point point = new Point(0, 0);
        Assert.assertEquals(Value.ZERO, point.getX());
        assertEquals(Value.ZERO, point.getY());
    }
}
