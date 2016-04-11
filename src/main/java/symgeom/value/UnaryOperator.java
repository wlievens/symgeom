package symgeom.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum UnaryOperator
{
    NEGATE("-", false, x -> -x),
    ABS("abs", true, Math::abs),
    COSINE("cos", true, Math::cos),
    SINE("sin", true, Math::sin);

    private final String symbol;
    private final boolean applicative;
    private final Function<Double, Double> approximator;
}
