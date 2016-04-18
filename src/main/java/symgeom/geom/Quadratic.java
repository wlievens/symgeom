package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Quadratic
{
    private final Point start;
    private final Point control;
    private final Point end;

    public List<Point> intersections(Segment segment)
    {
        return null;
    }
}
