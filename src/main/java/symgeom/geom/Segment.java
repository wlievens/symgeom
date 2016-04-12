package symgeom.geom;

import lombok.RequiredArgsConstructor;
import symgeom.value.Tribool;
import symgeom.value.Value;

import java.util.Optional;

@lombok.Value
@RequiredArgsConstructor
public class Segment
{
    private final Point start;
    private final Point end;

    public Value getLength()
    {
        return start.getDistance(end);
    }

    public Optional<Point> intersection(Segment other)
    {
        Value x1 = this.start.getX();
        Value y1 = this.start.getY();
        Value x2 = this.end.getX();
        Value y2 = this.end.getY();
        Value x3 = other.start.getX();
        Value y3 = other.start.getY();
        Value x4 = other.end.getX();
        Value y4 = other.end.getY();

        Value d = x1.subtract(x2).multiply(y3.subtract(y4)).subtract(y1.subtract(y2).multiply(x3.subtract(x4))).simplify();
        if (d.eq(Value.ZERO).isTrue())
        {
            return Optional.empty();
        }

        Value xi = x3.subtract(x4).multiply(x1.multiply(y2).subtract(y1.multiply(x2))).subtract(x1.subtract(x2).multiply(x3.multiply(y4).subtract(y3.multiply(x4)))).divide(d).simplify();
        Value yi = y3.subtract(y4).multiply(x1.multiply(y2).subtract(y1.multiply(x2))).subtract(y1.subtract(y2).multiply(x3.multiply(y4).subtract(y3.multiply(x4)))).divide(d).simplify();

        Point intersection = new Point(xi, yi);

        Tribool thisContains = this.contains(intersection);
        if (thisContains.isUnknown())
        {
            throw new IllegalStateException("Intersection unknown");
        }
        if (thisContains.isFalse())
        {
            return Optional.empty();
        }
        Tribool otherContains = other.contains(intersection);
        if (otherContains.isUnknown())
        {
            throw new IllegalStateException("Intersection unknown");
        }
        if (otherContains.isFalse())
        {
            return Optional.empty();
        }

        return Optional.of(intersection);
    }

    public Point getMidpoint()
    {
        Value x = start.getX().add(end.getX()).divide(Value.number(2)).simplify();
        Value y = start.getY().add(end.getY()).divide(Value.number(2)).simplify();
        return new Point(x, y);
    }

    public Tribool contains(Point point)
    {
        if (start.eq(point).isTrue() || end.eq(point).isTrue())
        {
            return Tribool.TRUE;
        }
        Value x = point.getX();
        Value y = point.getY();
        Value x1 = start.getX();
        Value y1 = start.getY();
        Value x2 = end.getX();
        Value y2 = end.getY();
        Value dx12 = getDeltaX();
        Value dy12 = getDeltaY();
        Value dx = x.subtract(x1).simplify();
        Value dy = y.subtract(y1).simplify();
        Tribool zeroX = dx12.isZero();
        Tribool zeroY = dy12.isZero();
        if (zeroX.isUnknown() || zeroY.isUnknown())
        {
            return Tribool.UNKNOWN;
        }
        if (zeroX.isTrue())
        {
            return dy.divide(dy12).simplify().between(Value.ZERO, Value.ONE);
        }
        if (zeroY.isTrue())
        {
            return dx.divide(dx12).simplify().between(Value.ZERO, Value.ONE);
        }
        Value posX = dx.divide(dx12).simplify();
        Value posY = dy.divide(dy12).simplify();
        System.out.println(posY.toPrefix());
        Tribool onLine = posX.eq(posY);
        if (onLine.isUnknown())
        {
            return Tribool.UNKNOWN;
        }
        if (onLine.isFalse())
        {
            return Tribool.FALSE;
        }
        return posX.between(Value.ZERO, Value.ONE);
    }

    public Value getDeltaX()
    {
        return end.getX().subtract(start.getX()).simplify();
    }

    public Value getDeltaY()
    {
        return end.getY().subtract(start.getY()).simplify();
    }
}
