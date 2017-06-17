package visualization;

/**
 * Created by kai on 6/17/17.
 * Examples for Visualization Element is the LiveLineChart or the TextualPresentation.
 * All of those Visualization Elements must be able to unsubscribe from the dataSources they subscibed to in their construction.
 * This interface forces them to implement it.
 */
public interface VisualizationElement {
    void unsubscibeDataSources();
}
