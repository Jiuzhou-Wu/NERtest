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
import edu.stanford.nlp.util.StringUtils;


public class CallNER {

	public static void main(String[] args) throws IOException{
		List<String> content = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\data"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath, Charset.forName("ISO-8859-1"));
						//System.out.println(filePath);
						String title = s.get(1);
						String type = s.get(2);
						//title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(1));
						typeList.add(type);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		}
		
		String model = "lib\\models\\english-bidirectional-distsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
		
		List<String> plural = new ArrayList<String>();
    	plural = checkPlural(content, tagger);
		
		StanfordNER ner = new StanfordNER();
		String classifier = "lib\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
		
		Path file = Paths.get("lib\\Result\\Res.arff");
		Files.write(file, ner.toString(ner.identify(content, classifier),content,plural,typeList), Charset.forName("UTF-8"));
		
	}
	
	public static List<String> checkPlural(List<String> content, MaxentTagger tagger){
		List<String> res = new ArrayList<String>();
    	String temp;
    	for(int i=0;i<content.size();i++){
    		temp = "";
    		boolean flag = false;
    		boolean structure = false;
    		temp = tagger.tagTokenizedString(content.get(i));
    		String[] words = temp.split("\\s+");
    		if(!StringUtils.isAlphanumeric(content.get(i))){
    			//System.out.println(content.get(i));
    			structure = true;
    		}
    		for (int j = 0; j < words.length; j++) {
    		    temp = words[j].substring(words[j].lastIndexOf("_")+1, words[j].length());
    		    if(temp.equals("NNS")|| temp.equals("NNPS")){
    		    	flag = true;
    		    }
    		    else if(temp.equals("VB")|| temp.equals("FW")|| temp.equals("CD")){
    		    	structure = true;
    		    }
    		   
    		}
    		if(flag && structure){
    			res.add("plural, structure");
    		}
    		else if(!flag && structure){
    			res.add("singular, structure");
    		}
    		else if(flag && !structure){
    			res.add("plural, noStr");
    		}
    		else{
    			res.add("singular, noStr");
    		}
    		
    			
    	}
    	
		return res;
	}

}
