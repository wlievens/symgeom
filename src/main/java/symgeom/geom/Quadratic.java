package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.Sign;
import symgeom.value.Tribool;
import symgeom.value.Value;

import java.util.ArrayList;
import java.util.List;

import static symgeom.value.Value.ONE;
import static symgeom.value.Value.ZERO;
import static symgeom.value.Value.number;

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

        for (Value root: roots)
        {
            System.out.println(root.approximate());
            Tribool check = root.between(ZERO, ONE);
            if (check.isUnknown())
            {
                throw new IllegalStateException();
            }
            if (check.isTrue())
            {
                System.out.println(root);
            }
        }

        /*
        Value qx0 = p1.getX();
        Value qx1 = p2.getX();
        Value qx2 = p3.getX();

        Value qy0 = p1.getY();
        Value qy1 = p2.getY();
        Value qy2 = p3.getY();

        Value xa = qy0.subtract(number(2).multiply(qy1)).subtract(qy2);
        Value xb = number(2).multiply(qy1.subtract(qy0));
        Value xc = qy0;

        xa = xa.simplify();
        xb = xb.simplify();
        xc = xc.simplify();

        System.out.println(xa + "\t" + xb + "\t" + xc);

        Value b4ac = xb.square().subtract(number(4).multiply(xa).multiply(xc)).sqrt();
        Value xa2 = number(2).multiply(xa);
        Value t1 = b4ac.subtract(xb).divide(xa2);
        Value t2 = b4ac.negate().subtract(xb).divide(xa2);

        System.out.println(t1);
        System.out.println(t1.simplify());
        System.out.println(t1.approximate());

        System.out.println(t2);
        System.out.println(t2.simplify());
        System.out.println(t2.approximate());
*/
        return null;
    }
}
