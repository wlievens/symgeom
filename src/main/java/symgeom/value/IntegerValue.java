package symgeom.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerValue extends Value
{
    @Getter
    private final int value;

    @Override
    public final int getDepth()
    {
        return 1;
    }

    @Override
    public String toPrefix()
    {
        return String.valueOf(value);
    }

    public String toExpression(int precedence)
    {
        return String.valueOf(value);
    }

    public Value simplify()
    {
        return this;
    }

    public double approximate()
    {
        return value;
    }

    @Override
    public Tribool eqInternal(Value value)
    {
        Value other = value.simplify();
        if (other.isInteger())
        {
            return Tribool.of(this.value == other.asInteger());
        }
        return Tribool.UNKNOWN;
    }

    @Override
    public Tribool lt(Value other)
    {
        Value value = other.simplify();
        if (value.isInteger())
        {
            return Tribool.of(this.value < value.asInteger());
        }
        return other.lt(this).invert();
    }

    public static Value create(int value)
    {
        return new IntegerValue(value);
    }
}
