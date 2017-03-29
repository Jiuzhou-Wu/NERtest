import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class CallNER {

	public static void main(String[] args) throws IOException{
		List<String> content = new ArrayList<String>();
		
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\Test"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath);
						String title = s.get(1);
						title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(1));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		}
		
		List<String> plural = new ArrayList<String>();
    	plural = checkPlural(content);
		
    	System.out.println(plural.toString());
    	
		StanfordNER ner = new StanfordNER();
		String classifier = "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
		
		Path file = Paths.get("lib\\Result\\Res.arff");
		Files.write(file, ner.toString(ner.identify(content, classifier),content), Charset.forName("UTF-8"));
	}
	
	public static List<String> checkPlural(List<String> content){
		List<String> res = new ArrayList<String>();
		
		String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	String temp;
    	for(int i=0;i<content.size();i++){
    		temp = content.get(i);
    		boolean flag = false;
    		String[] words = temp.split("\\s+");
    		for (int j = 0; j < words.length; j++) {
    		    words[j] = words[j].replaceAll("[^\\w]", "");
    		    temp = tagger.tagTokenizedString(words[j]).substring(words[j].length()+1, tagger.tagTokenizedString(words[j]).length()-1);
    		    if(temp.equals("NNS")|| temp.equals("NNPS")){
    		    	flag = true;
    		    }
    		}
    		if(flag){
    			res.add("plural");
    		}
    		else{
    			res.add("singular");
    		}
    	}
    	
		return res;
	}
}
