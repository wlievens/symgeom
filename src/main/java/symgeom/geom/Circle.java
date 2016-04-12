package symgeom.geom;

import lombok.RequiredArgsConstructor;
import symgeom.value.Sign;
import symgeom.value.Tribool;
import symgeom.value.Value;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@lombok.Value
@RequiredArgsConstructor
public class Circle
{
    private final Point center;
    private final Value radius;

    public Tribool contains(Point point)
    {
        return center.getDistance(point).lteq(radius);
    }

    public Value getArea()
    {
        return radius.multiply(radius).multiply(Value.PI).simplify();
    }

    public Tribool inside(Point point)
    {
        return center.getDistance(point).lt(radius);
    }

    public Value getCircumference()
    {
        return radius.multiply(Value.number(2)).multiply(Value.PI).simplify();
    }

    public List<Point> intersections(Segment segment)
    {
        System.out.println("circle  : " + this);
        System.out.println("segment : " + segment);

        Value dx = segment.getDeltaX();
        Value dy = segment.getDeltaY();
        Value dr = segment.getLength();

        Value cx = center.getX();
        Value cy = center.getY();
        Value x1 = segment.getStart().getX().subtract(cx);
        Value y1 = segment.getStart().getY().subtract(cy);
        Value x2 = segment.getEnd().getX().subtract(cx);
        Value y2 = segment.getEnd().getY().subtract(cy);

        Value x1y2 = x1.multiply(y2);
        Value x2y1 = x2.multiply(y1);
        Value d = x1y2.subtract(x2y1).simplify();

        Value discriminant = radius.square().multiply(dr.square()).subtract(d.square()).simplify();
        Sign discriminantSign = discriminant.getSign();
        if (discriminantSign.isUnknown())
        {
            throw new IllegalStateException();
        }
        if (discriminantSign.isNegative())
        {
            return Collections.emptyList();
        }

        Value[] signs;
        if (discriminantSign.isPositive())
        {
            signs = new Value[] { Value.number(-1), Value.number(+1) };
        }
        else
        {
            signs = new Value[] { Value.number(1) };
        }

        Sign dySign = dy.getSign();
        if (dySign.isUnknown())
        {
            throw new IllegalStateException();
        }
        int signX = dySign.isNegative() ? -1 : +1;

        List<Point> points = new LinkedList<>();
        for (Value factor : signs)
        {
            Value ix = cx.add(d.multiply(dy).add(factor.multiply(Value.number(signX)).multiply(dx).multiply(discriminant.sqrt())).divide(dr.square())).simplify();
            Value iy = cy.add(d.negate().multiply(dx).add(factor.multiply(dy.abs()).multiply(discriminant.sqrt())).divide(dr.square())).simplify();
            System.out.println(ix);
            System.out.println(iy);
            Point point = new Point(ix, iy);
            Tribool contains = segment.contains(point);
            if (contains.isUnknown())
            {
                throw new IllegalStateException(String.format("UNKNOWN: %s contains %s", segment, point));
            }
            if (contains.isTrue())
            {
                points.add(point);
            }
        }

        return points;
    }
}
