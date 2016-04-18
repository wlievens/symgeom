package symgeom.geom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Cubic
{
    private final Point start;
    private final Point control1;
    private final Point control2;
    private final Point end;

    public List<Point> intersections(Segment segment)
    {
        return null;
    }
}
