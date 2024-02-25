package au.edu.unimelb.habic.censor_check.renderers;

import java.util.Map;

import au.edu.unimelb.habic.censor_check.Category;
import au.edu.unimelb.habic.censor_check.Document;
import au.edu.unimelb.habic.censor_check.ResultSet;

public class HtmlRenderer {

	public static String render(Map<Category, ResultSet> results) {
		StringBuilder output = new StringBuilder();
		output.append("<table>");
		
		// Create the header section
		buildHeader(output);
		
		// Create the body of the table
		output.append("<tbody>");
		
		ResultSet all = results.values().stream().reduce(new ResultSet(), (a ,b) -> a.add(b));
		writeRow(output, "ALL", all);
		for(Map.Entry<Category, ResultSet> row : results.entrySet()) {
			if(!row.getKey().equals(Document.NONE)) {
				writeRow(output, row.getKey().toString(), row.getValue());
			}
		}
		
		output.append("</tbody>");
		
		output.append("</table>");
		return output.toString();
	}
	
	public static void writeRow(StringBuilder builder, String category, ResultSet results) {
		builder.append("<tr>");
		
		builder.append("<td>");
		builder.append(category);
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append(String.format("%.4f", (double)results.truePositives() / (double)(results.truePositives() + results.falseNegative())));
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append(String.format("%.4f", (double)results.truePositives() / (double)(results.truePositives() + results.falsePositive())));
		builder.append("</td>");
				
		builder.append("<td>");
		builder.append(results.truePositives());
		builder.append("</td>");
				
		builder.append("<td>");
		builder.append(results.classMiss());
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append(results.falsePositive());
		builder.append("</td>");
		
		
		builder.append("<td>");
		builder.append(results.trueNegative());
		builder.append("</td>");
		
		
		builder.append("<td>");
		builder.append(results.falseNegative());
		builder.append("</td>");
		
		
		builder.append("</tr>");
	}
	
	public static void buildHeader(StringBuilder builder) {
		builder.append("<thead>");
		builder.append("<tr>");
		
		builder.append("<td>");
		builder.append("Category");
		builder.append("</td>");
	
		builder.append("<td>");
		builder.append("Recall");
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append("Precision");
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append("True Positives");
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append("Class Miss");
		builder.append("</td>");
		
		builder.append("<td>");
		builder.append("False Positives");
		builder.append("</td>");
		
		
		builder.append("<td>");
		builder.append("True Negatives");
		builder.append("</td>");
		
		
		builder.append("<td>");
		builder.append("False Negatives");
		builder.append("</td>");
		
		
		builder.append("</tr>");
		builder.append("</thead>");
	}
}
