package symgeom.geom;

import lombok.RequiredArgsConstructor;
import symgeom.value.Tribool;
import symgeom.value.Value;

@lombok.Value
@RequiredArgsConstructor
public class Point
{
    private final Value x;
    private final Value y;

    public Point(int x, int y)
    {
        this(Value.number(x), Value.number(y));
    }

    public Value getDistance(Point point)
    {
        Value dx = x.subtract(point.getX());
        Value dy = y.subtract(point.getY());
        return Value.sqrt(dx.square().add(dy.square())).simplify();
    }

    public Tribool eq(Point point)
    {
        Tribool eqX = x.eq(point.x);
        if (eqX.isUnknown())
        {
            return Tribool.UNKNOWN;
        }
        if (eqX.isFalse())
        {
            return Tribool.FALSE;
        }
        return y.eq(point.y);
    }
}
