package symgeom.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBinaryValue extends Value
{
    @Getter
    @NonNull
    private final Value left;

    @Getter
    @NonNull
    private final Value right;

    @Override
    public int getDepth()
    {
        return Math.max(left.getDepth(), right.getDepth()) + 1;
    }

    @Override
    public String toPrefix()
    {
        return String.format("(%s %s %s)", getOperator().name(), left.toPrefix(), right.toPrefix());
    }

    public String toExpression(int precedence)
    {
        BinaryOperator operator = getOperator();
        boolean parentheses = precedence > operator.getPrecedence();
        return String.format("%s%s %s %s%s", parentheses ? "(" : "", left.toExpression(operator.getPrecedence()), operator.getSymbol(), right.toExpression(operator.getPrecedence()), parentheses ? ")" : "");
    }

    @Override
    public Tribool eq(Value value)
    {
        Value other = value.simplify();
        if (value.getClass() == this.getClass())
        {
            AbstractBinaryValue binary = (AbstractBinaryValue)other;
            if (this.left.eq(binary.left).isTrue() && this.right.eq(binary.right).isTrue())
            {
                return Tribool.TRUE;
            }
            if (getOperator().isCommutative() && this.left.eq(binary.right).isTrue() && this.right.eq(binary.left).isTrue())
            {
                return Tribool.TRUE;
            }
        }
        return Tribool.UNKNOWN;
    }

    @Override
    public Tribool lt(Value value)
    {
        System.out.println("AbstractBinaryValue.lt [" + this + "] < " + value);
        return Tribool.UNKNOWN;
    }

    @Override
    public final double approximate()
    {
        return getOperator().getApproximator().apply(left.approximate(), right.approximate());
    }

    public abstract BinaryOperator getOperator();
}
