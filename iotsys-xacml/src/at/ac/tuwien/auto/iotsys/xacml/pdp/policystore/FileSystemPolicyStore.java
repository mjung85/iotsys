package at.ac.tuwien.auto.iotsys.xacml.pdp.policystore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import at.ac.tuwien.auto.iotsys.xacml.pdp.policystore.interfaces.PolicyLoader;

/**
 * 
 * @author Thomas Hofer
 *
 */
public class FileSystemPolicyStore implements PolicyLoader {

	private String dir = "src/main/resources/policies";

	private HashMap<String, File> files = new HashMap<String, File>();
	
	private File policies;

	private final Logger log = Logger.getLogger(this.getClass());

	public FileSystemPolicyStore() {

	}

	/**
	 * 
	 * @param directory
	 */
	public FileSystemPolicyStore(String path) {
		this.dir = path;
		
		policies = new File (path);
		if (policies.isDirectory()) {
			loadDirectory(dir);			
		}
	}
	
	/**
	 * Loads the files in the HashMap files with the filename as key
	 */
	private void loadDirectory(String directory) {
		log.info("Directory: " + directory);
		File f = new File(directory);
		log.info("Reading resource " + f.getAbsolutePath());
		if (f.isDirectory()) {
			File[] list = f.listFiles();
			for (File i : list) {
				files.put(i.getName(), i);
				log.info(i.getName());
			}
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private String loadFile(String fileName) {
		String policy = "";
		BufferedReader br = null;
		try {
			StringBuilder sb = new StringBuilder();
			String line = "";
			
			FileReader fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			policy = sb.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return policy;
	}

	@Override
	public String findPolicy(String resourceId) {
		String policy = "";
		log.info("Following file should exist: " + resourceId);
		if (!policies.isDirectory()) {
			return loadFile(policies.getAbsolutePath());
		}
		
		if (files.containsKey(resourceId)) {
			log.info("Found file, loading ...");
			policy = loadFile(files.get(resourceId).getAbsolutePath());
		} 
		return policy;		
	}
}
