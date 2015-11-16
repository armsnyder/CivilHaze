package snyder.adam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Adam Snyder
 */
public class Resolution implements Comparable<Resolution> {
    public static final Resolution[] supportedResolutions;
    static {
        ArrayList<Resolution> resolutions = new ArrayList<>();

        resolutions.add(new Resolution(1920, 1080)); // 16:9
        resolutions.add(new Resolution(1366, 768)); // ~16:9
        resolutions.add(new Resolution(1600, 900)); // 16:9
        resolutions.add(new Resolution(1280, 1024)); // 5:4
        resolutions.add(new Resolution(1440, 900)); // 16:10
        resolutions.add(new Resolution(1680, 1050)); // 16:10
        resolutions.add(new Resolution(1360, 768)); // ~16:9
        resolutions.add(new Resolution(1024, 768)); // 4:3
        resolutions.add(new Resolution(1280, 800)); // 16:10
        resolutions.add(new Resolution(2560, 1440)); // 16:9
        resolutions.add(new Resolution(2560, 1600)); // 16:10
        resolutions.add(new Resolution(800, 600)); // 4:3

        Collections.sort(resolutions);
        supportedResolutions = new Resolution[resolutions.size()];
        for (int i = 0; i < resolutions.size(); i++) {
            supportedResolutions[i] = resolutions.get(i);
        }
    }

    public final int WIDTH;
    public final int HEIGHT;
    public final Integer PIXELS;
    public final float ASPECT_RATIO;

    private Resolution(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.PIXELS = width * height;
        this.ASPECT_RATIO = (float) width / height;
    }

    @Override
    public int compareTo(Resolution o) {
        return PIXELS.compareTo(o.PIXELS);
    }

    public static Resolution getMatchingFullScreenResolution(int displayWidth, int displayHeight) {
        // Check if there's a perfect match:
        for (Resolution r : supportedResolutions) {
            if (r.WIDTH == displayWidth && r.HEIGHT == displayHeight) {
                return r;
            }
        }

        // Find closest match:
        int displayPixels = displayWidth * displayHeight;
        float displayAspect = (float) displayWidth / displayHeight;
        float deltaAspect = 0.001f;
        // Larger resolution of exact aspect ratio:
        for (Resolution r : supportedResolutions) {
            if (r.PIXELS >= displayPixels && Math.abs(r.ASPECT_RATIO - displayAspect) < deltaAspect) {
                return r;
            }
        }
        // Smaller resolution of exact aspect ratio:
        for (int i = supportedResolutions.length-1; i >= 0; i--) {
            Resolution r = supportedResolutions[i];
            if (r.PIXELS < displayPixels && Math.abs(r.ASPECT_RATIO - displayAspect) < deltaAspect) {
                return r;
            }
        }
        // Closest pixel-count-wise:
        ArrayList<Resolution> resolutions = new ArrayList<>(Arrays.asList(supportedResolutions));
        Collections.sort(resolutions, new CompareRatio(displayPixels));
        return resolutions.get(0);
    }

    public static Resolution getMatchingWindowedResolution(int displayWidth, int displayHeight) {
        float edgeBuffer = 1.1f;
        for (int i = supportedResolutions.length-1; i >= 0; i--) {
            Resolution r = supportedResolutions[i];
            if (r.WIDTH * edgeBuffer < displayWidth && r.HEIGHT * edgeBuffer < displayHeight) {
                return r;
            }
        }
        return supportedResolutions[0];
    }

    private static class CompareRatio implements Comparator<Resolution> {

        private float compareTo;

        public CompareRatio(float compareTo) {
            this.compareTo = compareTo;
        }

        @Override
        public int compare(Resolution o1, Resolution o2) {
            float o1c = Math.abs(o1.ASPECT_RATIO-compareTo);
            float o2c = Math.abs(o2.ASPECT_RATIO-compareTo);
            float difference = Math.abs(o1c-o2c);
            return difference > 0 ? -1 : difference < 0 ? 1 : 0;
        }
    }
}
