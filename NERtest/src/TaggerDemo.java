import java.io.IOException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class TaggerDemo {
    public static void main(String[] args){
 
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger("lib\\taggers\\english-caseless-left3words-distsim.tagger");
        // The sample string
        String sample = "This is a sample text";
 
        // The tagged string
        String tagged = tagger.tagString(sample);
 
        // Output the result
        System.out.println(tagged);
    }
}