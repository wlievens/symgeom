package symgeom.geom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class GeomTestUtil
{
    public static void createSvgCircle(Document document, Circle circle, double scale)
    {
        Element svgCircle = document.createElement("circle");
        svgCircle.setAttribute("cx", String.valueOf(scale * circle.getCenter().getX().approximate()));
        svgCircle.setAttribute("cy", String.valueOf(scale * circle.getCenter().getY().approximate()));
        svgCircle.setAttribute("r", String.valueOf(scale * circle.getRadius().approximate()));
        svgCircle.setAttribute("style", "stroke-width: 1; stroke: blue; fill: none;");
        document.getDocumentElement().appendChild(svgCircle);
    }

    private static String svgXY(Point point, double scale)
    {
        return String.format("%s,%s", point.getX().approximate() * scale, point.getY().approximate() * scale);
    }

    public static void createSvgCubic(Document document, Cubic cubic, double scale)
    {
        Element svgPath = document.createElement("path");
        svgPath.setAttribute("d", "M " + svgXY(cubic.getStart(), scale) + " C " + svgXY(cubic.getControl1(), scale) + " " + svgXY(cubic.getControl2(), scale) + " " + svgXY(cubic.getEnd(), scale));
        svgPath.setAttribute("style", "stroke-width: 1; stroke: blue; fill: none;");
        document.getDocumentElement().appendChild(svgPath);
    }

    public static Document createSvgDocument(int sx, int sy) throws ParserConfigurationException
    {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element svg = document.createElement("svg");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        svg.setAttribute("width", String.valueOf(sx));
        svg.setAttribute("height", String.valueOf(sy));
        document.appendChild(svg);
        return document;
    }

    public static void createSvgPoint(Document document, Point point, double scale)
    {
        Element svgCircle = document.createElement("circle");
        svgCircle.setAttribute("cx", String.valueOf(scale * point.getX().approximate()));
        svgCircle.setAttribute("cy", String.valueOf(scale * point.getY().approximate()));
        svgCircle.setAttribute("r", String.valueOf(3.0));
        svgCircle.setAttribute("style", "stroke-width: 1; stroke: red; fill: rgba(60,200,255,0.5);");
        document.getDocumentElement().appendChild(svgCircle);
    }

    public static void createSvgSegment(Document document, Segment segment, double scale)
    {
        Element svgLine = document.createElement("line");
        svgLine.setAttribute("x1", String.valueOf(scale * segment.getStart().getX().approximate()));
        svgLine.setAttribute("y1", String.valueOf(scale * segment.getStart().getY().approximate()));
        svgLine.setAttribute("x2", String.valueOf(scale * segment.getEnd().getX().approximate()));
        svgLine.setAttribute("y2", String.valueOf(scale * segment.getEnd().getY().approximate()));
        svgLine.setAttribute("style", "stroke-width: 1; stroke: cyan; fill: none;");
        document.getDocumentElement().appendChild(svgLine);
    }

    public static File getTestOutputPath(String testClassName, String testName)
    {
        File file = new File(String.format("target/test-output/%s/%s", testClassName, testName));
        file.mkdirs();
        return file;
    }

    public static void saveDocument(Document document, File file) throws TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(file);
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
    }
}
