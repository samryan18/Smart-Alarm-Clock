package be470.upenn.edu.smartalarmclock;

/**
 * Created by Sam on 4/24/17.
 */
import android.opengl.GLSurfaceView;
import android.content.Context;

public class GraphView extends GLSurfaceView {
    private Renderer renderer;
    public GraphView(Context context) {
        super(context);
        renderer = new GraphRenderer();
        setRenderer(renderer);
    }
}

