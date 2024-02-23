package au.edu.unimelb.habic.censor_check;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;

/**
 * Entrypoint for comparing text annotations.
 */
public class App 
{
//	private final Config config;
	private final List<Record> records;
	
	public App(Config config) {
//		this.config = config;

        records = new ArrayList<Record>();
        for (FileTuple file : config.listFiles()) {
        	Record record;
			try {
				record = new Record(file.fullTextFile);
			} catch (IOException e) {
				throw new RuntimeException("Could not read full text file " + file.fullTextFile.getAbsolutePath(), e);
			}
        	
        	// Read the true annotations
        	try (BufferedReader reader = new BufferedReader(new FileReader(file.groundTruthFile))) {
        		reader.lines().map(Annotation::new).forEach(a -> record.addTrueMask(a));
			} catch (IOException e) {
				throw new RuntimeException("Could not read ground truth file " + file.groundTruthFile.getAbsolutePath(), e);
			}
        	
        	// Read the test annotation
        	try (BufferedReader reader = new BufferedReader(new FileReader(file.testAnnotationsFile))) {
        		reader.lines().map(Annotation::new).forEach(a -> record.addTestMask(a));
        	} catch (IOException e) {
				throw new RuntimeException("Could not read test annotations file " + file.testAnnotationsFile.getAbsolutePath(), e);
			}
        	
        	records.add(record);
        }
    }
	
	public void report() {
		Map<Category, ResultSet> results = new HashMap<>();
		for (Record rec : records) {
			var stats = rec.computeStats();
			for (var row : stats.entrySet()) {
				if (results.containsKey(row.getKey())) {
					var soFar = results.get(row.getKey());
					results.put(row.getKey(), soFar.add(row.getValue()));
				} else {
					results.put(row.getKey(), row.getValue());
				}
			}
		}
		
		for (var row : results.entrySet()) {
			System.out.printf("%s: %s\n", row.getKey(), row.getValue());
		}
	}
	
    public static void main( String[] args )
    {
    	Config config = new Config();
    	JCommander.newBuilder()
    		.addObject(config)
    		.build()
    		.parse(args);
    	App app = new App(config);
    	app.report();
    }

}
