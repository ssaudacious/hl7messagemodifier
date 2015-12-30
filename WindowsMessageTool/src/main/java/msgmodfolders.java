package main.java;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Scanner;

import org.immunizationsoftware.dqa.transform.ModifyMessageRequest;
import org.immunizationsoftware.dqa.transform.ModifyMessageService;

public class msgmodfolders {

	public static void main2(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String transform = "MSH-5=TESTING";
		String message = "";
		ModifyMessageService m = new ModifyMessageService();
		ModifyMessageRequest req = new ModifyMessageRequest();
		String banner = "msgmod v1.0 11/13/2015\nModifies a message or all the messages in the folder using the script provided. \nUsage: java -jar msgmod.jar msgpath scriptpath outFolder [-interactive]";
		System.out.println(banner);
		String inFile, scriptFile, outFolder;
		
		// input file
		if (args.length < 1) {
			System.out.println("Please enter the full path of file or folder where the .msg files need to be transformed:");
			inFile = new Scanner(System.in).nextLine();
		}
		else
			inFile = args[0];

		// script file
		if (args.length < 2) {
			System.out.println("Please enter the full path of script file (empty for default):");
			scriptFile = new Scanner(System.in).nextLine();
			if (scriptFile.trim().isEmpty()) {
				System.out.println("Transforming using defualt:\n" + transform + "\n");
			}
		}
		else
			scriptFile = args[1];

		File in = new File(inFile);
		if (!in.exists()) {
			System.out.println("File " + inFile + "not found! Please try again.");
			return;
		}
		
		File script = new File(scriptFile);

		File out;

		if (args.length < 3) {
			System.out.println("Please specify output folder (empty for current):");
			outFolder = new Scanner(System.in).nextLine();
			if (outFolder.trim().isEmpty()) {
				out = in.isDirectory() ? in.getCanonicalFile() : in.getParentFile();
				System.out.print("------> Note: Transforming inplace:" +  out.toString() + "\n");
			}
			else
				out = new File(outFolder);
		}
		else
			out =  new File(args[2]);

		String scriptContents = script.exists() ? new String(Files.readAllBytes(script.toPath())) : transform;
		
		File [] fileList = in.isDirectory() ?
				in.listFiles(new FilenameFilter() { public boolean accept(File dir, String name) { return name.endsWith(".msg");} }) :
					new File[]{in};
		
		for (File file : fileList) {
			System.out.println("----> Transforming " + file.getCanonicalPath());
			req.setMessageOriginal(new String(Files.readAllBytes(file.toPath())));
			req.setTransformScript(scriptContents);
			m.transform(req);
			System.out.println("--------->Transformed. Sending output to: " + out.toPath().resolve(file.getName()).toString());
			PrintWriter writer = new PrintWriter(out.toPath().resolve(file.getName()).toString(), "UTF-8");
			writer.print(req.getMessageFinal());
			writer.close();
			
			if (true || (args.length >= 4 && args[3].equals("-interactive"))) {
				System.out.println("********************************\n\n");
				System.out.println(req.getMessageFinal());
				System.out.println("********************************\n\n");
				System.out.println("Please press any key to continue...");
				System.in.read();
			}
		}
		
	}

}
