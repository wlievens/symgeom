package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.Value;

import java.util.List;

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

        Value x0 = start.getX();
        Value x1 = control.getX();
        Value x2 = end.getX();

        Value y0 = start.getY();
        Value y1 = control.getY();
        Value y2 = end.getY();

        Value xa = y0.subtract(number(2).multiply(y1)).subtract(y2);
        Value xb = number(2).multiply(y1.subtract(y0));
        Value xc = y0;

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

        return null;
    }
}
