package symgeom.linear;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import symgeom.util.Util;
import symgeom.value.Value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@lombok.Value
@RequiredArgsConstructor
public class LinearExpression
{
    private final ImmutableList<LinearTerm> terms;

    @Override
    public final String toString()
    {
        return String.format("Linear[%s]", Joiner.on(" + ").join(terms.stream().map(LinearTerm::toString).iterator()));
    }

    public LinearExpression simplify()
    {
        if (terms.size() == 1)
        {
            return create(terms.get(0).simplify());
        }
        Set<Value> values = new LinkedHashSet<>();
        terms.stream().map(LinearTerm::getValue).forEach(values::add);
        List<LinearTerm> result = new ArrayList<>();
        for (Value value : values)
        {
            List<LinearTerm> valueTerms = terms.stream().filter(term -> term.getValue().equals(value)).collect(Collectors.toList());
            if (valueTerms.size() == 1)
            {
                result.add(valueTerms.get(0).simplify());
                continue;
            }
            BigInteger[] denominators = valueTerms.stream().map(LinearTerm::getDenominator).toArray(size -> new BigInteger[size]);
            BigInteger lcm = Util.lcm(denominators);
            BigInteger numerators = BigInteger.ZERO;
            for (LinearTerm term : valueTerms)
            {
                BigInteger factor = term.getNumerator().multiply(lcm.divide(term.getDenominator()));
                numerators = numerators.add(factor);
            }
            result.add(LinearTerm.create(numerators, lcm, value).simplify());
        }
        return new LinearExpression(ImmutableList.copyOf(result));
    }

    public Value toValue()
    {
        return toValue(terms);
    }

    private Value toValue(ImmutableList<LinearTerm> terms)
    {
        if (terms.isEmpty())
        {
            throw new IllegalArgumentException();
        }
        Value first = terms.get(0).toValue();
        if (terms.size() == 1)
        {
            return first;
        }
        return first.add(toValue(terms.subList(1, terms.size())));
    }

    public static LinearExpression create(LinearTerm... terms)
    {
        return new LinearExpression(ImmutableList.copyOf(terms));
    }
}
