package main.java;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.immunizationsoftware.dqa.transform.ModifyMessageRequest;
import org.immunizationsoftware.dqa.transform.ModifyMessageService;

public class XmsgChecker {

	public static void main(String[] args) throws IOException, TransformerConfigurationException, TransformerException {
		ModifyMessageService m = new ModifyMessageService();
		ModifyMessageRequest req = new ModifyMessageRequest();
		String banner = "xmsgcheck v1.0 11/13/2015\nChecks test-steps1 and test-steps2 cross reference in the messages. \nUsage: java -jar xmsgchecker.jar msgpath xsltfile [-interactive]";
		System.out.println(banner);
		String inFile, scriptFile, interactive;
		
		// input file
		if (args.length < 1) {
			System.out.println("Please enter the full path of file or folder where the .msg files need to be checked:");
			inFile = new Scanner(System.in).nextLine();
			if (inFile.trim().isEmpty()) {
				inFile = "C:\\Users\\sna10\\DEV\\Source\\java\\dqa\\WindowsMessageTool\\msg\\ORIGINAL-ONC-TestPlan2015";
			}
		}
		else
			inFile = args[0];

		// script file
		if (args.length < 2) {
			System.out.println("Please enter the script folder containing the script files for each step (TestStep_1.txt and TestStep2_.txt:");
			scriptFile = new Scanner(System.in).nextLine();
			if (scriptFile.trim().isEmpty()) {
				scriptFile = "C:\\Users\\sna10\\DEV\\Source\\xslt\\nistmu2hl7xslt\\trunk\\xsl\\common\\xmsgcheck.xsl";
			}
		}
		else
			scriptFile = args[1];

		final File in = new File(inFile);
		if (!in.exists()) {
			System.out.println("File " + inFile + "not found! Please try again.");
			return;
		}
		
		//File script = new File(scriptFile);

		File out;

		if (args.length < 3) {
			System.out.println("Do you want verbose informataion (enter if yes, or type no):");
			interactive = new Scanner(System.in).nextLine();
		}
		else
			interactive =  args[2];
				
		ArrayList<File> fileList = in.isDirectory() ?
					recurseFileList(in.getAbsolutePath(), "Message.txt") :
					new ArrayList<File>(){{ add(in); }};
		
		System.out.println("Files found " + fileList.size());
		HashMap<String, String> testStep1Name = new HashMap<String, String>();
		
		for (File file : fileList) {
			
			if (file.getAbsolutePath().contains("TestStep_1")) {
				testStep1Name.put(file.getAbsolutePath().replace("TestStep_1", "TestStep_2"), file.getAbsolutePath());
			}
			else {
				String s = testStep1Name.get(file.getAbsolutePath());
				if (s != null) {
					System.out.println("------------------------------------------------------------------------");
					System.out.println("----> Checking " + s.substring(s.indexOf("TestPlan2015")) + " -- against --- "  + file.getCanonicalPath().substring(file.getCanonicalPath().indexOf("TestPlan2015")));
					System.out.println("------------------------------------------------------------------------");
					System.out.println(xslttransform(scriptFile, scriptFile, s, file.getCanonicalPath()));
					System.out.println("------------------------------------------------------------------------");
					System.out.println("------------------------------------------------------------------------");
					//System.in.read();
				}
			}
					
		}
		
	}

	public static String xslttransform(String xsl, String input, String param1, String param2) throws TransformerConfigurationException, TransformerException, IOException {
	   StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);       TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File(xsl));
        input = "C:\\Users\\sna10\\DEV\\Source\\xslt\\nistmu2hl7xslt\\trunk\\xml\\common\\MessageContents.xml";
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("file1name", new File(param1).toURI().toURL().toExternalForm());
        transformer.setParameter("file2name", new File(param2).toURI().toURL().toExternalForm());
        Source text = new StreamSource(new File(input));
        transformer.transform(text, result);
        return writer.toString();
    }
	
    public static ArrayList<File> recurseFileList(String path, String match) {

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return null;
        
        ArrayList<File> fl = new ArrayList<File>();
        
        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	fl.addAll(recurseFileList( f.getAbsolutePath(), match ));
            }
            else {
            	if (match.isEmpty() || match.equals(f.getName())) {
            		fl.add(f.getAbsoluteFile());
            	}
            }
        }
        
        return fl;
    }

	
}
