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

public class TestQuadratic
{
    @Rule
    public TestName name = new TestName();

    @Test
    public void testIntersection001()
    {
        Quadratic quadratic = new Quadratic(
                new Point(50, 20),
                new Point(10, 30),
                new Point(50, 70)
        );
        Segment segment = new Segment(new Point(35, 10), new Point(50, 80));
        debug(quadratic, segment, Collections.emptyList());
        List<Point> intersections = quadratic.intersections(segment);
        debug(quadratic, segment, intersections);
        assertEquals(2, intersections.size());
        assertEquals("", intersections.get(0).toString());
        assertEquals("", intersections.get(1).toString());
    }

    private void debug(Quadratic quadratic, Segment segment, List<Point> points)
    {
        try
        {
            Document document = GeomTestUtil.createSvgDocument(800, 800);
            double scale = 8.0;
            GeomTestUtil.createSvgQuadratic(document, quadratic, scale);
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
