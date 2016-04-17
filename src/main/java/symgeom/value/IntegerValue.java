package symgeom.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegerValue extends Value
{
    @Getter
    private final BigInteger value;

    @Override
    public final int getDepth()
    {
        return 1;
    }

    @Override
    public final boolean isAtom()
    {
        return true;
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

    public double approximate()
    {
        return value.doubleValue();
    }

    public static Value create(BigInteger value)
    {
        return new IntegerValue(value);
    }
}
