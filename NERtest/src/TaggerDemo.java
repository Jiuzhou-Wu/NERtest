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
 //treetagger
public class TaggerDemo {
    public static void main(String[] args) throws IOException{
    	
    	ArrayList<String> content = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		List<String> abstracts = new ArrayList<String>();
		
		try(Stream<Path> paths = Files.walk(Paths.get("lib\\data"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            try {
						List<String> s = Files.readAllLines(filePath, Charset.forName("UTF-8"));
						//System.out.println(filePath);
						String title = s.get(1);
						String type = s.get(2);
						String abs = "EmptyAbs";
						if(!s.get(3).equals("")){
							abs = s.get(3);
						}
						
						//title = title.substring(11, title.length()-8);
						content.add(title);
						//System.out.println(s.get(3));
						typeList.add(type);
						abstracts.add(abs);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });
		}
    	
    	String model = "lib\\models\\wsj-0-18-bidirectional-nodistsim.tagger";
    	MaxentTagger tagger = new MaxentTagger(model);
    	
    	
    	List<String> cls = new ArrayList<String>();
    	List<String> ins = new ArrayList<String>();
    	List<String> pat = new ArrayList<String>();

    	//System.out.println(abstracts.get(3));
    	for(int i=0;i<content.size();i++){
    		String abs = abstracts.get(i);
    		String res =typeList.get(i) + ": ";
    		int end = abs.indexOf(".");
    		if(end >0){
    			abs = abs.substring(0, end);
    			//System.out.println(abs);
    			String[] words = tagger.tagString(abs).split("\\s+");
    			for(int j=0;j<words.length;j++){
    				String temp = words[j];
    				temp = temp.substring(temp.lastIndexOf("_")+1, temp.length());
    				res = res + temp + " ";
    			}
    		}
    		else{
    			//System.out.println(abs);
    			String[] words = tagger.tagString(abs).split("\\s+");
    			for(int j=0;j<words.length;j++){
    				String temp = words[j];
    				//System.out.println(temp);
    				temp = temp.substring(temp.lastIndexOf("_")+1, temp.length());
    				res = res + temp + " ";
    			}
    		}
    		if(typeList.get(i).equals("class")){
    			cls.add(res);
    		}else{
    			ins.add(res);
    			
    			/*if(ins.indexOf(res)==93){
    				System.out.println(abstracts.get(i));
    			}*/
    		}
    		pat.add(res);
    	}
    	//System.out.println(abstracts.get(533));
    	Path file = Paths.get("lib\\Result\\patternClass");
		Files.write(file, cls, Charset.forName("UTF-8"));
		Path file2 = Paths.get("lib\\Result\\patternInstance");
		Files.write(file2, ins, Charset.forName("UTF-8"));
		Path file3 = Paths.get("lib\\Result\\pattern");
		Files.write(file3, pat, Charset.forName("UTF-8"));
    }
}