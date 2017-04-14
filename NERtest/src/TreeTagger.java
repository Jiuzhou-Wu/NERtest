
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;

public class TreeTagger{
	static String temPos= "";
	public static void main(String[] args) throws Exception	{
		// Point TT4J to the TreeTagger installation directory. The executable is expected
		// in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
		System.setProperty("treetagger.home", "C:\\tree-tagger-windows-3.2\\TreeTagger");
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		tt.setModel("lib\\english-utf8.par:utf-8");
		Path file = Paths.get("lib\\Result\\temp");
		
		tt.setHandler(new TokenHandler<String>(){
			public void token(String token, String pos, String lemma)
			{
				temPos = pos;
			}
		});
		tt.process(new String[] { "City"});
		System.out.println(temPos);
	}
}