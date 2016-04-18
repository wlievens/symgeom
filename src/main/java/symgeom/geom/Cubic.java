package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.Value;

import java.util.List;

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
        Value a = start.getX();
        Value b = control1.getX();
        Value c = control2.getX();
        Value d = end.getX();

        Value b_a = b.divide(a).simplify();
        Value c_a = c.divide(a).simplify();
        Value d_a = d.divide(a).simplify();

        Value q = (number(3).multiply(c_a).subtract() ).divide(number(9));

        return null;
    }
}
