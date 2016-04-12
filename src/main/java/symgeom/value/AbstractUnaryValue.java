package symgeom.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractUnaryValue extends Value
{
    @Getter
    @NonNull
    private final Value operand;

    @Override
    public int getDepth()
    {
        return operand.getDepth() + 1;
    }

    @Override
    public String toPrefix()
    {
        return String.format("(%s %s)", getOperator().name(), operand.toPrefix());
    }

    @Override
    public Tribool eqInternal(Value value)
    {
        Value other = value.simplify();
        if (value.getClass() == this.getClass())
        {
            AbstractUnaryValue binary = (AbstractUnaryValue)other;
            if (this.operand.eq(binary.operand).isTrue())
            {
                return Tribool.TRUE;
            }
        }
        return Tribool.UNKNOWN;
    }

    @Override
    public Tribool lt(Value value)
    {
        return Tribool.UNKNOWN;
    }

    public final String toExpression(int precedence)
    {
        UnaryOperator operator = getOperator();
        if (operator.isApplicative())
        {
            return String.format("%s(%s)", operator.getSymbol(), operand.toExpression(0));
        }
        if (operand instanceof IntegerValue || operand instanceof AbstractConstantValue)
        {
            return String.format("%s%s", operator.getSymbol(), operand.toExpression(0));
        }
        return String.format("%s(%s)", operator.getSymbol(), operand.toExpression(0));
    }

    @Override
    public final double approximate()
    {
        return getOperator().getApproximator().apply(operand.approximate());
    }

    public abstract UnaryOperator getOperator();
}
