package main.java;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.immunizationsoftware.dqa.transform.ModifyMessageRequest;
import org.immunizationsoftware.dqa.transform.ModifyMessageService;

public class msgmod {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String transform1 = "OBX-3.1*=Testing9999";
		String transform2 = "OBX-3=Testing";
		String message = "";
		ModifyMessageService m = new ModifyMessageService();
		ModifyMessageRequest req = new ModifyMessageRequest();
		String banner = "msgmod v1.0 11/13/2015\nModifies a message or all the messages in the folder using the script provided. \nUsage: java -jar msgmod.jar msgpath scriptpath [-interactive]";
		System.out.println(banner);
		String inFile, scriptFile, interactive;
		
		// input file
		if (args.length < 1) {
			System.out.println("Please enter the full path of file or folder where the .msg files need to be transformed:");
			inFile = new Scanner(System.in).nextLine();
		}
		else
			inFile = args[0];

		// script file
		if (args.length < 2) {
			System.out.println("Please enter the script folder containing the script files for each step (TestStep_1.txt and TestStep2_.txt:");
			scriptFile = new Scanner(System.in).nextLine();
			if (scriptFile.trim().isEmpty()) {
				System.out.println("Transforming using default:\n" + transform1 + "\n");
				System.out.println("Transforming using default:\n" + transform2 + "\n");
			}
		}
		else
			scriptFile = args[1];

		final File in = new File(inFile);
		if (!in.exists()) {
			System.out.println("File " + inFile + "not found! Please try again.");
			return;
		}
		
		File script = new File(scriptFile);

		File out;

		if (args.length < 3) {
			System.out.println("Do you want verbose informataion (enter if yes, or type no):");
			interactive = new Scanner(System.in).nextLine();
		}
		else
			interactive =  args[2];
		
		if (script.exists() && script.isDirectory()) {
			transform1 = new String(Files.readAllBytes(script.toPath().resolve("TestStep_1.txt")));
			transform2 = new String(Files.readAllBytes(script.toPath().resolve("TestStep_2.txt")));
		} else {
			System.out.println("Using default scripts..either " + scriptFile + "does not exist or is not a directory. press enter to continue!");
			System.in.read();
		}
		
		ArrayList<File> fileList = in.isDirectory() ?
					recurseFileList(in.getAbsolutePath(), "Message.txt") :
					new ArrayList<File>(){{ add(in); }};
		
		System.out.println("Files found " + fileList.size());
		HashMap<String, String> testStep1Name = new HashMap<String, String>();
		
		for (File file : fileList) {
			System.out.println("----> Transforming " + file.getCanonicalPath());
			req.setMessageOriginal(new String(Files.readAllBytes(file.toPath())));
			String nameFromJSON = (new String(Files.readAllBytes(file.getParentFile().toPath().resolve("TestStep.json")))).
					split("\\r?\\n")[1].replace("  \"name\" : \"", "").replace("\",", "").replace("\n", "").replace("\r", "");
			
			System.out.println("Name from Json is " + nameFromJSON);
			String extraTransform = "\nMSH-10=NIST-" + nameFromJSON + "\nclean no last slash\n";
			
			if (file.getAbsolutePath().contains("TestStep_1")) {
				testStep1Name.put(file.getAbsolutePath().replace("TestStep_1", "TestStep_2"), nameFromJSON);
			}
			else {
				String s = testStep1Name.get(file.getAbsolutePath());
				if (s != null) {
					extraTransform += "\nMSA-2=NIST-" + s + "\nclean no last slash\n";
				}
			}
			
			/*String [] transforms = getTransforms((file.getAbsolutePath().contains("TestStep_1") ? transform1 : transform2) + extraTransform, 20);
			
			for (String transform: transforms) {
				req.setTransformScript(transform);
				m.transform(req);
			}*/

			String transform = (file.getAbsolutePath().contains("TestStep_1") ? transform1 : transform2) + extraTransform;
			req.setTransformScript(transform);
			m.transform(req);
			
			System.out.println("--------->Transformed. Sending output to: " + file.getAbsolutePath());
			PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
			writer.println(req.getMessageFinal().replace("\r",  "\r\n"));
			writer.flush();
			writer.close();
			
			if (interactive.equals("-interactive") || interactive.equals("")) {
				System.out.println("********************************\n\n");
				System.out.println(req.getMessageFinal());
				System.out.println("********************************\n\n");
				System.out.println("Please press any key to continue...");
				//System.in.read();
			}
		}
		
	}

	
	public static String[] getTransforms(String transform, int x) {
		String [] out = new String[1];
		/*
		for (int i = 0; i < x; i++) {
			transform.replace("-", "a");
		}
		*/
		out[0] = transform;
		
		return out;
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
