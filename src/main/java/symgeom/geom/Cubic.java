package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.Value;

import java.util.List;
import java.util.function.Function;

import static symgeom.value.Value.number;

@Getter
@RequiredArgsConstructor
public class Cubic
{
    private final Point start;
    private final Point control1;
    private final Point control2;
    private final Point end;

    public List<Point> intersections(Segment segment)
    {
        Value[] coefficients = getCoefficients(Point::getX);

        Value a = coefficients[0];
        Value b = coefficients[1];
        Value c = coefficients[2];
        Value d = coefficients[3];

        Value b_a = b.divide(a).simplify();
        Value c_a = c.divide(a).simplify();
        Value d_a = d.divide(a).simplify();

        Value q = (number(3).multiply(c_a).subtract(b_a.square())).divide(number(9)).simplify();
        Value r = (number(9).multiply(b_a).multiply(c_a).subtract(number(28).multiply(d_a)).subtract(number(2).multiply(b_a.cube()))).divide(number(54)).simplify();
        Value discriminant = q.cube().add(r.square()).simplify();

        System.out.println(discriminant);
        System.out.println(discriminant.approximate());

        return null;
    }

    private Value[] getCoefficients(Function<Point, Value> getter)
    {
        return new Value[]{ getter.apply(start), getter.apply(control1), getter.apply(control2), getter.apply(end) };
    }
}
