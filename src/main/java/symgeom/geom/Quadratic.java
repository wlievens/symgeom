package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.Sign;
import symgeom.value.Tribool;
import symgeom.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static symgeom.value.Value.*;

@Getter
@RequiredArgsConstructor
public class Quadratic
{
    private final Point start;
    private final Point control;
    private final Point end;

    public List<Point> intersections(Segment segment)
    {
        // https://www.reddit.com/r/MathHelp/comments/3pt8l5/quadratic_bezier_curve_line_intersections_the/

        Point p1 = this.start;
        Point p2 = this.control;
        Point p3 = this.end;
        Point a1 = segment.getStart();
        Point a2 = segment.getEnd();

        Point normal = new Point(
                a1.getY().subtract(a2.getY()).simplify(),
                a2.getX().subtract(a1.getX()).simplify()
        );

        Point c2 = new Point(
                p1.getX().add(p2.getX().multiply(number(-2))).add(p3.getX()).simplify(),
                p1.getY().add(p2.getY().multiply(number(-2))).add(p3.getY()).simplify()
        );

        Point c1 = new Point(
                p1.getX().multiply(number(-2)).add(p2.getX().multiply(number(2))).simplify(),
                p1.getY().multiply(number(-2)).add(p2.getY().multiply(number(2))).simplify()
        );

        Point c0 = p1;

        Value coefficient = a1.getX().multiply(a2.getY()).subtract(a2.getX().multiply(a1.getY())).simplify();
        Value a = normal.getX().multiply(c2.getX()).add(normal.getY().multiply(c2.getY())).simplify();
        Value b = (normal.getX().multiply(c1.getX()).add(normal.getY().multiply(c1.getY()))).divide(a).simplify();
        Value c = (normal.getX().multiply(c0.getX()).add(normal.getY().multiply(c0.getY())).add(coefficient)).divide(a).simplify();
        Value d = b.square().subtract(c.multiply(number(4))).simplify();

        System.out.println(c2);
        System.out.println(c1);
        System.out.println(c0);
        System.out.println(coefficient);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);

        Sign dSign = d.getSign();
        if (dSign.isUnknown())
        {
            throw new IllegalStateException();
        }

        List<Value> roots = new ArrayList<>();

        if (dSign.isPositive())
        {
            Value e = d.sqrt().simplify();
            System.out.println(e);

            roots.add(b.negate().add(e).divide(number(2)).simplify());
            roots.add(b.negate().subtract(e).divide(number(2)).simplify());
        }
        else
        {
            throw new IllegalStateException("NYI");
        }

        List<Point> points = new ArrayList<>(roots.size());

        for (Value root : roots)
        {
            System.out.println(root.approximate());
            System.out.println("root: " + root);
            Tribool check = root.between(ZERO, ONE);
            if (check.isUnknown())
            {
                throw new IllegalStateException();
            }
            if (check.isTrue())
            {
                points.add(interpolate(root));
            }
        }

        return points;
    }

    private Point interpolate(Value t)
    {
        Value x = interpolate(t, Point::getX);
        Value y = interpolate(t, Point::getY);
        return new Point(x.simplify(), y.simplify());
    }

    private Value interpolate(Value t, Function<Point, Value> getter)
    {
        Value tm = Value.ONE.subtract(t);
        Value p0 = getter.apply(start);
        Value p1 = getter.apply(control);
        Value p2 = getter.apply(end);
        return tm.square().multiply(p0).add(number(2).multiply(tm).multiply(t).multiply(p1)).add(t.square().multiply(p2));
    }
}
