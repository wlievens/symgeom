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

    public Value old_simplify()
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
    public Tribool lt(Value value)
    {
        value = value.simplify();
        if (value.isInteger())
        {
            return Tribool.of(this.value < value.asInteger());
        }
        Tribool eq = eq(value);
        if (eq.isKnown())
        {
            if (eq.isTrue())
            {
                return Tribool.FALSE;
            }
            return value.lt(this).invert();
        }
        return Tribool.UNKNOWN;
        /*
        Sign sign = value.getSign();
        switch (sign)
        {
            case ZERO:
            {
                return Tribool.of(this.value == 0);
            }
            case NEGATIVE:
            {
                if (this.value >= 0)
                {
                    return Tribool.FALSE;
                }
                break;
            }
            case POSITIVE:
            {
                if (this.value <= 0)
                {
                    return Tribool.TRUE;
                }
                break;
            }
        }
        */
    }

    public static Value create(int value)
    {
        return new IntegerValue(value);
    }
}
