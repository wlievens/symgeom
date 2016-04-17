package symgeom.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import symgeom.linear.LinearExpression;
import symgeom.linear.LinearExpressionBuilder;

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
        return String.format("(%s %s %s)", getOperator().getSymbol(), left.toPrefix(), right.toPrefix());
    }

    @Override
    public final boolean isAtom()
    {
        return false;
    }

    public String toExpression(int precedence)
    {
        BinaryOperator operator = getOperator();
        boolean parentheses = operator.getPrecedence() <= precedence;
        return String.format("%s%s %s %s%s",
                parentheses ? "(" : "",
                left.toExpression(operator.getPrecedence()),
                operator.getSymbol(),
                right.toExpression(operator.getPrecedence()),
                parentheses ? ")" : ""
        );
    }

    public Tribool eqInternal(Value value)
    {
        value = value.simplify();
        if (this instanceof AbstractBinaryValue && this.getClass() == value.getClass())
        {
            AbstractBinaryValue bx = (AbstractBinaryValue)this;
            AbstractBinaryValue by = (AbstractBinaryValue)value;
            if (bx.left.eq(by.left).isTrue() && bx.right.eq(by.right).isTrue())
            {
                return Tribool.TRUE;
            }
            if (bx.getOperator().isCommutative() && bx.left.eq(by.right).isTrue() && bx.right.eq(by.left).isTrue())
            {
                return Tribool.TRUE;
            }
            if (bx instanceof DivideValue && by instanceof DivideValue)
            {
                // both are fractions
                // A/B = C/D -> AD = BC
                Value a = bx.left.simplify();
                Value b = bx.right.simplify();
                Value c = by.left.simplify();
                Value d = by.right.simplify();
                Value ad = a.multiply(d).simplify();
                Value bc = b.multiply(c).simplify();
                return ad.eq(bc);
            }
        }
        if (this instanceof DivideValue && value instanceof IntegerValue)
        {
            // both are fractions
            // A/B = C -> A = BC
            AbstractBinaryValue bx = (AbstractBinaryValue)this;
            Value a = bx.left.simplify();
            Value b = bx.right.simplify();
            Value c = value;
            Value bc = b.multiply(c).simplify();
            return a.eq(bc);
        }

        LinearExpressionBuilder builder = new LinearExpressionBuilder();
        LinearExpression expression1 = builder.build(this);
        LinearExpression expression2 = builder.build(value);
        if (expression1.equals(expression2))
        {
            return Tribool.TRUE;
        }

        return Tribool.UNKNOWN;
    }

    @Override
    public final double approximate()
    {
        return getOperator().getApproximator().apply(left.approximate(), right.approximate());
    }

    public abstract BinaryOperator getOperator();

}
