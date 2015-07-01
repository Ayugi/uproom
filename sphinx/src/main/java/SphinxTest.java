import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;

/**
 * Created by HEDIN on 01.07.2015.
 */
public class SphinxTest {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();

// Set path to acoustic model.
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
// Set path to dictionary.
        configuration.setDictionaryPath("resource:/9421.dic");
        // edu/cmu/sphinx/models/en-us/cmudict-en-us.dict
// Set language model.
        configuration.setLanguageModelPath("resource:/9421.lm");
        // /edu/cmu/sphinx/models/en-us/en-us.lm.bin

        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
// Start recognition process pruning previously cached data.
        recognizer.startRecognition(true);
        SpeechResult result = recognizer.getResult();

        while ((result = recognizer.getResult()) != null) {
            System.out.println(result.getHypothesis());
        }
    }
}
