package be470.upenn.edu.smartalarmclock;

/**
 * Created by Sam on 4/24/17.
 */

import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLU;
import javax.microedition.khronos.egl.EGLConfig;

public class GraphRenderer implements Renderer {

    public void onDrawFrame(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 1.0f);
        gl.glColor4f(1, 0, 0, .5f);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    private void drawGraph(GL10 gl) {
        gl.glLineWidth(1.0f);
        // put your code here ..
        //MORE INFO HERE:
        //http://stackoverflow.com/questions/12545936/how-to-draw-graph-in-android

    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
