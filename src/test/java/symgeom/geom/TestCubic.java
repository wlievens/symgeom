package symgeom.geom;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCubic
{
    @Rule
    public TestName name = new TestName();

    @Test
    public void testIntersection001()
    {
        Cubic cubic = new Cubic(
                new Point(50, 20),
                new Point(10, 30),
                new Point(120, 90),
                new Point(50, 70)
        );
        Segment segment = new Segment(new Point(35, 10), new Point(70, 90));
        debug(cubic, segment, Collections.emptyList());
        List<Point> intersections = cubic.intersections(segment);
        debug(cubic, segment, intersections);
        assertEquals(0, intersections.size());
    }

    private void debug(Cubic cubic, Segment segment, List<Point> points)
    {
        try
        {
            Document document = GeomTestUtil.createSvgDocument(800, 800);
            double scale = 8.0;
            GeomTestUtil.createSvgCubic(document, cubic, scale);
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
